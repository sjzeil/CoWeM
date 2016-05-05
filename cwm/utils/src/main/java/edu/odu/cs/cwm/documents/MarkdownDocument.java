/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import edu.odu.cs.cwm.macroproc.Macro;
import edu.odu.cs.cwm.macroproc.MacroProcessor;

import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Document written in Markdown that can be transformed to
 * HTML in 3 steps:
 * 1) pre-processing, 2) markdown to html conversion, and 3) post-processing.
 * 
 * @author zeil
 *
 */
public class MarkdownDocument implements Document {
	
	/**
	 * For logging error messages.
	 */
	private static Logger logger = LoggerFactory.getLogger(MarkdownDocument.class);
	
	/**
	 * Source for text of the document. 
	 */
	private BufferedReader documentIn;
	
	/**
	 * Metadata extracted from lines at the beginning of the document in the form 
	 *    FieldName: value
	 */
	private Properties metadata;
	
	/**
	 * Last line of text read from documentIn.
	 */
	private String line = "";
	
	public MarkdownDocument(String input) {
		documentIn = new BufferedReader (new StringReader (input));
		metadata = null;
	}
	
	public MarkdownDocument(File input) {
		try {
			documentIn = new BufferedReader (new FileReader (input));
		} catch (FileNotFoundException e) {
			logger.warn("Unable to read from " + input + ": " + e);
		}
		metadata = null;
	}
	
	public MarkdownDocument(Reader input) {
		documentIn = new BufferedReader(input);
		metadata = null;
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
		MacroProcessor macroProc = new MacroProcessor("%");
		macroProc.defineMacro(new Macro("_" + format, "1"));
		Path defaultMacros = (Path)(properties.get("_defaultMacros"));
		if (defaultMacros != null) {
			macroProc.process(defaultMacros.toFile());
		}
		
		extractMetdataIfNecessary();
		
		// Add any files listed in Macro: lines to the macro processor.
		if (metadata.containsKey("Macro")) {
			String macroFilesList = metadata.getProperty("Macro");
			String[] macroFiles = macroFilesList.split("\t");
			for (String macroFileName: macroFiles) {
				File macroFile = new File(macroFileName);
				if (macroFile.exists()) {
					macroProc.process(macroFile);
				} else {
					logger.warn("Could not find macro file " + macroFileName);
				}
			}
		}
		// Add any properties that begin with "_" as macros
		for (Object fieldNameObj: properties.keySet()) {
			String fieldName = (String)fieldNameObj;
			if (fieldName.startsWith("_")) {
				macroProc.defineMacro(new Macro(fieldName, properties.getProperty(fieldName)));
			}
		}
		
		StringBuffer documentBody = new StringBuffer(line + "\n");
		try {
			while ((line = documentIn.readLine()) != null) {
				documentBody.append(line);
				documentBody.append("\n");
			}
		} catch (IOException e) {
			logger.error("Unexpected problem reading document: " + e);
		}
		
		String result = macroProc.process(documentBody.toString());
		return result;
	}

	/**
	 * Read through the opening lines of the document, extracting metadata from
	 * lines matching the pattern:
	 *    Fieldname: value
	 * 
	 */
	private void extractMetdataIfNecessary() {
		if (metadata == null) {
			metadata = new Properties();
			Pattern metadataLine = Pattern.compile("[^ :]+: .*");
			try {
				while ((line = documentIn.readLine()) != null && metadataLine.matcher(line).matches()) {
					int pos = line.indexOf(':');
					String fieldName = line.substring(0, pos);
					++pos;
					while (pos < line.length() && line.charAt(pos) == ' ') {
						++pos;
					}
					String fieldValue = line.substring(pos);
					if (fieldIsCumulative(fieldName)) {
						if (!metadata.containsKey(fieldName)) {
							metadata.put(fieldName, fieldValue);
						} else {
							metadata.put(fieldName, metadata.getProperty(fieldName) + "\t" + fieldValue);
						}
					} else {
						metadata.put(fieldName, fieldValue);
					}
				}
			} catch (IOException e) {
				logger.error("Unexpected problem reading metadata from document: " + e);
			}
			
		}
	}

	/**
	 * A few metadata fields may be specified multiple times to build up a list of values.
	 * At the moment, these fields are: Macros, & CSS
	 * 
	 * @param fieldName a metadata field name
	 * @return true iff the field can be specified multiple times.
	 */
	private boolean fieldIsCumulative(String fieldName) {
		return fieldName.equals("Macros") || fieldName.equals("CSS");
	}

	
	private static final String HTMLheader = "<html>\n<head>\n"
			+ "<title>@Title@</title>\n</head>\n<body>\n";
	private static final String HTMLtrailer = "</body>\n</html>\n";
	
	/**
	 * Convert Markdown text to an HTML structure.
	 *  
	 * @param markDownText
	 * @return  DOM tree of generated HTML
	 */
	public org.w3c.dom.Document process(String markDownText) {
		int pdOptions = org.pegdown.Extensions.ALL;
		PegDownProcessor pdProc = new PegDownProcessor(pdOptions);
		String pdResults = pdProc.markdownToHtml(markDownText);
		String htmlText = HTMLheader + pdResults + HTMLtrailer;
		
		org.w3c.dom.Document basicHtml = null;
		try {
			DocumentBuilder b 
			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			basicHtml = b.parse(new InputSource(new StringReader(htmlText)));
		} catch (ParserConfigurationException e) {
			logger.error ("Could not set up XML parser: " + e);
		} catch (SAXException e) {
			logger.error("Unable to parse output from Markdown processor: " + e);
		} catch (IOException e) {
			logger.error("Unable to parse output from Markdown processor: " + e);
		}
		return basicHtml;
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
		extractMetdataIfNecessary();
		return metadata.getProperty(fieldName);
	}

	
	

}
