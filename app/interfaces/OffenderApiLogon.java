package interfaces;

import java.util.concurrent.CompletionStage;

public interface OffenderApiLogon {
    CompletionStage<String> logon(String username);
}
