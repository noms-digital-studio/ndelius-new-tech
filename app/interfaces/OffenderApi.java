package interfaces;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface OffenderApi {

    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<JsonNode> searchDb(Map<String, String> queryParams);

    CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams);

    CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes);

}
