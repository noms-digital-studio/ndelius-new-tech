package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.xpath;

public class SearchFeedbackPage extends FluentPage {

    public SearchFeedbackPage(FluentControl control) {
        super(control);
    }

    @Override
    public void isAt() {
        assertThat(window().title()).contains("HMPPS - National Search Feedback");
    }

    public SearchFeedbackPage navigateHere() {
        goTo("http://feedback.user:changeit@localhost:19001/feedback");
        return this;
    }

    public String getSubmittedDate() {
        return $(xpath("//tbody//tr//td[1]")).text();
    }

    public String getUsername() {
        return $(xpath("//tbody//tr//td[2]")).text();
    }

    public String getRole() {
        return $(xpath("//tbody//tr//td[3]")).text();
    }

    public String getProvider() {
        return $(xpath("//tbody//tr//td[4]")).text();
    }

    public String getRegion() {
        return $(xpath("//tbody//tr//td[5]")).text();
    }

    public String getRating() {
        return $(xpath("//tbody//tr//td[6]")).text();

    }

    public String getAdditionalComments() {
        return $(xpath("//tbody//tr//td[7]")).text();
    }

    public String getEmailAddress() {
        return $(xpath("//tbody//tr//td[2]//a")).attribute("href");
    }

}
