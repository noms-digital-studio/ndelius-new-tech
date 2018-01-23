package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import interfaces.Search;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import play.test.WithApplication;
import utils.AnalyticsStoreMock;
import utils.DocumentStoreMock;
import utils.PdfGeneratorMock;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static org.junit.Assert.*;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortFormatPreSentenceReportControllerTest extends WithApplication implements PdfGeneratorMock, DocumentStoreMock, AnalyticsStoreMock {

    @Mock
    private Search search;

    @Test
    public void getSampleReportOK() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void getSampleReportConsumesDtoQueryStrings() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?name=i8%2Fp1Ti7JMS%2FjO%2BPOhHtGA%3D%3D&foobar=xyz987");
        val result = route(app, request);

        val content = Helpers.contentAsString(result);

        assertEquals(OK, result.status());
        assertTrue(content.contains("i8/p1Ti7JMS/jO+POhHtGA=="));
        assertFalse(content.contains("xyz987"));
    }

    @Test
    public void getSampleReportWithFailedAlfrescoSave() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?name=i8%2Fp1Ti7JMS%2FjO%2BPOhHtGA%3D%3D&onBehalfOfUser=i8%2Fp1Ti7JMS%2FjO%2BPOhHtGA%3D%3D");
        val result = route(app, request);

        val content = Helpers.contentAsString(result);

        assertEquals(BAD_REQUEST, result.status());
        assertTrue(content.contains("Upload blows up for this user"));
    }

    @Test
    public void getSampleReportWithDocumentIdDecryptsAndRetrievesFromStore() {

        try {

            val secretKey = "ThisIsASecretKey";
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(Encryption.encrypt(clearDocumentId, secretKey), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(Encryption.encrypt(clearUserName, secretKey), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId + "&onBehalfOfUser=" + onBehalfOfUser);

            val content = Helpers.contentAsString(route(app, request));

            assertTrue(content.contains(Encryption.encrypt("Retrieved From Store", secretKey)));   // Returned from Mock retrieveOriginalData

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
    }

    @Test
    public void postSampleReportPage2TitleOnlyReturnsBadRequest() {

        val formData = ImmutableMap.of(
                "name", "",
                "pageNumber", "2"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage2AllFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));

                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));

                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));

                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));

                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));

                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage4SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));

                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("otherInformationSource", "true");

                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage4AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage5SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");

                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage5AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage6SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("patternOfOffending", "Some pattern of offending");

                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage6AllFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage7SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("issueAccommodation", "false");
                put("issueEmployment", "false");
                put("issueFinance", "false");
                put("issueRelationships", "false");
                put("issueSubstanceAbuse", "false");
                put("issueHealth", "false");
                put("issueBehaviour", "false");
                put("issueOther", "false");

                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage7AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("issueAccommodation", "true");
                put("issueEmployment", "false");
                put("issueFinance", "false");
                put("issueRelationships", "false");
                put("issueSubstanceMisuse", "false");
                put("issueHealth", "false");
                put("issueBehaviour", "false");
                put("issueOther", "false");

                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage8SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");


                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage8AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");


                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage9SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage9AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("proposal", "Some proposal");

                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    // PAGE 10

    @Test
    public void postSampleReportPage11SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");
                put("proposal", "Some proposal");

                put("office", "Sheffield probation office");
                put("officePhone", "0114 114 114");
                put("counterSignature", "Some other person");

                put("pageNumber", "11");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage11AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("67890"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");
                put("proposal", "Some proposal");

                put("reportAuthor", "Arthur Author");
                put("office", "Sheffield probation office");
                put("officePhone", "0114 114 114");
                put("counterSignature", "Some other person");
                put("reportDate", "21/06/2017");

                put("pageNumber", "11");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    private boolean pdfGenerated;

    @Override
    public void setPdfGenerated(boolean flag) {
        pdfGenerated = flag;
    }

    private boolean pdfUploaded;

    @Override
    public void setPdfUploaded(boolean flag) {
        pdfUploaded = flag;
    }

    private boolean pdfUpdated;

    @Override
    public void setPdfUpdated(boolean flag) {
        pdfUploaded = flag;
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(this),
                        bind(DocumentStore.class).toInstance(this),
                        bind(AnalyticsStore.class).toInstance(this),
                        bind(Search.class).toInstance(search)
                )
                .build();
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public CompletableFuture<Boolean> isUp() {
        throw new RuntimeException("Not yet implemented");
    }
}
