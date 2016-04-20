<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bb="http://www.blackboard.com/content-packaging/"
>

  <xsl:param name="workDir" select="'export-calendar'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="courseName" select="'CS'"/>
  
  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:output name="resources"
	      method="xml"
	      encoding="utf-8"
	      indent="yes"
	      /> 
  <xsl:output name="textual"
	      method="text"
	      encoding="utf-8"
	      /> 


  <xsl:variable name="now">
      <xsl:value-of xmlns:date="java:java.util.Date"
         	xmlns:sdf="java:java.text.SimpleDateFormat"
          	select='sdf:format(sdf:new("yyyy-MM-dd hh:mm:ss z"), date:new())'/>
  </xsl:variable>


  <xsl:template match="/">
      <manifest xmlns:bb="http://www.blackboard.com/content-packaging/" identifier="man00001">
          <organizations/>
          <resources>
              <xsl:apply-templates select="*|text()"/>
          </resources>
      </manifest>
      <xsl:apply-templates mode="resources" select="*|text()"/>
      <xsl:result-document 
          href=".bb-package-info"
	      format="textual">
	     <xsl:text>#Bb PackageInfo Property File
#</xsl:text>
         <xsl:value-of select='$now'/>
	     <!-- xsl:value-of select="$nowDT"/ -->
	     <xsl:text>
</xsl:text>
      </xsl:result-document>
      <xsl:result-document 
          href=".bb-package-sig"
	      format="textual">
	     <xsl:text>BEDBC1234B88F661117C26D53AECCB3B8
</xsl:text>
         <!--  This is a fake signature. I assume the signature is supposed to
               be some sort of checksum over the files. BB9 doesn't seem to object if
               the signature is wrong, but it rejects the import if the signature file is
               missing. -->
      </xsl:result-document>
  </xsl:template>
  
  <xsl:template name="formatDateTime">
     <xsl:param name="dateTime"/>
     <xsl:value-of xmlns:date="java:java.util.Date"
         	xmlns:sdf="java:java.text.SimpleDateFormat"
          	select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), sdf:parse(sdf:new("yyyy-MM-dd hh:mmaaa"),$dateTime))'/>
  </xsl:template>

  <xsl:template name="formatEndDateTime">
     <xsl:param name="dateTime"/>
     <xsl:value-of xmlns:date="java:java.util.Date"
         	xmlns:sdf="java:java.text.SimpleDateFormat"
          	select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:59 z"), sdf:parse(sdf:new("yyyy-MM-dd hh:mmaaa"),$dateTime))'/>
  </xsl:template>

  <xsl:template match="topic">
  	  <xsl:variable name="ident" select="generate-id()"/>
      <xsl:choose>
          <xsl:when test="@enddate != '' or @date != '' or @due != ''">
              <resource bb:title="{$courseName}: {@title}" 
                        identifier="res-{$ident}"
                        bb:file="res-{$ident}.dat"
                        type="resource/x-bb-calendar"
                        xml:base="res-{$ident}">
              </resource>
          </xsl:when>
      </xsl:choose>
      <xsl:apply-templates select="*"/>
  </xsl:template>
  
  
  
  <xsl:template match="topic[(@date != '') or (@due != '')]" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
   	  <xsl:variable name="start">
   	  	  <xsl:call-template name="formatDateTime">
  	          <xsl:with-param name="dateTime" select="concat(@date, ' 12:00am')"/>
  	      </xsl:call-template>
  	  </xsl:variable>
   	  <xsl:variable name="stop">
   	  	<xsl:choose>
   	  		<xsl:when test="@enddate != ''">
   	  			<xsl:call-template name="formatEndDateTime">
  	                <xsl:with-param name="dateTime" select="concat(@enddate, ' 11:59pm')"/>
  	            </xsl:call-template>
   	  		</xsl:when>
   	  		<xsl:otherwise>
   	  		    <xsl:call-template name="formatEndDateTime">
  	                <xsl:with-param name="dateTime" select="concat(@date, ' 11:59pm')"/>
  	            </xsl:call-template>
   	  		</xsl:otherwise>
   	  	</xsl:choose>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.dat"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE value="{$courseName}: {@title}"/>
              <DESCRIPTION>
                  <TEXT> </TEXT>
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
      <xsl:apply-templates select="*" mode="resources"/>
  </xsl:template>


 


  <xsl:template match="item[@date != '' or @due != '']">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="theTitle">
  	      <xsl:call-template name="getTitle"/>
	  </xsl:variable> 
      <resource bb:title="{$courseName}: {$theTitle}" 
                identifier="res-{$ident}"
                bb:file="res-{$ident}.dat"
                type="resource/x-bb-calendar"
                xml:base="res-{$ident}"/>
  </xsl:template>
  
  

  <xsl:template match="item[(@date != '') or (@due != '')]" mode="resources">
  	  <xsl:variable name="ident" select="generate-id()"/>
  	  <xsl:variable name="startString">
  	      <xsl:choose>
  	          <xsl:when test="@enddate != ''">
  	              <xsl:value-of select="concat(@date, ' 12:00am')"/>
  	          </xsl:when>
  	          <xsl:when test="@time != ''">
  	              <xsl:value-of select="concat(@date,@due, ' ', @time)"/>
  	          </xsl:when>
  	          <xsl:otherwise>
  	              <xsl:value-of select="concat(@date,@due, ' 11:59pm')"/>
  	          </xsl:otherwise>
  	      </xsl:choose>
  	  </xsl:variable>
   	  <xsl:variable name="start">
   	  	  <xsl:call-template name="formatDateTime">
  	          <xsl:with-param name="dateTime" select="$startString"/>
  	      </xsl:call-template>
  	  </xsl:variable>
  	  <xsl:variable name="stopString">
  	      <xsl:choose>
  	          <xsl:when test="@enddate != ''">
  	              <xsl:value-of select="concat(@enddate, ' 11:59pm')"/>
  	          </xsl:when>
  	          <xsl:when test="@endtime != ''">
  	              <xsl:value-of select="concat(@date,@due, ' ', @endtime)"/>
  	          </xsl:when>
  	          <xsl:when test="@time != ''">
  	              <xsl:value-of select="concat(@date,@due, ' ', @time)"/>
  	          </xsl:when>
  	          <xsl:otherwise>
  	              <xsl:value-of select="concat(@date,@due, ' 11:59pm')"/>
  	          </xsl:otherwise>
  	      </xsl:choose>
  	  </xsl:variable>
   	  <xsl:variable name="stop">
   	  	  <xsl:call-template name="formatEndDateTime">
  	          <xsl:with-param name="dateTime" select="$stopString"/>
  	      </xsl:call-template>
  	  </xsl:variable>
  	  <xsl:variable name="theTitle">
  	      <xsl:call-template name="getTitle"/>
  	  </xsl:variable>
      <xsl:result-document 
          href="res-{$ident}.dat"
	      format="resources">
          <CALENDAR id="cal-{$ident}">
              <TITLE value="{$courseName}: {$theTitle}"/>
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
      <xsl:apply-templates select="*" mode="resources"/>
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

  <xsl:variable name="titleTable" 
     select="document(concat($pwdURL, '/titleTable.xml'))/table"/>

  <xsl:template name="getTitle">
    <xsl:variable name="textContent" select="normalize-space(text() | *)"/>
    <xsl:choose>
        <xsl:when test="$textContent != ''">
        	<xsl:value-of select="$textContent"/>
        </xsl:when>
        <xsl:when test="@targetdoc != ''">
        	<xsl:variable name="doc" select="@targetdoc"/>
            <xsl:value-of select="$titleTable/title[@doc=$doc]/text()"/>
        </xsl:when>
        <xsl:otherwise>
        	<xsl:text>(???)</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:stylesheet>
