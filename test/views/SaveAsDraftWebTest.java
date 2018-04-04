package views;

import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.CheckYourReportPage;
import views.pages.DraftSavedConfirmationPage;
import views.pages.OffenderAssessmentPage;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;

@RunWith(MockitoJUnitRunner.class)
public class SaveAsDraftWebTest extends WithBrowser {
    @Mock
    private DocumentStore alfrescoDocumentStore;

    private OffenderAssessmentPage offenderAssessmentPage;
    private DraftSavedConfirmationPage draftSavedConfirmationPage;
    private CheckYourReportPage checkYourReportPage;

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        draftSavedConfirmationPage = new DraftSavedConfirmationPage(browser);
        checkYourReportPage = new CheckYourReportPage(browser);
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(alfrescoDocumentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        offenderAssessmentPage.navigateHere();
    }

    @Test
    public void savingDraftWillStoreAllValues() {
        whenSaveAsDraftIsClicked();

        verify(alfrescoDocumentStore, atLeastOnce()).updateExistingPdf(any(), any(), any(), any(), any());
    }


    @Test
    public void savingDraftDisplaysConfirmationPage() {
        whenSaveAsDraftIsClicked();

        draftSavedConfirmationPage.isAt();
    }

    @Test
    public void editReportAfterSavingReportDisplaysCheckYourReportPage() {
        whenSaveAsDraftIsClicked();

        draftSavedConfirmationPage.updateReport();

        checkYourReportPage.isAt();
    }


    private void whenSaveAsDraftIsClicked() {
        offenderAssessmentPage.saveAsDraft();
    }


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(alfrescoDocumentStore),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock())
            )
            .build();
    }

}
