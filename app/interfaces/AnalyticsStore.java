package interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStore {

    void recordEvent(Map<String, Object> data);

    CompletableFuture<List<Map<String, Object>>> recentEvents(int limit);

    CompletableFuture<Map<Integer, Long>> pageVisits();

    CompletableFuture<Long> pageVisits(String eventType);

    CompletableFuture<Long> uniquePageVisits(String eventType);

    CompletableFuture<Boolean> isUp();
}
