package edu.odu.cs.cwm



class Course {
    // Global document attributes - these should rarely change

    /**
     *  URL used for mathematics rendering.
     */
    URL mathJaxURL = new URL("https://cdn.mathjax.org/mathjax/latest");
    
    /**
     *  URL used for code highlighting.
     */
    URL highlightjsURL = new URL("https://www.cs.odu.edu/~zeil/styles/highlight.js");
    
    /**
     *  URL used for slide/paging support (deprecated)
     */
    URL slidyURL = new URL("https://www.cs.odu.edu/~zeil/styles/Slidy2");


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
     * URL used in "home" links from documents (generally in document footers).
     * If null or empty, such links should be suppressed.
     */
    String homeURL = "../../Directory/outline/";

    // Build attributes

    /**
     * A directory on the local machine to which course materials
     * should be copied to deploy the website.
     *
     * If null, deploy will fail.
     */
    String deployDestination = null;

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
     * An ssh URL, usually on a remote machine, to which course materials
     * should be copied to deploy the website via rsync.  
     *
     * If null, uses sshDeployURL (and sshDeployKey).
     * If both URLs are null, deployByRsync will fail.
     */
    String rsyncDeployURL = null;

    /**
     * An ssh key used for deploying to a remote machine via rsync.
     *
     * If null, deployByRsync will use sshDeployKey
     */
    File rsyncDeployKey = null;

    
    /**
     * Allow application of a closure to a Course.
     */ 
    Course call (Closure closure) {
        closure.delegate = this
        closure.call()
        return this
    }

}
