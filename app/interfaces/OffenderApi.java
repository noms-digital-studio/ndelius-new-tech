package interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;


public interface OffenderApi {

    @Value
    class Offender {
        private String firstName;
        private String surname;
        private List<String> middleNames;
        private String dateOfBirth;
        private Map<String, String> otherIds;
        private ContactDetails contactDetails;

        public String displayName() {
            List<String> names = ImmutableList.<String>builder()
                .add(ofNullable(firstName).orElse(""))
                .addAll(ofNullable(middleNames)
                    .map(middleNames -> middleNames.stream().findFirst().map(ImmutableList::of)
                    .orElse(ImmutableList.of())).orElse(ImmutableList.of()))
                .add(ofNullable(surname).orElse("")).build();

            return String.join(" ", names).trim();
        }
    }

    @Value
    class ContactDetails {
        private List<OffenderAddress> addresses;

        public Optional<OffenderAddress> currentAddress() {
            return Optional.ofNullable(addresses)
                .flatMap(offenderAddresses -> offenderAddresses.stream()
                    .filter(address -> address.getFrom() != null)
                    .max(comparing(OffenderAddress::getFrom)));
        }

    }

    @Value
    class OffenderAddress {
        private String buildingName;
        private String addressNumber;
        private String streetName;
        private String district;
        private String town;
        private String county;
        private String postcode;
        private String from;
        private String to;

        public String render() {
            return Optional.ofNullable(this.getBuildingName()).orElse("")  + "\n" +
                   Optional.ofNullable(this.getAddressNumber()).orElse("")  + " " +
                   Optional.ofNullable(this.getStreetName()).orElse("")  + "\n" +
                   Optional.ofNullable(this.getDistrict()).orElse("")  + "\n" +
                   Optional.ofNullable(this.getTown()).orElse("")  + "\n" +
                   Optional.ofNullable(this.getCounty()).orElse("")  + "\n" +
                   Optional.ofNullable(this.getPostcode()).orElse("")  + "\n";
        }

    }

    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<JsonNode> searchDb(Map<String, String> queryParams);

    CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams);

    CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes);

    CompletionStage<Offender> getOffenderByCrn(String bearerToken, String crn);
}
