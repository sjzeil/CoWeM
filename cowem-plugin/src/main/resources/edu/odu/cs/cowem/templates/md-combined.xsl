<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="courseTitle" select="'@courseTitle@'"/>
  <xsl:param name="semester" select="'@semester@'"/>

  

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:copy-of select="node()"/>
        <link href="./styles/md-combined.css" 
              media="screen, print" rel="stylesheet" 
              type="text/css"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <div class='booktitleblock'>
		<h1><xsl:value-of select="$courseName"/></h1>
		<h2><xsl:value-of select="$courseTitle"/></h2>
		<h3><xsl:value-of select='format-date(current-date(), "[MNn] [D], [Y]", "en", (), ())'/></h3>
	  </div>
	  <xsl:apply-templates select="node()"/>
	  <script><xsl:text><![CDATA[
	  MathJax.Hub.Queue(function() {
	    var monitoredDiv = document.getElementById("mathJaxHasCompleted");
	    monitoredDiv.style.display = 'block';
	  });
	  ]]></xsl:text></script>
	  <div id="mathJaxHasCompleted" style="display: none;"><hr/></div>
	</xsl:copy>
  </xsl:template>

  <xsl:template match="div[@style='display: none;']">
      <xsl:copy>
          <xsl:copy-of select='@*' />
          <xsl:attribute name="style">
              <xsl:text>block;</xsl:text>
          </xsl:attribute>
          <xsl:apply-templates select="node()"/>
      </xsl:copy>
  </xsl:template>

  <xsl:template match="table[@class='slideshowcontrol']">
  </xsl:template>
  

  <xsl:template match="*">
      <xsl:copy>
          <xsl:copy-of select='@*' />
          <xsl:apply-templates select="node()"/>
      </xsl:copy>
  </xsl:template>


  <xsl:template match="text()">
    <xsl:copy-of select='.'/>
  </xsl:template>

</xsl:stylesheet>
