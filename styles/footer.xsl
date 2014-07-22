<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
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

  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>


  <xsl:output method="xml" encoding="utf-8"/>

<xsl:template name="insertFooter">
  <div class="footer">
    <script src="{$stylesURL}/communications.js"> 
      <xsl:text>/* */</xsl:text>
    </script>
    <div style="text-align: center; border-top: solid #000040; margin-top: 40px;">
      <xsl:if test="$homeURL != ''">
	<xsl:text>&#10;</xsl:text>
	<a class="imgLink" href="{$homeURL}">
	  <img src="{$graphicsURL}/home.png"/>
	</a>
      </xsl:if>
      <xsl:if test="$email != ''">
	<xsl:text>&#10;</xsl:text>
	<a id="contactByEmail" href="javascript:footerEmail('{$email}','{$courseName}')">
	<img src="{$graphicsURL}/email.png"/>
      </a>
      </xsl:if>
      <xsl:if test="$forumsURL != ''">
	<xsl:text>&#10;</xsl:text>
	<a class="imgLink" href="{$forumsURL}">
	  <img src="{$graphicsURL}/forum.png"/>
	</a>
      </xsl:if>
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
</xsl:stylesheet>
