package services;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import interfaces.OffenderApi;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Environment;
import play.Mode;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static scala.io.Source.fromInputStream;


@RunWith(MockitoJUnitRunner.class)
public class DeliusOffenderApi_probationAreaDescriptions_Test {

    private OffenderApi offenderApi;

    @Mock
    private WSClient wsClient;

    @Mock
    private WSRequest wsRequest;

    @Mock
    private WSResponse wsResponse;

    @Before
    public void setup() {
        val config = ConfigFactory.load().withValue("offender.api.url", ConfigValueFactory.fromAnyRef("http://delius-api/api/"));
        offenderApi = new DeliusOffenderApi(config, wsClient);
        when(wsClient.url(any())).thenReturn(wsRequest);
        when(wsRequest.addHeader(any(), any())).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
        when(wsResponse.getStatus()).thenReturn(200);
        when(wsResponse.getBody()).thenReturn(fromInputStream(new Environment(Mode.TEST).resourceAsStream("/deliusoffender/probationAreaFiltered.json"), "UTF-8").mkString());
    }

    @Test
    public void setsSingleProbationAreaCode() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01")).toCompletableFuture().join();

        verify(wsClient).url(eq("http://delius-api/api/probationAreas?codes=N01"));
    }

    @Test
    public void setsMultipleAreaProbationAreaCodes() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01", "N02")).toCompletableFuture().join();

        verify(wsClient).url(eq("http://delius-api/api/probationAreas?codes=N01,N02"));
    }

    @Test
    public void doesNotBotherCallingAPIWhenEmptyListSupplied() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of()).toCompletableFuture().join();

        verify(wsClient, never()).url(any());
    }

    @Test
    public void setsBearerTokenInHeader() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01")).toCompletableFuture().join();

        verify(wsRequest).addHeader("Authorization", "Bearer ABC");
    }

    @Test
    public void returnsMapOfCodeDescriptions() {
        val probationAreaCodeToDescriptionMap = offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N02", "N03")).toCompletableFuture().join();

        assertThat(probationAreaCodeToDescriptionMap)
                .contains(entry("N02", "NPS North East"))
                .contains(entry("N03", "NPS Wales"));
    }

    private static Map.Entry<String, String> entry(String code, String description) {
        return new AbstractMap.SimpleEntry(code, description);
    }

}