<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="./footer.xsl"/>
  <xsl:import href="bblink.xsl"/>

  <!-- 
       Chops an HTML page into "slidy" style slides
       divided at each <h1>, <h2>, and <hr/> marker
  -->

  <xsl:param name="Author" select="''"/>
  <xsl:param name="Date" select="''"/>

  <xsl:param name="doc" select="'doc'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>
  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="bbURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>
  <xsl:param name="altformats" select="'yes'"/>

  <xsl:output method="xml" encoding="iso-8859-1"/>



  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$stylesURL}/md-html.css" />
	  <meta name="viewport" content="width=device-width, initial-scale=1"/>	
      <script type="text/javascript"
	      src="{$stylesURL}/md-html.js">
	<xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="{$MathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	<xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="{$highlightjsURL}/../styles/googlecode.css"/>
      <script src="{$highlightjsURL}">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>

      <div class="titleblock">
	<h1>
	  <xsl:value-of select="/html/head/title/text()"/>
	</h1>
	<xsl:if test="$Author != ''">
	    <h2>
	      <xsl:value-of select="$Author"/>
	    </h2>
	</xsl:if>
	<xsl:if test="$Date != ''">
	  <p>
	    <xsl:text>Last modified: </xsl:text>
	    <xsl:value-of select="$Date"/>
	  </p>
	</xsl:if>
      </div>

      <xsl:if test="/html/head/meta[@name='toc']">
	<div class="toc">
	  <xsl:text>Contents:</xsl:text>
	  <xsl:apply-templates select="h1 | h2" mode="toc"/>
	</div>
      </xsl:if>

      <xsl:apply-templates select="*|text()"/>
      <xsl:call-template name="insertFooter"/>
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
  
  
   <xsl:template match="div[@class = 'slideshow']">
    <xsl:variable name="slideshowNum" select="1 + count(preceding::div[@class = 'slideshow'])"/>
    <xsl:variable name="slideCount" select="count(./div[@class = 'slideshowslide'])"/>
    <xsl:copy>
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates select="*|text()"/>
       <div id="slideshowControl{$slideshowNum}">
          <script>
             <xsl:text>sshowControl</xsl:text>
             <xsl:value-of select="$slideshowNum"/>
             <xsl:text> = { counter: 1,
             showNumber: </xsl:text>
             <xsl:value-of select="$slideshowNum"/>
             <xsl:text>, max: </xsl:text>
             <xsl:value-of select="$slideCount"/>
             <xsl:text>};
             </xsl:text>          
          </script>
          <table class="slideshowcontrol">
             <tr class="slideshowcontrol">
               <td class="slideshowcontrol">
                   <a class="slideshowcontrol" onclick="sshowback(sshowControl{$slideshowNum})" title="previous">
                      <xsl:text>&#x25C0;</xsl:text>
                   </a>
               </td>
               <td id="slideshowposition{$slideshowNum}" class="slideshowcontrol">
                  <xsl:text>1 of </xsl:text>
                  <xsl:value-of select="$slideCount"/>
               </td>
            
               <td class="slideshowcontrol">
                   <a class="slideshowcontrol" onclick="sshowforward(sshowControl{$slideshowNum})" title="next">
                      <xsl:text>&#x25B6;</xsl:text>
                   </a>
               </td>
             </tr>
          </table>
       </div>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="div[@class = 'slideshowslide']">
    <xsl:variable name="slideshowNum" select="1 + count(preceding::div[@class = 'slideshow'])"/>
    <xsl:variable name="slideNum" select="count(preceding-sibling::div[@class = 'slideshowslide'])"/>
    <xsl:copy>
      <xsl:attribute name="id">
         <xsl:text>slide-</xsl:text>
         <xsl:value-of select="$slideshowNum"/>
         <xsl:text>-</xsl:text>
         <xsl:value-of select="$slideNum"/>
      </xsl:attribute>
      <xsl:if test="$slideNum != 0">
         <xsl:attribute name="style">
            <xsl:text>display: none;</xsl:text>
         </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
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
    <xsl:copy>
      <xsl:copy-of select="@*[name() != 'href']"/>
      <xsl:attribute name="href">
	<xsl:choose>
	  <xsl:when test="starts-with(@href, 'bb:')">
	    <xsl:call-template name="bblinkConvert">
	      <xsl:with-param name="bbcourseURL"
			      select="$bbURL"/>
	      <xsl:with-param name="bblinkURL"
			      select="@href"/>
	    </xsl:call-template>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="@href"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
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

</xsl:stylesheet>
