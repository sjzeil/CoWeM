<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:param name="bbURL" select="''"/>


  <xsl:output method="xml" encoding="utf-8"/>

  <xsl:template name="bblinkConvert">
    <xsl:param name="bbcourseURL"/>
    <xsl:param name="bblinkURL"/>
    
    <xsl:variable name="bburlstart"
		  select="concat(substring-before($bbcourseURL,'url='),'url=')"/>
    <xsl:variable name="bburl0"
		  select="concat('/webapps', substring-after($bblinkURL,'/webapps'))"/>
    <xsl:variable name="bburl1">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl0"/>
	<xsl:with-param name="replace" select="'/'"/>
	<xsl:with-param name="by" select="'%2f'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl2">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl1"/>
	<xsl:with-param name="replace" select="'='"/>
	<xsl:with-param name="by" select="'%3d'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl3">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl2"/>
	<xsl:with-param name="replace" select="'&amp;'"/>
	<xsl:with-param name="by" select="'%26'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl4">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl3"/>
	<xsl:with-param name="replace" select="'?'"/>
	<xsl:with-param name="by" select="'%3f'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="concat($bburlstart,$bburl4)"/>
  </xsl:template>


  <xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
	<xsl:value-of select="substring-before($text,$replace)" />
	<xsl:value-of select="$by" />
	<xsl:call-template name="string-replace-all">
	  <xsl:with-param name="text"
			  select="substring-after($text,$replace)" />
	  <xsl:with-param name="replace" select="$replace" />
	  <xsl:with-param name="by" select="$by" />
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:stylesheet>

