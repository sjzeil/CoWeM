<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
>

  <xsl:param name="baseURL" select="'document'"/>
  <xsl:param name="doc" select="'OEPBS'"/>

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:variable name="docContentID"
		select="translate(concat(/epub/doc/text(),'__epub.html'), ' ', '_')"/>

  <xsl:template match="epub">
    <opf:package
	unique-identifier="bookid" version="2.0">
      <opf:metadata>
	<xsl:if test="title/text() != ''">
	  <dc:title>
	    <xsl:value-of select="title/text()"/>
	  </dc:title>
	</xsl:if>
	<xsl:if test="author/text() != ''">
	  <dc:creator>
	    <xsl:value-of select="author/text()"/>
	  </dc:creator>
	</xsl:if>
	<xsl:if test="date/text() != ''">
	  <dc:date>
	    <xsl:value-of select="date/text()"/>
	  </dc:date>
	</xsl:if>
	<dc:identifier id="bookid">
	  <xsl:value-of select="$baseURL "/>
	</dc:identifier>
	<dc:language>en-US</dc:language>
	<meta name="cover" content="cover-image" />
      </opf:metadata>
      <opf:manifest>
	<opf:item id="ncx" href="toc.ncx" 
		  media-type="application/x-dtbncx+xml"/>
	<opf:item id="cover" 
		  href="cover.html" media-type="application/xhtml+xml"/>
	<opf:item id="coverimg" 
		  href="cover.png" media-type="image/png"/>
	<opf:item id="epub-overview.html" 
		  href="epub-overview.html" media-type="application/xhtml+xml"/>
	<opf:item id="epub-appendix.html" 
		  href="epub-appendix.html" media-type="application/xhtml+xml"/>
	<xsl:apply-templates select="outline"/>
	<xsl:apply-templates select="files/file"/>
      </opf:manifest>
      <opf:spine toc="ncx">
	<opf:itemref idref="cover" linear="no"/>
	<opf:itemref idref="epub-overview.html" />
	<xsl:apply-templates select="outline/topic" mode="spine"/>
	<opf:itemref idref="epub-appendix.html" />
	<xsl:apply-templates select="/epub/outline/appendix/*" mode="spine"/>
	<xsl:apply-templates select="files/file" mode="spine"/>
      </opf:spine>
      <opf:guide>
	<opf:reference href="cover.html" type="cover" title="Cover"/>
      </opf:guide>
    </opf:package>
  </xsl:template>


  <xsl:template match="file">
    <xsl:variable name="fileName" select="text()"/>
    <xsl:variable name="fileID" 
		  select="translate(encode-for-uri($fileName), '%', '_')"/>
    <!-- xsl:message>
      <xsl:text>ID is </xsl:text>
      <xsl:value-of select="$fileID"/>
    </xsl:message -->
    <xsl:choose>
      <xsl:when test="$fileName = 'epub-appendix.html'">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.css')">
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="text/css"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.css')">
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="text/css"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.png')">
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="image/png"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '__epub.html')">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.html')">
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="application/xhtml+xml"/>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="outline">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="appendix">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="topic">
    <xsl:apply-templates select="topic | subject | item"/>
  </xsl:template>

  <xsl:template match="subject">
    <xsl:apply-templates select="topic | subject | item"/>
  </xsl:template>

  <xsl:template match="item[@targetdoc != '']">
    <xsl:variable name="fileName" 
	select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/>
    <xsl:variable name="fileID" 
		  select="translate(encode-for-uri($fileName), '%', '_')"/>
    <opf:item id="{$fileID}" 
	      href="{$fileName}" 
	      media-type="application/xhtml+xml"/>
    <xsl:if test="count(/epub/files/file[text() = $fileName]) = 0">
      <!-- This file does not exist. May be lecture notes that have
	   not yet been written, or that were not built for epub
	   output. -->
      <xsl:result-document href="{$fileName}" method="xhtml"> 
	<html>
	  <head>
	    <title>
	      <xsl:call-template name="getTitle">
		<xsl:with-param name="doc" select="@targetdoc"/>
	      </xsl:call-template>
	    </title>
	  </head>
	  <body>
	    <h1>
	      <xsl:call-template name="getTitle">
		<xsl:with-param name="doc" select="@targetdoc"/>
	      </xsl:call-template>
	    </h1>
	    <h2>(place holder)</h2>
	    <p>
	      This section is currently unavailable.
	    </p>
	  </body>
	</html>
      </xsl:result-document>
    </xsl:if>
  </xsl:template>


  <xsl:template match="outline" mode="spine">
    <xsl:apply-templates select="*" mode="spine"/>
  </xsl:template>

  <xsl:template match="topic" mode="spine">
    <xsl:apply-templates select="topic | subject | item" mode="spine"/>
  </xsl:template>

  <xsl:template match="subject" mode="spine">
    <xsl:apply-templates select="topic | subject | item" mode="spine"/>
  </xsl:template>

  <xsl:template match="item[@targetdoc != '']" mode="spine">
    <xsl:variable name="fileName" 
	select="concat(@targetdoc, '/', @targetdoc, '__epub.html')"/>
    <xsl:variable name="fileID" 
		  select="translate(encode-for-uri($fileName), '%', '_')"/>
    <opf:itemref idref="{$fileID}"/>
  </xsl:template>





<!-- ============  Spine ===================== -->

  <xsl:template match="file" mode="spine">
    <xsl:variable name="fileName" select="text()"/>
    <xsl:variable name="fileID" 
		  select="translate(encode-for-uri($fileName), '%', '_')"/>

    <xsl:choose>
      <xsl:when test="$fileName = 'epub-appendix.html'">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="ends-with($fileName, '__epub.html')">
	<xsl:if test="count(/epub/outline//item[@href = concat('../../Public/',$fileName)]) = 0">
	  <xsl:variable name="targetdoc" 
			select="substring-before($fileName, '/')"/>
	  <xsl:if test="count(/epub/outline//item[@targetdoc = $targetdoc]) = 0">
	    <opf:itemref idref="{$fileID}"/>
	  </xsl:if>
	</xsl:if>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.html')">
	<opf:itemref idref="{$fileID}"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="*" mode="spine">
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
