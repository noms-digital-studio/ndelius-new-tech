package utils;

import interfaces.OffenderApi;

public class InstitutionalReportHelpers {
    public static OffenderApi.InstitutionalReport anInstitutionalReport() {
        return OffenderApi.InstitutionalReport.builder()
            .conviction(OffenderApi.Conviction.builder()
                .mainOffence(OffenderApi.Offence.builder()
                    .offenceId("123")
                    .mainOffence(true)
                    .offenceDate("08/11/2018")
                    .detail(OffenderApi.OffenceDetail.builder()
                        .subCategoryDescription("Sub cat desc")
                        .code("code123")
                        .build())
                    .build())
                .build())
            .build();
    }
}
