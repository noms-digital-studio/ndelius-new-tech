package utils;

import interfaces.OffenderApi;

public class InstitutionalReportHelpers {
    public static OffenderApi.InstitutionalReport anInstitutionalReport() {
        return OffenderApi.InstitutionalReport.builder()
            .conviction(OffenderApi.Conviction.builder()
                .mainOffence(OffenderApi.Offence.builder()
                    .offenceId("123")
                    .mainOffence(true)
                    .offenceDate("2018-11-08T00:00")
                    .detail(OffenderApi.OffenceDetail.builder()
                        .subCategoryDescription("Stealing the limelight")
                        .code("code123")
                        .build())
                    .build())
                .build())
            .build();
    }
}
