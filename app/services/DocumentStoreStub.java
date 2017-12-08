package services;

import com.google.common.collect.ImmutableMap;
import interfaces.DocumentStore;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class DocumentStoreStub implements DocumentStore {
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123"));
    }

    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        // Non-legacy data (i.e. it has a startDate)
//        return CompletableFuture.supplyAsync(() -> "{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"" + onBehalfOfUser + "\", \"address\": \"" + documentId + "\", \"pnc\": \"Retrieved From Store\", \"startDate\": \"12/12/2017\", \"crn\": \"1234\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\" } }");

        // Legacy data with no start date
        return CompletableFuture.supplyAsync(() -> "{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"" + onBehalfOfUser + "\", \"address\": \"" + documentId + "\", \"pnc\": \"Retrieved From Store\", \"crn\": \"1234\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\" } }");
    }

    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {

        return CompletableFuture.supplyAsync(() -> 200);
    }

    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456"));
    }

}
