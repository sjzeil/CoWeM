/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.odu.cs.cowem.documents.urls.URLRewriting;
import edu.odu.cs.cowem.macroproc.Macro;
import edu.odu.cs.cowem.macroproc.MacroProcessor;

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
	private static Logger logger = 
	        LoggerFactory.getLogger(MarkdownDocument.class);
	
	/**
     * The number of directory levels separating this document from the website
     * base.  This is used to enable the generation of relative URLs to styles,
     * graphics, and other document directories.
     */
    private int directoryDepth;
    
    /**
     * Context info for this document. Indicates the project's root and all
     * available document sets.
     */
    private WebsiteProject project;
    
	/**
	 * Source for text of the document. 
	 */
	private BufferedReader documentIn;
	
	/**
	 * Directory from which source code was being obtained.
	 */
	private File sourceDirectory;
	
	/**
	 * Text to be used instead of actually reading from asource file.
	 */
	private String forcedText;
	
	/**
	 * Metadata extracted from lines at the beginning of the document in
	 * the form: FieldName: value.
	 */
	private Properties metadata;
	
    /**
     * Properties to be used when processing this document.
     */
    private Properties properties;

    
    /**
	 * Last line of text read from documentIn.
	 */
	private String line = "";

	 
    private static final String BEGIN_SLIDE_EXCLUSION = "{{{";
    
    private static final String END_SLIDE_EXCLUSION = "}}}";
	
    private static final String BEGIN_SLIDE_INCLUSION = "[[[";
    
    private static final String END_SLIDE_INCLUSION = "]]]";

    /**
     * Code to pre-pend to PegDown output. 
     */
    private static final String HTML_HEADER = "<html>\n<head>\n"
            + "<title>@Title@</title>\n</head>\n<body>\n";
    
    /**
     * Code to append to PegDown output.
     */
    private static final String HTML_TRAILER = "</body>\n</html>\n";

    /**
     * If "yes", adds indentation to XML outputs for easier debugging. 
     */
    private String debugMode = "no";
	
	
    /**
     * Create a document from the given file.
     * @param input Markdown document text
     * @param project0 website project context,
     * @param properties0 Properties to be used in processing this document.
     */
	public MarkdownDocument(final File input, 
	        final WebsiteProject project0,
	        final Properties properties0) {
	    project = project0;
	    forcedText = null;
		try {
			documentIn = new BufferedReader (new FileReader (input));
		} catch (FileNotFoundException e) {
			logger.warn("Unable to read from " + input + ": " + e);
		}
		metadata = null;
		directoryDepth = 0;
		File dir = input.getParentFile();
		while ((dir != null) && !(new File(dir, "settings.gradle").exists())) {
		    ++directoryDepth;
		    dir = dir.getParentFile();
		}
		String baseName = input.getName();
		if (baseName.contains(".")) {
		    baseName = baseName.substring(0, baseName.lastIndexOf('.'));
		}
	    sourceDirectory = input.getParentFile();
		initProperties (properties0, baseName);
		properties.setProperty("docModDate", getModificationDate(input));
	}

	

    /**
     * Create a document from the given string.  "Pretend" that the input
     * string was actually read from a file.
     *  
     * @param fakeInputFile A path to a file that we will "pretend" is where
     *           the input string was obtained
     * @param project0 project website context
     * @param properties0 Properties to be used in processing this document.
     * @param input Markdown document text
     */
    public MarkdownDocument(
            final File fakeInputFile,
            final WebsiteProject project0,
            final Properties properties0, final String input
             ) {
        project = project0;
        forcedText = input;
        documentIn = new BufferedReader (new StringReader(forcedText));
        metadata = null;
        directoryDepth = 0;
        try {
            File dir = fakeInputFile.getParentFile();
            while ((dir != null) && (directoryDepth < 8) 
                    && !(Files.isSameFile(dir.toPath(), 
                            project.getRootDir().toPath()))) {
                ++directoryDepth;
                dir = dir.getParentFile();
            }
        } catch (IOException e) {
            directoryDepth = 2;
        }
        String baseName = fakeInputFile.getName();
        if (baseName.contains(".")) {
            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
        }
        sourceDirectory = fakeInputFile.getParentFile();
        initProperties (properties0, baseName);
    }
    


	
	
	
    /**
     * Copy a set of properties into the data member, augmenting
     * with internally computed properties: baseURL, directoryDepth,
     * and primaryDocument.
     * 
     * @param properties0 Properties to be used in processing this document.
     * @param primaryDocumentName base name of the primary document file,
     *                            empty if this is not a primary document.
     */
    private void initProperties(final Properties properties0, 
                                final String primaryDocumentName) {
        properties = new Properties();
        for (Object okey: properties0.keySet()) {
            String key = okey.toString();
            String value = properties0.getProperty(key);
            properties.put(key, value);
        }
        properties.put(PropertyNames.PRIMARY_DOCUMENT_PROPERTY,
                primaryDocumentName);
        properties.put(PropertyNames.DIRECTORY_DEPTH_PROPERTY, 
                Integer.toString(directoryDepth));
        StringBuffer base = new StringBuffer();
        for (int i = 0; i < directoryDepth; ++i) {
            base.append("../");
        }
        properties.put(PropertyNames.BASE_URL_PROPERTY,
                base.toString());
        properties.put(PropertyNames.DOCUMENT_SET_PATH_PROPERTY,
                sourceDirectory.getAbsolutePath());
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
		logger.info("pre-processing for " + format);
		String preprocessed = preprocess (format);
		logger.info("processing for " + format);
		org.w3c.dom.Document htmlDoc = process (preprocessed);
		logger.info("post-processing for " + format);
		String result = postprocess (htmlDoc, format);
		logger.info("completed " + format);
		try {
			documentIn.close();
		} catch (IOException e) {
			logger.warn("Error closing input", e);
		}
		return result;
	}

	/**
	 * Prepare for processing: Apply macros and extract metadata.
	 * @param format  HTML format to be applied
	 * @return  A Markdown document string ready for conversion to HTML.
	 */
	public final String preprocess(final String format) {
		final String defaultMacrosFile = "macros.md";
        
		MacroProcessor macroProc = new MacroProcessor("%");
		macroProc.defineMacro(new Macro("_" + format, "1"));
		
		final String xsltLocation  = "/edu/odu/cs/cowem/templates/";
		final InputStream macrosInStream = getClass().getResourceAsStream(
				xsltLocation + defaultMacrosFile);
		if (macrosInStream == null) {
		    logger.error("Unable to load " 
		       + xsltLocation + defaultMacrosFile + " from CoWeM library.");
		}
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
		if (metadata.containsKey("Macros")) {
			String macroFilesList = metadata.getProperty("Macros");
			String[] macroFiles = macroFilesList.split("\t");
			for (String macroFileName: macroFiles) {
				Path macroFile = Paths.get(macroFileName);
				if (!macroFile.isAbsolute()) {
				    macroFile = sourceDirectory.toPath().resolve(macroFileName);
				}
				if (macroFile.toFile().exists()) {
					macroProc.process(macroFile.toFile());
				} else {
					logger.warn("Could not find macro file " + macroFileName);
				}
			}
		}
		// Add any properties that begin with "_" as macros
		for (Object fieldNameObj: properties.keySet()) {
			String fieldName = (String) fieldNameObj;
			if (fieldName.startsWith("_")) {
				macroProc.defineMacro(
				    new Macro(fieldName, properties.getProperty(fieldName)));
			}
		}
		
		StringBuffer documentBody = new StringBuffer(line);
		documentBody.append('\n');
		try {
			while ((line = documentIn.readLine()) != null) {
			    if (line.contains(BEGIN_SLIDE_EXCLUSION)) {
			        line = line.replace(BEGIN_SLIDE_EXCLUSION, "%ifnot _slides");
			    }
                if (line.contains(END_SLIDE_EXCLUSION)) {
                    line = line.replace(END_SLIDE_EXCLUSION, "%endif");
                }
			    if (line.contains(BEGIN_SLIDE_INCLUSION)) {
			        line = line.replace(BEGIN_SLIDE_INCLUSION, "%if _slides");
			    }
                if (line.contains(END_SLIDE_INCLUSION)) {
                    line = line.replace(END_SLIDE_INCLUSION, "%endif");
                }
				documentBody.append(line);
				documentBody.append('\n');
			}
		} catch (IOException e) {
			logger.error("Unexpected problem reading document: " + e);
		}
		
		return macroProc.process(documentBody.toString());
	}

	/**
	 * Read through the opening lines of the document, extracting metadata from
	 * lines matching the pattern: Fieldname: value.
	 * 
	 */
	private void extractMetdataIfNecessary() {
		if (metadata == null) {
			metadata = new Properties();
			Pattern metadataLine = Pattern.compile("[^ :]+: .*");
			try {
			    while (true) {
			        line = documentIn.readLine();
			        if (line == null || !metadataLine.matcher(line).matches()) {
			            break;
			        }
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
							metadata.put(fieldName,
							    metadata.getProperty(fieldName) 
							    + "\t" + fieldValue);
						}
					} else {
						metadata.put(fieldName, fieldValue);
					}
				}
			} catch (IOException e) {
				logger.error(
				    "Unexpected problem reading metadata from document",  e);
			}
			
		}
	}

	/**
	 * A few metadata fields may be specified multiple times to build up a
	 * list of values.
	 * At the moment, these fields are: Macros, & CSS
	 * 
	 * @param fieldName a metadata field name
	 * @return true iff the field can be specified multiple times.
	 */
	private boolean fieldIsCumulative(final String fieldName) {
		return "Macros".equals(fieldName) || "CSS".equals(fieldName);
	}

	
	/**
	 * Convert Markdown text to an HTML structure.
	 *  
	 * @param markDownText document text in Markdown
	 * @return  DOM tree of generated HTML
	 */
	public final org.w3c.dom.Document process(final String markDownText) {
		int pdOptions = org.pegdown.Extensions.ALL;
		pdOptions -=  org.pegdown.Extensions.HARDWRAPS;
		pdOptions -=  org.pegdown.Extensions.TASKLISTITEMS;
		PegDownProcessor pdProc = new PegDownProcessor(pdOptions);
		String pdResults = pdProc.markdownToHtml(markDownText);
		String htmlText = HTML_HEADER + pdResults + HTML_TRAILER;
		htmlText = new CommonEntitySubstitutions().apply(htmlText);
		//logger.warn("pre-cwm clean:\n" + htmlText);
		htmlText = new CWMcleaner().apply(htmlText);
		htmlText = new ListingInjector(sourceDirectory).apply(htmlText);
		        
		org.w3c.dom.Document basicHtml = null;
		try {
			DocumentBuilder b 
			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			basicHtml = b.parse(new InputSource(new StringReader(htmlText)));
		} catch (ParserConfigurationException e) {
			logger.error ("Could not set up XML parser: " + e);
		} catch (SAXParseException e) {
			logger.error("Parsing error from Markdown processor: "
					+ e);
			if (e.toString().contains("lineNumber:")) {
				Pattern p = Pattern.compile(
						"lineNumber: (\\d+); columnNumber: (\\d+);");
				Matcher m  = p.matcher(e.toString());
				if (m.find()) {
					String lNum = m.group(1);
					int ln = Integer.parseInt(lNum);
					String cNum = m.group(2);
					int cn = Integer.parseInt(cNum);
					String context = Utils.extractContext(htmlText, ln-1, cn-1);
					logger.error("Generated output was:\n" + context);
				} else {
					logger.error("Text was:\n" + htmlText);					
				}
			} else {
				logger.error("Text was:\n" + htmlText);
			}
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
	 * @return transformed HTML string
	 */
	public final String postprocess(final org.w3c.dom.Document htmlDoc, 
	        final String format) {
		
		final String xsltLocation  = "/edu/odu/cs/cowem/templates/";
		final InputStream formatConversionSheet = 
		    MarkdownDocument.class.getResourceAsStream(
				xsltLocation + "md-" + format + ".xsl");

		String htmlText =
		        "<html><body>Document generation failed.</body></html>\n";
		if (formatConversionSheet == null) {
			logger.error("Unsupported output format: " + format);
			return htmlText;
		}


		extractMetdataIfNecessary();

		System.setProperty("javax.xml.transform.TransformerFactory", 
				"net.sf.saxon.TransformerFactoryImpl"); 
		TransformerFactory transFact = TransformerFactory.newInstance();
		transFact.setURIResolver((href, base) -> {
			//System.err.println("resolving URI to: " + xsltLocation + href);
		    final InputStream s = this.getClass()
		            .getResourceAsStream(xsltLocation + href);
		    return new StreamSource(s);
		});
		
		DocumentBuilder dBuilder = null;
		try {
			DocumentBuilderFactory dbFactory = 
			        DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error ("Problem creating new XML document ", e); 
			return htmlText;		
		}
		
		
		// Transform basic HTML into the selected format
		
		org.w3c.dom.Document formattedDoc = null;		
		try {
			Source xslSource = new StreamSource(formatConversionSheet);
			xslSource.setSystemId("http://www.cs.odu.edu/~zeil");
			formattedDoc = dBuilder.newDocument();
			Templates template = transFact.newTemplates(xslSource);
			Transformer xform = template.newTransformer();
			xform.setParameter("format", format);
			for (Object okey: properties.keySet()) {
				String key = okey.toString();
				xform.setParameter(key, properties.getProperty(key));
				//System.err.println("prop " + key + " => " 
				//                   + properties.getProperty(key));
			}
			for (Object okey: metadata.keySet()) {
				String key = okey.toString();
				xform.setParameter("" + key, metadata.getProperty(key));
				//System.err.println("prop " + "" + key + " => " 
				//                           + metadata.getProperty(key));
			}
			Source xmlIn = new DOMSource(htmlDoc.getDocumentElement());
			DOMResult htmlOut = new DOMResult(formattedDoc);
			xform.transform(xmlIn, htmlOut);
			logger.trace("format transformation completed");
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
			Transformer transformer = 
			        TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, debugMode); 
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
		//logger.warn("Sectioned text is\n" + htmlText);
		String result = performTextSubstitutions(htmlText);
		
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
	 *  bbassess:foo A link to a test/quiz/survey in a Blackboard course
	 *  
	 *  @param htmlDoc XML document to be rewritten
	 */
	private void transformURLs (final org.w3c.dom.Document htmlDoc) {
	    String baseURL = 
	            properties.getProperty(PropertyNames.BASE_URL_PROPERTY, "");
        String bbURL = 
                properties.getProperty(PropertyNames.BB_URL_PROPERTY, "");
	    new URLRewriting(sourceDirectory, project, bbURL).rewrite (htmlDoc);
	}

	/**
	 * Substitutes metadata, property, and special values 
	 * occurring in the HTML text. 
	 * 
	 * @param htmlText  text in which to perform the substitutions
	 * @return  htmlText with all substitutions performed.
	 */
	private String performTextSubstitutions(final String htmlText) {
	    String result = new PropertySubstitutions(metadata).apply(htmlText);
	    result = new PropertySubstitutions(properties).apply(result);
	    result = new SourceCodeSubstitutions().apply(result);
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
	public final Object getMetadata(final String fieldName) {
		extractMetdataIfNecessary();
		return metadata.getProperty(fieldName);
	}

	/**
	 * Turn indentation in generated XML on and off.
	 * Must be off in production to avoid distorting code listings.
	 *  
	 * @param mode true iff debugging
	 */
	public final void setDebugMode (final boolean mode) {
	    if (mode) {
	        debugMode = "yes";
	    } else {
	        debugMode = "no";
	    }
	}


	/**
	 * Estimate the date on which this document was last modified.
	 * 
	 * 1. If the file is within a Git repository and unchanged, use
	 *    the date of the last commit.
	 * 2. If the file is not within a Git repository or is changed,
	 *    use the modification date of the file.
	 * @param input file to be checked
	 * @return  Data of last modification
	 */
	public String getModificationDate(File input) {
	    File repoDir = findGitRepository(input);
	    Path existingFile = input.toPath().toAbsolutePath();
	    long lastUpdatedOn = 0; 
	    if (repoDir != null) {
	        try (
	                Repository repo = new FileRepositoryBuilder()
	                .setGitDir(repoDir)
	                .build()) {
	            try (Git git = new Git(repo)) {
	                Iterable<RevCommit> log = git.log().call();
	                for (RevCommit commit : log) {
	                    long commitTime = 1000L * commit.getCommitTime();
	                    if (commitTime > lastUpdatedOn) {
	                        if (commitContains(commit, existingFile, repo)) {
	                            lastUpdatedOn = commitTime;
	                        }
	                    }
	                }
	            }
	        } catch (Exception e) {
	            // Do nothing: fall through to use file modification date;
	        }
	    }
	    if (lastUpdatedOn <= 0) {
	        // Fall back to using the file modification date.
	        lastUpdatedOn = input.lastModified();
	    }
	    Calendar dateChanged = new GregorianCalendar();
        dateChanged.setTimeInMillis(lastUpdatedOn);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");  
        String result = formatter.format(dateChanged.getTime());
        logger.trace("mod date is " + result);
        return result;
	}

    private File findGitRepository(File input) {
        final String repoName = ".git";
        File thisDir = input.getParentFile();
        File repoDir = new File(thisDir, repoName);
        boolean found = false;
        while (!(found = repoDir.isDirectory())) {
            thisDir = thisDir.getParentFile();
            if (thisDir == null) {
                found = true;
                break;
            }
            repoDir = new File(thisDir, repoName);
        }
        if (found) {
            logger.trace("found repo at " + repoDir);
            return repoDir;
        } else {
            return null;
        }
    }



    private boolean commitContains(RevCommit commit, 
            Path existingFile, 
            Repository repo) 
    {
        if (commit.getParentCount() == 0) {
            // No parent. (First commit?)
            RevTree tree = commit.getTree(); 
            try (TreeWalk treeWalk = new TreeWalk(repo)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                while (treeWalk.next()) {
                    String pathStr = treeWalk.getPathString();
                    if (existingFile.toString().endsWith(pathStr)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                return false;
            } 
            return false;
        } else {
            try (RevWalk rw = new RevWalk(repo)) {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                try (DiffFormatter df = 
                        new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    df.setRepository(repo);
                    df.setDiffComparator(RawTextComparator.DEFAULT);
                    df.setDetectRenames(true);
                    List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                    for (DiffEntry diff : diffs) {
                        String pathStr = diff.getNewPath();
                        if (existingFile.toString().endsWith(pathStr)) {
                            //logger.warn("Match at " + new SimpleDateFormat("MMM d, yyyy").format(1000L*commit.getCommitTime()));
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        }
    }


}
