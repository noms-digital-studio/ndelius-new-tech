package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.id;

public class SentencingCourtDetailsPage extends FluentPage {

    public SentencingCourtDetailsPage(FluentControl control) {
        super(control);
    }

    public SentencingCourtDetailsPage navigateHere() {
        goTo("/report/shortFormatPreSentenceReport?onBehalfOfUser=92Q036CvVIRT%2Fi428X3zpg%3D%3D&name=xylkFTVA6GXA1GRZZxZ4MA%3D%3D&localJusticeArea=EH5gq4HPVBLvqnZIG8zkYDK1zJo3nrGPNgKqHKceJUU%3D&dateOfHearing=igY1rhdHh6XNlTto%2BoNRSw%3D%3D&dateOfBirth=twqjuUftRY5xaB556mJb6A%3D%3D&court=eqyTlt9YxlqALprD4wyBD4bUUntAZzSTw4BTi%2BAN1jwwQb7aDEOUHrvD3Nc5CsmQ&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&age=RRioaTyIHLGnja2CBw8hqg%3D%3D&entityId=RRioaTyIHLGnja2CBw8hqg%3D%3D");
        $(id("nextButton")).click();
        $(id("nextButton")).click();
        return this;
    }

    public SentencingCourtDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    public List<String> localJusticeAreas() {
        return $(By.name("localJusticeArea")).
                $(By.tagName("option")).
                stream().
                map(FluentWebElement::value).
                filter(locale -> locale.contains("London")).
                collect(Collectors.toList());

    }
}
