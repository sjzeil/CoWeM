<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <!-- 
       Generates the common footer for all documents.
       Generate link elements for 
          - course home URL
          - email contact
          - course forums (on Blackboard)
          - integrated course forum ("discuss this page")
       depending on whether the appropriate parameters
       (presumably drawn from entries in course.properties)
       have been given non-empty values/
  -->

  <xsl:param name="meta_Author" select="''"/>
  <xsl:param name="meta_CSS" select="''"/>
  <xsl:param name="meta_Date" select="''"/>
  <xsl:param name="meta_Title" select="'@Title@'"/>
  <xsl:param name="meta_TOC" select="''"/>

  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="courseTitle" select="'@courseTitle@'"/>
  <xsl:param name="semester" select="'@semester@'"/>
  <xsl:param name="sem" select="'@sem@'"/>
  <xsl:param name="instructor" select="'@instructor@'"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="copyright" select="''"/>
  <xsl:param name="primaryDocument" select="'@primaryDocument@'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="formats" select="'html'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>

  <xsl:param name="baseURL" select="'../../'"/>
  <xsl:param name="homeURL" select="'../../index.html'"/>

  <xsl:param name="altformats" select="'yes'"/>


  <xsl:output method="xml" encoding="utf-8"/>


  <xsl:variable name="buildURL">
    <xsl:value-of select="'./build.xml'"/>
  </xsl:variable>

<xsl:template name="insertNavIcons">
  <xsl:if test="$homeURL != ''">
    <xsl:text>&#10;</xsl:text>
    <a class="imgLink" href="{$homeURL}" title="Course home/outline">
      <img src="{$baseURL}graphics/home.png"/>
    </a>
  </xsl:if>
  <xsl:if test="$email != ''">
    <xsl:variable name="subject" 
       select="encode-for-uri(concat($courseName, ', ', $meta_Title))"/>
    <xsl:text>&#10;</xsl:text>
    <a href="mailto:{$email}?subject={$subject}">
      <img src="{$baseURL}graphics/email.png" title="Email to instructor"/>
    </a>
  </xsl:if>
  <span style="margin: 0 32px;"></span>
  <xsl:for-each select="tokenize($formats,',')">
      <xsl:call-template name="linkToFormat">
          <xsl:with-param name="otherFormat" select="normalize-space(.)"/>
      </xsl:call-template> 
  </xsl:for-each>
</xsl:template>

<xsl:template name="insertHeader">
  <div class="navHeader">
    <div style="text-align: center; border-bottom: solid #000040; margin-bottom: 40px;">
      <xsl:call-template name="insertNavIcons"/>
    </div>
  </div>
</xsl:template>


<xsl:template name="insertFooter">
  <div class="footer">
    <div style="text-align: center; border-top: solid #000040; margin-top: 40px;">
      <xsl:call-template name="insertNavIcons"/>
    </div>
    <xsl:if test="$copyright != ''">
      <div class="copyright">
        <xsl:text>&#169; </xsl:text>
        <xsl:value-of select="$copyright"/>
     </div>
   </xsl:if> 
  </div>
</xsl:template>




<xsl:template name="linkToFormat">
  <xsl:param name="otherFormat"/>
  <xsl:if test="$altformats = 'yes'">
    <xsl:if test="$otherFormat != $format">
      <xsl:text>&#10;</xsl:text>
      <xsl:choose>
        <xsl:when test="$otherFormat = 'topics'">
        </xsl:when>
        <xsl:when test="$otherFormat = 'modules'">
        </xsl:when>
        <xsl:when test="$otherFormat = 'epub'">
	      <a class="imgLink" href="{$baseURL}Directory/ebooks/index.html">
	        <img src="{$baseURL}graphics/{$otherFormat}.png" title="e-book for course"/>
	      </a>      
        </xsl:when>
        <xsl:when test="$otherFormat = 'mobi'"></xsl:when>
	    <xsl:when test="$otherFormat = 'html'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="single-page HTML version"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'pages'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="multi-page HTML version"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'slides'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="Slides for lectures"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'slidy'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="Slides for lectures"/>
	        </a>
	    </xsl:when>
	  </xsl:choose>
    </xsl:if>
  </xsl:if>
</xsl:template>


</xsl:stylesheet>

