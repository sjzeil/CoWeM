<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
>

  <xsl:param name="workDir" select="'export-calendar'"/>

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:output name="resources"
	      method="xml"
	      encoding="utf-8"
	      /> 




  <xsl:template match="/">
      <manifest xmlns:bb="http://www.blackboard.com/content-packaging/" identifier="man00001">
          <organizations/>
          <resources>
              <xsl:apply-templates select="*|text()"/>
          </resources>
      </manifest>
      <xsl:apply-templates mode="resources" select="*|text()"/>
  </xsl:template>

  <xsl:template match="topic">
  	  <xsl:variable name="ident" select="generate-id()"/>
      <xsl:choose>
          <xsl:when test="@enddate != '' or @date != '' or @due != ''">
              <resource bb:title="@title" 
                        identifier="res-{$ident}"
                        bb:file="res-{$ident}.xml"
                        type="resource/x-bb-calendar"
              </resource>
          </xsl:when>
      </xsl:choose>
  </xsl:template>
  
  <xsl:template match="topic[@enddate != '']" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
   	  <xsl:variable name="start">
  	      <xsl:value-of select="@date"/>
  	  </xsl:variable>
   	  <xsl:variable name="stop">
  	      <xsl:value-of select="@enddate"/>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.xml.tex"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE>
                  <xsl:value-of select="@title"/>
              </TITLE>
              <DESCRIPTION>
                  <TEXT>(none)</TEXT>
                  <TYPE value="S"/>
              </DESCRIPTION>
              <USERID value="??"/>
              <TYPE value="COURSE"/>
              <DATES>
                  <CREATED value=""/>
                  <UPDATED value="{current-dateTime()}"/>
                  <START value="{start}"/>
                  <END value="{stop}"/>
              </DATES>
          </CALENDAR>
      </xsl:result-document>
  </xsl:template>


  <xsl:template match="topic[@due != '']" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="start">
  	      <xsl:choose>
  	          <xsl:when test="@time != ''">
  	              <xsl:value-of select="@due"/><xsl:text> </xsl:text><xsl:value-of select="@time"/>
  	          </xsl:when>
  	          <xsl:otherwise>
  	              <xsl:value-of select="@due"/>
  	          </xsl:otherwise>
  	      </xsl:choose>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.xml.tex"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE>
                  <xsl:value-of select="@title"/>
              </TITLE>
              <DESCRIPTION>
                  <TEXT>(none)</TEXT>
                  <TYPE value="S"/>
              </DESCRIPTION>
              <USERID value="??"/>
              <TYPE value="COURSE"/>
              <DATES>
                  <CREATED value=""/>
                  <UPDATED value="{current-dateTime()}"/>
                  <START value="{start}"/>
                  <END value="{start}"/>
              </DATES>
          </CALENDAR>
      </xsl:result-document>
  </xsl:template>


  <xsl:template match="topic[@date != '']" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="start">
  	      <xsl:choose>
  	          <xsl:when test="@time != ''">
  	              <xsl:value-of select="@date"/><xsl:text> </xsl:text><xsl:value-of select="@time"/>
  	          </xsl:when>
  	          <xsl:otherwise>
  	              <xsl:value-of select="@date"/>
  	          </xsl:otherwise>
  	      </xsl:choose>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.xml.tex"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE>
                  <xsl:value-of select="@title"/>
              </TITLE>
              <DESCRIPTION>
                  <TEXT>(none)</TEXT>
                  <TYPE value="S"/>
              </DESCRIPTION>
              <USERID value="??"/>
              <TYPE value="COURSE"/>
              <DATES>
                  <CREATED value=""/>
                  <UPDATED value="{current-dateTime()}"/>
                  <START value="{start}"/>
                  <END value="{start}"/>
              </DATES>
          </CALENDAR>
      </xsl:result-document>
  </xsl:template>


  <xsl:template match="item[@targetdoc != '']">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="theTitle">
  	      <xsl:call-template name="getTitle">
		      <xsl:with-param name="doc" select="@targetdoc"/>
	      </xsl:call-template>
	  </xsl:variable> 
      <xsl:choose>
          <xsl:when test="@enddate != '' or @date != '' or @due != ''">
              <resource bb:title="{$theTitle}" 
                        identifier="res-{$ident}"
                        bb:file="res-{$ident}.xml"
                        type="resource/x-bb-calendar"
              </resource>
          </xsl:when>
      </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="item">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="theTitle" select="text() | *"/>
      <xsl:choose>
          <xsl:when test="@enddate != '' or @date != '' or @due != ''">
              <resource bb:title="{$theTitle}" 
                        identifier="res-{$ident}"
                        bb:file="res-{$ident}.xml"
                        type="resource/x-bb-calendar"
              </resource>
          </xsl:when>
      </xsl:choose>
  </xsl:template>



  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="*" mode="resources">
    <xsl:apply-templates select="*" mode="resources"/>
  </xsl:template>

  <xsl:template match="text()">
  </xsl:template>


  <xsl:template match="text()" mode="resources">
  </xsl:template>


  <xsl:template name="getTitle">
    <xsl:param name="doc"/>
    <xsl:variable name="title" select="/epub/table/title[@doc = $doc]"/>
    <xsl:choose>
      <xsl:when test="count($title) > 0">
	<xsl:value-of select="$title/text()"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>???</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:stylesheet>
