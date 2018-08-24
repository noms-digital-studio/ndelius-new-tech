package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class BehaviourInPrisonPage extends ParoleParom1PopupReportPage {
    private final OPDPathwayPage opdPathwayPage;
    @Inject
    public BehaviourInPrisonPage(OPDPathwayPage opdPathwayPage, TestBrowser control) {
        super(control);
        this.opdPathwayPage = opdPathwayPage;
    }

    public BehaviourInPrisonPage navigateHere() {
        opdPathwayPage.navigateHere();
        jumpTo(Page.BEHAVIOUR_IN_PRISON);
        return this;
    }
}
