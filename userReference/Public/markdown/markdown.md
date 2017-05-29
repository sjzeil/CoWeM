Title: Course Documents from MarkDown
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

_Markdown_ is a popular language for easy production of electronic
documents.   For course development, I find that it serves well as a
way to prepare high-quality documents quickly that can be easily maintained and
modified. 

* I don't like typing in HTML directly.
* Editing in Word and saving as HTML results in grungy HTML that is
  hard to modify and unstable if any post-processing (e.g., XSL) is
  desired.
* Markdown can easily be used to produce many different output
  formats.
* Markdown processing is fast (which is why it is replacing LaTeX as
  my input format of choice for casual documents).
* Word, PowerPoint, and other binary file formats do not lend themselves
  well either to any post-processing nor to tracking using conventional
  version control system. 

There are actually many different dialects of Markdown. CoWeM uses
[PegDown](https://github.com/sirthias/pegdown), because it is available as
portable library that can be run on virtually any system. 
For reference purposes, PegDown is  probably closest to 
[MultiMarkdown](http://fletcherpenney.net/multimarkdown/), which has a
convenient
[syntax cheat sheet](https://rawgit.com/fletcher/human-markdown-reference/master/index.html).

# Markdown: Basic Text 
 
I won't attempt a full listing of Markdown features as those can be
found in the documents already discussed above


\bSidebar{60}

```
But the essence of Markdown is that it appears as "natural" ASCII text
in its source format, with relatively obvious markings.

For example, paragraphs are separated simply by leaving a blank line
between them.

* And bulleted lists are indicated by paragraphs beginning
  with * or  -.
    * We can have sublists
* And so on.

    Four spaces of indentation creates a
	paragraph within a list item.

* And then we con continue with the list.


Numbered lists are similarly natural.

1. One thing ...
2. ...follows another.
1. But if you don't actually like keeping count, or if
   you later rearrange your list items, the actual
   output will be numbered appropriately.
```

\eSidebar




But the essence of Markdown is that it appears as "natural" ASCII text
in its source format, with relatively obvious markings.

For example, paragraphs are separated simply by leaving a blank line
between them.

* And bulleted lists are indicated by paragraphs beginning
  with * or  -.
    * We can have sublists
* And so on.

    Four spaces of indentation creates a
	paragraph within a list item.

* And then we can continue with the list.


Numbered lists are similarly natural.

1. One thing ...
2. ...follows another.
1. But if you don't actually like keeping count, or if
   you later rearrange your list items, the actual
   output will be numbered appropriately.

\bSidebar{60}

```

Text can be marked with asterisks to indicate *emphasis* or **strong
emphasis**. You can also use underscores to indicate _emphasis_ or
__strong emphasis__.  Use a pair of tildes to ~~strike out~~ text.

```

\eSidebar

Text can be marked with asterisks to indicate *emphasis* or **strong
emphasis**. You can also use underscores to indicate _emphasis_ or
__strong emphasis__. Use a pair of tildes to ~~strike out~~ text.


\bSidebar{60}

```
Quotations are typed using the normal 
keyboard 'apostrophes'
or "quotation marks".  The Markdown
engine will typeset these using the
distinct left- and right-leaning
versions but you don't want to
type them `this way'. It
``does not work''. 
```

\eSidebar

Quotations are typed using the normal 
keyboard 'apostrophes'
or "quotation marks".  The Markdown
engine will typeset these using the
distinct left- and right-leaning
versions but you don't want to
type them `this way'. It
``does not work''. 



\bSidebar{60}

```
Use backwards apostrophes to mark text as `pre-formatted code`.  

You can set entire paragraphs as code by placing them between two
lines containing three
back-apostrophes.

```

<pre>```</pre>
```
for (int i = 0; i < 100; ++i)
   cout << i << ": " << a[i] << endl; 
```
<pre>```</pre>

```
You can achieve the same result by simply indenting the lines by 4 or
more spaces.

    for (int i = 0; i < 100; ++i)
       cout << i << ": " << a[i] << endl; 


(Notice the general pattern of treating 4 spaces of
 indentation as significant. Anything less is considered accidental and
  ignored.)

```

\eSidebar

Use backwards apostrophes to mark text as `pre-formatted code`.  

You can set entire paragraphs as code by placing them between two
lines containing three
back-apostrophes.

```
for (int i = 0; i < 100; ++i)
   cout << i << ": " << a[i] << endl; 
```

You can achieve the same result by simply indenting the lines by 4 or
more spaces.

    for (int i = 0; i < 100; ++i)
       cout << i << ": " << a[i] << endl; 




(Notice the general pattern of treating 4 spaces of
indentation as significant. Anything less is considered accidental and
  ignored.)


> **Note on C++ & Java code**: There is a glitch in the Markdown processor
> caused by a conflict between the code typesetting conventions and
> the ability to insert HTML elements directly into the Markdown
> source. Code like this:
>
>        vector<int> v;
>
> will not be typeset properly if the backward apostrophes
> technique is used. The
> `<int>` will be interpreted as a (bad) HTML tag and passed directly
> through into the HTML output documents.
>
> * And because there is no balancing `</int>` tag, this eventually
>   results in an error during document processing.
>
> You **can**, however, typeset that code using the 4-space
> indentation markup instead of three backwards apostrophes.
>
> Consequently the indentation style is heavily recommended for
> C++ and Java code.









\bSidebar{60}

```

> Create block quotes by indenting text with a > character
> at the start of the line.


```

\eSidebar



> Create block quotes by indenting text with a > character
> at the start of the line.


# Links and Graphics


\bSidebar{60}

```
You can have [links](http://www.cs.odu.edu/~zeil).

You can add graphics:
![lab icon](lab.png "lab")

The Markdown processor allows inline HTML to be
entered directly when you want something that is
awkward or impossible to do in Markdown. So you can
also insert graphics by simply typing an `img`
element <img src="lab.png"/>, which may allow you
easier access to styling options.  
```

\eSidebar

You can have [links](http://www.cs.odu.edu/~zeil).

You can add graphics:
![lab icon](lab.png "lab")

The Markdown processor allows inline HTML to be
entered directly when you want something that is
awkward or impossible to do in Markdown. So you can
also insert graphics by simply typing an `img`
element <img src="lab.png"/>, which may allow you
easier access to styling options.  

# Section Headings

\bSidebar{60}

```
# Section Headings

There are  a couple of ways to introduce section headings. I tend to
use the technique of placing #, ##, ###, etc., at the start of
a paragraph. For example:


## A Subsection Heading


```

\eSidebar



There are  a couple of ways to introduce section headings. I tend to
use the technique of placing #, ##, ###, etc., at the start of
a paragraph. For example:

## A Subsection Heading


# Mathematics


Markdown is compatible with some of the nicer Javascript-based tools
for dealing with mathematics and with programming code.

\bSidebar{60}

```
With the aid of [MathJax](http://www.mathjax.org/), you can enter
mathmatics in LaTeX notation, both for displayed

\\[ \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\]

and for inline \\( \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\)
mathematics. $a^b_c$ also works. To put a "real" dollar
sign into your text,
put a backslash in front: my \$.02 worth. 

```

\eSidebar

With the aid of [MathJax](http://www.mathjax.org/), you can enter
mathmatics in LaTeX notation, both for displayed

\\[ \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\]

and for inline  \\( \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\) 
mathematics. $a^b_c$ also works. To put a "real" dollar sign into your text,
put a backslash in front: my \$.02 worth.

The rendering of LaTeX mathematics can be turned off as a document set option.
It is also possible to switch to [AsciiMath](http://asciimath.org/)
instead of LaTeX.

Take note of the double-backslashes used to surround the mathematics
regions. The normal convention in LaTeX would be to use
<span>\\</span>(...<span>\\</span>)
and
<span>\\</span>[...<span>\\</span>]. But the Markdown processor treats
backslashes oddly:

* If followed by a space or letter, a backslash is just a backslash.
* If followed by a punctuation character, a  backslash must be doubled.

This affects not only the 
<span>\\</span>(...<span>\\</span>)
and
<span>\\</span>[...<span>\\</span>], but also selected uses of \
within LaTeX, most notable the use of set brackets { } and the \\\\ used
to signal a line break in tables, arrays, or multi-line equations.


\bSidebar{60}

```
\\[ \begin{align}
s & = \\{ i | i \in \cal{N} \; \wedge \; i \bmod 2 = 0 \\} \\\\
  & = \\{ 2i | i \in \cal{N} \\}
\end{align} \\]


```

\eSidebar


\\[ \begin{align}
s & = \\{ i | i \in \cal{N} \; \wedge \; i \bmod 2 = 0 \\} \\\\
  & = \\{ 2i | i \in \cal{N} \\}
\end{align} \\]



# Syntax Highlighting of Source Code



\bSidebar{60}

```
Using [HighlightJS](http://highlightjs.org/),
syntax highlighting is automatically
be added to code:
```

<pre>```c++</pre>
```
class Book {
 public:
  std::string title;
  int numAuthors;
  std::string isbn;
  Publisher publisher;

  static const int maxAuthors = 12;
  Author* authors;  // array of Authors
};
```
<pre>```</pre>

\eSidebar

Using [HighlightJS](http://highlightjs.org/), syntax highlighting
is automatically
be added to code:

```c++
class Book {
 public:
  std::string title;
  int numAuthors;
  std::string isbn;
  Publisher publisher;

  static const int maxAuthors = 12;
  Author* authors;  // array of Authors
};
```

The Markdown convention is to put the programming language name after
the opening three back-apostrophes. As it happens, HighlightJS
auto-detects the programming language being used, so you can put
anything there that you like.


# Metadata

A Markdown document can begin with one or more lines of _metadata_ which
provide data about the document. Typically these affect the document
headers and appearance.

Each metadata field or item appears as a single line of text, beginning
with a field name, then a colon, then the value of the field.

An empty line separates the metadata from the start of the Markdown text proper.
For example, the [Markdown source of this document](markdown.md) begins with

    Title: Course Documents from MarkDown
    Author: Steven J Zeil
    Date: @docModDate@
    TOC: yes

    _Markdown_ is a popular language for easy production of electronic
    /*...*/

The metadata fields supported in CoWeM are

Title
: Provides a title for the document.  This appears in the header and
  as the `<title>` of any generated HTML.  

Author
: Name of the author. This appears in the header.

Date
: Appears in the header as "Last modified: ...".  The special value
<span><tt> @</tt></span>`docModDate@` is replaced by the

    * The last change checked into the repository if the course
	   document is part of a `git` repository, or
    * the last modification date of the Markdown source
       file, if that file is not within a `git` repository.
  
TOC
: "yes" or "true" indicates that a table of contents should be generated at
  the start of the document.
  
CSS
: Followed by a URL, links to a CSS file to adjust the document's appearance.
  This field may be specified multiple times to add more than one CSS file.
  
Macros
: Followed by a file path, adds that file to the set of [macros](doc:macros)
  used to process the document. This field may be specified multiple times to
  add more than one macro set.
 

You can actually add other metadata fields, but the above are the only ones
that affect document processing at present.

All metadata fields can be inserted into your document text, however,
by surrounding the field name with '@' characters.

\bSidebar{60}


`For example, `<span><tt> @</tt></span>`Author@ last modified this`
`document on `<span><tt> @</tt></span>`Date@.`


\eSidebar

For example, @Author@ last modified this
document on @Date@.
 
 
 
