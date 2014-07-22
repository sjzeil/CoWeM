<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="../styles/footer.xsl"/>

  <!-- 
       Chops an HTML page into "slidy" style slides
       divided at each <h1>, <h2>, and <hr/> marker
  -->

  <xsl:param name="MathJaxURL" select="'../../styles/MathJax'"/>
  <xsl:param name="highlightjsURL" select="'../../styles/highlight.js'"/>
  <xsl:param name="slidyURL" select="'../../styles/Slidy2'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>

  <xsl:output method="xml" encoding="utf-8"/>


  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$slidyURL}/styles/slidy.css" />
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$stylesURL}/md-pages.css" />
      <script src="{$slidyURL}/scripts/slidy.js"
	      charset="utf-8" type="text/javascript">
	<xsl:text> </xsl:text>
      </script>
      <script src="{$stylesURL}/md-pages.js"
	      charset="utf-8" type="text/javascript">
	<xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="{$MathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	<xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="{$highlightjsURL}/styles/googlecode.css"/>
      <script src="{$highlightjsURL}/highlight.pack.js">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

    <div class="slide titleblock">
      <h1>
	<xsl:value-of select="/html/head/title/text()"/>
      </h1>
      <h2>
	<xsl:value-of select="/html/head/meta[@name='author']/@content"/>
      </h2>
      <xsl:if test="/html/head/meta[@name='date']">
	<p>
	  <xsl:text>Last modified: </xsl:text>
	  <xsl:value-of select="/html/head/meta[@name='date']/@content"/>
	</p>
      </xsl:if>

      <xsl:if test="/html/head/meta[@name='toc']">
	<div class="toc">
	  <xsl:text>Contents:</xsl:text>
	  <xsl:apply-templates select="h1 | h2" mode="toc"/>
	</div>
      </xsl:if>



    </div>

      <xsl:call-template name="splitIntoSlides">
	<xsl:with-param name="sequence" select="*"/>
      </xsl:call-template>

      <div class="slide">
	<span>
	  <xsl:attribute name="id">footer</xsl:attribute>
	</span>
	<xsl:call-template name="insertFooter"/>
      </div>
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
	  <xsl:when test="$nodeName = 'h1' or $nodeName = 'h2'">
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

  <xsl:template match="h1">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="span[@class='incremental']">
  </xsl:template>

  <xsl:template match="h1" mode="toc">
    <div class="toc-h1">
	<xsl:value-of select="1 + count(preceding-sibling::h1)"/>
	<xsl:text>. </xsl:text>
	<xsl:apply-templates select="*|text()"/>
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
	<h1 class="h2">
	  <xsl:copy-of select="@*"/>
	  <xsl:value-of select="$sectionNum"/>
	  <xsl:text>.</xsl:text>
	  <xsl:value-of select="$subsectionNum"/>
	  <xsl:text> </xsl:text>
	  <xsl:apply-templates select="*|text()"/>
	</h1>
      </xsl:when>
      <xsl:otherwise>
	<h1 class="h2">
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</h1>
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
	    <xsl:value-of select="$sectionNum"/>
	    <xsl:text>.</xsl:text>
	    <xsl:value-of select="$subsectionNum"/>
	    <xsl:text> </xsl:text>
	    <xsl:apply-templates select="*|text()"/>
	</div>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="code">
    <xsl:choose>
      <xsl:when test="@class != ''">
	<xsl:copy>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</xsl:copy>
      </xsl:when>
      <xsl:otherwise>
	<tt>
	  <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="*|text()"/>
	</tt>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="details">
    <div class="details">
      <xsl:variable name="idValue"
		    select="concat('_details_', generate-id())"/>
      <span class="summary">
        <xsl:apply-templates select="summary/* | summary/text()"/>
      </span>
      <xsl:text> </xsl:text>
      <input id="but{$idValue}" type="button" value="+"
        onclick="toggleDisplay('{$idValue}')"
        />
      <div style="display: none;">
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
	<xsl:apply-templates select="*[local-name() != 'summary'] | text()"/>
      </div>
    </div>
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
      <div style="display: none;">
	<xsl:attribute name="id">
	  <xsl:value-of select="$idValue"/>
	</xsl:attribute>
        <xsl:variable name="encoded" select="document(@file)"/>
        <xsl:copy-of select="$encoded/html/body/pre"/>
      </div>
    </div>    
  </xsl:template>

  <xsl:template match="includeHTML">
    <xsl:variable name="encoded" select="document(@file)"/>
    <xsl:copy-of select="$encoded/html/body/*"/>
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


  <xsl:template match="div|blockquote">
    <xsl:copy>
      <xsl:choose>
	<xsl:when test="p/span[@class='incremental']">
	  <xsl:choose>
	    <xsl:when test="@class">
	      <xsl:apply-templates select='@*' mode="incremental"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:copy-of select='@*'/>
	      <xsl:attribute name="class">
		<xsl:text>incremental</xsl:text>
	      </xsl:attribute>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:copy-of select='@*'/>
	</xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@class" mode="incremental">
    <xsl:attribute name="class">
      <xsl:value-of select="."/>
      <xsl:text> incremental</xsl:text>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*" mode="incremental">
    <xsl:copy-of select="."/>
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
