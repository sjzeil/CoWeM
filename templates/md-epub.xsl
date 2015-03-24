<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:import href="bblink.xsl"/>


  <xsl:output method="xml" encoding="utf-8"/>
  <!-- xsl:output 
      doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
      doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      / -->

  <xsl:param name="doc" select="'document'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="MathJaxURL" select="'../../styles/MathJax'"/>
  <xsl:param name="highlightjsURL" select="'../../styles/highlight.js'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>
  <xsl:param name="baseURL" select="'../../'"/>


  <xsl:output name="math"
	      method="text"
	      encoding="utf-8"
	      /> 

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
    <xsl:text>
</xsl:text>
    <xsl:result-document 
	href="{$doc}__epub.math.tex"
	format="math">
      <xsl:apply-templates select="*|text()" mode="math"/>
    </xsl:result-document>
  </xsl:template>

  <xsl:template match="meta">
    <xsl:if test="@charset != ''">
      <xsl:copy>
	<xsl:copy-of select='@* except @charset'/>
	<xsl:apply-templates select="*|text()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="../md-epub.css" />
	  <meta name="viewport" content="width=device-width, initial-scale=1"/>	
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <div class="titleblock">
	<h1>
	  <xsl:value-of select="/html/head/title/text()"/>
	</h1>
	<h2>
	  <xsl:value-of select="/html/head/meta[@name='author']/@content"/>
	</h2>
	<xsl:if test="/html/head/meta[@name='date']">
	  <p>
	    <xsl:text>Last modified: </xsl:text>
	    <xsl:value-of select="/html/head/meta[@name='date']/@content"/>
	  </p>
	</xsl:if>
      </div>

      <xsl:if test="/html/head/meta[@name='toc']">
	<div class="toc">
	  <xsl:text>Contents:</xsl:text>
	  <xsl:apply-templates select="h1 | h2" mode="toc"/>
	</div>
      </xsl:if>

      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1">
    <xsl:copy>
      <xsl:copy-of select="@* except @id"/>
      <xsl:call-template name="copyID"/>
      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="copyID">
    <xsl:if test="@id != ''">
      <xsl:attribute name="id">
	<xsl:value-of select="translate(@id, ':', '-')"/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>

  <xsl:template match="h1" mode="toc">
    <div class="toc-h1">
      <a href="#{translate(@id,':','-')}">
	<xsl:value-of select="1 + count(preceding-sibling::h1)"/>
	<xsl:text>. </xsl:text>
	<xsl:apply-templates select="*|text()"/>
      </a>
    </div>
  </xsl:template>

  <xsl:template match="h2">
    <xsl:variable name="sectionNum" select="count(preceding-sibling::h1)"/>
    <xsl:variable name="sectionNds" select="preceding-sibling::h1"/>
    <xsl:choose>
      <xsl:when test="count($sectionNds) &gt; 0">
	<xsl:variable name="sectionNd" select="preceding-sibling::h1[1]"/>
	<xsl:variable name="priors" select="count($sectionNd/preceding-sibling::h1 | $sectionNd/preceding-sibling::h2)"/>
	<xsl:variable name="currents" select="count(preceding-sibling::h1 | preceding-sibling::h2)"/>
	<xsl:variable name="subsectionNum" select="$currents - $priors"/>
	<xsl:copy>
	  <xsl:copy-of select="@* except @id"/>
	  <xsl:call-template name="copyID"/>
	  <xsl:value-of select="$sectionNum"/>
	  <xsl:text>.</xsl:text>
	  <xsl:value-of select="$subsectionNum"/>
	  <xsl:text> </xsl:text>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="h2" mode="toc">
    <xsl:variable name="sectionNum" select="count(preceding-sibling::h1)"/>
    <xsl:variable name="sectionNds" select="preceding-sibling::h1"/>
    <xsl:choose>
      <xsl:when test="count($sectionNds) &gt; 0">
	<xsl:variable name="sectionNd" select="preceding-sibling::h1[1]"/>
	<xsl:variable name="priors" select="count($sectionNd/preceding-sibling::h1 | $sectionNd/preceding-sibling::h2)"/>
	<xsl:variable name="currents" select="count(preceding-sibling::h1 | preceding-sibling::h2)"/>
	<xsl:variable name="subsectionNum" select="$currents - $priors"/>
	<div class="toc-h2">
	  <a href="#{translate(@id,':','-')}">
	    <xsl:value-of select="$sectionNum"/>
	    <xsl:text>.</xsl:text>
	    <xsl:value-of select="$subsectionNum"/>
	    <xsl:text> </xsl:text>
	    <xsl:apply-templates select="*|text()"/>
	  </a>
	</div>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="h3">
    <xsl:copy>
      <xsl:copy-of select="@* except @id"/>
      <xsl:call-template name="copyID"/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="h4">
    <xsl:copy>
      <xsl:copy-of select="@* except @id"/>
      <xsl:call-template name="copyID"/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="code">
    <xsl:choose>
      <xsl:when test="@class != ''">
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<tt>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</tt>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="details">
    <div class="details">
      <span class="summary">
        <xsl:apply-templates select="summary/* | summary/text()"/>
      </span>
      <!-- div style="page-break-before:always;"></div -->
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div>(this space intentionally left blank)</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div style="margin-left: 5ex;">&#x22ee;</div>
      <div>
	<xsl:apply-templates select="*[local-name() != 'summary'] | text()"/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="longlisting">
    <div class="details">
      <xsl:variable name="idValue"
		    select="concat('_details_', generate-id())"/>
      <span class="summary">
	<a href="{@file}" target="listing">
        <xsl:value-of select="substring-before(@file,'.html')"/>
	</a>
      </span>
      <div>
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
        <xsl:variable name="encoded" select="document(@file)"/>
        <xsl:copy-of select="$encoded/html/body/pre | $encoded/html/body/pre"/>
      </div>
    </div>    
  </xsl:template>


  <xsl:template match="includeHTML">
    <xsl:variable name="encoded" select="document(@file)"/>
    <xsl:copy-of select="$encoded/html/body/* | $encoded/html/body/*"/>
  </xsl:template>

  <xsl:template match="img">
    <xsl:choose>
      <xsl:when test="@align != ''">
	<xsl:copy>
	  <xsl:choose>
	    <xsl:when test="@alt != ''">
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:attribute name="alt">
		<xsl:text>no alt</xsl:text>
	      </xsl:attribute>
	    </xsl:otherwise>
	  </xsl:choose>
	  <xsl:attribute name="style">
	    <xsl:value-of select="concat('float: ', @align, '; ',  @style)"/>
	  </xsl:attribute>
	  <xsl:copy-of select='@* except @align except @id'/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:choose>
	    <xsl:when test="@alt != ''">
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:attribute name="alt">
		<xsl:text>no alt</xsl:text>
	      </xsl:attribute>
	    </xsl:otherwise>
	  </xsl:choose>
	  <xsl:copy-of select='@* except @align except @id'/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="a[@href != '']">
    <xsl:variable name="newHref">
      <xsl:choose>
	<xsl:when test="starts-with(@href, '../../Public/')">
	  <xsl:call-template name="localHref">
	    <xsl:with-param name="href" 
	       select="concat('../', substring-after(@href, 'Public/'))"/>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="starts-with(@href, '../../')">
	  <xsl:value-of select="concat($baseURL, substring-after(@href,'../../'))"/>
	</xsl:when>
	<xsl:when test="contains(@href, '://')">
	  <xsl:value-of select="@href"/>
	</xsl:when>
	  <xsl:when test="starts-with(@href, 'bb:')">
	    <xsl:call-template name="bblinkConvert">
	      <xsl:with-param name="bbcourseURL"
			      select="$bbURL"/>
	      <xsl:with-param name="bblinkURL"
			      select="@href"/>
	    </xsl:call-template>
	  </xsl:when>
	<xsl:otherwise>
	  <xsl:call-template name="localHref">
	    <xsl:with-param name="href" select="@href"/>
	  </xsl:call-template>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a>
      <xsl:copy-of select="@* except @href"/>
      <xsl:attribute name="href">
	<xsl:choose>
	  <xsl:when test="starts-with($newHref, '#')">
	    <xsl:value-of select="translate($newHref, ':','-')"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="$newHref"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </a>
  </xsl:template>

  <xsl:template name="localHref">
    <xsl:param name="href"/>
    <xsl:choose>
      <xsl:when test="ends-with($href, '/index.html')">
	<xsl:variable name="otherDoc">
	  <xsl:analyze-string select="$href"
			      regex="/([^/]*)/index.html$">
	    <xsl:matching-substring>
	      <xsl:value-of select="regex-group(1)"/>
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	      <xsl:value-of select="$doc"/>
	    </xsl:non-matching-substring>
	  </xsl:analyze-string>
	</xsl:variable>
	<xsl:value-of select="concat('../', $otherDoc, '/', $otherDoc, '__epub.html')"/>;
      </xsl:when>
      <xsl:when test="ends-with($href, '/')">
	<xsl:variable name="otherDoc">
	  <xsl:analyze-string select="$href"
			      regex="/([^/]*)/$">
	    <xsl:matching-substring>
	      <xsl:value-of select="regex-group(1)"/>
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	      <xsl:value-of select="$doc"/>
	    </xsl:non-matching-substring>
	  </xsl:analyze-string>
	</xsl:variable>
	<xsl:value-of select="concat('../', $otherDoc, '/', $otherDoc, '__epub.html')"/>;
      </xsl:when>
      <xsl:when test="ends-with($href, '__html.html')">
	<xsl:variable name="otherDoc">
	  <xsl:analyze-string select="$href"
			      regex="/([^/]*)/index.html$">
	    <xsl:matching-substring>
	      <xsl:value-of select="regex-group(1)"/>
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	      <xsl:value-of select="$doc"/>
	    </xsl:non-matching-substring>
	  </xsl:analyze-string>
	</xsl:variable>
	<xsl:value-of select="concat('../', $otherDoc, '/', $otherDoc, '__epub.html')"/>;
      </xsl:when>
      <xsl:when test="ends-with($href, '__pages.html')">
	<xsl:variable name="otherDoc">
	  <xsl:analyze-string select="$href"
			      regex="/([^/]*)/index.html$">
	    <xsl:matching-substring>
	      <xsl:value-of select="regex-group(1)"/>
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	      <xsl:value-of select="$doc"/>
	    </xsl:non-matching-substring>
	  </xsl:analyze-string>
	</xsl:variable>
	<xsl:value-of select="concat('../', $otherDoc, '/', $otherDoc, '__epub.html')"/>;
      </xsl:when>
      <xsl:when test="ends-with($href, '__slidy.html')">
	<xsl:variable name="otherDoc">
	  <xsl:analyze-string select="$href"
			      regex="/([^/]*)/index.html$">
	    <xsl:matching-substring>
	      <xsl:value-of select="regex-group(1)"/>
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	      <xsl:value-of select="$doc"/>
	    </xsl:non-matching-substring>
	  </xsl:analyze-string>
	</xsl:variable>
	<xsl:value-of select="concat('../', $otherDoc, '/', $otherDoc, '__epub.html')"/>;
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$href"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="span[@class = 'math']">
    <xsl:variable name="priors" select="preceding::span[@class = 'math']"/>
    <xsl:variable name="count" select="1 + count($priors)"/>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <img src="{$doc}__epub.math{$count}.png"
	   alt="{normalize-space(text())}"/>
    </xsl:copy>
  </xsl:template>



  <xsl:template match="text()">
    <xsl:choose>
      <xsl:when test="ancestor::pre">
	<xsl:copy-of select='.'/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="translate(., '&#10;&#13;', '  ')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select='@* except @markdown'/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="html" mode="math">
    <xsl:text>\documentclass[12pt]{article}
\usepackage{amsmath}
\usepackage[charter]{mathdesign}
\pagestyle{empty}
\begin{document}
</xsl:text>
    <xsl:apply-templates select="*" mode="math"/>
    <xsl:text>
\end{document}</xsl:text>
  </xsl:template>

  <xsl:template name="trimMath">
    <xsl:choose>
      <xsl:when test="contains(text(), '\[')">
	<xsl:value-of select="substring-before(substring-after((*|text()), '\['), '\]')"/>
      </xsl:when>
      <xsl:when test="contains(text(), '\(')">
	<xsl:value-of select="substring-before(substring-after((*|text()), '\('), '\)')"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="text()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="span[@class = 'math']" mode="math">
    <xsl:choose>
      <xsl:when test="contains(text(), 'eqnarray')">
	<xsl:call-template name="trimMath"/>
      </xsl:when>
      <xsl:when test="contains(text(), 'equation')">
	<xsl:call-template name="trimMath"/>
      </xsl:when>
      <xsl:when test="contains(text(), 'align')">
	<xsl:call-template name="trimMath"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="* | text()"/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:text>
\newpage
    </xsl:text>
  </xsl:template>


  <xsl:template match="*" mode="math">
    <xsl:apply-templates select="*" mode="math"/>
  </xsl:template>


</xsl:stylesheet>
