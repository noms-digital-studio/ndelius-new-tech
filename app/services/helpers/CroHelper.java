package services.helpers;

/**
 * CRO: Criminal Record Office reference
 */
public interface CroHelper {

    static boolean canBeConvertedToACro(String term) {
        return canBeConvertedToAFullCro(term) ||
               canBeConvertedToASearchFileCro(term);
    }

    static String covertToCanonicalCro(String croNumber) {
        return croNumber.toLowerCase();
    }

    // e.g. 123456/08A
    static boolean canBeConvertedToAFullCro(String term) {
        return term.matches("^[0-9]{1,6}/[0-9]{2}[a-zA-Z]");
    }

    // e.g. SF93/123456A
    static boolean canBeConvertedToASearchFileCro(String term) {
        return term.matches("^SF[0-9]{2}/[0-9]{1,6}[a-zA-Z]");
    }
}
