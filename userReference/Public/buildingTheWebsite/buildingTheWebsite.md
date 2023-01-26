Title: Building the Website
Author: Steven Zeil
Date: @docModDate@
TOC: yes

CoWeM uses [Gradle](https://gradle.org) to automate the process of building the website.

Gradle is a build manager for software projects. Although a website may not involve programming
in the usual sense, it can certainly be regarded as a software system. If your only experience with
with build managers has been the ones built in to a programming IDE or the venerable Unix `make` utility,
you may be surprised at the range of features provided by Gradle.  Of particular relevance to CoWeM:

* Gradle allows the steps involved in a build, such as copying and and manipulation of files, to be described and performed in in a portable (not dependent on a specific operating system) manner.

* Gradle can fetch and incorporate software packages needed for the build from over the internet,
  and can check periodically for updates to those packages.
  
* The ability to fetch needed software packages extends to Gradle itself, so by including a few small
  files in each copy of a CoWeM course, the Gradle build system can be bootstrapped on to any Linux,
  Windows, or OS/X machine with a reasonably up-to-date Java environment (JRE).
  
* Gradle can be extended, teaching it new processes for different kinds of projects, via a _plugin_ system.
    * CoWeM is, in fact, implemented as a package of related Gradle plugins.


# Running Gradle


\bSidebar

    course root/
    |- settings.gradle
    |- build.gradle
    |- gradlew
    |- gradlew.bat
    |- gradlew-gui.bat
    |- gradle/
    |- /*...*/

\eSidebar

The files `gradlew` , `gradlew.bat`, and `gradlew-gui.bat` are scripts
that are used to launch the gradle build manager to request building the
website.  The `.bat` files are for Windows. The simple `gradlew` file is used
for both Linux and OS/X. 

The first time that you run one of these on a given machine, it will
download the actual Gradle build manager and install it in your personal
account area (Look for a `.gradle` directory in your user home directory.)
It will then proceed to download the latest version of CoWeM and of the
various software libraries used by CoWeM. This software is stored in a cache
(within the afore-mentioned `.gradle`directory). Items in the cache expire
periodically (typically after 24 hours), so that Gradle will check at intervals
to see if any of that software has been updated.


Gradle can be run in a command-line mode or a GUI mode.  

## Windows

To run in command-line mode, open a `cmd` window, `cd` to your course root
directory, and run

`gradlew` _target_

The possible _targets_ are discussed in the next section.


## Linux  

To run in command-line mode, open a terminal window, `cd` to your course root
directory, and run

`./gradlew` _target_

The possible _targets_ are discussed in the next section.







# The Gradle Build Targets

The following are the targets that can be supplied to Gradle at the command
line or selected from the GUI. 

## Building the Website

build
: Build the website by processing the document sets. The resulting website
  can be found in the directory `build/website` (under the course root
  directory).   You can use your favorite web browser "Open File" command to
  view any of the HTML files that have been generated. 

  The `build` target is the default. If you don't specify a target at the
  command line, `build` is run.


clean
: Delete all files that can be reconstructed via the `build` target. This is
  done by deleting the entire `build` directory. 

    As noted earlier, you can "clean" a project by simply deleting the
    `build` directory via normal operating system commands.


## Deploying the Website

"Deploying" a website means to copy it onto the server where it will be hosted.
Three options are supported for this. The relevant targets are:

\bSidebar{66}

**course root build.gradle:**

    course {
        courseName        = 'CS 350'
        courseTitle       = 'Introduction to Software Engineering'
        semester          = 'Fall 2016'
        sem               = 'f16'
        instructor        = 'Steven J Zeil'
        email             = 'zeil@cs.odu.edu'  
        copyright         = '2016, Old Dominion Univ.'
        homeURL           = '../../Directory/outline/index.html'
        /*+*/deployDestination = '/home/zeil/secure_html/cs350/f16/'/*-*/
    }
    
\eSidebar

deploy
: Synchronize the `build/website` with a directory specified in the course
  configuration (`build.gradle`) as  `deployDestination`.
  
    This works only if the destination directory is on the same
    local file system as the machine on which you are working.


\bSidebar{66}

**course root build.gradle:**

    course {
        courseName        = 'CS 350'
        courseTitle       = 'Introduction to Software Engineering'
        semester          = 'Fall 2016'
        sem               = 'f16'
        instructor        = 'Steven J Zeil'
        email             = 'zeil@cs.odu.edu'  
        copyright         = '2016, Old Dominion Univ.'
        homeURL           = '../../Directory/outline/index.html'
        /*+*/sshDeployURL      = 'zeil@atria.cs.odu.edu:/home/zeil/secure_html/cs350/f16/'
        sshDeployKey      = 'courseKey.rsa'/*-*/
    }
    
\eSidebar

deployBySsh
: Deploy the `build/website` to a directory on a remote machine 
  configuration (`build.gradle`) as  `sshDeployURL`.

    * You may optionally also specify an SSH key for access to that
      remote machine as `sshDeployKey`.
    * If no such key is used, this use any
      ssh key agent you might have running to authenticate
      on the remote machine.
        * There is currently no option for supplying a simple password.
          (Coming?)
    * Currently this deployment is done by packing the entire website
      into a Zip file archive, sending that file to the remote
      directory, and remotely issuing an `unzip` command there. 
      
        If the website is large and only a few files have been changed,
        this can be quite slow.  



\bSidebar{66}

**course root build.gradle:**

    course {
        courseName        = 'CS 350'
        courseTitle       = 'Introduction to Software Engineering'
        semester          = 'Fall 2016'
        sem               = 'f16'
        instructor        = 'Steven J Zeil'
        email             = 'zeil@cs.odu.edu'  
        copyright         = '2016, Old Dominion Univ.'
        homeURL           = '../../Directory/outline/index.html'
        /*+*/rsyncDeployURL      = 'zeil@atria.cs.odu.edu:/home/zeil/secure_html/cs350/f16/'
        rsyncDeployKey      = 'courseKey.rsa'/*-*/
    }
    
\eSidebar

deployByRsync
: Synchronize the `build/website` with a directory on a remote machine 
  configuration (`build.gradle`) as  `rsyncDeployURL` using the
  `rsync` protocol.
   
    * This can only be done if the machine on which you are running
      has the `rsync` command installed. 
    * You may optionally also specify an SSH key for access to that
      remote machine as `rsyncDeployKey`.
    * If no such key is used, this will still take advantage of any
      ssh key agent you might have running.
    * The `rsync` protocol will only transfer files that have been
      changed since the last deployment. For large files in which
      only small portions have changed, `rsync` will try to transfer
      only the changed portions.  This makes `rsync` ideal for
      updating websites after only a few things have been changed.  
      
All deployment commands will do a `build` first, if necessary.

## Packaging the Website

_Packaging_ refers to packing up the entire website into a single file that
can later be uploaded to a particular server.  All packages are deposited in
the `build/packages` directory.

CoWeM cab build both "fat" and "thin" packages.  A _fat_ package contains
the entire website. A _thin_ package contains navigation items that link
back to a deployed version of the full website elsewhere.   

Build options are:


zip
:  Package the website as a Zip archive.

    This can be unpacked in any directory managed by a web server
    to make the course content available. This includes a
    [Blackboard content collection](#importing-a-zip-package). 




scorm
: Package the website as a SCORM 1.2 package that can be imported into
  Blackboard or most other Learning Management Systems.

    Brief instructions on importing a SCORM package into
    Blackboard is given [below](#importing-a-scorm-package).
    
    The SCORM format does not include calendar updates
    in Blackboard, but calendar entries can be generated
    and imported as a `bbthin` target.



## Building a Specific Document Set Directory

If you are working intensively on a single document or document set, you can
instruct CoWeM to build or deploy only that one document set, saving some
time.

There are several ways to do this:

* If working in the Gradle GUI, you will see your CoWem groups as expandable
  items in the list of targets. Expanding one of those groups will give you a
   list of the document sets in that group. Expand one of those document sets
   and you can select a target to be applied to that document set. 

* Working at the command line, if you `cd` into the document set directory,
  you can specify the build targets just as you would at the top. The one
  caution is that you must invoke `gradlew` via the path back to the
  document root. For example, to rebuild a course syllabus, you might do
  
        cd Public/syllabus
        ../../gradlew build 
  
  in Linux or OS/X. In Windows, you would do the same except using "\" instead
  of "/".

  
* Working at the command line, but `cd`'d into the course root directory, you
  can specify a target on a specific document set using the format 
  `:`_group_`:`_documentset_`:`_target_.  For example, to rebuild a course
  syllabus you could say
  
        ./gradlew :Public:syllabus:build
  
  
The targets that can be launched for a single document set are:

build
:  Same as the yop-level `build` target, but builds only the
   indicated document set.
  
deployDoc
: Like the top-level `deploy`, this builds the document set and copies it
  to the appropriate deployment directory on the same machine.
       
deployDocBySsh
: Like the top-level `deployBySsh`, this builds the document set and copies it
  to a remote machine via secure shell. 

deployDocByRsync
: Like the top-level `deployByRsync`, this builds the document set and copies it
  to a remote machine via an `rsync` command. 
  
#  Integrating CoWeM Content into an LMS

## Linking to CoWeM Content

One of the easiest ways to integrate CoWeM content into an LMS is to simply link to your CoWeM-generated website from your LMS's.  For example, Blackboard and Canvas allow you to put links to external URLs i a course's navigation panel. In my own courses, I will typically add navigation links to

* my syllabus
* the main course outline
* a policies page, and
* a library or resources page

replacing any similarly-named entries already in the LMS navigation panel.

## LMS Modules and the CoWeM Outline

Both Blackboard and Canvas encourage organizing content into modules.  These LMS modules cna display information from the CoWeM outline page via an `<iframe>` element.

1. Add the [LMS format](doc:directories#formats-for-the-outline) to the course outline document set.
2. In the LMS, add modules pages for module overviews (if provided in the outline) and activities, using HTML code similar to this:

        <iframe src="/*+i*/URL-for-course/*-i*//Directory/outline/outline_LMS.html?reveal=overview1" width="600" height="400"></iframe>

    The `?reveal=` parameter determines what will be displayed.
    
    * Use "overview" for the module overview section (if present in
    the outline) and 'activities" for the activities section.
    * Follow that with the module number $k$, $k = 1, 2, \ldots$.



## Importing a SCORM package

1. Use the _scorm_ build target, 
   as described above, to produce a package stored in
   `build/packages/scorm`...`.zip`
   
2. Enter your LMS.  Follow its procedure for importing a SCORM package. Blackboard course. Select (or create) a content area, such
   as the "Outline" area, and enter that area.
   

3. from the "`BuildContent`" menu, select "`Content package
   (SCORM)`" and follow the instructions to upload your newly generated
   `scorm`...`.zip` file.

4. There is no "grading" associated with these packages. So choose the following
   settings in Blackboard (and similar ones for Canvas):
   
    * Make SCORM Available; yes
    * Number of Attempts: Allow unlimited attempts
    * Track Number of Views: whatever you like
    * Grade SCORM: No Grading
    * Grade SCOS: No 
    

