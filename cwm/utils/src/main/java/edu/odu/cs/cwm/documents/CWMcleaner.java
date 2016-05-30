/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The pegDown processor gets confused by passthrough HTML display elements
 * (though inline elements are OK)., often adding bogus <p> elements
 * around the opening and/or closing tag, resulting in bad XML.
 * 
 * As a workaround, the macro processor can be
 * used to insert <cwm tag='tagname' ... /> empty elements. Here we change
 * these to <tagname ... >, after stripping away any immediately adjacent
 * <p> and </p> tags.
 *  
 * @author Zeil
 */
public class CWMcleaner implements TextSubstitutions {
    
    /**
     * Regular expression used to detect cwm tags.
     */
    private Pattern cwmPattern;
    
 
    /**
     * Create a cleaner.
     */
    public CWMcleaner() {
        cwmPattern = Pattern.compile("(<p>)?[<]cwm ([^>]*)/[>](</p>)?");
    }
    
    
    /**
     * Apply substitutions.
     * 
     * @param target  original string
     * @return original after applying substitutions.
     */
    @Override
    public final String apply(final String target) {
        final String tagAttrStart = "tag=";
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = cwmPattern.matcher(target);
        int start = 0;
        int counter = 0;
        //System.err.println("*********");
        //System.err.println(target);
        //System.err.println("*********");
        
        while (matcher.find()) {
            ++counter;
            int cwmStart = matcher.start();
            int cwmStop = matcher.end();
            String content = matcher.group(2);

            String[] attributePairs = content.split("  *");
            
            buffer.append(target.substring(start, cwmStart));
            
            String tag = "";
            for (String pair: attributePairs) {
                                
                if (pair.startsWith(tagAttrStart)) {
                    tag = pair.substring(tagAttrStart.length() + 1,  
                            pair.length() - 1);
                    break;
                }
            }
            
            buffer.append('<');
            buffer.append(tag);
            
            if (!tag.startsWith("/")) {
                for (String pair: attributePairs) {
                    if (!pair.startsWith(tagAttrStart)) {
                        buffer.append(' ');
                        buffer.append(pair);
                    }
                }
            }
            buffer.append('>');
            
            start = cwmStop;
        }
        buffer.append(target.substring(start));
        //System.err.println(buffer.toString());
        //System.err.println("*********");
        //System.err.println("Applied " + counter + " substitutions.");
        
        return buffer.toString();
    }

}
