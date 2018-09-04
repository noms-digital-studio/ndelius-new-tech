package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Offence;
import interfaces.OffenderApi.Offences;

public class OffenceHelpers {
    public static Offences someOffences() {

        return Offences.builder()
            .items(ImmutableList.of(
                Offence.builder()
                    .offenceId("M1")
                    .mainOffence(true)
                    .build()))
            .build();
    }
}
