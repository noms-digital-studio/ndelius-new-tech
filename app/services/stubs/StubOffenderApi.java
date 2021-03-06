package services.stubs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import interfaces.HealthCheckResult;
import interfaces.OffenderApi;
import lombok.val;
import play.Environment;
import play.Mode;
import play.libs.Json;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import static interfaces.HealthCheckResult.healthy;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static play.libs.Json.toJson;
import static scala.io.Source.fromInputStream;
import static services.stubs.StubPrisonerApi.NOMS_NUMBER_OF_FEMALE;

public class StubOffenderApi implements OffenderApi {

    @Override
    public CompletionStage<String> logon(String username) {
        // JWT Header/Body is {"alg":"HS512"}{"sub":"cn=fake.user,cn=Users,dc=moj,dc=com","uid":"fake.user","probationAreaCodes":["N02", "N01", "N03", "N04", "C01", "C16"],"exp":1523599298}
        return CompletableFuture.completedFuture("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbj1mYWtlLnVzZXIsY249VXNlcnMsZGM9bW9qLGRjPWNvbSIsInVpZCI6ImZha2UudXNlciIsInByb2JhdGlvbkFyZWFDb2RlcyI6WyJOMDIiLCAiTjAxIiwgIk4wMyIsICJOMDQiLCAiQzAxIiwgIkMxNiJdLCJleHAiOjE1MjM1OTkyOTh9.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ");
    }

    @Override
    public CompletionStage<Boolean> canAccess(String bearerToken, long offenderId) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletionStage<AccessLimitation> checkAccessLimitation(String bearerToken, long offenderId) {
        return CompletableFuture.completedFuture(AccessLimitation.userAllowed());
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return CompletableFuture.completedFuture(healthy());
    }

    @Override
    public CompletionStage<JsonNode> searchDb(Map<String, String> queryParams) {
        return CompletableFuture.completedFuture(toJson(ImmutableMap.of("db", "example")));
    }

    @Override
    public CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams) {
        return CompletableFuture.completedFuture(Json.toJson(ImmutableMap.of("ldap", "example")));
    }

    @Override
    public CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes) {
        return CompletableFuture.completedFuture(probationAreaCodes.stream().collect(Collectors.toMap(Function.identity(), this::probationAreaDescription)));
    }

    @Override
    public CompletionStage<Offender> getOffenderByCrn(String bearerToken, String crn) {
        if (isBlank(bearerToken)) {
            throw new RuntimeException("getOffenderByCrn called with blank bearerToken");
        }

        if (isBlank(crn)) {
            throw new RuntimeException("getOffenderByCrn called with blank CRN");
        }

        if (crn.equals("X54321")) {
            return CompletableFuture.completedFuture(Offender.builder()
                .firstName("Lucy")
                .surname("Jones")
                .middleNames(ImmutableList.of("Jane", "Suzi"))
                .dateOfBirth("1980-12-01")
                .otherIds(otherIds(NOMS_NUMBER_OF_FEMALE))
                .contactDetails(contactDetails())
                .gender("Female")
                .build());
        } else {
            return CompletableFuture.completedFuture(Offender.builder()
                .firstName("Sam")
                .surname("Jones")
                .middleNames(ImmutableList.of("Henry", "James"))
                .dateOfBirth("2000-06-22")
                .otherIds(otherIds("G8678GG"))
                .contactDetails(contactDetails())
                .gender("Male")
                .build());
        }
    }

    @Override
    public CompletionStage<JsonNode> getOffenderDetailByOffenderId(String bearerToken, String offenderId) {
        return CompletableFuture.completedFuture(Json.parse("{\n" +
                "  \"offenderId\": 2500078511,\n" +
                "  \"title\": \"Mr\",\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"middleNames\": [\n" +
                "    \"Adams\",\n" +
                "    \"ZZ\"\n" +
                "  ],\n" +
                "  \"surname\": \"Smith\",\n" +
                "  \"dateOfBirth\": \"1994-02-16\",\n" +
                "  \"gender\": \"Female\",\n" +
                "  \"otherIds\": {\n" +
                "    \"crn\": \"X069596\",\n" +
                "    \"pncNumber\": \"2008/7231544V\",\n" +
                "    \"croNumber\": \"SF68/995833Y\",\n" +
                "    \"nomsNumber\": \"A123456K\",\n" +
                "    \"niNumber\": \"AB121354U\"\n" +
                "  },\n" +
                "  \"contactDetails\": {\n" +
                "    \"phoneNumbers\": [\n" +
                "      {\n" +
                "        \"type\": \"TELEPHONE\",\n" +
                "        \"number\": \"01753862474\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"emailAddresses\": [\n" +
                "      \"Brian.Findus@session.com\"\n" +
                "    ],\n" +
                "    \"addresses\": [\n" +
                "      {\n" +
                "        \"addressNumber\": \"5\",\n" +
                "        \"buildingName\": \"Sea View\",\n" +
                "        \"county\": \"Yorkshire\",\n" +
                "        \"district\": \"Nether Edge\",\n" +
                "        \"from\": \"2018-06-22\",\n" +
                "        \"noFixedAbode\": false,\n" +
                "        \"notes\": \"\",\n" +
                "        \"postcode\": \"S10 1EQ\",\n" +
                "        \"streetName\": \"High Street\",\n" +
                "        \"telephoneNumber\": \"\",\n" +
                "        \"town\": \"Sheffield\",\n" +
                "        \"status\": {\n" +
                "          \"code\": \"M\",\n" +
                "          \"description\": \"Main\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"from\": \"2018-02-02\",\n" +
                "        \"noFixedAbode\": false,\n" +
                "        \"addressNumber\": \"25\",\n" +
                "        \"streetName\": \"Colligiate Cresecent\",\n" +
                "        \"town\": \"Sheffield\",\n" +
                "        \"postcode\": \"S10 2BJ\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"offenderProfile\": {\n" +
                "    \"offenderLanguages\": {},\n" +
                "    \"disabilities\": [\n" +
                "        {\n" +
                "            \"disabilityId\": 2500072586,\n" +
                "            \"disabilityType\": {\n" +
                "                \"code\": \"LD\",\n" +
                "                \"description\": \"Learning Difficulties\"\n" +
                "            },\n" +
                "            \"startDate\": \"2019-01-13\",\n" +
                "            \"endDate\": \"2019-01-23\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"disabilityId\": 2500073086,\n" +
                "            \"disabilityType\": {\n" +
                "                \"code\": \"SI\",\n" +
                "                \"description\": \"Speech Impairment\"\n" +
                "            },\n" +
                "            \"startDate\": \"2019-01-13\",\n" +
                "            \"endDate\": \"2019-01-23\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"disabilityId\": 2500073087,\n" +
                "            \"disabilityType\": {\n" +
                "                \"code\": \"ND\",\n" +
                "                \"description\": \"No Disability\"\n" +
                "            },\n" +
                "            \"startDate\": \"2019-01-13\"\n" +
                "        }\n" +
                "    ]," +
                "    \"previousConviction\": {},\n" +
                "    \"riskColour\": \"Amber\"\n" +
                "  },\n" +
                "  \"offenderManagers\": [\n" +
                "    {\n" +
                "      \"trustOfficer\": {\n" +
                "        \"forenames\": \"Andy\",\n" +
                "        \"surname\": \"Dolphin\"\n" +
                "      },\n" +
                "      \"staff\": {\n" +
                "        \"forenames\": \"Andy\",\n" +
                "        \"surname\": \"Dolphin\"\n" +
                "      },\n" +
                "      \"partitionArea\": \"National Data\",\n" +
                "      \"softDeleted\": false,\n" +
                "      \"team\": {\n" +
                "        \"description\": \"OMU A\",\n" +
                "        \"district\": {\n" +
                "          \"code\": \"C16CRCA\",\n" +
                "          \"description\": \"C16 County\"\n" +
                "        },\n" +
                "        \"borough\": {\n" +
                "          \"code\": \"C16100\",\n" +
                "          \"description\": \"C16 Cluster 1\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"probationArea\": {\n" +
                "        \"code\": \"C16\",\n" +
                "        \"description\": \"CPA Thames Valley\"\n" +
                "      },\n" +
                "      \"allocationReason\": {\n" +
                "        \"code\": \"OM\",\n" +
                "        \"description\": \"Offender Moved\"\n" +
                "      },\n" +
                "      \"fromDate\": \"2018-02-16\",\n" +
                "      \"active\": true\n" +
                "    },\n" +
                "    {\n" +
                "      \"trustOfficer\": {\n" +
                "        \"forenames\": \"Unallocated Staff(N04)\",\n" +
                "        \"surname\": \"Staff\"\n" +
                "      },\n" +
                "      \"staff\": {\n" +
                "        \"forenames\": \"Unallocated Staff(N04)\",\n" +
                "        \"surname\": \"Staff\"\n" +
                "      },\n" +
                "      \"partitionArea\": \"National Data\",\n" +
                "      \"softDeleted\": false,\n" +
                "      \"team\": {\n" +
                "        \"description\": \"Unallocated Team(N04)\",\n" +
                "        \"district\": {\n" +
                "          \"code\": \"N04NPSA\",\n" +
                "          \"description\": \"N04 Division\"\n" +
                "        },\n" +
                "        \"borough\": {\n" +
                "          \"code\": \"N04100\",\n" +
                "          \"description\": \"N04 Cluster 1\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"probationArea\": {\n" +
                "        \"code\": \"N04\",\n" +
                "        \"description\": \"NPS Midlands\"\n" +
                "      },\n" +
                "      \"fromDate\": \"1900-01-01\",\n" +
                "      \"toDate\": \"2018-02-16\",\n" +
                "      \"active\": false\n" +
                "    }\n" +
                "  ],\n" +
                "  \"offenderAliases\": [" +
                "    {\n" +
                "      \"firstName\": \"Tom\",\n" +
                "      \"gender\": \"Male\",\n" +
                "      \"middleNames\": [\n" +
                "        \"John\"\n" +
                "      ],\n" +
                "      \"surname\": \"Buddy\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"softDeleted\": false,\n" +
                "  \"currentDisposal\": \"1\",\n" +
                "  \"partitionArea\": \"National Data\",\n" +
                "  \"currentRestriction\": false,\n" +
                "  \"currentExclusion\": false\n" +
                "}"));
    }

    @Override
    public CompletionStage<JsonNode> getOffenderRegistrationsByOffenderId(String bearerToken, String offenderId) {
        return CompletableFuture.completedFuture(loadJsonResource("/stubdata/offender-registrations.json"));
    }

    @Override
    public CompletionStage<JsonNode> getOffenderConvictionsByOffenderId(String bearerToken, String offenderId) {
        return CompletableFuture.completedFuture(loadJsonResource("/stubdata/offender-convictions.json"));
    }

    @Override
    public CompletionStage<JsonNode> getOffenderFutureAppointmentsByOffenderId(String bearerToken, String offenderId) {
        return CompletableFuture.completedFuture(loadJsonResource("/stubdata/offender-appointments.json"));
    }

    @Override
    public CompletionStage<JsonNode> getOffenderPersonalCircumstancesByOffenderId(String bearerToken, String offenderId) {
        return CompletableFuture.completedFuture(loadJsonResource("/stubdata/offender-personal-circumstances.json"));
    }

    @Override
    public CompletionStage<CourtAppearances> getCourtAppearancesByCrn(String bearerToken, String crn) {
        if (isBlank(bearerToken)) {
            throw new RuntimeException("getOffenderByCrn called with blank bearerToken");
        }

        if (isBlank(crn)) {
            throw new RuntimeException("getOffenderByCrn called with blank CRN");
        }

        val courtAppearances = CourtAppearances.builder()
            .items(ImmutableList.of(CourtAppearance.builder()
                .courtAppearanceId(12345L)
                .appearanceDate("2018-08-01T00:00:00")
                .court(Court.builder()
                    .courtName("High Court")
                    .locality("City of Westminster")
                    .build())
                .courtReports(ImmutableList.of(
                    CourtReport.builder()
                        .courtReportId(41L)
                        .build(),
                    CourtReport.builder()
                        .courtReportId(2L)
                        .build()
                    ))
                .offenceIds(ImmutableList.of("M1", "A1", "A2"))
                .build()))
            .build();

        return CompletableFuture.completedFuture(courtAppearances);

    }

    @Override
    public CompletionStage<CourtReport> getCourtReportByCrnAndCourtReportId(String bearerToken, String crn, String courtReportId) {
        return CompletableFuture.completedFuture(CourtReport.builder()
                .courtReportId(Long.valueOf(courtReportId))
                .dateRequired("2018-08-09T00:00:00")
                .requiredByCourt(Court.builder()
                        .courtName("Old Bailey")
                        .locality("City of Westminster")
                        .build())
                .build());
    }

    @Override
    public CompletionStage<Offences> getOffencesByCrn(String bearerToken, String crn) {
        if (isBlank(bearerToken)) {
            throw new RuntimeException("getOffencesByCrn called with blank bearerToken");
        }

        if (isBlank(crn)) {
            throw new RuntimeException("getOffencesByCrn called with blank CRN");
        }

        val offences = Offences.builder()
            .items(ImmutableList.of(
                aMainOffence(),
                additionalOffence1(),
                additionalOffence2())
            ).build();

        return CompletableFuture.completedFuture(offences);
    }

    private Offence additionalOffence1() {
        return Offence.builder()
            .mainOffence(false)
            .offenceId("A1")
            .offenceDate("2018-08-01T00:00:00")
            .detail(OffenceDetail.builder()
                .code("05332")
                .description("Dishonestly retaining a wrongful credit - 05332")
                .mainCategoryCode("053")
                .mainCategoryDescription("Other frauds (Category)")
                .subCategoryCode("32")
                .subCategoryDescription("Dishonestly retaining a wrongful credit")
                .build())
            .build();
    }

    private Offence additionalOffence2() {
        return Offence.builder()
            .mainOffence(false)
            .offenceId("A2")
            .offenceCount(2L)
            .detail(OffenceDetail.builder()
                .code("05333")
                .description("Dishonest representation for obtaining benefit etc - 05333")
                .mainCategoryCode("053")
                .mainCategoryDescription("Other frauds (Category)")
                .subCategoryCode("33")
                .subCategoryDescription("Dishonest representation for obtaining benefit etc")
                .build())
            .build();
    }

    public CompletionStage<JsonNode> callOffenderApi(String bearerToken, String url) {
        return CompletableFuture.completedFuture(NullNode.instance);
    }

    private String getConvictionDate() {
        return LocalDate.now().plusMonths(-6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public CompletionStage<InstitutionalReport> getInstitutionalReport(String bearerToken, String crn, String institutionalReportId) {
        if (isBlank(bearerToken)) {
            throw new RuntimeException("getOffencesByCrn called with blank bearerToken");
        }

        if (isBlank(crn)) {
            throw new RuntimeException("getOffencesByCrn called with blank CRN");
        }

        if (isBlank(institutionalReportId)) {
            throw new RuntimeException("getOffencesByCrn called with blank institutionalReportId");
        }

        val institutionalReports = InstitutionalReport.builder()
            .conviction(Conviction.builder()
                .offences(
                    ImmutableList.of(
                        aMainOffence(),
                        additionalOffence1(),
                        additionalOffence2())
                )
                .convictionDate(getConvictionDate())
                .build())
            .sentence(Sentence.builder()
                .originalLengthUnits("Years")
                .originalLength(25L)
                .description("Life imprisonment (Adult)")
                .build())
            .build();

        return CompletableFuture.completedFuture(institutionalReports);
    }

    private Offence aMainOffence() {
        return Offence.builder()
            .mainOffence(true)
            .offenceId("M1")
            .offenceDate("2018-09-03T00:00:00")
            .offenceCount(3L)
            .detail(OffenceDetail.builder()
                .code("05331")
                .description("Obtaining a money transfer by deception - 05331")
                .mainCategoryCode("053")
                .mainCategoryDescription("Other frauds (Category)")
                .subCategoryCode("31")
                .subCategoryDescription("Obtaining a money transfer by deception")
                .build())
            .build();
    }

    private ContactDetails contactDetails() {
        return ContactDetails.builder().addresses(addresses()).build();
    }

    private ImmutableMap<String, String> otherIds(String nomisNumber) {
        return ImmutableMap.of("pncNumber", "2018/123456N", "nomsNumber", nomisNumber);
    }

    private ImmutableList<OffenderAddress> addresses() {
        return ImmutableList.of(anAddress(), anOtherAddress());
    }

    private OffenderAddress anAddress() {
        return OffenderAddress.builder()
            .buildingName("Main address Building")
            .addressNumber("7")
            .streetName("High Street")
            .district("Nether Edge")
            .town("Sheffield")
            .county("Yorkshire")
            .postcode("S7 1AB")
            .from("2010-06-22")
            .status(AddressStatus.builder()
                .code("M")
                .description("Main")
                .build())
            .build();
    }

    private OffenderAddress anOtherAddress() {
        return OffenderAddress.builder()
            .buildingName("Previous address Building")
            .addressNumber("14")
            .streetName("Low Street")
            .district("East Field")
            .town("Dover")
            .county("Kent")
            .postcode("K23 9QW")
            .from("2010-11-19")
            .status(AddressStatus.builder()
                .code("B")
                .description("Bail")
                .build())
            .build();
    }

    private String probationAreaDescription(String code) {
        val probationAreas = new HashMap<String, String>();

        probationAreas.put("ACI","Altcourse (HMP)");

        probationAreas.put("ARK","Arkham Asylum");

        probationAreas.put("ASI","Ashfield (HMYOI)");

        probationAreas.put("AGI","Askham Grange (HMP & YOI)");

        probationAreas.put("ASP","Avon & Somerset");

        probationAreas.put("AYI","Aylesbury (HMYOI)");

        probationAreas.put("BFI","Bedford (HMP)");

        probationAreas.put("BED","Bedfordshire");

        probationAreas.put("BLR","Belle Reve Federal Penitentiary");

        probationAreas.put("BAI","Belmarsh (HMP)");

        probationAreas.put("BWI","Berwyn (HMP)");

        probationAreas.put("BMI","Birmingham (HMP)");

        probationAreas.put("BHI","Blantyre House (HMP)");

        probationAreas.put("BSI","Brinsford (HMYOI)");

        probationAreas.put("BLI","Bristol (HMP)");

        probationAreas.put("BXI","Brixton (HMP)");

        probationAreas.put("BZI","Bronzefield (HMP)");

        probationAreas.put("BCI","Buckley Hall (HMP)");

        probationAreas.put("BNI","Bullingdon (HMP)");

        probationAreas.put("BRI","Bure (HMP)");

        probationAreas.put("BT1","BVT CRC");

        probationAreas.put("BVT","BVT NPS Division");

        probationAreas.put("BTC","BVT NPS Division 2");

        probationAreas.put("CBS","Cambridgeshire &Peterborough");

        probationAreas.put("CFI","Cardiff (HMP)");

        probationAreas.put("N40","Central Projects Team");

        probationAreas.put("CWI","Channings Wood (HMP)");

        probationAreas.put("CDI","Chelmsford (HMP)");

        probationAreas.put("CHS","Cheshire");

        probationAreas.put("CLI","Coldingley (HMP)");

        probationAreas.put("CKI","Cookham Wood (HMP)");

        probationAreas.put("C13","CPA BeNCH");

        probationAreas.put("C15","CPA Brist Gloucs Somerset Wilts");

        probationAreas.put("C07","CPA Cheshire and Gtr Manchester");

        probationAreas.put("C02","CPA Cumbria and Lancashire");

        probationAreas.put("C08","CPA Derby Leics Notts Rutland");

        probationAreas.put("C19","CPA Dorset Devon and Cornwall");

        probationAreas.put("C03","CPA Durham Tees Valley");

        probationAreas.put("C18","CPA Essex");

        probationAreas.put("C20","CPA Hampshire and Isle of Wight");

        probationAreas.put("C04","CPA Humber Lincs & N Yorks");

        probationAreas.put("C21","CPA Kent Surrey and Sussex");

        probationAreas.put("C17","CPA London");

        probationAreas.put("C06","CPA Merseyside");

        probationAreas.put("C14","CPA Norfolk and Suffolk");

        probationAreas.put("C01","CPA Northumbria");

        probationAreas.put("C09","CPA South Yorkshire");

        probationAreas.put("C11","CPA Staffs and West Mids");

        probationAreas.put("C16","CPA Thames Valley");

        probationAreas.put("C00","CPA Training");

        probationAreas.put("C10","CPA Wales");

        probationAreas.put("C12","CPA Warwickshire and West Mercia");

        probationAreas.put("C05","CPA West Yorkshire");

        probationAreas.put("CMB","Cumbria");

        probationAreas.put("DAI","Dartmoor (HMP)");

        probationAreas.put("DTI","Deerbolt (HMP & YOI)");

        probationAreas.put("DBS","Derbyshire");

        probationAreas.put("DCP","Devon & Cornwall");

        probationAreas.put("ALF","Document Management");

        probationAreas.put("DNI","Doncaster (HMP)");

        probationAreas.put("DRS","Dorset");

        probationAreas.put("DGI","Dovegate (HMP)");

        probationAreas.put("DWI","Downview (HMP)");

        probationAreas.put("DHI","Drake Hall (HMP & YOI)");

        probationAreas.put("N00","Dummy Trust - nDelius SPG Sender Identity");

        probationAreas.put("ZMZ","Dummy Trust - Steria Monitoring");

        probationAreas.put("DRH","Durham");

        probationAreas.put("DMI","Durham (HMP)");

        probationAreas.put("DTV","Durham and Tees Valley");

        probationAreas.put("DPP","Dyfed-Powys");

        probationAreas.put("ER2","Earth-2");

        probationAreas.put("ESI","East Sutton Park (HMP & YOI)");

        probationAreas.put("EWI","Eastwood Park (HMP)");

        probationAreas.put("EYI","Elmley (HMP)");

        probationAreas.put("EEI","Erlestoke (HMP)");

        probationAreas.put("ESX","Essex");

        probationAreas.put("EXI","Exeter (HMP)");

        probationAreas.put("N21","External - NPS London");

        probationAreas.put("N22","External - NPS Midlands");

        probationAreas.put("N23","External - NPS North East");

        probationAreas.put("N24","External - NPS North West");

        probationAreas.put("N25","External - NPS South East & Estn");

        probationAreas.put("N26","External - NPS South West & SC");

        probationAreas.put("N27","External - NPS Wales");

        probationAreas.put("FSI","Featherstone (HMP)");

        probationAreas.put("FMI","Feltham (HMP & YOI)");

        probationAreas.put("FOL","Folsom");

        probationAreas.put("FDI","Ford (HMP)");

        probationAreas.put("FBI","Forest Bank (HMP & YOI)");

        probationAreas.put("ROZ","Fort Rozz");

        probationAreas.put("FHI","Foston Hall (HMP &YOI)");

        probationAreas.put("FKI","Frankland (HMP)");

        probationAreas.put("FNI","Full Sutton (HMP)");

        probationAreas.put("GHI","Garth (HMP)");

        probationAreas.put("GTI","Gartree (HMP)");

        probationAreas.put("GPI","Glen Parva (HMYOI & RC)");

        probationAreas.put("GCS","Gloucestershire");

        probationAreas.put("MCG","Greater Manchester");

        probationAreas.put("GMP","Greater Manchester (Source Area)");

        probationAreas.put("GNI","Grendon (HMP)");

        probationAreas.put("GMI","Guys Marsh (HMP)");

        probationAreas.put("GWT","Gwent");

        probationAreas.put("HPS","Hampshire");

        probationAreas.put("HDI","Hatfield (HMP & YOI)");

        probationAreas.put("HVI","Haverigg (HMP)");

        probationAreas.put("HFS","Hertfordshire");

        probationAreas.put("HEI","Hewell (HMP)");

        probationAreas.put("HOI","High Down (HMP)");

        probationAreas.put("HPI","Highpoint (HMP)");

        probationAreas.put("HII","Hindley (HMP & YOI)");

        probationAreas.put("HBI","Hollesley Bay (HMP)");

        probationAreas.put("HHI","Holme House (HMP)");

        probationAreas.put("HLI","Hull (HMP)");

        probationAreas.put("HMI","Humber (HMP)");

        probationAreas.put("HBS","Humberside");

        probationAreas.put("HCI","Huntercombe (HMP)");

        probationAreas.put("DVI","IRC Dover ");

        probationAreas.put("HRI","IRC Haslar");

        probationAreas.put("MHI","IRC Morton Hall");

        probationAreas.put("VEI","IRC The Verne");

        probationAreas.put("ISI","Isis (HMP & YOI)");

        probationAreas.put("IWI","Isle of Wight (HMP)");

        probationAreas.put("KTI","Kennet (HMP)");

        probationAreas.put("KNT","Kent");

        probationAreas.put("KMI","Kirkham (HMP)");

        probationAreas.put("KVI","Kirklevington Grange (HMP)");

        probationAreas.put("LCS","Lancashire");

        probationAreas.put("LFI","Lancaster Farms (HMYOI & RC)");

        probationAreas.put("LEI","Leeds (HMP)");

        probationAreas.put("LCI","Leicester (HMP)");

        probationAreas.put("LTS","Leicestershire & Rutland");

        probationAreas.put("LWI","Lewes (HMP)");

        probationAreas.put("LYI","Leyhill (HMP)");

        probationAreas.put("LII","Lincoln (HMP)");

        probationAreas.put("LNS","Lincolnshire");

        probationAreas.put("LHI","Lindholme (HMP)");

        probationAreas.put("LTI","Littlehey (HMP)");

        probationAreas.put("LPI","Liverpool (HMP)");

        probationAreas.put("LDN","London");

        probationAreas.put("LLI","Long Lartin (HMP)");

        probationAreas.put("LGI","Lowdham Grange (HMP)");

        probationAreas.put("LNI","Low Newton (HMP)");

        probationAreas.put("MSI","Maidstone (HMP)");

        probationAreas.put("MRI","Manchester (HMP)");

        probationAreas.put("MRS","Merseyside");

        probationAreas.put("MGO","MOGO");

        probationAreas.put("MDI","Moorland (HMP & YOI)");

        probationAreas.put("RIO","NEW");

        probationAreas.put("NHI","New Hall (HMP & YOI)");

        probationAreas.put("ZZZ","Non-NOMS Region");

        probationAreas.put("NFK","Norfolk");

        probationAreas.put("NSP","Norfolk and Suffolk");

        probationAreas.put("NTS","Northamptonshire");

        probationAreas.put("NSI","North Sea Camp (HMP)");

        probationAreas.put("NLI","Northumberland (HMP)");

        probationAreas.put("NBR","Northumbria");

        probationAreas.put("WSN","North Wales");

        probationAreas.put("NWI","Norwich (HMP & YOI)");

        probationAreas.put("ALL","No Trust or Trust Unknown");

        probationAreas.put("NMI","Nottingham (HMP)");

        probationAreas.put("NHS","Nottinghamshire");

        probationAreas.put("N07","NPS London");

        probationAreas.put("N04","NPS Midlands");

        probationAreas.put("N02","NPS North East");

        probationAreas.put("N01","NPS North West");

        probationAreas.put("N06","NPS South East and Eastern");

        probationAreas.put("N05","NPS South West and South Central");

        probationAreas.put("N03","NPS Wales");

        probationAreas.put("OWI","Oakwood");

        probationAreas.put("ONI","Onley (HMP)");

        probationAreas.put("OUT","Outside/Released");

        probationAreas.put("PRI","Parc (HMP & YOI)");

        probationAreas.put("PVI","Pentonville (HMP)");

        probationAreas.put("PBI","Peterborough");

        probationAreas.put("PFI","Peterborough Female");

        probationAreas.put("PDI","Portland (HMP & YOI)");

        probationAreas.put("UPI","Prescoed (HMP & YOI)");

        probationAreas.put("PNI","Preston (HMP)");

        probationAreas.put("RNI","Ranby (HMP)");

        probationAreas.put("RSI","Risley (HMP)");

        probationAreas.put("RCI","Rochester (HMYOI)");

        probationAreas.put("RHI","Rye Hill (HMP)");

        probationAreas.put("sfi","San So");

        probationAreas.put("SDI","Send (HMP)");

        probationAreas.put("SWS","South Wales");

        probationAreas.put("YSS","South Yorkshire");

        probationAreas.put("SPG","SPG Sender Identity");

        probationAreas.put("SPI","Spring Hill (HMP)");

        probationAreas.put("SFI","Stafford (HMP)");

        probationAreas.put("STF","Staffordshire");

        probationAreas.put("SWM","Staffordshire and West Midlands");

        probationAreas.put("EHI","Standford Hill (HMP)");

        probationAreas.put("SC1","Star City");

        probationAreas.put("SKI","Stocken (HMP)");

        probationAreas.put("SHI","Stoke Heath (HMP & YOI)");

        probationAreas.put("STI","Styal (HMP & YOI)");

        probationAreas.put("SUI","Sudbury (HMP)");

        probationAreas.put("SFK","Suffolk");

        probationAreas.put("SRY","Surrey");

        probationAreas.put("SSP","Surrey and Sussex ");

        probationAreas.put("SSX","Sussex");

        probationAreas.put("SLI","Swaleside (HMP)");

        probationAreas.put("SWI","Swansea (HMP)");

        probationAreas.put("SNI","Swinfen Hall (HMP & YOI)");

        probationAreas.put("TES","Teeside");

        probationAreas.put("TSI","Thameside");

        probationAreas.put("TVP","Thames Valley");

        probationAreas.put("MTI","The Mount (HMP)");

        probationAreas.put("TCI","Thorn Cross (HMYOI)");

        probationAreas.put("A00","Transfer Provider");

        probationAreas.put("UAT","Unallocated");

        probationAreas.put("UNK","Unknown");

        probationAreas.put("UAL","Unlawfully at Large");

        probationAreas.put("UKI","Usk (HMP)");

        probationAreas.put("WDI","Wakefield (HMP)");

        probationAreas.put("WPT","Wales Probation Trust");

        probationAreas.put("WWI","Wandsworth (HMP)");

        probationAreas.put("WII","Warren Hill (HMP & YOI)");

        probationAreas.put("WWS","Warwickshire");

        probationAreas.put("WLI","Wayland (HMP)");

        probationAreas.put("WEI","Wealstun (HMP)");

        probationAreas.put("WNI","Werrington (HMYOI)");

        probationAreas.put("WMP","West Mercia");

        probationAreas.put("MLW","West Midlands");

        probationAreas.put("YSW","West Yorkshire");

        probationAreas.put("WYI","Wetherby (HMYOI)");

        probationAreas.put("WTI","Whatton (HMP)");

        probationAreas.put("WRI","Whitemoor (HMP)");

        probationAreas.put("WTS","Wiltshire");

        probationAreas.put("WCI","Winchester (HMP)");

        probationAreas.put("WHI","Woodhill (HMP)");

        probationAreas.put("WSI","Wormwood Scrubs (HMP)");

        probationAreas.put("WMI","Wymott (HMP)");

        probationAreas.put("YSN","York and North Yorkshire");

        probationAreas.put("CPT","Z_Never Used");

        probationAreas.put("zz9","zz9 PROVIDER-");

        probationAreas.put("XXX","ZZ BAST Public Provider 1");

        probationAreas.put("ZMM","ZZ - Steria Monitoring Trust");

        probationAreas.put("ZSM","ZZ - Steria Support Trust");

        probationAreas.put("ZPP","ZZ - Test PPCS");

        return probationAreas.get(code);
    }

    private static JsonNode loadJsonResource(String resource) {
        return Json.parse(new Environment(Mode.DEV).resourceAsStream(resource));
    }


}
