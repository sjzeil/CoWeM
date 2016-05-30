Title: Course Documents from MarkDown
Author: Steven J Zeil
Date: @docModDate@
TOC: yes



_Markdown_ is a popular language for easy production of electronic
documents.   For my courses, I use it as a source format from which
HTML pages and slides can be generated.

* I don't like typing in HTML directly.
* Editing in Word and saving as HTML results in grungy HTML that is
  hard to modify and unstable if any post-processing (e.g., XSL) is
  desired.
* Markdown can easily be used to produce many different output
  formats.
* Markdown processing is fast (which is why it is replacing LaTeX as
  my input format of choice for casual documents).

There are actually many different dialects of Markdown. After trying a
few, I have settled on
[MultMarkdown](http://fletcherpenney.net/multimarkdown/), (version 4).



# Standard Markdown Features

I won't attempt a full listing of Markdown features as those can be
found in the
[MultiMarkdown User's Guide](http://fletcher.github.io/MultiMarkdown-4/)
and
[Syntax Cheat Sheet](https://rawgithub.com/fletcher/human-markdown-reference/master/index.html).

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
Use backwards apostrophes to mark text as `pre-formatted code`.  Note,
however, that markdown is smart enough to recognize `this pattern' as
indicating quoting rather than code.

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

```

\eSidebar

Use backwards apostrophes to mark text as `pre-formatted code`.  Note,
however, that markdown is smart enough to recognize `this pattern' as
indicating quoting rather than code.

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


\bSidebar{60}

```

> Create block quotes by indenting text with a > character
> at the start of the line.


```

\eSidebar



> Create block quotes by indenting text with a > character
> at the start of the line.




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

The Markdown processor allows inline HTML to be entered directly when you
want something that is awkward or impossible to do in Markdown. So you can
also insert graphics by simply typing an `img` element <img src="lab.png"/>,
which may allow you easier access to styling options.  



\bSidebar{60}

```
There are  a couple of ways to introduce section headings. I tend to
use the technique of placing #, ##, ###, etc., at the start of
a paragraph. For example:


## Special text in Markdown


```

\eSidebar



There are  a couple of ways to introduce section headings. I tend to
use the technique of placing #, ##, ###, etc., at the start of
a paragraph. For example:

## Special text in Markdown

Markdown is compatible with some of the nicer Javascript-based tools
for dealing with mathematics and with programming code.

\bSidebar{60}

```
With the aid of [MathJax](http://www.mathjax.org/), you can enter
mathmatics in LaTeX notation, both for displayed

\\[ \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\]

and for inline \\( \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\)
mathematics. $a^b_c$ also works.

```

\eSidebar

With the aid of [MathJax](http://www.mathjax.org/), you can enter
mathmatics in LaTeX notation, both for displayed

\\[ \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\]

and for inline  \\( \sum_{i=0}^{n} i = \frac{n(n-1)}{2} \\) 
mathematics. $a^b_c$ also works.

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




# My Extensions

Of course, I can't leave well enough alone.  I have a number of
extensions that I use in my regular processing.

## Inline markup 

I have added a macro facility to pre-process files before they are
handed to the Markdown program. This allows me to add new markups to
the Markdown dialect that I use in my own documents.

### Incremental Lists

%define {\incr} {} {>>>}

\bSidebar

```
* Lists can be marked as incremental \incr 
* so that, during slide shows (see [below](#slides)),
* the list elements will be revealed one at a time.
```

\eSidebar

* Lists can be marked as incremental >>> 
* so that, during slide shows (see [below](#slides)),
* the list elements will be revealed one at a time.


%define {\\} {x} {\}

### firstterm and emph


\bSidebar{60}

```
I have a markup for \\{}firstterm{new terms} being
introduced for the first time in a document, and for
\\{}emph{very strong emphasis}.  
```

\eSidebar

More significant, I have a markup for \firstterm{new terms} being
introduced for the first time in a document, and for
\emph{very strong emphasis}.  


### Highlighting and Callouts

\bSidebar

```
I also have a set of markups for highlighting text
in \\{}hli{one}, \\{}hlii{two}, \\{}hliii{three},
and \\{}hliv{four colors}.
I have commands to generate callout numbers \\{}co1 ,
\\{}co2 , ..., \\{}co9 . Both the callout
numbers and color highlighting are generally used
in conjunction with
the code markups discussed [later](#post-processing).
```

\eSidebar

I also have a set of markups for highlighting text in \hli{one},
\hlii{two}, \hliii{three}, and \hliv{four colors}. I have commands to
generate callout numbers \co1 , \co2 , ..., \co9 . Both the callout
numbers and color highlighting are generally used in conjunction with
the code markups discussed [later](#post-processing).




## Block markups

I have introduced block markups that flag a block of
paragraphs for special formatting. Most of these are
begin by a  `\b`... command and terminated by a matching
`\e`command.  

### Examples

\bSidebar{60}

```
\\{}bExample{An Example of Examples}

For example, you can introduce numbered
examples.

\\{}eExample
```

\eSidebar


\bExample{An Example of Examples}

For example, you can introduce numbered
examples.

\eExample

As a general rule, these `\b`... and `\e`... commands should be entered on a
separate line, as if they constitute a separate paragraph. (This was required
in older versions of this software.  It's more relaxed in the current version,
but it is probably still a good idea if only because it improves the visibility
of the commands and helps you make sure that, for every `\b`... you have a
corresponding `\e`...    


%define {\rev} {x} {^^^}


### Sidebars

\bSidebar

```

\\{}bSidebar

I do like an occasional sidebar.

\\{}eSidebar
```

\eSidebar

I do like an occasional sidebar.


\bSidebar{33}

```
\\{}bSidebar{33}

Sidebars can be width-constrained
so that they will not extend
horizontally past a certain
percentage of the page width.
Any multiple of 5 can be given as
a maximum with. 33 and 67 are also
suppported.

If no value is
given, the default is 50.

If the sidebar contents have a
"natural" width less than the value
given, it will stay that wide.
The value is only used to set a maximum
width.

\\{}eSidebar`
```

\eSidebar


Sidebars can be width-constrained
so that they will not extend
horizontally past a certain
percentage of the page width.
Any multiple of 5 can be given as
a maximum with. 33 and 67 are also
suppported.

If no value is
given, the default is 50.

If the sidebar contents have a "natural" width less than the value
given, it will stay that wide. the value is only used to set a maximum
width.

### Slideshows

\bSidebar{40}

```
\\{}bSlideshow

\\{}bSlide

You can insert an internal "slideshow".

Clicking on the controls below...

\\{}eSlide

\\{}bSlide

...will move you from "slide" to "slide"...

\\{}eSlide

\\{}bSlide

...to "slide".

\\{}eSlide


\\{}eSlideshow
```

\eSidebar

\bSlideshow

\bSlide

You can insert an internal "slideshow".

Clicking on the controls below...

\eSlide

\bSlide

...will move you from "slide" to "slide"...

\eSlide

\bSlide

...to "slide".

\eSlide


\eSlideshow


### Splitting into Columns


\bSplitColumns

Every now and then, I like to
present two columns of
material, side-by-side.

\splitColumn

    // Do something
    void foo();

\eSplitColumns


\bSidebar{40}

\\{}bSplitColumns

Every now and then, I like to
present two columns of
material, side-by-side.

\\{}splitColumn

    // Do something
    void foo();

\\{}eSplitColumns


\eSidebar


\bSplitColumns

Like this, for example. The right column will wind up getting bumped
down below the left.

\splitColumn

    // Do something
    void foo();

\eSplitColumns


This is not a particularly robust formatting, however, and
things get ugly if the content is a bit too wide for the screen.





## Hiding and Revealing

\bSidebar{60}

```
\rev{}[click to reveal]

The three carets introduce an HTML5-style details 
element, which allows click-to-reveal behavior.

As it happens, neither of the Microsoft browsers (IE or Edge) 
support this element yet, so this is currently
simulated via Javascript. 
\rev{}
```

\eSidebar



^^^[click to reveal]

The three carets introduce an HTML5-style details element, which
allows click-to-reveal behavior.

The three carets introduce an HTML5-style details 
element, which allows click-to-reveal behavior.

As it happens, neither of the Microsoft browsers (IE or Edge) 
support this element yet, so this is currently
simulated via Javascript. 

^^^


\bSidebar{60}

```
A variation on this is the ability to load long code listings,
contained in a separate file providing both a link to the code as a
separate HTML page and a click-to-reveal button to expand the listing
in place.

\\{}loadlisting{unittest.h}

Files processed in this way can end with `.h`, `.cpp`, `.java`, or
`.listing` file names.
```

\eSidebar

A variation on this is the ability to load long code listings,
contained in a separate file providing both a link to the code as a
separate HTML page and a click-to-reveal button to expand the listing
in place.


\loadlisting{unittest.h}

A link is included to the unmodified source code file (to facilitate
downloading), so that file should be one of the support files for the
document set.  






## Graphics

\bSidebar{60}

```
As noted earlier,
Markdown already has support for
inserting graphics into a page. You
can place graphics inline
like this: ![lab icon](lab.png "lab"). 

You can also type HTML `img` tags directly, 
<img src="bookarray.png" align='right'/> which makes
it possible to use the "`align`" attribute
to position your graphics. 

<img src="bookLL.png" align='right'/>
A problem with this can occur when long
vertical graphics are
accompanied by relatively short text 
(particularly when viewed on a
wide screen).  

<img src="bookarray.png" align='right'/>The
floating graphics
can stack up on one another,
leading to a confusing layout.

```

\eSidebar

As noted earlier,
Markdown already has support for
inserting graphics into a page. You
can place graphics inline
like this: ![lab icon](lab.png "lab"). 

You can also type HTML `img` tags directly, 
<img src="bookarray.png" align='right'/> which makes
it possible to use the "`align`" attribute
to position your graphics. 



<img src="bookLL.png" align='right'/>
A problem with this can occur when long
vertical graphics are
accompanied by relatively short text 
(particularly when viewed on a
wide screen).  

<img src="bookarray.png" align='right'/>The
floating graphics
can stack up on one another,
leading to a confusing layout.


\bSidebar

```
<img src="bookarray.png" align='right'/> As
a fix for this, I provide a CSS style
class  `noFloat` with the attribute clear:both.  This
can be inserted as an empty paragraph:

<div class="noFloat"> </div>

<img src="bookLL.png" align='right'/> This
has the effect of forcing any following
 text and graphics to be positioned after the end of
 floating graphics, sidebars, or other floating content.

```

\eSidebar

<img src="bookarray.png" align='right'/> As a fix for this, I provide a CSS style class  `noFloat`
with the attribute clear:both.  This can be inserted as an empty paragraph:

<div class="noFloat"> </div>

<img src="bookLL.png" align='right'/> This has the effect of forcing any following text and
 graphics to be positioned after the end of floating graphics,
 sidebars, or other floating content.
 
However, I find that I often wind up using the same graphics sequences
over and over, so I have some shortcut commands. The command
`\\{}picOnRight(`_filename_`,`_pct_`)` is equivalent to

```
<div class="noFloat"></div>
<img src="filename.png" align="right"
     style="max-width: pct%/>"
```


It drops below any existing floating content, then inserts a floating
graphic on the right, reducing the graphic's size only if it exceeds a
width of _pct_%.

\bSidebar

```
\\{}picOnLeft(bookarray,25)

Similarly, I can insert graphics on the left
instead of on the right.
```

\eSidebar

\picOnLeft(bookarray,25)

Similarly, I can insert graphics on the left instead of on the right.

\bSidebar

```
\\{}centerPic(bookLL,50)

Or I can put them centered with nothing flowing around them.

```

\eSidebar

\centerPic(bookLL,50)

Or I can put them centered with nothing flowing around them.




## Post-processing

After the markdown processor has generated an HTML page, I do some
additional processing.  Major steps done are


1. A standard footer (from `footer.xml`) is added to the bottom of the
   page. This generally adds a link to the course home URL and either
   an email link or an interface to one of my course Forums.

2. HTML5 `<details>` elements are converted to a form that current
   browsers can handle.

3. Links are added to the MathJax and HighlightJS Javascript packages
   for supporting mathematics and code highlighting.

4. Links are added to my standard CSS pages. (Additional CSS can be
   added in the document headers. Refer to the markdown documentaiton
   for details.)

5. Special code markups are processed, as described [below](#code-markup).

6. If slides or paged output has been requested, the page is divided
   into slides/pages as described next.



### Slides

Slides and paged content are provided using the
[Slidy](http://www.w3.org/Talks/Tools/Slidy2/Overview.html)
package. Although most markdown processors have their own support for
doing this, the rules for dividing things into slides/pages are not
always convenient.

My own processing creates a new slide at every #, ##, ###, or ####
header and at every hrule (produced by a paragraph containing only 3 or more
consecutive hyphens). For paged documents, a new page is created at
every # or ## header and at every hrule.


You can see this same document as [slides](testDoc__slidy.html) or
divided into [pages](testDoc__pages.html).

The macro processor I use for pre-processing documents has support for
conditional text, allowing a single source document to serve for both
abbreviated slides and a lengthier full-text version.


%define {\macro} {x} {%}

\bSidebar

```
\macro{}if _slides
> This text, for example, will only appear in slides.
\macro{}endif

```
`\macro{}if` _`printable`

```
> This text, on the other hand, will not appear in slides
> but appears in the HTML and Pages formats.
\macro{}endif
```

\eSidebar

%if _slides
> This text, for example, will only appear in slides.
%endif

%if _printable
> This text, on the other hand, will not appear in slides but appears in
> the HTML and Pages formats.
%endif



### Code Markup

To facilitate discussing programming code, I have some special markups
that can be inserted into C++ or Java code as comments. This way it
does not affect the syntactic integrity of the code. You can load it
into a programming editor or run it through a compiler with no ill
effects. But when the HTML is generated, these comments are converted
to appropriate markup.

If a `/* */` style comment contains only a single digit number, e.g.,
`/*`1`*/`, it is converted to the corresponding callout symbol, which
can then be discussed in the text:




> **Anatomy of a for loop**
> 
> ```c++
> for (int i = 0/*1*/; i < n/*2*/; ++i/*3*/)
>    cout << a[i] << endl; /*4*/
> ```
> 
> \co1  represents the initialization of the loop.
> 
> \co2  is the loop condition.
> 
> \co3  is the repeat action.
> 
> \co4 is the loop body.
 

Similarly, if a `/* */` style comment contains only +1, +2, +3, or +4,
this turns on a background color for highlighting. This highlighting
is turned off with -1 , -2, -3, and -4, respectively.  Also, + and -
by themselves are shorthand for +1 and -1.

 

> **Anatomy of a for loop**
> 
> ```c++
> for (/*+*/int i = 0/*-*/; /*+2*/i < n/*-2*/; /*+3*/++i/*-3*/)
>    /*+4*/cout << a[i] << endl; /*-4*/
> ```
>  
> Here we see the \hli{initialization}, \hlii{condition},
> \hliii{repeat action}, and \hliv{body} components of the loop.






