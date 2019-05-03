package data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchedDefendant {
    private String crn;
    private String pncNumber;
    private String surname;
    private String firstName;
    private String dateOfBirth;
    private String address;
}
