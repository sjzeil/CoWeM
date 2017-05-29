Title: Conditional Text
Author: Steven J Zeil
Date: @docModDate@
TOC: yes


The CoWeM macro processor provides support for
conditional text, allowing a single source document to serve for both
abbreviated slides and a lengthier full-text version.

%define {\macro} {macroName} {<tt>&#x25;</tt><tt>macroName</tt>}



# Special Case - Slide Markup

The markers <tt>{</tt><tt>{{</tt> and <tt>}</tt><tt>}}</tt> can be used to
surround text that will only appear in any output format **except**
`Slides`.

\bSidebar

<tt>{</tt><tt>{{</tt>

	This paragraph will appear in

	* scrolls
	* pages
	* but not in slides.


<tt>}</tt><tt>}}</tt>

\eSidebar


{{{

This paragraph will appear in

* scrolls
* pages
* but not in slides.

}}}

(Look [here](conditionalText__slides.html#special-case-slide-markup) to see the slides.)

\bSidebar{20}

For the purpose of this example, I am
typing `{({` and `})}` instead of
<tt>{</tt><tt>{{</tt> and <tt>}</tt><tt>}}</tt>
so that the CoWeM processing does not make
conditional markers disappear.

\eSidebar


There are a few limitations on the placements of these markers.


\bSidebar{60}

```

* If only a single paragraph is affected, the markers can be placed at
  the beginning and end of that paragraph.

    {({E.g., like this.})}

* If more than one paragraph is affected, the markers must go in
  separate paragraphs.

    {({

    * Like this.

    * Notice that the paragraph form "respects" the indentation
      of the regular Markdown text.

        * In fact, the closing marker can never appear at a different
          indentation than the opening one.

    })}

```

\eSidebar

* If only a single paragraph is affected, the markers can be placed at
  the beginning and end of that paragraph.

    {{{E.g., like this.}}}

* If more than one paragraph is affected, the markers must go in
  separate paragraphs.

    {{{

    * Like this.

    * Notice that the paragraph form "respects" the indentation
      of the regular Markdown text.

        * In fact, the closing marker can never appear at a different
          indentation than the opening one.

    }}}


# The General Case

## Macro Conditional Commands

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


## Predefined Macros for Conditional Text

There are two macros that are pre-defined when processing primary documents:

1. The _format_ (scroll, slides, pages, directory, ...) in which the document
   is being processed is added as a macro named with a leading underscore, e.g.,
   `_slides`.


    In fact, the <tt>{</tt><tt>{{</tt> and <tt>}</tt><tt>}}</tt>
    markers introduced earlier are just macros providing a convenient
	shorthand for <tt>%</tt>`ifnot _slides` and <tt>%</tt>`endif`.
	


2. In the course configuration `build.gradle`, you can specify a property "`delivery`", e.g.,

        course {
	        courseName        = 'CS 361'
	        courseTitle       = 'Advanced Data Structures and Algorithms'
	        /*...*/
	        delivery          = 'online'
        }

    Whatever value is given to this property is declared as a macro, again named
    with a leading underscore. In this example, a macro named <tt>_</tt>`online` would
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

%define <\mac> <> <%>
%define <\cmd> <> <\>
%define <\uscore> <> <_>
%define <\wNoSlides> <>  [<span>}</span><span>}}</span>]

---

```
# Integration Testing


\cmd{}firstterm{Integration testing} is testing that combines
    several modules, but still falls short of exercising the entire program
    all at once. 


*  Integration testing usually combines a small number of
    modules that call upon one another.
   

*  Integration testing can be conducted 
    *  \cmd{}firstterm{bottom-up}
        *   relieves the need for stubs

    *  or \cmd{}firstterm{top-down} 

```

---

might become



---

\bSidebar{20}

Again, for the purpose of this example, I am
typing `{({` and `})}` instead of
<tt>{</tt><tt>{{</tt> and <tt>}</tt><tt>}}</tt>
so that the CoWeM processing does not make
conditional markers disappear.

\eSidebar


```
# Integration Testing


\cmd{}firstterm{Integration testing} is testing that combines
    several modules, but still falls short of exercising the entire program
    all at once. 


*  Integration testing usually combines a small number of
    modules that call upon one another.
   

*  Integration testing can be conducted 
    *  \cmd{}firstterm{bottom-up }

        {({
        (start by unit-testing the modules that don't call anything
        else, then add the modules that call those starting modules
        and thest the combination, then add the modules that call
        those, and so on until you are ready to test
        \function{main()}.)
        })}


        *   relieves the need for stubs

    *  or \cmd{}firstterm{top-down} 

        {({

        (start by unit-testing \function{main()} with stubs for
        everything it calls, then replace those stubs by the real
        code, but leaving in stubs for anything called from the
        replacement code, then replacng those stubs, and so on, until
        you have assembled and tested the entire program).

        })}

{({It's worth noting that unit testing and integration testing can
sometimes use some of the same test inputs (and maybe the same
expected outputs), because we are testing the software in different
configurations.})}


```

---


The upshot of this is that my original slides remain and can still be
used for live presentations.  But the *scroll* format will contain the
more detailed text, and can be deployed as the web course content or
as lecture notes for students in the live version.



