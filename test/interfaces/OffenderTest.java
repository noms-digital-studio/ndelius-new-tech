package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Offender;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OffenderTest {

    @Test
    public void displayNameCorrectForFirstNameSurnameOnly() {
        Offender offender = new Offender(
            "Sam",
            "Jones",
            null,
            null,
            null,
            null
        );

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameAndMiddleName() {
        Offender offender = new Offender(
            "Sam",
            "Jones",
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );

        assertThat(offender.displayName()).isEqualTo("Sam Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingFirstName() {
        Offender offender = new Offender(
            null,
            "Jones",
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingSurname() {
        Offender offender = new Offender(
            "Sam",
            null,
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("Sam Ping");
    }

    @Test
    public void displayNameCorrectForMissingEverything() {
        Offender offender = new Offender(
            null,
            null,
            null,
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("");
    }

}
