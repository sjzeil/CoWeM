package edu.odu.cs.cwm

import org.gradle.api.file.FileCollection
import org.gradle.api.Project

import java.nio.file.*


/**
 * Describes a collection of documents that will appear in a single directory of the website.
 * Each document set has a primary document and zero or more secondary documents.
 * The primary document may be rendered in multiple formats, one of which becomes the
 * index.html format for the directory. All secondary documents are
 * rendered in a single format (the basic "scroll" format). 
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
     * List of outputs from the primary target
     */
    File[] getPrimaryTargets() {
        File projectBaseDir = _inProject.file("../../");
        File projectWebsiteDir = _inProject.file("../../build/website/");
        
        Path toDestinationDirFromBase =
                projectBaseDir.toPath().relativize(primaryDocument.parentFile.toPath());
        Path toOutputDir = projectWebsiteDir.toPath().resolve(toDestinationDirFromBase);
        
        File[] result = new File[1 + formats.size()];
        result[0] = new File(toOutputDir.toFile(), "index.html");
        
            String nameBase = primaryDocument.getName();
            int k = nameBase.lastIndexOf('.');
            if (k >= 0) {
                nameBase = nameBase.substring (0, k);
            }
        int i = 1;
        for (String format: formats) {
           String targetFile = nameBase + "__" + format + ".html"
           result[i] = new File(toOutputDir.toFile(), targetFile)
           ++i
        }
        return result
    }

    /**
     * Name of the format that will be selected for the
     * 'index.html' of the website directory.
     */
    String indexFormat = "";

    /**
     * Sets the name of the index format.
     */
    void setIndex (String name) {
        indexFormat = name
    }

    String getIndex() {
        if (indexFormat.length() > 0) {
            return indexFormat
        } else if (formats.size() > 0) {
            return formats[0]
        } else {
            return 'scroll';
        }
    }

    /**
     * File to be used as source of index.html
     */
    File getIndexTarget() {
        File projectBaseDir = _inProject.file("../../");
        File projectWebsiteDir = _inProject.file("../../build/website/");
        
        Path toDestinationDirFromBase =
                projectBaseDir.toPath().relativize(primaryDocument.parentFile.toPath());
        Path toOutputDir = projectWebsiteDir.toPath().resolve(toDestinationDirFromBase);
        
        String nameBase = primaryDocument.getName();
        int k = nameBase.lastIndexOf('.');
        if (k >= 0) {
            nameBase = nameBase.substring (0, k);
        }
        String targetFile = nameBase + "__" + getIndex() + ".html"
        File result = new File(toOutputDir.toFile(), targetFile)
        return result
    }


    /**
     * List of formats in which the the primary document will
     * be generated. Possibilities are:  scroll, pages, slides,
     *   slidy (deprecated), epub, directory, navigation, modules,
     *   topics. 
     */
    ArrayList<String> formats = ['scroll']

    /**
     * The secondary documents in this set. These will be converted to
     * 'scroll' format.  Defaults to *.mmd, *.md (excluding the primary)
     */
    FileCollection secondaryDocuments

    /**
     * Add a set of files to the current list of secondary documents.
     */
    void docs (Object... files) {
        FileCollection additions = _inProject.files(files)
        secondaryDocuments = secondaryDocuments.plus (additions)
    }

    /**
     * Clear the current list of secondary documents.
     */
    void clearDocs () {
        secondaryDocuments = []
    }

    /**
     * The set of files tht will be produced by processing the
     * secondary documents.
     */
    File[] getSecondaryTargets() {
        File projectBaseDir = _inProject.file("../../");
        File projectWebsiteDir = _inProject.file("../../build/website/");
        File[] result = new File[secondaryDocuments.size()];
        int k = 0;
        for (File secondaryFile: secondaryDocuments) {
            Path toDestinationDirFromBase =
                projectBaseDir.toPath().relativize(secondaryFile.getParentFile().toPath());
            Path toOutputDir = projectWebsiteDir.toPath().resolve(toDestinationDirFromBase);
            
            String outputFileName = secondaryFile.name + ".html";
        
            File outputFile = new File (toOutputDir.toFile(), outputFileName);
            result[k] = outputFile;
            ++k;
        }
        return result;
    }

    /**
     * The listing documents in this set. These will be converted to
     * 'scroll' format.  Defaults to *.h, *.cpp, *.java, and *.listing
     * files in the project directory, but not subdirectories. 
     */
    FileCollection listingDocuments

    /**
     * Add a set of files to the current list of listing documents.
     */
    void listings (Object... files) {
        FileCollection additions = _inProject.files(files)
        listingDocuments = listingDocuments.plus (additions)
    }

    /**
     * Clear the current list of listing documents.
     */
    void clearListings () {
        listingDocuments = []
    }

    /**
     * The set of files tht will be produced by processing the
     * listing documents.
     */
    File[] getListingTargets() {
        File projectBaseDir = _inProject.file("../../");
        File projectWebsiteDir = _inProject.file("../../build/website/");
        File[] result = new File[listingDocuments.size()];
        int k = 0;
        for (File listingFile: listingDocuments) {
            Path toDestinationDirFromBase =
                projectBaseDir.toPath().relativize(listingFile.getParentFile().toPath());
            Path toOutputDir = projectWebsiteDir.toPath().resolve(toDestinationDirFromBase);
            
            String outputFileName = listingFile.name + ".html";
        
            File outputFile = new File (toOutputDir.toFile(), outputFileName);
            result[k] = outputFile;
            ++k;
        }
        return result;
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
    void support (Object... files) {
        FileCollection additions = _inProject.files(files)
        supportDocuments = supportDocuments.plus (additions)
    }

    /**
     * Clear the current list of listing documents.
     */
    void clearSupport () {
        supportDocuments = []
    }

    /**
     * What support is provided for typesetting Mathematics?
     * Math support is provided via MathJax.
     * 
     * Valid values are:
     *    latex: Detect and render LaTeX mathematics
     *    ascii: Detect and render AsciiMath mathematics
     *    none : Do not use MathJax
     */
    String mathSupport = "latex";

    void math(String mode) {
        mathSupport = mode
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

        _inProject.logger.debug(_inProject.name + ": created DocumentSet");

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
