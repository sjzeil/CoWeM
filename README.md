# CoWeM: Course Websites from Markdown

This is a system for for building course websites, including lecture notes, 
slides & organizational pages, from Markdown documents. 
You can see examples of these websites linked from my
[home page](http://www.cs.odu.edu/~zeil/), including the course sites
for CS 250, 330, 333, 350, 361, and 382.

The primary input document format for course content is Markdown --
specifically [multimarkdown](http://fletcherpenney.net/multimarkdown/). 

Course content can be portrayed in various HTML-based formats,
including single and multi-page documents and slides. Conditional text allows
a single Markdown document to serve as the source for both a full set of lecture
notes with explanations and a sparser set of slides.  Support is provided
for automatic pretty-printing of program source code and for the rendering
of mathematics from LaTeX expressions.

This content can be deployed as an ordinary website or as an 
export package suitable for importing into Blackboard or other
Learning Management Systems.

# Project Status

* [Project development reports](http://www.cs.odu.edu/~zeil/gitlab/cwm/utils/reports/reportsSummary/projectReports.html)

* The early v0.5 release is currently in use for multiple courses. but is
  largely limited to Linux machines with a
  fair amount of support software needing to be installed.
  
* The v1.0 release currently under development is a pure Java + Gradle
  implementation that should run portably on Linux, Windows, & OS/X. 
  
* The CoWeM system itself is a small collection of Gradle plug-ins and
  associated support libraries.  Courses built in CoWeM include a small
  set of files that will "bootstrap" the fetching of CoWeM code and the
  Gradle build manager onto nearly any system with a working Java runtime
  environment without requiring a special installation procedure.
