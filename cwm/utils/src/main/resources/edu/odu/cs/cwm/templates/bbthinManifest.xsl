<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:bb="http://www.blackboard.com/content-packaging/"
	>

  <xsl:param name="workDir" select="'bbthin'"/>

  <xsl:param name="baseURL" select="'document'"/>

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
  
  
  <!--
      Input structure:
      
      <imscc>
         <sem>semester code</sem>
         <title>course title</title>
         <author>course name</author>
         <courseName>course name</courseName>
         <files>
             <file>path to file</file>
                ... all files in web distribution ...
                ... empty for the "thin" version export ...
         </files>
         <outline>
            ... entire contents of outline.xml
         </outline>
         <table> <!- - titleTable.xml - ->
             <title doc="docName">title of document</title>
               ...
         </table>
      </imscc> 
   -->

  <xsl:variable name="now">
      <xsl:value-of xmlns:date="java:java.util.Date"
         	xmlns:sdf="java:java.text.SimpleDateFormat"
          	select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), date:new())'/>
  </xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates select="/imscc"/>
  </xsl:template>
    
  <xsl:template match="imscc">
    <xsl:message><xsl:text>Matched root</xsl:text></xsl:message>
    <manifest identifier="man0001">
      <organizations default="toc00001">
	      <organization identifier="toc00001">
	          <xsl:apply-templates select="/imscc/navigation/*" mode="navigation"/>
	      </organization>
      </organizations>
      <resources>
          <xsl:apply-templates select="/imscc/navigation/*" mode="nav-resources"/>
	      <xsl:apply-templates select="/imscc/outline/*" mode="calendar"/>
      </resources>
    </manifest>
    <xsl:result-document 
        href="{$workDir}/.bb-package-info"
	    format="textual">
        <xsl:text>#Bb PackageInfo Property File
#</xsl:text>
      <xsl:value-of select='$now'/>
      <xsl:text>
</xsl:text>
      </xsl:result-document>

      <xsl:result-document 
	  href="{$workDir}/.bb-package-sig"
	  format="textual">
	<xsl:text>BEDBC1234B88F661117C26D53AECCB3B8
</xsl:text>
<!--  This is a fake signature. I assume the signature is supposed to
      be some sort of checksum over the files. BB9 doesn't seem to object if
      the signature is wrong, but it rejects the import if the signature file is
      missing. -->
      </xsl:result-document>

  </xsl:template>

  <xsl:template match="a" mode="navigation">
      <xsl:variable name="itemID" select="generate-id()"/>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
      <item identifier="itm-{$itemID}" identifierref="res-{$itemID}">
          <title>
            <xsl:value-of select="$title"/>
          </title>
      </item>
      <xsl:call-template name="generateLinkResource">
          <xsl:with-param name="url" select="@href"/>
          <xsl:with-param name="itemID" select="$itemID"/>
          <xsl:with-param name="label" select="$title"/>
      </xsl:call-template>
  </xsl:template>

  <xsl:template match="table|tr|td" mode="navigation">
      <xsl:apply-templates select="*" mode="navigation"/>
  </xsl:template>
  

  <xsl:template match="a" mode="nav-resources">
      <xsl:variable name="itemID" select="generate-id()"/>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
      <resource
          bb:file="res-{$itemID}.dat"
          bb:title="{$title}"
          identifier="res-{$itemID}"
          type="course/x-bb-coursetoc"
          xml:base="res-{$itemID}"/>
  </xsl:template>

  <xsl:template match="table|tr|td" mode="nav-resources">
      <xsl:apply-templates select="*" mode="nav-resources"/>
  </xsl:template>






  <xsl:template name="generateLinkResource">
      <xsl:param name="url"/>
      <xsl:param name="itemID"/>
      <xsl:param name="label"/>
      
    <xsl:result-document 
        href="{concat($workDir, '/res-', $itemID, '.dat')}"
        format="resources">
      <COURSETOC id="_id{$itemID}_">
         <LABEL value="{$label}"/>
         <URL value="{concat($baseURL, 'Directory/navigation/',$url)}"/>
         <TARGETTYPE value="URL"/>
         <INTERNALHANDLE value=""/>
         <FLAGS>
             <LAUNCHINNEWWINDOW value="true"/>
             <ISENABLED value="true"/>
             <ISENTRYPOINT value="false"/>
             <ALLOWOBSERVERS value="true"/>
             <ALLOWGUESTS value="true"/>
         </FLAGS>
      </COURSETOC>
    </xsl:result-document>
   </xsl:template>
   
   
  <xsl:template match="span[@class='date' and @startsAt != '']" mode="calendar">
      <!-- Now add calendar entries if appropriate -->

      <xsl:variable name="item" select="./ancestor::p[1]"/>
      <xsl:variable name="ident" select="generate-id($item)"/>
      <xsl:variable name="rawEventTitle">
          <xsl:apply-templates select="$item/* | $item/text()" mode="itemTitle"/>
      </xsl:variable>
      <xsl:variable name="eventTitle" select="normalize-space($rawEventTitle)"/>

      <resource bb:title="{/imscc/courseName/text()}: {$eventTitle}" 
        identifier="date-{$ident}"
        bb:file="date-{$ident}.dat"
        type="resource/x-bb-calendar"
        xml:base="date-{$ident}"/>

      <xsl:variable name="title" select="concat(/imscc/courseName/text(),
              ': ', normalize-space($eventTitle))"/>
      <xsl:result-document 
          href="{$workDir}/date-{$ident}.dat"
          format="resources">
          <CALENDAR id="date-{$ident}">
              <TITLE value="{$title}"/>
              <DESCRIPTION>
                <TEXT> </TEXT>
                <TYPE value="S"/>
              </DESCRIPTION>
              <!-- USERID value="??"/ -->
              <TYPE value="COURSE"/>
              <DATES>
                  <CREATED value=""/>
                  <UPDATED value="{$now}"/>
                  <START value="{@startsAt}"/>
                  <END value="{@endsAt}"/>
              </DATES>
          </CALENDAR>
      </xsl:result-document>
  </xsl:template>
  
  <xsl:template match="*" mode="calendar">
      <xsl:apply-templates select="*" mode="calendar"/>
  </xsl:template>

  <xsl:template match="span[@class='date']" mode="itemTitle">
  </xsl:template>
  
  <xsl:template match="*" mode="itemTitle">
      <xsl:apply-templates select="* | text()" mode="itemTitle"/>      
  </xsl:template>
  
  <xsl:template match="text()" mode="itemTitle">
      <xsl:copy-of select='.'/>      
  </xsl:template>


</xsl:stylesheet>
