/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.io.File;
import java.io.Reader;
import java.util.Properties;

/**
 * A Document written in Markdown that can be transformed to
 * HTML in 3 steps:
 * 1) pre-processing, 2) markdown to html conversion, and 3) post-processing.
 * 
 * @author zeil
 *
 */
public class MarkdownDocument implements Document {
	
	private Reader documentIn;
	private Properties metadata;
	
	public MarkdownDocument(String input) {
		// TODO Auto-generated constructor stub
	}
	
	public MarkdownDocument(File input) {
		// TODO Auto-generated constructor stub
	}
	
	public MarkdownDocument(Reader input) {
		// TODO Auto-generated constructor stub
	}
	

	/* (non-Javadoc)
	 * @see edu.odu.cs.cwm.documents.Document#transform(java.lang.String, java.util.Properties)
	 */
	@Override
	public String transform(String format, Properties properties) {
		String preprocessed = preprocess (format, properties);
		org.w3c.dom.Document htmlDoc = process (preprocessed);
		String result = postprocess (htmlDoc, format, properties);
		return result;
	}

	/**
	 * Prepare for processing: Apply macros and extract metadata.
	 * @param format  HTML format to be applied
	 * @param properties Properties to be defined to the macro processor.
	 * @return  A Markdown document string ready for conversion to HTML.
	 */
	private String preprocess(String format, Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Convert Markdown text to an HTML structure.
	 *  
	 * @param markDownText
	 * @return  DOM tree of generated HTML
	 */
	private org.w3c.dom.Document process(String markDownText) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Transform HTML to desired output format and apply late substitutions
	 * of properties and other special strings.
	 *   
	 * @param htmlDoc  the basic XML/HTML structure for the document.
	 * @param format   the desired format - used to select a stylesheet for
	 *                    the basic transformations.
	 * @param properties a collection of key,value pairs for late substitution.
	 * @return transformed HTML string
	 */
	private String postprocess(org.w3c.dom.Document htmlDoc, String format, 
			Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
