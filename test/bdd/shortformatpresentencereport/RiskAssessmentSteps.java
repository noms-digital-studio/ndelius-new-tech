package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.RiskAssessmentPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.RISK_ASSESSMENT;

public class RiskAssessmentSteps {
    @Inject
    private RiskAssessmentPage page;

    @Given("^that the Delius user is on the \"Risk assessment\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(RISK_ASSESSMENT.getPageHeader());
    }
}
