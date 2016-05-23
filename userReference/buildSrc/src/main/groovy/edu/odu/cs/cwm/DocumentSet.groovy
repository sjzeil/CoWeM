package edu.odu.cs.cwm

import org.gradle.api.file.FileCollection
import org.gradle.api.Project


/**
 * Describes a collection of documents that will appear in a single directory of the website.
 * Each document set has a primary document and zero or more secondary documents.
 * The primary document may be rendered in multiple formats, one of which becomes the
 * index.html format for the directory. All secondary documents are
 * rendered in a single format (the basic "html" format). 
 */
class DocumentSet {
	
	/**
	 * Name of the primary document. 
	 * Defaults to the directory name + ".md". 
	 */
	File primaryDocument;
	
	/**
	 * Sets the name of the primary document.
	 */
	void primary (String name) {
		primaryDocument = _inProject.file(name)
	}
	
	
	/**
	 * Name of the format that will be selected for the
	 * 'index.html' of the website directory.
	 */
	String indexFormat = "";
	
	/**
	 * Sets the name of the index format.
	 */
	void index (String name) {
		indexFormat = name
	}

	/**
	 * List of formats in which the the primary document will
	 * be generated. Possibilities are:  html, pages, slides,
	 *   slidy (deprecated), epub, directory, navigation, modules,
	 *   topics. 
	 */
	def formats = ['html']
	
	/**
	 * The secondary documents in this set. These will be converted to
	 * 'html' format.  Defaults to *.mmd, *.md (excluding the primary)
	 */
	FileCollection secondaryDocuments
	
	/**
	 * Add a set of files to the current list of secondary documents.
	 */
	void docs (FileCollection files) {
		secondaryDocuments = secondaryDocuments + files
	}

	/**
	 * Clear the current list of secondary documents.
	 */
	void clearDocs () {
		secondaryDocuments = []
	}

	/**
	 * The listing documents in this set. These will be converted to
	 * 'html' format.  Defaults to *.h, *.cpp, *.java, and *.listing
	 * files in the project directory, but not subdirectories. 
	 */
	FileCollection listingDocuments
	
	/**
	 * Add a set of files to the current list of listing documents.
	 */
	void listings (FileCollection files) {
		listingDocuments = listingDocuments + files
	}

	/**
	 * Clear the current list of listing documents.
	 */
	void clearListings () {
		listingDocuments = []
	}

	/**
	 * The support documents in this set. These will be copied to
	 * the corresponding directory of the website without modification.
	 * Defaults to *.html, *.css, *.js, *.png, *.gif, *.jpg, *.h, *.cpp,
	 *   *.java, and *.listing (ignoring subdirectories)
	 */
	FileCollection supportDocuments
	
	/**
	 * Add a set of files to the current list of listing documents.
	 */
	void support (FileCollection files) {
		supportDocuments = supportDocuments + files
	}

	/**
	 * Clear the current list of listing documents.
	 */
	void clearSupport () {
		supportDocuments = []
	}


	Project _inProject
	
	DocumentSet (Project project) {
		_inProject = project
		primaryDocument = _inProject.file(project.name + '.md')
		secondaryDocuments = project.fileTree('.').include('*.mmd').
		   include('*.md').exclude(project.name + '.md');
		listingDocuments = project.fileTree('.').include('*.h').
		   include('*.cpp').include('*.java').include('*.listing');
		supportDocuments = project.fileTree('.').include('*.h').
		   include('*.cpp').include('*.java').include('*.listing').
		   include('*.html').include('*.css').include('*.js').
		   include('*.png').include('*.jpg').include('*.gif');
	}
	
	 
    
    /**
     * Allow application of a closure to a DocumentSet.
     */ 
    Course call (Closure closure) {
        closure.delegate = this
        closure.call()
        return this
    }

}
