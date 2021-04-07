package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RecommendationPage;
import views.pages.paroleparom1report.SourcesPage;

import javax.inject.Inject;

public class RecommendationSteps {
    @Inject
    private RecommendationPage page;

    @Given("^Delius User is on the \"Recommendation\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Recommendation\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickCheckboxWithLabel("I confirm that I have had sight of the On/post Tariff Parole Custody Report and liaised with the Prison Offender Manager and their views around the assessment and recommendation have been taken into account in this report.");
        page.fillTextArea("What is your recommendation?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
