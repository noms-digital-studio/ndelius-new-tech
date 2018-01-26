package services;

import interfaces.OffenderApiLogon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FakeOffenderApi implements OffenderApiLogon {
    @Override
    public CompletionStage<String> logon(String username) {
        return CompletableFuture.completedFuture("fake-bearer");
    }
}
