Title: Preparing a Course Outline
Author: Steven J. Zeil



<!-- Items can be dated in several fashions by including a date and/or
     time in the the text of the item description, in a special link
	 format. Dates and times are written in the format
     YYYY-MM-DDTHH:MM.  The hours are given in the local time zone
     military (0..23) style. The date part or the time part may be
     omitted, yielding YYYY-MM-DD or HH:MM.
	 
     These are written as special links to enable dates and times to
     be easily recognized and processed when packaging the course for
	 Blackboard or other LMS system. If you have no intention of
     importing calendar items into an LMS, you can ignore these
     entirely and simpyl type your dates as ordinary Markdown text.
	 
	 Link styles:

     [2016-05-21T19:30](date:) A simple date & time, formatted
	                           as (05/21/2016, 07:30PM)
     [2016-05-22T01:00](enddate:) An ending date & time, will be combined
	                           with a preceding "date:"
							   as (05/21/2016, 7:30PM - 05/22/2016, 1:00AM)
     [2016-05-21T19:30](due:) A variation on the basic date & time, formatted
	                           as (Due: 05/21/2016, 07:30PM). Does not
	                           combine with enddate

-->

This document review the two documents that serve mainly to enable easy
navigation of the website, the navigation bar and the outline.

It's possible to use CoWeM to build a website without either of these special
documents, with a few exceptions:

* If you will have any documents prepared using the _directory_,
  _modules_, or _topics_  formats, then
  you __must__ have a navigation document.
  
* If you will be preparing export packages for Blackboard (or, in future CoWeM
  version, other Learning Management Systems), you __must__ have a
  navigation document.

* If you will be preparing export packages for Blackboard (or, in future CoWeM
  version, other Learning Management Systems), and wish to have dates exported
  into the Calendar, you __must__ have an outline document.

# The Navigation Bar

The navigation document supplies the navigation bar found to the left of
directory pages like [this one](doc:library).

* The navigation document must be in group `Directory`.
* It must be the primary document for the document set must be named 
  "`navigation`".  (In other words, it will be written in 
  `Directory/navigation/navigation.md`.
* It must be processed in the _navigation_ format.  
 
The document itself consists of a markdown document containing a 
simple item list of (usually) links to be
placed into a navigation bar.

Here for example, is the navigation document for this website:

\loadlisting{../../Directory/navigation/navigation.md}

Not much to it, is there?  (The "`doc:`" links are examples of 
[URL shortcuts](doc:urlShortcuts), described in a later document.

To guarantee that this is processed in the proper format, the accompanying
`build.gradle` file is  

\loadlisting{../../Directory/navigation/build.gradle}


# The Outline Document

The outline document is a Markdown document that presents a hierarchically
organized collection of links to other documents in the website.

The outline document plays a special role when preparing export packages
for Blackboard and other LMSs. For those purposes, it should be in group
`Directory`, document set `outline`.   

* If desired, you could have multiple outlines presenting different
  organizations of the same website. But only `Directory/outline/outline.md`
  will be used to collect Calendar entries.      


Despite it's special nature, the outline document is, first and foremost, still
a Markdown document. There are special formats for presenting an outline:

* _modules_ format yields a collapsible list format, like
  [this](../../Directory/outline/outline__modules.html).

* _topics_ format yields a tabular format, like
  [this](../../Directory/outline/outline__topics.html).


However, the outline can still be processed with the more basic format. In fact,
preparing it as a _scroll_ may be a useful way to visualize the content as you
are adding it. Here is [the same outline in _scroll_ 
format](../../Directory/outline/outline__scroll.html).


## Preparing the outline

### Organization

Prepare the basic outline using the normal Markdown headers `#`, `##`,
`###`, etc., to divide the outline as sections, subsections, etc.

If a section/subsection is going to be further subdivided, do not add
any text in the enclosing level.

\bExample(Dividing the Outline into Sections)

```
Title: @courseName@ Outline
Author: @semester@

# Getting Started

/*...*/ 

  

# Regular Languages

## Finite Automata

/*...*/ 

## Regular Expressions

/*...*/ 

## Properties of Regular Languages

/*...*/ 

```

\eExample


### Descriptive material

A section (or subsection or subsubsection or...) that is not further
subdivided can have descriptive content. This material will appear in
the _modules_ view, but not the more compace _topics_ view.

\bExample(Adding Descriptive Material)

```
## Finite Automata

**Overview**
    
 A finite automaton (FA) is a mathematical model of a machine that
 switches among a limited number of different states each time it is
 fed a new piece of input.
    
    
 This is the most basic and the least powerful of the automata that we
 will examine this semester. It is, however, still capable of
 performing a variety of useful tasks, and its very simplicity makes
 it something that many people can work with intuitively.
    
      
**Objectives**

* Understand the basic definitions and concepts of finite
     automata and how they can be used to recognize languages.
       
* Construct FAs for some simple languages and determine what
     strings a given FA will accept. 
       
* Apply the pumping lemma to determine what languages can be
     recognized by finite automata.
       
* Modify a FA to minimize the number of states.
       
      
**Relevance**

    
 Despite their simplicity, finite automata can process a number of
 useful languages.  Because of their simplicity, programmers find FAs
 intuitive enough that they are often used to model high-level
 behaviors in large systems and in complicated programs (even though
 FAs are not powerful enough to model program behaviors in general.)

/*...*/ 

## Regular Expressions

**Overview**
    
 For every interesting group of automata, there is a corresponding set
 of languages that can be recognized by those automata. In this
 module, we examine the regular languages, the set of languages
 recognized by FAs. We look at regular expressions, a common notation
 for describing such languages, and one that should already be
 familiar to most students.
    
    
 Moving back and forth between FAs and their corresponding regular
 expressions is easier if we allow the FA to be in multiple states
 simultaneously. This leads us to the idea of non-determinism. We will
 distinguish between deterministic FAs (DFAs) and non-deterministic
 FAs (NFAs) and will explore how to convert from one to the other.
    
      
**Objectives**

* Read and write regular expressions for desired regular languages.
       
* Construct NFAs corresponding to a regular expression and
     regular expressions describing a given NFA.
       
* Convert NFAs to DFAs.
       
      
**Relevance**

    
 Regular expressions are pervasive in programming. They are a common
 tool in string searching, and students should recall their
 introduction in CS252 for that purpose. They are popular enough to be
 included in the standard libraries for both C++ and Java.
    
    
 Regular expressions are also widely employed in programming language
 compilers. The first phase of a typical compiler (the "scanner" or
 "lexical analyzer") compresses the input by reducing a string of
 characters to a string of tokens. Those tokens are typically
 recognized by a FA, which may have thousands of states. Compiler
 developers use automated tools to generate these FAs from a
 collection of regular expressions.


/*...*/ 
 
```

\eExample

### Activities

The core content of the outline is a list of activities in each of the
undivided sections.

The activities are introduced by a horizontal rule in the section (typed in
Markdown as a line containing three hyphens).

The activities are placed in ordered (numbered) lists. (Remember that
Markdown will renumber the items, so the exact number you type are not
important.)   You can have multiple numbered lists in a section, separated by 
a simple a paragraph.

Here is an example of a complete section of a course outline , with multiple
activities:

\bExample{Adding Activities}

```

# Finite Automata

    
**Overview**
    
 A finite automaton (FA) is a mathematical model of a machine that
 switches among a limited number of different states each time it is
 fed a new piece of input.
    
    
 This is the most basic and the least powerful of the automata that we
 will examine this semester. It is, however, still capable of
 performing a variety of useful tasks, and its very simplicity makes
 it something that many people can work with intuitively.
    
      
**Objectives**

* Understand the basic definitions and concepts of finite
     automata and how they can be used to recognize languages.
       
* Construct FAs for some simple languages and determine what
     strings a given FA will accept. 
       
* Apply the pumping lemma to determine what languages can be
     recognized by finite automata.
       
* Modify a FA to minimize the number of states.
       
      
**Relevance**

    
 Despite their simplicity, finite automata can process a number of
 useful languages.  Because of their simplicity, programmers find FAs
 intuitive enough that they are often used to model high-level
 behaviors in large systems and in complicated programs (even though
 FAs are not powerful enough to model program behaviors in general.)
    
      
---


1. [ ](reading) Hopcroft, 2.1

2. [ ](lab) [JFlap: Finite State Automata](doc:fsa-jflap)
    
3. [ ](reading) Hopcroft, 2.2

4. [ ](lecture) [Finite State Automata](doc:fsa)

5. [ ](selfassess) FSAs (in Blackboard)

88. [ ](quiz) FSAs (in Blackboard) [2016-09-21](due:)

**At the end of the week**

1. [ ](survey) Take the module 2 feedback survey. 

```

\eExample

Each activity begins with a "fake" Markdown link `[text](url)`.

For this fake link, the URL part designates the "kind" of activity.
In the item above, one kind is "lecture". This will be rendered using 
a small icon <img src="graphics:lecture-kind.png"/> `lecture-kind.png`
loaded from the website's `graphics/` directory. 

In the _modules_ format, if the text part of the fake link is blank (must
have at least 1 blank character), then
the presentation table (described [below](#presentation) is checked
to see if there is a standard prefix for that "kind". If so, that prefix text
is inserted. If the text part is non-blank, it overrides the
standard prefix, e.g.,

    5. [Take the self-assessment](self-assess) FSAs (in Blackboard)  


After that, the rest of the item is treated as text in any CoWeM document. 
Many items will be links to documents, though references to off-line content
such as textbook chapters will usually be in plain text.

The "quiz" item in the example above contains an example of a [shortcut
URL](doc:urlShortcuts) for recording a date. Dates recorded in this manner
may be picked up in export packages for automatic inclusion in a calendar. 


## Special Sections

Following the main outline are one to three special sections that receive
distinct processing in the _modules_ and _topics_ formats. 



### The Preamble

A section titled "Preamble" provides content that appears aobve the outline
in either the _modules_ or _topics_ format.

> It is actually a bug that this section must appear after the main
> outline sections.  Ideally, you should be able to put it anywhere, but,
> currently, placing it above the outline causes incorrect section numbering
> in the outline itself.

The preamble is typically used to provide general navigation instructions
to students entering the website.


### The Postscript

A section titled "Postscript" provides content that appears below the outline
in either the _modules_ or _topics_ format.

Typical content in the postscript might include a symbol key for the various
kinds of activities listed in the main outline, or a 
"All times in this schedule are given in "... time zone notice.

### The Presentation 

A final section, titled "Presentation" is a required element in the outline.

This section will contain two tables that provide formatting information
used in the _topics_ and _modules_ formats, respectively.


The first table controls the number of columns in the _topics_ view, the
headings of those columns, and the arrangement of activities within those
columns.

For example, this table,

| Topics | Lecture Notes | Readings | Assignments & Other Events |
|--------|---------------|----------|----------------------------|
| topics | slides video lecture construct | text | quiz asst selfassess exam event |


which can be typed in Markdown like this:

    | Topics | Lecture Notes  | Readings | Assignments & Other Events |
    |--------|----------------|----------|----------------------------|
    | topics | slides lecture | text     | quiz asst selfassess exam  |


indicates that topics headings go into the first column, that `slides` and
`lecture` activities appear in the second column, `text` activities in the third,
and that various kinds of tests and assignments appear in the fourth column.
   


The second table controls wording inserted before activity items in the _modules
view.  For example, this table

| Document Kind | Prefix        |
|---------------|---------------|
| lecture       | Read:         |
| lab           | In the lab:   |
| event         | Attend        |
 
which can be typed in Markdown as
 
    | Document Kind | Prefix        |
    |---------------|---------------|
    | lecture       | Read:         |
    | lab           | In the lab:   |
    | event         | Attend        |
 
indicates that any `lecture` activity has the "Read:" appearing in
front of it by default.  These default prefixes are used only
when no text is supplied when the activity kind is set.  For example,
the activities

    1. [ ](lecture) Notes on getting started.
    2. [Join](lecture) orientation session by net-conference
    
would appear in the _modules_ listing as:

    1. Read: Notes on getting started
    2. Join orientation session by net-conference 
