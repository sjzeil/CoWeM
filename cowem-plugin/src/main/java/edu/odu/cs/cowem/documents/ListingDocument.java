/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Document consisting of source code or uninterpreted data.
 * 
 * @author zeil
 *
 */
public class ListingDocument implements Document {
	
 
 	/**
	 * For logging error messages.
	 */
	private static Logger logger = 
	        LoggerFactory.getLogger(ListingDocument.class);
	
	
	/**
	 * File from which source code was being obtained.
	 */
	private File sourceFile;
		
    /**
     * Properties to be used when processing this document.
     */
    private Properties properties;

    /**
     * Context info describing this project and its document sets.
     */
	private WebsiteProject project;
	

    /**
     * Create a document from the given file.
     * @param input listing document source file
     * @param project0 website project contest.
     * @param properties0 Properties to be used in processing this document.
     */
	public ListingDocument(final File input,
	        final WebsiteProject project0,
	        final Properties properties0) {
		sourceFile = input;
		properties = properties0;
		project = project0;
	}
	
    /**
     * Transform this document into a string suitable for writing into
     * an HTML file.
     *  
     * @param format   Specifies a style/format for the generated HTML. 
     * @return String containing HTML of page for web site.
     */
    @Override
    public final String transform(final String format) {
        StringWriter sout = new StringWriter();
        sout.append("Title: ");
        sout.append(sourceFile.getName());
        sout.append("\n\n");
        try (BufferedReader documentIn 
                = new BufferedReader (new FileReader (sourceFile));) {
            String line = documentIn.readLine();
            while (line != null) {
                sout.append("    ");
                sout.append(line);
                sout.append("\n");
                line = documentIn.readLine();
            }
            //sout.append("'''\n");
            sout.append("\n[unformatted source](" 
                    + sourceFile.getName() 
                    + ")\n\n\n");
        } catch (IOException e) {
            logger.warn("Unable to read from " + sourceFile + ": " + e);
        }
        String newSource = sout.toString();
        MarkdownDocument doc = new MarkdownDocument (sourceFile, 
                project,
                properties,
                newSource);
        return doc.transform("scroll");
    }


}
