package views;

import helpers.FutureListener;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import services.DeliusOffenderApi;
import views.pages.NationalSearchPage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static utils.data.OffenderESDataFactory.getSearchHitArray;

@RunWith(MockitoJUnitRunner.class)
public class NationalOffenderSearchWebTest extends WithChromeBrowser {
    private NationalSearchPage nationalSearchPage;
    @Mock
    private RestHighLevelClient restHighLevelClient;
    @Mock
    private DeliusOffenderApi deliusOffenderApi;
    @Mock
    private SearchResponse searchResponse;

    private String BEARER = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbj1mYWtlLnVzZXIsY249VXNlcnMsZGM9bW9qLGRjPWNvbSIsInVpZCI6ImZha2UudXNlciIsImV4cCI6MTUxNzYzMTkzOX0=.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ";

    @Before
    public void before() {
        when(deliusOffenderApi.logon(anyString())).thenReturn(CompletableFuture.completedFuture(BEARER));
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());

        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), 1, 42));
        nationalSearchPage = new NationalSearchPage(browser);
        nationalSearchPage.navigateHere();
    }

    @Test
    public void searchBoxRendered() {
       assertThat(nationalSearchPage.hasSearchBox()).isTrue();
    }

    @Test
    public void searchResultsAreDisplayedAfterEnteringSearchTerm() {
        nationalSearchPage.fillSearchTerm("John Smith");
        browser
                .fluentWait()
                .withTimeout(1, TimeUnit.SECONDS)
                .until(
                        (driver) -> nationalSearchPage.hasOffenderResults());
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(RestHighLevelClient.class).toInstance(restHighLevelClient),
                bind(DeliusOffenderApi.class).toInstance(deliusOffenderApi)
            ).configure("params.user.token.valid.duration", "100000d")
            .build();
    }

}
