Title: MarkDown Extensions in CoWeM
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

Using the [macro facility](doc:macros), CoWeM adds a number of
extensions that I have found useful in my own courses.

# Inline mark-up 

These mark-ups can be applied to text inside a paragraph: 

## Incremental Lists

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

## firstterm and emph


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


## Highlighting and Callouts

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




# Block mark-ups

Block mark-ups flag a block of
paragraphs for special formatting. Most of these are
begin by a  `\b`... command and terminated by a matching
`\e`command.  

## Examples

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


## Sidebars

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

## Slideshows

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


## Splitting into Columns


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





# Hiding and Revealing Text

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






# Graphics

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





## Code Markup

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






