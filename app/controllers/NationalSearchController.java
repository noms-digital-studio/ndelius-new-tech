package controllers;

import play.Environment;
import play.mvc.Controller;
import play.mvc.Result;
import scala.io.Source;

import javax.inject.Inject;

public class NationalSearchController extends Controller {

    private final views.html.nationalSearch template;
    private final Environment environment;

    @Inject
    public NationalSearchController(views.html.nationalSearch template, Environment environment) {

        this.template = template;
        this.environment = environment;
    }

    public Result index() {

        return ok(template.render());
    }

    public Result searchOffender(String searchTerm) {
        if ("blank".equals(searchTerm.toLowerCase())) {
            return ok("[]").as("application/json");
        }

        String offenderSearchResults =
            Source.fromInputStream(environment.resourceAsStream("offender-search-results.json"), "UTF-8").mkString();
        return ok(offenderSearchResults).as("application/json");
    }

}
