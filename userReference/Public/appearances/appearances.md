Title: Modifying Appearances & Behaviors
Author: Steven J Zeil
Date: @docModDate@
TOC: yes

Appearances and behaviors of documents are controlled by

* CSS and Javascript files in the`styles/` directory,
* Graphics in the `graphics/` directory.

The general rule is that anything placed in a course's `styles/` or `graphics/` directory
will be copied in to the corresponding dirctory of the webite. If  you place a file there  that has
the same name as any of the CoWeM defaults, it  ill replace  the default  file.

# CSS

## Modifying All Documents in a Format

When a primary or secondary document is processed using a _format_, the generated web page loads
CSS from

`styles/md-`<i>format</i>`.css`
: This contains all of the default CSS for that format.  You can override the CSS for all documents
  in the website that use that _format_ by supplying your own version of that file.  Do  this only if 
  you need to override  all or most of the default CSS. You would probably
  want to build the website first, get a copy of the default from the generated `styles/` directory, 
  and modify that to make your own version. 
  
`styles/md-`<i>format</i>`-ext.css`
: This is loaded after the `styles/md-`<i>format</i>`.css` file, and  so can extend or override
  any of the default CSS. The default version of this file is empty. So, if you only want  to
  change  a few items in the CSS, supply your own version  of this file with just those CSS  items.
  


\bExample{Changing  the background  of  slides}

The default slide background can be
seen [here](../directories/directories__slides.html).

Suppose that we want to change that for all slides in a course.

The CSS for the _slides_ format is in files `styles/md-slides.css` and the
initially empty `styles/md-slides.css`. A look at the default
`styles/md-slides.css` shows that the background is set in this rule:

```
body
{
  font-size: 28pt;
  font-family: "Arial", "Helvetica", "sans-serif";
  margin-left: 4ex;
  margin-right: 4ex;
  width: 95%;
  max-width: 95%;
  overflow: false;

  background: #c6c6c6; /* Old browsers */
  background: -moz-linear-gradient(top, #0484ef 0%, #f7f7f7 9%, #c6c6c6 100%); /* FF3.6+ */
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#0484ef), color-stop(9%,#f7f7f7),  color-stop(100%,#c6c6c6)); /* Chrome,Safari4+ */
  background: -webkit-linear-gradient(top, #0484ef 0%,#f7f7f7 9%,#c6c6c6 100%); /* Chrome10+,Safari5.1+ */
  background: -o-linear-gradient(top, #0484ef 0%,#f7f7f7 9%,#c6c6c6 100%); /* Opera 11.10+ */
  background: -ms-linear-gradient(top, #0484ef 0%,#f7f7f7 9%,#c6c6c6 100%); /* IE10+ */
  background: linear-gradient(to bottom, #0484ef 0%,#f7f7f7 9%,#c6c6c6 100%); /* W3C */
}
```
The background clauses define a gradient. You can google
"HTML CSS gradient generators" and find any number of online tools that would
allow you to generate replacements for these.

For now, however, let's assume that we simply wanted to replace the background
by a solid, light-gray color.

Since only one CSS rule is affected, we could accomplish this by

1. Creating a `styles` directory in the course root directory, if we don't
   have one already.
   
2. In that directory, create a file `md-slides-ext.css` containing
   the single rule:
   
    ```  
    body
    {
     font-size: 28pt;
     font-family: "Arial", "Helvetica", "sans-serif";
     margin-left: 4ex;
     margin-right: 4ex;
     width: 95%;
     max-width: 95%;
     overflow: false;

     /*+*/background: #c0c0c0;/*-*/
    }
    ```

The next time that the course website is deployed, the new CSS will take effect.

\eExample


\bExample{Changing  the background  of directory pages (using a background graphic)}

The default directory background can be
seen [here](doc:library).

Suppose that we want to change that for all directory pages in a course to
[this](doc:directory330).  This uses a graphic that I created to suggest
a theme of "interacting objects" for my course in object-oriented
programming and design.  

The CSS for the _directory_ format is in files `styles/md-directory.css` and the
initially empty `styles/md-directory.css`. A look at the default
`styles/md-directory.css` shows that the background is set in this rule:

```
body    {
background: #cfeff9; /* Old browsers */
background: -moz-linear-gradient(left, #cfeff9 0%, #aae6f7 6%, #37c4ef 6%, #aae6f7 8%, #cfeff9 100%); /* FF3.6-15 */
background: -webkit-linear-gradient(left, #cfeff9 0%,#aae6f7 5%,#37c4ef 6%,#aae6f7 8%,#cfeff9 100%); /* Chrome10-25,Safari5.1-6 */
background: linear-gradient(to right, #cfeff9 0%,#aae6f7 5%,#37c4ef 6%,#aae6f7 8%,#cfeff9 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */

padding-left: 175px;
background-attachment: fixed;
}
```
Again, with only one rule affected, the easiest route is to override
that single rule in an `-ext.css` file:

1. Create `styles/` and `graphics/` directories in the course root directory,
   if we don't have them already.
   
2. In the `styles/` directory, create a file `md-directory-ext.css` containing
   the single rule:
   
    ```  
    body
    {
     background-color: #ebcda5;  
     background-image: url(../graphics/cs330-border2.png); 
     background-repeat: repeat-y;}
     padding-left: 175px;
    }
    ```
3. Place the file [cs330-border2.png](../../Directory/directory330/cs330-border2.png) 
   into the `graphics/` directory.


In practice, I would probably then also change some of the other CSS entries
to change the dark blues to shades of brown.

For visual consistency, I would also want to make similar changes to the
backgrounds of the _modules_ and _topics_ styles.  

\eExample


## Modifying Individual Documents

You can add additional CSS files  that affect only a single document 
by listing CSS URLs (relative or absolute) in `CSS:` lines in
the document's opening metadata.





# Graphics

There are a number of graphics files stored in the default `graphics/` directory. You can supply
your own versions of  any of these or augment  the common graphics pool by adding your own.

Most of the default graphics are

* The activity kind icons _activity_`-kind.png` mark the various activities in the  course  outline.
  Examples are `lab-kind.png` <img src="graphics:/lab-kind.png"/> and `lecture-kind.png` 
  <img src="graphics:/lecture-kind.png"/>.

* The navigation icons that appear at the top and bottom of most pages:
    <img src="graphics:/prev.png"/>, <img src="graphics:/home.png"/>,
    <img src="graphics:/slides.png"/>, etc.

\bExample{Adding a New Activity Kind}

Suppose that we wished to add a a new "fieldTrip" activity kind to our
course outline, so that we could designate activity items like this:

```
4. [ ](fieldTrip) Visit a local museum.
```

In fact, all that is necessary to support this is to


1. Create a `graphics/` directory in the course root directory,
   if we don't have it already.
   
2. Create an appropriate icon in a `graphics/fieldTrip-kind.png` file.  
   Typically, these would be
   about 32x32 pixels.


\eExample



# Javascript

When a primary or secondary document is processed using a _format_, the generated web page loads
Javascript from `styles/md-`<i>format</i>`.js`.
  
 

\bExample{Changing Keyboard  Bindings  for Advancing  Pages/Slides}

In the _slides_ and _pages_ formats, a reader can advance to
the next slide/page using the right-arrow key, down-arrow, or space keys.

This binding can be found in the `md-slides.js` file:

```
function doKeyPressed (e) {
   // alert ("key pressed " + e.keyCode);
   if (e.keyCode == 39 || e.keyCode == 40) {
       sshowforward(sshowControl0);
   } else 
   /*...*/
}

document.addEventListener("keydown", doKeyPressed, false);
```

To add the Enter key to that list, we would:

1. Create a `styles/` directory in the course root directory,
   if we don't have it already.
   
2. make a copy of the default `md-slides.js` in that directory.

3. Edit the `if` line above to 

        if (e.keyCode == 39 || e.keyCode == 40 || e.keyCode == 13) {


\eExample


\bExample{Click to Advance Slides}

Many people are used to clicking on a Powerpoint slide to advance it.
I dislike this behavior myself, as I often click-and-drag the mouse
across text to select/highlight it during lectures, and I often wind up 
advancing to the next slide by accident.

But if you want this behavior, it's fairly simple to add.

1. Create a `styles/` directory in the course root directory,
   if we don't have it already.
   
2. make a copy of the default `md-slides.js` in that directory.

3. Add to the end of that file:

        document.addEventListener("click", function(){
           sshowforward(sshowControl0);
        });

\eExample





