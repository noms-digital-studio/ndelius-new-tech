package controllers;

import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchAnalyticsControllerTest extends WithApplication {
    @Mock
    private AnalyticsStore analyticsStore;


    @Test
    public void returnsOkResponseWithCountsAsJson() {
        when(analyticsStore.pageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(100l));
        when(analyticsStore.uniquePageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(10l));
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"uniqueUserVisits\":10,\"allVisits\":100}", contentAsString(result));
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(AnalyticsStore.class).toInstance(analyticsStore)
                )
                .build();
    }

}