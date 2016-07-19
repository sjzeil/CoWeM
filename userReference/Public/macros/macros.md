Title: Macros
Author: Steven J Zeil
Date: @docModDate@
TOC: yes


The first stage in processing a CoWeM document is to pass it through a macro processor.
The macro processor allows extension of the notation supported by CoWeM beyond the basics
of Markdown. 

Nearly all of the [Markdown extensions described here](doc:markdownExtensions) rely on the
macro processor. CoWeM provides a standard set of  macros for this purpose, but document
authors can add their own.

%define {\macro} {macroName} {<tt>&#x25;</tt><tt>macroName</tt>}
%define {\cmd} {macroName} {<tt>&#x5C;</tt><tt>macroName</tt>}
%define {\lt} {macroName} {<tt>&lt;</tt>}

# The Macro Commands


The macro processor provides the following commands:

\macro{define}
: declares new macros. The use of this command is the subject of the remainder of this document.

\macro{if}...\macro{else}...\macro{endif}
: Allows text to be included or ignored based upon whether a macro has been defined. The use of this
command is the subject of [TBD](doc:conditionalText).

\macro{ifnot}...\macro{else}...\macro{endif}
: Allows text to be included or ignored based upon whether a macro has not been defined. The use of this
command is the subject of [TBD](doc:conditionalText).




# Defining and Using Macros

Macros are defined using the `\macro{define}` command, which has the form:

\macro{define} `{` _name_ `} {` _parameters_ `} {` _body_ `}`


* The `{ }` brackets can be replaced  by `[ ]`, `( )`, or `< >`, whichever is convenient.   
* The _name_ can be any ASCII string not inclduing blanks, control characters, or either of the bracketing
  characters surrounding the name.
  
* The _parameters_ is a comma-separated list of zero or more parameters - names that will be replaced
  in the body based upon how the macro was called.
* The _body_ is the text that will be inserted in place of the macro call. The body can span multiple
  lines, and can contain any characters except the ones used for the outer bracket (which is why it is useful
  to be able to choosethe characters actually used for bracketing.
  
A macro is used by simply writing the macro name followed immediately by a bracket pair
(same options as above) contining a comma-separated list of parameter strings. These may contain
any character except a comma and the slected bracketing characters.  If a macro is declared to take no
parameters, it is called by simply writing its name.

\bExample{A Macro With No Parameters}

One of the predefined macros is \cmd{firstterm}, which is declared as

\macro{define} \lt[]\cmd{firstterm}> `{newterm} {`\lt{}`span class="firstterm">newterm`\lt{}`/span>}`
    
and called as \cmd{firstterm}`{something}` or \cmd{firstterm}`[something]` or
 \cmd{firstterm}`<something>` or \cmd{firstterm}`(something)` to produce "\firstterm{something}".   More precisely, it produces an HTML `span` element with CSS class "firstterm".
 The Markdown processor will pass this HTML content through unchanged, and the standard CoWeM dictates the appearance of "firstterm" class elements.
 
\eExample


\bExample{A Macro with Multiple Parameters}

Another predefined macro is \cmd{picOnRight}, which typsets a `.png` graphic on the right of the screen with a stipulation that it grow no wider than a specified percentage of the total width of the document.

It is defined as:

\macro{define} \lt{}\cmd{bPicOnRight}`> (file,pctwidth) {`\lt{}`div class="noFloat">&nbsp;`\lt{}`/div>`
    \lt{}`img src="file.png" style="float: right; max-width: pctwidth%;"/>}`	
 
Examining the body, you can see how both the `file` and `pctWidth` paramters are inserted into
the generated HTML.

\eExample


\bExample{A Macro with No Parameters}

The macro \cmd{co1} is used to insert a "callout symbol" containing a "1" : \co1 .

It is declared as

\macro{define} \lt{}\cmd{co1}`> `\lt{}`> [`\lt{}`span>&#x2780;`\lt{}`/span>]`
    
Because this macro is delcared to take no parameters, it is called by writing it without a following bracket: \cmd{co1} produces \co1 .

\eExample


# Adding New Macros

Macros can be added to a document by simply typing the \macro{define} command into the document.

A file of macros can be added by listing it in the metadata for a document, e.g.,

```
Title: My Document
Author: John Doe
Macros: myMacros.md

# Introduction
```
