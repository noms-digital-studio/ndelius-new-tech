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
import views.pages.*;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static views.helpers.AlfrescoDataHelper.legacyReportWith;

@RunWith(MockitoJUnitRunner.class)
public class InterstialWebTest extends WithBrowser {
    @Mock
    private DocumentStore alfrescoDocumentStore;

    private OffenderAssessmentPage offenderAssessmentPage;
    private OffenderDetailsPage offenderDetailsPage;
    private StartPage startPage;

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        offenderDetailsPage = new OffenderDetailsPage(browser);
        startPage = new StartPage(browser);
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(alfrescoDocumentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
    }

    @Test
    public void reportSaveOnPage1WillTakeYouToPage2WhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "1")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasInvalidZeroPageNumberWillTakeYouToPage2WhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "0")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderAssessmentPage.isAt();

    }

    @Test
    public void continuingAReportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();
        offenderAssessmentPage.isAt();

        startPage.switchToWindow().gotoNext();
        offenderAssessmentPage.isAt();

    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(alfrescoDocumentStore),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class))
            ).build();
    }

}
