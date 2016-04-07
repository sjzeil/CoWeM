<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/"
>

  <xsl:output method="xml" encoding="utf-8"
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
    <ncx:ncx version="2005-1">
      <ncx:head>
	<ncx:meta name="dtb:uid"
		  content="{concat($baseURL, '/Public/', doc/text())}"/>
	<ncx:meta name="dtb:depth" content="1"/>
	<ncx:meta name="dtb:totalPageCount" content="0"/>
	<ncx:meta name="dtb:maxPageNumber" content="0"/>
      </ncx:head>
      <ncx:docTitle>
	<ncx:text>
	  <xsl:value-of select="title/text()"/>
	</ncx:text>
      </ncx:docTitle>
      <ncx:navMap>
	<ncx:navPoint id="navpoint-1" playOrder="1">
	  <ncx:navLabel>
	    <ncx:text>Book cover</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="cover.html"/>
	</ncx:navPoint>
	<ncx:navPoint id="navpoint-2" playOrder="2">
	  <ncx:navLabel>
	    <ncx:text>Contents</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="{doc/text()}__epub.html"/>
	</ncx:navPoint>
	<ncx:navPoint id="navpoint-3" playOrder="3">
	  <ncx:navLabel>
	    <ncx:text>Appendices</ncx:text>
	  </ncx:navLabel>
	  <ncx:content src="epub-appendix.html"/>
	</ncx:navPoint>
      </ncx:navMap>
    </ncx:ncx>
  </xsl:template>

  <xsl:template match="*">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="text()">
  </xsl:template>



</xsl:stylesheet>
