package services;

import com.typesafe.config.Config;
import interfaces.OffenderApiLogon;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.lang.String.format;

public class DeliusOffenderApi implements OffenderApiLogon {

    private final WSClient wsClient;
    private final String offenderApiBaseUrl;

    @Inject
    public DeliusOffenderApi(Config configuration, WSClient wsClient) {
        this.wsClient = wsClient;
        offenderApiBaseUrl = configuration.getString("offender.api.url");
    }

    @Override
    public CompletionStage<String> logon(String username) {
        return wsClient.url(offenderApiBaseUrl + "/logon")
            .post(format("cn=%s,cn=Users,dc=moj,dc=com", username))
            .thenApply(WSResponse::getBody);
    }
}
