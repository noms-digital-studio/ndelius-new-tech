package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.OffenderApi.CourtReport;
import interfaces.OffenderApi.Offence;
import interfaces.OffenderApi.OffenceDetail;
import interfaces.OffenderApi.Offences;
import interfaces.OffenderApi.Offender;
import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.OffenderHelper.contactDetailsAddressesNonOfWhichHAveAMainStatus;
import static utils.OffenderHelper.contactDetailsHaveOneAddressWithNullStatus;
import static utils.OffenderHelper.contactDetailsWithEmptyAddressList;
import static utils.OffenderHelper.contactDetailsWithMultipleAddresses;
import static utils.OffenderHelper.emptyContactDetails;

public class OffenderTest {

    @Test
    public void displayNameCorrectForFirstNameSurnameOnly() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameOnlyWithEmptyMiddleNameArray() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .middleNames(ImmutableList.of())
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameAndMiddleName() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingFirstName() {
        val offender = Offender.builder()
            .surname("Jones")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingSurname() {
        val offender = Offender.builder()
            .firstName("Sam")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Ping");
    }

    @Test
    public void displayNameCorrectForMissingEverything() {
        assertThat(Offender.builder().build().displayName()).isEqualTo("");
    }

    @Test
    public void noMainAddressWhenContactDetailsAreEmpty() {
        assertThat(emptyContactDetails().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveEmptyAddressList() {
        assertThat(contactDetailsWithEmptyAddressList().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveNoAddressesWithAMainStatus() {
        assertThat(contactDetailsAddressesNonOfWhichHAveAMainStatus().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveOneAddressWithNullStatus() {
        assertThat(contactDetailsHaveOneAddressWithNullStatus().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void selectsTheMainAddressFromMultipleAddresses() {
        assertThat(contactDetailsWithMultipleAddresses().mainAddress().get().render())
            .isEqualTo("Main address Building\n7 High Street\nNether Edge\nSheffield\nYorkshire\nS10 1LE");
    }

    @Test
    public void findsCourtAppearanceForCourtReportId() {
        assertThat(courtAppearances().findForCourtReportId(4L).get().getCourtAppearanceId()).isEqualTo(3L);
    }

    @Test
    public void findsNoCourtAppearanceWhenNoCourtReportsMatch() {
        assertThat(courtAppearances().findForCourtReportId(42L).isPresent()).isFalse();
    }

    @Test
    public void findsNoCourtAppearanceWhenCourtReportsIsNull() {
        assertThat(courtAppearanceWithNullItems().findForCourtReportId(1L).isPresent()).isFalse();
    }

    @Test
    public void formatsTheOffenceDescriptionCorrectly() {
        val offence = Offence.builder()
            .offenceDate("2016-12-24T00:00")
            .detail(OffenceDetail.builder()
                .code("code")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("sub (code) - 24/12/2016");
    }

    @Test
    public void formatsTheOffenceDescriptionCorrectlyWhenDateIsMissing() {
        val offence = Offence.builder()
            .detail(OffenceDetail.builder()
                .mainCategoryDescription("main")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("main, sub");
    }

    @Test
    public void itGetsTheCorrectMainOffenceId() {
        CourtAppearance courtAppearance = courtAppearances().getItems().get(0);
        assertThat(courtAppearance.mainOffenceId()).isEqualTo("M1");
    }

    @Test
    public void itReturnEmptyStringWhenThereIsNoMainOffenceId() {
        CourtAppearance courtAppearance = courtAppearances().getItems().get(1);
        assertThat(courtAppearance.mainOffenceId()).isEqualTo("");
    }

    @Test
    public void itGetsTheCorrectOtherOffenceIds() {
        CourtAppearance courtAppearance = courtAppearances().getItems().get(0);
        assertThat(courtAppearance.otherOffenceIds()).containsOnly("A1", "A2");
    }

    @Test
    public void itEmptyListWhenThereAreNoOtherOffenceIds() {
        CourtAppearance courtAppearance = courtAppearances().getItems().get(2);
        assertThat(courtAppearance.otherOffenceIds()).isEmpty();
    }

    @Test
    public void itFindsTheMainOffenceDescription() {
        assertThat(offences().mainOffenceDescriptionForId("M1")).isEqualTo("Main, Sub");
    }

    @Test
    public void itReturnsDefaultTextWhenIdsDontMatch() {
        assertThat(offences().mainOffenceDescriptionForId("M92")).isEqualTo("NO MAIN OFFENCE FOUND");
    }

    @Test
    public void itReturnsDefaultTextIfThereIsNoMainOffence() {
        assertThat(additionalOffences().mainOffenceDescriptionForId("M1")).isEqualTo("NO MAIN OFFENCE FOUND");
    }

    @Test
    public void itFindTheOtherOffenceDescriptions() {
        assertThat(offences().otherOffenceDescriptionsForIds(ImmutableList.of("A1", "A2"))).isEqualTo("Main1, Sub1<br>Main2, Sub2");
    }

    @Test
    public void itReturnsEmptyStringWhenIdsDontMatchOffenceIds() {
        assertThat(offences().otherOffenceDescriptionsForIds(ImmutableList.of("A92", "A93"))).isEqualTo("");
    }

    @Test
    public void itReturnsEmptyStringWhenThereAreNoOtherOffenceDescriptions() {
        assertThat(onlyAMainOffence().otherOffenceDescriptionsForIds(ImmutableList.of("A1", "A2"))).isEqualTo("");
    }

    private CourtAppearances courtAppearanceWithNullItems() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(null).build()
            )).build();
    }

    private CourtAppearances courtAppearances() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(1L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("M1", "A1", "A2"))
                    .build(),
                CourtAppearance.builder()
                    .courtAppearanceId(2L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(2L)
                            .build(),
                        CourtReport.builder()
                            .courtReportId(3L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("A1", "A2"))
                    .build(),
                CourtAppearance.builder()
                    .courtAppearanceId(3L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(4L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("M1"))
                    .build()
                )).build();
    }

    private Offences offences() {

        return Offences.builder().items(
            ImmutableList.of(
                Offence.builder()
                    .offenceId("M1")
                    .mainOffence(true)
                    .detail(OffenceDetail.builder()
                        .mainCategoryDescription("Main")
                        .subCategoryDescription("Sub")
                        .build())
                    .build(),
                Offence.builder()
                    .offenceId("A1")
                    .mainOffence(false)
                    .detail(OffenceDetail.builder()
                        .mainCategoryDescription("Main1")
                        .subCategoryDescription("Sub1")
                        .build())
                    .build(),
                Offence.builder()
                    .offenceId("A1")
                    .mainOffence(false)
                    .detail(OffenceDetail.builder()
                        .mainCategoryDescription("Main2")
                        .subCategoryDescription("Sub2")
                        .build())
                    .build()

            )).build();
    }

    private Offences onlyAMainOffence() {

        return Offences.builder().items(
            ImmutableList.of(
                Offence.builder()
                    .offenceId("M1")
                    .mainOffence(true)
                    .detail(OffenceDetail.builder()
                        .mainCategoryDescription("Main")
                        .subCategoryDescription("Sub")
                        .build())
                    .build()
            )).build();
    }

    private Offences additionalOffences() {

        return Offences.builder().items(
            ImmutableList.of(
                Offence.builder()
                    .offenceId("A1")
                    .mainOffence(false)
                    .detail(OffenceDetail.builder()
                        .mainCategoryDescription("Main")
                        .subCategoryDescription("Sub")
                        .build())
                    .build()
            )).build();
    }
}
