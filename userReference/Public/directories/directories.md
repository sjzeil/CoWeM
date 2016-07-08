Title: Directories and Files
Author: Steven Zeil
Date: @docModDate@

This document presents an overview of the directory and file structure of a CWM website.

The directory structure, both of the input documents and of the generated website, is kept purposely simple. This allows easy deve;opement of the content and easy interlinking between documents in the generated website.  On the other hand, the logical structure of the website, and revealed via the navigation links, can be as complicated as you need it to be.

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


The overall directory structure is shown in the diagram on the right.  The "course root" is the top directory for the course website content.

The first several files and directories, the ones that contain "gradle" in their name, provide the software and configuration that actually builds the website. These are discussed beloe in [The Gradle Build Manager](#the-gradle-build-manager).

The `Directory`, `Public`,and `Protected` directories are examples of _document set groups_, or _groups_ for short. Each group contains an arbitrary number of _document sets_. Document sets are described in the next section, and groups in [Document Set Groups](#document-set-groups).

The next two directories, `graphics` and `styles`, are optional in the input but will be present in the generated website. The graphics directory contains icons that are used in multiple places in the website, and the `styles`c directory contains CSS and Javascript files that control the appearance and behavior of pages on the site. If you provide either of these in your input directories, the content you place there will appear in the corresponding direcotires in the generated website. This can be used to extend the standard offerings {e.g., adding new icons) or to override the defaults (e.g., to alter the CSS used for some pages).,  This is discussed in a later document, [TBD]{doc:customizing). 

The `build` directory contains work areas and output areas and is created when you generate the website.
The generated website istelf will appear in `build/website/` and you can preview the website before
deploying it by opening any of the HTML files in `build/website/...` with your web browser. If you reuqest construction of a _package_, such as a zip archive of the generated website, you will find that in `build/packages`.

> Everything in the `build` directory can be reconstructed quickly and easily. It is safe to delete
> any subdirectory in there, or the entire `build` directory. In fact, that is the easiest way to "reset"
> the build process.

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

* one primary document,\co2  [written in Markdown](doc:markdown), which is rendered in one or more
  HTML-based _formats_ on the website.
    * By default, the primary document in a document set directory named "foo" would
      be in a file named `foo.md`.
* any number of secondary documents,\co3   written in Markdown, which are rendered as web pages using the _scroll_ format.
    * By default, any files ending in `.md` or `.mmd`, other than the  primary document,
      will be treated as secondary documents.
    
* any number of listings,\co4   which are rendered as basic web pages preserving all line breaks, indentation, and spacing.
    * By default, files ending in`.h`, `.cpp`, `.java`, or `.listing` are treated
      as listings.
* any number of support files,\co5   which are copied to the website without modification. Examples of support files would be graphics, document-specific CSS, or data files that you want to link to.
    * By default, common graphics formats and files ending in `.css`, `.js`, 
      and `.html` are treated as support files.
* a configuration file named `build.gradle` \co6  
    * Among  other  things, this configuration can augment or override any of the defaults listed above.
    
## Primary Document Formats

Primary documents can be rendered in several _formats_. Currently supported formats are

The three majpr formats are:

scroll
: The entire document rendered as a single web page.

pages
: The document is split into multiple pages with next-page and previous-page controls. New pages are started at each new section, sub-section, or sub-sub-section.  A new page is also started at any horizontal rule that is not nested inside a sidebar, example, or other text organizational unit.

slides
: The document is split as in the pages format, but the formatting (CSS) uses larger fonts and colored background suitable for slides in a lecture.  These "slides" can be longer vertically than the screen, and so may require some scrolling. This is contrary to the practice of more conventional slide formats, but I actually regard that as a major advantage, as it allows the presentation of programming code, mathematics, or diagrams that don't require shrinking or being artificially chopped into short segments.

There ae also a number of more specialized formats:

directory
: This is used for the major organizational pages in the website. It is a single web page with a navigation panel on the left. The navigation panel provides links, usually to the various directory pages.

navigation
: This is theformat used to prepare the navigation panel that appears in all directory pages. 

modules
: A special format for presenting the course outline. It renders as a single page with collapsible sections.

topics
: Another special format for presenting the course outline. It renders as a table with different columns for different types of activities.


Early versions of CWM also supported and _epub_ format for preparing documents for publication in an e-book. That option is not currently supported, but may be restored in future versions.



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

A document set group, or group for short, is a directory containing one or more document sets.

A group gathers together document sets that are to share some common processing or policy. For example,
a _Directory_ group might specify that all of its document sets will, be default, render their primary document using the _directory_ format, while the Public group might set a default that its primary documents will be rendered using both _scroll_ and _slides_ formats. I often separate my non-directory documents into Public and Private groups and, when deploying the website to an Apache web server, set the Private directory to be accessible only to students who supply a valid username and password.



# The Gradle Build Manager

