package views.pages.paroleparom1report;

public enum Page {
    PRISONER_DETAILS("2", "Prisoner details"),
    PRISONER_CONTACT("3", "Prisoner contact"),
    ROSH_AT_POINT_OF_SENTENCE("4", "RoSH at point of sentence"),
    VICTIMS("5", "Victims"),
    OPD_PATHWAY("6", "OPD pathway"),
    INTERVENTION("7", "Interventions"),
    SENTENCE_PLAN("8", "Prison sentence plan and response"),
    MAPPA("9", "Multi Agency Public Protection Arrangements (MAPPA)"),
    CURRENT_RISK_ASSESSMENT_SCORES("10", "Current risk assessment scores"),
    CURRENT_ROSH_COMMUNITY("11", "Current RoSH: community"),
    CURRENT_ROSH_CUSTODY("12", "Current RoSH: custody"),
    RISK_TO_PRISONER("13", "Risk to the prisoner"),
    ROSH_ANALYSIS("14", "RoSH analysis"),
    RISK_MANAGEMENT_PLAN("15", "Risk Management Plan (RMP)"),
    RESETTLEMENT_PLAN("16", "Resettlement plan for release"),
    SUPERVISION_PLAN("17", "Supervision plan for release"),
    RECOMMENDATION("18", "Recommendation"),
    ORAL_HEARING("19", "Oral hearing"),
    SOURCES("20", "Sources"),
    CHECK_YOUR_REPORT("21", "Check your report"),
    SIGNATURE("22", "Signature & date")
    ;
    private final String pageNumber;
    private final String pageHeader;

    Page(String pageNumber, String pageHeader) {
        this.pageNumber = pageNumber;
        this.pageHeader = pageHeader;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public String getPageHeader() { return pageHeader; }
}
