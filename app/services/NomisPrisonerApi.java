package services;

import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.PrisonerApi;
import interfaces.PrisonerApiToken;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.Base64;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.OK;

public class NomisPrisonerApi implements PrisonerApi {

    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    private final Base64.Decoder base64Decoder = Base64.getDecoder();

    @Inject
    public NomisPrisonerApi(Config configuration, WSClient wsClient, PrisonerApiToken apiToken) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;
    }

    @Override
    public CompletionStage<byte[]> getImage(String nomisId) {

        final Function<WSResponse, WSResponse> reportNonOKResponse = wsResponse -> {

            if (wsResponse.getStatus() != OK) {
                Logger.warn("Nomis Image API Id: {} bad response Status: {}", nomisId, wsResponse.getStatus());
            }
            return wsResponse;
        };

        Logger.info("Nomis Image API request for Id: {}", nomisId);

        return wsClient.url(apiBaseUrl + "offenders/" + nomisId + "/image").
                addHeader(AUTHORIZATION, "Bearer " + apiToken.get()).
                get().
                thenApply(reportNonOKResponse).
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap).
                thenApply(result -> result.get("image")).
                thenApply(base64Decoder::decode);
    }
}
