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
       depending on whther the appropriate parameters
       (preseumably drawn from entries in course.properties)
       have been given non-empty values/
  -->

  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="bbURL" select="''"/>
  <xsl:param name="altformats" select="'yes'"/>


  <xsl:output method="xml" encoding="utf-8"/>


  <xsl:variable name="buildURL">
    <xsl:value-of select="concat($pwdURL, 'build.xml')"/>
  </xsl:variable>

<xsl:template name="insertNavIcons">
  <xsl:if test="$homeURL != ''">
    <xsl:text>&#10;</xsl:text>
    <a class="imgLink" href="{$homeURL}" title="Course home/outline">
      <img src="{$graphicsURL}/home.png"/>
    </a>
  </xsl:if>
  <xsl:if test="$bbURL != ''">
    <xsl:text>&#10;</xsl:text>
    <a class="imgLink" href="{$bbURL}" title="ODU Blackboard">
      <img src="{$graphicsURL}/bb.png"/>
    </a>
  </xsl:if>
  <xsl:if test="$email != ''">
    <xsl:text>&#10;</xsl:text>
    <a href="javascript:footerEmail('{$email}','{$courseName}')">
      <img src="{$graphicsURL}/email.png" title="Email to instructor"/>
    </a>
  </xsl:if>
  <xsl:if test="$forumsURL != ''">
    <xsl:text>&#10;</xsl:text>
    <a class="imgLink" href="{$forumsURL}">
      <img src="{$graphicsURL}/forum.png" title="Forums"/>
    </a>
  </xsl:if>
  <span style="margin: 0 32px;"></span>
  <xsl:apply-templates 
      select="document($buildURL)/project/target[@name='documents']/docformat" 
      mode="options"/>
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
    <script src="{$stylesURL}/communications.js"> 
      <xsl:text>/* */</xsl:text>
    </script>
    <div style="text-align: center; border-top: solid #000040; margin-top: 40px;">
      <xsl:call-template name="insertNavIcons"/>
    </div>
    <xsl:if test="$forum != ''">
      <xsl:text>&#10;</xsl:text>
      <div id="commentary" class="commentary"> </div>
      <script>
	<xsl:text>loadCommentsInto("</xsl:text>
	<xsl:value-of select="$forum"/>
	<xsl:text>", "commentary", "contactByEmail");</xsl:text>
      </script>
    </xsl:if>
  </div>
</xsl:template>




<xsl:template match="docformat" mode="options">
  <xsl:if test="$altformats = 'yes'">
    <xsl:text>&#10;</xsl:text>
    <xsl:choose>
      <xsl:when test="@format = 'topics'">
      </xsl:when>
      <xsl:when test="@format = 'modules'">
      </xsl:when>
      <xsl:when test="@format = 'epub'">
	<a class="imgLink" href="../../Directory/ebooks/index.html">
	  <img src="{$graphicsURL}/{@format}.png" title="e-book for course"/>
	</a>      
      </xsl:when>
      <xsl:when test="@format = 'mobi'"></xsl:when>
      <xsl:otherwise>
	<xsl:variable name="theTitle">
	  <xsl:choose>
	    <xsl:when test="@format = 'html'">
	      <xsl:text>single-page HTML version</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'pages'">
	      <xsl:text>multi-page HTML version</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'slidy'">
	      <xsl:text>Slides for classroom lectures</xsl:text>
	    </xsl:when>
	  </xsl:choose>
	</xsl:variable>
	<xsl:if test="@format != $format">
	  <a class="imgLink" href="{$doc}__{@format}.html">
	    <img src="{$graphicsURL}/{@format}.png" title="{$theTitle}"/>
	  </a>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>





</xsl:stylesheet>

