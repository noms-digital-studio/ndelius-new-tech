package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.SentencingCourtDetailsPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.SENTENCING_COURT_DETAILS;

public class SentencingCourtDetailsSteps {
    @Inject
    private SentencingCourtDetailsPage page;

    @Given("^that the Delius user is on the \"Sentencing court details\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(SENTENCING_COURT_DETAILS.getPageHeader());
    }
}
