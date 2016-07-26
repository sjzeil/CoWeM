
  Macros for use in pre-processing markdown files

  >>> causes lists (or other HTML elements) to be revealed
  incrementally in slides

%ifdef _slides
%define {>>>} <> {<span class="incremental"> </span>

}
%else
%define {>>>} <> {

}
%endif


Assorted text decorations (from docbook, originally)

%define <\firstterm> {newterm} {<span class="firstterm">newterm</span>}
%define <\emph> {newterm} {<span class="emph">newterm</span>}
%define <\type> {newterm} {<span class="type">newterm</span>}
%define <\varname> {newterm} {<span class="varname">newterm</span>}
%define <\code> {newterm} {<span class="code">newterm</span>}
%define <\function> {newterm} {<span class="function">newterm</span>}
%define <\file> {newterm} {<span class="file">newterm</span>}
%define <\filename> {newterm} {<span class="file">newterm</span>}
%define <\command> {newterm} {<span class="command">newterm</span>}
%define <\replaceable> {newterm} {<span class="replaceable">newterm</span>}
%define <\sout> <strikePhrase> {~~strikePhrase~~}
%define <\anchor> <anchorID> {<span id='anchorID'></span>}



  Conversion for older docbook olinks as translated into LaTeX
%define <\olink> {document,linkedText} {[linkedText](../document/)}





  Highlighting
%define <\hli> (highlightedText) {<span class='hli'>highlightedText</span>}
%define <\hlii> (highlightedText) {<span class='hlii'>highlightedText</span>}
%define <\hliii> (highlightedText) {<span class='hliii'>highlightedText</span>}
%define <\hliv> (highlightedText) {<span class='hliv'>highlightedText</span>}

%ifdef _epub
%define <\hli> (highlightedText) {<span class='hli'>&#x2460;&#8594;highlightedText&#8592;&#x2460;</span>}
%define <\hlii> (highlightedText) {<span class='hlii'>&#x2461;&#8594;highlightedText&#x2461;&#x27e9;</span>}
%define <\hliii> (highlightedText) {<span class='hliii'>&#x2462;&#8594;highlightedText&#x2462;&#x27e9;</span>}
%define <\hliv> (highlightedText) {<span class='hliv'>&#x2463;&#8594;highlightedText&#x2463;&#x27e9;</span>}
%endif



Callout numbers
%define <\co1> <> [<span>&#x2780;</span>]
%define <\co2> <> [<span>&#x2781;</span>]
%define <\co3> <> [<span>&#x2782;</span>]
%define <\co4> <> [<span>&#x2783;</span>]
%define <\co5> <> [<span>&#x2784;</span>]
%define <\co6> <> [<span>&#x2785;</span>]
%define <\co7> <> [<span>&#x2786;</span>]
%define <\co8> <> [<span>&#x2787;</span>]
%define <\co9> <> [<span>&#x2788;</span>]

Image processing:

%define <\bPicOnRight> (file,pctwidth) {<div class="noFloat">&nbsp;</div><img src="file.png" style="float: right; max-width: pctwidth%;"/>}
%define <\picOnRight> (file,pctwidth) {<div class="noFloat">&nbsp;</div><img src="file.png" style="float: right; max-width: pctwidth%;"/>}
%define <\bPicOnLeft> (file,pctwidth) {<div class="noFloat">&nbsp;</div><img src="file.png" style="float: left; max-width: pctwidth%;"/>}
%define <\picOnLeft> (file,pctwidth) {<div class="noFloat">&nbsp;</div><img src="file.png" style="float: left; max-width: pctwidth%;"/>}
%define <\centerPic> (file,pctwidth) {<div class="noFloat">&nbsp;</div><div style="text-align: center'"><img src="file.png" align="center" style="max-width: pctwidth%;"/></div>}
%define <\icon> (file,linkURL) {<span class="linkedIcon"><a href="linkURL" target="_blank"><img src="file"/></a></span>}
%define <\noFloat> <> {<div class="noFloat">&nbsp;</div>}

%define <\ePicOnRight> () <>
%define <\ePicOnLeft> () <>



Listings:
%define <\loadlisting> (sourceFile) (<cwm tag='longlisting' file='sourceFile'/>Loading code from sourceFile<cwm tag='/longlisting'/>)

%ifdef _epub
%define <\loadlisting> (sourceFile) (<cwm tag='longlisting' file='sourceFile'/>Loading code from sourceFile<cwm tag='/longlisting'/>)
%endif

<%define <\linklisting> <filePath> <[`filepath`](filePath.html]>



%define {\bExample} {theTitle} {<cwm tag="example" title="theTitle"/>}
%define {\eExample} {} {<cwm tag="/example"/>

}


Click to reveal
%define {^^^} {summaryText} {<cwm tag="details" summary="summaryText"/>}
%define {^^^} {} {<cwm tag="/details"/>

}

Slideshow

%define <\bSlideshow> () {<cwm tag="slideshow"/>}
%define <\eSlideshow> () {<cwm tag="/slideshow"/>

}
%define <\bSlide> () {<cwm tag="slideshowslide"/>

}
%define <\eSlide> () {<cwm tag="/slideshowslide"/>

}


Sidebars

%define <\bSidebar> () {<cwm tag="sidebar" width="50"/>

}

%define <\bSidebar> (sidebarWidth) {<cwm tag="sidebar" width="sidebarWidth"/>}

%define <\eSidebar> () {<cwm tag="/sidebar"/>

}


Columns

%define <\bSplitColumns> () {<cwm tag="splitColumns"/><cwm tag="leftColumn"/>

}
%define <\eSplitColumns> () {<cwm tag="/rightColumn"/><cwm tag="/splitColumns"/>

} 
%define <\splitColumns> () {<cwm tag="/leftColumn"/><cwm tag="rightColumn"/>

} 
%define <\splitColumn> () {<cwm tag="/leftColumn"/><cwm tag="rightColumn"/>

} 


%define <\Implies> <> [<span>&#x21D2;</span>]


Generating the submission button for assignments:

%define <\submitButton> <submissionControlFile> {<form><div><input type="button" value="Submit this assignment" onclick="window.open('https://www.cs.odu.edu/~zeil/submit/submit.html?asstinfo=submissionControlFile')"/></div></form>}

%define <\gradeButton> <submissionControlFile> {<form><div><input type="button" value="View Grade Report" onclick="window.open('https://www.cs.odu.edu/~zeil/submit/submit.html?asstinfo=submissionControlFile')"/></div></form>}


