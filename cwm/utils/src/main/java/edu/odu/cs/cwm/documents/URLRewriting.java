package edu.odu.cs.cwm.documents;

/**
 * Implements URL rewriting in course documents. Special forms of URL are:
 * 
 *  foo:bar  baseURL/foo/bar/index.html  (if directory baseURL/foo exists)
 *  asst:foo  ../../Protected/Assts/foo.mmd.html
 *  bblink:foo   A link to an internal page of a Blackboard course (requires
 *                 a legitimate URL for bbURL in the constructor below)
 *  targetDoc:foo  baseURL/Public/foo/index.html    (deprecated)
 * 
 * @author zeil
 *
 */
public class URLRewriting {

    /**
     * Create a URL rewriter.
     * 
     * @param baseURL relative URL/directory to base of website.
     * @param bbURL  URL of a Blackboard course
     */
    public URLRewriting(final String baseURL, final String bbURL) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Rewrite href attributes of "a" elements and src attributes of "img"
     * elements that match the patterns listed above. Attributes not matching
     * any such pattern are left unchanged.
     *  
     * @param htmlDoc the DOM of an HTML document.
     */
    public final void rewrite(final org.w3c.dom.Document htmlDoc) {
        // TODO
    }

}
