package views.pages.shortformatpresentencereport;

public enum Page {
    OFFENDER_DETAILS("2", "Offender details"),
    SENTENCING_COURT_DETAILS("3", "Sentencing court details"),
    SOURCES_OF_INFORMATION("4", "Sources of information"),
    OFFENCE_DETAILS("5", "Offence details"),
    OFFENCE_ANALYSIS("6", "Offence analysis"),
    OFFENDER_ASSESSMENT("7", "Offender assessment"),
    RISK_ASSESSMENT("8", "Risk assessment"),
    PROPOSAL("9", "Proposal"),
    CHECK_YOUR_REPORT("10", "Check your report"),
    SIGNATURE("11", "Sign your report");

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
