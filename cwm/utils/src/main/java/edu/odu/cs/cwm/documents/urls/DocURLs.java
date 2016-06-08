package edu.odu.cs.cwm.documents.urls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.odu.cs.cwm.documents.MarkdownDocument;

/**
 * Implements URL rewriting in course documents.
 * 
 *  doc:foo  where foo contains no '/' or '.' characters and matches a
 *           document set name in a group DIR
 *           baseURL/DIR/foo/index.html
 *  doc:foo  where foo contains one or more '/' or '.' characters and
 *           matches a file (presumably a secondary document or listing)
 *           in a group DIR,
 *           baseURL/DIR/foo.html
 *  docex:foo  same as doc:foo, except during epub package generation when such
 *           links are ignored when choosing files to include in the e-book
 *           (useful for excluding "private" documents such as assignments)
 *  
 * @author zeil
 *
 */
public class DocURLs implements SpecialURL {
    
 
    /**
     * Name used for Gradle files in document set directories.
     */
    private static final String GRADLE_FILE_NAME = "website.gradle";

    
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(DocURLs.class);
    
    /**
     * List of paths to document sets that can be reached from the baseURL. 
     */
    private static List<File> documentSets;


    /**
     * Create a URL rewriter.
     * 
     * @param baseURL relative URL/directory to base of website.
     */
    public DocURLs(final String baseURL) {

        if (documentSets == null) {
            // Look for all possible document sets - directories 2 levels below
            // the base that contain an appropriately named gradle file.
            documentSets = new ArrayList<>();
            File baseDir = new File(baseURL.replace('/', File.separatorChar));
            if (baseDir.canRead()) {
                try {
                    for (String groupName: baseDir.list()) {
                        File group = new File(baseDir, groupName);
                        if (!group.canRead() || !group.isDirectory()) {
                            continue;
                        }
                        try {
                            for (String docSetName: group.list()) {
                                File docSet = new File(group, docSetName);
                                if (!docSet.canRead() 
                                        || !docSet.isDirectory()) {
                                    continue;
                                }
                                File groupGradle = 
                                        new File(docSet, GRADLE_FILE_NAME);
                                if (groupGradle.exists()) {
                                    documentSets.add(docSet);
                                }
                            }
                        } catch (SecurityException ex) {
                            logger.warn("Could not list contents of " + group);
                        } 
                    }
                } catch (SecurityException ex) {
                    logger.warn("Could not list contents of " + baseDir);
                }
            } else {
                logger.warn("Could not find directory " + baseURL);
            }
        }
    }



	/**
	 * Checks to see if a linking element (a or img) uses a special
	 * protocol label and, if so, attempts to rewrite the element.
	 * 
     * @param link an element containing a URL
     * @param linkAttr name of the attribute containing the URL 
     * @return true if the element has been rewritten.
	 */
	@Override
	public final boolean applyTo(final Element link, final String linkAttr) {
	    String url = link.getAttribute(linkAttr);
	    if (url.startsWith("doc:") || url.startsWith("docex:")) {
	        int dividerPos = url.indexOf(':');
	        String documentSpec = url.substring(dividerPos + 1);
	        if (documentSpec.contains(".") || documentSpec.contains("/")) {
	            documentSpec = 
	                    documentSpec.replace('/', File.separatorChar);
	            List<File> candidates = new ArrayList<>();
	            for (File documentSet: documentSets) {
	                File document = new File(documentSet, documentSpec);
	                if (document.exists()) {
	                    candidates.add(document);
	                }
	            }
	            if (!candidates.isEmpty()) {
	                if (candidates.size() > 1) {
	                    logger.warn("Ambiguous URL shorthand: " + link
	                            + "\n  matched " + candidates.size() 
	                            + " documents. Using " + candidates.get(0));
	                }
	                File selected = candidates.get(0);
	                String newLink = selected.toString()
	                        .replace(File.separatorChar, '/')
	                        + ".html";
	                link.setAttribute(linkAttr, newLink);
	                if (url.startsWith("doc:")) {
	                    link.setAttribute("class", "doc");
	                }
	                attemptTBDReplacement(link, selected);
	            } else {
	                logger.warn(
	                        "Could not find a replacement for URL shorthand: "
	                                + link);
	            }
	        } else {
	            List<File> candidates = new ArrayList<>();
	            for (File documentSet: documentSets) {
	                String documentSetName = documentSet.getName();
	                if (documentSpec.equals(documentSetName)) {
	                    candidates.add(documentSet);
	                }
	            }
	            if (!candidates.isEmpty()) {
	                if (candidates.size() > 1) {
	                    logger.warn("Ambiguous URL shorthand: " + link
	                            + "\n  matched " + candidates.size() 
	                            + " documents. Using " + candidates.get(0));
	                }
	                File selected = candidates.get(0);
	                String newLink = selected.toString()
	                        .replace(File.separatorChar, '/')
	                        + "/index.html";
	                link.setAttribute(linkAttr, newLink);
	                if (url.startsWith("doc:")) {
	                    link.setAttribute("class", "doc");
	                }
	                File sourceFile = new File(selected, 
	                        selected.getName() + ".md");
	                attemptTBDReplacement(link, sourceFile);
	            } else {
	                logger.warn(
	                        "Could not find a replacement for URL shorthand: "
	                                + link);
	            }                 	    
	        }
	    }
	    return false;
	}
     	    
     	    
    /**
     * Checks to see if the entire text of an element is "TBD".
     * If so, attempts to replace that text by the Title metadata field
     * from the source document.
     * 
     * @param element link element to examine
     * @param sourceFile  Markdown source document 
     */
    private void attemptTBDReplacement(final Element element, 
            final File sourceFile) {
        String textContent = element.getTextContent().trim();
        if ("TBD".equals(textContent) && sourceFile.canRead()) {
            MarkdownDocument sourceDoc = new MarkdownDocument(sourceFile,
                    new Properties());
            String title = (String) sourceDoc.getMetadata("Title");
            if (title != null && title.length() > 0) {
                while (element.hasChildNodes()) {
                    element.removeChild(element.getFirstChild());
                }
                Node newTitle = element.getOwnerDocument()
                        .createTextNode(title);
                element.appendChild(newTitle);
            }
        }
    }
  	    
     	    

}
