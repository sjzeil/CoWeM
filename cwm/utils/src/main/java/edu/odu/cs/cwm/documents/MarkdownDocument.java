/**
 * 
 */
package edu.odu.cs.cwm.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.odu.cs.cwm.macroproc.Macro;
import edu.odu.cs.cwm.macroproc.MacroProcessor;

import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
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
		
	private final static String[] commonEntities = {
			"cent", 	"#162",	 
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
			"ge", "#8805"
	};

	
	private final static String[] specialSubstitutionValues = {
			"/*...*/", "&#x22ee;",
			"/*1*/", "&#x2780;",
			"/*2*/", "&#x2781;",
			"/*3*/", "&#x2782;",
			"/*4*/", "&#x2783;",
			"/*5*/", "&#x2784;",
			"/*6*/", "&#x2785;",
			"/*7*/", "&#x2786;",
			"/*8*/", "&#x2787;",
			"/*9*/", "&#x2788;",
			"/*+*/", "<span class='hli'>",
			"/*-*/", "</span>",
			"/*+1*/", "<span class='hli'>",
			"/*-1*/", "</span>",
			"/*+2*/", "<span class='hlii'>",
			"/*-2*/", "</span>",
			"/*+3*/", "<span class='hliii'>",
			"/*-3*/", "</span>",
			"/*+4*/", "<span class='hliv'>",
			"/*-4*/", "</span>",
			"/*+i*/", "<i>",
			"/*-i*/", "</i>",
			"/*+=*/", "<span class='strike'>",
			"/*-=*/", "</span>",
			"\\%", "%",
			"[_", "<span class='userinput'>",
			"_]", "</span>"
	};
	
	private final static TreeMap<String, String> specialSubstitutions;
	static {
		specialSubstitutions = new TreeMap<>();
		for (int i = 0; i < specialSubstitutionValues.length; i += 2)
		specialSubstitutions.put(specialSubstitutionValues[i],
				specialSubstitutionValues[i+1]);
	}
	
	
	
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
		final String DefaultMacrosFile = "macros.md";
		
		MacroProcessor macroProc = new MacroProcessor("%");
		macroProc.defineMacro(new Macro("_" + format, "1"));
		
		final String xsltLocation  = "/edu/odu/cs/cwm/templates/";
		final InputStream macrosInStream = getClass().getResourceAsStream(
				xsltLocation + DefaultMacrosFile);
		try {
			BufferedReader macrosIn = new BufferedReader(
					new InputStreamReader(macrosInStream, "UTF-8"));
			macroProc.process(macrosIn);
		} catch (UnsupportedEncodingException e) {
			logger.error("Unexpected error loading default macros.", e);
		} catch (IOException e) {
			logger.error("Unexpected error reading default macros.", e);
		}
		
		extractMetdataIfNecessary();
		
		// Add any files listed in Macro: lines to the macro processor.
		if (metadata.containsKey("meta_Macros")) {
			String macroFilesList = metadata.getProperty("meta_Macros");
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
		pdOptions -=  org.pegdown.Extensions.HARDWRAPS;
		PegDownProcessor pdProc = new PegDownProcessor(pdOptions);
		String pdResults = pdProc.markdownToHtml(markDownText);
		String htmlText = HTMLheader + pdResults + HTMLtrailer;
		htmlText = performEntitySubstitutions(htmlText);
		
		org.w3c.dom.Document basicHtml = null;
		try {
			DocumentBuilder b 
			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			basicHtml = b.parse(new InputSource(new StringReader(htmlText)));
		} catch (ParserConfigurationException e) {
			logger.error ("Could not set up XML parser: " + e);
		} catch (SAXException e) {
			logger.error("Unable to parse output from Markdown processor: ", e);
			logger.error("Text was:\n" + htmlText);
		} catch (IOException e) {
			logger.error("Unable to parse output from Markdown processor: ", e);
			logger.error("Text was:\n" + htmlText);
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
		
		final String xsltLocation  = "/edu/odu/cs/cwm/templates/";
		final InputStream formatConversionSheet = MarkdownDocument.class.getResourceAsStream(
				xsltLocation + "md-" + format + ".xsl");

		String htmlText = "<html><body>Document generation failed.</body></html>\n";
		if (formatConversionSheet == null) {
			logger.error("Unsupported output format: " + format);
			return htmlText;
		}


		extractMetdataIfNecessary();

		System.setProperty("javax.xml.transform.TransformerFactory", 
				"net.sf.saxon.TransformerFactoryImpl"); 
		TransformerFactory transFact = TransformerFactory.newInstance();
		transFact.setURIResolver((href, base) -> {
			System.err.println("resolving URI to: " + xsltLocation + href);
		    final InputStream s = this.getClass().getResourceAsStream(xsltLocation + href);
		    return new StreamSource(s);
		});
		
		DocumentBuilder dBuilder = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error ("Problem creating new XML document ", e); 
			return htmlText;		
		}
		
		
		// Transform basic HTML into the selected format
		
		org.w3c.dom.Document formattedDoc = null;		
		try {
			Source xslSource = new StreamSource(formatConversionSheet);
			formattedDoc = dBuilder.newDocument();
			Templates template = transFact.newTemplates(xslSource);
			Transformer xform = template.newTransformer();
			xform.setParameter("format", format);
			for (Object okey: properties.keySet()) {
				String key = okey.toString();
				xform.setParameter(key, properties.getProperty(key));
				//System.err.println("prop " + key + " => " + properties.getProperty(key));
			}
			for (Object okey: metadata.keySet()) {
				String key = okey.toString();
				xform.setParameter("meta_" + key, metadata.getProperty(key));
				//System.err.println("prop " + "meta_" + key + " => " + metadata.getProperty(key));
			}
			Source xmlIn = new DOMSource(htmlDoc.getDocumentElement());
			DOMResult htmlOut = new DOMResult(formattedDoc);
			xform.transform(xmlIn, htmlOut);
			logger.info("format transformation completed");
		} catch (TransformerConfigurationException e) {
			logger.error ("Problem parsing XSLT2 stylesheet " 
					+ formatConversionSheet, e);
			return htmlText;
		} catch (TransformerException e) {
			logger.error ("Problem applying stylesheet " 
					+ formatConversionSheet, e);
			return htmlText;			
		}

		// URL transformation
		transformURLs (formattedDoc);
		
		// Generate result text
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			Source source = new DOMSource(formattedDoc.getDocumentElement());
			StringWriter htmlString = new StringWriter();
			StreamResult htmlOut = new StreamResult(htmlString);
			transformer.transform(source, htmlOut);
			htmlText = htmlString.toString();
		} catch (TransformerConfigurationException e) {
			logger.error ("Problem creating empty stylesheet " 
					+ ": " + e);
		} catch (TransformerException e) {
			logger.error ("Problem serializing formatted document " 
					+ e);
		}
		
		// Apply property and other final substitutions.
		String result = performTextSubstitutions(properties, htmlText);
		
		return result;
	}
	
	
	/**
	 * Replace URL shortcuts by a legal URL form.  Shortcuts recognized are:
	 * 
	 *  targetDoc:foo  ../../Public/foo/index.html    (deprecated)
     *  public::foo  ../../Public/foo/index.html
	 *  protected:foo  ../../Protected/foo/index.html
	 *  asst:foo  ../../Protected/Assts/foo.mmd.html
	 *  
	 *  Possibly in the future:
	 *  bblink:foo   A link to an internal page of a Blackboard course
	 *  bbassess:foo A link to a test/quiz/survey in a Blacokbaord course
	 */
	private void transformURLs (org.w3c.dom.Document htmlDoc) {
		
	}

	/**
	 * Substitutes metadata, property, and special values (see specialSubstitutions, above)
	 * occurring in the HTML text. 
	 * 
	 * @param properties  property values for the course/document
	 * @param htmlText  text in which to perform the substitutions
	 * @return  htmlText with all substitutions performed.
	 */
	private String performTextSubstitutions(Properties properties, String htmlText) {
		StringBuilder buffer = new StringBuilder();
		int start = 0;
		while (start < htmlText.length()) {
			int newStart = htmlText.indexOf('@', start);
			if (newStart < 0) {
				buffer.append(htmlText.substring(start));
				break;
			}
			int stop = htmlText.indexOf('@', newStart+1);
			if (stop < 0) {
				buffer.append(htmlText.substring(start));
				break;
			}
			String possibleProperty = htmlText.substring(newStart+1, stop);
			Object value = metadata.getProperty(possibleProperty);
			if (value == null) {
				value = properties.getProperty(possibleProperty);
			}
			if (value != null) {
				buffer.append(htmlText.substring(start, newStart));
				buffer.append(value.toString());
				start = stop + 1;
			} else {
				buffer.append(htmlText.substring(start, newStart+1));
				start = newStart+1;
			}
		}
		
		String result = buffer.toString();
		for (String target: specialSubstitutions.keySet()) {
			String value = specialSubstitutions.get(target);
			result = result.replace(target, value);
		}
		return result;
	}

	
	/**
	 * Substitutes common symbolic entities by numeric codes 
	 * to allow PegDown output to be loaded as XML.
	 * 
	 * @param htmlText  text in which to perform the substitutions
	 * @return  htmlText with all substitutions performed.
	 */
	private String performEntitySubstitutions(String htmlText) {
		String result = htmlText;
		for (int i = 0; i < commonEntities.length; i +=2) {
			String target = "&" + commonEntities[i] + ";";
			String value = "&" + commonEntities[i+1] + ";";
			result = result.replace(target, value);
		}
		return result;
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
