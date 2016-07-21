Title: Library
CSS: local.css

# Reference Material:

-   [Draft ANSI/ISO standard for
    C++](http://www.cs.odu.edu/~zeil/references/cpp_ref_draft_nov97/default.htm).
-   [Java Tutorial](http://java.sun.com/docs/books/tutorial/index.html)
-   [Java API (library) 1.7
    reference](http://download.oracle.com/javase/7/docs/api/index.html)
-   C++ [STL reference material](http://www.sgi.com/tech/stl/) from
    SiliconGraphics

# Downloads:

## Chart Drawing:

Please note that, no matter which of these you use, you should never
turn in a chart as a separate file in the proprietary format laoded
and saved by these programs. Instead, you should be using your
favorite word processor to create a document, within which these
charts appear as figures. For more info, see Turning in
Non-Programming Assignments.

-   [dia](http://www.gnome.org/projects/dia/) is an excellent tool,
    it's free, and it's probably the easiest thing to use to get
    good-quality UML class relationship diagrams.
	
- The CS Dept PCs should all have Microsoft's Visio installed (as part
    of the Office suite). Although not as easy to use as the `dia`,
    this does have templates for UML diagrams.

-   The [`xfig`](http://www.xfig.org/) family of tools provide a
	good, general-purpose drawing program, though the interface
	takes some getting used to. xfig predates MS Windows, so it's
	interface was designed long before the Microsoft world of
	applictions began establishing certain conventions on GUI style.

    `xfig` is available on our Unix network. Cygwin (see below)
	users install it on their PCs via the usual CygWin setup
	utility, if they are running an X server. A Java version,
	[jfig](http://tech-www.informatik.uni-hamburg.de/applets/javafig/),
	can be run on any platform with a Java engine (including the
	Java SDK below).
	
-   All of the above tools are good for UML class relationship
	diagrams, but can be very difficult to work with when doing UML
	sequence diagrams. For those, I recommend
	[sdedit](http://sdedit.sourceforge.net/). This takes a very
	different approach by having you write a textual description of
	the elements in a chart and then producing the graphics from
	there. In essence, you are describing the sequence of calls in
	your diagram using a programming-language-like notation.


## PDF generation

The following programs will allow you to generate PDF from any Windows
program that allows printing. Generally, these create a special
printer that writes to a file instead of to a physical output
device. (Note that increasing numbers of Windows programs, including
most word processors, support options for direct generation of
PDF. Such built-in generation is usually preferable to the fake
printer approach.

-   [PDFCreator](http://sourceforge.net/projects/pdfcreator/)

-   [CutePDF Writer](http://www.cutepdf.com/Products/CutePDF/writer.asp)

## Postscript Viewing

- [Ghostscript](http://pages.cs.wisc.edu/~ghost/) and related viewers
  (GUI-front-ends) for viewing and manipulating Postscript graphics.
