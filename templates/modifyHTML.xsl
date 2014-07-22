<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>


  <xsl:import href="../styles/footer.xsl"/>

  <!-- 
       Inserts shared material (e.g., buttons and footer) into 
       a directory web page
  -->

  <xsl:output method="xml" encoding="utf-8"/>

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


  <xsl:template match="body|xhtml:body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="*|text()"/>
      <xsl:call-template name="insertFooter"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="buttons">
    <xsl:copy-of select="document('../Directory/buttons.xml')"/>
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
