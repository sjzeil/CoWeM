Title: Conditional Text
Author: Steven J Zeil
Date: @docModDate@
TOC: yes


The CoWeM macro processor provides support for
conditional text, allowing a single source document to serve for both
abbreviated slides and a lengthier full-text version.

%define {\macro} {macroName} {<tt>&#x25;</tt><tt>macroName</tt>}

# Macro Conditional Commands

The macro commands that allow this are 

\macro{if} _macroName_ ...\macro{else}...\macro{endif}
: If _macroName_ has been defined as a macro, then include the text after the \macro{if} and
exclude the text after the \macro{else}.   If _macroName_ has not been defined as a macro, then
ignore the text after the \macro{if} and
include the text after the \macro{else}.

* The \macro{else} part can be omitted if it is empty.

\macro{ifnot} _macroName_ ...\macro{else}...\macro{endif}
: If _macroName_ has not been defined as a macro, then include the text after the \macro{if} and
exclude the text after the \macro{else}.   If _macroName_ has been defined as a macro, then
ignore the text after the \macro{if} and
include the text after the \macro{else}.

* The \macro{else} part can be omitted if it is empty.

\bExample{Selecting Text}

I might include the following in a course syllabus:

---

\macro{define} `[summerSemester] [] [1]`  
`/*...*/`  
\macro{if} `summerSemester`
    
`The final exam is held on the last day of classes.`
   
\macro{else}
    
`The final exam is held during exam week, according to the`  
`scheduled published by the Registrar's office.`
    
\macro{endif}

---   
    
By removing or restoring the \macro{define} line, I can quickly switch between summer and regular-semester
versions of the yllabus.

\eExample



\bExample{Suppressing Text}

Sometimes I have old text that I don't actually want to delete, but don't want to appear in
the current website.  I can "comment out" such text like this ....

---

\macro{if} `_ignore`  
`/*...*/`  
...text that won't appear in the website...  
`/*...*/`  
\macro{endif}

---

Of course, I _never_ define a macro names "`_ignore`".

\eExample


# Predefined Macros for Conditional Text

There are two macros that are pre-defined when processing primary documents:

1. The _format_ (scroll, slides, pages, directory, ...) in which the document
   is being processed is added as a macro named with a leading underscore, e.g.,
   `_slides`.
   
2. In the course configuration `build.gradle`, you can specify a property "`delivery`", e.g.,

        course {
	        courseName        = 'CS 361'
	        courseTitle       = 'Advanced Data Structures and Algorithms'
	        /*...*/
	        delivery          = 'online'
        }

    Whatever value is given to this property is declared as a macro, again named
    with a leading underscore. In this example, a macro named `_online` would
    be declared.
    
    This property could be any string, but I usually use either "online" or "live".

# Hybrid Course Documents

I teach many courses in both live and web versions.

In a typical live course, I prepare slides which contain a lot of
terse bulleted lists, in which I fill in the details while speaking during the
lectures.  For example:

When preparing a web version of the course, I revisit those lists, and insert
an approximation of what I would have said during lectures, but make this additional
text conditional on **not** being processed in _slides_ format.

For example, the original slide

TBW

might become

TBW


The upshot of this is that my original slides remain and can still be used for live presentations.
But the _scroll_ format will contain the more detailed text, and can be deployed as the
web course content or as lecture notes for students in the live version.

If I wanted the extra material to appear only in web versions of the course, I would change the
"\macro{ifnot} `_slides`" to "\macro{if} `_online`".