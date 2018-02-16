package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import lombok.val;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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

    public CompletionStage<Result> visitCounts(String from) {
        val pageVisitsStage = analyticsStore.pageVisits("search-index", fromDateTime(from));
        val uniquePageVisitsStage = analyticsStore.uniquePageVisits("search-index", fromDateTime(from));
        return pageVisitsStage.thenCombine(
                uniquePageVisitsStage,
                (allVisits, uniqueUserVisits) -> ImmutableMap.of("uniqueUserVisits", uniqueUserVisits, "allVisits", allVisits))
                .thenApply(JsonHelper::okJson);
    }

    private LocalDateTime fromDateTime(String from) {
        return Optional.ofNullable(from).
                map(text -> LocalDateTime.parse(text, ISO_OFFSET_DATE_TIME)).
                orElse(LocalDateTime.of(2017, 1, 1, 0, 0));
    }
}
