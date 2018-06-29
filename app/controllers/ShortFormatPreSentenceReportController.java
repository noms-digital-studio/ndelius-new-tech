package controllers;

import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.ReportGeneratorWizardController;
import data.ShortFormatPreSentenceReportData;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.data.Form;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static helpers.DateTimeHelper.calculateAge;
import static helpers.DateTimeHelper.format;
import static helpers.JwtHelper.principal;
import static java.time.Clock.systemUTC;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ShortFormatPreSentenceReportController extends ReportGeneratorWizardController<ShortFormatPreSentenceReportData> {

    private final views.html.shortFormatPreSentenceReport.cancelled cancelledTemplate;
    private final views.html.shortFormatPreSentenceReport.completed completedTemplate;

    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  WebJarsUtil webJarsUtil,
                                                  Config configuration,
                                                  Environment environment,
                                                  AnalyticsStore analyticsStore,
                                                  EncryptedFormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore,
                                                  views.html.shortFormatPreSentenceReport.cancelled cancelledTemplate,
                                                  views.html.shortFormatPreSentenceReport.completed completedTemplate,
                                                  ParamsValidator paramsValidator,
                                                  OffenderApi offenderApi) {

        super(ec, webJarsUtil, configuration, environment, analyticsStore, formFactory, ShortFormatPreSentenceReportData.class, pdfGenerator, documentStore, paramsValidator, offenderApi);
        this.cancelledTemplate = cancelledTemplate;
        this.completedTemplate = completedTemplate;
    }

    @Override
    protected String templateName() {

        return "shortFormatPreSentenceReport";
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {

        val username = decryptQueryParam("user");
        val epochRequestTimeMills = decryptQueryParam("t");
        val crn = decryptQueryParam("crn");
        val documentId = decryptQueryParam("documentId");

        Logger.info("queryString: " + request().queryString());
        Logger.info(String.format("PARAMS: user:%s t:%s crn:%s", username, epochRequestTimeMills, crn));

        // If documentId present this is an `update` rather than a `new`
        if (isNotBlank(documentId)) {
            return super.initialParams().thenApply(params -> {

                params.putIfAbsent("pncSupplied", Boolean.valueOf(!isNullOrEmpty(params.get("pnc"))).toString());
                params.putIfAbsent("addressSupplied", Boolean.valueOf(!isNullOrEmpty(params.get("address"))).toString());
                return migrateLegacyReport(params);
            });
        } else {

            return offenderApi.logon(username)
                .thenApplyAsync(bearerToken -> {
                    Logger.info("AUDIT:{}: WizardController: Successful logon for user {}", principal(bearerToken), username);
                    return bearerToken;

                }, ec.current())
                .thenCompose(bearerToken -> offenderApi.getOffenderByCrn(bearerToken, crn))
                .thenCombineAsync(super.initialParams(), (offenderDetails, params) -> {

                    params = migrateLegacyReport(params);

                    if (offenderDetails.get("firstName") != null) {
                        params.put("name", String.format("%s %s", offenderDetails.get("firstName"), offenderDetails.get("surname")));
                    }

                    if (offenderDetails.get("dateOfBirth") != null) {
                        params.put("dateOfBirth", format((String) offenderDetails.get("dateOfBirth")));
                    }

                    if (offenderDetails.get("dateOfBirth") != null) {
                        params.put("age", String.format("%d", calculateAge((String) offenderDetails.get("dateOfBirth"), systemUTC())));
                    }

                    if (offenderDetails.get("otherIds") != null && isNotBlank(((Map<String, String>) offenderDetails.get("otherIds")).get("pncNumber"))) {
                        params.put("pnc", ((Map<String, String>) offenderDetails.get("otherIds")).get("pncNumber"));
                        params.put("pncSupplied", "true");
                    } else {
                        params.put("pncSupplied", "false");
                    }

                    if (offenderDetails.get("contactDetails") != null &&
                        ((List<Object>) ((Map<String, Object>) offenderDetails.get("contactDetails")).get("addresses")) != null &&
                        !((List<Object>) ((Map<String, Object>) offenderDetails.get("contactDetails")).get("addresses")).isEmpty()) {
                        params.put("address", singleLineAddress(offenderDetails));
                        params.put("addressSupplied", "true");
                    } else {
                        params.put("addressSupplied", "false");
                    }

                    return params;
                }, ec.current());

            //        final Runnable errorReporter = () -> Logger.error(String.format("Short format report search request did not receive a valid user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
//        return paramsValidator.invalidCredentials(username, epochRequestTimeMills, errorReporter).
//            map(result -> (CompletionStage<Result>) CompletableFuture.completedFuture(result)).
//            orElseGet(renderedPage).
//            exceptionally(throwable -> {
//
//                Logger.info("AUDIT:{}: Unable to login {}", "anonymous", username);
//                Logger.error("Unable to logon to offender API", throwable);
//
//                return internalServerError();
//            });
        }
    }

    private String singleLineAddress(Map<String, Object> offenderDetails) {
        val currentAddress = currentAddress(offenderDetails);
        return ((currentAddress.get("buildingName") == null) ? "" : currentAddress.get("buildingName") + "\n") +
            ((currentAddress.get("addressNumber") == null) ? "" : currentAddress.get("addressNumber") + " ") +
            ((currentAddress.get("streetName") == null) ? "" : currentAddress.get("streetName") + "\n") +
            ((currentAddress.get("district") == null) ? "" : currentAddress.get("district") + "\n") +
            ((currentAddress.get("townCity") == null) ? "" : currentAddress.get("townCity") + "\n") +
            ((currentAddress.get("county") == null) ? "" : currentAddress.get("county") + "\n") +
            ((currentAddress.get("postcode") == null) ? "" : currentAddress.get("postcode") + "\n");
    }

    private Map<String, Object> currentAddress(Map<String, Object> offenderDetails) {
        return ((List<Map<String, Object>>) ((Map<String, Object>) offenderDetails.get("contactDetails")).get("addresses")).stream()
            .sorted(comparing((Map<String, Object> address) -> (String) address.get("from")).reversed())
            .collect(Collectors.toList()).get(0);
    }

    private String decryptQueryParam(String param) {
        return decrypter.apply(request().queryString() != null && request().queryString().get(param) != null ? request().queryString().get(param)[0] : "");
    }

    private Map<String, String> migrateLegacyReport(Map<String, String> params) {
        return migrateLegacyOffenderAssessmentIssues(params);
    }

    private Map<String, String> migrateLegacyOffenderAssessmentIssues(Map<String, String> params) {
        if(Boolean.parseBoolean(params.get("issueDrugs"))) {
            params.putIfAbsent("issueSubstanceMisuse", "true");
        }
        if(Boolean.parseBoolean(params.get("issueAlcohol"))) {
            params.putIfAbsent("issueSubstanceMisuse", "true");
        }
        if(StringUtils.isNotBlank(params.get("offenderAssessment"))) {
            params.putIfAbsent("issueOther", "true");
            params.putIfAbsent("issueOtherDetails", params.get("offenderAssessment"));
        }
        return params;
    }

    @Override
    protected Map<String, String> modifyParams(Map<String, String> params, Consumer<String> paramEncrypter) {

        if ("2".equals(params.get("pageNumber"))) {

            if ("false".equals(params.get("pncSupplied"))) {

                paramEncrypter.accept("pnc");
            }

            if ("false".equals(params.get("addressSupplied"))) {

                paramEncrypter.accept("address");
            }
        }

        if ("3".equals(params.get("pageNumber"))) {

            paramEncrypter.accept("court");
            paramEncrypter.accept("dateOfHearing");
            paramEncrypter.accept("localJusticeArea");
        }

        return params;
    }

    @Override
    protected Content renderCancelledView() {

        val boundForm = wizardForm.bindFromRequest();

        return cancelledTemplate.render(boundForm, viewEncrypter, reviewPageNumberFor(boundForm));
    }

    @Override
    protected Content renderCompletedView(Byte[] bytes) {

        val boundForm = wizardForm.bindFromRequest();

        return completedTemplate.render(boundForm, viewEncrypter, reviewPageNumberFor(boundForm));
    }

    private Integer reviewPageNumberFor(Form<ShortFormatPreSentenceReportData> boundForm) {
        return boundForm.value().map(form -> form.totalPages() - 1).orElse(1);
    }

    @Override
    protected Content renderErrorMessage(String errorMessage) {

        return views.html.shortFormatPreSentenceReport.error.render("Report error", errorMessage, webJarsUtil);
    }
}
