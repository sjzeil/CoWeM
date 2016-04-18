<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

  <!-- 
       Selects the document to be used for the index.html file in
       this directory.
  -->

  <xsl:param name="doc"/>

  <xsl:output method="text" encoding="utf-8"/>

  <xsl:template match="/">
    <xsl:apply-templates select="/project/target[@name='documents']"/>
  </xsl:template>


  <xsl:template match="target">
    <xsl:variable name="explicitChoice"
		  select="docformat[(@index = '1') or (@index='yes') or (@index='true')]"/>

    <xsl:choose>
      <xsl:when test="$explicitChoice">
	<xsl:text>doc.indexFile=</xsl:text>
	<xsl:value-of select="$doc"/>
	<xsl:text>__</xsl:text>
	<xsl:value-of select="$explicitChoice/@format"/>
	<xsl:text>.html&#10;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>doc.indexFile=</xsl:text>
	<xsl:value-of select="$doc"/>
	<xsl:text>__</xsl:text>
	<xsl:value-of select="./docformat[1]/@format"/>
	<xsl:text>.html&#10;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



</xsl:stylesheet>
