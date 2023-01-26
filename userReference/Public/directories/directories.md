Title: Directories and Files
Author: Steven Zeil
Date: @docModDate@
TOC: true

This document presents an overview of the directory and file structure
of a CoWeM website.

The directory structure, both of the input documents and of the
generated website, is kept purposely simple. This allows easy
development of the content and easy interlinking between documents in
the generated website.  On the other hand, the logical structure of
the website, and revealed via the navigation links, can be as
complicated as you need it to be.

# Directory Overview

\bSidebar

    course root/
    |- settings.gradle
    |- build.gradle
    |- gradlew
    |- gradlew.bat
    |- gradlew-gui.bat
    |- gradle/
    |- Directory/
    |--|- outline/
    |--|--navigation/
    |--|--policies/
    |--|--library/
    |- Public/
    |--|- syllabus/
    |--|--orientation/
    |--|--lecture1/
    |--|--lecture2/
    |- Protected/
    |--|- assignment1/
    |--|--assignment2/
    |--|--project/
    |- graphics/
    |- styles/
    |- build/
    |--|- website/
    |--|- packages/

\eSidebar


The overall directory structure is shown in the diagram on the right.
The "course root" is the top directory for the course website content.

The first several files and directories, the ones that contain
"gradle" in their name, provide the software and configuration that
actually builds the website. Some of these are discussed in the
sections that follow, and a more detailed discussion is in
[TBD](doc:buildingTheWebsite).

The `Directory`, `Public`,and `Protected` directories are examples of
_document set groups_, or _groups_ for short. Each group contains an
arbitrary number of _document sets_. Document sets are described in
the next section, and groups in
[Document Set Groups](#document-set-groups).

The next two directories, `graphics` and `styles`, are optional in the
input but will be present in the generated website. The `graphics`
directory contains icons that are used in multiple places in the
website, and the `styles` directory contains CSS and Javascript files
that control the appearance and behavior of pages on the site. If you
provide either of these in your input directories, the content you
place there will appear in the corresponding direcotires in the
generated website. This can be used to extend the standard offerings
{e.g., adding new icons) or to override the defaults (e.g., to alter
the CSS used for some pages)., This is discussed in the later documents
on [customizing the website](doc:outline).

The `build` directory contains work areas and output areas and is
created when you generate the website.  The generated website istelf
will appear in `build/website/` and you can preview the website before
deploying it by opening any of the HTML files in `build/website/...`
with your web browser. If you request construction of a _package_,
such as a zip archive of the generated website, you will find that in
`build/packages`.

> Everything in the `build` directory can be reconstructed quickly and
> easily. It is safe to delete any subdirectory in there, or the
> entire `build` directory. In fact, that is the easiest way to
> "reset" the build process back to a clean state.

# Document Sets

\bSidebar

    course root/
    |--/*...*/
    |--|--lecture1/           /*1*/
    |--|--|- lecture1.md      /*2*/
    |--|--|- extraNotes.mmd   /*3*/
    |--|--|- listing1.cpp     /*4*/
    |--|--|- listing2.md      /*4*/
    |--|--|- diagram.png      /*5*/
    |--|--|- build.gradle     /*6*/
    |--|--lecture2/
    |- /*...*/

\eSidebar
A document set \co1  consists of

* one primary document,\co2  [written in Markdown](doc:markdown), which
  is rendered in one or more HTML-based _formats_ on the website.
    * By default, the primary document in a document set directory
      named "foo" would be in a file named `foo.md`.
* any number of secondary documents,\co3  written in Markdown, which
  are rendered as web pages using the _<span/>scroll_ format.
    * By default, any files ending in `.md` or `.mmd`, other than the
      primary document, will be treated as secondary documents.
    
* any number of listings,\co4  which are rendered as basic web pages
  preserving all line breaks, indentation, and spacing.
    * By default, files ending in`.h`, `.cpp`, `.java`, or `.listing`
      are treated as listings.
    * Syntax highlighting (coloring) is applied to these listings.
    
* any number of support files,\co5  which are copied to the website
  without modification. Examples of support files would be graphics,
  document-specific CSS, or data files that you want to link to.
    * By default, common graphics formats and files ending in `.css`,
      `.js`, and `.html` are treated as support files.
* a configuration file named `build.gradle` \co6   
    * Among other things, this configuration can augment or override
      any of the defaults listed above.
    
## Primary Document Formats

Primary documents can be rendered in several _formats_. Currently
supported formats are

### The Major Formats

The three major formats are:

scroll
: The entire document rendered as a single web page.

%if _scroll
    
    You are currently reading the _scroll_ format of this document.
    
%else

    You can view the _scroll_ format of this document by clicking the
    <img src="graphics:scroll.png"/> symbol at the top or bottom of
    this page.
    
%endif

pages
: The document is split into multiple pages with next-page and
previous-page controls. New pages are started at each new section,
sub-section, or sub-sub-section.  A new page is also started at any
horizontal rule that is not nested inside a sidebar, example, or other
text organizational unit.

%if _pages
    
    You are currently reading the _pages_ format of this document.
    
%else

    You can view the _pages_ format of this document by clicking the
    <img src="graphics:pages.png"/> symbol at the top or bottom of
    this page.
    
%endif




slides
: The document is split as in the pages format, but the formatting
(CSS) uses larger fonts and colored background suitable for slides in
a lecture.  These "slides" can be longer vertically than the screen,
and so may require some scrolling. This is contrary to the practice of
more conventional slide formats, but I actually regard that as a major
advantage, as it allows the presentation of programming code,
mathematics, or diagrams that don't require shrinking or being
artificially chopped into short segments.

%if _slides
    
    You are currently reading the _slides_ format of this document.
    
%else

    You can view the _slides_ format of this document by clicking the
    <img src="graphics:slides.png"/> symbol at the top or bottom of
    this page.
    
%endif

    * Navigation through both the _pages_ and _slides_ formats can
    be accomplished by clicking on the <img src="graphics:prev.png"/> 
    and <img src="graphics:next.png"/>
    symbols at the top and bottom of each page or, on most system, 
    using the left and right arrow keys.  It is possible to change
    the bindings to use other keys and events (e.g., click/Enter to
    advance) by a fairly simple [modification of the Javascript in
    the `styles/` directory](doc:graphicsAndStyles).

You may notice that the text above changes slightly as you move from one
format to another.  You can use [conditional text](doc:conditionalText) to
select paragraphs of text based upon, among other things, the format into which
the document is being rendered. My most common use for conditional text
is to omit paragraphs of explanatory text from slides that represent things I will
actually say in class when discussing a slide, but allow that text to appear in
the lecture notes that I provide for students to review or for publication in a
web version of the same course.

### Specialized Formats

There are also a number of more specialized formats:

directory
: This is used for the major organizational pages in the website. It is a
  single web page with a navigation panel on the left. The navigation panel
  provides links, usually to the various directory pages.
  
    * [Here](doc:library) is an example of a page in _directory_
      format. 

navigation
: This is the format used to prepare the navigation panel that appears in
  all directory pages. 

    * [Here](doc:navigation) is an example of a page in _navigation_
      format. This is generally not viewed separately, but as included
      in directory pages.

### Formats for the Outline

Finally, there are several formats used only for presenting the course outline:

modules
: This renders the outline as a single
   page with collapsible sections.
   
    * [Here](doc:outline) is an example of a page in _modules_
      format. 
   

topics
: This renders the outline as a
  table with different columns for different types of activities.

    * [Here](../../Directory/outline/outline__topics.html) is an
      example of a page in _topics_
      format. 

calendar
: This format renders as a
  simple list of those activities in the outline that have been given
  dates, with formatting assigned based upon whether the event is in
  the past, is currently running, or is in the future.   It is
  intended to serve as a notice of upcoming events.

    This list can appear as a component of the "modules" format
    listing but can also be used separately, e.g. by inserting an
    `iframe` into a Blackboard item.
    
LMS
: This renders individual
  modules of the outline in a format that can be incorporated into Canvas (and
  potentially other LMS systems) via an `<iframe>` element.
  
  For example, compare the [outline page](doc:outline) for this site to
  the LMS format renderings of the following URLs:
  
  * [../../Directory/outline/outline__LMS.html?reveal=overview1](../../Directory/outline/outline__LMS.html?reveal=overview1)
  * [../../Directory/outline/outline__LMS.html?reveal=activities1](../../Directory/outline/outline__LMS.html?reveal=activities1)
  * [../../Directory/outline/outline__LMS.html?reveal=overview2](../../Directory/outline/outline__LMS.html?reveal=overview2)
  * [../../Directory/outline/outline__LMS.html?reveal=activities2](../../Directory/outline/outline__LMS.html?reveal=activities2)
  * [../../Directory/outline/outline__LMS.html?reveal=overview3](../../Directory/outline/outline__LMS.html?reveal=overview3)
  * [../../Directory/outline/outline__LMS.html?reveal=activities3](../../Directory/outline/outline__LMS.html?reveal=activities3)
  * [../../Directory/outline/outline__LMS.html?reveal=overview4](../../Directory/outline/outline__LMS.html?reveal=overview4)
  * [../../Directory/outline/outline__LMS.html?reveal=activities4](../../Directory/outline/outline__LMS.html?reveal=activities4)



## The build.gradle File

Each document set must have a file named "`build.gradle`". Unless you
are [overriding the default document processing options](doc:buildOptions#document-set-options),
this can consist of a single line:

    apply plugin: 'edu.odu.cs.cowem.Documents' 




# Document Set Groups

\bSidebar

    course root/
    |- /*...*/
    |- Directory/
    |- build.gradle
    |--|- outline/
    |--|--navigation/
    |--|--policies/
    |--|--library/
    |- Public/
    |--|- syllabus/
    |--|--orientation/
    |--|--lecture1/
    |--|--lecture2/
    |- Protected/
    |--|- assignment1/
    |--|--assignment2/
    |--|--project/

\eSidebar

A document set group, or group for short, is a directory containing
one or more document sets.

A group gathers together document sets that are to share some common
processing or policy. For example, a _Directory_ group might specify
that all of its document sets will, be default, render their primary
document using the _directory_ format, while the Public group might
set a default that its primary documents will be rendered using both
_scroll_ and _slides_ formats.

I often separate my non-directory documents into Public and Private
groups and, when deploying the website to an Apache web server, set
the Private directory to be accessible only to students who supply a
valid username and password.


## The build.gradle File

A group may optionally have a file named "`build.gradle`". 

You would add such a file to the group directory if you wanted to
establish some default processing options for all document sets in
that group. The details are discussed [later](doc:groupOptions). But
as an example, to establish a default that all documents in the
`Directory` group should be processed using the _directory_ format, I
would put this into a `build.gradle` file in my `Directory/` folder:

    apply plugin: 'edu.odu.cs.cowem.Group'

    subprojects {
        documents {
            formats = ['directory']
        }
    }

Similarly, in my `Public/` directory, I would place this in a
`build.gradle` file during a semester when I am teaching a live
course:

    apply plugin: 'edu.odu.cs.cowem.Group'

    subprojects {
        documents {
            formats = ['scroll', 'slides']
        }
    }
  
to indicate that I wanted document sets in the Public group to be
processed into both lecture notes (scroll) and slides for in-class
presentations. In a semester when I offer the same couse as a
web-based offering, I would change this to

    apply plugin: 'edu.odu.cs.cowem.Group'

    subprojects {
        documents {
            formats = ['scroll']
        }
    }
      
and eliminate the slides, since the lecture notes (scroll) version
usually [includes more detailed information](doc:conditionalText).

# The Course

The top-level directory of the course has two files of particular
interest.

## settings.gradle

One is the `settings.gradle` file.  This establishes the top-level
directory as the "root" of a project that is spread over multiple
directories. It looks like

```
pluginManagement {
    repositories {
        ivy { // Use my own CS dept repo
            url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
        }
        gradlePluginPortal()        
        mavenCentral()
    }
}

def includeFrom = {
    dir ->  new File(rootDir,dir).eachFileRecurse { f ->
        if ( f.name == "build.gradle" ) {
            String relativePath = f.parentFile.absolutePath - rootDir.absolutePath
            String projectName = relativePath.replaceAll("[\\\\\\/]", ":")
            include projectName
        }
   }
}

// Don't touch anything abode this line

rootProject.name = 'CS350'   // any short descriptive word or phrase - no blanks  


// The following lines establish the course groups.
includeFrom('Directory')
includeFrom('Public')
includeFrom('Protected')

```

In general, you need at least one group, but can have as many as you
like.  The only restrictions:

* If you have any pages in _directory_, _modules_, or _topics_ format,
  you need a `Directory` group and a _navigation_ document set within
  that group.
  
* If you intend to export your course website to Blackboard, you need
  a `Directory` group and a _navigation_ document set within that
  group.

That said, most websites will have a `Directory` group and both
_outline_ and _navigation_ document sets within that group.
  
## build.gradle

The `build.gradle` file in the top directory identifies this as a
CoWeM project and provides a basic set of information about the
course.


\bSidebar{66}

```
// Top-level build.gradle for a course.

plugins {
   id 'edu.odu.cs.cowem.course' version '1.16'
}

                   /*1*/

course {
    courseName        = 'CS 350'     /*2*/
    courseTitle       = 'Introduction to Software Engineering'
    semester          = 'Fall 2016'
    sem               = 'f16'             /*3*/
    instructor        = 'Steven J Zeil'   /*4*/
    email             = 'zeil@cs.odu.edu' /*5*/  
    copyright         = '2016, Old Dominion Univ.'  /*6*/
    homeURL           = '../../Directory/outline/index.html' /*7*/
}


```

\eSidebar


\co1 : Everything above this line should be reproduced exactly as shown.

\co2 : The first few lines of the course description should be easy to
understand.

\co3 : An abbreviates form for the `semester`, sometimes inserted into
  generated file names to help distinguish different offerings of the
  course.

\co4 : Again, no mystery. It's worth noting, however, that each of the
  items being defined here can be inserted into course documents
  automatically.  For example, the text "`@`instructor`@`" will be
  replaced by the name of the instructor as defined here, which can be
  useful in a syllabus for a course that gets handed from one
  instructor to another in different semesters.

\co5 : In addition to providing another convenient tag that can be
  automatically be substituted into courses, if this is given a
  non-empty value, then a <img src="graphics:email.png"/> icon is
  added in the navigation bar for most pages that, when clicked upon,
  opens up an email to that address.

\co6 : This will be added to bottom of most pages.

\co7 : If this is non-empty, a "home" <img src="graphics:home.png"/> icon
  is added to most pages that takes the reader to the indicated URL.
  Because of the "flat" directory structure, this can often be done as
  relative URL as shown in this example, or an absolute URL can be
  given when the "real" deployment URL is known.
 
