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

Example:

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




### Descriptive material

A section (or subsection or subsubsection or...) that is not further
subdivided can have descriptive content. This material will appear in
the _modules_ view, but not the more compace _topics_ view.

Example: 

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

Each activity begins with a "fake" Markdown link `[text](url)`.

For this fake link, the URL part designates the "kind" of activity.
In the item above, one kind is "lecture". This will be rendered using 
a small icon <img src="graphics:lecture-kind.png"/> `lecture-kind.png`
loaded from the website's `graphics/` directory. 

In the _modules_ format, if the text part of the fake link is blank (must
have at least 1 blank character), then
the presentation table (described [below](#presentation-table) is checked
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

This outline, like all other documents in the course, is prepared as a
[Markdown](https://en.wikipedia.org/wiki/Markdown) document.

Most of the sections of the document become part of the course outline
that you see below. There are three exceptions to that rule:

1. A section titled "Preamble" provides content to be written above the 
   outline. You are reading the Preamble right now.
   
2. A section titled "Postscript" provides content to be written below the 
   outline. A typical use for the Postscript is to provide a symbol key like
   the one at the bottom of this page.
   
3. A section titled "Presentation" does not provide visible content, but is
   used to control formatting of the outline (in both the expandable modules
   form and the more compressed tabular format).   

# Postscript


| Symbol Key ||
|:-----------------------------------------------:|:------------------| 
| <img alt="lecture" src="graphics:lecture.png"/> | Lecture Notes     |
| <img alt="slides" src="graphics:slides.png"/>   | Slides            |
| <img alt="text" src="graphics:text.png"/>       | Textbook readings |
| <img alt="lab" src="graphics:lab.png"/>         | Lab               |

All times in this schedule are given in Eastern Time.

# Presentation


<!-- The first table controls the number of columns in the table view and
     the arrangement of items within those columns -->

| Topics | Lecture Notes | Readings | Assignments & Other Events |
|--------|---------------|----------|----------------------------|
| topics | slides video lecture construct | text | quiz asst selfassess exam event |


<!-- The second table controls prefix wording inserted before items in the moules view. -->

| Document Kind | Prefix        |
|---------------|---------------|
| lecture       | Read:         |
| lab           | In the lab:   |
| event         | Attend        |
 
