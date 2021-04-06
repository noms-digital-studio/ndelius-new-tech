package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.CurrentRiskAssessmentScoresPage;

import javax.inject.Inject;

public class CurrentRiskAssessmentScoresSteps {
    @Inject
    private CurrentRiskAssessmentScoresPage page;

    @Given("^Delius user is on the \"Current risk assessment scores\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable { page.navigateHere(); }

    @Given("^Delius User completes the \"Current risk assessment scores\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillInputWithId("riskAssessmentRSRScore", "2.54");
        page.fillInputWithId("riskAssessmentOGRS3ReoffendingProbability", "23");
        page.fillInputWithId("riskAssessmentOGPReoffendingProbability", "12");
        page.fillInputWithId("riskAssessmentOVPReoffendingProbability", "31");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has a OASys Sexual re-offending Predictor (Contact) (OSP /C) assessment been completed?");
        page.clickRadioButtonWithLabelWithinLegend("Low", "OASys Sexual re-offending Predictor (Contact) (OSP /C)");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has a OASys Sexual re-offending Predictor (Indecent Images) (OSP/I) assessment been completed?");
        page.clickRadioButtonWithLabelWithinLegend("Medium", "OASys Sexual re-offending Predictor (Indecent Images) (OSP/I)");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has a Spousal Assault Risk Assessment (SARA) been completed?");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Spousal Assault Risk Assessment (SARA)");
        page.clickButton("Continue");
    }
}
