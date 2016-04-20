<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="../styles/footer.xsl"/>
  <xsl:import href="bblink.xsl"/>

  <!-- 
     Modifies an md-sourced HTML file for export as a
     Canvas Wiki page
     -->


  <xsl:param name="doc" select="'doc'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="MathJaxURL" select="'../../styles/MathJax'"/>
  <xsl:param name="highlightjsURL" select="'../../styles/highlight.js'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="bbURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>
  <xsl:param name="altformats" select="'yes'"/>

  <xsl:output method="xml" encoding="utf-8"/>



  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
      <xsl:copy-of select="title"/>	
      <meta name="identifier" 
	    content="{concat('wiki-', $doc)}"/>
      <meta name="editing_roles" content="teachers"/>
      <meta name="workflow_state" content="active"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      
      <xsl:if test="/html/head/meta[@name='toc']">
	<div class="toc">
	  <xsl:text>Contents:</xsl:text>
	  <xsl:apply-templates select="h1 | h2" mode="toc"/>
	</div>
      </xsl:if>

      <xsl:apply-templates select="*|text()"/>
      
      <xsl:if test="/html/head/meta[@name='date']">
	<p> </p>
	<hr/>
	<p style="font-size: 75%;">
	  <xsl:text>Last modified: </xsl:text>
	  <xsl:value-of select="/html/head/meta[@name='date']/@content"/>
	</p>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1" mode="toc">
    <div class="toc-h1">
      <a href="#{@id}">
	<xsl:value-of select="1 + count(preceding-sibling::h1)"/>
	<xsl:text>. </xsl:text>
	<xsl:apply-templates select="*|text()"/>
      </a>
    </div>
  </xsl:template>

  <xsl:template match="h2">
    <xsl:variable name="sectionNum" select="count(preceding-sibling::h1)"/>
    <xsl:variable name="sectionNds" select="preceding-sibling::h1"/>
    <xsl:choose>
      <xsl:when test="count($sectionNds) &gt; 0">
	<xsl:variable name="sectionNd" select="preceding-sibling::h1[1]"/>
	<xsl:variable name="priors" select="count($sectionNd/preceding-sibling::h1 | $sectionNd/preceding-sibling::h2)"/>
	<xsl:variable name="currents" select="count(preceding-sibling::h1 | preceding-sibling::h2)"/>
	<xsl:variable name="subsectionNum" select="$currents - $priors"/>
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:value-of select="$sectionNum"/>
	  <xsl:text>.</xsl:text>
	  <xsl:value-of select="$subsectionNum"/>
	  <xsl:text> </xsl:text>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="h2" mode="toc">
    <xsl:variable name="sectionNum" select="count(preceding-sibling::h1)"/>
    <xsl:variable name="sectionNds" select="preceding-sibling::h1"/>
    <xsl:choose>
      <xsl:when test="count($sectionNds) &gt; 0">
	<xsl:variable name="sectionNd" select="preceding-sibling::h1[1]"/>
	<xsl:variable name="priors" select="count($sectionNd/preceding-sibling::h1 | $sectionNd/preceding-sibling::h2)"/>
	<xsl:variable name="currents" select="count(preceding-sibling::h1 | preceding-sibling::h2)"/>
	<xsl:variable name="subsectionNum" select="$currents - $priors"/>
	<div class="toc-h2">
	  <a href="#{@id}">
	    <xsl:value-of select="$sectionNum"/>
	    <xsl:text>.</xsl:text>
	    <xsl:value-of select="$subsectionNum"/>
	    <xsl:text> </xsl:text>
	    <xsl:apply-templates select="*|text()"/>
	  </a>
	</div>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



  <xsl:template match="example">
    <xsl:variable name="exampleCounter" 
		  select="1 + count(preceding::example)"/>
    <blockquote class="details" id="example{$exampleCounter}">
      <div class="exampleTitle">
	<xsl:text>Example </xsl:text>
	<xsl:value-of select="$exampleCounter"/>
	<xsl:text>: </xsl:text>
	<xsl:apply-templates select="title/node()"/>
      </div>
      <xsl:apply-templates select="*[local-name() != 'title'] | text()"/>
    </blockquote>
  </xsl:template>


  <xsl:template match="longlisting">
    <div class="details">
      <xsl:variable name="idValue"
		    select="concat('_details_', generate-id())"/>
      <span class="summary">
	<a href="{@file}" target="listing">
        <xsl:value-of select="substring-before(@file,'.html')"/>
	</a>
      </span>
      <xsl:text> </xsl:text>
      <input id="but{$idValue}" type="button" value="+"
        onclick="toggleDisplay('{$idValue}')"
        />
      <div class="detailPart">
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
        <xsl:variable name="encoded" select="document(@file)"/>
        <xsl:copy-of select="$encoded/html/body/pre"/>
      </div>
    </div>    
  </xsl:template>


  <xsl:template match="a[@href != '']">
    <xsl:message>
      <xsl:text>link to </xsl:text>
      <xsl:value-of select="@href"/>
    </xsl:message>

    <xsl:variable name="wikiTrailer"
		  select="'__canvas.html'"/>
    <xsl:choose>
      <xsl:when test="contains(@href, $wikiTrailer)">
	<xsl:variable name="fileName">
	  <xsl:call-template name="stripDirectories">
	    <xsl:with-param name="path" select="@href"/>
	  </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="doc"
		      select="substring-before($fileName, '.html')"/>
	<a
	    title="{@title}"
	    href="{concat('%24WIKI_REFERENCE%24/pages/', $doc)}"
	    >
	  <xsl:apply-templates select="*|text()"/>
	</a>
      </xsl:when>
      <xsl:when test="starts-with(@href, '../../')">
	<a
	    class=" instructure_file_link"
	    title="{@title}"
	    href="{concat('%24IMS-CC-FILEBASE%24/', 
                           substring-after(@href, '../../'),
			   '?canvas_download=1&amp;canvas_qs_wrap=1')}"
	    >
	  <xsl:apply-templates select="*|text()"/>
	</a>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="includeHTML">
    <xsl:variable name="encoded" select="document(@file)"/>
    <xsl:copy-of select="$encoded/html/body/*"/>
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


</xsl:stylesheet>
