<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.imsglobal.org/xsd/imsccv1p2/imscp_v1p1"
	xmlns:lom="http://ltsc.ieee.org/xsd/imsccv1p2/LOM/resource"
	xmlns:lomimscc="http://ltsc.ieee.org/xsd/imsccv1p2/LOM/manifest"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="
  http://www.imsglobal.org/xsd/imsccv1p2/imscp_v1p1 http://www.imsglobal.org/profile/cc/ccv1p2/ccv1p2_imscp_v1p1_v1p0.xsd
  http://ltsc.ieee.org/xsd/imsccv1p2/LOM/resource http://www.imsglobal.org/profile/cc/ccv1p2/LOM/ccv1p2_lomresource_v1p0.xsd
  http://ltsc.ieee.org/xsd/imsccv1p2/LOM/manifest http://www.imsglobal.org/profile/cc/ccv1p2/LOM/ccv1p2_lommanifest_v1p0.xsd"
	
	xmlns:opf="http://www.idpf.org/2007/opf"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	>

  <xsl:param name="baseURL" select="'document'"/>
  <xsl:param name="doc" select="'OEPBS'"/>

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>
  
  
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
         </files>
         <outline>
            ... entire contents of outline.xml
         </outline>
         <table> <!-- titleTable.xml -->
             <title doc="docName">title of document</title>
               ...
         </table>
      </imscc> 
   -->

  <xsl:template match="/">
  <manifest identifier="man0001">
      
  <metadata>
    <schema>IMS Common Cartridge</schema>
    <schemaversion>1.2.0</schemaversion>
    <lomimscc:lom>
      <lomimscc:general>
        <lomimscc:title>
          <lomimscc:string language="en-US">
          	<xsl:value-of select="/imscc/title/text()"/>
          </lomimscc:string>
        </lomimscc:title>
        <lomimscc:description>
          <lomimscc:string language="en-US">
          	<xsl:value-of select="concat('Materials for ', /imscc/courseName/text())"/> 
          </lomimscc:string>
        </lomimscc:description>
        <lomimscc:keyword>
          <lomimscc:string language="en-US">
            <xsl:value-of select="/imscc/courseName/text()"/> 
          </lomimscc:string>
        </lomimscc:keyword>
      </lomimscc:general>
    </lomimscc:lom>
  </metadata>
  <organizations>
    <organization identifier="O_1" structure="rooted-hierarchy">
      <item identifier="LearningModules">
           <xsl:apply-templates select="/imscc/outline/topic"/>
      </item>
    </organization>
  </organizations>
  <resources>
      <xsl:apply-templates select="/imscc/outline/topic" mode="resources"/>
  </resources>
  </manifest>
  </xsl:template>


  <xsl:template match="topic">
      <xsl:choose>
      	 <xsl:when test="item | subject">
      	     <xsl:variable name="topicID" select="generate-id()"/>
      	     <item identifier="{$topicID}">
      	     	<title>
      	     		<xsl:value-of select="@title"/>
      	     		<xsl:text> (</xsl:text>
      	     		<xsl:value-of select="@date"
      	     		<xsl:text> - </xsl:text>
      	     		<xsl:value-of select="@enddate"
      	     		<xsl:text> )</xsl:text>
      	     	</title>
      	     	<xsl:apply-templates select="description"/>
      	     	<xsl:apply-templates select="item | subject"/>
      	     </item>
      	 </xsl:when>
      	 <xsl:otherwise>
      	     <xsl:apply-templates select="topic"/>
      	 </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="description">
      <xsl:variable name="descriptionID" select="generate-id()"/>
  	  <item identifier="{$descriptionID}" identifierref="{concat("res",$desciptionID)}">
  	      <title>Description</title>
  	  </item>
  </xsl:template>

  <xsl:template match="subject">
      <xsl:variable name="subjectID" select="generate-id()"/>
  	  <item identifier="subjectID">
  	      <title>
  	          <xsl:value-of select="@title"/>
  	      </title>
  	  </item>
  	  <xsl:apply-templates select="item | subject"/>
  </xsl:template>
  	  
  <xsl:template match="item">
      <xsl:variable name="itemID" select="generate-id()"/>
  	  <item identifier="{$itemID}" identifierref="{concat("res",$itemID)}">
  	      <title>
  	          <xsl:value-of select="@kind"/>
  	          <xsl:text>: </xsl:text>
  	          <xsl:value-of select="@title"/>
  	          <xsl:value-of select="text()"/>
  	      </title>
  	  </item>
  </xsl:template>



  <xsl:template match="item[@targetdoc != '']" mode="suppressed">
    <xsl:variable name="fileName" 
	select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/>
    <xsl:variable name="fileID" 
		  select="translate(encode-for-uri($fileName), '%', '_')"/>
    <opf:item id="{$fileID}" 
	      href="{$fileName}" 
	      media-type="application/xhtml+xml"/>
    <xsl:if test="count(/epub/files/file[text() = $fileName]) = 0">
      <!-- This file does not exist. May be lecture notes that have
	   not yet been written, or that were not built for epub
	   output. -->
      <xsl:result-document href="{$fileName}" method="xhtml"> 
	<html>
	  <head>
	    <title>
	      <xsl:call-template name="getTitle">
		<xsl:with-param name="doc" select="@targetdoc"/>
	      </xsl:call-template>
	    </title>
	  </head>
	  <body>
	    <h1>
	      <xsl:call-template name="getTitle">
		<xsl:with-param name="doc" select="@targetdoc"/>
	      </xsl:call-template>
	    </h1>
	    <h2>(place holder)</h2>
	    <p>
	      This section is currently unavailable.
	    </p>
	  </body>
	</html>
      </xsl:result-document>
    </xsl:if>
  </xsl:template>




  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="*" mode="resources">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="text()">
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
