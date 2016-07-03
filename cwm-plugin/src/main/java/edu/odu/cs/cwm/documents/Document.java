package edu.odu.cs.cwm.documents;

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
	 * @return String containing HTML of page for web site.
	 */
	String transform (String format); 

}
