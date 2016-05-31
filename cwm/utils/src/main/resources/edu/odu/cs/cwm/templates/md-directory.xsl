<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:param name="format" select="'directory'"/>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <html>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="head"/>  
      <xsl:apply-templates select="body"/>    
    </html>
  </xsl:template>


  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <div class="titleblock">
        <div class="courseName">@courseName@, @semester@</div>
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
      
      <div class="center">
	    <div class="leftPart">
	      <iframe src="../navigation/index.html" class="navigation">_</iframe>
	    </div>
	    <div class="rightPart">
	      <xsl:apply-templates select="node()"/>
	      <xsl:call-template name="insertFooter"/>
	    </div>
      </div>
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
