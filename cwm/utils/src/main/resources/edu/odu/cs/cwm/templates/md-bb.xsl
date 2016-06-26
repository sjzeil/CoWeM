<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<!-- 
     Unlike most Markdown document styles, this BB format does not produce
     HTML for direct viewing but generates an XML capture of the course 
     outline that is subsequently used to generate an IMS CC manifest as
     part of a Blackboard import package.
 -->
 
  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  <xsl:param name="format" select="'modules'"/>

  

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
  	<xsl:variable name="numbered">
	  <xsl:apply-templates select="body" mode="sectionNumbering"/>    
  	</xsl:variable>
  	<xsl:variable name="sectioned">
	  <xsl:apply-templates select="$numbered" mode="sectioning"/>    
  	</xsl:variable>
    <imscc>
      <sem>@sem@</sem>
      <title>@courseTitle@</title>
      <courseName>@courseName@</courseName>
      <outline>
	      <xsl:apply-templates select="$sectioned"/>
	  </outline>    
    </imscc>
  </xsl:template>


  <xsl:template match="body">
	<xsl:variable name="preamble"
		select="section[normalize-space(./*[1]) = 'Preamble']" />
	<xsl:if test="$preamble">
		<preamble>
			<xsl:apply-templates select="$preamble/sectionDescription | $preamble/sectionContent" />
		</preamble>
	</xsl:if>

	<xsl:for-each select="section">
		<xsl:variable name="sectionName" select="normalize-space(*[1])" />
		<xsl:choose>
			<xsl:when test="$sectionName = 'Preamble'" />
			<xsl:when test="$sectionName = 'Postscript'" />
			<xsl:when test="$sectionName = 'Presentation'" />
			<xsl:otherwise>
				<xsl:apply-templates select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>

	<xsl:variable name="postscript"
		select="section[normalize-space(./*[1]) = 'Postscript']" />
	<xsl:if test="$postscript">
		<postscript>
            <xsl:apply-templates select="$preamble/sectionDescription | $preamble/sectionContent" />
		</postscript>
	</xsl:if>
  </xsl:template>
  
  
  <xsl:template match="section">
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:copy-of select="sectionHeader"/>
        <xsl:copy-of select="sectionDescription"/>
        <xsl:apply-templates select="sectionContent" mode="activities"/>
        <xsl:apply-templates select="section"/>
      </xsl:copy>
  </xsl:template>
    
  <xsl:template match="ol" mode="activities">
     <ol>
        <xsl:apply-templates select="*" mode="activities"/>
     </ol>
  </xsl:template>

   
  <xsl:template match="li" mode="activities">
    <xsl:choose>
        <xsl:when test="local-name(*[1]) = 'p'">
            <li>
                <xsl:copy-of select="@*"/>
                <p>
                    <xsl:apply-templates select="*[1]" mode="activities2"/>
                </p>
                <xsl:apply-templates select="*[position &gt; 1]"/>
            </li>
          </xsl:when>
          <xsl:otherwise>
            <li>
              <xsl:copy-of select="@*"/>
              <xsl:apply-templates select="." mode="activities2"/>
            </li>
          </xsl:otherwise>
       </xsl:choose>
  </xsl:template>

  <xsl:template match="p|li" mode="activities2">
      <xsl:variable name="prefixTable"
        select="ancestor::body/section[sectionHeader//a[@name = 'presentation']]//table[2]"/>
      <xsl:choose>
          <xsl:when test="(local-name(*[1]) = 'a') and (normalize-space(*[1]/preceding-sibling::node()) = '')">
              <xsl:variable name="kind" select="normalize-space(a[1]/@href)"/>
              <img src="{$baseURL}graphics/{$kind}-kind.png" alt="{$kind}"/>
              <xsl:text> </xsl:text>
              <xsl:choose>
                 <xsl:when test="normalize-space(a[1]) = ''">
                     <xsl:variable name="kindTD" select="$prefixTable//td[normalize-space() = $kind]"/>
                     <xsl:if test="$kindTD">
                       <xsl:apply-templates select="$kindTD/../td[2]/node()"/>
                     </xsl:if>
                 </xsl:when>
                 <xsl:otherwise>
                    <xsl:apply-templates select="a[1]/node()"/>
                    <xsl:text> </xsl:text>
                 </xsl:otherwise>
              </xsl:choose>
              <xsl:apply-templates select="a[1]/following-sibling::node()"/>
          </xsl:when>
          <xsl:otherwise>
             <xsl:apply-templates select="node()"/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="*|text()" mode="activities">
      <xsl:apply-templates select="."/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
