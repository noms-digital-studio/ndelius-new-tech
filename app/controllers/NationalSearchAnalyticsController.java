package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NationalSearchAnalyticsController extends Controller {

    private final AnalyticsStore analyticsStore;
    private final views.html.nationalSearchAnalytics template;

    @Inject
    public NationalSearchAnalyticsController(AnalyticsStore analyticsStore, views.html.nationalSearchAnalytics template) {

        this.analyticsStore = analyticsStore;
        this.template = template;
    }

    public Result index() {

        return ok(template.render());
    }

    public CompletionStage<Result> visitCounts(String duration) {
        return CompletableFuture.completedFuture(ImmutableMap.of("uniqueUserVisits", 28, "allVisits", 33)).thenApply(JsonHelper::okJson);
    }
}
