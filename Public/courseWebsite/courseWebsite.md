Title: Course Website Overview and Instructions
Author: Steven Zeil
Date: @docModDate@

Some words of explanation about how I organize and maintain course
websites.

The website is a collection of primary and secondary
documents.

Primary documents carry lots of content and are generally
stored with each in its own directory. Primary documents can be
converted from a source format (DocBook, LaTeX, or Markdown) into any
of a variety of HTML and PDF output formats.

Secondary documents are shorter documents that are often intended to
accompany a primary document. For example, a set of lecture notes (a
primary document) on sorting algorithms might be accompanied by
several C++ source code files, each of which will be converted to an
HTML document residing in the same directory as the primary.

# The Main Directories

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

If the entire website is being hosted in a Learning management System
(e.g., Blackboard) where the entire site will be limited to enrolled
students, then there is, in effect, no difference between the _Public_
and _Protected_ directories and no special `.htaccess` file would be
necessary.

There several other directories in the website as well (_templates_,
_styles_, _graphics_).  Generally these should simply be symbolic
links to the corresponding directories in
_/home/zeil/courses/prototype_, or copies of those directories. Less
commonly, custom versions of the content of  the _styles_ and
_graphics_ directories may be used to alter the style of the website.

# Document Formats

## Primary Documents

Primary documents can be written in

* [DocBook](http://www.docbook.org):  I used this for a while, and
  still have DocBook material in several of my course websites, but have
  moved away from it.
* LaTeX: Specifically, I use
  [Beamer](http://en.wikibooks.org/wiki/LaTeX/Presentations) to
  produce both slides and notes/articles. I also have (in the
  _templates_ directory, a number of my own LaTeX macros). Most
  notably, I have my own commands for injecting graphics in a way that
  allows consistency in both the slide and notes forms.

    This produces some really beautiful PDF output, but is slow and
    finicky.	
* Markdown: specifically the
  [MultiMarkdown](http://fletcher.github.io/MultiMarkdown-4/) dialect.

    Currently, this is my favorite, because it's very easy to prepare
    documents that generate good looking out, and does so very
    quickly.

    I do run the documents through a C/M4-style pre-processor,
    allowing me to do some macros and conditional text markup.


A primary document stored in the directory _primaryDoc_ would appear
in files:

| Input format | Content Files |
|:------------:|:--------------|
   DocBook     | _primaryDoc_`.dbk` 
   LaTeX       | _primaryDoc_`.info.tex`, _primaryDoc_`.ext.tex`, _primaryDoc_`.content.tex` 
   Markdown    | _primaryDoc_`.md`





The available output formats are:

* HTML outputs
    * _html_ : the entire document in a single page of HTML
    * _pages_ : the entire document, split into pages at section and
      subsection boundaries.
	* _slidy_ : The document formatted as slides. New slides begin at
      each h1 and h2 element, and also at each horizontal rule in the
      `<body>` that is not nested within some other HTML element.

* PDF outputs
    * web: a landscape format with medium-sized type for easy reading
      on screens and tablets.
	* printable: 8.5x11 inch pages, in 11pt type
	* slides: The document formatted as slides (Beamer).


Not every input format can be converted to every output format. The
possibilities are shown in the table below:

| Input Format | can be converted to ||||||
|              |  HTML ||| PDF |||
|              | html | pages | slidy | web | printable | slides |
|:------------:|:----:|:-----:|:-----:|:---:|:---------:|:------:|
**DocBook**    |  Y   |   Y   |       |     |           |        |
**LaTeX**      |  Y   |   ?   |       |  Y  |    Y      |   Y    |
**Markdown**   |  Y   |   Y   |   Y   |     |           |        |

In theory, Markdown could generate any of the output formats, but I
have not felt it worthwhile to do more than idle experimenting with
PDF output from Markdown.

A primary document _primaryDoc_, when converted into output format
_format_, will yield a file in the same directory named
_primaryDoc_`__`_format_`.html` or _primaryDoc_`__`_format_`.pdf`.
Those are two underscores between the document name and the output
format. Most of the temporary files produced while doing the
conversions will also contain those two underscores, so that one easy
way to "clean" a directory is to delete all files _primaryDoc_`__*`.


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


## Secondary Documents

### Graphics

As a general rule, graphics to be imported into PDF output formats
should themselves be in PDF files. Graphics for use with HTML output
formats should be in PNG format.

If graphics are present in `.gif` or `.eps`` formats, they will be
automatically converted into these other formats.

Similarly, diagrams produced by `xfig` or `dia` are autmatically
exported as PNG and PDF files.

### HTML

In most cases, all primary documents when rendered into a non-slides
format will have a standard "footer" attached that provides a link
back toe the course home and a link to either a course email address
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
instructor=Steven J Zeil
copyright=2014, Old Dominion Univ.
baseurl=https://secweb.cs.odu.edu/~zeil/cs350/s14
homeurl=https://secweb.cs.odu.edu/~zeil/cs350/s14/Directory/topics.html
deploymentDestination=/home/zeil/secure_html/cs350/s14/
email=zeil@cs.odu.edu
forum=
```

The first few entries are self-explanatory.

The `baseurl` is required mainly for PDF outputs, as relative links in
PDF files do not work well at all. (Some browsers, particularly on
tablets, copy all PDFs to an internal folder and then open the PDF
from there, completely losing all memory of the URL form which the
document was obtained.)

The `homeurl` and `emailurl` values are used in the footer information
at the bottom of most pages. However, if the `forumurl` is non-empty,
the email links are replaced by Javascript code to provide access to a
course Forum.

The `deploymentDestination` names the directory to which the final content
is copied when `ant deploy` is run.

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
</outline>
```

The heart of the outline is the collection of `<topic` and `<item` elements.

* Topics divide the course and are titled.
* Items generate a line in the outline page.
    * Each item has a kind.
	* Items may link to course content. A link can appear as an `href`
      attribute (as in the HTML `<a>` element) or in a shorthand form
      as `targetdoc` if the target is a primary document in the
      _Public_ area.
	* Item elements can contain text. If the text is omitted/empty and
      the item contains a `targetdoc` link, the title of the document
      is extracted and used as the link text.
	* Other optional attributes include `date=` and `due=`, which add
      a date to the listing. The attribute `time=` can be used to add
      a time.



The `<preamble>` and `<postscript>` sections provide HTML content that
should appear above and below the course outline.

The `<presentation>` section indicates how many titles should be used
to present the outline, and what kinds of items go into each column.
