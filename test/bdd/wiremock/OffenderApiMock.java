package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import helpers.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static utils.CourtAppearanceHelpers.aCourtReport;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.aFemaleOffenderWithNoContactDetails;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

public class OffenderApiMock {
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    public OffenderApiMock start() {
            offenderApiWireMock.start();
        return this;
    }

    public OffenderApiMock stop() {
        offenderApiWireMock.stop();
        return this;
    }

    public OffenderApiMock stubDefaults() {
        offenderApiWireMock.stubFor(
                post(urlEqualTo("/documentLink")).willReturn(created()));

        offenderApiWireMock.stubFor(
                post(urlEqualTo("/logon")).willReturn(ok().withBody("aBearerToken")));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/courtReports/.*"))
                        .willReturn(ok().withBody(JsonHelper.stringify(aCourtReport()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/offences"))
                        .willReturn(ok().withBody(JsonHelper.stringify(someOffences().getItems()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/courtAppearances"))
                        .willReturn(ok().withBody(JsonHelper.stringify(someCourtAppearances().getItems()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X54321/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(aFemaleOffenderWithNoContactDetails()))));

        return this;
    }

}
