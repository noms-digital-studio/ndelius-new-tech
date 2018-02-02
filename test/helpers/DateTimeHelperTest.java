package helpers;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeHelperTest {

    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));

    @Test
    public void calculatesCorrectAge() {
        assertThat(DateTimeHelper.calculateAge("1969-01-01", fixedClock)).isEqualTo(1);
        assertThat(DateTimeHelper.calculateAge("1954-02-01", fixedClock)).isEqualTo(15);
    }

    @Test
    public void returnsTrueForStringsThatAreValidDates() {
        assertThat(DateTimeHelper.looksLikeADate("1992-02-01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-2-01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-2-1")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01-02-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1-12-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1-2-1992")).isEqualTo(true);

        assertThat(DateTimeHelper.looksLikeADate("01-Feb-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01-feb-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1-Feb-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1-feb-1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-Feb-01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-feb-01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-Feb-1")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992-feb-1")).isEqualTo(true);

        assertThat(DateTimeHelper.looksLikeADate("1992/02/01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992/2/01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992/2/1")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01/02/1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01/2/1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1/2/1992")).isEqualTo(true);

        assertThat(DateTimeHelper.looksLikeADate("1992/Feb/01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992/feb/01")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992/Feb/1")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1992/feb/1")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01/Feb/1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("01/feb/1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1/Feb/1992")).isEqualTo(true);
        assertThat(DateTimeHelper.looksLikeADate("1/feb/1992")).isEqualTo(true);

    }

    @Test
    public void returnsFalseForStringsThatAreNotValidDates() {
        assertThat(DateTimeHelper.looksLikeADate("1992 02 01")).isEqualTo(false);
        assertThat(DateTimeHelper.looksLikeADate("1992:02:01")).isEqualTo(false);
        assertThat(DateTimeHelper.looksLikeADate("1992:Feb:01")).isEqualTo(false);
        assertThat(DateTimeHelper.looksLikeADate("foo bar")).isEqualTo(false);
    }
}