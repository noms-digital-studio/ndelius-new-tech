package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class CheckYourReportPage extends FluentPage {
    private final SentencingCourtDetailsPage sentencingCourtDetailsPage;

    public CheckYourReportPage(FluentControl control) {
        super(control);
        sentencingCourtDetailsPage = new SentencingCourtDetailsPage(control);
    }

    public CheckYourReportPage navigateHere() {
        // goto sources of information
        sentencingCourtDetailsPage.navigateHere().gotoNext();

        // TODO move these to appropriate page object
        // goto offence details
        $(id("nextButton")).click();
        $(id("offenceSummary")).fill().with("Offence summary");
        $(id("mainOffence")).fill().with("Main offence");

        // goto offence analysis
        $(id("nextButton")).click();
        $(id("offenceAnalysis")).fill().with("Offence analysis");

        // goto offender assessment
        $(id("nextButton")).click();
        $(id("offenderAssessment")).fill().with("Offender assessment");

        // goto risk assessment
        $(id("nextButton")).click();
        $(id("likelihoodOfReOffending")).fill().with("Likelihood Of ReOffending");
        $(id("riskOfSeriousHarm")).fill().with("Risk of Serious Harm");
        $(id("previousSupervisionResponse_Good")).click();
        $(id("additionalPreviousSupervision")).fill().with("Additional previous supervision");

        // goto Conclusion
        $(id("nextButton")).click();
        $(id("proposal")).fill().with("Proposal");

        // goto Summary
        $(id("nextButton")).click();
        return this;
    }

    public CheckYourReportPage gotoNext() {
        // goto sign and date report
        $(id("nextButton")).click();
        return this;
    }
}
