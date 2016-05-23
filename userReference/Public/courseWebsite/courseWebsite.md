Title: Course Website Overview and Instructions
Author: Steven Zeil
Date: @docModDate@
TOC: yes

Some words of explanation about how I organize and maintain course
websites.

The website is a collection of primary and secondary
documents.

_Primary documents_ carry lots of content and are
stored with each in its own directory. Primary documents can be
converted from source documents in [Markdown](../markdown/index.html)
into a variety of a variety of HTML-based output formats.

Also, supported, but deprecated, is support for source documents in
DocBook or LaTeX, converted into HTML or PDF.

_Secondary documents_ are shorter documents that are often intended to
accompany a primary document. For example, a set of lecture notes (a
primary document) on sorting algorithms might be accompanied by
several C++ source code files, each of which will be converted to an
HTML document residing in the same directory as the primary.

# The Main Directories

<iframe align="right" width="60%" height="600px" scrolling="auto" src="treeOverview.html"> </iframe>

There are three major directories relating to course content:

_Directory_
:   Holds the "directory pages" of the website, including the topics
    outline, library page, search page, etc. These are the pages that
    are readily identified by the column of navigation buttons down
    the left edge.

_Public_
:   The collection of non-directory documents that are available to
    everyone who reaches the website. Typically, these include lecture
    notes, policy documents, ungraded labs, etc.

    Within the _Public_ directory will be a number of subdirectories,
    one per primary document.


_Protected_
:    The collection of documents that will be restricted to students
     enrolled in the course. Assignments and assignment solutions
     generally go here.

     Within the _Protected_ directory will be a number of subdirectories,
     one per primary document. In addition, a directory _Assts_ is used
     to hold an arbitrary collection of HTML pages for course assignments.


When deployed on an Apache-style web server, you will need to 
enforce content protection by placing an appropriate
`.htaccess` file in the _Protected_ directory of the server.

If the entire website is being hosted in a Learning Management System
(e.g., Blackboard) where the entire site will be limited to enrolled
students, then there is, in effect, no difference between the _Public_
and _Protected_ directories and no special `.htaccess` file would be
necessary.

There several other directories in the website as well (_templates_,
_styles_, _graphics_).  These are generated automatically when the
website is built.


# Document Formats

## Primary Documents

Primary documents can be written in

* Markdown: specifically the
  [MultiMarkdown](http://fletcher.github.io/MultiMarkdown-4/) dialect.

    Currently, this is my favorite, because it's very easy to prepare
    documents that generate good looking output, and does so very
    quickly.

    before processing, I run the documents through a pre-processor,
    allowing me to do some macros and conditional text markup.

* XML:  Used for the course outline only.


A primary document stored in the directory _primaryDoc_ would appear
in files:

| Input format | Content Files |
|:------------:|:--------------|
   Markdown    | _primaryDoc_`.md`
   XML      | `outline.xml` 





The available output formats are:

* from Markdown:
    * _html_ : the entire document in a single page of HTML
    * _pages_ : the entire document, split into pages at section and
      subsection boundaries.
	* _slidy_ : The document formatted as slides. New slides begin at
      each #, ##, and ### header, and also at each horizontal rule in the
      document that is not nested within a list or other nested
      formatting block.
	* _epub_ : An e-book collection of the web pages nad associated
      graphics and style files, suitable for viewing in an ebook
      reader. Selecting this option results in ebooks generated in
      both `.epub` and `.mobi` formats.

        A single ebook is generated for the entire site. Marking a
        particular document with this output option indicates that the
        document should be included within the ebook.

    * _canvas_ : A simplified version of the _html_ format, suitable for
      import into the Canvas LMS as a (wiki) "Page"  


* from XML:
    * _modules_ The course outline presetned in HTML as a collapsed arrangement of modules. Expanding a module reveals a linear list of activities associated with that module, 
    * _topics_  The same course outline presented on an HTML page as a multi-column table.
	* _epub_ : An e-book collection generated in
      both `.epub` and `.mobi` formats.



### Selecting the desired formats

In the directory for a primary document, the output formats are
controlled by the `build.xml` file. A typical such file is

```
<project name="docs" default="build">

  <import file="../../commonBuild.xml"/>

  <target name="documents" depends="setup">
    /*+*/<docformat from="md" format="html"/>
    <docformat from="md" format="pages"/>
    <docformat from="md" format="slidy"/>/*-*/
  </target>


</project>
```

The highlighted `docformat` instructions are the key. The `from`
attribute indicates the input format (`md`, `tex`, or `dbk`) and the
`format` attribute names the desired output format.

The first `docformat` listed becomes the "default" document for the
directory -- it is copied into an `index.html` file for that
directory.  The choice of default document can be overridden with an
explicit `index` attribute:

```
<project name="docs" default="build">

  <import file="../../commonBuild.xml"/>

  <target name="documents" depends="setup">
    <docformat from="md" format="html"/>
    <docformat from="md" format="pages"/>
    <docformat from="md" format="slidy" /*+*/index="1"/*-*//>
  </target>


</project>
```



## Secondary Documents


Secondary documents are logically associated with the primary or main
document in a directory. Usually, the primary document will link to
them, and they are often not referenced from anywhere else.

| Secondary Documents |||
| source format | input file extension | processed file extension |
|:-------------:|:--------------------:|:------------------------:|
| Markdown      |  .mmd                | .mmd.html                |
| C++ source code |  .cpp              | .cpp.html                |
| C++ source code |  .h                | .h.html                  |
| Java source code |  .java            | .java.html               |
| preformatted text |  .listing        | .listing.html            |
| simple HTML |  .html.xml             | .html (deprecated)       |


### Graphics

As a general rule, graphics for use with HTML output formats should be
in PNG format.  Graphics to be imported into PDF output formats should
themselves be in PDF files.

If graphics are present in `.gif` or `.eps` formats, they will be
automatically converted into these other formats.

Similarly, diagrams produced by `xfig` or `dia` are autmatically
exported as PNG and PDF files.

### HTML

In most cases, all primary documents when rendered into a non-slides
format will have a standard "footer" attached that provides a link
back to the course home and a link to either a course email address
or to a related section of a course Forum.

The same footer can be added to secondary HTML documents by storing
the document as a .html.xml file (and making sure that its contents
are valid XML, e.g., each `<p>` element must have a closing `</p>`,
elements with no nested content must end in `/>` as in `<br/>` or
`<img src=.../>`,).


### Source Code Listings

Source code listings can be "dropped" into a primary document
directory and will be converted to a separate HTML file with syntax
highlighting. Any file ending in `.h`, `.cpp`, `.java`, or `.listing`
will be handled in this manner.  The resulting files will have the
same name with `.html` appended (e.g., `foo.cpp` will yield `foo.cpp.html`).

In addition, there are some standard markups supported:

* `/*`...`*/` will be replaced by a vertical ellipsis `/*...*/`
* `/*`1`*/`, `/*`2`*/`, etc.,  will be replaced callout symbols `/*1*/`,
  `/*2*/`, etc.
* `/*`+`*/` and `/*`-`*/` can be used to \hli{highlight} regions
  of code.
  
    Actually, that's a shorthand.
	
    * `/*`+1`*/` and `/*`-1`*/` produce \hli{this highlight}.
    * `/*`+2`*/` and `/*`-2`*/` produce \hlii{this highlight}.
    * `/*`+3`*/` and `/*`-3`*/` produce \hliii{this highlight}.
    * `/*`+4`*/` and `/*`-4`*/` produce \hliv{this highlight}.

Similar markups are available within each of the input formats. For
Markdown, in particular, these same markups are available.


# Building the Website

The build manager is [Apache Ant](http://ant.apache.org/).

The website can be build by giving an appropriate `ant` command. A log
of the build output will be written to a file `ant.log`.

If you are working on a single primary document, then `cd` to that
document's directory and give the appropriate `ant` command there. To
build the entire website, cd up to the top (the directory containing
_Directory_, _Public_, and _Protected_) and give the command there.


The commands/targets that are provided are:

`ant`
: builds the primary document in all requested output formats and
builds/converts secondary documents as necessary.

`ant deploy`
: does `ant`, and then syncs the entire website with the deployment
destination (the actual server copy in a `public_html` or, if any content is
to be protected, a `secure_html` directory).

`ant zip`
: does `ant`, and then creates a `.zip` file of the website
contents. This can be uploaded to another server or to a content
collection on Blackboard.

`ant imscc`
:  builds the course documents and creates a course cartridge file
  that could be imported by most Learning Management Systems (LMS)

`ant bb`
: builds the course documents and creates a course cartridge file
     that could be imported into BlackBoard. This cartridge features the
	 entire course contents, a set of Learning Modules reflecting the
	 basic course outline, and calendar events for all dated items 
	 in the outline.

`ant bbthin`
: builds the course documents and creates a course cartridge file
  that could be imported into BlackBoard. This cartridge features 
  a set of Learning Modules reflecting the basic course outline, 
  and calendar events for all dated items in the outline. 

    This "thin" cartridge does not, however, include the actual 
	 course documents. Those are presumed to have been deployed to
	 a separate web server. The imported Blackboard modules will 
	 link to those documents.

`ant canvas`
:  builds the course documents and creates a course cartridge file
         that could be imported into Canvas. This cartridge features the
	 entire course contents, a set of Modules reflecting the
	 basic course outline, and calendar events for all dated items 
	 in the outline. In addition, documents built with a "canvas"
	 output format will be exported as Canvas (wiki) Pages.

`ant canvasthin`
: builds the course documents and creates a course cartridge file
         that could be imported into Canvas. This cartridge features 
         a set of Modules reflecting the basic course outline, 
	 and calendar events for all dated items in the outline. 
         In addition, documents built with a "canvas"
	 output format will be exported as Canvas (wiki) Pages.

    This "thin" cartridge does not, however, include the actual 
	course documents. Those are presumed to have been deployed to
	a separate web server. The imported Blackboard modules will 
	link to those documents.


`ant clean`
: Removes most of the temporary files produced as a side effect of
building the site. Temporary files that are particularly time consuming to
produce (or that affect cross references between documents)
are kept. All generated HTML and PDF is kept.

`ant cleaner`
: Does `clean` and all generated HTML and PDF is kept.

`ant cleanest`
: Does `cleaner` and removes temporary files that are time-consuming to regenerate.


# Customizing the Website

Besides creating and editing document content, some key files used to
customize the site are:

## course.properties

This file defined website-wide properties. Here is an example:

```
courseName=CS350
courseTitle=Introduction to Software Engineering
semester=Spring 2014
sem=s14
instructor=Steven J Zeil
instructorEmail=zeil@cs.odu.edu
copyright=2014, Old Dominion Univ.
baseurl=https://secweb.cs.odu.edu/~zeil/cs350/s14
homeurl=https://secweb.cs.odu.edu/~zeil/cs350/s14/Directory/topics.html
deploymentDestination=/home/zeil/secure_html/cs350/s14/
email=zeil@cs.odu.edu
forum=
bbURL=
forums=
delivery=_online
MathJaxURL=https://www.cs.odu.edu/~zeil/styles/MathJax
highlightjsURL=https://www.cs.odu.edu/~zeil/styles/highlight.js
slidyURL=https://www.cs.odu.edu/~zeil/styles/Slidy2
```

The first few entries are self-explanatory.

The `baseurl` indicates where the course website will be deployed. 
Most of the intra-course links are expressed as relative URLs, but there
are a few circumstnaces where the absolute URL is required. In particular,
"thin" targets that allow an LMS (Blackboard or Canvas) to link to a website
for course documents will use this to inform the LMS of the location of that
website.  On the other hand, the "fat" export formats (`imscc`, `bb`, 
and `canvas`) should ignore this entirely. 

The `homeurl` and `email` values are used in the footer information
at the bottom of most pages.

The `bbURL` and `forums` entries are used to interface with a
Blackboard course. The proper value for the `bbURL` should be the link
to a specific course on Blackboard (go to your list of courses for the
semester, right-click and copy the link). Similarly, the proper value
for `forums` can be found by right-clicking and saving the link of any
"tool link" in Blackboard.   If non-empty, each of these adds another
icon and link to the navigation header/footer for all documents.

That `bbURL` entry is also important because it sets up "smart"
linking for Blackboard. Most Blackboard URLs cannot be simply copied
and pasted from a browser into a webpage link and used later frm
outside of Blackboard. But both the `forums` URL and most links within
documents preceded by "bb:" will be rewritten, using the value of
`bbURL`, into a legal external Blackboard URL.

The `deploymentDestination` names the directory to which the final content
is copied when `ant deploy` is run.

The last three entries are used to locate important Javascript and CSS
resources. I find it useful, for performance reasons, to keep these on
the same server as the one serving the course website, though they can
be shared by multiple course websites on that same server.



## Directory/outline.xml

This file contains an outline of the course and is used ot prodice the
course outline/topics page.

The outline divides the course into topic
areas. Within each topic can be subtopics and items. Items are of
varying kinds, such as "lecture", "lab", or "asst". The kind names are
largely arbitrary, but are used bot to arrange the items into columns
on the course outline page and also to name an icon to be displayed
alongside the icon.

Here is a portion of a course outline:


```
<?xml version="1.0" encoding="UTF-8" ?>
<outline xmlns:axle="http://www.cs.odu.edu/~zeil/axle">
  <!-- Sample course outline: The outline is divided into topics.
       Topics are titled and may be nested inside other topics. 
       Topics may also include items. Each item is a short 
       descriptive text, with optional date, kind, and href attributes.
       -->

  <preamble>
    <info>
      <title>CS350 Introduction to Software Engineering</title>
    </info>
  </preamble>

  <topic title="Introduction">
    <item kind="slides" targetdoc="introduction"/>
  </topic>
  <topic title="The Software Development Process">
    <topic title="Process Models">
      <item kind="slides" targetdoc="processModels"/>
      <item kind="text" href="http://proquest.safaribooksonline.com/book/software-engineering-and-development/9781934015551?bookview=overview">Agarwal , Ch.2</item>
      <item kind="text" href="http://proquest.safaribooksonline.com/book/software-engineering-and-development/9780470031469">van Vliet, Sect. 3.2, 3.3</item>
    </topic>
    <topic title="Staffing">
      <item kind="slides" targetdoc="staffing"/>
      <item kind="text" href="http://proquest.safaribooksonline.com/book/software-engineering-and-development/9780470031469">van Vliet, Sect. 5.2</item>
      <item kind="lab" targetdoc="lab1" date="2013-02-14">Introductory lab</item>
    </topic>
  </topic>
   /*...*/

 <presentation>
    <column title="Topics" kinds="topics"/>
    <column title="Lectures" kinds="lecture slides event exam"/>
    <column title="Readings" kinds="text"/>
    <column title="Labs/Assignments" kinds="exam asst lab unix TA"/>
  </presentation>

  <postscript>
     /*...*/
  </postscript>

  <appendix>
	  <item kind="lecture" targetdoc="syllabus"/>
      <item kind="lecture" targetdoc="grading"/>
  </appendix>

</outline>
```

The heart of the outline is the collection of `<topic` and `<item` elements.

* Topics divide the course and are titled.
* Items generate a line in the outline page.
    * Each item has a kind.
	* Items may link to course content. A link can appear as
	    * an `href` attribute (as in the HTML `<a>` element)
		* a `targetdoc` attribute names a primary document in
		     the _Public_ area.

            If ebooks are being generated, then anything with a
            `targetdoc` link is copied into the ebook. If, for some
            reason, a document appears multiple times within the
            outline, it can have at most one `targetdoc` link.			
			
		* a `target` attribute names a primary document in
		     the _Public_ area.

             `target` links do not force the
		     copying of a document into the ebook.

		* an `assignment` attribute names a secondary document with a
		     `.mmd.html` extension in the  _Protected/Assts_ directory.



* Item elements can contain text. If the text is omitted/empty and the
      item contains a `targetdoc` or `target` link, the title of the
      document is extracted and used as the link text.
	* Other optional attributes include `date=` and `due=`, which add
      a date to the listing. The attribute `time=` can be used to add
      a time.



The `<preamble>` and `<postscript>` sections provide HTML content that
should appear above and below the course outline.

The `<presentation>` section indicates how many titles should be used
to present the outline, and what kinds of items go into each column.

The `<appendix>` section adds primary documents to the ebook that are
not named in earlier `targetdoc` links.
