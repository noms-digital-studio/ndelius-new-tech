package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.OffenderAssessmentPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.OFFENDER_ASSESSMENT;

public class OffenderAssessmentSteps {
    @Inject
    private OffenderAssessmentPage page;

    @Given("^that the Delius user is on the \"Offender assessment\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(OFFENDER_ASSESSMENT.getPageHeader());
    }
}
