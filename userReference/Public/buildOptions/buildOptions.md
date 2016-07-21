Title: Modifying Build Options
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

The `build.gradle` files in each directory of the website source control
a variety of options as to how the website will be built.

For the most part, these have been provided with reasonable defaults. But
when the default behavior is not what you want, you have options.

# Document Set Options

Each document set will have a `build.grade` file in its directory. At a minimum,
this will consist of a single line:

```
apply plugin: 'edu.odu.cs.cowem.Documents' 
```

More generally, this file would have the form:

```
apply plugin: 'edu.odu.cs.cowem.Documents' 

documents {
   /*...*/
}
```

with any of the following appearing in the `documents` area:

`primary` = _fileName_
: indicates that the name of the primary document will not be the same as
  the name of the directory holding the document set ( followed by "`.md`"),
  but will be _fileName-.[^This feature has not been extensively tested.] 

`formats = [` _list-of-formats_ `]`
: Sets the list of formats in which the the primary document will
  be generated. Possibilities are:  _scroll_, _pages_, _slides_,
  _directory_, _navigation_, _modules_, and
  _topics_. 

    Example: `formats = [slides, scroll]`

    A primary document `foo.md` produces a webpage `foo__`_format_`.html`
    for each _format_ listed. There are two underscore characters between
    the document name and the format (to reduce the chances of an accidental
    conflict with support file names. 


 `index =` _format_
 : One format of the primary document is designated as the "index" of the
   document set.  It can be accessed as `index.html`, and this is the format
   that other documents will l8ink to when using the `doc:` URL shortcut.
   
     By default, the first format named in the `formats =` specification is the
     index format.


`docs` _file-specification_
: Adds some files to the list of secondary documents for this document set.
  Secondary documents will be converted to 'scroll' format.  
  
    By default, the secondary documents are all `*.mmd` and `*.md` files 
    (excluding the primary document).
    
    A secondary document `foo.mmd` produces a webpage `foo.mmd.html`.
    
    To add a single file `secondary.mtxt` to the list of secondary documents,
    just say
    
        docs 'secondary.mtxt'
        
    To add all files ending in '`.mtxt`' use the `fileTree` construct of Gradle:
    
        docs  fileTree('.').include('*.mtxt')
        
    It is also possible to add a `.exclude(`_pattern_')' to
    exclude selected files.  Intead of the `'.'`, the current
    document set directory, secondary documents could be loaded from
    a subdirectory.
    
    
 `clearDocs()`
:  Clears the entire list of secondary documents. Not even the normal
    defaults will be retained.


`listings` _file-specification_
: Adds some files to the list of listing documents for this document set.
  Listings are processed into 'scroll' format while preserving all
  indentation and line breaks.  
  
    By default, the listing documents are all `*.h`, *.cpp`, `*.java` and
    `*.listing` files. 
    
    A secondary document `foo.cpp` produces a webpage `foo.cpp.html`.
    
    To add a single file `bar.pl` to the list of listing documents,
    just say
    
        listings 'bar.pl'

    `fileTree`s can also be used to add multiple listing files at a time,
    as described for secondary documents, above.
            
    
 `clearListings()`
:  Clears the entire list of listing documents. Not even the normal
    defaults will be retained.



`support` _file-specification_
: Adds some files to the list of listing documents for this document set.
  Support documents are copied, u hanged, to the website. 
  
    By default, the support documents are all `*.html`, *.css`, `*.js` and
    most common graphics format files. 
        
    To add a single file `bar.gif` to the list of listing documents,
    just say
    
        support 'bar.gif'

    `fileTree`s can also be used to add multiple listing files at a time,
    as described for secondary documents, above.
            
    
`clearListings()`
 :  Clears the entire list of listing documents. Not even the normal
    defaults will be retained.


`math =`_mode_
: Sets the mode used for mathematics processing. 
  Valid values are:
  
     *    `latex`: Detect and render LaTeX mathematics
     *    `ascii`: Detect and render AsciiMath mathematics
     *    `none` : Do not use MathJax to render mathematics


# Group Options

Document groups (the directories containing one or more document sets) are not
required to have a `build.gradle` file. They can, however, use one to
establish defaults for all document sets within the group.

Such a group `build.gradle` would take the form:

```
apply plugin: 'edu.odu.cs.cwm.Group'

subprojects {
    project.documents {
        /*...*/
    }
}
```

Within the `project.documents` area, you can place any of the document set
options described in the previous section. These will become the defaults for
all document sets within the group. They can still be overidden by the
`build.gradle` specification for individual document sets.

One difference is that any uses of `fileTree` must now be written as
`project.fileTree` and placed in side `{ }` brackets.

For example:

```
 apply plugin: 'edu.odu.cs.cwm.Group'

subprojects {
    project.documents {
        formats = ['scroll', 'slides']
        support {
            project.fileTree('.').include('*.zip')
        }
    }
}
``` 





# Course Options   

## Adding and Removing Groups

The number and name of the document groups is established within the
`settings.gradle` file in the root directory.


```
def includeFrom = {
    dir ->  new File(rootDir,dir).eachFileRecurse { f ->
        if ( f.name == "build.gradle" ) {
            String relativePath = f.parentFile.absolutePath - rootDir.absolutePath
            String projectName = relativePath.replaceAll("[\\\\\\/]", ":")
            include projectName
        }
   }
}

// Don't touch anything abode this line

rootProject.name = 'CS350'   // any short descriptive word or phrase - no blanks  


// The following lines establish the course groups.
includeFrom('Directory')
includeFrom('Public')
includeFrom('Protected')

```

In general, you need at least one group, but can have as many as you
like.  The only restrictions:

* If you have any pages in _directory_, _modules_, or _topics_ format,
  you need a `Directory` group and a _navigation_ document set within
  that group.
  
* If you intend to export your course website to Blackboard, you need
  a `Directory` group and a _navigation_ document set within that
  group.

That said, most websites will have a `Directory` group and both
_outline_ and _navigation_ document sets within that group.


## Course Options

The root directory's `build.gradle` file describes the course for which
the website is being built.

```
// Top-level build.gradle for a course.


buildscript { 
     repositories {
         jcenter()
         mavenCentral()
 
        ivy { // Use my own CS dept repo
            url 'https://secweb.cs.odu.edu/~zeil/ivyrepo'
        }
     }
     dependencies {
         classpath 'org.hidetake:gradle-ssh-plugin:2.3.0+'
         classpath 'edu.odu.cs.zeil:cowem-plugin:1.0+'
     }
}


apply plugin: 'edu.odu.cs.cowem.CourseWebsite'    


course {
    /*...*/
}

```

Within the `course` area, any of the following can be specified

### Required Properties

`courseName = ` _string_
: The short name of course, generally in DeptNumber form.


`courseTitle = ` _string_
: The  full name of course.


`semester =` _string_
: The semester of this offering.

`sem =` _string_
: Abbreviated name for the  semester of this offering.


`instructor =` _string_
: The instructor name.



### Optional Parameters: Course properties
    

`email = ` _string_
: The email address for inquiries about the course. This appears as a `mailto:`
  link at the top and bottom of most pages.
      

`copyright =` _string_
: A copyright notification.

`delivery =` _string_
: The course delivery style, commonly used in conditional text within documents
  to select variant text for web versus face to face courses.
  
    Typical values are  "online"   or  "live"

`homeURL =` _URL_
: URL used in "home" links from documents (generally in document footers).
  If omitted,  such links should be suppressed.
 

### Optional Parameters: Deployment
    

`baseURL = ` _URL_
:  The URL at which the root directory of the website will be located.
   This is only used when preparing "thin" packages that allow Blackboard
   or other Learning Management Systems to link to a course website.
   
     Currently used only with the Gradle `bbthin` target.
   
   
    
`deployDestination =` _local-directory`     
:  A directory on the local machine to which course materials
   should be copied to deploy the website.
   
     Used only with the Gradle `deploy` target. 
     

`sshDeployURL = ` _URL_
: An ssh URL, usually on a remote machine, to which course materials
  should be copied to deploy the website.
  
     Used only with the Gradle `deployBySsh` target. 
  
    
`sshDeployKey = ` _file_
: An ssh key used for deploying to a remote machine.
  
     Used only with the Gradle `deployBySsh` target. 


`rsyncDeployURL = ` _URL_
: An ssh URL, usually on a remote machine, to which course materials
  should be copied to deploy the website.
  
     Used only with the Gradle `deployByRsync` target. 
  
    
`rsyncDeployKey = ` _file_
: An ssh key used for deploying to a remote machine.
  
     Used only with the Gradle `deployByRsync` target. 



### Optional Properties; Website


`mathjaxURL = ` _URL_
: The URL used for mathematics rendering.  Defaults 
  to `https://cdn.mathjax.org/mathjax/latest`
  
`highlightjsURL = ` _URL_
: The URL used for code highlighting.
 

