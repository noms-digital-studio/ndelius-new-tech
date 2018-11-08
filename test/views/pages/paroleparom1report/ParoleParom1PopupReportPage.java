package views.pages.paroleparom1report;

import org.openqa.selenium.By;
import play.test.TestBrowser;
import views.pages.ReportPage;

import javax.inject.Inject;

public class ParoleParom1PopupReportPage extends ReportPage {
    @Inject
    public ParoleParom1PopupReportPage(TestBrowser control) {
        super(control);
    }

    @Override
    public void isAt(Object... parameters) {
        control.await().until(driver -> driver.find(By.tagName("h1")).first().text().equals(parameters[0]));
    }

    public void jumpTo(Page page) {
        super.jumpTo(page.getPageNumber());
    }
}
