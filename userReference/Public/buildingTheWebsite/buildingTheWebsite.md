Title: Building the Website
Author: Steven Zeil
Date: @docModDate@

CoWeM uses [Gradle](https://gradle.org) to automate the process of building the website.

Gradle is a build manager for software projects. Although a websiote may not involvr programming
in the usual sense, it can certainly be regarded as a software system. If ypur only experience with
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

To run in GUI mode, do the same and run

`gradlew-gui`

or simply view the course root root directory in the Windows File Explorer and
double-click the `gradle-gui.bat` file.

## Linux  

To run in command-line mode, open a terminal window, `cd` to your course root
directory, and run

`./gradlew` _target_

The possible _targets_ are discussed in the next section.

To run in GUI mode, do the same and run

`./gradlew --gui &`
 


## The GUI Mode

\picOnRight{gradle-gui, 66}

In GUI mode, Gradle displays a list of possible build targets
(in the top panel), which
will include those discussed in the next section plus a variety of "generic"
Gradle targets.

You can click on any of them to select it, then click the small green "run"
button at the top of the upper panel to execute that target.    



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

As noted earlier, you can "clean" a project by simply deleting the
entire `build` directory.


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
    * If no such key is used, this will still take advantage of any
      ssh key agent you might have running.
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

    One way to deploy a website to Blackboard, for example, is to
    create a `zip` package, then upload that package to your
    course's Blackboard "Content Area". Blackboard can be instructed
    to unpack the Zip as soon as it has been transferred.

bb
: Package the website as a fat Blackboard module that can be imported into
  a Blackboard course.  Content included is
  
    * The entire website is exported to the Blackboard course 
      Content Area.
    * All links from the `Directory/navigation` document set are added
      to the navigation panel of the Blackboard course.
    * All dates found in items in the course outline 
      (`Directory/outline`) are added to the Blackboard calendar. 

> Currently, this fat Blackboard package is not working.

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
        /*+*/baseURL      = 'https://www.cs.odu.edu:/~zeil/cs350/f16/'/*-*/
    }
    
\eSidebar

bbthin
: Package the website as a that Blackboard module that can be imported into
  a Blackboard course.  Content included is
  
    * All links from the `Directory/navigation` document set are added
      to the navigation panel of the Blackboard course. These are
      resolved to absolute URLs to a deployed version of the course
      website whose root directory is located at the `baseURL`.
    * All dates found in items in the course outline 
      (`Directory/outline`) are added to the Blackboard calendar. 

When importing either of these Blackboard packages, it is important to
realize that Blackboard always duplicates items when importing the same content
two or more times. You can wind up with a really messy calendar and navigation bar
if you import more than once, unless you 

* delete the old navigation and calendar entries before doing another import, or
* make careful use of the Blackboard "import" options to import only the part
  of the package that has changed.  For example, if a navigation link has
  changed but no calendar dates have changed, you can opt to import Navigation
  Settings but not Calendar entries when usign the Blackboard "import package"
  command.     

### Unsupported in v1.0

Possible future packaging options include:

imscc
: A fat package in the IMS Common Cartridge standard format.

imsccthin
: A thin package in the IMS Common Cartridge standard format.
 
canvas
: A fat package for import into the Canvas LMS.

canvasthin
: A thin package for import into the Canvas LMS.

All of these were available in an earlier protoype of CoWeM.


## Building a Specific Document Set Directory

If you are working intensively on a single document or document set, you can
instruct Gradle to build or deploy only that one document set, saving some
time.

This can be done with any of the build or deployment targets, but not the
packaging targets.

There are several ways to do this:

* If working in the Gradle GUI, you will see your CoWem groups as expandable
  items in the list of targets. Expanding one of those groups will give you a
   list of the document sets in that group. Expand one of those document sets
   and you can select a taget to be applied to that document set. 

* Working at the command line, if you `cd` into the document set directory,
  you can specify the build targets just as you would at the top. The one
  caution is that you must invoke `gradlew` via the path back to the
  document root. For example, to rebuild a course syllabus, you might do
  
        cd Public/syllabus
        ../../gradlew build 
  
  in Linux or OS/X. In Windows, you would do the same excpet using "\" instead
  of "/".

  
* Working at the command line, but `cd`'d into the course root directory, you
  can specify a target on a specific document set using the format 
  `:`_group_`:`_documentset_`:`_target_.  For example, to rebuild a course
  syllabus you could say
  
        ./gradlew :Public:syllabus:build
  
  