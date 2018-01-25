package services;

import interfaces.OffenderApiLogon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DeliusOffenderApi implements OffenderApiLogon {

    @Override
    public CompletionStage<String> logon(String username) {
        return CompletableFuture.completedFuture("charlie");
    }
}
