package services;

import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.OffenderApi;
import lombok.val;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static java.lang.String.format;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;

public class DeliusOffenderApi implements OffenderApi {

    private final WSClient wsClient;
    private final String offenderApiBaseUrl;

    @Inject
    public DeliusOffenderApi(Config configuration, WSClient wsClient) {
        this.wsClient = wsClient;
        offenderApiBaseUrl = configuration.getString("offender.api.url");
    }

    @Override
    public CompletionStage<String> logon(String username) {
        return wsClient.url(offenderApiBaseUrl + "logon")
            .post(format("cn=%s,cn=Users,dc=moj,dc=com", username))
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
                if (wsResponse.getStatus() != 200) {
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
    public CompletionStage<Object> searchDb(Map<String, String> params) {
        return wsClient.url(offenderApiBaseUrl + "logon")
            .post(format("NationalUser"))
            .thenApply(this::assertOkResponse)
            .thenApply(WSResponse::getBody)
            .thenCompose(bearerToken -> getUser(params, bearerToken));
    }

    private CompletionStage<Object> getUser(Map<String, String> params, String bearerToken) {
        String url = offenderApiBaseUrl + "users" + queryParamsFrom(params);
        return wsClient.url(url)
            .addHeader(AUTHORIZATION, String.format("Bearer %s", bearerToken))
            .get()
            .thenApply(wsResponse -> {
                if (wsResponse.getStatus() != 200) {
                    Logger.warn("Bad response calling Delius Offender API {}. Status {}", url, wsResponse.getStatus());
                    return JsonHelper.jsonToMap("{\"error\": \"" + wsResponse.getStatus() + "\"}");
                }
                return wsResponse.asJson();
            })
            .exceptionally(throwable -> {
                Logger.error("Got an error calling Delius Offender API health endpoint", throwable);
                return JsonHelper.jsonToMap("{\"error\": \"" + throwable.getMessage() + "\"}");
            });
    }

    String queryParamsFrom(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder().append("?");
        params.forEach((key, value) -> stringBuilder.append(String.format("%s=%s&", key, params.get(key))));
        String paramString = stringBuilder.substring(0, stringBuilder.length() - 1).toString();
        return paramString;
    }

    private WSResponse assertOkResponse(WSResponse response) {
        if (response.getStatus() != OK) {
            Logger.error("Logon API bad response {}", response.getStatus());
            throw new RuntimeException("Unable to call logon. Status = " + response.getStatus());
        }
        return response;
    }
}
