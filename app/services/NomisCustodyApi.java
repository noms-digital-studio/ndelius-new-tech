package services;

import akka.util.ByteString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.typesafe.config.Config;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
import interfaces.PrisonerApiToken;
import lombok.Value;
import lombok.val;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.JsonHelper.readValue;
import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.*;

public class NomisCustodyApi  implements PrisonerApi {
    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    @Value
    static private class NomisImageMetaData {
        private String offenderImageId;
        private String orientationType;
        private String imageObjectType;
        private String imageViewType;
        private boolean activeFlag;
    }



    @Inject
    public NomisCustodyApi(Config configuration, WSClient wsClient, PrisonerApiToken apiToken) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;

        Logger.info("Running with NomisCustodyApi");

    }

    @Override
    public CompletionStage<byte[]> getImage(String nomsNumber) {
        Function<WSResponse, WSResponse> checkForValidResponse = (wsResponse) -> checkForValidResponse(nomsNumber, wsResponse);
        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%scustodyapi/api/offenders/nomsId/%s/images", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(checkForValidResponse)
                        .thenApply(WSResponse::getBody)
                        .thenApply(this::toImageMetaDataList)
                        .thenApply(this::findLatestFaceThumbnail)
                        .thenCompose( image -> wsClient
                                .url(String.format("%scustodyapi/api/offenders/nomsId/%s/images/%s/thumbnail", apiBaseUrl, nomsNumber, image.getOffenderImageId()))
                                .addHeader(AUTHORIZATION, "Bearer " + token)
                                .get()
                                .thenApply(checkForValidResponse)
                                .thenApply(WSResponse::getBodyAsBytes)
                                .thenApply(ByteString::toArray)));

    }

    private WSResponse checkForValidResponse(String nomsNumber, WSResponse wsResponse) {
        switch (wsResponse.getStatus()) {
            case OK:
                return wsResponse;
            case NOT_FOUND:
                throw new RuntimeException(String.format("No offender found in NOMIS - check the noms number %s is correct", nomsNumber));
            case UNAUTHORIZED:
            case FORBIDDEN:
                apiToken.clearToken();
                Logger.error("NOMIS authentication token has expired or is invalid");
                throw new RuntimeException(String.format("NOMIS authentication token has expired or is invalid"));
            default:
                Logger.error("Failed to retrieve offender record from NOMIS. Status code {}", wsResponse.getStatus());
                throw new RuntimeException(String.format("Failed to retrieve offender record from NOMIS. Status code %d", wsResponse.getStatus()));
        }
    }

    private NomisImageMetaData findLatestFaceThumbnail(List<NomisImageMetaData> images) {
        return images
                .stream()
                .filter(NomisImageMetaData::isActiveFlag)
                .filter(image -> "FRONT".equals(image.getOrientationType()))
                .filter(image -> "BOOKING".equals(image.getImageObjectType()))
                .filter(image -> "FACE".equals(image.getImageViewType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No current image found for offender"));
    }

    private List<NomisImageMetaData> toImageMetaDataList(String body) {
        return readValue(body, new TypeReference<List<NomisImageMetaData>>() {});
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {

        return wsClient.url(apiBaseUrl + "custodyapi/health").
                get().
                thenApply(wsResponse -> {

                    val healthy = wsResponse.getStatus() == OK;

                    if (!healthy) {
                        Logger.warn("Custody API Response Status: " + wsResponse.getStatus());
                        return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                    }

                    return healthy(wsResponse.asJson());
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking Custody API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }
}
