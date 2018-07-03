package interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;


public interface OffenderApi {

    @Data
    @NoArgsConstructor
    public static class Offender {
        private String firstName;
        private String surname;
        private String dateOfBirth;
        private Map<String, String> otherIds;
        private ContactDetails contactDetails;
    }

    @Data
    @NoArgsConstructor
    public static class ContactDetails {
        private List<OffenderAddress> addresses;
    }

    @Data
    @NoArgsConstructor
    public static class OffenderAddress {
        private String buildingName;
        private String addressNumber;
        private String streetName;
        private String district;
        private String townCity;
        private String county;
        private String postcode;
        private String from;
        private String to;
    }

    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<JsonNode> searchDb(Map<String, String> queryParams);

    CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams);

    CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes);

    CompletionStage<Offender> getOffenderByCrn(String bearerToken, String crn);
}
