package edu.odu.cs.cwm.documents.urls;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implements URL rewriting in course documents. URL rewriting is
 * used to provide stable shorthands for various URLS that will
 * resolve properly even if document sets are moved, and to provide
 * special rewriting of fields such as dates that may need to be flagged
 * for special processing.  
 *  
 * @author zeil
 *
 */
public class URLRewriting {
    
    
    /**
     * List of handlers for various kinds of special link URLs.
     */
    private List<SpecialURL> rewriters;
    


    /**
     * Create a URL rewriter.
     * 
     * @param baseURL relative URL/directory to base of website.
     * @param bbURL  URL of a Blackboard course
     */
    public URLRewriting(final String baseURL, final String bbURL) {
        rewriters = new ArrayList<>();
        
        rewriters.add(new GraphicsURLs(baseURL));
        rewriters.add(new StylesURLs(baseURL));
        rewriters.add(new DocURLs(baseURL));
        rewriters.add(new BlackboardURLs(bbURL));
    }

    /**
     * Rewrite href attributes of "a" elements and src attributes of "img" 
     * and script elements that match the patterns listed above. Attributes 
     * not matching any such pattern are left unchanged.
     *  
     * @param htmlDoc the DOM of an HTML document.
     */
    public final void rewrite(final org.w3c.dom.Document htmlDoc) {
       NodeList allAElements = htmlDoc.getElementsByTagName("a");
       rewrite (allAElements, "href");
       NodeList allImgElements = htmlDoc.getElementsByTagName("img");
       rewrite (allImgElements, "src");
       NodeList allScriptElements = htmlDoc.getElementsByTagName("script");
       rewrite (allScriptElements, "src");
    }

    /**
     * Search a list of elements for link attributes written in one of
     * the special forms listed above, rewriting that attribute when
     * found.  "doc:"-style links also get a "class='doc'" attribute
     * added to facilitate later epub packaging.
     *   
     * @param elements  elements to be examined and, possibly, rewritten
     * @param linkAttribute name of the attribute containing a link URL
     */
    private void rewrite(final NodeList elements, final String linkAttribute) {
        for (int i = 0; i < elements.getLength(); ++i) {
            rewrite ((Element) elements.item(i), linkAttribute);
        }
    }

    /**
     * Examine an element for a link attribute written in one of
     * the special forms listed above, rewriting that attribute if
     * found.  "doc:"-style links also get a "class='doc'" attribute
     * added to facilitate later epub packaging.
     *   
     * @param element  element to be examined and, possibly, rewritten
     * @param linkAttribute name of the attribute containing a link URL
     */

    private void rewrite(final Element element, final String linkAttribute) {
        for (SpecialURL rewriter: rewriters) {
            boolean done = rewriter.applyTo(element, linkAttribute);
            if (done) {
                break;
            }
        }
    }


}
