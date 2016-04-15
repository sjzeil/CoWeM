<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

  <!-- 
       Generates an index.html file providing access to each 
       of the output document formats listed in a build.xml file.
  -->

  <xsl:param name="doc"/>
  <xsl:param name="title"/>
  <xsl:param name="modDate"/>

  <xsl:output method="xml" encoding="utf-8"/>


  <xsl:template match="/">
    <html>
      <head>
	<title>
	  <xsl:value-of select="$title"/>
	</title>
	<link href="../../styles/md-directory.css" 
		    rel="stylesheet" type="text/css"/>
      </head>
      <body>
	<xsl:comment>*automatically generated index file*</xsl:comment>
	<h1>
	  <xsl:value-of select="$title"/>
	</h1>
	<h2>
	  <xsl:text> - Available Formats -</xsl:text>
	</h2>
	<p>
	  <table class="formatTable" border="1">
	    <tr>
	      <th>Formatted for...</th>
	      <th>File</th>
	    </tr>

	    <xsl:apply-templates select="/project/target/docformat"/>
	  </table>
	</p>
	<p>
	  <xsl:text>Last modified on: </xsl:text>
	  <xsl:value-of select="$modDate"/>
	</p>
	<xsl:copy-of select="document('../styles/footer.xml')/div/*"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="docformat">
    <tr>
      <td>
	<xsl:choose>
	  <xsl:when test="@format = 'web'">
	    <xsl:text>on-line viewing (PDF)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'printable'">
	    <xsl:text>printing (PDF, 8.5x11 inches)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'slides'">
	    <xsl:text>slides for presentation in class (PDF)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'slidy'">
	    <xsl:text>slides for presentation in class (HTML)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'pages'">
	    <xsl:text>general viewing (HTML, multiple pages)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'html'">
	    <xsl:text>printing or viewing (HTML, single page)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'directory'">
	    <xsl:text>course directory page</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'topics'">
	    <xsl:text>course outline of topics</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'modules'">
	    <xsl:text>course topics by module</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'epub'">
	    <xsl:text>offline viewing (epub)</xsl:text>
	  </xsl:when>
	  <xsl:when test="@format = 'mobi'">
	    <xsl:text>offline viewing (mobi)</xsl:text>
	  </xsl:when>
	</xsl:choose>
      </td>
      <td>
	<xsl:variable name="ext">
	  <xsl:choose>
	    <xsl:when test="@format = 'web'">
	      <xsl:text>pdf</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'printable'">
	      <xsl:text>pdf</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'slides'">
	      <xsl:text>pdf</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'slidy'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'pages'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'html'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'directory'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'topics'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'modules'">
	      <xsl:text>html</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'epub'">
	      <xsl:text>epub</xsl:text>
	    </xsl:when>
	    <xsl:when test="@format = 'mobi'">
	      <xsl:text>mobi</xsl:text>
	    </xsl:when>
	  </xsl:choose>
	</xsl:variable>
	<span class="but">
	  <xsl:choose>
	    <xsl:when test="@format = 'epub' or @format = 'mobi'">
	      <a class="but" href="../../Directory/ebooks/">Open</a>
	    </xsl:when>
	    <xsl:otherwise>
	      <a class="but" href="{$doc}__{@format}.{$ext}">Open</a>
	    </xsl:otherwise>
	  </xsl:choose>
	</span>
      </td>
    </tr>
  </xsl:template>


</xsl:stylesheet>
