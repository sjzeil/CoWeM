/**
 * 
 */
package edu.odu.cs.cwm.documents.urls;

import org.w3c.dom.Element;

/**
 * A variety of special URL protocols are supported in a and img
 * elements, but rewritten during document processing.
 * 
 * @author zeil
 *
 */
public interface SpecialURL {
	
	/**
	 * Checks to see if a linking element (a or img) uses a special
	 * protocol label and, if so, attempts to rewrite the element.
	 * 
	 * @param link an a or img element
	 * @return true if the element has been rewritten.
	 */
    boolean applyTo (Element link);
}
