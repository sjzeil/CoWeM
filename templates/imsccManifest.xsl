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

        <resource identifier="webcontent0"
                  type="webcontent" 
                  href="webcontent/Directory/outline/index.html">
          <xsl:apply-templates 
              select="/imscc/files/file"
              mode="resources"/>
        </resource >
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



  <xsl:template match="description">
    <xsl:variable name="descriptionID" select="generate-id()"/>
    <item identifier="{$descriptionID}">
      <xsl:attribute name="identifierref">
        <xsl:value-of select="concat('res',$descriptionID)"/>
      </xsl:attribute>
      <title>Overview</title>
    </item>
  </xsl:template>


  <xsl:template match="description" mode="resources">
    <xsl:variable name="descriptionID" select="generate-id()"/>
    <xsl:variable name="fileName" 
                  select="concat('webcontent/Directory/outline/', $descriptionID, '__description.html')"/>
    <resource identifier="{concat('res',$descriptionID)}"
              type="webcontent" 
              href="{$fileName}">
      <file href="{$fileName}"/>
      <file href="webcontent/styles/modules.css"/>
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
          <xsl:value-of select="concat('res',$itemID)"/>
        </xsl:attribute>
      </xsl:if>
      <title>
        <xsl:call-template name="kindPrefix"/>
        <xsl:call-template name="getTitle"/>
        <xsl:call-template name="dateAttributes"/>
      </title>
    </item>
  </xsl:template>



  <xsl:template match="item" mode="resources">
    <xsl:variable name="itemID" select="generate-id()"/>
    <resource identifier="{concat('res',$itemID)}" 
              >
      <xsl:choose>
        <xsl:when test="@targetdoc">
          <xsl:attribute name="type">
            <xsl:text>webcontent</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('webcontent/Public/',@targetdoc, '/index.html')"/>
          </xsl:attribute>
          <xsl:call-template name="listResourceFiles">
            <xsl:with-param name="directory"
                            select="concat('Public/',@targetdoc)"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:when test="@target">
          <xsl:attribute name="type">
            <xsl:text>webcontent</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('webcontent/Public/',@target, '/index.html')"/>
          </xsl:attribute>
          <xsl:call-template name="listResourceFiles">
            <xsl:with-param name="directory"
                            select="concat('Public/',@target)"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:when test="@assignment">
          <xsl:attribute name="type">
            <xsl:text>webcontent</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('Protected/Assts/',@assignment, '.mmd.html')"/>
          </xsl:attribute>
          <xsl:call-template name="listResourceFiles">
            <xsl:with-param name="directory"
                            select="'Protected/Assts'"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:when test="starts-with(@href, '../../')">
          <xsl:attribute name="type">
            <xsl:text>webcontent</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('webcontent/', substring-after(@href, '../../'))"/>
          </xsl:attribute>
          <xsl:call-template name="listResourceFiles">
            <xsl:with-param name="directory"
                            select="substring-after(@href, '../../')"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:otherwise>
          <xsl:variable name="fileName" select="concat('res-',$itemID,'.xml')"/>
          <xsl:attribute name="type">
            <xsl:text>imswl_xmlv1p1</xsl:text>
          </xsl:attribute>
          <file href="{$fileName}"/>
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
        </xsl:otherwise>
      </xsl:choose>
    </resource>
  </xsl:template>

  <xsl:template match="item" mode="events">
    <xsl:variable name="itemID" select="generate-id()"/>
    <xsl:if test="@date|@due">
      <event xmlns="http://canvas.instructure.com/xsd/cccv1p0"
             identifier="{concat('event-',$itemID)}">
        <title>
          <xsl:call-template name="getTitle"/>
        </title>
        <description>
          <xsl:value-of select="normalize-space(*|text())"/>
          <xsl:if test="@due != ''">
            <xsl:text> due</xsl:text>
          </xsl:if>
          <xsl:if test="@time != ''">
            <xsl:text> (</xsl:text>
            <xsl:value-of select="@date"/>
            <xsl:text>)</xsl:text>
          </xsl:if>
        </description>
          
        <xsl:choose>
          <xsl:when test="@enddate != ''">
            <start_at>
              <xsl:value-of select="concat(@date, 'T00:00:00')"/>
            </start_at>
            <end_at>
              <xsl:value-of select="concat(@enddate, 'T23:59:59')"/>
            </end_at>
          </xsl:when>
          
          <xsl:when test="@date != ''">
            <start_at>
              <xsl:value-of select="concat(@date, 'T00:00:00')"/>
            </start_at>
            <end_at>
              <xsl:value-of select="concat(@date, 'T23:59:59')"/>
            </end_at>
          </xsl:when>
          <xsl:when test="@due != ''">
            <start_at>
              <xsl:value-of select="concat(@date, 'T23:59:58')"/>
            </start_at>
            <end_at>
              <xsl:value-of select="concat(@date, 'T23:59:59')"/>
            </end_at>
          </xsl:when>
        </xsl:choose>
      </event>
    </xsl:if>
  </xsl:template>

  <xsl:template match="file" mode="resources">
    <file href="{concat('webcontent/', text())}"/>
  </xsl:template>


  <xsl:template name="listResourceFiles">
    <xsl:param name="directory" select="'garbage/'"/>

    <!--
    <xsl:apply-templates 
        select="/imscc/files/file[starts-with(text(), $directory)]"
        mode="resources"/>
    <xsl:apply-templates 
        select="/imscc/files/file[starts-with(text(), 'styles/')]"
        mode="resources"/>
    <xsl:apply-templates 
        select="/imscc/files/file[starts-with(text(), 'graphics/')]"
        mode="resources"/>
    -->
  </xsl:template>


  <!-- <xsl:template match="item[@targetdoc != '']" mode="suppressed"> -->
  <!--   <xsl:variable name="fileName"  -->
  <!--            select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/> -->
  <!--   <xsl:variable name="fileID"  -->
  <!--            select="translate(encode-for-uri($fileName), '%', '_')"/> -->
  <!--   <opf:item id="{$fileID}"  -->
  <!--        href="{$fileName}"  -->
  <!--        media-type="application/xhtml+xml"/> -->
  <!--   <xsl:if test="count(/epub/files/file[text() = $fileName]) = 0"> -->
  <!--     <\!- - This file does not exist. May be lecture notes that have -->
  <!--     not yet been written, or that were not built for epub -->
  <!--     output. -\-> -->
  <!--     <xsl:result-document href="{$fileName}" method="xhtml">  -->
  <!--  <html> -->
  <!--    <head> -->
  <!--      <title> -->
  <!--        <xsl:call-template name="getTitle"> -->
  <!--          <xsl:with-param name="doc" select="@targetdoc"/> -->
  <!--        </xsl:call-template> -->
  <!--      </title> -->
  <!--    </head> -->
  <!--    <body> -->
  <!--      <h1> -->
  <!--        <xsl:call-template name="getTitle"> -->
  <!--          <xsl:with-param name="doc" select="@targetdoc"/> -->
  <!--        </xsl:call-template> -->
  <!--      </h1> -->
  <!--      <h2>(place holder)</h2> -->
  <!--      <p> -->
  <!--        This section is currently unavailable. -->
  <!--      </p> -->
  <!--    </body> -->
  <!--  </html> -->
  <!--     </xsl:result-document> -->
  <!--   </xsl:if> -->
  <!-- </xsl:template> -->




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
      <xsl:when test="normalize-space(.) != ''">
        <xsl:value-of select="normalize-space(.)"/>
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
        <xsl:call-template name="formatDate">
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
      <xsl:call-template name="formatDate">
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
    <xsl:param name="date" select="'2005-01-01'"/>

    <xsl:variable name="year" select="substring-before($date, '-')"/>
    <xsl:variable name="afterYear" select="substring-after($date, '-')"/>

    <xsl:variable name="month" select="substring-before($afterYear, '-')"/>
    <xsl:variable name="afterMonth" select="substring-after($afterYear, '-')"/>

    <xsl:variable name="day">
      <xsl:choose>
        <xsl:when test="contains($afterMonth, 'T')">
          <xsl:value-of select="substring-before($afterMonth, 'T')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$afterMonth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$month"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="$day"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="$year"/>

  </xsl:template>


</xsl:stylesheet>
