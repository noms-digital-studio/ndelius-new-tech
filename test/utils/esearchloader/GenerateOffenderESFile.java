package utils.esearchloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import lombok.val;
import play.Environment;
import play.Mode;
import scala.compat.java8.ScalaStreamSupport;
import scala.io.Source;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static play.libs.Json.parse;

public class GenerateOffenderESFile {
    private final static Pattern firstNumberPattern = Pattern.compile("\\d+ ");
    private static final String CSV_RESOURCE_FILE = "/esearchloader/uk-1000000.csv";
    private static long EIGHTEEN_YEARS_IN_DAYS = 18 * 365;
    private static int FIFTY_YEARS_IN_DAYS = 50 * 365;
    private static List<Map.Entry<String, String>> probationAreas = probationAreas();

    public static void main(String[] args) throws IOException {
        System.out.println("Generating source file for offender records...");
        val environment = new Environment(null, GenerateOffenderESFile.class.getClassLoader(), Mode.TEST);
        val offenderTemplate = Source.fromInputStream(environment.resourceAsStream("/esearchloader/generate-offender-search-result.json"), "UTF-8").mkString();
        val path = new File(environment.resource(CSV_RESOURCE_FILE).getPath()).getParent();
        val outputFile = new File(path, "es-test-data.txt");
        val randomPersonData = readCsv(environment, CSV_RESOURCE_FILE);

        try (val output = new BufferedWriter(new FileWriter(outputFile))) {

            randomPersonData.forEach(element -> {
                val offender = (ObjectNode) parse(offenderTemplate);
                offender.replace("offenderId", numberNode(new Random().nextInt(Integer.MAX_VALUE)));
                offender.replace("title", textNode(randomTitle()));
                val firstName = textNode(element.get("firstName"));
                offender.replace("firstName", firstName);
                val surname = textNode(element.get("surname"));
                offender.replace("surname", surname);
                offender.replace("middleNames", randomMiddleNames(randomPersonData));
                offender.replace("dateOfBirth", textNode(randomDateOfBirth()));
                offender.replace("gender", textNode(randomGender()));
                offender.replace("otherIds", randomIds());
                val contactDetails = (ObjectNode) offender.get("contactDetails");
                val phoneNumbers = (ArrayNode) contactDetails.get("phoneNumbers");
                ((ObjectNode) phoneNumbers.get(0)).replace("number", textNode(element.get("telephoneNumber")));
                contactDetails.replace("addresses", addresses(element, randomPersonData));
                contactDetails.replace("emailAddresses", emailAddresses(element));
                if (chance(30)) {
                    offender.set("offenderAliases", aliases(element, randomPersonData, firstName, surname));
                }
                val offenderProfile = (ObjectNode) offender.get("offenderProfile");
                if (chance(10)) {
                    offenderProfile.replace("riskColour", textNode(randomRisk()));
                } else {
                    offenderProfile.remove("riskColour");
                }
                offender.replace("currentDisposal", textNode(chance(10) ? "1" : "0"));
                offender.replace("currentRestriction", booleanNode(chance(1)));
                offender.replace("currentExclusion", booleanNode(chance(1)));

                val activeOffenderManager = (ObjectNode)offender.get("offenderManagers").get(0);
                val staff = (ObjectNode)activeOffenderManager.get("staff");
                staff.replace("surname", textNode(randomSurname(randomPersonData)));
                staff.replace("forenames", textNode(randomName(randomPersonData)));
                val probationArea = (ObjectNode)activeOffenderManager.get("probationArea");
                val areaEntry = randomProbationArea();
                probationArea.replace("code", textNode(areaEntry.getKey()));
                probationArea.replace("description", textNode(areaEntry.getValue()));

                write(output, offender);
            });
        }

        System.out.println(String.format("Finished. Results in %s", outputFile));
    }

    private static String randomRisk() {
        if (chance(20)) {
            return "Green";
        } else {
            if (chance(60)) {
                return "Amber";
            } else {
                return "Red";
            }
        }
    }

    private static void write(BufferedWriter output, JsonNode node) {
        try {
            System.out.print(".");
            output.write(String.format("{\"index\":{\"_id\":%d,\"_type\":\"document\",\"pipeline\":\"pnc-pipeline\"}}", node.get("offenderId").asLong()));
            output.newLine();
            output.write(node.toString());
            output.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayNode aliases(Map<String, String> element, List<Map<String, String>> randomPersonData, TextNode firstName, TextNode surname) {
        final ArrayNode aliases = JsonNodeFactory.instance.arrayNode();
        aliases.add(alias(element, randomPersonData));
        IntStream.range(0, randomUpTo(5)).forEach(ignoredCount -> aliases.add(randomAlias(randomPersonData, firstName, surname)));

        return aliases;
    }

    private static ArrayNode emailAddresses(Map<String, String> element) {
        final ArrayNode emailAddresses = JsonNodeFactory.instance.arrayNode();
        emailAddresses.add(element.get("emailAddress"));
        return emailAddresses;
    }

    private static ArrayNode addresses(Map<String, String> element, List<Map<String, String>> randomPersonData) {
        final ArrayNode addresses = JsonNodeFactory.instance.arrayNode();
        addresses.add(address(element));
        IntStream.range(0, randomUpTo(3)).forEach(ignoredCount -> addresses.add(randomAddress(randomPersonData)));

        return addresses;
    }

    private static ObjectNode randomAddress(List<Map<String, String>> randomPersonData) {
        val address = JsonNodeFactory.instance.objectNode();

        address.put("from", randomDate());
        address.put("noFixedAbode", false);
        address.put("addressNumber", randomPersonData.get(randomUpTo(randomPersonData.size())).get("addressNumber"));
        address.put("streetName", randomPersonData.get(randomUpTo(randomPersonData.size())).get("streetName"));
        address.put("town", randomPersonData.get(randomUpTo(randomPersonData.size())).get("town"));
        address.put("county", randomPersonData.get(randomUpTo(randomPersonData.size())).get("county"));
        address.put("postcode", randomPersonData.get(randomUpTo(randomPersonData.size())).get("postcode"));
        return address;
    }

    private static ObjectNode randomAlias(List<Map<String, String>> randomPersonData, TextNode firstName, TextNode surname) {
        if (chance(80)) {

            return aliasFromCurrentName(firstName, surname);
        }

        return alias(randomPersonData.get(randomUpTo(randomPersonData.size())), randomPersonData);
    }

    private static ObjectNode address(Map<String, String> element) {
        val address = JsonNodeFactory.instance.objectNode();

        address.put("from", randomDate());
        address.put("noFixedAbode", false);
        address.put("addressNumber", element.get("addressNumber"));
        address.put("streetName", element.get("streetName"));
        address.put("town", element.get("town"));
        address.put("county", element.get("county"));
        address.put("postcode", element.get("postcode"));
        return address;
    }

    private static ObjectNode alias(Map<String, String> element, List<Map<String, String>> randomPersonData) {
        val address = JsonNodeFactory.instance.objectNode();

        address.put("dateOfBirth", randomDateOfBirth());
        address.put("firstName", randomName(randomPersonData));
        address.put("surname", element.get("surname"));
        address.put("gender", randomGender());
        return address;
    }

    private static ObjectNode aliasFromCurrentName(TextNode firstName, TextNode surname) {
        val address = JsonNodeFactory.instance.objectNode();

        address.put("dateOfBirth", randomDateOfBirth());
        address.set("firstName", firstName);
        address.set("surname", surname);
        address.put("gender", randomGender());
        return address;
    }

    private static ObjectNode randomIds() {
        val ids = JsonNodeFactory.instance.objectNode();
        ids.put("crn", randomCrn());
        if (chance(50)) {
            ids.put("pncNumber", randomPnc());
        }
        if (chance(30)) {
            ids.put("croNumber", randomCro());
        }
        if (chance(30)) {
            ids.put("niNumber", randomNino());
        }
        return ids;
    }

    private static String randomNino() {
        return String.format("%c%c%06d%c", randomCapitalLetter(), randomCapitalLetter(), randomUpTo(999999), randomCapitalLetter());
    }

    private static String randomPnc() {
        return String.format("%s/%07d%c", randomYear(), randomUpTo(9999999), randomCapitalLetter());
    }

    private static String randomCro() {
        return String.format("%c%c%02d/%06d%c", randomCapitalLetter(), randomCapitalLetter(), randomUpTo(99), randomUpTo(999999), randomCapitalLetter());
    }

    private static String randomYear() {
        return String.valueOf(2018 - randomUpTo(25));
    }

    private static boolean chance(int percent) {
        return randomUpTo(100) < percent;
    }

    private static String randomCrn() {
        return String.format("%c%06d", randomCapitalLetter(), randomUpTo(999999));
    }

    private static char randomCapitalLetter() {
        return (char) ('A' + randomUpTo(25));
    }

    private static String randomGender() {
        return randomUpTo(100) > 10 ? "Male" : "Female";
    }

    private static String randomDateOfBirth() {
        return LocalDate.now().minusDays(EIGHTEEN_YEARS_IN_DAYS + randomUpTo(FIFTY_YEARS_IN_DAYS)).format(DateTimeFormatter.ISO_DATE);
    }

    private static String randomDate() {
        return LocalDate.now().minusDays(EIGHTEEN_YEARS_IN_DAYS).format(DateTimeFormatter.ISO_DATE);
    }

    private static TextNode textNode(String text) {
        return JsonNodeFactory.instance.textNode(text);
    }

    private static NumericNode numberNode(int number) {
        return JsonNodeFactory.instance.numberNode(number);
    }

    private static BooleanNode booleanNode(boolean maybe) {
        return JsonNodeFactory.instance.booleanNode(maybe);
    }

    private static ArrayNode randomMiddleNames(List<Map<String, String>> randomPersonData) {
        final ArrayNode names = JsonNodeFactory.instance.arrayNode();
        IntStream.range(0, randomUpTo(3)).forEach(ignoredCount -> names.add(randomName(randomPersonData)));

        return names;
    }

    private static String randomName(List<Map<String, String>> randomPersonData) {
        return randomPersonData.get(randomUpTo(randomPersonData.size())).get("firstName");
    }

    private static String randomSurname(List<Map<String, String>> randomPersonData) {
        return randomPersonData.get(randomUpTo(randomPersonData.size())).get("surname");
    }

    private static int randomUpTo(int bound) {
        return new Random().nextInt(bound);
    }

    private static String randomTitle() {
        String[] titles = {"Mr", "Ms", "Mrs"};
        return titles[randomUpTo(3)];
    }

    private static List<Map<String, String>> readCsv(Environment environment, String fileName) {
        return ScalaStreamSupport.stream(Source.fromInputStream(environment.resourceAsStream(fileName), "UTF-8")
                .getLines())
                .map(line -> line.split("\",\""))
                .map(part -> Arrays.stream(part).map(item -> item.replace("\"", ""))
                        .collect(Collectors.toList()))
                .map(GenerateOffenderESFile::toOffenderMap)
                .collect(Collectors.toList());
    }

    private static Map<String, String> toOffenderMap(List<String> row) {
        // "first_name","last_name","company_name","address","city","county","postal","phone1","phone2","email","web"

        final HashMap<String, String> offenderMap = new HashMap<>();
        offenderMap.put("firstName", row.get(0));
        offenderMap.put("surname", row.get(1));
        offenderMap.put("addressNumber", firstNumber(row.get(3)));
        offenderMap.put("streetName", afterFirstNumber(row.get(3)));
        offenderMap.put("town", row.get(4));
        offenderMap.put("county", row.get(5));
        offenderMap.put("postcode", row.get(6));
        offenderMap.put("telephoneNumber", row.get(7));
        offenderMap.put("emailAddress", row.get(9));
        return offenderMap;
    }

    private static String afterFirstNumber(String addressLine) {
        return addressLine.replaceFirst(firstNumberPattern.pattern(), "");
    }

    private static String firstNumber(String addressLine) {
        Matcher m = firstNumberPattern.matcher(addressLine);
        return m.find() ? m.group() : null;
    }
    
    private static Map.Entry<String, String> randomProbationArea() {
        return probationAreas.get(randomUpTo(probationAreas.size()));
    }
    
    private static List<Map.Entry<String, String>> probationAreas() {
        val probationAreas = new LinkedHashMap<String, String>();

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

        return new ArrayList<>(probationAreas.entrySet());

    }
}
