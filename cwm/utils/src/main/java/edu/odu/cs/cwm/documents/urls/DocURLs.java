package edu.odu.cs.cwm.documents.urls;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.odu.cs.cwm.documents.MarkdownDocument;
import edu.odu.cs.cwm.documents.WebsiteProject;

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
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(DocURLs.class);
    

    /**
     * Context in which to determine relative URLs/paths.
     */
    private WebsiteProject project;
    
    /**
     * directory containing the document source.
     */
    private File sourceDirectory;

    /**
     * Create a URL rewriter.
     * 
     * @param sourceDirectory0 directory containing the document source
     * @param project0 context info on document set locations 
     */
    public DocURLs(final File sourceDirectory0, final WebsiteProject project0) {
        sourceDirectory = sourceDirectory0;
        project = project0;
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
	        logger.warn("documentSpec=" + documentSpec);
            if (documentSpec.contains("/")) {
                int k = documentSpec.indexOf('/');
	            String documentSetName = documentSpec.substring(0, k);
	            String continuation = documentSpec.substring(0, k + 1);
	            File targetFile = project.documentSetLocation(documentSetName);
	            if (targetFile != null) {
	                Path relative = project.relativePathToDocumentSet(
	                        sourceDirectory, 
	                        documentSetName);
	                relative = relative.resolve(continuation);
	                String newLink = relative.toString() + ".html";
	                link.setAttribute(linkAttr, newLink);
	                if (url.startsWith("doc:")) {
	                    link.setAttribute("class", "doc");
	                }
	                attemptTBDReplacement(link, new File(targetFile, continuation));
	            } else {
	                logger.warn(
	                        "Could not find a replacement for URL shorthand: @"
	                                + linkAttr + "=" + url);
	            }
            } else if (documentSpec.contains(".")) {
                File targetFile = null;
                String targetDocSet = null;
                for (String documentSetName: project) {
                    File candidateDir = project.documentSetLocation(documentSetName);
                    File candidateFile = new File(candidateDir, documentSpec);
                    if (candidateFile.exists()) {
                        if (targetFile == null) {
                            logger.warn("Ambiguous URL shortcut: " + documentSpec);
                        }
                        targetFile = candidateFile;
                        targetDocSet = documentSetName;
                    }
                }
                if (targetFile != null) {
                    Path relative = project.relativePathToDocumentSet(
                            sourceDirectory, 
                            targetDocSet);
                    relative = relative.resolve(documentSpec);
                    String newLink = relative.toString() + ".html";
                    link.setAttribute(linkAttr, newLink);
                    if (url.startsWith("doc:")) {
                        link.setAttribute("class", "doc");
                    }
                    attemptTBDReplacement(link, targetFile);
                } else {
                    logger.warn(
                            "Could not find a replacement for URL shorthand: @"
                                    + linkAttr + "=" + url);
                }
            } else {
	            String documentSetName = documentSpec;
	            File targetFile = project.documentSetLocation(documentSetName);
                if (targetFile != null) {
                    Path relative = project.relativePathToDocumentSet(
                            sourceDirectory, 
                            documentSetName);
                    String newLink = relative.resolve("index.html").toString();
                    link.setAttribute(linkAttr, newLink);
                    if (url.startsWith("doc:")) {
                        link.setAttribute("class", "doc");
                    }
                    File sourceFile = new File(targetFile, 
                            targetFile.getName() + ".md");
                    attemptTBDReplacement(link, sourceFile);
                } else {
                    logger.warn(
                            "Could not find a replacement for URL shorthand: @"
                                    + linkAttr + "=" + url);
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
                    project, new Properties());
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
