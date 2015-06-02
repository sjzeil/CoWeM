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
	>

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
	  <lomimscc:lifeCycle>
	    <lomimscc:contribute>
	      <lomimscc:date>
		<lomimscc:dateTime>
		  <xsl:value-of select="current-dateTime()"/>
		</lomimscc:dateTime>
	      </lomimscc:date>
	    </lomimscc:contribute>
	  </lomimscc:lifeCycle>
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
	<resource identifier="course-settings" 
		  type="associatedcontent/imscc_xmlv1p1/learning-application-resource"
		  href="course_settings/canvas_export.txt">
	  <file href="course_settings/course_settings.xml"/>
	  <file href="course_settings/module_meta.xml"/>
	  <file href="course_settings/assignment_groups.xml"/>
	  <file href="course_settings/files_meta.xml"/>
	  <file href="course_settings/events.xml"/>
	  <file href="course_settings/canvas_export.txt"/>
	</resource>

	<xsl:apply-templates select="/imscc/outline/topic" mode="resources"/>
	<xsl:apply-templates 
	    select="/imscc/files/file"
	    mode="resources"/>
      </resources>
    </manifest>
    <xsl:result-document
	href="course_settings/canvas_export.txt"
	format="textual">
      <xsl:text>Q: What did the panda say when he was forced out of his natural habitat?
A: This is un-BEAR-able
</xsl:text>
    </xsl:result-document>
    <xsl:result-document 
	href="course_settings/course_settings.xml"
	format="resources">
      <course identifier="{generate-id()}" 
	      xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://canvas.instructure.com/xsd/cccv1p0 http://canvas.instructure.com/xsd/cccv1p0.xsd"
	      >
	<title>Imported matter</title>
      </course>
    </xsl:result-document>
    <xsl:result-document 
	href="course_settings/assignment-groups.xml"
	format="resources">
      <assignmentGroups 
	  xmlns="http://canvas.instructure.com/xsd/cccv1p0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	  xsi:schemaLocation="http://canvas.instructure.com/xsd/cccv1p0 http://canvas.instructure.com/xsd/cccv1p0.xsd"
	  >
	<assignmentGroup identifier="{generate-id()}">
	  <title>Assignments</title>
	  <position>1</position>
	  <group_weight>0</group_weight>
	</assignmentGroup>
	<assignmentGroup identifier="{generate-id()}">
	  <title>Assignments</title>
	  <position>1</position>
	  <group_weight>0</group_weight>
	</assignmentGroup>
      </assignmentGroups>
    </xsl:result-document>
    <xsl:result-document 
	href="course_settings/files_meta.xml"
	format="resources">
      <fileMeta 
	  xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	  xsi:schemaLocation="http://canvas.instructure.com/xsd/cccv1p0 http://canvas.instructure.com/xsd/cccv1p0.xsd">
	<xsl:apply-templates select="/imscc/files/file" mode="files"/>
      </fileMeta>
    </xsl:result-document>
    <xsl:result-document 
	href="course_settings/module_meta.xml"
	format="resources">
      <modules 
	  xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	  xsi:schemaLocation="http://canvas.instructure.com/xsd/cccv1p0 http://canvas.instructure.com/xsd/cccv1p0.xsd">
	<xsl:apply-templates select="/imscc/outline/topic" mode="modules"/>
      </modules>
    </xsl:result-document>
    <xsl:result-document 
	href="course_settings/events.xml"
	format="resources">
      <events 
	  xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://canvas.instructure.com/xsd/cccv1p0 http://canvas.instructure.com/xsd/cccv1p0.xsd"
	  >
	<xsl:apply-templates select="/imscc/outline/topic" mode="events"/>
      </events>
    </xsl:result-document>
  </xsl:template>


  <xsl:template match="topic">
    <xsl:choose>
      <xsl:when test="item | subject">
	<xsl:variable name="topicID" select="generate-id()"/>
	<item identifier="{$topicID}">
	  <title>
	    <xsl:call-template name="getTitle"/>
	    <xsl:call-template name="dateAttributes"/>
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


  <xsl:template match="topic" mode="resources">
    <xsl:apply-templates select="topic | description | item | subject" 
			 mode="resources"/>
  </xsl:template>

  <xsl:template match="topic" mode="events">
    <xsl:apply-templates select="topic | item | subject" 
			 mode="events"/>
  </xsl:template>

  <xsl:template match="topic" mode="modules">
    <xsl:choose>
      <xsl:when test="item | subject">
	<xsl:variable name="topicID" select="generate-id()"/>
	<module xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
		identifier="{$topicID}">
	  <title>
	    <xsl:call-template name="getTitle"/>
	    <xsl:call-template name="dateAttributes"/>
	  </title>
	  <workflow_state>active</workflow_state>
	  <position>
	    <xsl:value-of select="1 + count(preceding-sibling::topic)"/>
	  </position>
	  <require_sequential_progress>false</require_sequential_progress>
	  <items>
	    <xsl:apply-templates select="description" mode="modules"/>
	    <xsl:apply-templates select="item | subject" mode="modules"/>
	  </items>
	</module>
      </xsl:when>
      <xsl:otherwise>
	<xsl:apply-templates select="topic" mode="modules"/>
      </xsl:otherwise>
    </xsl:choose>
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
    <xsl:variable name="fileName" 
		  select="concat('web_resources/Directory/outline/', $descriptionID, '__description.html')"/>
    <resource identifier="{concat('res-',$descriptionID)}"
	      type="webcontent" 
	      href="{$fileName}">
      <file href="{$fileName}"/>
    </resource>

    <xsl:result-document 
	href="{$fileName}"
	format="resources">
      <html>
	<head>
	  <title>
	    <xsl:value-of select="../@title"/>
	    <xsl:text> Overview</xsl:text>
	  </title>
	  <link rel="stylesheet" 
		type="text/css" media="screen, projection, print"
		href="../../styles/md-html.css" />
	  <meta name="viewport" 
		content="width=device-width, initial-scale=1"/>	
	  <script type="text/javascript"
		  src="{$MathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	    <xsl:text> </xsl:text>
	  </script>
	  <link rel="stylesheet" 
		href="{$highlightjsURL}/styles/googlecode.css"/>
	  <script src="{$highlightjsURL}/highlight.pack.js">
	    <xsl:text> </xsl:text>
	  </script>
	  <script>hljs.initHighlightingOnLoad();</script>
	</head>
	<body>
	  <h1>
	    <xsl:value-of select="../@title"/>
	  </h1>
	  <xsl:apply-templates select="*|text()" mode="generateDescription"/>
	</body>
      </html>
    </xsl:result-document>
  </xsl:template>

  <xsl:template match="objectives" mode="generateDescription">
    <h2>Objectives</h2>
    <ol>
      <xsl:apply-templates select="*|text()" mode="generateDescription"/>
    </ol>
  </xsl:template>

  <xsl:template match="obj" mode="generateDescription">
    <li>
      <xsl:copy-of select="*|text()"/>
    </li>
  </xsl:template>


  <xsl:template match="*" mode="generateDescription">
    <xsl:variable name="firstLetter" select="upper-case(substring(local-name(),1,1))"/>
    <xsl:variable name="remainder" select="substring(local-name(),2)"/>
    <xsl:variable name="combined" select="concat($firstLetter, $remainder)"/>
    <h2>
      <xsl:value-of select="$combined"/>
    </h2>
    <xsl:copy-of select="*|text()"/>
  </xsl:template>

  <xsl:template match="text()" mode="generateDescription">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="description" mode="modules">
    <xsl:variable name="descriptionID" select="generate-id()"/>
    <item xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	  identifier="{$descriptionID}">
      <content_type>Attachment</content_type>
      <title>Overview</title>
      <workflow_state>active</workflow_state>
      <identifierref>
	<xsl:value-of select="concat('res-',$descriptionID)"/>
      </identifierref>
      <position>0</position>
      <new_tab></new_tab>
      <indent>1</indent>
    </item>
  </xsl:template>

  <xsl:template match="subject">
    <xsl:variable name="subjectID" select="generate-id()"/>
    <item identifier="{$subjectID}">
      <title>
	<xsl:call-template name="getTitle"/>
	<xsl:call-template name="dateAttributes"/>
      </title>
    </item>
    <xsl:apply-templates select="item | subject"/>
  </xsl:template>


  <xsl:template match="subject"  mode="modules">
    <xsl:variable name="subjectID" select="generate-id()"/>
    <item xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	  identifier="{$subjectID}">
      <content_type>ContextModuleSubHeader</content_type>
      <title>
	<xsl:call-template name="getTitle"/>
	<xsl:call-template name="dateAttributes"/>
      </title>
      <workflow_state>active</workflow_state>
      <position>
	<xsl:variable name="count1"
		      select="count(preceding::subject | preceding::topic | ancestor::subject | ancestor::topic | preceding::item)"/>
	<xsl:variable name="count2"
		  select="count(ancestor::topic/preceding::subject | ancestor::topic/preceding::topic | ancestor::topic | ancestor::topic/preceding::item)"/>
	<xsl:value-of select="$count1 - $count2 + 1"/>
      </position>
      <new_tab></new_tab>
      <indent>0</indent>
    </item>
    <xsl:apply-templates select="item | subject" mode="modules"/>
  </xsl:template>
  	  

  <xsl:template match="subject" mode="resources">
    <xsl:apply-templates select="item | subject" mode="resources"/>
  </xsl:template>

  <xsl:template match="subject" mode="events">
    <xsl:apply-templates select="item | subject" mode="events"/>
  </xsl:template>


  <xsl:template match="item">
    <xsl:variable name="itemID" select="generate-id()"/>
    <item identifier="{$itemID}">
      <xsl:if test="@href | @bblink | @target | @targetdoc | @assignment">
	<xsl:attribute name="identifierref">
	  <xsl:value-of select="concat('res-',$itemID)"/>
	</xsl:attribute>
      </xsl:if>
      <title>
	<xsl:call-template name="kindPrefix"/>
	<xsl:call-template name="getTitle"/>
	<xsl:call-template name="dateAttributes"/>
      </title>
    </item>
  </xsl:template>


  <xsl:template match="item" mode="modules">
    <xsl:variable name="itemID" select="generate-id()"/>
	<xsl:variable 
	    name="count1"
	    select="count(preceding::subject | preceding::topic | ancestor::subject | ancestor::topic | preceding::item)"/>
	<xsl:variable 
	    name="count2"
	    select="count(ancestor::topic/preceding::subject | ancestor::topic/preceding::topic | ancestor::topic | ancestor::topic/preceding::item)"/>

	<item xmlns="http://canvas.instructure.com/xsd/cccv1p0" 
	      identifier="{$itemID}">
	  <title>
	    <xsl:call-template name="kindPrefix"/>
	    <xsl:call-template name="getTitle"/>
	    <xsl:call-template name="dateAttributes"/>
	  </title>
	  <workflow_state>active</workflow_state>
	  <position>
	    <xsl:value-of select="$count1 - $count2 + 1"/>
	  </position>

	  <xsl:choose>
	    <xsl:when test="@targetdoc | @target | @assignment">
	      <content_type>Attachment</content_type>
	      <new_tab></new_tab>
	      <indent>1</indent>
	      <identifierref>
		<xsl:value-of select="concat('res-',$itemID)"/>
	      </identifierref>
	    </xsl:when>
	    
	    <xsl:when test="starts-with(@href, '../../')">
	      <content_type>Attachment</content_type>
	      <new_tab></new_tab>
	      <indent>1</indent>
	      <identifierref>
		<xsl:value-of select="concat('res-',$itemID)"/>
	      </identifierref>
	    </xsl:when>
	    
	    <xsl:when test="@href != ''">
	      <content_type>ExternalUrl</content_type>
	      <new_tab>true</new_tab>
	      <indent>1</indent>
	      <identifierref>
		<xsl:value-of select="$itemID"/>
	      </identifierref>
	      <url>
		<xsl:value-of select="@href"/>
	      </url>
	    </xsl:when>

	    <xsl:otherwise>
	      <content_type>ContextModuleSubHeader</content_type>
	      <new_tab></new_tab>
	      <indent>0</indent>
	    </xsl:otherwise>



      </xsl:choose>
    </item>
  </xsl:template>


  <xsl:template match="item" mode="resources">
    <xsl:variable name="itemID" select="generate-id()"/>
    <xsl:if test="@targetdoc | @target | @assignment | @href">
      <xsl:choose>
	<xsl:when test="@targetdoc">
	  <xsl:variable 
	      name="fileName"
	      select="concat('web_resources/Public/',@targetdoc, '/index.html')"/>
	  <resource 
	      identifier="{concat('res-',$itemID)}"
	      type="webcontent"
	      href="{$fileName}"
	      >
	    <file href="{$fileName}"/>
	  </resource>
	</xsl:when>
	  
	<xsl:when test="@target">
	  <xsl:variable 
	      name="fileName"
	      select="concat('web_resources/Public/',@target, '/index.html')"/>
	  <resource 
	      identifier="{concat('res-',$itemID)}"
	      type="webcontent"
	      href="{$fileName}"
	      >
	    <file href="{$fileName}"/>
	  </resource>
	  </xsl:when>
	  
	  <xsl:when test="@assignment">
	    <xsl:variable 
		name="fileName"
		select="concat('web_resources/Public/',@target, '/index.html')"/>
	    <resource 
		identifier="{concat('res-',$itemID)}"
		type="webcontent"
		href="{$fileName}"
		>
	      <file href="{$fileName}"/>
	    </resource>
	  </xsl:when>
	  
	  <xsl:when test="starts-with(@href, '../../')">
	    <xsl:variable 
		name="fileName"
		select="concat('web_resources/', substring-after(@href, '../../'))"/>
	    <resource 
		identifier="{concat('res-',$itemID)}"
		type="webcontent"
		href="{$fileName}"
		>
	      <file href="{$fileName}"/>
	    </resource>
	  </xsl:when>

	  <xsl:when test="@href != ''">
	    <xsl:variable name="fileName" select="concat('res-',$itemID,'.xml')"/>
	    <resource 
		identifier="{concat('res-',$itemID)}"
		type="imswl_xmlv1p1"
		href="{$fileName}"
		>
	      <file href="{$fileName}"/>
	    </resource>
	    <xsl:result-document 
		href="{$fileName}"
		format="resources">
	      <webLink 
		  xmlns="http://www.imsglobal.org/xsd/imsccv1p1/imswl_v1p1" 
		  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		  xsi:schemaLocation="http://www.imsglobal.org/xsd/imsccv1p1/imswl_v1p1 http://www.imsglobal.org/profile/cc/ccv1p1/ccv1p1_imswl_v1p1.xsd">
		<title>
		  <xsl:call-template name="kindPrefix"/>
		  <xsl:call-template name="getTitle"/>
		</title>
		<url href="{@href}"/>
	      </webLink>
	    </xsl:result-document>
	  </xsl:when>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="item" mode="events">
    <xsl:variable name="itemID" select="generate-id()"/>
    <xsl:if test="@date|@due">
      <event xmlns="http://canvas.instructure.com/xsd/cccv1p0"
	     identifier="{concat('event-',$itemID)}">
	<title>
	  <xsl:call-template name="getTitle"/>
	  <xsl:if test="@enddate != ''">
	    <xsl:call-template name="dateAttributes"/>
	  </xsl:if>
	</title>
	<description>
	  details
	</description>
	  
	<xsl:choose>
	  <xsl:when test="@enddate != ''">
	    <start_at>
	      <xsl:call-template name="isoDate">
		<xsl:with-param name="date"
				select="@date"/>
		</xsl:call-template>
	    </start_at>
	    <end_at>
	      <xsl:call-template name="isoEndDate">
		<xsl:with-param name="date"
				select="@enddate"/>
		</xsl:call-template>
	    </end_at>
	  </xsl:when>
	  
	  <xsl:when test="@date != '' or @due != ''">
	    <xsl:variable name="startDateTime">
	      <xsl:choose>
		<xsl:when test="@date != ''">
		  <xsl:call-template name="isoDate">
		    <xsl:with-param name="date" select="@date"/>
		  </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:call-template name="isoEndDate">
		    <xsl:with-param name="date" select="@due"/>
		  </xsl:call-template>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:variable>

	    <start_at>
	      <xsl:value-of select="$startDateTime"/>
	    </start_at>

	    <xsl:variable name="oneSecondLater">
	      <xsl:value-of 
		  xmlns:date="java:java.util.Date"
		  xmlns:sdf="java:java.text.SimpleDateFormat"
		  select='sdf:format(
                    sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                    date:new(
                      date:getTime(
                        sdf:parse(
                          sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                          $startDateTime)) 
                      + 1000))'/>
	    </xsl:variable>
	    <xsl:message>
	      <xsl:value-of select="$startDateTime"/>
	      <xsl:text> - </xsl:text>
	      <xsl:value-of select="$oneSecondLater"/>
	    </xsl:message>
	    <end_at>
	      <xsl:value-of select="$oneSecondLater"/>
	    </end_at>
	  </xsl:when>
	</xsl:choose>
      </event>
    </xsl:if>
  </xsl:template>

  <xsl:template match="file" mode="resources">
    <resource
	type="webcontent"
	identifier="{concat('file-',generate-id())}"
	href="{concat('web_resources/', text())}">
      <file href="{concat('web_resources/', text())}"/>
    </resource>
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

  <xsl:template name="kindPrefix">
    <xsl:choose>
      <xsl:when test="@prefix">
	<xsl:value-of select="@prefix"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="kindNm" select="@kind"/>
	<xsl:variable name="kindDescription" select="/imscc/outline/presentation/kind[@name=$kindNm]"/>
	<xsl:choose>
	  <xsl:when test="$kindDescription">
	    <xsl:value-of select="$kindDescription/@prefix"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:variable name="firstLetter" select="upper-case(substring($kindNm,1,1))"/>
	    <xsl:variable name="remainder" select="substring($kindNm,2)"/>
	    <xsl:value-of select="concat($firstLetter, $remainder, ': ')"/>
	  </xsl:otherwise>
	</xsl:choose>
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
		select='sdf:format(sdf:new("MM/dd/yyyy hh:mm aa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"),
                           $date))'/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy hh:mm aa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           $date))'/>
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
		select='sdf:format(sdf:new("MM/dd/yyyy hh:mm aa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"),
                           $date))'/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- Time zone unspecified - use locale -->
	    <xsl:value-of 
		xmlns:date="java:java.util.Date"
		xmlns:sdf="java:java.text.SimpleDateFormat"
		select='sdf:format(sdf:new("MM/dd/yyyy hh:mm aa z"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           $date))'/>
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


  <xsl:template name="isoDate">
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
		select='sdf:format(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           $date))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date, "T00:00:00")))'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="isoEndDate">
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
		select='sdf:format(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           $date))'/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<!-- only a date is given -->
	<xsl:value-of 
	    xmlns:date="java:java.util.Date"
	    xmlns:sdf="java:java.text.SimpleDateFormat"
	    select='sdf:format(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ssXXX"), 
                           sdf:parse(sdf:new("yyyy-MM-dd&apos;T&apos;HH:mm:ss"),
                           concat($date, "T23:59:59")))'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>







</xsl:stylesheet>
