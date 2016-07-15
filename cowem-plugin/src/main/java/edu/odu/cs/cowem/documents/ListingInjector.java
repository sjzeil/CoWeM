/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scans for longlisting elements and injects the code
 * from a file as a CDATA field.
 *  
 * @author Zeil
 */
public class ListingInjector implements TextSubstitutions {
    
    /**
     * Regular expression used to detect cwm tags.
     */
    private Pattern listingPattern;
    
    
    /**
     * Directory from which source code was being obtained.
     */
    private File sourceDirectory;

    
    
    
    /**
     * For logging error messages.
     */
    private static Logger logger = 
            LoggerFactory.getLogger(ListingInjector.class);

    /**
     * Create a cleaner.
     * 
     * @param sourceDir; directory from which document source was loaded.
     */
    public ListingInjector(File sourceDir) {
        listingPattern = Pattern.compile
             ("[<]longlisting ([^>]*)[>]([^<]*)[<]/longlisting([^>]*)[>]");
        sourceDirectory = sourceDir;
    }
    
    
    /**
     * Apply substitutions.
     * 
     * @param target  original string
     * @return original after applying substitutions.
     */
    @Override
    public final String apply(final String target) {
        
        final String fileAttrStart = "file=";
        final int initialBufferCapacity = 1024;
        StringBuilder buffer = new StringBuilder(initialBufferCapacity);
        Matcher matcher = listingPattern.matcher(target);
        int start = 0;
        
        while (matcher.find()) {
            int elementStart = matcher.start();
            int elementStop = matcher.end();
            String attributes = matcher.group(1);

            String[] attributePairs = attributes.split("  *");
            
            buffer.append(target.substring(start, elementStart));
            
            String fileName = "";
            for (String pair: attributePairs) {
                                
                if (pair.startsWith(fileAttrStart)) {
                    fileName = pair.substring(fileAttrStart.length() + 1,  
                            pair.length() - 1);
                    break;
                }
            }
            
            buffer.append("<longlisting ");
            buffer.append(attributes);
            buffer.append("><![CDATA[");
            
            
            try (BufferedReader fileIn 
                    = new BufferedReader(new FileReader(
                            new File(sourceDirectory, fileName)))) {
                String line = fileIn.readLine();
                while (line != null) {
                    buffer.append(line);
                    buffer.append('\n');
                    line = fileIn.readLine();
                }
            } catch (FileNotFoundException e) {
                String msg = "Could not open file " + fileName;
                buffer.append (msg);
                logger.warn(msg);
            } catch (IOException e) {
                String msg = "Problem reading file " + fileName;
                buffer.append (msg);
                logger.warn(msg, e);
            }
            buffer.append("]]></longlisting>\n");
            
            start = elementStop;
        }
        buffer.append(target.substring(start));
        
        return buffer.toString();
    }

}
