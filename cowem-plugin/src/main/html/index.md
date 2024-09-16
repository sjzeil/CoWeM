# CoWeM: Course Websites from Markdown

CoWeM is a static site generator for for building course websites, including lecture notes, 
slides & organizational pages, from Markdown documents. 

CoWeM is a small collection of Gradle plug-ins and associated support libraries.

CoWeM has been used to support both traditional face-to-face and web courses, 
examples of which can be found [here](./examples.html).

The primary input document format for course content is Markdown --
specifically [multimarkdown](http://fletcherpenney.net/multimarkdown/). 

Course content can be portrayed in various HTML-based formats,
including single and multi-page documents and slides. Conditional text allows
a single Markdown document to serve as the source for both a full set of lecture
notes with explanations and a sparser set of slides.  Support is provided
for automatic pretty-printing of program source code and for the rendering
of mathematics from LaTeX expressions.

This content can be deployed as an ordinary website or as a
SCORM package suitable for importing into Blackboard or other
Learning Management Systems.

# Project Status

* Currently in version 1.19.0.

* [Release Notes](./ReleaseNotes.html)

* [Users' Reference Manual](./userReference/index.html)

* [Project development reports](./reports/reportsSummary/projectReports.html)

  
# Usage

_settings.gradle_

```
pluginManagement {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
        maven { 
            url 'https://github.com/sjzeil/mvnRepo/raw/main'
        }
        mavenCentral()
    }
}

def includeFrom = {
    dir ->  new File(rootDir,dir).eachFileRecurse { f ->
        if ( f.name == "build.gradle" ) {
            String relativePath = f.parentFile.absolutePath - rootDir.absolutePath
            String projectName = relativePath.replaceAll("[\\\\\\/]", ":")
            include projectName
        }
   }
}

// Don't touch anything above this line

rootProject.name = 'CS199'   // any short descriptive word or phrase - no blanks  


// The following lines establish the course groups.
includeFrom('Directory')
includeFrom('Public')
includeFrom('Protected')
```

_build.gradle_
```
plugins {
     id 'edu.odu.cs.cowem.course' version '1.19.0'
  }



course {
	courseName        = 'CS199'
	courseTitle       = 'Introduction to Software Testing'
	semester          = 'Spring 2021'
	sem               = 's21'
	instructor        = 'John Q. Instructor'
	email             = 'jqins001@whatever.edu'
	copyright         = '2015-2021, Whatever Univ.'
	baseURL           = 'https://whatever.edu/cs350/s21/'
	homeURL           = '../../Directory/outline/index.html'
	deployDestination = '/home/jqins001/secure_html/cs199/s21/'
	sshDeployURL      = 'jqins001@www.whatever.edu:/home/jqins001/secure_html/cs199/s21/'
    delivery          = 'online'
}

```

For more details, refer to the [User Reference Manual](https://sjzeil.github.io/CoWeM/userReference/index.html)