# CoWeM: Course Websites from Markdown

This is a system for for building course websites, including lecture notes, 
slides & organizational pages, from Markdown documents. 
CoWeM has been used to support both traditional face-to-face and web courses, 
including the course sites
for [the CS 250 pretest](https://www.cs.odu.edu/~zeil/cs250PreTest/latest/), 
[CS 252](https://www.cs.odu.edu/~zeil/cs252/latest/),
300 (in development), 
[CS 330](https://www.cs.odu.edu/~tkennedy/cs330/s17/), 
[CS 333](https://www.cs.odu.edu/~zeil/cs333/latest/), 
[CS 350]((https://www.cs.odu.edu/~zeil/cs350/latest/)), 
[CS 361](https://www.cs.odu.edu/~zeil/cs361/latest/),
[CS 382](https://www.cs.odu.edu/~zeil/cs382/latest/),
[CS 410](https://www.cs.odu.edu/~tkennedy/cs410/s17/), and
[CS 411w](https://www.cs.odu.edu/~tkennedy/cs411/s17/).

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

* [Users' Reference Manual](https://www.cs.odu.edu/~zeil/cowem/Directory/outline/index.html)

* [Project development reports](http://www.cs.odu.edu/~zeil/gitlab/cowem/reports/reportsSummary/projectReports.html)

* Currently in version 1.11.
  
* The CoWeM system itself is a small collection of Gradle plug-ins and
  associated support libraries.  Courses built in CoWeM include a small
  set of files that will "bootstrap" the fetching of CoWeM code and the
  Gradle build manager onto nearly any system with a working Java runtime
  environment without requiring a special installation procedure.


# New in v1.11

* `[[[` and `]]]` can now be used to surround paragraphs that should be
  shown only in slides.   (The markers `{{{` and `}}}` to surround text to
  be shown everywhere _except_ in slides was intrduced in v1.8.)


# New in v1.10

* PDF collections of the primary documents on the web site contents can be
  constructed and deployed as part of the web site for off-line viewing. 

* CoWeM now publishes under the "new" (Gradle 4.x) plugin style. This
  changes the way that the plugin is imported into course projects.
  
    __settings.gradle__: Add a `pluginManagement` section at the top of the
    `settings.gradle` file.
    
        pluginManagement {
            repositories {
                ivy { // Use my own CS dept repo
                    url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
                }
                gradlePluginPortal()        
                mavenCentral()
            }
        }
   
    __build.gradle__: Remove the `buildscript` section and replace the old
    `apply plugin` statement by
    
        plugins {
            id 'edu.odu.cs.cowem.course' version '1.10'
        }


