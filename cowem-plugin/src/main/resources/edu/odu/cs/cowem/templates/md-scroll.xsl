<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="normalizeHeaders.xsl"/>
  <xsl:include href="sectionNumbering.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  <xsl:param name="format" select="'scroll'"/>

  

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
