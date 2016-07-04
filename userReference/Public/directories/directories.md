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

A document set consists of

* one primary document, [written in Markdown](doc:markdown), which is rendered in one or more
  HTML-based _formats_ on the website.
    * By default, the primary document in a document set directory named "foo" would
      be in a file named `foo.md`.
* any number of secondary documents, written in Markdown, which are rendered as web pages using the _scroll_ format.
    * By default, any files ending in `.md` or `.mmd`, other than the  primary document,
      will be treated as secondary documents.
    
* any number of listings, which are rendered as basic web pages preserving all line breaks, indentation, and spacing.
    * By default, files ending in`.h`, `.cpp`, `.java`, or `.listing` are treated
      as listings.
* any number of support files, which are copied to the website without modification. Examples of support files would be graphics, document-specific CSS, or data files that you want to link to.
    * By default, common graphics formats and files ending in `.css`, `.js`, 
      and `.html` are treated as support files.
* a configuration file named `build.gradle`
    * Among  other  things, this configuration can augment or override any of the defaults listed above.
    
## Primary Document Formats

## Configuring the Document Set

# Document Set Groups

# The Gradle Build Manager

