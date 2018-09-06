package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RiskManagementPlanPage;

import javax.inject.Inject;

public class RiskManagementPlanSteps {
    @Inject
    private RiskManagementPlanPage page;

    @Given("^that the Delius user is on the \"Risk Managemant Plan (RMP)\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }
}
