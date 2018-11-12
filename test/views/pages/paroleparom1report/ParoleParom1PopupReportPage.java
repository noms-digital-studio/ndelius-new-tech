package views.pages.paroleparom1report;

import play.test.TestBrowser;
import views.pages.ReportPage;

import javax.inject.Inject;

public class ParoleParom1PopupReportPage extends ReportPage {

    @Inject
    public ParoleParom1PopupReportPage(TestBrowser control) {
        super(control);
    }

    public void jumpTo(Page page) {
        super.jumpTo(page.getPageNumber());
    }
}
