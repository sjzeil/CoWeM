/**
 * 
 */
package edu.odu.cs.cowem.documents;

/**
 * Applies a set of text substitutions to a string.
 * 
 * @author zeil
 *
 */
public interface TextSubstitutions {
    
    /**
     * Apply substitutions.
     * 
     * @param toString  original string
     * @return original after applying substitutions.
     */
    String apply(String toString);

}
