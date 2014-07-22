<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <!-- 
       Chops an HTML page into "slidy" style slides
       divided at each <h1>, <h2>, and <hr/> marker
  -->

  <xsl:output method="xml" encoding="utf-8"/>



  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="../../styles/Slidy2/styles/slidy.css" />
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="../../styles/mmd-slidy.css" />
      <script src="../../styles/Slidy2/scripts/slidy.js"
	      charset="utf-8" type="text/javascript">
	<xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="../../styles/MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	<xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="../../styles/highlight.js/styles/default.css"/>
      <script src="../../styles/highlight.js/highlight.pack.js">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:call-template name="splitIntoSlides">
	<xsl:with-param name="sequence" select="*"/>
      </xsl:call-template>
      <!-- xsl:copy-of select="document('../templates/footer.xml')"/ -->
    </xsl:copy>
  </xsl:template>


  <xsl:template name="splitIntoSlides">
    <xsl:param name="sequence"/>
    <xsl:if test="count($sequence) &gt; 0">
      <xsl:variable name="first" select="$sequence[1]"/>
      <xsl:variable name="rest" select="$sequence[position() &gt; 1]"/>
      <xsl:variable name="breakPos">
	<xsl:call-template name="findBreak">
	  <xsl:with-param name="seq" select="$rest"/>
	</xsl:call-template>
      </xsl:variable>
      <div class="slide">
	<xsl:if test="local-name($first) != 'hr'">
	  <xsl:apply-templates select="$first"/>
	</xsl:if>
	<xsl:apply-templates select="$rest[position() &lt; $breakPos]"/>
      </div>
      <xsl:call-template name="splitIntoSlides">
	<xsl:with-param name="sequence" 
			select="$rest[position() &gt;= $breakPos]"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
	
  <xsl:template name="findBreak">
    <xsl:param name="seq"/>
    <xsl:choose>
      <xsl:when test="count($seq) = 0">
	<xsl:value-of select="0"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="nodeName" select="local-name($seq[1])"/>
	<xsl:choose>
	  <xsl:when test="$nodeName = 'h1' or $nodeName = 'h2' or $nodeName = 'hr'">
	    <xsl:value-of select="1"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:variable name="continue">
	      <xsl:call-template name="findBreak">
		<xsl:with-param name="seq" select="$seq[position() &gt; 1]"/>
	      </xsl:call-template>
	    </xsl:variable>
	    <xsl:value-of select="$continue+1"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="span[@class='incremental']">
  </xsl:template>

  <xsl:template match="ul|ol">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:if test="li/p/span[@class='incremental'] | li/span[@class='incremental']">
	<xsl:attribute name="class">
	  <xsl:text>incremental</xsl:text>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
