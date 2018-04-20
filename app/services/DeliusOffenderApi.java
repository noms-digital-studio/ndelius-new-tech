package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import interfaces.OffenderApi;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static helpers.JsonHelper.readValue;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;

public class DeliusOffenderApi implements OffenderApi {

    private final WSClient wsClient;
    private final String ldapStringFormat;
    private final String offenderApiBaseUrl;

    @Data
    @NoArgsConstructor
    private static class ProbationArea {
        private String code;
        private String description;
    }

    private static TypeReference probationAreaListRef = new TypeReference<List<ProbationArea>>(){};

    @Inject
    public DeliusOffenderApi(Config configuration, WSClient wsClient) {
        this.wsClient = wsClient;

        ldapStringFormat = configuration.getString("ldap.string.format");
        offenderApiBaseUrl = configuration.getString("offender.api.url");
    }

    @Override
    public CompletionStage<String> logon(String username) {
        return wsClient.url(offenderApiBaseUrl + "logon")
            .post(format(ldapStringFormat, username))
            .thenApply(this::assertOkResponse)
            .thenApply(WSResponse::getBody);
    }

    @Override
    public CompletionStage<Boolean> canAccess(String bearerToken, long offenderId) {
        val url = String.format(offenderApiBaseUrl + "offenders/offenderId/%d/userAccess", offenderId);
        return wsClient.url(url)
                .addHeader(AUTHORIZATION, String.format("Bearer %s", bearerToken))
                .get()
                .thenApply(WSResponse::getStatus)
                .thenApply(status -> {
                    switch(status) {
                        case OK: return true;
                        case FORBIDDEN: return false;
                        default:
                            Logger.error("Got a bad response from {} status {}", url, status);
                            return false;
                    }
                })
                .exceptionally(e -> {
                    Logger.error("Got an error from {}", url, e);
                    return false;
                });

    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        String url = offenderApiBaseUrl + "health";
        return wsClient.url(url)
            .get()
            .thenApply(wsResponse -> {
                if (wsResponse.getStatus() != OK) {
                    Logger.warn("Bad response calling Delius Offender API {}. Status {}", url, wsResponse.getStatus());
                    return false;
                }
                return true;
            })
            .exceptionally(throwable -> {
                Logger.error("Got an error calling Delius Offender API health endpoint", throwable);
                return false;
            });
    }

    @Override
    public CompletionStage<JsonNode> searchDb(Map<String, String> queryParams) {

        return logonAndCallOffenderApi("users", queryParams);
    }

    @Override
    public CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams) {

        return logonAndCallOffenderApi("ldap", queryParams);
    }

    @Override
    public CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes) {
        if (probationAreaCodes.isEmpty()) {
            return CompletableFuture.completedFuture(ImmutableMap.of());
        }
        val url = String.format(offenderApiBaseUrl + "probationAreas?codes=%s", probationAreaCodes.stream().collect(Collectors.joining( "," )));
        return wsClient.url(url)
                .addHeader(AUTHORIZATION, String.format("Bearer %s", bearerToken))
                .get()
                .thenApply(this::assertOkResponse)
                .thenApply(WSResponse::getBody)
                .thenApply(body -> {
                    List<ProbationArea> areas = readValue(body, probationAreaListRef);
                    return areas.stream().collect(toMap(ProbationArea::getCode, ProbationArea::getDescription));
                });
    }

    private CompletionStage<JsonNode> logonAndCallOffenderApi(String action, Map<String, String> params) {

        val url = offenderApiBaseUrl + action + queryParamsFrom(params);
        return wsClient.url(offenderApiBaseUrl + "logon")
                .post("NationalUser")
                .thenApply(this::assertOkResponse)
                .thenApply(WSResponse::getBody)
                .thenCompose(bearerToken -> callOffenderApi(bearerToken, url));
    }

    private CompletionStage<JsonNode> callOffenderApi(String bearerToken, String url) {

        return wsClient.url(url)
            .addHeader(AUTHORIZATION, String.format("Bearer %s", bearerToken))
            .get()
            .thenApply(wsResponse -> {
                if (wsResponse.getStatus() != OK) {
                    throw new RuntimeException(String.format("Bad response calling Delius Offender API %s. Status %d", url, wsResponse.getStatus()));
                }
                return wsResponse.asJson();
            })
            .exceptionally(throwable -> {
                Logger.error("Got an error calling Delius Offender API", throwable);
                return Json.toJson(ImmutableMap.of("error", throwable.getMessage()));
            });
    }

    String queryParamsFrom(Map<String, String> params) {

        return "?" + String.join("&", params.entrySet().stream().
                map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(toList()));
    }

    private WSResponse assertOkResponse(WSResponse response) {
        if (response.getStatus() != OK) {
            Logger.error("Logon API bad response {}", response.getStatus());
            throw new RuntimeException("Unable to call logon. Status = " + response.getStatus());
        }
        return response;
    }
}
