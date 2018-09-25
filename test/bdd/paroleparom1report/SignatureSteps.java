package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SignaturePage;

import javax.inject.Inject;

public class SignatureSteps {
    @Inject
    private SignaturePage page;

    @Given("^Delius User is on the \"Signature\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() {
        page.navigateHere();
    }
}
