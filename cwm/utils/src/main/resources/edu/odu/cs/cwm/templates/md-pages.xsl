<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:param name="format" select="'pages'"/>
  
  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  
  
  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <xsl:variable name="numbered">
      <xsl:apply-templates select="body" mode="sectionNumbering"/>    
    </xsl:variable>
    <xsl:variable name="paged">
      <xsl:apply-templates select="$numbered" mode="pagination"/>    
    </xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="head"/>  
      <xsl:apply-templates select="$paged"/>    
    </html>
  </xsl:template>


  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>

      <div class="page" id="_page0">
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
            <xsl:apply-templates select="page/h1 | page/h2" mode="toc"/>
        </div>
      </xsl:if>
      </div>  <!--  class="page" id="_page0"  -->

      <xsl:apply-templates select="node()"/>
      
      <xsl:call-template name="insertFooter"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="page">
    <xsl:variable name="pageNumber" select="1 + count(preceding-sibling::page)"/>
    <div class="page">
        <xsl:attribute name="id">
            <xsl:value-of select="concat('_page', $pageNumber)"/>
        </xsl:attribute>
        <xsl:apply-templates select="node()"/>
    </div> 
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
