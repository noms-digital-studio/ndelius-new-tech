package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.shortformatpresentencereport.ShortFormatPreSentencePopupReportPage;

import javax.inject.Inject;

public class GlobalSteps {

    @Inject
    private ShortFormatPreSentencePopupReportPage page;

    @When("^they select the \"([^\"]*)\" button within the SFR")
    public void theySelectTheButton(String button) {
        page.clickButton(button);
    }

    @Then("^the user should be directed to the \"([^\"]*)\" page within the SFR$")
    public void theUserShouldBeDirectedTo(String header) {
        page.isAt(header);
    }
}
