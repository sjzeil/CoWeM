<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:opf="http://www.idpf.org/2007/opf"
>

  <xsl:param name="baseURL" select="'document'"/>
  <xsl:param name="doc" select="'document'"/>

  <xsl:output method="xml" encoding="utf-8" indent="yes"/>

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
	  <xsl:value-of select="concat($baseURL, '/Public/', doc/text())"/>
	</dc:identifier>
	<dc:language>en-US</dc:language>
	<meta name="cover" content="cover-image" />
      </opf:metadata>
      <opf:manifest>
	<xsl:text>&#10;</xsl:text>
	<opf:item id="ncx" href="toc.ncx" 
		  media-type="application/x-dtbncx+xml"/>
	<xsl:text>&#10;</xsl:text>
	<opf:item id="cover" 
		  href="cover.html" media-type="application/xhtml+xml"/>
	<xsl:text>&#10;</xsl:text>
	<opf:item id="coverimg" 
		  href="cover.png" media-type="image/png"/>
	<xsl:text>&#10;</xsl:text>
	<opf:item id="{$doc}__epub.html" 
		  href="{$doc}__epub.html" media-type="application/xhtml+xml"/>
	<xsl:text>&#10;</xsl:text>
	<opf:item id="epub-appendix.html" 
		  href="epub-appendix.html" media-type="application/xhtml+xml"/>
	<xsl:apply-templates select="file"/>
	<xsl:text>&#10;</xsl:text>
      </opf:manifest>
      <opf:spine toc="ncx">
	<opf:itemref idref="cover" linear="no"/>
	<opf:itemref idref="{$doc}__epub.html" />
	<opf:itemref idref="epub-appendix.html" />
	<xsl:apply-templates select="file" mode="spine"/>
      </opf:spine>
      <opf:guide>
	<opf:reference href="cover.html" type="cover" title="Cover"/>
      </opf:guide>
    </opf:package>
  </xsl:template>


  <xsl:template match="file">
    <xsl:variable name="fileName" select="text()"/>
    <xsl:variable name="contentName" select="concat($doc, '__epub.html')"/>
    <!-- xsl:message>
      <xsl:text>fileName is</xsl:text>
      <xsl:value-of select="$fileName"/>
    </xsl:message -->
    <xsl:variable name="fileID" select="translate(text(), ' ', '_')"/>
    <!-- xsl:message>
      <xsl:text>ID is</xsl:text>
      <xsl:value-of select="$fileID"/>
    </xsl:message -->
    <xsl:choose>
      <xsl:when test="$fileName = $contentName">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="$fileName = 'epub-appendix.html'">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.css')">
	<xsl:text>&#10;</xsl:text>
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="text/css"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.css')">
	<xsl:text>&#10;</xsl:text>
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="text/css"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.png')">
	<xsl:text>&#10;</xsl:text>
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="image/png"/>
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.html')">
	<xsl:text>&#10;</xsl:text>
	<opf:item id="{$fileID}" 
		  href="{$fileName}" media-type="application/xhtml+xml"/>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="file" mode="spine">
    <xsl:variable name="fileName" select="text()"/>
    <xsl:variable name="contentName" select="concat($doc, '__epub.html')"/>
    <xsl:variable name="fileID" select="translate(text(), ' ', '_')"/>

    <xsl:choose>
      <xsl:when test="$fileName = $contentName">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="$fileName = 'epub-appendix.html'">
	<!-- do nothing -->
      </xsl:when>
      <xsl:when test="ends-with($fileName, '.html')">
	<opf:itemref idref="{$fileID}"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="text()">
  </xsl:template>



</xsl:stylesheet>
