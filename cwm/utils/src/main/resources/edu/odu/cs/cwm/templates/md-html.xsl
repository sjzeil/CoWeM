<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  
  <xsl:param name="Author" select="''"/>
  <xsl:param name="CSS" select="''"/>
  <xsl:param name="Date" select="''"/>
  <xsl:param name="Title" select="'@Title@'"/>
  <xsl:param name="TOC" select="''"/>

  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="courseTitle" select="'@courseTitle@'"/>
  <xsl:param name="semester" select="'@semester@'"/>
  <xsl:param name="sem" select="'@sem@'"/>
  <xsl:param name="instructor" select="'@instructor@'"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="copyright" select="''"/>
  <xsl:param name="primaryDocument" select="'@primaryDocument@'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="formats" select="'html'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>

  <xsl:param name="baseURL" select="'../../'"/>
  <xsl:param name="homeURL" select="'../../index.html'"/>

  <xsl:param name="altformats" select="'yes'"/>
  <xsl:param name="numberingDepth" select="'3'"/>

  <xsl:output method="xml" encoding="UTF-8"/>

  <xsl:variable name="stylesURL" select="concat($baseURL, 'styles/')"/>
  <xsl:variable name="graphicsURL" select="concat($baseURL, 'graphics/')"/>

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
  	<xsl:variable name="numbered">
	  <xsl:apply-templates select="body" mode="sectionNumbering"/>    
  	</xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="head"/>  
	  <xsl:apply-templates select="$numbered"/>    
    </html>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$stylesURL}/md-{$format}.css" />
	  <xsl:call-template name="generateCSSLinks"/>
	  <meta name="viewport" content="width=device-width, initial-scale=1"/>	
      <script type="text/javascript"
	      src="{$stylesURL}/md-{$format}.js">
	      <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="{$mathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	      <xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="@highlightjsURL@/styles/googlecode.css"/>
      <script src="@highlightjsURL@/highlight.pack.js">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
	  <xsl:call-template name="generateJSLinks"/>
  </xsl:template>
  

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>

      <div class="titleblock">
	    <h1 class="title">
	       <xsl:value-of select="$Title"/>
	    </h1>
	    <xsl:if test="$Author != ''">
	      <h2 class="author">
	        <xsl:value-of select="$Author"/>
	      </h2>
	    </xsl:if>
	    <xsl:if test="$Date != ''">
	      <div class="date">
	        <xsl:text>Last modified: </xsl:text>
	        <xsl:value-of select="$Date"/>
	       </div>
	      </xsl:if>
      </div>

      <xsl:if test="$TOC != ''">
	    <div class="toc">
	      <xsl:text>Contents:</xsl:text>
	        <xsl:apply-templates select="h1 | h2" mode="toc"/>
	    </div>
      </xsl:if>

      <xsl:apply-templates select="node()"/>
      
      <xsl:call-template name="insertFooter"/>
    </xsl:copy>
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
