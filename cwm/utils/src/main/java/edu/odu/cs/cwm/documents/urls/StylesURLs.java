package edu.odu.cs.cwm.documents.urls;

import java.io.File;

import org.w3c.dom.Element;

/**
 * Implements URL rewriting in course documents.
 * 
 *  styles:foo, rewritten as baseURL/styles/foo
 *  
 * @author zeil
 *
 */
public class StylesURLs implements SpecialURL {
    
 
    /**
     * relative URL/directory to base of website.
     */
    private String baseURL;
    
    


    /**
     * Create a URL rewriter.
     * 
     * @param baseURL0 relative URL/directory to base of website.
     */
    public StylesURLs(final String baseURL0) {
        baseURL = baseURL0;
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
	    if (url.startsWith("styles:")) {
	        int dividerPos = url.indexOf(':');
	        String documentSpec = url.substring(dividerPos + 1);
	        File baseDir = new File(
	                baseURL.replace('/', File.separatorChar));
	        File graphicsDir = new File(baseDir, "styles");
	        File selected = new File(graphicsDir, documentSpec); 
	        String newLink = selected.toString()
	                .replace(File.separatorChar, '/');
	        link.setAttribute(linkAttr, newLink);
	        return true;
	    }
		return false;
	}

}
