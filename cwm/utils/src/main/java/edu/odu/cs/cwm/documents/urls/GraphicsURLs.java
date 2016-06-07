package edu.odu.cs.cwm.documents.urls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.odu.cs.cwm.documents.MarkdownDocument;
import edu.odu.cs.cwm.macroproc.MacroProcessor;

/**
 * Implements URL rewriting in course documents of
 *  graphics:foo, rewritten as baseURL/graphics/foo
 *  
 * @author zeil
 *
 */
public class GraphicsURLs implements SpecialURL {
    
    /**
     * Name used for Gradle files in document set directories.
     */
    private static final String GRADLE_FILE_NAME = "website.gradle";

    /**
     * relative URL/directory to base of website.
     */
    private String baseURL;
    
    
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(GraphicsURLs.class);


    /**
     * Create a URL rewriter.
     * 
     * @param baseURL0 relative URL/directory to base of website.
     */
    public GraphicsURLs(final String baseURL0) {
        baseURL = baseURL0;
    }



	/**
	 * Checks to see if a linking element (a or img) uses a special
	 * protocol label and, if so, attempts to rewrite the element.
	 * 
	 * @param link an a or img element
	 * @return true if the element has been rewritten.
	 */
	@Override
	public boolean applyTo(Element link) {
		String localName = link.getLocalName();
		if ("a".equals(localName) || "img".equals(localName)) {
			String linkAttr = ("a".equals(localName)) ? "href" : "src";
			String url = link.getAttribute(linkAttr);
     	    if (url.startsWith("graphics:")) {
     	    	int dividerPos = url.indexOf(':');
     	    	String documentSpec = url.substring(dividerPos + 1);
     	    	File baseDir = new File(baseURL.replace('/', File.separatorChar));
     	    	File graphicsDir = new File(baseDir, "graphics");
     	    	File selected = new File(graphicsDir, documentSpec); 
     	    	String newLink = selected.toString()
     	    			.replace(File.separatorChar, '/');
     	    	link.setAttribute(linkAttr, newLink);
     	    	return true;
     	    }
		}
		return false;
	}

}
