Title: Conditional Text
Author: Steven J Zeil
Date: @docModDate@
TOC: yes


The macro processor provides support for
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

