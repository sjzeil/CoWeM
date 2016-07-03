/**
 * 
 */
package edu.odu.cs.cwm.documents;

/**
 * A collection of text substitutions to repalce sommon HTML
 * entities by their Unicode numeric equivalents.
 * 
 * @author zeil
 *
 */
public class CommonEntitySubstitutions implements TextSubstitutions {

    /**
     * PegDown inserts various common HTML entities into its output.
     * These are an inconvenience during XSLT processing, however, as they
     * are not defined in general XML processing. This table is used to
     * replace these common symbolic entuities by their Unicode numeric
     * codes. 
     */
    private static final String[] COMMON_ENTITIES = {
            "cent",     "#162",  
            "pound", "#163",
            "sect", "#167",
            "copy", "#169",
            "laquo", "#171",
            "raquo", "#187",
            "reg", "#174",
            "deg", "#176",
            "plusmn", "#177",
            "para", "#182",
            "middot", "#183",
            "frac12", "#188",
            "ndash", "#8211",
            "mdash", "#8212",
            "lsquo", "#8216",
            "rsquo", "#8217",
            "sbquo", "#8218",
            "ldquo", "#8220", 
            "rdquo", "#8221",
            "bdquo", "#8222",
            "dagger", "#8224",
            "Dagger", "#8225",
            "bull", "#8226",
            "hellip", "#8230",
            "prime", "#8242",
            "Prime", "#8243",
            "euro", "#8364",
            "trade", "#8482",
            "asymp", "#8776",
            "ne", "#8800",
            "le", "#8804",
            "ge", "#8805",
            "nbsp", "#160"
    };


    /**
     * Create a substitution set.
     */
    public CommonEntitySubstitutions() {
    }


    /**
     * Apply substitutions.
     * 
     * @param toString  original string
     * @return original after applying substitutions.
     */
    @Override
    public final String apply(final String toString) {
        String result = toString;
        for (int i = 0; i < COMMON_ENTITIES.length; i += 2) {
            String target = "&" + COMMON_ENTITIES[i] + ";";
            String value = "&" + COMMON_ENTITIES[i + 1] + ";";
            result = result.replace(target, value);
        }
        return result;
    }

}
