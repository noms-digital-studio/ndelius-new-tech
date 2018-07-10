package bdd;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.TestBrowser;
import views.WithChromeBrowser;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static play.libs.Json.toJson;

public class GlobalHooks extends WithChromeBrowser {
    @Inject
    private TestBrowser theTestBrowser;

    @Inject
    @Named("pdfWireMock")
    private WireMockServer pdfWireMock;
    @Inject
    @Named("alfrescofWireMock")
    private WireMockServer alfrescofWireMock;
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    @Before
    public void before() {
        startServer();
        pdfWireMock.start();
        alfrescofWireMock.start();
        offenderApiWireMock.start();
        pdfWireMock.stubFor(
                post(urlEqualTo("/generate"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withBody(JsonHelper.stringify(new Byte[]{}))));

        alfrescofWireMock.stubFor(
                post(urlEqualTo("/noms-spg/uploadnew"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));

        alfrescofWireMock.stubFor(
                post(urlMatching("/noms-spg/uploadandrelease/.*"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));

        alfrescofWireMock.stubFor(
                post(urlMatching("/noms-spg/updatemetadata/.*"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));


        offenderApiWireMock.stubFor(
                post(urlEqualTo("/documentLink")).willReturn(created()));
        createBrowser();
    }

    @After
    public void after() {
        pdfWireMock.stop();
        alfrescofWireMock.stop();
        offenderApiWireMock.stop();
        stopServer();
        quitBrowser();
    }

    @Override
    protected TestBrowser provideBrowser(int port) {
        return theTestBrowser;
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)))
                .configure("params.user.token.valid.duration", "100000d")
                .configure("pdf.generator.url", String.format("http://localhost:%d/", Ports.PDF.getPort()))
                .configure("store.alfresco.url", String.format("http://localhost:%d/", Ports.ALFRESCO.getPort()))
                .configure("offender.api.url", String.format("http://localhost:%d/", Ports.OFFENDER_API.getPort()))
                .build();
    }

}
