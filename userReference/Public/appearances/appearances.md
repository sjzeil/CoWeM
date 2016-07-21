Title: Modifying Appearances & Behaviors
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

Appearances and behaviors of documents are controlled by

* CSS and Javascript files in the`styles/` directory,
* Graphics in the `graphics/` directory.

The general rule is that anything placed in a course's `styles/` or `graphics/` directory
will be copied in to the corresponding dirctory of the webite. If  you place a file there  that has
the same name as any of the CoWeM defaults, it  ill replace  the default  file.

# CSS

## Modifying All Documents in a Format

When a primary or secondary document is processed using a _format_, the generated web page loads
CSS from

`styles/md-`<i>format</i>`.css`
: This contains all of the default CSS for that format.  You can override the CSS for all documents
  in the website that use that _format_ by supplying your own version of that file.  Do  this only if 
  you need to override  all or most of the default CSS. You would probably
  want to build the website first, get a copy of the default from the generated `styles/` directory, 
  and modify that to make your own version. 
  
`styles/md-`<i>format</i>`-ext.css`
: This is loaded after the `styles/md-`<i>format</i>`.css` file, and  so can extend or override
  any of the default CSS. The default version of this file is empty. So, if you only want  to
  change  a few items in the CSS, supply your own version  of this file with just those CSS  items.
  


\bExample{Changing  the background  of  slides}

\eExample

## Modifying Individual Documents

You can add additional CSS files  that affect only a single document by listing CSS URLs (relative or absolute) in `CSS:` lines in the document's opening metadata.





# Graphics

There are a number of graphics files stored in the default `graphics/` directory. You can supply
your own versions of  any of these or augment  the common graphics pool by adding your own.

Most of the default graphics are

* The activity kind icons _activity_`-kind.png` mark the various activities in the  course  outline.
  Examples are `lab-kind.png` <img src="graphics:/lab-kind.png"/> and `lecture-kind.png` 
  <img src="graphics:/lecture-kind.png"/>.

* The navigation icons that appear at the top and bottom of most pages:
    * `prev.png` <img src="graphics:/prev.png"/>
    * `next.png` <img src="graphics:/next.png"/>

\bExample{Adding a New Activity Kind}

\eExample



# Javascript

\bExample{Changing Keyboard  Bindings  for Advancing  Pages/Slides}

\eExample


