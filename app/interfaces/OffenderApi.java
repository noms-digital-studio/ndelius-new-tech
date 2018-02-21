package interfaces;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface OffenderApi {
    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<Boolean> isHealthy();

    CompletionStage<Object> searchDb(Map<String, String> params);
}
