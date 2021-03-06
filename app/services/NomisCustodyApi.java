package services;

import akka.util.ByteString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.typesafe.config.Config;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
import interfaces.PrisonerApiToken;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.JsonHelper.readValue;
import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.*;
import static services.NomisReponseHelper.checkForValidResponse;

public class NomisCustodyApi  implements PrisonerApi {
    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    interface HasName {
        String getFirstName();
        String getSurname();
    }
    @Value
    static private class NomisImageMetaData {
        private String offenderImageId;
        private String orientationType;
        private String imageObjectType;
        private String imageViewType;
        private boolean activeFlag;
    }

    @Value
    @Builder(toBuilder = true)
    static class AgencyLocation {
        private String description;
    }
    @Value
    @Builder(toBuilder = true)
    static class Booking {
        private AgencyLocation agencyLocation;
        private long bookingSequence;
        private long bookingId;
        private long offenderId;
        private long rootOffenderId;
        private String bookingNo;
        private boolean activeFlag;
    }
    @Value
    @Builder(toBuilder = true)
    static class OffenderEntity implements HasName {
        private String firstName;
        private String surname;
        private List<Alias> aliases;
        private List<Booking> bookings;
    }

    @Value
    @Builder(toBuilder = true)
    static class Alias implements HasName {
        private String firstName;
        private String surname;
        private long offenderId;
        HasName asName() {
            return this; // avoid cast in Stream operation
        }
    }



    @Inject
    public NomisCustodyApi(Config configuration, WSClient wsClient, PrisonerApiToken apiToken) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;

        Logger.info("Running with NomisCustodyApi");

    }

    @Override
    public CompletionStage<byte[]> getImage(String nomsNumber) {
        Function<WSResponse, WSResponse> checkForValidOffenceResponse = (wsResponse) -> checkForValidResponse(wsResponse, apiToken, () -> String.format("No offender found in NOMIS - check the noms number %s is correct", nomsNumber));
        Function<WSResponse, WSResponse> checkForValidImageResponse = (wsResponse) -> checkForValidResponse(wsResponse, apiToken, () -> String.format("No images found for offender %s", nomsNumber));
        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%scustodyapi/api/offenders/nomsId/%s/images", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(checkForValidOffenceResponse)
                        .thenApply(WSResponse::getBody)
                        .thenApply(this::toImageMetaDataList)
                        .thenApply(this::findLatestFaceThumbnail)
                        .thenCompose( imageMetaData -> wsClient
                                .url(String.format("%scustodyapi/api/offenders/nomsId/%s/images/%s/thumbnail", apiBaseUrl, nomsNumber, imageMetaData.getOffenderImageId()))
                                .addHeader(AUTHORIZATION, "Bearer " + token)
                                .get()
                                .thenApply(checkForValidImageResponse)
                                .thenApply(WSResponse::getBodyAsBytes)
                                .thenApply(ByteString::toArray)));

    }

    @Override
    public CompletionStage<Optional<Offender>> getOffenderByNomsNumber(String nomsNumber) {

        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%scustodyapi/api/offenders/nomsId/%s", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(this::checkForMaybeResponse)
                        .thenApply(maybeResponse ->
                                maybeResponse.map(this::transformOffenderResponse)));
    }

    private Offender transformOffenderResponse(WSResponse response) {
        return OffenderTransformer.offenderOf(readValue(response.getBody(), OffenderEntity.class));
    }


    private Optional<WSResponse> checkForMaybeResponse(WSResponse wsResponse) {
        return NomisReponseHelper.checkForMaybeResponse(wsResponse, apiToken);
    }

    private NomisImageMetaData findLatestFaceThumbnail(List<NomisImageMetaData> images) {
        return images
                .stream()
                .filter(NomisImageMetaData::isActiveFlag)
                .filter(image -> "FRONT".equals(image.getOrientationType()))
                .filter(image -> "BOOKING".equals(image.getImageObjectType()))
                .filter(image -> "FACE".equals(image.getImageViewType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No current image found for offender"));
    }

    private List<NomisImageMetaData> toImageMetaDataList(String body) {
        return readValue(body, new TypeReference<List<NomisImageMetaData>>() {});
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {

        return wsClient.url(apiBaseUrl + "custodyapi/health").
                get().
                thenApply(wsResponse -> {

                    val healthy = wsResponse.getStatus() == OK;

                    if (!healthy) {
                        Logger.warn("Custody API Response Status: " + wsResponse.getStatus());
                        return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                    }

                    return healthy(wsResponse.asJson());
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking Custody API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }

    static class OffenderTransformer {
        static Offender offenderOf(OffenderEntity offenderEntity) {
            val mostRecentBooking = offenderEntity.getBookings()
                    .stream()
                    .filter(booking -> booking.getBookingSequence() == 1)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No current booking for offender found"));
            return Offender
                    .builder()
                    .firstName(nameForActiveBooking(offenderEntity).getFirstName())
                    .surname(nameForActiveBooking(offenderEntity).getSurname())
                    .mostRecentPrisonerNumber(String.valueOf(mostRecentBooking.getBookingNo()))
                    .institution(
                            Institution
                                    .builder()
                                    .description(mostRecentBooking.getAgencyLocation().getDescription())
                                    .build())
                    .build();

        }
    }

    private static HasName nameForActiveBooking(OffenderEntity offenderEntity) {
        val activeBooking = offenderEntity.getBookings().stream().filter(Booking::isActiveFlag).findFirst();

        return activeBooking.map(booking -> {
            if (booking.getOffenderId() != booking.getRootOffenderId()) {
                val maybeAlias = offenderEntity.getAliases().stream().filter(alias -> alias.getOffenderId() == booking.getOffenderId()).findFirst();

                return maybeAlias.map(Alias::asName).orElse(offenderEntity);
            }
            return offenderEntity;

        }).orElse(offenderEntity);
    }
}
