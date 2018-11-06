package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import views.pages.shortformatpresentencereport.LandingPage;

import javax.inject.Inject;

public class LandingPageSteps {
    @Inject
    private LandingPage landingPage;

    @Given("^that the user is on the SFR landing page$")
    public void thatTheUserIsOnTheLandingPageOfTheReport() {
        landingPage.navigateHere();
    }

    @When("^they select the \"([^\"]*)\" button on the SFR landing page$")
    public void theySelectTheButton(String button) {
        landingPage.clickButton(button);
    }

}