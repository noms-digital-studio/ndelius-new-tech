package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportGeneratorWizardController_AutoSave_Test extends WithApplication {

    private Function<String, String> encryptor = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

    @Mock
    private DocumentStore alfrescoDocumentStore;

    @Test
    public void autosaveReportSuccessfullyWhenAlfrescoIsWorking() {
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of()));

        val result = route(app, addCSRFToken(givenAnAutoSaveRequest()));

        assertThat(result.status()).isEqualTo(OK);
        verify(alfrescoDocumentStore).updateExistingPdf(any(), any(), eq("autosave"), any(), any());
    }

    @Test
    public void autosaveReportSuccessfullyWhenAlfrescoIsNotWorking() {
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
            .thenReturn(supplyAsync(() -> { throw new RuntimeException("boom"); }));

        val result = route(app, addCSRFToken(givenAnAutoSaveRequest()));

        assertThat(result.status()).isEqualTo(INTERNAL_SERVER_ERROR);
        verify(alfrescoDocumentStore).updateExistingPdf(any(), any(), eq("autosave"), any(), any());
    }

    private Http.RequestBuilder givenAnAutoSaveRequest() {
        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("autosave"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("pageNumber", "2");
                put("jumpNumber", "3");
            }
        };
        return new Http.RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport/save");
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
