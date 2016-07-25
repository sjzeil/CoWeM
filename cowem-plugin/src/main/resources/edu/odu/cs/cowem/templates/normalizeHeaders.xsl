<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
<!--  PegDown generates headers h1, h2, ... with all text inside an <a> element
      containing both an href and name attribute. This complicates a lot of
      of later processing, so we spend a pass to rewrite this as simply an
      id on the header itself.  -->
  
  
<!--  For testing purposes -->

  <xsl:template match="html[@test = 'normalizeHeaders']">
      <xsl:apply-templates select="." mode="normalizeHeaders"/>
  </xsl:template>


  

<!--  Normalize Headers -->


  <xsl:template match="html" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3|h4|h5" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:choose>
         <xsl:when test="a[1][@href != '' and @name != '']">
             <xsl:variable name="a1" select="a[1]"/>
             <xsl:attribute name="id">
                <xsl:value-of select="$a1/@name"/>
             </xsl:attribute>
             
             <xsl:copy-of select="$a1/node()"/>
             <xsl:copy-of select="$a1/following-sibling::node()"/>
             
         </xsl:when>
         <xsl:otherwise>
             <xsl:copy-of select="node()"/>
         </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*" mode="normalizeHeaders">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="node()" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()" mode="normalizeHeaders">
    <xsl:copy-of select='.'/>
  </xsl:template>


</xsl:stylesheet>
