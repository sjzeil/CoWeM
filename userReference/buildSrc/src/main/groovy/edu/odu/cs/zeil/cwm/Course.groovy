package edu.odu.cs.zeil.cwm


class Course {
    // Global document attributes - these should rarely change

    /**
     *  URL used for mathematics rendering.
     */
    URL MathJaxURL = new URL("https://cdn.mathjax.org/mathjax/latest");
    
    /**
     *  URL used for code highlighting.
     */
    URL highlightjsURL = new URL("https://www.cs.odu.edu/~zeil/styles/highlight.js");
    
    /**
     *  URL used for slide/paging support (deprecated)
     */
    URL slidyURL = new URL("https://www.cs.odu.edu/~zeil/styles/Slidy2");

    /**
     *  Relative URL from document web pages to styles directory.
     */
    String stylesURL = "../../styles";

    /**
     *  Relative URL from document web pages to graphics directory.
     */
    String graphicsURL = "../../graphics";

    // Course Attributes

    /**
     *  Short name of course, generally in DeptNumber form.
     */
    String courseName = "CS999";
    
    
    /**
     *  Full name of course.
     */
    String courseTitle = "Independent Study";
    
    /**
     *  Semester of this offering
     */
    String semester = "Summer 2016";

    /**
     *  Short name for semester of this offering, generally used to modify URLs and
     *  directory paths.
     */
    String sem = "sum16";
    
    /**
     * Course delivery style, commonly used in conditionals within documents to
     * select variant text for web versus face to face courses.
     *
     * During document processing, the value is added (with a pre-pended "_")
     * as a macro definition.
     */ 
    String delivery = "online";  // "online" or  "live"

    /**
     * Instructor name
     */
    String instructor = "John Doe";
    
    /**
     * Instructor email address
     */
    String email = "zeil@cs.odu.edu";
    
    /**
     * Copyright notice
     */
    String copyright = "2016, Old Dominion Univ.";

    // Website Attributes
    
    /**
     * Common string at the beginning of all course URLs after deployment.
     * In general, we try to stick with relative URLs whenever possible so that
     * the web content can be deployed in a wide variety of contexts (e.g., within
     * an ordinary web site, within an Epub document, within a Blackboard course
     * cartridge.  Since many of these don't provide ready access to an absolute URL,
     * uses of this are under scrutiny to see if it can be eliminated.
     */
    String baseurl = "https://www.cs.odu.edu/~jdoe/cs999/" + sem + "/";
    
    /**
     * URL used in "home" links from documents (generally in document footers).
     * If null or empty, such links should be suppressed.
     */
    String homeurl = "../../Directory/outline/";

    // Build attributes

    /**
     * A directory on the local machine to which course materials
     * should be copied to deploy the website.
     *
     * If null, deploy will fail.
     */
    File deployDestination = null;

    /**
     * An ssh URL, usually on a remote machine, to which course materials
     * should be copied to deploy the website.  
     *
     * If null, deployBySsh will fail.
     */
    String sshDeployURL = null;

    /**
     * An ssh key used for deploying to a remote machine.
     *
     * If null, deployBySsh will use an existing ssh agent or will
     * prompt for credentials.
     */
    File sshDeployKey = null;
    
    /**
     * Allow application of a closure to a Course.
     */ 
    Course call (Closure closure) {
        closure.delegate = this
        closure.call()
        return this
    }

}
