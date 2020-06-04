/**
 * 
 */
package edu.odu.cs.cowem.documents;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Directory layout of a website project. Allows easy access
 * to groups, document sets, and the generation of relative
 * paths among them.
 *  
 * @author zeil
 *
 */
public class WebsiteProject implements Iterable<String> {
  
    
    /**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(WebsiteProject.class);
    

    /**
     * Name used for Gradle files in document set directories.
     */
    private static final String GRADLE_FILE_NAME = "build.gradle";


    /**
     * Root directory of the project.
     */
    private File rootDir;
    
    /**
     * Mapping from all known document set names to their
     * containing directories.
     */
    private Map<String, File> documentSets;
    
    /**
     * Mapping from all known document set names to their
     * source code file (.md or .mmd).
     */
    private Map<String, File> documentSources;

    /**
     * Mapping from all known document set names to their
     * titles.
     */
    private Map<String, String> documentTitles;

    /**
     * Create a project summary object.
     * @param rootDirectory location of this project's root
     */
    public WebsiteProject (final File rootDirectory) {
        rootDir = rootDirectory.getAbsoluteFile();
        documentSets = new TreeMap<String,File>();
        documentSources = new TreeMap<String,File>();
        documentTitles = new TreeMap<String,String>();
        
        for (File group: rootDir.listFiles()) {
            if (group.isDirectory()) {
                rememberDocumentSetsInThisGroup(group);
            }
        }
    }


	private void rememberDocumentSetsInThisGroup(File group) {
		for (File docSet: group.listFiles()) {
		    if (docSet.isDirectory() 
		            && new File(docSet, GRADLE_FILE_NAME).exists()) {
		        // This is a valid document set
		    	rememberDocumentSetComponents(docSet);
		    }
		}
	}


	private void rememberDocumentSetComponents(File docSet) {
		String docSetName = docSet.getName();
		warnIfDocSetNameIsDuplicated(docSetName);
		documentSets.put(docSetName, 
		        docSet.getAbsoluteFile());
		//logger.warn("Remember doc set " + docSet.getName() + " at " + docSet.getAbsoluteFile());
		File sourceFile = new File(docSet, docSetName + ".md");
		rememberThisDocument(docSetName, sourceFile);
		scanForSecondaryDocuments(docSet);
	}


	private void scanForSecondaryDocuments(File docSet) {
		for (File componentFile: docSet.listFiles()) {
			String fileName = componentFile.getName();
			if (fileName.endsWith(".mmd")) {
				// This is a secondary document.
				warnIfDocSetNameIsDuplicated(fileName);
				documentSets.put(fileName, docSet);
				rememberThisDocument(fileName, componentFile);
			}
		}
	}


	private void warnIfDocSetNameIsDuplicated(String docSetName) {
		if (documentSets.containsKey(docSetName)) {
		    logger.warn (
		       "Ambiguous structure: two or more document sets named "
		            + docSetName);
		}
	}


	private void rememberThisDocument(String docSetName, File sourceFile) {
		if (sourceFile.exists()) {
			documentSources.put(docSetName, sourceFile);
			String title = extractTitleFrom(sourceFile);
			if (title != null) {
				documentTitles.put(docSetName, title);
			}
				
		}
	}
    
    
    private String extractTitleFrom(File sourceFile) {
		try (BufferedReader in = new BufferedReader(new FileReader(sourceFile))) {
			String line = in.readLine();
			while (line != null) {
				if (line.toLowerCase().startsWith("title:")) {
					int k = 6;
					while (k < line.length() && line.charAt(k) == ' ') {
						++k;
					}
					String title = line.substring(k);
					return title;
				}
				line = in.readLine();
			}
		} catch (IOException e) {
			logger.warn ("Unexpected problem getting title from " + sourceFile, e);
		}
		return null;
	}


	/**
     * Determine the relative path from some file to the project root.
     * @param from a file that is a descendant of the project root.
     * @return the relative path from some file to the project root or null
     *            if no such relation can be determined.
     */
    public final Path relativePathToRoot (final File from) {
        File aFrom = from.getAbsoluteFile();
        if (!aFrom.isDirectory()) {
            aFrom = aFrom.getParentFile();
        }
        Path p = aFrom.toPath().relativize(rootDir.toPath());
        return p;
    }
    
    /**
     * Compute a relative path from a file to the directory containing
     * some document set.
     * @param from  a file that is descended from the project root.
     * @param documentSet any document set
     * @return the relative path, or null if no such path can be determined
     *    (the file is not descended from the project root or the document
     *      set does not exist). 
     */
    public final Path relativePathToDocumentSet (final File from, 
            final String documentSet) {
        File docSet = documentSetLocation(documentSet);
        if (docSet == null) {
            return null;
        }
        Path p1 = relativePathToRoot(from);
        if (p1 == null) {
            return null;
        }
        Path p2 = rootDir.toPath().relativize(docSet.toPath());
        Path p3 = p1.resolve(p2);
        return p3;
        
    }
    
    /**
     * The location of a document set.
     * @param documentSet The name of a document set.
     * @return the location or null if the document set does not exit
     */
    public final File documentSetLocation  (final String documentSet) {
        return documentSets.get(documentSet);
    }

    /**
     * The name of the group to which a document set belongs.
     * @param documentSet name of a document set
     * @return the group name or null if the document set does not exist.
     */
    public final String documentSetGroup (final String documentSet) {
        File loc =  documentSets.get(documentSet);
        if (loc == null) {
            return null;
        }
        return loc.getParentFile().getName();
    }

    /**
     * Iterate through all document sets.
     * @return iterator over the document set names.
     */
    public final Iterator<String> iterator() {
        return documentSets.keySet().iterator();
    }
    
    /**
     * Get the root directory.
     * @return the root directory.
     */
    public final File getRootDir() {
        return rootDir;
    }
    
    /**
     * Printable summary of project.
     * @return summary 
     */
    public final String toString() {
        return rootDir.toString() + ": " + documentSets.keySet().toString();
    }

    /**
     * Source file from which we obtain a named document.
     * @param documentName the identifier for the document (primary or secondary)
     * @return the .md or .mmd file containing the document source
     */
	public File documentSource(String documentName) {
        return documentSources.get(documentName);
	}


    /**
     * Title within a named document.
     * @param documentName the identifier for the document (primary or secondary)
     * @return the contents of the "Title:" metadata line or null if no such line
     */
	public String getDocumentTitle(String documentName) {
		return documentTitles.get(documentName);
	}
    
    
    
    
 
}
