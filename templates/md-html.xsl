<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="../styles/footer.xsl"/>

  <!-- 
       Chops an HTML page into "slidy" style slides
       divided at each <h1>, <h2>, and <hr/> marker
  -->

  <xsl:param name="doc" select="'doc'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="MathJaxURL" select="'../../styles/MathJax'"/>
  <xsl:param name="highlightjsURL" select="'../../styles/highlight.js'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="bbURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>

  <xsl:output method="xml" encoding="utf-8"/>



  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$stylesURL}/md-html.css" />
      <script type="text/javascript"
	      src="{$stylesURL}/md-html.js">
	<xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="{$MathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	<xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="{$highlightjsURL}/styles/googlecode.css"/>
      <script src="{$highlightjsURL}/highlight.pack.js">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>

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
      <xsl:call-template name="insertFooter"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1" mode="toc">
    <div class="toc-h1">
      <a href="#{@id}">
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
	  <xsl:copy-of select="@*"/>
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
	  <a href="#{@id}">
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
      <xsl:variable name="idValue"
		    select="concat('_details_', generate-id())"/>
      <span class="summary">
        <xsl:apply-templates select="summary/* | summary/text()"/>
      </span>
      <xsl:text> </xsl:text>
      <input id="but{$idValue}" type="button" value="+"
        onclick="toggleDisplay('{$idValue}')"
        />
      <div style="display: none;">
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
	<xsl:apply-templates select="*[local-name() != 'summary'] | text()"/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="example">
    <xsl:variable name="exampleCounter" 
		  select="1 + count(preceding::example)"/>
    <blockquote class="details" id="example{$exampleCounter}">
      <div class="exampleTitle">
	<xsl:text>Example </xsl:text>
	<xsl:value-of select="$exampleCounter"/>
	<xsl:text>: </xsl:text>
	<xsl:apply-templates select="title/node()"/>
      </div>
      <xsl:apply-templates select="*[local-name() != 'title'] | text()"/>
    </blockquote>
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
      <xsl:text> </xsl:text>
      <input id="but{$idValue}" type="button" value="+"
        onclick="toggleDisplay('{$idValue}')"
        />
      <div class="detailPart">
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
        <xsl:variable name="encoded" select="document(@file)"/>
        <xsl:copy-of select="$encoded/html/body/pre"/>
      </div>
    </div>    
  </xsl:template>


  <xsl:template match="includeHTML">
    <xsl:variable name="encoded" select="document(@file)"/>
    <xsl:copy-of select="$encoded/html/body/*"/>
  </xsl:template>



  <xsl:template match="text()">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
