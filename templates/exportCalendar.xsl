<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bb="http://www.blackboard.com/content-packaging/"
>

  <xsl:param name="workDir" select="'export-calendar'"/>

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:output name="resources"
	      method="xml"
	      encoding="utf-8"
	      /> 
  <xsl:output name="textual"
	      method="text"
	      encoding="utf-8"
	      /> 


  <xsl:variable name="nowDT" select="adjust-dateTime-to-timezone(current-dateTime())"/>
  <xsl:variable name="now">
  	<xsl:value-of select="year-from-dateTime($nowDT)"/>
  	<xsl:text>-</xsl:text>
  	<xsl:value-of select="month-from-dateTime($nowDT)"/>
  	<xsl:text>-</xsl:text>
  	<xsl:value-of select="day-from-dateTime($nowDT)"/>
  	<xsl:text> </xsl:text>
    <xsl:value-of select="hours-from-dateTime($nowDT)"/>
  	<xsl:text>:</xsl:text>
    <xsl:value-of select="minutes-from-dateTime($nowDT)"/>
 </xsl:variable>


  <xsl:template match="/">
      <manifest xmlns:bb="http://www.blackboard.com/content-packaging/" identifier="man00001">
          <organizations/>
          <resources>
              <xsl:apply-templates select="*|text()"/>
              <resource 
              		bb:file="courseinfo.dat" 
              		bb:title="arbitrary-course-title" 
                    identifier="courseinfo"
   					type="resource/x-mhhe-course-cx" 
   					xml:base="courseinfo"/>
          </resources>
      </manifest>
      <xsl:apply-templates mode="resources" select="*|text()"/>
      <xsl:result-document 
          href="courseinfo.dat"
	      format="resources">
	      <parentContextInfo>
	          <parentContextId>arbitrary-course-title</parentContextId>
	      </parentContextInfo>
      </xsl:result-document>
      <xsl:result-document 
          href=".bb-package-info"
	      format="textual">
	     <xsl:text>#Bb PackageInfo Property File
#</xsl:text>
	     <xsl:value-of select="$nowDT"
	     <xsl:text>
</xsl:text>
      </xsl:result-document>
      <xsl:result-document 
          href=".bb-package-sig"
	      format="textual">
	     <xsl:text>BEDBC1234B88F661117C26D53AECCB3B8
</xsl:text>
      </xsl:result-document>
  </xsl:template>

  <xsl:template match="topic">
  	  <xsl:variable name="ident" select="generate-id()"/>
      <xsl:choose>
          <xsl:when test="@enddate != '' or @date != '' or @due != ''">
              <resource bb:title="{@title}" 
                        identifier="res-{$ident}"
                        bb:file="res-{$ident}.dat"
                        type="resource/x-bb-calendar"
                        xml:base="res-{$ident}">
              </resource>
          </xsl:when>
      </xsl:choose>
  </xsl:template>
  
  
  
  <xsl:template match="topic[@date != '']" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
   	  <xsl:variable name="start">
  	      <xsl:value-of select="@date"/>
  	  </xsl:variable>
   	  <xsl:variable name="stop">
   	  	<xsl:choose>
   	  		<xsl:when test="@enddate != ''">
  	            <xsl:value-of select="@enddate"/>
   	  		</xsl:when>
   	  		<xsl:otherwise>
   	  		    <xsl:value-of select="@date"/>
   	  		</xsl:otherwise>
   	  	</xsl:choose>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.dat"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE>
                  <xsl:value-of select="@title"/>
              </TITLE>
              <DESCRIPTION>
                  <TEXT>(none)</TEXT>
                  <TYPE value="S"/>
              </DESCRIPTION>
              <!-- USERID value="??"/ -->
              <TYPE value="COURSE"/>
              <DATES>
                  <CREATED value=""/>
                  <UPDATED value="{$now}"/>
                  <START value="{$start}"/>
                  <END value="{$stop}"/>
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
          href="res-{$ident}.dat"
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
                  <UPDATED value="{$now}"/>
                  <START value="{$start}"/>
                  <END value="{$start}"/>
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
                        bb:file="res-{$ident}.dat"
                        type="resource/x-bb-calendar"
                        xml:base="res-{$ident}">
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
                        bb:file="res-{$ident}.dat"
                        type="resource/x-bb-calendar">
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
