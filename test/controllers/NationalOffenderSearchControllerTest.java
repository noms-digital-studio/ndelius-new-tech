package controllers;

import data.offendersearch.OffenderSearchResult;
import interfaces.OffenderSearch;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class NationalOffenderSearchControllerTest extends WithApplication {

    @Mock
    private OffenderSearch elasticOffenderSearch;

    @Test
    public void searchTermReturnsResults() {
        when(elasticOffenderSearch.search(any(), anyInt(), anyInt())).thenReturn(completedFuture(OffenderSearchResult.builder().build()));
        val request = new Http.RequestBuilder().method(GET).uri("/searchOffender/smith");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"offenders\":null,\"suggestions\":null,\"total\":0}", contentAsString(result));
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(OffenderSearch.class).toInstance(elasticOffenderSearch)
            )
            .build();
    }

}