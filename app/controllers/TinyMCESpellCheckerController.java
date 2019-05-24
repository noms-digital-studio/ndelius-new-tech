package controllers;

import data.Params;
import data.WordsRequested;
import lombok.val;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.SpellcheckService;

import javax.inject.Inject;
import java.util.Optional;

public class TinyMCESpellCheckerController extends Controller {

    private final FormFactory formFactory;
    private final Form<WordsRequested> wordsRequestedForm;
    private final SpellcheckService spellcheckService;

    @Inject
    public TinyMCESpellCheckerController(FormFactory formFactory, SpellcheckService spellcheckService) {
        this.formFactory = formFactory;
        this.wordsRequestedForm = formFactory.form(WordsRequested.class);
        this.spellcheckService = spellcheckService;
    }

    public Result findSpellings() {
        val wordsRequested = wordsRequestedForm.bindFromRequest().get();
        Optional<Params> params = Optional.ofNullable(wordsRequested.getParams());
        if(params.isPresent()) {
            String[] words = params.get().getWords().toArray(new String[0]);
            return play.mvc.Results.ok(spellcheckService.getSpellcheckSuggestionsString(words));
        } else {
            return play.mvc.Results.ok("{ }");
        }
    }
}
