package edu.odu.cs.cowem.documents.urls;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.odu.cs.cowem.documents.MarkdownDocument;
import edu.odu.cs.cowem.documents.WebsiteProject;

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

    private class DocumentSpecification {
    	public String documentName;
    	public String anchor;
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
	    	DocumentSpecification docSpec = parseDocumentSpecification(url);
	        String documentName = docSpec.documentName;
	        //logger.warn("Document spec is " + documentSpec);
	        if (documentName.contains("/")) {
                processRelativeSpecification(link, linkAttr, url, docSpec);
	        } else if (documentName.contains(":")) {
	        	// External site reference
	        	int k = documentName.indexOf(':');
	        	String siteName = documentName.substring(0, k);
	        	documentName = documentName.substring(k+1);
	        	String anchor = docSpec.anchor;
	        	if (anchor.startsWith("#")) {
	        		anchor = anchor.substring(1);
	        	}
	        	String siteURL = project.getExternalSite(siteName);
	        	if (siteURL != null) {
	        		if (!siteURL.endsWith("/")) {
	        			siteURL = siteURL + "/";
	        		}
	        		siteURL = siteURL + "index.html?doc=" + documentName
	        				+ "&anchor=" + anchor;
	        		link.setAttribute(linkAttr, siteURL);
	        		link.setAttribute("target", "_blank");
	        	} else {
	        		logger.warn(
                            "Could not find external site for URL shorthand: @"
                                    + linkAttr + "=" + url);
	        	}
            } else if (documentName.endsWith(".mmd")) {
                File targetFile = project.documentSource(documentName);
                File targetDocSet = project.documentSetLocation(documentName);
                if (targetFile != null) {
                    Path relative = project.relativePathToDocumentSet(
                            sourceDirectory, 
                            documentName);
                    relative = relative.resolve(documentName);
                    String newLink = relative.toString().replace("\\","/") 
                    		+ ".html" + docSpec.anchor;
                    link.setAttribute(linkAttr, newLink);
                    if (url.startsWith("doc:")) {
                        link.setAttribute("class", "doc");
                    }
                    attemptTBDReplacement(link, documentName);
                } else {
                    logger.warn(
                            "Could not find a replacement for URL shorthand: @"
                                    + linkAttr + "=" + url);
                }
            } else {
	            String documentSetName = documentName;
	            //logger.warn("Hunting for document set name " + documentSpec);
	            File targetFile = project.documentSetLocation(documentSetName);
                if (targetFile != null) {
                    Path relative = project.relativePathToDocumentSet(
                            sourceDirectory, 
                            documentSetName);
                    String newLink = relative.resolve("index.html").toString().replace("\\","/")
                            + docSpec.anchor;
                    link.setAttribute(linkAttr, newLink);
                    if (url.startsWith("doc:")) {
                        link.setAttribute("class", "doc");
                    }
                    attemptTBDReplacement(link, documentSetName);
                } else {
                    logger.warn(
                            "Could not find a replacement for URL shorthand: @"
                                    + linkAttr + "=" + url);
                }
	        }
	    }
	    return false;
	}


	private void processRelativeSpecification(final Element link, final String linkAttr, String url,
			DocumentSpecification docSpec) {
		String documentName = docSpec.documentName;
		int k = documentName.indexOf('/');
		String documentSetName = documentName.substring(0, k);
		String continuation = documentName.substring(0, k + 1);
		File targetFile = project.documentSetLocation(documentSetName);
		if (targetFile != null) {
		    Path relative = project.relativePathToDocumentSet(
		            sourceDirectory, 
		            documentSetName);
		    relative = relative.resolve(continuation);
		    String newLink = relative.toString().replace("\\","/") + ".html" 
		        + docSpec.anchor;
		    link.setAttribute(linkAttr, newLink);
		    if (url.startsWith("doc:")) {
		        link.setAttribute("class", "doc");
		    }
		    attemptTBDReplacement(link, continuation);
		} else {
		    logger.warn(
		            "Could not find a replacement for URL shorthand: @"
		                    + linkAttr + "=" + url);
		}
	}


	private DocumentSpecification parseDocumentSpecification(String documentSpecStr) {
		DocumentSpecification docSpec = new DocumentSpecification();
		int dividerPos = documentSpecStr.indexOf(':');
		docSpec.documentName = documentSpecStr.substring(dividerPos + 1);
		int anchorPos = docSpec.documentName.indexOf('#');
		docSpec.anchor = "";
		if (anchorPos >= 0) {
			docSpec.anchor = docSpec.documentName.substring(anchorPos);
			docSpec.documentName = docSpec.documentName.substring(0, anchorPos);
		}
		return docSpec;
	}
     	    
     	    
    /**
     * Checks to see if the entire text of an element is "TBD".
     * If so, attempts to replace that text by the Title metadata field
     * from the source document.
     * 
     * @param element link element to examine
     * @param documentName  name of document 
     */
    private void attemptTBDReplacement(final Element element, 
            final String documentName) {
    	String title = project.getDocumentTitle(documentName);
        String textContent = element.getTextContent().trim();
        if ("TBD".equals(textContent) && title != null && title.length() > 0) {
        	while (element.hasChildNodes()) {
        		element.removeChild(element.getFirstChild());
        	}
        	Node newTitle = element.getOwnerDocument()
        			.createTextNode(title);
        	element.appendChild(newTitle);
        }
    }
  	    
     	    

}
