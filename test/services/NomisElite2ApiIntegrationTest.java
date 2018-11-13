package services;

import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import helpers.JsonHelper;
import interfaces.PrisonerCategoryApi;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.Application;
import play.Environment;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scala.io.Source.fromInputStream;

public class NomisElite2ApiIntegrationTest extends WithApplication {

    private PrisonerCategoryApi prisonerCategoryApi;
    private static final int PORT = 18080;

    @Rule
    public WireMockRule wireMock = new WireMockRule(wireMockConfig().port(PORT).jettyStopTimeout(10000L));

    @Before
    public void beforeEach() {

        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG.json"))));

        wireMock.stubFor(
                post(urlEqualTo("/auth/oauth/token"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/token.json"))));



        prisonerCategoryApi = instanceOf(PrisonerCategoryApi.class);
    }

    @Test
    public void credentialsSuppliedToRetrieveToken() {
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                1,
                postRequestedFor(urlEqualTo("/auth/oauth/token"))
                        .withRequestBody(containing("grant_type=client_credentials"))
                        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                        .withBasicAuth(new BasicCredentials("my_username", "my_password")));
    }

    @Test
    public void retrievesNewTokenPriorToRetrievingImageWhichIsThenCached() {
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();


        wireMock.verify(
                1,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));

        wireMock.verify(
                3,
                getRequestedFor(urlMatching("/elite2api/api/bookings/offenderNo/G8020GG\\?fullInfo=true")));

    }

    @Test
    public void retrievesNewTokenAfterCachedExpires() throws InterruptedException {
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();
        Thread.sleep(1000);
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                2,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));


    }


    @Test
    public void completesExceptionallyIfFailedToRetrieveToken() {
        wireMock.stubFor(
                post(urlEqualTo("/auth/oauth/token"))
                        .willReturn(
                                aResponse().withStatus(401)));

        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("Unable to call http://localhost:18080/auth/oauth/token Status = 401");

    }

    @Test
    public void unauthorisedResponseCompletesExceptionally() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                aResponse().withStatus(401)));


        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("NOMIS authentication token has expired or is invalid");
    }

    @Test
    public void forbiddenResponseCompletesExceptionally() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                aResponse().withStatus(403)));


        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("NOMIS authentication token has expired or is invalid");
    }

    @Test
    public void unauthorisedResponseClearsCachedToken() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                aResponse().withStatus(401)));


        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);

        wireMock.verify(
                3,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));
    }

    @Test
    public void completesExceptionallyIfOffenderServiceErrors() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                aResponse().withStatus(500)));

        assertThatThrownBy(() -> prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("Failed to retrieve offender record from NOMIS. Status code 500");

    }


    @Test
    public void tokenFromAuthCallUsedInImageCalls() {
        val tokenFromFakeAuthResponse = JsonHelper.jsonToMap(loadResource("/nomsoffender/token.json")).get("access_token");

        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify( getRequestedFor(urlMatching("/elite2api/api/bookings/offenderNo/.*")).withHeader("Authorization", equalTo(String.format("Bearer %s", tokenFromFakeAuthResponse))));

    }

    @Test
    public void tokenIsRetrievePriorToRetrievingAnOffender() {
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                postRequestedFor(urlEqualTo("/auth/oauth/token")));

    }

    @Test
    public void nomsNumberIsUsedWhenRetrievingAnOffender() {
        prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                getRequestedFor(urlEqualTo("/elite2api/api/bookings/offenderNo/G8020GG?fullInfo=true")));

    }

    @Test
    public void categoryIsReturnedWhenOffenderFound() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/G8020GG\\?fullInfo=true"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG.json"))));



        val maybeCategory = prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        assertThat(maybeCategory.isPresent()).isTrue();
    }

    @Test
    public void categoryIsNotReturnedWhenOffenderNotFound() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/G8020GG\\?fullInfo=true"))
                        .willReturn(
                                notFound()));



        val maybeCategory = prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        assertThat(maybeCategory.isPresent()).isFalse();
    }

    @Test
    public void categoryIsNotReturnedWhenOffenderHasNoCategory() {
        wireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/G8020GG\\?fullInfo=true"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG_no_category.json"))));



        val maybeCategory = prisonerCategoryApi.getOffenderCategoryByNomsNumber("G8020GG").toCompletableFuture().join();

        assertThat(maybeCategory.isPresent()).isFalse();
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("nomis.api.url", String.format("http://localhost:%d/", PORT))
                .configure("prisoner.api.provider", "custody")
                .configure("custody.api.auth.username", "my_username")
                .configure("custody.api.auth.password", "my_password")
                .configure("custody.api.token.cache.time.seconds", "1")
                .build();
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

}