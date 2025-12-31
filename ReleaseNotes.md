= Release Notes

== v1.24.0 Dec 31, 2025

* Added document metadata option FirstSection to change the number of the first top-level section (defaults to 1)

    Motivation is to allow course outlines to have a "Getting Started" module numbered 0.

== v1.23.0 Oct 23, 2025

* Imported document sets, if not provided a website URL, will teat the location as a CSV file containing lines of the form:

        documentName,URL

    Intention is to allow downloading of a collection of URLs from an LMS

* Updated gradle version & plugin unit testing libraries

== v1.22.6 Sept 27, 2024

* Syntax highlighting restored by rolling back to version 10 of highlight.js.

== v1.22.5 Sept 16, 2024

* Temporarily (?) turn off the use of highlight.js.  Current version is now removing all explicit code highlighting.

== v1.22.4 June 22, 2024

* Fixed incompatibility with Gradle 8.x (change in how Zip tasks
  are configured) allowing CoWeM to be used with Gradle 8.x and
  JDK 21 (requires Gradle 8.5+)
* Internally: reconfigured integration test setup for compatibility 
  with Gradle 8.x. No longer uses test-sets plugin.

== v1.22.3 May 31, 2024

* Fixed bug in SCORM generation.
* Removed hard-coded references to ODU websites
* Publication is now to a GitHub-hosted repository

== v1.22.0 Dec 18, 2022

* Updated for compatibility with Gradle 1.7.6

== v1.21.0 March 7, 2022

* Added LMS format for course outlines.  This provides for easy
  integration into Canvas "modules" by providing a URL that can be
  placed into an `iframe` in a Canvas page to display CoWeM-generated
  module info from the CoWeM outline page,

== v1.20.0 Jan 8, 2022

* Organisation name changed to `edu.odu.cs` - should not affect most projects.
* Support for PDF generation dropped -- too fragile and unwieldy for
  the little benefit it offered
* @amp@ added as a "delayed ampersand" to counter the Markdown
  engine's tendency to step of ampersands appearing within URLs.

== v1.19 Jan 6, 2021

* Bug fix affecting repostitories searched for 3rd party libraries
* Bug fix breaking PDF generation ("no usuable sandbox" error from Puppeteer

== v1.18 Dec 11, 2020

* Bug fix affecting operation in Windows
* Force Java compatibility to Java 11


== v1.17 Sept 11, 2020

* Fixed bug in handling of certain code blocks that caused slides to be empty
  after code block.

== v1.16 June 9, 2020

* Added ability to use doc: URL shorcuts to reference documents in other CoWeM-generated
  websites.
* Cross-group document links (doc: shortcuts) now open in a separate tab/window.

== v1.15 June 4, 2020

* [Mermaid diagrams](https://mermaid-js.github.io/mermaid/#/) can be
  embedded by giving a code block (in the three back-tick form, not
  the indented form) with "mermaid" as the programming langauge.
* Improved performance when processing intra-website `doc:` links. Should speed outline
  page processing considerably.
* Updated code to Java 11 & Gradle 1.6.5

== v1.14 Apr 28, 2020

* Javascript can be extended by placing a `md-`_formatName_`-ext.js` file in
  the styles directory.

== v1.13 Jan 20, 2020

* PlantUML diagrams can be embedded by giving a code block (in the
  three back-tick form, not the indented form) with "plantuml" as the
  programming langauge.

== v1.12, Dec 28, 2019

* Courses can be published as a SCORM 1.2 package for easy import into
  Blackboard and other Learning Management Systems.


== v1.11 August 10, 2018

* `[[[` and `]]]` can now be used to surround paragraphs that should be
  shown only in slides.   (The markers `{{{` and `}}}` to surround text to
  be shown everywhere _except_ in slides was intrduced in v1.8.)

== v1.10 June 9, 2018

* PDF collections of the primary documents on the web site contents can be
  constructed and deployed as part of the web site for off-line viewing. 
  
* CoWeM now publishes under the "new" (Gradle 4.x) plugin style. This
  changes the way that the plugin is imported into course projects.
  
    __settings.gradle__: Add a `pluginManagement` section at the top of the
    `settings.gradle` file.
    
        pluginManagement {
            repositories {
                ivy { // Use my own CS dept repo
                    url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
                }
                gradlePluginPortal()        
                mavenCentral()
            }
        }
   
    __build.gradle__: Remove the `buildscript` section and replace the old
    `apply plugin` statement by
    
        plugins {
            id 'edu.odu.cs.cowem.course' version '1.10'
        }
  
== v1.9 March 4 2018

* Added filtered option to document sets.  Like support files, filtered files
  are copied into the website, but parameters from the course build.gradle
  are replaced, e.g., @semester@ will be replaced by the semester value.
  
* Course build.gradle can name extension parameters and these will also be
  replaced in all documents and filtered files. 

== v1.8 May 29. 2017

* @docModDate@ in earlier versions was replaced by the last
  modification date of the document's source file. Now, that is the
  fallback, but if the document is in a `git` repository, it's last
  change (commit) date is used instead.

* {{{ and }}} can now be used to surround paragraphs that should be
  omitted from slides.
  
== v1.7 

* Preamble can now be placed at start of outline


== v1.6 January 16, 2017

* Outline has a new output format, "calendar". This is a simple list
  of the dated items from the outline. The accompanying Javascript
  will compare each item in the list to the current date, marking the
  items as past, recent, current, & future.  CSS formats each
  separately.

  This is suitable for access via a short iframe to provide a
  date-sensitive list of upcoming events. Such an iframe can be
  inserted into the "modules" listing or into a Blackboard item.

* In the "modules" format, adding the metadata

        Calendar: yes

    to the top of the outline.md will insert an Upcoming Events
    section after the preamble.

* Modules output format now defaults to fully expanded. (Note that
  v1.5 allowed this page to remember which modules had been collapsed
  on previous visits.  An expand/collapse button has been added for
  the preamble.

	
  
== v1.5 January 14, 2017

* "modules" format of outline now remembers which modules were
  expanded/compressed and restores state when page is revisited.

* Improved formatting of tables in slides format.


== v1.3 -- v1.4

* Various bug fixes.

== v1.2 August 13, 2016

* Various bug fixes.
* Deployment uses copy instead of sync to avoid overwriting externally
  generated files on the server.
* Groups with a build.gradle will now have
    - all *.* files in the group directory
	- an index.html file generated to prevent walking the directory
      via a web browser.

== v1.1 August 13, 2016

* Various bug fixes.
* Support for rsync from Windows (Mingw/MSYS version)


== v1.0 July 10, 2016

Initial release.

Supports

* Creation of documents in scroll, pages, slides, directory,
  modules, & topics formats.
* URL shortcuts to facilitate inter-document linking.
* Deployment of website via ssh or rsync
* Packaging of website as zip archive or thin Blackboard export
    * "Thin" exports push navigation links & calendar events. The navigation
       links point back to a conventional website for the actual content.
    * "Fat" exports push navigation links, calendar events, and all course
       content.
       
Features deferred to future releases:
* EPUB & Mobi publication
* IMS CC exports (thin and fat)
* Dating of documents from a combination of file mod dates &
  (if the course materials are in a Git repository) Git log info.
