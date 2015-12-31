
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

%ifdef _html
%define <\firstterm> {newterm} {<span class="firstterm" markdown="1">newterm</span>}
%define <\emph> {newterm} {<span class="emph" markdown="1">newterm</span>}
%define <\type> {newterm} {<span class="type" markdown="1">newterm</span>}
%define <\varname> {newterm} {<span class="varname" markdown="1">newterm</span>}
%define <\code> {newterm} {<span class="code" markdown="1">newterm</span>}
%define <\function> {newterm} {<span class="function" markdown="1">newterm</span>}
%define <\file> {newterm} {<span class="file" markdown="1">newterm</span>}
%define <\filename> {newterm} {<span class="file" markdown="1">newterm</span>}
%define <\command> {newterm} {<span class="command" markdown="1">newterm</span>}
%define <\replaceable> {newterm} {<span class="replaceable" markdown="1">newterm</span>}
%define <\sout> <strikePhrase> {<span class='strike' markdown='1'>strikePhrase</span>}
%define <\anchor> <anchorID> {<span id='anchorID'></span>}
%endif


%ifdef _epub
%define <\firstterm> {newterm} {<span class="firstterm" markdown="1">newterm</span>}
%define <\emph> {newterm} {<span class="emph" markdown="1">newterm</span>}
%define <\type> {newterm} {<span class="type" markdown="1">newterm</span>}
%define <\varname> {newterm} {<span class="varname" markdown="1">newterm</span>}
%define <\code> {newterm} {<span class="code" markdown="1">newterm</span>}
%define <\function> {newterm} {<span class="function" markdown="1">newterm</span>}
%define <\file> {newterm} {<span class="file" markdown="1">newterm</span>}
%define <\filename> {newterm} {<span class="file" markdown="1">newterm</span>}
%define <\command> {newterm} {<span class="command" markdown="1">newterm</span>}
%define <\replaceable> {newterm} {<span class="replaceable" markdown="1">newterm</span>}
%define <\sout> <strikePhrase> {<span class='strike' markdown='1'>strikePhrase</span>}
%define <\anchor> <anchorID> {<span id='anchorID'></span>}
%endif



  Conversion for older docbook olinks as translated into LaTeX
%ifdef _html
%define <\olink> {document,linkedText} {[linkedText](../document/)}
%else
%define <\olink> {document,linkedText} {\doclink{document}{linkedText}}
%endif


%ifdef _epub
%define <\olink> {document,linkedText} {[linkedText](../document/)}
%else
%define <\olink> {document,linkedText} {\doclink{document}{linkedText}}
%endif




  Highlighting
%ifdef _html
%define <\hli> (highlightedText) {<span class='hli'>highlightedText</span>}
%define <\hlii> (highlightedText) {<span class='hlii'>highlightedText</span>}
%define <\hliii> (highlightedText) {<span class='hliii'>highlightedText</span>}
%define <\hliv> (highlightedText) {<span class='hliv'>highlightedText</span>}
%endif

%ifdef _epub
%define <\hli> (highlightedText) {<span class='hli'>&#x2460;&#8594;highlightedText&#8592;&#x2460;</span>}
%define <\hlii> (highlightedText) {<span class='hlii'>&#x2461;&#8594;highlightedText&#x2461;&#x27e9;</span>}
%define <\hliii> (highlightedText) {<span class='hliii'>&#x2462;&#8594;highlightedText&#x2462;&#x27e9;</span>}
%define <\hliv> (highlightedText) {<span class='hliv'>&#x2463;&#8594;highlightedText&#x2463;&#x27e9;</span>}
%endif



Callout numbers
%ifdef _html
%define <\co1> <> [<span>&#x2780;</span>]
%define <\co2> <> [<span>&#x2781;</span>]
%define <\co3> <> [<span>&#x2782;</span>]
%define <\co4> <> [<span>&#x2783;</span>]
%define <\co5> <> [<span>&#x2784;</span>]
%define <\co6> <> [<span>&#x2785;</span>]
%define <\co7> <> [<span>&#x2786;</span>]
%define <\co8> <> [<span>&#x2787;</span>]
%define <\co9> <> [<span>&#x2788;</span>]
%else

%ifdef _epub
%define <\co1> <> [<span>&#x2780;</span>]
%define <\co2> <> [<span>&#x2781;</span>]
%define <\co3> <> [<span>&#x2782;</span>]
%define <\co4> <> [<span>&#x2783;</span>]
%define <\co5> <> [<span>&#x2784;</span>]
%define <\co6> <> [<span>&#x2785;</span>]
%define <\co7> <> [<span>&#x2786;</span>]
%define <\co8> <> [<span>&#x2787;</span>]
%define <\co9> <> [<span>&#x2788;</span>]

%else

%define <\co1> <> [\ding{192}]
%define <\co2> <> [\ding{193}]
%define <\co3> <> [\ding{194}]
%define <\co4> <> [\ding{195}]
%define <\co5> <> [\ding{196}]
%define <\co6> <> [\ding{197}]
%define <\co7> <> [\ding{198}]
%define <\co8> <> [\ding{199}]
%define <\co9> <> [\ding{200}]
%endif
%endif

Image processing:

%ifdef _html
%define <\bPicOnRight> (file,pctwidth) {<div class="noFloat"> </div><img src="file.png" style="float: right; max-width: pctwidth%;"/>}
%define <\picOnRight> (file,pctwidth) {<div class="noFloat"> </div><img src="file.png" style="float: right; max-width: pctwidth%;"/>}
%define <\bPicOnLeft> (file,pctwidth) {<div class="noFloat"> </div><img src="file.png" style="float: left; max-width: pctwidth%;"/>}
%define <\picOnLeft> (file,pctwidth) {<div class="noFloat"> </div><img src="file.png" style="float: left; max-width: pctwidth%;"/>}
%define <\centerPic> (file,pctwidth) {<div class="noFloat"> </div><div style="text-align: center'"><img src="file.png" align="center" style="max-width: pctwidth%;"/></div>}
%define <\icon> (file,linkURL) {<span class="linkedIcon"><a href="linkURL" target="_blank"><img src="file"/></a></span>}
%define <\noFloat> <> {<div class="noFloat"> </div>}
%else
%ifdef _epub
%define <\bPicOnRight> (file,pctwidth) {<div class="noFloat"> </div><div><img src="file.png" style="float: right; max-width: pctwidth%;"/></div>}
%define <\picOnRight> (file,pctwidth) {<div class="noFloat"> </div><div><img src="file.png" style="float: right; max-width: pctwidth%;"/></div>}
%define <\bPicOnLeft> (file,pctwidth) {<div class="noFloat"> </div><div><img src="file.png" style="float: left; max-width: pctwidth%;"/></div>}
%define <\picOnLeft> (file,pctwidth) {<div class="noFloat"> </div><div><img src="file.png" style="float: left; max-width: pctwidth%;"/></div>}
%define <\centerPic> (file,pctwidth) {<div class="noFloat"> </div><div style="text-align: center'"><img src="file.png" align="center" style="max-width: pctwidth%;"/></div>}
%define <\icon> (file,linkURL) {<span class="linkedIcon"><a href="linkURL" target="_blank"><img src="file"/></a></span>}
%define <\noFloat> <> {<div class="noFloat"/>}
%else
%define <\bPicOnRight> (file,pctwidth) <\begin{picOnRight}[pctwidth]{file}>
%define <\bPicOnLeft> (file,pctwidth) <\begin{picOnLeft}[pctwidth]{file}>
%define <\centerPic> (file,pctwidth) <\n\n\centerPic[pctwidth]{file}\n\n>
%endif
%ifdef _html
%define <\ePicOnRight> () <>
%define <\ePicOnLeft> () <>
%else
%ifdef _epub
%define <\ePicOnRight> () <>
%define <\ePicOnLeft> () <>
%else
%define <\ePicOnRight> () <\end{picOnRight}>
%define <\ePicOnLeft> () <\end{picOnRight}>
%endif
%endif
%endif



Listings:
%ifdef _html
%define <\loadlisting> (sourceFile) (

<longlisting file='sourceFile.html'/>

)
%else
%ifdef _epub
%define <\loadlisting> (sourceFile) (

</p><longlisting file='sourceFile.html'/><p>

)
%define <\loadlisting> (sourceFile) {

[sourceFile](sourceFile.html)

}
%endif
%endif

%ifdef _html
%define {\bExample} {theTitle} {</p><example markdown="1"><title
 markdown="1">theTitle</title><p>}
%define {\eExample} {} {</p></example><p>}
%endif
%ifdef _epub
%define {\bExample} {theTitle} {</p><example markdown="1"><title
 markdown="1">theTitle</title><p>}
%define {\eExample} {} {</p></example><p>}
%endif

Click to reveal
%ifdef _html
%define {^^^} {summaryText} {</p><details markdown="1">
<summary markdown="1">summaryText</summary><div markdown="1"><p>}
%define {^^^} {} {</p></div></details><p>}
%else
%ifdef _epub
%define {^^^} {summaryText} {</p><details markdown="1">
<summary markdown="1">summaryText</summary><div markdown="1"><p>}
%define {^^^} {} {</p></div></details><p>}
%else

%define {^^^} {summaryText} {

---

summaryText

}
%define {^^^} {} {

---

}
%endif
%endif


Sidebars

%ifdef _html
%define <\bSidebar> (sidebarWidth) {<div class="noFloat"> </div><div class="sidebar pctsidebarWidth" markdown="1">
}
%define <\bSidebar> () {<div class="noFloat"> </div><div class="sidebar pct50" markdown="1">
}
%define <\eSidebar> () {
</p></div><p>
} 
%else
%ifdef _epub
%define <\bSidebar> (sidebarWidth) {<div class="noFloat"> </div><div class="sidebar pctsidebarWidth" markdown="1">
}
%define <\bSidebar> () {<div class="noFloat"> </div><div class="sidebar pct50" markdown="1">
}
%define <\eSidebar> () {
</p></div><p>
} 
%endif
%endif


Columns

%ifdef _html
%define <\bSplitColumns> () {<div markdown="1"><div class="leftColumn" markdown="1">
}
%define <\eSplitColumns> () {
</p></div></div><p>
} 
%define <\splitColumns> () {
</p></div><div class="rightColumn" markdown="1"><p>
} 
%define <\splitColumn> () {
</p></div><div class="rightColumn" markdown="1"><p>
} 
%else
%ifdef _epub
%define <\bSplitColumns> () {<div markdown="1"><div class="leftColumn" markdown="1">
}
%define <\eSplitColumns> () {
</p></div></div><p>
} 
%define <\splitColumns> () {
</p></div><div class="rightColumn" markdown="1"><p>
} 
%define <\splitColumn> () {
</p></div><div class="rightColumn" markdown="1"><p>
} 
%endif
%endif


%define <\Rightarrow> <> [<span>&#x21D2;</span>]


Generating the submission button for assignments:

%define <\submitButton> <submissionControlFile> {<form><p><input type="button" value="Submit this assignment" onclick="window.open('https://www.cs.odu.edu/~zeil/submit/submit.html?asstinfo=submissionControlFile')"/></p></form>}

%define <\gradeButton> <submissionControlFile> {<form><p><input type="button" value="View Grade Report" onclick="window.open('https://www.cs.odu.edu/~zeil/submit/submit.html?asstinfo=submissionControlFile')"/></p></form>}

}
