package edu.odu.cs.cwm.documents.urls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Implements URL rewriting in course documents.
 * 
 *  bb:foo   A link to an internal page of a Blackboard course (requires
 *           a legitimate URL for bbURL in the constructor below)
 *  
 * @author zeil
 *
 */
public class BlackboardURLs implements SpecialURL {
    
 
    /**
     * URL to course area on Blackboard.
     */
    private String bbURL;
    
    
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(BlackboardURLs.class);


    /**
     * Create a URL rewriter.
     * 
     * @param bbURL0 url to course on Blackboard
     */
    public BlackboardURLs(final String bbURL0) {
        bbURL = bbURL0;
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
	    if (url.startsWith("bb:")) {
	        if ("".equals(bbURL)) {
	            logger.warn("Could not resolve URL shorthand " + link
	                    + "\n  because base Blackboard URL has not been"
	                    + " specified as part of website properties.");
	            return false;
	        } else {
	            int dividerPos = url.indexOf(':');
	            String documentSpec = url.substring(dividerPos + 1);
	            String urlSpec = "url=";
	            String urlStart = 
	                    bbURL.substring(0, bbURL.indexOf(urlSpec)) 
	                    + urlSpec;
	            String webAppsSpec = "/webapps";
	            int k = documentSpec.indexOf(webAppsSpec);
	            if (k < 0) {
	                logger.warn ("Could not resolve URL shorthand " + link);
	                return false;
	            }
	            documentSpec = documentSpec.substring(k);
	            documentSpec = documentSpec.replaceAll("/", "%2f");
	            documentSpec = documentSpec.replaceAll("=", "%36");
	            documentSpec = documentSpec.replaceAll("&", "%26");
	            documentSpec = documentSpec.replaceAll("[?]", "%3f");
	            String newLink = urlStart + documentSpec;
	            link.setAttribute(linkAttr, newLink);
	            return true;
	        }
	    }
	    return false;
	}
}
