package services;

import akka.stream.impl.io.ByteStringParser;
import com.google.common.base.Strings;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import helpers.StaticImage;
import interfaces.PrisonerApi;
import interfaces.PrisonerApiToken;
import org.apache.commons.compress.utils.IOUtils;
import play.Environment;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.StaticImage.noPhotoImage;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.OK;

public class NomisPrisonerApi implements PrisonerApi {

    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;
    private final Environment environment;

    @Inject
    public NomisPrisonerApi(Config configuration, WSClient wsClient, PrisonerApiToken apiToken, Environment environment) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;
        this.environment = environment;
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
                thenApply(result -> result != null ? result.get("image") : null).
                thenApply(base64 -> {

                    if (Strings.isNullOrEmpty(base64)) {

                        Logger.warn("Empty Image Base64 text for Nomis Id: {}", nomisId);
                        return noPhotoImage(environment);
                    }
                    else {
                        return  Base64.getDecoder().decode(base64);
                    }
                });
    }
}
