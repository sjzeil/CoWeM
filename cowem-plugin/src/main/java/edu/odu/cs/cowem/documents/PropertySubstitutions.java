/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.util.Properties;

/**
 * Provides substitution of @name@ tokens by named values
 * from a property set. 
 * 
 * @author zeil
 *
 */
public class PropertySubstitutions implements TextSubstitutions {
    
    /**
     * The properties to be substituted.
     */
    private Properties properties;

    
 
    /**
     * Create a substitution set.
     * 
     * @param properties0 the set of (name,value) pairs for
     *        substitution.
     */
    public PropertySubstitutions(final Properties properties0) {
        properties = properties0;
    }
    
    
    /**
     * Apply substitutions.
     * 
     * @param target  original string
     * @return original after applying substitutions.
     */
    @Override
    public final String apply(final String target) {
        StringBuilder buffer = new StringBuilder();
        int start = 0;
        while (start < target.length()) {
            int newStart = target.indexOf('@', start);
            if (newStart < 0) {
                buffer.append(target.substring(start));
                break;
            }
            int stop = target.indexOf('@', newStart + 1);
            if (stop < 0) {
                buffer.append(target.substring(start));
                break;
            }
            String possibleProperty = target.substring(newStart + 1, stop);
            Object value = properties.getProperty(possibleProperty);
            if (value != null) {
                buffer.append(target.substring(start, newStart));
                buffer.append(value.toString());
                start = stop + 1;
            } else {
                buffer.append(target.substring(start, newStart + 1));
                start = newStart + 1;
            }
        }
        
        return buffer.toString();
    }

}
