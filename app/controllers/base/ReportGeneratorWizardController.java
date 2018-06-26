package controllers.base;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import controllers.ParamsValidator;
import data.base.ReportGeneratorWizardData;
import helpers.JsonHelper;
import helpers.ThrowableHelper;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.springframework.cglib.beans.BeanMap;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import play.twirl.api.Content;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.FluentHelper.not;
import static helpers.FluentHelper.value;
import static helpers.JsonHelper.*;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;

public abstract class ReportGeneratorWizardController<T extends ReportGeneratorWizardData> extends WizardController<T> {

    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;

    protected ReportGeneratorWizardController(HttpExecutionContext ec,
                                              WebJarsUtil webJarsUtil,
                                              Config configuration,
                                              Environment environment,
                                              AnalyticsStore analyticsStore,
                                              EncryptedFormFactory formFactory,
                                              Class<T> wizardType,
                                              PdfGenerator pdfGenerator,
                                              DocumentStore documentStore,
                                              ParamsValidator paramsValidator) {

        super(ec, webJarsUtil, configuration, environment, analyticsStore, formFactory, wizardType, paramsValidator);

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
    }

    public CompletionStage<Result> reportPost() {
        return wizardForm.bindFromRequest().value().
                filter(not(this::hasSessionMismatch)).
                map(wizardData -> generateAndStoreReport(wizardData).
                    exceptionally(error -> error(wizardData, error)).
                    thenApply(this::toJsonResult)).
                orElse(CompletableFuture.supplyAsync(() -> badRequestJson(ImmutableMap.of("status", "badRequest")), ec.current()));
    }

    public CompletionStage<Result> getPdf(String documentIdEncrypted, String onBehalfOfUserEncrypted) {
        val onBehalfOfUser = decrypter.apply(onBehalfOfUserEncrypted);
        val documentId = decrypter.apply(documentIdEncrypted);

        return documentStore.retrieveDocument(documentId, onBehalfOfUser).
                thenCombine(
                        documentStore.getDocumentName(documentId, onBehalfOfUser),
                        (bytes, filename) -> ok(bytes).
                                                as("application/pdf").
                                                withHeader(CONTENT_DISPOSITION, String.format("attachment;filename=%s;", filename)));
    }



    private Map<String, String> error(T wizardData, Throwable error) {
        Logger.error("Save: Generation or Storage error - " + wizardData.toString(), error);
        return ImmutableMap.of("errorMessage", error.getMessage());
    }

    private Result toJsonResult(Map<String, String> result) {
        return Optional.ofNullable(result.get("errorMessage")).
                map(data -> serverUnavailableJsonResult(result)).
                orElse(okJson(ImmutableMap.of("status", "ok")));

    }

    private Result serverUnavailableJsonResult(Map<String, String> response) {
        Logger.error("Save: Generation or Storage error - " + response);
        return serverUnavailableJson(ImmutableMap.of("status", "error"));
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {
        val queryParams = request().queryString().keySet();
        val continueFromInterstitial = queryParams.contains("continue");
        val stopAtInterstitial = queryParams.contains("documentId") && !continueFromInterstitial;
        val brandNewDocument = !stopAtInterstitial && !continueFromInterstitial;

        return super.initialParams().thenCompose(params -> originalData(params).orElseGet(() -> addPageAndDocumentId(params))).thenApplyAsync(params -> {
            if (stopAtInterstitial) {
                params.put("originalPageNumber", currentPageButNotInterstitialOrCompletion(params.get("pageNumber")));
                params.put("pageNumber", "1");
            }
            if (continueFromInterstitial) {
                params.put("pageNumber", currentPageButNotInterstitialOrCompletion(params.get("pageNumber")));
                params.put("jumpNumber", params.get("pageNumber"));
            }
            if (continueFromInterstitial || brandNewDocument) {
                startNewSession(params);
            }

            return params;
        }, ec.current());
    }

    private String currentPageButNotInterstitialOrCompletion(String pageNumber) {
        // never allow jumping from interstitial  to interstitial, which would happen on
        // saved report that never left the first page or jumping to completion page ("0")
        return String.valueOf(max(parseInt(pageNumber), 2));
    }


    @Override
    protected Integer nextPage(T wizardData) {

        generateAndStoreReport(wizardData).exceptionally(error -> { // Continues in parallel as a non-blocking future result

            Logger.error("Next Page: Generation or Storage error - " + wizardData.toString(), error);
            return ImmutableMap.of();
        });

        return super.nextPage(wizardData);  // Return next page without waiting for draft save to complete
    }

    @Override
    protected final CompletionStage<Result> cancelledWizard(T data) {

        return CompletableFuture.supplyAsync(() -> ok(renderCancelledView()), ec.current());
    }

    @Override
    protected final CompletionStage<Result> completedWizard(T data) {

        final Function<Byte[], CompletionStage<Optional<Byte[]>>> resultIfStored = result ->
                storeReport(data, result).thenApply(stored ->
                        Optional.ofNullable(stored.get("ID")).filter(not(Strings::isNullOrEmpty)).map(value(result)));

        final Function<CompletionStage<Byte[]>, CompletionStage<Optional<Byte[]>>> optionalResult = result ->
            result.thenCompose(resultIfStored);

        return optionalResult.apply(generateReport(data)).
                exceptionally(error -> {

                    Logger.error("Completed Wizard: Generation or Storage error - " + data.toString(), error);
                    return Optional.empty();
                }).
                thenApplyAsync(result -> result.map(bytes -> ok(renderCompletedView(bytes))).orElseGet(() -> {

                    Logger.warn("Report generator wizard failed");
                    return wizardFailed(data);

                }), ec.current()); // Have to provide execution context for HTTP Context to be available when rendering views
    }

    @Override
    protected final String baseViewName() {

        return "views.html." + templateName() + ".page";
    }

    protected abstract String templateName();

    protected abstract Content renderCompletedView(Byte[] bytes);

    protected abstract Content renderCancelledView();

    private CompletionStage<Map<String, String>> addPageAndDocumentId(Map<String, String> params) {

        params.put("pageNumber", "1");
        params.put("startDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        return generateAndStoreReport(wizardForm.bind(params).value().orElseGet(this::newWizardData)).
                exceptionally(error -> {

                    Logger.error("Initial Params: Generation or Storage error - " + params.toString(), error);
                    return ImmutableMap.of("errorMessage", ThrowableHelper.toMessageCauseStack(error));
                }).
                thenApply(stored -> {

                    params.put("documentId", stored.get("ID"));
                    params.put("errorMessage", stored.get("errorMessage"));

                    if (Strings.isNullOrEmpty(params.get("documentId")) && Strings.isNullOrEmpty(params.get("errorMessage"))) {

                        val errorMessage = stored.get("message");

                        params.put("errorMessage", Strings.isNullOrEmpty(errorMessage) ? "No Document ID" : errorMessage);
                    }

                    return params;
                });
    }

    private Optional<CompletionStage<Map<String, String>>> originalData(Map<String, String> params) {

        return Optional.ofNullable(params.get("documentId")).
                map(documentId -> documentStore.retrieveOriginalData(documentId, params.get("onBehalfOfUser"))).
                map(originalData -> originalData.thenApply(data -> {
                    val info = JsonHelper.jsonToMap(Json.parse(data.getUserData()).get("values"));
                    info.put("lastUpdated", data.getLastModifiedDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    return info;
                })).
                map(originalInfo -> originalInfo.thenApply(info -> {

                    info.put("onBehalfOfUser", params.get("onBehalfOfUser"));
                    info.put("documentId", params.get("documentId"));
                    info.put("sessionToken", params.get("sessionToken"));

                    return info;
                }));
    }

    private CompletionStage<Byte[]> generateReport(T data) {

        data.setWatermark(data.getPageNumber() < data.totalPages() ? "DRAFT" : "");

        return pdfGenerator.generate(templateName(), data);
    }

    private CompletionStage<Map<String, String>> storeReport(T data, Byte[] document) {

        val filename = templateName() + ".pdf";
        val metaData = JsonHelper.stringify(ImmutableMap.of(
                "templateName", templateName(),
                "values", convertDataToMap(data)
        ));

        CompletionStage<Map<String, String>> result;

        if (Strings.isNullOrEmpty(data.getDocumentId())) {

            result = documentStore.uploadNewPdf(
                    document,
                    filename,
                    data.getOnBehalfOfUser(),
                    metaData,
                    data.getCrn(),
                    data.getEntityId());
        } else {

            result = documentStore.updateExistingPdf(
                    document,
                    filename,
                    data.getOnBehalfOfUser(),
                    metaData,
                    data.getDocumentId());
        }

        return result.thenApply(stored -> {

            Logger.info("Store result: " + stored);
            return stored;
        });
    }

    private Map<String, Object> convertDataToMap(T data) {
        BeanMap beanMap = BeanMap.create(data);

        val dataValues = new HashMap<String, Object>(beanMap);
        List<String> excludedKeys = Arrays.asList("email", "rating", "feedback", "role", "provider", "region");
        excludedKeys.forEach(dataValues::remove);

        return dataValues;
    }

    private CompletionStage<Map<String, String>> generateAndStoreReport(T data) {

        return generateReport(data).thenCompose(result -> storeReport(data, result));
    }
}
