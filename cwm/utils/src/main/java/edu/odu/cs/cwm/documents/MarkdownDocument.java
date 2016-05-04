/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
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
	public final String transform(String format, Properties properties) {
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
	public String preprocess(String format, Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Convert Markdown text to an HTML structure.
	 *  
	 * @param markDownText
	 * @return  DOM tree of generated HTML
	 */
	public org.w3c.dom.Document process(String markDownText) {
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
	public String postprocess(org.w3c.dom.Document htmlDoc, String format, 
			Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Extracts a desired metadata field from the document. Metadata fields are
	 * found at the start of a document in the form
	 *    FieldName: value of field
	 * 
	 * Metadata fields become part of the property set that is
	 * replaced in the document text as part of the post-processing,
	 * in which "@FieldName@" is replaced by the value of
	 * that property.  Some metadata field names are inserted as
	 * part of early processing. These in include Title, Author, CSS,
	 * Date, Copyright, and JaxenURL.
	 *    
	 * @param fieldName 
	 * @return value of that metadata field extracted from the beginning of the
	 *               document. 
	 */
	public Object getMetadata(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}
