/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.util.Properties;

/**
 * A document that can be transformed into a variety of HTML
 * formats.
 * 
 * @author zeil
 *
 */
public interface Document {
	
	/**
	 * Transform this document into a string suitable for writing into
	 * an HTML file.
	 *  
	 * @param format   Specifies a style/format for the generated HTML. 
	 * @param properties A collection of property values that will
	 *         be substituted for any text of the form @propertyName@.
	 *         These are used both to customize the files (e.g., inserting
	 *         the instructor's email address) and to access external
	 *         support URLs (e.g., the URL used to access MathJax)   
	 * @return
	 */
	public String transform (String format, Properties properties); 

}
