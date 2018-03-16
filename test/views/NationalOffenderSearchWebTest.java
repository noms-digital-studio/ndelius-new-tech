package views;

import com.google.common.collect.ImmutableMap;
import helpers.FutureListener;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import play.Application;
import play.Environment;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;
import services.DeliusOffenderApi;
import views.pages.NationalSearchPage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.test.Helpers.FIREFOX;
import static play.test.Helpers.HTMLUNIT;
import static scala.io.Source.fromInputStream;

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

    @BeforeClass
    public static void beforeAll() throws IOException {
    }


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
    public void searchResultsDisplayed() {
        nationalSearchPage.submitSearch("John Smith");
        browser.fluentWait().withTimeout(1, TimeUnit.SECONDS).until((driver) -> nationalSearchPage.hasOffenderResults());
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

    private SearchHit[] getSearchHitArray() {
        return getSearchHitArray(ImmutableMap.of("offenderId", 123, "crn", "X1224", "currentRestriction", false, "currentExclusion", false));
    }
    @SafeVarargs
    private final SearchHit[] getSearchHitArray(Map<String, Object>... replacements) {
        return stream(replacements).map((replacement) -> toSearchHit(ImmutableMap.of(), replacement))
                .collect(toList()).toArray(new SearchHit[replacements.length]);
    }

    private SearchHit toSearchHit(Map<String, HighlightField> highlightFields, Map<String, Object> replacementMap) {
        val searchHitMap = new HashMap<String, Object>();
        val environment = new Environment(null, this.getClass().getClassLoader(), Mode.TEST);

        val offenderSearchResultsTemplate =
                fromInputStream(environment.resourceAsStream("offender-search-result.json.template"), "UTF-8").mkString();

        val offenderSearchResults =
                withDefaults(replacementMap).
                        keySet().
                        stream().
                        reduce(offenderSearchResultsTemplate,
                                (template, key) -> template.replace(format("${%s}", key), withDefaults(replacementMap).get(key).toString()));


        val bytesReference = new BytesArray(offenderSearchResults);
        searchHitMap.put("_source", bytesReference);
        searchHitMap.put("highlight", highlightFields);
        return SearchHit.createFromMap(searchHitMap);
    }

    private Map<String, Object> withDefaults(Map<String, Object> replacementMap) {
        if (!replacementMap.containsKey("dateOfBirth")) {
            return ImmutableMap.<String, Object>builder().putAll(replacementMap)
                    .put("dateOfBirth", "1978-01-16")
                    .build();
        }

        if (!replacementMap.containsKey("firstName")) {
            return ImmutableMap.<String, Object>builder().putAll(replacementMap)
                    .put("firstName", "firstName")
                    .build();
        }

        if (!replacementMap.containsKey("surname")) {
            return ImmutableMap.<String, Object>builder().putAll(replacementMap)
                    .put("surname", "surname")
                    .build();
        }

        if (!replacementMap.containsKey("currentDisposal")) {
            return ImmutableMap.<String, Object>builder().putAll(replacementMap)
                    .put("currentDisposal", "0")
                    .build();
        }

        return replacementMap;
    }

}
