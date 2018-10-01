package views.pages;

import helpers.Encryption;
import lombok.val;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.openqa.selenium.By.id;

public class StartPage extends FluentPage {
    public StartPage(FluentControl control) {
        super(control);
    }

    public StartPage navigateHere() {
        goTo("/report/shortFormatPreSentenceReport?onBehalfOfUser=92Q036CvVIRT%2Fi428X3zpg%3D%3D&name=xylkFTVA6GXA1GRZZxZ4MA%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&entityId=RRioaTyIHLGnja2CBw8hqg%3D%3D&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
        return this;
    }

    public StartPage navigateWithExistingReport() {
        val secretKey = "ThisIsASecretKey";
        val clearDocumentId = "12345";
        val clearUserName = "Smith,John";

        try {
            val documentId = URLEncoder.encode(Encryption.encrypt(clearDocumentId, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(Encryption.encrypt(clearUserName, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

            goTo("/report/shortFormatPreSentenceReport?documentId=" + documentId +
                "&onBehalfOfUser=" + onBehalfOfUser +
                "&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
            return this;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public StartPage gotoNext() {
        $(id("nextButton")).click();
        window().switchTo("reportpopup");
        return this;
    }

    public StartPage switchToWindow() {
        window().switchToLast("reportpopup");
        return this;
    }

    public String lastUpdatedText() {
        return $(id("lastUpdated")).text();
    }
}
