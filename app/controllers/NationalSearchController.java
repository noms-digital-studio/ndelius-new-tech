package controllers;

import helpers.JsonHelper;
import interfaces.Search;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NationalSearchController extends Controller {

    private final views.html.nationalSearch template;
    private final Search elasticSearch;

    @Inject
    public NationalSearchController(views.html.nationalSearch template, Search elasticSearch) {
        this.template = template;
        this.elasticSearch = elasticSearch;
    }

    public Result index() {
        return ok(template.render());
    }

    public CompletionStage<Result> searchOffender(String searchTerm) {
        if ("blank".equals(searchTerm.toLowerCase())) {
            return CompletableFuture.supplyAsync(() -> ok("[]").as("application/json"));
        }

        return elasticSearch.search(searchTerm).thenApply(JsonHelper::okJson);
    }

}
