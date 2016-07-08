/**
 * 
 */
package edu.odu.cs.cowem.documents;

/**
 * Names of important course and document properties.
 * 
 * @author zeil
 *
 */
public final class PropertyNames {
	
 	/**
 	 * Name of the property holding the relative URL from a document to
 	 * the website base directory.
 	 */
    public static final String BASE_URL_PROPERTY = "baseURL";

    /**
     * Name of the property holding the URL of a Blackboard course site.
     */
    public static final String BB_URL_PROPERTY = "bbURL";

    
    /**
     * Name of the property holding the number of directory levels below
     * the website base at which this document will reside.
     */
    public static final String DIRECTORY_DEPTH_PROPERTY = "directoryDepth";

    /**
     * Name of the property holding the base name of a primary document.
     * Property value is "" if the document being processed is not a primary.
     */
    public static final String PRIMARY_DOCUMENT_PROPERTY = "primaryDocument";

    /**
     * Absolute path to directory containing the primary document.
     */
    public static final Object DOCUMENT_SET_PATH_PROPERTY = "documentSetPath";

    /**
     * This class provides only static members. Instances cannot be created.
     */
    private PropertyNames () {
        
    }

}
