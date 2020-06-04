<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<!--  Special format for debugging purposes  -->
  
  <xsl:param name="format" select="'scroll'"/>

  

  <xsl:template match="/">
    <xsl:copy-of select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:copy-of select="head"/>  
	  <xsl:copy-of select="body"/>  
    </html>
  </xsl:template>


</xsl:stylesheet>
