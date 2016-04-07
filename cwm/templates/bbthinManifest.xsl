<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:bb="http://www.blackboard.com/content-packaging/"
	>

  <xsl:param name="workDir" select="'bbthin'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="courseName" select="'CS'"/>

  <xsl:param name="baseURL" select="'document'"/>
  <xsl:param name="doc" select="'OEPBS'"/>
  <xsl:param name="MathJaxURL" select="'../../styles/MathJax'"/>
  <xsl:param name="highlightjsURL" select="'../../styles/highlight.js'"/>

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
    <manifest identifier="man0001">
      <organizations default="toc00001">
	<organization identifier="toc00001">
	  <item identifier="itm00001" identifierref="res00001">
	    <title>Modules</title>
	    <item identifier="itm00002" identifierref="res00002">
	      <title>--TOP--</title>
	      <xsl:if test="normalize-space(/imscc/outline/preamble) != ''">
		<item identifier="mod00001" identifierref="rmod00001">
		  <xsl:choose>
		    <xsl:when test="/imscc/outline/preamble/h1">
		      <title>
			<xsl:value-of select="normalize-space(/imscc/outline/preamble/h1[1])"/>
		      </title>
		    </xsl:when>
		    <xsl:otherwise>
		      <title>Course Modules</title>
		    </xsl:otherwise>
		  </xsl:choose>
		</item>
	      </xsl:if>
	      <xsl:apply-templates select="/imscc/outline/topic"/>
	    </item>
	  </item>
	</organization>
      </organizations>
      <resources>
	<resource bb:file="res00001.dat" 
		  bb:title="Modules" 
		  identifier="res00001" 
		  type="course/x-bb-coursetoc" 
		  xml:base="res00001"/>
	<resource bb:file="res00002.dat" 
		  bb:title="--TOP--" 
		  identifier="res00002" 
		  type="resource/x-bb-document" 
		  xml:base="res00002"/>
	<xsl:if test="normalize-space(/imscc/outline/preamble) != ''">
	  <xsl:variable name="pageTitle">
	    <xsl:choose>
	      <xsl:when test="/imscc/outline/preamble/h1">
		<title>
		  <xsl:value-of select="normalize-space(/imscc/outline/preamble/h1[1])"/>
		</title>
	      </xsl:when>
	      <xsl:otherwise>
		<title>Course Modules</title>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	  <resource identifier="rmod00001"
		    xml:base="rmod00001"
		    bb:file="rmod00001.dat"
		    bb:title="{$pageTitle}"
		    type="resource/x-bb-document" 
		    />
	  <xsl:result-document 
	      href="rmod00001.dat"
	      format="resources">
	    <xsl:variable name="pageContentID">
	      <xsl:call-template name="contentID">
		<xsl:with-param name="node" 
				select="/imscc/outline/preamble"/>
	      </xsl:call-template>
	    </xsl:variable>
	    <xsl:variable name="parentID">
	      <xsl:call-template name="contentID">
		<xsl:with-param name="node" select="/imscc/outline"/>
	      </xsl:call-template>
	    </xsl:variable>

	    <CONTENT id="{$pageContentID}">
	      <TITLE value="{$pageTitle}"/>
	      <TITLECOLOR value="#000000"/>
	      <BODY>
		<TEXT>
		  <xsl:apply-templates select="/imscc/outline/preamble/node()"
				       mode="bbDoc"/>
		</TEXT>
		<TYPE value="H"/>
	      </BODY>
	      <DATES>
		<CREATED value=""/>
		<UPDATED value="{$now}"/>
		<START value=""/>
		<END value=""/>
	      </DATES>
	      <FLAGS>
		<ISAVAILABLE value="true"/>
		<ISFROMCARTRIDGE value="false"/>
		<ISFOLDER value="false"/>
		<ISDESCRIBED value="false"/>
		<ISTRACKED value="false"/>
		<ISLESSON value="false"/>
		<ISSEQUENTIAL value="false"/>
		<ALLOWGUESTS value="true"/>
		<ALLOWOBSERVERS value="true"/>
		<LAUNCHINNEWWINDOW value="false"/>
		<ISREVIEWABLE value="false"/>
		<ISGROUPCONTENT value="false"/>
		<ISSAMPLECONTENT value="false"/>
	      </FLAGS>
	      <CONTENTHANDLER value="resource/x-bb-document"/>
	      <RENDERTYPE value="REGULAR"/>
	      <URL value=""/>
	      <VIEWMODE value="TEXT_ICON_ONLY"/>
	      <OFFLINENAME value=""/>
	      <OFFLINEPATH value=""/>
	      <LINKREF value=""/>
	      <PARENTID value="{$parentID}"/>
	      <VERSION value="3"/>
	      <EXTENDEDDATA/>
	      <FILES/>
	    </CONTENT>
	  </xsl:result-document>
	</xsl:if>

	<xsl:apply-templates select="/imscc/outline/topic" mode="resources"/>
      </resources>
    </manifest>
    <xsl:result-document 
	href=".bb-package-info"
	format="textual">
      <xsl:text>#Bb PackageInfo Property File
#</xsl:text>
      <xsl:value-of select='$now'/>
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

      <xsl:result-document 
	href="res00001.dat"
	format="resources">
	<COURSETOC id="{concat('toc-', generate-id())}">
	  <LABEL value="Modules"/>
	  <URL value=""/>
	  <TARGETTYPE value="CONTENT"/>
	  <INTERNALHANDLE value="content"/>
	  <DATES>
	    <CREATED value=""/>
	    <UPDATED value="{$now}"/>
	    <START value=""/>
	    <END value=""/>
	  </DATES>
	  <FLAGS>
	    <LAUNCHINNEWWINDOW value="false"/>
	    <ISENABLED value="true"/>
	    <ISENTYRPOINT value="false"/>
	    <ALLOWOBSERVERS value="false"/>
	    <ALLOWGUESTS value="false"/>
	  </FLAGS>
	</COURSETOC>
    </xsl:result-document>

    <xsl:variable name="tocContentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select="/imscc/outline"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:result-document 
	href="res00002.dat"
	format="resources">
      <CONTENT id="{$tocContentID}">
	<TITLE value="--TOP--"/>
	<TITLECOLOR value="#000000"/>
	<BODY>
	  <TEXT/>
	  <TYPE value="S"/>
	</BODY>
	<DATES>
	  <CREATED value=""/>
	  <UPDATED value="{$now}"/>
	  <START value=""/>
	  <END value=""/>
	</DATES>
	<FLAGS>
	  <ISAVAILABLE value="true"/>
	  <ISFROMCARTRIDGE value="false"/>
	  <ISFOLDER value="true"/>
	  <ISDESCRIBED value="false"/>
	  <ISTRACKED value="false"/>
	  <ISLESSON value="false"/>
	  <ISSEQUENTIAL value="false"/>
	  <ALLOWGUESTS value="true"/>
	  <ALLOWOBSERVERS value="true"/>
	  <LAUNCHINNEWWINDOW value="false"/>
	  <ISREVIEWABLE value="false"/>
	  <ISGROUPCONTENT value="false"/>
	  <ISSAMPLECONTENT value="false"/>
	</FLAGS>
	<CONTENTHANDLER value="resource/x-bb-folder"/>
	<RENDERTYPE value="REGULAR"/>
	<URL value=""/>
	<VIEWMODE value="TEXT_ICON_ONLY"/>
	<OFFLINENAME value=""/>
	<OFFLINEPATH value=""/>
	<LINKREF value=""/>
	<PARENTID value="{'{unset id}'}"/>
	<VERSION value="3"/>
	<EXTENDEDDATA/>
	<FILES/>
      </CONTENT>
    </xsl:result-document>

  </xsl:template>


  <xsl:template match="topic">
    <xsl:variable name="topicID" select="generate-id()"/>
    <item identifier="{$topicID}" 
	  identifierref="{concat('res-',$topicID)}">
      <title>
	<xsl:call-template name="getTitle"/>
	<xsl:text> </xsl:text>
	<xsl:call-template name="dateAttributes"/>
      </title>
      <xsl:apply-templates select="description"/>
      <xsl:apply-templates select="topic | item | subject"/>
    </item>
  </xsl:template>


  <xsl:template match="topic | subject" mode="resources">
    <xsl:variable name="topicID" select="generate-id()"/>
    <xsl:variable name="theTitle">
      <xsl:call-template name="getTitle"/>
      <xsl:text> </xsl:text>
      <xsl:call-template name="dateAttributes"/>
    </xsl:variable>
    <resource
	identifier="{concat('res-',$topicID)}"
	bb:file="{concat('res-',$topicID,'.dat')}"
	bb:title="{normalize-space($theTitle)}"
	type="resource/x-bb-document"
	xml:base="{concat('res-',$topicID)}"
	/>
    <xsl:variable name="contentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="parentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:result-document 
	href="{concat('res-', $topicID, '.dat')}"
	format="resources">
      <CONTENT id="{$contentID}">
	<TITLE>
	  <xsl:attribute name="value">
	    <xsl:call-template name="getTitle"/>
	    <xsl:text> </xsl:text>
	    <xsl:call-template name="dateAttributes"/>
	    </xsl:attribute>
	</TITLE>
	<TITLECOLOR value="#000000"/>
	<BODY>
	  <TEXT/>
	  <TYPE value="H"/>
	</BODY>
	<DATES>
	  <CREATED value=""/>
	  <UPDATED value="{$now}"/>
	  <START value=""/>
	  <END value=""/>
	</DATES>
	<FLAGS>
	  <ISAVAILABLE value="true"/>
	  <ISFROMCARTRIDGE value="false"/>
	  <ISFOLDER value="true"/>
	  <ISDESCRIBED value="false"/>
	  <ISTRACKED value="false"/>
	  <ISLESSON>
	    <xsl:attribute name="value">
	      <xsl:choose>
		<xsl:when test="../topic">
		  <xsl:text>false</xsl:text>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:text>true</xsl:text>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	  </ISLESSON>
	  <ISSEQUENTIAL value="false"/>
	  <ALLOWGUESTS value="true"/>
	  <ALLOWOBSERVERS value="true"/>
	  <LAUNCHINNEWWINDOW value="false"/>
	  <ISREVIEWABLE value="false"/>
	  <ISGROUPCONTENT value="false"/>
	  <ISSAMPLECONTENT value="false"/>
	</FLAGS>
	<CONTENTHANDLER>
	  <xsl:attribute name="value">
	    <xsl:choose>
	      <xsl:when test="./ancestor::topic">
		<xsl:text>resource/x-bb-folder</xsl:text>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:text>resource/x-bb-lesson</xsl:text>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:attribute>
	</CONTENTHANDLER>
	<RENDERTYPE value="REGULAR"/>
	<URL value=""/>
	<VIEWMODE value="TEXT_ICON_ONLY"/>
	<OFFLINENAME value=""/>
	<OFFLINEPATH value=""/>
	<LINKREF value=""/>
	<PARENTID value="{$parentID}"/>
	<VERSION value="3"/>
	<EXTENDEDDATA>
	  <xsl:if test="local-name(..) = 'outline'">
	    <ENTRY key="HierarchyDisplay">None</ENTRY>
	    <ENTRY key="ShouldHideToc">false</ENTRY>
	  </xsl:if>
	</EXTENDEDDATA>
	<FILES/>
      </CONTENT>
    </xsl:result-document>
    <xsl:apply-templates select="description | topic | item | subject" mode="resources"/>
  </xsl:template>


  <xsl:template match="description">
    <xsl:variable name="descriptionID" select="generate-id()"/>
    <item identifier="{$descriptionID}">
      <xsl:attribute name="identifierref">
	<xsl:value-of select="concat('res-',$descriptionID)"/>
      </xsl:attribute>
      <title>Overview</title>
    </item>
  </xsl:template>


  <xsl:template match="description" mode="resources">
    <xsl:variable name="descriptionID" select="generate-id()"/>
    <xsl:variable name="resourceID" select="concat('res-',$descriptionID)"/>
    <xsl:variable name="fileName" 
		  select="concat('res-', $descriptionID, '.dat')"/>
    <resource identifier="{$resourceID}"
	      bb:title="Overview"
	      type="resource/x-bb-document" 
	      bb:file="{$fileName}"
	      xml:base="{$resourceID}"
	      >
    </resource>

    <xsl:variable name="contentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="parentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:result-document 
	href="{concat('res-', $descriptionID, '.dat')}"
	format="resources">
      <CONTENT id="{$contentID}">
	<TITLE value="Overview"/>
	<TITLECOLOR value="#000000"/>
	<BODY>
	  <TEXT>
	    <xsl:text>&lt;div&gt;</xsl:text>
	    <xsl:apply-templates select="*|text()" mode="generateDescription"/>
	    <xsl:text>&lt;/div&gt;</xsl:text>
	  </TEXT>
	  <TYPE value="H"/>
	</BODY>
	<DATES>
	  <CREATED value=""/>
	  <UPDATED value="{$now}"/>
	  <START value=""/>
	  <END value=""/>
	</DATES>
	<FLAGS>
	  <ISAVAILABLE value="true"/>
	  <ISFROMCARTRIDGE value="false"/>
	  <ISFOLDER value="false"/>
	  <ISDESCRIBED value="false"/>
	  <ISTRACKED value="false"/>
	  <ISLESSON value="false"/>
	  <ISSEQUENTIAL value="false"/>
	  <ALLOWGUESTS value="true"/>
	  <ALLOWOBSERVERS value="true"/>
	  <LAUNCHINNEWWINDOW value="false"/>
	  <ISREVIEWABLE value="false"/>
	  <ISGROUPCONTENT value="false"/>
	  <ISSAMPLECONTENT value="false"/>
	</FLAGS>
	<CONTENTHANDLER value="resource/x-bb-document"/>
	<RENDERTYPE value="REGULAR"/>
	<URL value=""/>
	<VIEWMODE value="TEXT_ICON_ONLY"/>
	<OFFLINENAME value=""/>
	<OFFLINEPATH value=""/>
	<LINKREF value=""/>
	<PARENTID value="{$parentID}"/>
	<VERSION value="3"/>
	<EXTENDEDDATA/>
	<FILES/>
      </CONTENT>
    </xsl:result-document>
  </xsl:template>

  <xsl:template match="objectives" mode="generateDescription">
    <xsl:text>
    &lt;h2&gt;Objectives&lt;/h2&gt;
    &lt;p&gt;
      At the end of this module, students will be able to:
    &lt;/p&gt;
    &lt;ol&gt;
    </xsl:text>
      <xsl:apply-templates select="*|text()" mode="generateDescription"/>
    <xsl:text>
    &lt;/ol&gt;
    </xsl:text>
  </xsl:template>

  <xsl:template match="overview" mode="generateDescription">
    <xsl:apply-templates select="*|text()" mode="bbDoc"/>
  </xsl:template>


  <xsl:template match="obj" mode="generateDescription">
    <xsl:text>
    &lt;li&gt;
    </xsl:text>
    <xsl:apply-templates select="*|text()" mode="bbDoc"/>
    <xsl:text>
    &lt;/li&gt;
    </xsl:text>
  </xsl:template>


  <xsl:template match="*" mode="generateDescription">
    <xsl:variable name="firstLetter" select="upper-case(substring(local-name(),1,1))"/>
    <xsl:variable name="remainder" select="substring(local-name(),2)"/>
    <xsl:variable name="combined" select="concat($firstLetter, $remainder)"/>
    <xsl:text>&lt;h2&gt;</xsl:text>
    <xsl:value-of select="$combined"/>
    <xsl:text>&lt;/h2&gt;</xsl:text>
    <xsl:apply-templates select="*|text()" mode="bbDoc"/>
  </xsl:template>

  <xsl:template match="text()" mode="generateDescription">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="a[@href != '']" mode="bbDoc">
    <xsl:message>
      <xsl:text>link to </xsl:text>
      <xsl:value-of select="@href"/>
    </xsl:message>

    <xsl:choose>
      <xsl:when test="starts-with(@href, '../../')">
	<xsl:text>&lt;a target="_blank" href="</xsl:text>
	<xsl:value-of select="concat($baseURL, 
                              substring-after(@href, '../..'))"/>
	<xsl:text>"&gt;</xsl:text>
	<xsl:apply-templates select="*|text()" mode="bbDoc"/>
	<xsl:text>&lt;/a&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="starts-with(@href, '../')">
	<xsl:text>&lt;a target="_blank" href="</xsl:text>
	<xsl:value-of select="concat($baseURL, 
		           '/Directory/',
                           substring-after(@href, '../'))"/>
	<xsl:text>"&gt;</xsl:text>
	<xsl:apply-templates select="*|text()" mode="bbDoc"/>
	<xsl:text>&lt;/a&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>&lt;a target="_blank" href="</xsl:text>
	<xsl:value-of select="@href"/>
	<xsl:text>"&gt;</xsl:text>
	<xsl:apply-templates select="*|text()" mode="bbDoc"/>
	<xsl:text>&lt;/a&gt;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="*" mode="bbDoc">
    <xsl:variable name="element" select="local-name()"/>
    <xsl:value-of select="concat('&lt;', $element, ' ')"/>
    <xsl:for-each select="@*">
      <xsl:value-of select="concat(name(.), '=&quot;', ., '&quot; ')"/>
    </xsl:for-each>
    <xsl:text>'&gt;</xsl:text>
    <xsl:apply-templates select="*|text()" mode="bbDoc"/>
    <xsl:value-of select="concat('&lt;/', $element, '&gt;')"/>
  </xsl:template>

  <xsl:template match="text()" mode="bbDoc">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="subject">
    <xsl:variable name="subjectID" select="generate-id()"/>
    <item identifier="{$subjectID}" 
	  identifierref="{concat('res-',$subjectID)}">
      <title>
	<xsl:call-template name="getTitle"/>
      <xsl:text> </xsl:text>
	<xsl:call-template name="dateAttributes"/>
      </title>
      <xsl:apply-templates select="item"/>
    </item>
  </xsl:template>




  <xsl:template match="item">
    <xsl:variable name="itemID" select="generate-id()"/>
    <item identifier="{$itemID}">
      <xsl:attribute name="identifierref">
	<xsl:value-of select="concat('res-',$itemID)"/>
      </xsl:attribute>
      <title>
	<xsl:call-template name="kindPrefix"/>
	<xsl:call-template name="getTitle"/>
	<xsl:call-template name="dateAttributes"/>
      </title>
    </item>
  </xsl:template>




  <xsl:template match="item" mode="resources">
    <xsl:variable name="itemID" select="generate-id()"/>
    <xsl:variable name="linkHref">
      <xsl:choose>
	<xsl:when test="@targetdoc">
	  <xsl:value-of select="concat($baseURL, '/Public/', 
				       @targetdoc, '/index.html')"/>
	</xsl:when>
	<xsl:when test="@target">
	  <xsl:value-of select="concat($baseURL, '/Public/', 
                                       @target, '/index.html')"/>
	</xsl:when>
	  
	<xsl:when test="@assignment">
	  <xsl:value-of select="concat($baseURL, '/Protected/Assts/', 
				       @assignment, '.mmd.html')"/>
	</xsl:when>
	  
	<xsl:when test="starts-with(@href, '../../')">
	  <xsl:value-of select="concat($baseURL, '/',
				substring-after(@href, '../../'))"/>
	</xsl:when>
	<xsl:when test="@href != ''">
	  <xsl:value-of select="@href"/>
	</xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="fileName" select="concat('res-',$itemID,'.dat')"/>

    <xsl:variable name="theTitle">
      <xsl:call-template name="kindPrefix"/>
      <xsl:call-template name="getTitle"/>
      <xsl:text> </xsl:text>
      <xsl:call-template name="dateAttributes"/>
    </xsl:variable>

    <resource
	identifier="{concat('res-',$itemID)}"
	bb:file="{concat('res-',$itemID,'.dat')}"
	bb:title="{normalize-space($theTitle)}"
	type="resource/x-bb-document"
	xml:base="{concat('res-',$itemID)}"
	/>

    <xsl:variable name="contentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="parentID">
      <xsl:call-template name="contentID">
	<xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:variable>


    <xsl:result-document 
	href="{$fileName}"
	format="resources">
      <CONTENT id="{$contentID}">
	<TITLE>
	  <xsl:attribute name="value">
	    <xsl:call-template name="kindPrefix"/>
	    <xsl:call-template name="getTitle"/>
	    <xsl:text> </xsl:text>
	    <xsl:call-template name="dateAttributes"/>
	  </xsl:attribute>
	</TITLE>
	<TITLECOLOR value="#000000"/>
	<BODY>
	  <TEXT/>
	  <TYPE value="H"/>
	</BODY>
	<DATES>
	  <CREATED value=""/>
	  <UPDATED value="{$now}"/>
	  <START value=""/>
	  <END value=""/>
	</DATES>
	<FLAGS>
	  <ISAVAILABLE value="true"/>
	  <ISFROMCARTRIDGE value="false"/>
	  <ISFOLDER value="false"/>
	  <ISDESCRIBED value="false"/>
	  <ISTRACKED value="false"/>
	  <ISLESSON value="false"/>
	  <ISSEQUENTIAL value="false"/>
	  <ALLOWGUESTS value="true"/>
	  <ALLOWOBSERVERS value="true"/>
	  <LAUNCHINNEWWINDOW value="true"/>
	  <ISREVIEWABLE value="false"/>
	  <ISGROUPCONTENT value="false"/>
	  <ISSAMPLECONTENT value="false"/>
	</FLAGS>
	<CONTENTHANDLER value="resource/x-bb-externallink"/>
	<RENDERTYPE value="URL"/>
	<URL value="{$linkHref}"/>
	<VIEWMODE value="TEXT_ICON_ONLY"/>
	<OFFLINENAME value=""/><OFFLINEPATH value=""/>
	<LINKREF value=""/>
	<PARENTID value="{$parentID}"/>
	<VERSION value="3"/>
        <EXTENDEDDATA/>
	<FILES/>
      </CONTENT>
    </xsl:result-document >


    <!-- Now add calendar entries if appropriate -->

    <xsl:if test="@date != '' or @due != ''">
      <xsl:variable name="ident" select="generate-id()"/>
      <xsl:variable name="eventTitle">
	<xsl:call-template name="getTitle"/>
      </xsl:variable> 


      <resource bb:title="{/imscc/courseName/text()}: {$eventTitle}" 
		identifier="date-{$ident}"
		bb:file="date-{$ident}.dat"
		type="resource/x-bb-calendar"
		xml:base="date-{$ident}"/>

      <xsl:result-document 
          href="date-{$ident}.dat"
	      format="resources">
          <CALENDAR id="date-{$ident}">
	    <TITLE value="{/imscc/courseName/text()}: {$eventTitle}"/>
	    <DESCRIPTION>
	      <TEXT>
		<xsl:if test="$linkHref != ''">
		  <!--
		      As far as I can tell, BB does not support calendar
		      events that link to other pages. -->
		  <xsl:text>Details at </xsl:text>
		  <xsl:value-of select="$linkHref"/>
		</xsl:if>
	      </TEXT>
	      <TYPE value="S"/>
	    </DESCRIPTION>
	    <!-- USERID value="??"/ -->
	    <TYPE value="COURSE"/>
	    <DATES>
	      <CREATED value=""/>
	      <UPDATED value="{$now}"/>
	      <xsl:call-template name="startAndStop"/>
	    </DATES>
	  </CALENDAR>
      </xsl:result-document>



    </xsl:if>

  </xsl:template>



  <xsl:template name="startAndStop">
    <xsl:choose>
      <xsl:when test="@enddate != ''">
	<START>
	  <xsl:attribute name="value">
	    <xsl:call-template name="bbDate">
	      <xsl:with-param name="date"
			      select="@date"/>
	    </xsl:call-template>
	  </xsl:attribute>
	</START>
	<END>
	  <xsl:attribute name="value">
	    <xsl:call-template name="bbEndDate">
	      <xsl:with-param name="date"
			      select="@enddate"/>
	    </xsl:call-template>
	  </xsl:attribute>
	</END>
      </xsl:when>

      <xsl:when test="@date != '' and @due != ''">
	<START>
	  <xsl:attribute name="value">
	    <xsl:call-template name="bbDate">
	      <xsl:with-param name="date"
			      select="@date"/>
	    </xsl:call-template>
	  </xsl:attribute>
	</START>
	<END>
	  <xsl:attribute name="value">
	    <xsl:call-template name="bbEndDate">
	      <xsl:with-param name="date"
			      select="@due"/>
	    </xsl:call-template>
	  </xsl:attribute>
	</END>
      </xsl:when>
      
      <xsl:when test="@date != ''">
	<xsl:variable name="startDateTime">
	  <xsl:call-template name="bbDate">
	    <xsl:with-param name="date" select="@date"/>
	  </xsl:call-template>
	</xsl:variable>
	  
	<START value="{$startDateTime}"/>
	
	<xsl:choose>
	  <xsl:when test="contains(@date, 'T')">
	    <xsl:variable name="oneSecondLater">
	      <xsl:value-of 
		  xmlns:date="java:java.util.Date"
		  xmlns:sdf="java:java.text.SimpleDateFormat"
		  select='sdf:format(
			  sdf:new("yyyy-MM-dd HH:mm:ss z"), 
			  date:new(
			  date:getTime(
			  sdf:parse(
			  sdf:new("yyyy-MM-dd HH:mm:ss z"), 
			  $startDateTime)) 
			  + 1000))'/>
	    </xsl:variable>
	    <END value="{$oneSecondLater}"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:variable name="endDateTime">
	      <xsl:call-template name="bbDate">
		<xsl:with-param name="date" select="concat(@date,'T23:59:59')"/>
	      </xsl:call-template>
	    </xsl:variable>
	    <END value="{$endDateTime}"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>


      <xsl:when test="@due != ''">
	<xsl:variable name="startDateTime">
	  <xsl:choose>
	    <xsl:when test="contains(@due, 'T')">
	      <xsl:call-template name="bbDate">
		<xsl:with-param name="date" select="@due"/>
	      </xsl:call-template>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:call-template name="bbDate">
		<xsl:with-param name="date" 
				select="concat(@due,'T23:59:00')"/>
	      </xsl:call-template>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
      
	<START value="{$startDateTime}"/>
	
	<xsl:variable name="oneMinuteLater">
	  <xsl:value-of 
	      xmlns:date="java:java.util.Date"
	      xmlns:sdf="java:java.text.SimpleDateFormat"
	      select='sdf:format(
		      sdf:new("yyyy-MM-dd HH:mm:ss z"), 
		      date:new(
		      date:getTime(
		      sdf:parse(
		      sdf:new("yyyy-MM-dd HH:mm:ss z"), 
		      $startDateTime)) 
		      + 59000))'/>
	</xsl:variable>
	<END value="{$oneMinuteLater}"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>






  <xsl:template match="file" mode="resources">
    <xsl:choose>
      <xsl:when test="ends-with(text(), '__canvas.html')">
	<xsl:variable name="fileName">
	  <xsl:call-template name="stripDirectories">
	    <xsl:with-param name="path" select="text()"/>
	  </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="doc"
		      select="substring-before($fileName, '__canvas.html')"/>
	<xsl:variable name="wikiPage"
		      select="concat('wiki_content/', $doc, '__canvas.html')"/>
	<resource
	    type="webcontent"
	    identifier="{concat('wiki-', $doc)}"
	    href="{$wikiPage}">
	  <file href="{$wikiPage}"/>
	</resource>	
      </xsl:when>
      <xsl:otherwise>
	<resource
	    type="webcontent"
	    identifier="{concat('file-',generate-id())}"
	    href="{concat('web_resources/', text())}">
	  <file href="{concat('web_resources/', text())}"/>
	</resource>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="stripDirectories">
    <xsl:param name="path" select="foo/bar/baz.html"/>
    <xsl:choose>
      <xsl:when test="contains($path, '/')">
	<xsl:call-template name="stripDirectories">
	  <xsl:with-param name="path" select="substring-after($path, '/')"/>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$path"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="file" mode="files">
  </xsl:template>

  <xsl:template name="listResourceFiles">
    <xsl:param name="directory" select="'garbage/'"/>

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
    <xsl:if test="local-name() = 'topic'">
      <xsl:apply-templates select='.' mode="itemNumber"/>
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="@title != ''">
	<xsl:value-of select="@title"/>
      </xsl:when>
      <xsl:when test="normalize-space(.) != ''">
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:when>
      <xsl:when test="@targetdoc != ''">
	<xsl:variable name="theDoc" 
		      select="@targetdoc"/>
	<xsl:variable name="theTitle" 
		      select="/imscc/table/title[@doc=$theDoc]"/>
	<xsl:value-of select="normalize-space($theTitle)"/>
      </xsl:when>
      <xsl:when test="@target != ''">
	<xsl:variable name="theDoc" 
		      select="@target"/>
	<xsl:variable name="theTitle" 
		      select="/imscc/table/title[@doc=$theDoc]"/>
	<xsl:value-of select="normalize-space($theTitle)"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>???</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="dateAttributes">
    <xsl:choose>
      <xsl:when test="@enddate != ''">
	<xsl:text> </xsl:text>
	<xsl:text>(</xsl:text>
	<xsl:call-template name="formatDate">
	  <xsl:with-param name="date" select="@date"/>
	</xsl:call-template>
	<xsl:text> - </xsl:text>
	<xsl:call-template name="formatEndDate">
	  <xsl:with-param name="date" select="@enddate"/>
	</xsl:call-template>
	<xsl:text>)</xsl:text>
      </xsl:when>
      
      <xsl:when test="@date != '' and @due != ''">
	<xsl:text> </xsl:text>
	<xsl:text>(</xsl:text>
	<xsl:call-template name="formatDate">
	  <xsl:with-param name="date" select="@date"/>
	</xsl:call-template>
	<xsl:text> - </xsl:text>
	<xsl:call-template name="formatEndDate">
	  <xsl:with-param name="date" select="@due"/>
	</xsl:call-template>
	<xsl:text>)</xsl:text>
      </xsl:when>


      <xsl:when test="@date != ''">
	<xsl:text> </xsl:text>
	<xsl:text>(</xsl:text>
	<xsl:call-template name="formatDate">
	  <xsl:with-param name="date" select="@date"/>
	</xsl:call-template>
	
	<xsl:if test="@time != ''">
	  <xsl:text>, </xsl:text>
	  <xsl:value-of select="@time"/>
	</xsl:if>
	<xsl:text>)</xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="@due != ''">
      <xsl:text>(Due: </xsl:text>
      <xsl:call-template name="formatEndDate">
	<xsl:with-param name="date" select="@due"/>
      </xsl:call-template>
      
      <xsl:if test="@time != ''">
	<xsl:text>, </xsl:text>
	<xsl:value-of select="@time"/>
      </xsl:if>
      <xsl:text>)</xsl:text>
    </xsl:if>
  </xsl:template>



  <xsl:template name="formatDate">
    <xsl:param name="date" select="'2005-01-01T13:00:00-05:00'"/>
    <xsl:choose>
      <xsl:when test="contains($date, 'T')">
	<!-- Both a date and a time are indicated. -->
	<xsl:variable name="timePart" select="substring-after($date, 'T')"/>
	<xsl:choose>
	  <xsl:when test="contains(timePart, '-') or contains(timePart, '+')">
	    <!-- DateTime includes ISO time zone -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy HH:mmaa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"),
                           $date))'/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy HH:mmaa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date,":00")))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(
                           sdf:new("MM/dd/yyyy"), 
		           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
		                     concat($date, "T00:00:00")
                                     )
                           )'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



  <xsl:template name="formatEndDate">
    <xsl:param name="date" select="'2005-01-01T23:59:59-05:00'"/>
    <xsl:choose>
      <xsl:when test="contains($date, 'T')">
	<!-- Both a date and a time are indicated. -->
	<xsl:variable name="timePart" select="substring-after($date, 'T')"/>
	<xsl:choose>
	  <xsl:when test="contains(timePart, '-') or contains(timePart, '+')">
	    <!-- DateTime includes ISO time zone -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy HH:mmaa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"),
                           $date))'/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy HH:mmaa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date,":00")))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(sdf:new("MM/dd/yyyy"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date, "T23:59:59")))'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="bbDate">
    <xsl:param name="date" select="'2005-01-01T13:00:00-05:00'"/>
    <xsl:choose>
      <xsl:when test="contains($date, 'T')">
	<!-- Both a date and a time are indicated. -->
	<xsl:variable name="timePart" select="substring-after($date, 'T')"/>
	<xsl:choose>
	  <xsl:when test="contains(timePart, '-') or contains(timePart, '+')">
	    <!-- DateTime includes ISO time zone -->
	    <xsl:value-of select="$date"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date,":00")))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date, "T00:00:00")))'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="bbEndDate">
    <xsl:param name="date" select="'2005-01-01T13:00:00-05:00'"/>
    <xsl:choose>
      <xsl:when test="contains($date, 'T')">
	<!-- Both a date and a time are indicated. -->
	<xsl:variable name="timePart" select="substring-after($date, 'T')"/>
	<xsl:choose>
	  <xsl:when test="contains(timePart, '-') or contains(timePart, '+')">
	    <!-- DateTime includes ISO time zone -->
	    <xsl:value-of select="$date"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date,":00")))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(sdf:new("yyyy-MM-dd HH:mm:ss z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date, "T23:59:59")))'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="contentID">
    <xsl:param name="node" select="."/>
    <xsl:variable name="genID" select="generate-id($node)"/>
    <xsl:variable name="decID">
      <xsl:call-template name="hex2dec">
	<xsl:with-param name="hex" select="$genID"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="concat('_',$decID,'_1')"/>
  </xsl:template>

  <xsl:template name="hex2dec">
    <xsl:param name="hex" select="0"/>
    <xsl:param name="num" select="0"/>

    <xsl:variable 
	name="digit" 
	select="translate(substring($hex, 1, 1), 'abcdef', 'ABCDEF')"/>
    <xsl:variable
	name="value" 
	select="string-length(substring-before('0123456789ABCDEF', $digit))"/>
    <xsl:variable name="result" select="16 * $num + $value"/>
    <xsl:choose>
      <xsl:when test="string-length($hex) > 1">
	<xsl:call-template name="hex2dec">
	  <xsl:with-param name="hex" select="substring($hex, 2)"/>
	  <xsl:with-param name="num" select="$result"/>
	</xsl:call-template>
        </xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$result"/>
	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="kindPrefix">
    <xsl:choose>
      <xsl:when test="@prefix">
	<xsl:value-of select="@prefix"/>
	<xsl:text> </xsl:text>
      </xsl:when>
      <xsl:when test="@kind">
	<xsl:variable name="kindNm" select="@kind"/>
	<xsl:variable name="kindDescription" select="/imscc/outline/presentation/kind[@name=$kindNm]"/>
	<xsl:choose>
	  <xsl:when test="$kindDescription">
	    <xsl:value-of select="$kindDescription/@prefix"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:variable name="firstLetter" select="upper-case(substring($kindNm,1,1))"/>
	    <xsl:variable name="remainder" select="substring($kindNm,2)"/>
	    <xsl:value-of select="concat($firstLetter, $remainder, ':')"/>
	  </xsl:otherwise>
	</xsl:choose>
	<xsl:text> </xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="outline" mode="itemNumber">
  </xsl:template>

  <xsl:template match="topic|item" mode="itemNumber">
    <xsl:apply-templates select=".." mode="itemNumber"/>
    <xsl:if test="local-name(..) = 'topic'">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="count(preceding-sibling::item | preceding-sibling::topic) + 1"/>
  </xsl:template>



</xsl:stylesheet>
