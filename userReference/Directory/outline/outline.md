Title: @courseName@ Outline
Author: @semester@



<!-- Items can be dated in several fashions by including a date and/or time in the
     the text of the item description, outside of any link. Dates are written
     in the format YYYY-MM-DD. If exporting the website as a Blackboard package, these
     dates and times can be copied directly into the Bb calendar.
     
     date:2016-05-21    A simple date, formatted as (05/21/2016)
     due:2016-05-21     Formatted as (Due: 05/21/2016)
     time:7:30PM        Combines with date: as (05/21/2016 07:30PM) or with 
                        due: as (Due: 05/21/2016 07:30PM) 
     time:7:30-8:30PM   Combines with date: as (05/21/2016 07:30-8:30PM)
     enddate:2016-05-24 Combines with date: as  (05/21/2016-05/24/2016) Cannot combine
                        with a time:

-->

# Organization of the Course Website

The outline document can be divided into sections, subsections,
subsubsections, etc., as necessary to represent the 
organization of the course.  At the lowest level, sections provide a
numbered list of _activities_. 

Text content within a section, up to the first numbered list, appears
in the left of the expandable modules page.  This content cannot include
section/subsection titles or numbered lists. A typical use of this material
is to provide **Overview** and **Objectives** sections for each module in
a course.

<!-- The first numbered list in a section begins the activities list
     of a module.  
     
     You can have more than one list, if you want to break the activities 
     apart into topics
     
     -->

1. [](lecture) [Directory Structure of a Course](doc:directories)

<!-- Each activity begins with a "fake" Markdown link [text](url).
     For this fake link, the URL part designates the "kind" of activity.
     In the item above, the kind is "lecture". This will be rendered using 
     a small icon graphics:lecture.png. If the text part is empty, then
     the presentation table (described below) is check to see if there
     is a standard prefix for that "kind". If so, that prefix text is
     inserted. If the text part is non-empty, it overrides the
     standard prefix.  

     After that, the rest of the item is treated as normal Markdown. In
     this case, the rest of the item is a link to a set of lecture notes
     in the document set "directories".   
-->

2. [Read ](lab) [TBD](doc:usingGradle)

<!-- A number of shorthands are available for linking to document sets,
     including the doc: style URL for easily referencing document sets
     and the automatic extraction of titles in the place of "TBD" link text.
     
     These shorthands probably see more use in the outline than anywhere
     in the course.  
-->

88. [](lab) [TBD](doc:courseConfiguration) date:2016-08-28

<!-- Like all numbered lists in Markdown, the actual numbers don''t matter. -->




# Writing Documents

**Overview**

This section covers document sets and their constituent documents.


88. [](lecture) [TBD](doc:markdown) date:2016-08-28 enddate:2017-02-28

88. [](lecture) [TBD](doc:urlShortcuts) due:2016-08-28 time:11:59PM

88. [](lecture) [TBD](doc:configuringDocumentSets)

88. [](lecture) [TBD](doc:outlineAndNavigation)


# Preamble

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
| <img alt="text" src="text.png"/>                | Textbook readings |
| <img alt="lab" src="lab.png"/>                  | Lab               |

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
 
