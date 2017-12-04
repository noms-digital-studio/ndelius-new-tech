package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.tagName;


public class SignAndDateReportPage extends FluentPage {
    private final CheckYourReportPage checkYourReportPage;

    public SignAndDateReportPage(FluentControl control) {
        super(control);
        checkYourReportPage = new CheckYourReportPage(control);
    }

    public SignAndDateReportPage navigateHere() {
        checkYourReportPage.navigateHere().gotoNext();
        return this;
    }

    public String getMainHeading() {
        return find(tagName("h1")).text();
    }
}
