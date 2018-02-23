package interfaces;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface OffenderApi {
    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<Boolean> isHealthy();

    CompletionStage<Map<String, Object>> searchDb(Map<String, String> queryParams);

    CompletionStage<Map<String, Object>> searchLdap(Map<String, String> queryParams);
}
