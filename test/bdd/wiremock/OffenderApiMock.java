package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static utils.InstitutionalReportHelpers.anInstitutionalReport;
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
                get(urlEqualTo("/offenders/crn/X12345/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X54321/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(aFemaleOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/institutionalReports/12345"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anInstitutionalReport()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X54321/institutionalReports/54332"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anInstitutionalReport()))));

        return this;
    }

    public OffenderApiMock stubOffenderWithName(String fullName) {
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(
                                anOffenderWithNoContactDetails()
                                        .toBuilder()
                                        .firstName(firstName)
                                        .surname(surname)
                                        .middleNames(ImmutableList.of())
                                        .build()
                        ))));

        return this;
    }

    public OffenderApiMock stubOffenderWithNameAndNoNomsNumber(String fullName) {
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(
                                anOffenderWithNoContactDetails()
                                        .toBuilder()
                                        .firstName(firstName)
                                        .surname(surname)
                                        .middleNames(ImmutableList.of())
                                        .otherIds(ImmutableMap.of())
                                        .build()
                        ))));

        return this;
    }
}
