package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshCommunityPage;

import javax.inject.Inject;

public class RoshCustodySteps {
    @Inject
    private RoshCommunityPage page;

    @Given("^Delius user is on the \"Current RoSH custody\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
