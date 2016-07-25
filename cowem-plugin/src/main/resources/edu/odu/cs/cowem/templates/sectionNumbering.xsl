<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
  <!-- 

     sectionNumbering: Adds @sectionNumber attributes to top-level <h?> 
       elements down to $numberDepth. (Must be applied before either pagination
       or sectioning.)

  -->

  <xsl:param name="numberDepth" select="'3'"/>
  
  
<!--  For testing purposes -->

  <xsl:template match="html[@test = 'sectionNumbering']">
      <xsl:apply-templates select="." mode="sectionNumbering"/>
  </xsl:template>


<!-- sectionNumbering -->


  <xsl:template match="html" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sectionNumbering">
    <xsl:variable name="normalized">
        <xsl:apply-templates select="." mode="normalizeHeaders"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="$normalized/body/*" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 0)">
	    <xsl:variable name="sectionNumber">
	      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
	    </xsl:variable>
	    <xsl:attribute name="sectionNumber">
	      <xsl:value-of select="concat($sectionNumber, ' ')"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h2" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 1)">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="count(preceding-sibling::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="1 + count(preceding-sibling::h2) 
				    - count(preceding-sibling::h1[1]/preceding-sibling::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, ' ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h3" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 2)">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="count(preceding-sibling::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="count(preceding-sibling::h2) 
		- count(preceding-sibling::h1[1]/preceding-sibling::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:variable name="subsubsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="1 + count(preceding-sibling::h3) 
		- count(preceding-sibling::h2[1]/preceding-sibling::h3)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h3)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, '.',
				$subsubsectionNumber, ' ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="*" mode="sectionNumbering">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="text()" mode="sectionNumbering">
    <xsl:copy-of select='.'/>
  </xsl:template>



</xsl:stylesheet>
