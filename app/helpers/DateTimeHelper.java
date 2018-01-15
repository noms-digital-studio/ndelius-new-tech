package helpers;

import java.time.Clock;
import java.time.LocalDate;

import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class DateTimeHelper {
    public static int calculateAge(String dateString, Clock clock) {
        LocalDate dateOfBirth = parse(dateString, ISO_LOCAL_DATE);
        long daysOld = now(clock).toEpochDay() - dateOfBirth.toEpochDay();
        return (int) (daysOld / 365);
    }

}
