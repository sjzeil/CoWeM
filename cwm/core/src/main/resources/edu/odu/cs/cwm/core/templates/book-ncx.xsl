<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/"
>

  <xsl:output method="xml" encoding="utf-8" indent="yes"
      doctype-public="-//NISO//DTD ncx 2005-1//EN"
      doctype-system="http://www.daisy.org/z3986/2005/ncx-2005-1.dtd"
      />


  <xsl:param name="baseURL" select="'document'"/>

  <xsl:output method="xml" encoding="utf-8"/>

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:variable name="docContentID"
		select="translate(concat(doc/text(),'__epub.html'), ' ', '_')"/>

  <xsl:template match="epub">
    <xsl:variable name="depth">
      <xsl:choose>
	<xsl:when test="/epub/outline/topic/topic/topic/topic/subject/item">
	  <xsl:text>6</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/topic/topic/topic/item">
	  <xsl:text>5</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/topic/topic/subject/item">
	  <xsl:text>5</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/topic/topic/item">
	  <xsl:text>4</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/topic/subject/item">
	  <xsl:text>4</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/topic/item">
	  <xsl:text>3</xsl:text>
	</xsl:when>
	<xsl:when test="/epub/outline/topic/subject/item">
	  <xsl:text>3</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:text>2</xsl:text>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <ncx:ncx version="2005-1">
      <ncx:head>
	<ncx:meta name="dtb:uid"
		  content="{$baseURL}"/>
	<ncx:meta name="dtb:depth" content="2"/>
	<ncx:meta name="dtb:totalPageCount" content="0"/>
	<ncx:meta name="dtb:maxPageNumber" content="0"/>
      </ncx:head>
      <ncx:docTitle>
	<ncx:text>
	  <xsl:value-of select="title/text()"/>
	</ncx:text>
      </ncx:docTitle>
      <xsl:variable name="documentCount" 
		    select="count(/epub/outline/topic//item[@targetdoc != ''])"/>
      <ncx:navMap>
	<ncx:navPoint id="navpoint-1" playOrder="1">
	  <ncx:navLabel>
	    <ncx:text>Book cover</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="cover.html"/>
	</ncx:navPoint>
	<ncx:navPoint id="navpoint-2" playOrder="2">
	  <ncx:navLabel>
	    <ncx:text>Overview</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="epub-overview.html"/>
	</ncx:navPoint>
	<xsl:apply-templates select="/epub/outline/topic"/>
	<xsl:variable name="appendixPoint" select="3 + $documentCount"/>
	<ncx:navPoint id="navpoint-{$appendixPoint}" 
		      playOrder="{$appendixPoint}">
	  <ncx:navLabel>
	    <ncx:text>Appendices</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="epub-appendix.html"/>
	  <xsl:apply-templates select="/epub/outline/appendix/*" mode="appendices"/>
	</ncx:navPoint>
      </ncx:navMap>
    </ncx:ncx>
  </xsl:template>



  <xsl:template match="outline">
    <xsl:apply-templates select="*" />
  </xsl:template>

  <xsl:template match="topic">
    <xsl:apply-templates select="topic | subject | item" />
  </xsl:template>

  <xsl:template match="subject">
    <xsl:apply-templates select="topic | subject | item" />
  </xsl:template>

  <xsl:template match="item[@targetdoc != '']" >
    <xsl:variable name="fileName" 
	select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/>
    <xsl:variable name="fileID" select="translate(encode-for-uri($fileName), '%', '_')"/>

    <xsl:variable name="navpoint"
		  select="3+count(preceding::item[@targetdoc != ''])"/>
    <ncx:navPoint id="navpoint-{$navpoint}" playOrder="{$navpoint}">
      <ncx:navLabel>
	<ncx:text>
	  <xsl:call-template name="getTitle">
	    <xsl:with-param name="doc" select="@targetdoc"/>
	  </xsl:call-template>
	</ncx:text>
      </ncx:navLabel>
      <ncx:content src="{$fileName}"/>
    </ncx:navPoint>
  </xsl:template>


  <xsl:template match="item[@targetdoc != '']" mode="appendices">
    <xsl:variable name="fileName" 
	select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/>
    <xsl:variable name="fileID" select="translate(encode-for-uri($fileName), '%', '_')"/>

    <xsl:variable name="navpoint"
		  select="4+count(preceding::item[@targetdoc != ''])"/>
    <ncx:navPoint id="navpoint-{$navpoint}" playOrder="{$navpoint}">
      <ncx:navLabel>
	<ncx:text>
	  <xsl:call-template name="getTitle">
	    <xsl:with-param name="doc" select="@targetdoc"/>
	  </xsl:call-template>
	</ncx:text>
      </ncx:navLabel>
      <ncx:content src="{$fileName}"/>
    </ncx:navPoint>
  </xsl:template>




  
  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="text()">
  </xsl:template>

  <xsl:template name="getTitle">
    <xsl:param name="doc"/>
    <xsl:variable name="title" select="/epub/table/title[@doc = $doc]"/>
    <xsl:choose>
      <xsl:when test="count($title) > 0">
	<xsl:value-of select="$title/text()"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>???</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:stylesheet>
