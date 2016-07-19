Title: URL Shortcuts
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

Markdown treats text in square brackets followed by a URL in parenthese as a link: `[example]``(http://www.cs.odu.edu/~zeil/)`.

CoWeM uses special forms of URL in Markdown links to provide for easier inter-document linking
and to flag certain formatted data fields for special processing.


# Special URLs for Navigation

## doc: shortcuts

If the URL in a link is written as `doc:`_documentName_, it is treated as a
shortcut to another document within the website.

* If _documentName_ contains no periods '.', then it is assumed to be the name of a document set and
  the link is made to the `index.html` file generated from the primary document of that document set.
  (The `index.html` file is, by default, the first _format_ requested for that document.)
  
  
* If _documentName_ contains one or more periods '.', then it is assumed to be the name of a file
  containing a secondary document. All document sets are searched for a matching secondary document
  and a link is generated to the web page generated from that document.
  
A warning is issued while the website is being built if a `doc:` URL shortcut does not match any
document or if it ambiguously matches more than one.

You can see multiple examples of `doc:` shortcuts in the examples in [TBD](doc:theOutline).
You can add `#` specifiers to the end to link to particular sections of a document. For example
`doc:urlShortCuts#tbd-links` would link directly to the [next section of this document](doc:urlShortcuts#tbd-links).


## TBD links

In conjunction with `doc:` shortcuts, if the entire text of the link is "TBD" (for "to Be Determined"), the generated link will actually contain the title of the linked document as specified in that document's metadata.

For example, `[TBD]`<tt>(doc:directories)</tt> would actually appear as the link: 
[TBD](doc:directories)


## graphics: and styles: shortcuts

If the URL in a link is written as `graphics:`_fileName_, it is treated as a
shortcut to that file within the website's main `graphics/` directory. For example,
I could insert the "home" navigation  icon as `<img src='graphics:home.png'/>`: 
<img src='graphics:home.png'/>

Similarly, a styles:_fileName_` directory is resolved as a link to the website's main `styles/` directory. 



# Special URLs for Dates
