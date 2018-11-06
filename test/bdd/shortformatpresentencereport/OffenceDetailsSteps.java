package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.OffenceDetailsPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.OFFENCE_DETAILS;

public class OffenceDetailsSteps {
    @Inject
    private OffenceDetailsPage page;

    @Given("^that the Delius user is on the \"Offence details\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(OFFENCE_DETAILS.getPageHeader());
    }
}
