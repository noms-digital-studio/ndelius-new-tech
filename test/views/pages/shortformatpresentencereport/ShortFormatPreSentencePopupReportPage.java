package views.pages.shortformatpresentencereport;

import lombok.val;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.*;

public class ShortFormatPreSentencePopupReportPage extends FluentPage {

    @Inject
    public ShortFormatPreSentencePopupReportPage(TestBrowser control) {
        super(control);
    }

    @Override
    public void isAt(Object... parameters) {
        control.await().until(driver -> driver.find(By.tagName("h1")).first().text().equals(parameters[0]));
    }

    public void jumpTo(Page page) {
        String linkSelector = String.format("//a[@data-target='%s']", page.getPageNumber());
        control.await().until(driver -> driver.find(By.xpath(linkSelector)).size() == 1);
        $(By.xpath(linkSelector)).click();
    }

    public void clickButton(String button) {
        $(By.xpath(String.format("//button[contains(text(),'%s')]", button))).click();
    }

    public void fillTextArea(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(cssSelector(String.format("#%s .ql-editor", fieldId))).fill().with(text);
    }

}
