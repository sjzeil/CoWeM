<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:bb="http://www.blackboard.com/content-packaging/"
    >

  <xsl:param name="workDir" select="'bbthin'"/>

  <xsl:param name="bbContentLabel" select="'_'"/>

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
    <manifest identifier="man0001">
      <organizations default="toc00001">
          <organization identifier="toc00001">
              <xsl:apply-templates select="/imscc/navigation/*" 
                  mode="navigation"/>
    <!-- 
              <item identifier="itm00001" identifierref="res00001">
                  <title><xsl:value-of select="$bbContentLabel"/></title>
                  <item identifier="itm00002" identifierref="res00002">
                      <title>- -TOP- -</title>
                 </item>
              </item>
      -->
          </organization>
      </organizations>
      <resources>
      <!-- 
          <resource bb:file="res00001.dat" 
              bb:title="{$bbContentLabel}" 
              identifier="res00001" 
              type="course/x-bb-coursetoc" 
              xml:base="res00001"/>
          <resource bb:file="res00002.dat" 
              bb:title="- -TOP- -" 
              identifier="res00002" 
              type="resource/x-bb-document" 
              xml:base="res00002"/>
       -->
          <xsl:apply-templates select="/imscc/navigation/*" mode="nav-resources"/>
          <xsl:apply-templates select="/imscc/outline/*" mode="calendar"/>
      </resources>
    </manifest>
    <!-- 
    <xsl:result-document 
        href="{$workDir}/res00001.dat"
        format="resources">
        <COURSETOC id="{concat('toc-', generate-id())}">
          <LABEL value="{$bbContentLabel}"/>
          <URL value=""/>
          <TARGETTYPE value="CONTENT"/>
          <INTERNALHANDLE value="content"/>
          <FLAGS>
            <LAUNCHINNEWWINDOW value="false"/>
            <ISENABLED value="true"/>
            <ISENTRYPOINT value="false"/>
            <ALLOWOBSERVERS value="false"/>
            <ALLOWGUESTS value="false"/>
          </FLAGS>
        </COURSETOC>
    </xsl:result-document>
    
    <xsl:variable name="tocContentID">
        <xsl:text>_1001_1</xsl:text>
    </xsl:variable>
    
    <xsl:result-document 
        href="{$workDir}/res00002.dat"
        format="resources">
	<CONTENT id="{$tocContentID}">
		<TITLE value="- -TOP- -" />
		<TITLECOLOR value="#000000" />
		<BODY>
			<TEXT />
			<TYPE value="S" />
		</BODY>
		<DATES>
			<CREATED value="" />
			<UPDATED value="{$now}" />
			<START value="" />
			<END value="" />
		</DATES>
		<FLAGS>
			<ISAVAILABLE value="true" />
			<ISFROMCARTRIDGE value="false" />
			<ISFOLDER value="true" />
			<ISDESCRIBED value="false" />
			<ISTRACKED value="false" />
			<ISLESSON value="false" />
			<ISSEQUENTIAL value="false" />
			<ALLOWGUESTS value="true" />
			<ALLOWOBSERVERS value="true" />
			<LAUNCHINNEWWINDOW value="false" />
			<ISREVIEWABLE value="false" />
			<ISGROUPCONTENT value="false" />
			<ISSAMPLECONTENT value="false" />
		</FLAGS>
		<CONTENTHANDLER value="resource/x-bb-folder" />
		<RENDERTYPE value="REGULAR" />
		<URL value="" />
		<VIEWMODE value="TEXT_ICON_ONLY" />
		<OFFLINENAME value="" />
		<OFFLINEPATH value="" />
		<LINKREF value="" />
		<PARENTID value="{'{unset id}'}" />
		<VERSION value="3" />
		<EXTENDEDDATA />
		<FILES />
	</CONTENT>
    </xsl:result-document>
    -->
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

	<xsl:template name="contentID">
        <xsl:variable name="itemID">
            <xsl:call-template name="navItemID"/>
        </xsl:variable>
		<xsl:value-of select="concat('_',$itemID,'_1')" />
	</xsl:template>

	<xsl:template name="fileID">
        <xsl:variable name="itemID">
            <xsl:call-template name="navItemID"/>
        </xsl:variable>
        <xsl:value-of select="concat('_2',$itemID,'_1')" />
	</xsl:template>
  
  
  <xsl:template name="navItemID">
      <xsl:variable name="navCount" 
          select="count(./ancestor::tr[1]/preceding-sibling::tr)"/>
      <xsl:variable name="offset" select="9000 + $navCount"/>
      <xsl:value-of select="concat('7', $offset)"/>
  </xsl:template>


  <xsl:template match="a" mode="nav-menu">
      <xsl:variable name="itemID">
           <xsl:call-template name="navItemID"/>
      </xsl:variable>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
      <item identifier="itm-{$itemID}" identifierref="menu-{$itemID}">
          <title>
            <xsl:value-of select="$title"/>
         </title>
      </item>
      <xsl:call-template name="generateMenuResources">
          <xsl:with-param name="url" select="@href"/>
          <xsl:with-param name="itemID" select="$itemID"/>
          <xsl:with-param name="label" select="$title"/>
      </xsl:call-template>
  </xsl:template>

  <xsl:template match="table|tr|td" mode="nav-menu">
      <xsl:apply-templates select="*" mode="nav-menu"/>
  </xsl:template>

  <xsl:template name="generateMenuResources">
	  <xsl:param name="url" />
	  <xsl:param name="itemID" />
	  <xsl:param name="label" />
		
	  <xsl:result-document 
        href="{$workDir}/entry-{$itemID}.dat"
        format="resources">
        <COURSETOC id="4{$itemID}_1">
          <LABEL value="{$label}"/>
          <URL value=""/>
          <TARGETTYPE value="CONTENT"/>
          <INTERNALHANDLE value="content"/>
          <FLAGS>
            <LAUNCHINNEWWINDOW value="false"/>
            <ISENABLED value="true"/>
            <ISENTRYPOINT value="false"/>
            <ALLOWOBSERVERS value="false"/>
            <ALLOWGUESTS value="false"/>
          </FLAGS>
        </COURSETOC>
    </xsl:result-document>
    
    <xsl:result-document 
        href="{$workDir}/top-{$itemID}.dat"
        format="resources">
    <CONTENT id="5{$itemID}_1">
        <TITLE value="--TOP--" />
        <TITLECOLOR value="#000000" />
        <BODY>
            <TEXT />
            <TYPE value="S" />
        </BODY>
        <DATES>
            <CREATED value="" />
            <UPDATED value="{$now}" />
            <START value="" />
            <END value="" />
        </DATES>
        <FLAGS>
            <ISAVAILABLE value="true" />
            <ISFROMCARTRIDGE value="false" />
            <ISFOLDER value="true" />
            <ISDESCRIBED value="false" />
            <ISTRACKED value="false" />
            <ISLESSON value="false" />
            <ISSEQUENTIAL value="false" />
            <ALLOWGUESTS value="true" />
            <ALLOWOBSERVERS value="true" />
            <LAUNCHINNEWWINDOW value="false" />
            <ISREVIEWABLE value="false" />
            <ISGROUPCONTENT value="false" />
            <ISSAMPLECONTENT value="false" />
        </FLAGS>
        <CONTENTHANDLER value="resource/x-bb-folder" />
        <RENDERTYPE value="REGULAR" />
        <URL value="" />
        <VIEWMODE value="TEXT_ICON_ONLY" />
        <OFFLINENAME value="" />
        <OFFLINEPATH value="" />
        <LINKREF value="" />
        <PARENTID value="{'{unset id}'}" />
        <VERSION value="3" />
        <EXTENDEDDATA />
        <FILES />
    </CONTENT>
    </xsl:result-document>
		
    <xsl:variable name="linkHref">
        <xsl:call-template name='formatURL'>
            <xsl:with-param name='url' select='@href' />
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="fileID">
        <xsl:value-of select="concat('_7', $itemID, '_1')" />
    </xsl:variable>
    
    
    <xsl:result-document href="{concat($workDir, '/res-', $itemID, '.dat')}"
         format="resources">
        <CONTENT id="6{$itemID}_1">
            <TITLE>
                <xsl:attribute name="value">
                    <xsl:value-of select="$label" />
                </xsl:attribute>
            </TITLE>
            <TITLECOLOR value="#000000" />
            <BODY>
                <TEXT>
                    <xsl:text>Item is </xsl:text>
                    <xsl:value-of select="$itemID"/>
                    <br/>
                    <xsl:text>
                    url is </xsl:text> 
                    <xsl:value-of select="$url"/>
                </TEXT>
                <xsl:choose>
                    <xsl:when test="contains($linkHref, '://')">
                        <TYPE value="H" />
                    </xsl:when>
                    <xsl:otherwise>
                        <TYPE value="S" /> 
                    </xsl:otherwise>
                </xsl:choose>
            </BODY>
            <DATES>
                <CREATED value="" />
                <UPDATED value="{$now}" />
                <START value="" />
                <END value="" />
            </DATES>
            <FLAGS>
                <ISAVAILABLE value="true" />
                <ISFROMCARTRIDGE value="false" />
                <ISFOLDER value="false" />
                <ISDESCRIBED value="false" />
                <ISTRACKED value="false" />
                <ISLESSON value="false" />
                <ISSEQUENTIAL value="false" />
                <ALLOWGUESTS value="true" />
                <ALLOWOBSERVERS value="true" />
                <LAUNCHINNEWWINDOW value="true" />
                <ISREVIEWABLE value="false" />
                <ISGROUPCONTENT value="false" />
                <ISSAMPLECONTENT value="false" />
            </FLAGS>
            <xsl:choose>
                <xsl:when test="contains($linkHref, '://')">
                    <CONTENTHANDLER value="resource/x-bb-externallink" />
                    <RENDERTYPE value="URL" />
                    <URL value="{$linkHref}" />
                </xsl:when>
                <xsl:otherwise>
                    <CONTENTHANDLER value="resource/x-bb-file" />
                    <RENDERTYPE value="REGULAR" />
                    <URL value="" />
                </xsl:otherwise>
            </xsl:choose>
            <VIEWMODE value="TEXT_ICON_ONLY" />
            <OFFLINENAME value="" />
            <OFFLINEPATH value="" />
            <LINKREF value="" />
            <PARENTID value="5{$itemID}_1" />
            <VERSION value="3" />
            <EXTENDEDDATA />
            <FILES>
                <xsl:choose>
                    <xsl:when test="contains($linkHref, '://')">
                    </xsl:when>
                    <xsl:otherwise>
                        <FILE id="{$fileID}">
                            <NAME>
                                <xsl:value-of select="$linkHref" />
                            </NAME>
                            <FILEACTION value="EMBED" />
                            <LINKNAME value="{$linkHref}" />
                            <STORAGETYPE value="CS" />
                            <DATES>
                                <CREATED value="" />
                                <UPDATED value="2015-06-12 08:51:05 EDT" />
                            </DATES>
                            <REGISTRY />
                        </FILE>
                    </xsl:otherwise>
                </xsl:choose>
            </FILES>
        </CONTENT>
    </xsl:result-document>
<!-- 
		<xsl:result-document href="{concat($workDir, '/menu-', $itemID, '.dat')}"
			format="resources">
			<COURSETOC id="_2{$itemID}_1">
				<LABEL value="{$label}" />
				<xsl:choose>
					<xsl:when test="contains($url, '://')">
						<URL value="{$url}" />
						<TARGETTYPE value="URL" />
						<INTERNALHANDLE value="" />
					</xsl:when>
					<xsl:otherwise>
						<TARGETTYPE value="LINK" />
						<URL value="" />
						<INTERNALHANDLE value="" />
					</xsl:otherwise>
				</xsl:choose>
				<FLAGS>
					<xsl:choose>
						<xsl:when test="contains($url, '://')">
							<LAUNCHINNEWWINDOW value="true" />
						</xsl:when>
						<xsl:otherwise>
							<LAUNCHINNEWWINDOW value="false" />
						</xsl:otherwise>
					</xsl:choose>

					<ISENABLED value="true" />
					<ISENTRYPOINT value="false" />
					<ALLOWOBSERVERS value="true" />
					<ALLOWGUESTS value="true" />
				</FLAGS>
			</COURSETOC>
		</xsl:result-document>

	<xsl:if test="not(contains($url, '://'))">
		<xsl:result-document href="{concat($workDir, '/link-', $itemID, '.dat')}"
			format="resources">
			<LINK id="_3{$itemID}_1">
				<TITLE value="{$label}" />
				<REFERRER id="menu-{$itemID}" type="COURSE_TOC" />
				<REFERREDTO id="res-{$itemID}" type="CONTENT" />
				<FLAGS>
					<ISAVAILABLE value="true" />
				</FLAGS>
			</LINK>
		</xsl:result-document>
	</xsl:if>
	
	-->
  </xsl:template>


  <xsl:template match="a" mode="navigation">
      <xsl:variable name="itemID">
           <xsl:call-template name="navItemID"/>
      </xsl:variable>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
      <item identifier="entry{$itemID}" identifierref="entry-{$itemID}">
          <title><xsl:value-of select="$title"/></title>
          <item identifier="top{$itemID}" identifierref="top-{$itemID}">
              <title>--TOP--</title>
              <item identifier="item{$itemID}" identifierref="res-{$itemID}">
                  <title><xsl:value-of select="$title"/></title>
              </item>
          </item>
      </item>
      
      <xsl:call-template name="generateMenuResources">
          <xsl:with-param name="url" select="@href"/>
          <xsl:with-param name="itemID" select="$itemID"/>
          <xsl:with-param name="label" select="$title"/>
      </xsl:call-template>
  </xsl:template>

  <xsl:template match="table|tr|td" mode="navigation">
      <xsl:apply-templates select="*" mode="navigation"/>
  </xsl:template>
  

  <xsl:template match="a" mode="nav-resources">
      <xsl:variable name="itemID">
           <xsl:call-template name="navItemID"/>
      </xsl:variable>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
          
          
      <resource bb:file="entry-{$itemID}.dat" 
              bb:title="{$title}" 
              identifier="entry-{$itemID}" 
              type="course/x-bb-coursetoc" 
              xml:base="entry-{$itemID}"/>
      <resource bb:file="top-{$itemID}.dat" 
              bb:title="--TOP--" 
              identifier="top-{$itemID}" 
              type="resource/x-bb-document" 
              xml:base="top-{$itemID}"/>
      <resource
          bb:file="res-{$itemID}.dat"
          bb:title="{$title}"
          identifier="res-{$itemID}"
          type="resource/x-bb-document"
          xml:base="res-{$itemID}"/>
    
  </xsl:template>

  <xsl:template match="table|tr|td" mode="nav-resources">
      <xsl:apply-templates select="*" mode="nav-resources"/>
  </xsl:template>



  <xsl:template match="a" mode="nav-links">
      <xsl:variable name="itemID">
           <xsl:call-template name="navItemID"/>
      </xsl:variable>
      <xsl:variable name="title"
          select="normalize-space(*|text())"/>
      <xsl:if test="not(contains(@href, '://'))">
        <resource bb:file="link-{$itemID}.dat"
                  bb:title="{$title}"
                  identifier="link-{$itemID}"
                  type="course/x-bb-link"
                  xml:base="link-{$itemID}" />
      </xsl:if>
    
  </xsl:template>

  <xsl:template match="table|tr|td" mode="nav-links">
      <xsl:apply-templates select="*" mode="nav-links"/>
  </xsl:template>







	<xsl:template name='formatURL'>
		<xsl:param name='url'/>
		
		<xsl:choose>
		  <xsl:when test="starts-with($url, './')">
		      <xsl:call-template name="formatURL">
		          <xsl:with-param name="url"
		              select="substring-after($url, './')"/>
		      </xsl:call-template>
		  </xsl:when>
		  <xsl:when test="starts-with($url, '../../')">
		      <xsl:value-of select = "concat('webcontent/', 
                      substring-after($url, '../../'))"/>
          </xsl:when>
          <xsl:when test="starts-with($url, '../')">
              <xsl:value-of select = "concat('webcontent/Directory/', 
                      substring-after($url, '../'))"/>
          </xsl:when>
          <xsl:otherwise>
              <xsl:value-of select="$url"/>
          </xsl:otherwise>
		</xsl:choose>
	</xsl:template>




	<xsl:template name="generateLinkResource">
		<xsl:param name="url" />
		<xsl:param name="itemID" />
		<xsl:param name="label" />

		<xsl:variable name="contentID">
			<xsl:value-of select="concat('_', $itemID, '_1')" />
		</xsl:variable>
		<xsl:variable name="fileID">
			<xsl:value-of select="concat('_2', $itemID, '_1')" />
		</xsl:variable>
		<xsl:variable name="parentID">
			<xsl:text>_1001_1</xsl:text>
		</xsl:variable>


		<xsl:variable name="linkHref">
			<xsl:call-template name='formatURL'>
				<xsl:with-param name='url' select='$url' />
			</xsl:call-template>
		</xsl:variable>

		<xsl:result-document href="{concat($workDir, '/res-', $itemID, '.dat')}"
			format="resources">
			<CONTENT id="{$contentID}">
				<TITLE>
					<xsl:attribute name="value">
                        <xsl:value-of select="$label" />
                    </xsl:attribute>
				</TITLE>
				<TITLECOLOR value="#000000" />
				<BODY>
                    <TEXT>
                        <xsl:text>Item is </xsl:text>
                        <xsl:value-of select="$itemID"/>
                        <br/>
                        <xsl:text>
                        url is </xsl:text> 
                        <xsl:value-of select="$url"/>
                    </TEXT>
					<xsl:choose>
						<xsl:when test="contains($linkHref, '://')">
							<TYPE value="H" />
						</xsl:when>
						<xsl:otherwise>
							<TYPE value="S" /> 
						</xsl:otherwise>
					</xsl:choose>
				</BODY>
				<DATES>
					<CREATED value="" />
					<UPDATED value="{$now}" />
					<START value="" />
					<END value="" />
				</DATES>
				<FLAGS>
					<ISAVAILABLE value="true" />
					<ISFROMCARTRIDGE value="false" />
					<ISFOLDER value="false" />
					<ISDESCRIBED value="false" />
					<ISTRACKED value="false" />
					<ISLESSON value="false" />
					<ISSEQUENTIAL value="false" />
					<ALLOWGUESTS value="true" />
					<ALLOWOBSERVERS value="true" />
					<LAUNCHINNEWWINDOW value="true" />
					<ISREVIEWABLE value="false" />
					<ISGROUPCONTENT value="false" />
					<ISSAMPLECONTENT value="false" />
				</FLAGS>
				<xsl:choose>
					<xsl:when test="contains($linkHref, '://')">
						<CONTENTHANDLER value="resource/x-bb-externallink" />
						<RENDERTYPE value="URL" />
						<URL value="{$linkHref}" />
					</xsl:when>
					<xsl:otherwise>
						<CONTENTHANDLER value="resource/x-bb-file" />
						<RENDERTYPE value="REGULAR" />
						<URL value="" />
					</xsl:otherwise>
				</xsl:choose>
				<VIEWMODE value="TEXT_ICON_ONLY" />
				<OFFLINENAME value="" />
				<OFFLINEPATH value="" />
				<LINKREF value="" />
				<PARENTID value="{$parentID}" />
				<VERSION value="3" />
				<EXTENDEDDATA />
				<FILES>
					<xsl:choose>
						<xsl:when test="contains($linkHref, '://')">
						</xsl:when>
						<xsl:otherwise>
							<FILE id="{$fileID}">
								<NAME>
									<xsl:value-of select="$linkHref" />
								</NAME>
								<FILEACTION value="EMBED" />
								<LINKNAME value="{$linkHref}" />
								<STORAGETYPE value="CS" />
								<DATES>
									<CREATED value="" />
									<UPDATED value="2015-06-12 08:51:05 EDT" />
								</DATES>
								<REGISTRY />
							</FILE>
						</xsl:otherwise>
					</xsl:choose>
				</FILES>
			</CONTENT>
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
