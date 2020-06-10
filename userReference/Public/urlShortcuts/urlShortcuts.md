Title: URL Shortcuts
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

Markdown treats text in square brackets followed by a URL in parenthese as a link: `[example]``(http://www.cs.odu.edu/~zeil/)`.

CoWeM uses special forms of URL in Markdown links to provide for easier inter-document linking
and to flag certain formatted data fields for special processing.


# Special URLs for Navigation

## doc: shortcuts

If the URL in a link is written as `doc:`_documentName_, it is treated as a
shortcut to another document within the website.

* If _documentName_ contains no periods '.', then it is assumed to be the name of a document set and
  the link is made to the `index.html` file generated from the primary document of that document set.
  (The `index.html` file is, by default, the first _format_ requested for that document.)
  
  
* If _documentName_ contains one or more periods '.', then it is assumed to be the name of a file
  containing a secondary document. All document sets are searched for a matching secondary document
  and a link is generated to the web page generated from that document.
  
A warning is issued while the website is being built if a `doc:` URL shortcut does not match any
document or if it ambiguously matches more than one.

You can see multiple examples of `doc:` shortcuts in the examples in [TBD](doc:theOutline).
You can add `#` specifiers to the end to link to particular sections of a document. For example
`doc:urlShortCuts#tbd-links` would link directly to the [next section of this document](doc:urlShortcuts#tbd-links).


## TBD links

In conjunction with `doc:` shortcuts, if the entire text of the link is "TBD" (for "to Be Determined"), the generated link will actually contain the title of the linked document as specified in that document's metadata.

For example, `[TBD]`<tt>(doc:directories)</tt> would actually appear as the link: 
[TBD](doc:directories)


## Linking to External CoWeM Sites

It is possible to link to external CoWeM sites with an extension of the `doc:` URL scheme. 

Ann number of external sites can be imported by adding commands of the form
`importing(`_name_,_url_`)` to the course description in the root `build.gradle` file. For example, 

```
// Top-level build.gradle for a course.

plugins {
   id 'edu.odu.cs.cowem.course' version '1.16'
}


course {
    courseName        = 'CS 350'     
    courseTitle       = 'Introduction to Software Engineering'
    semester          = 'Fall 2016'
    sem               = 'f16'             
    instructor        = 'Steven J Zeil'   
    email             = 'zeil@cs.odu.edu' 
    copyright         = '2016, Old Dominion Univ.'  
    homeURL           = '../../Directory/outline/index.html' 
    
    /*+*/importing('faq', 'https://www.cs.odu.edu/~zeil/FAQs')/*-*/
}


```

This indicates that we want to link to documents within the site at the
indicated URL, referring to that site as "faq" for sort.  We can then
refer to a document by its name on that website, prefixing the name with "faq:".

For example, we could link to [the document  "`installingACompiler`"](doc:faq:installingACompiler) as `doc:faq:installingACompiler` or to a section inside that document as `doc:faq:installingACompiler#installing-a-c-compiler-on-microsoft-windows`.


## graphics: and styles: shortcuts

If the URL in a link is written as `graphics:`<i>fileName</i>, it is treated as a
shortcut to that file within the website's main `graphics/` directory. For example,
I could insert the "home" navigation  icon as `<img src='graphics:home.png'/>`: 
<img src='graphics:home.png'/>

Similarly, a `styles:`<i>fileName</i> URL is resolved as a link to the website's main `styles/` directory. 



# Special URLs for Dates

A handful of special URLs are used to flag dates and times in the course
outline for special processing. These can be used anywhere, but their main
purpose is to flag activity items in the course outline for export into
calendars.

## date:

A link with the URL `date:` declares a date or date-time combination.
The text of the link should specify a date in ISO 8601 format: _YYYY-MM-DDThh:mm:ss_

The seconds may be omitted from the end of the time. The entire time (starting
with the separating character 'T') may be omitted if only the date is desired.

These will be reformatted into more readable forms. Examples:

`[2016-01-02T07:30]`<tt>(date:)</tt>
: is reformatted as [2016-01-02T07:30](date:)

`[2016-01-03]`<tt>(date:)</tt>
: is reformatted as [2016-01-03](date:)


## due:

This is like `date:`, but has slightly different handling when
imported into calendars.  A `date:` with no time component is assumed to
refer to the start of the day. A `due:` with no time component is assumed to
refer to the end of the day.  

`[2016-01-02T07:30]`<tt>(due:)</tt>
: is reformatted as [2016-01-02T07:30](date:)

`[2016-01-03]`<tt>(due:)</tt>
: is reformatted as [2016-01-03](date:)


## enddate:

A `date:` link can be immediately followed by a link with an `enddate:` URL
to specify a range of dates and times.

`[2016-01-02T07:30]`<tt>(date:)</tt> `[2016-01-05T08:50]`<tt>(enddate:)</tt>
: is reformatted as [2016-01-02T07:30](date:) [2016-01-05T08:50](enddate:)

`[2016-01-02]`<tt>(date:)</tt> `[2016-01-05]`<tt>(enddate:)</tt>
: is reformatted as [2016-01-02](date:) [2016-01-05](enddate:)

`[2016-01-02T07:30]`<tt>(date:)</tt> `[2016-01-02T08:50]`<tt>(enddate:)</tt>
: is reformatted as [2016-01-02T07:30](date:) [2016-01-02T08:50](enddate:)

>  No support is provided in CoWeM v1.0 for time zones. That may be added in
>  later versions.
 
 
 