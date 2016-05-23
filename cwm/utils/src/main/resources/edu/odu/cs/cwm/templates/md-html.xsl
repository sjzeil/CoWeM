<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="md-common.xsl"/>
  <xsl:import href="paginate.xsl"/>
  
  
  <xsl:param name="meta_Author" select="''"/>
  <xsl:param name="meta_CSS" select="''"/>
  <xsl:param name="meta_Date" select="''"/>
  <xsl:param name="meta_Title" select="'@Title@'"/>
  <xsl:param name="meta_TOC" select="''"/>

  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="courseTitle" select="'@courseTitle@'"/>
  <xsl:param name="semester" select="'@semester@'"/>
  <xsl:param name="sem" select="'@sem@'"/>
  <xsl:param name="instructor" select="'@instructor@'"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="copyright" select="''"/>
  <xsl:param name="primaryDocument" select="'@primaryDocument@'"/>
  <xsl:param name="format" select="'html'"/>
  <xsl:param name="formats" select="'html'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>

  <xsl:param name="baseURL" select="'../../'"/>
  <xsl:param name="homeURL" select="'../../index.html'"/>

  <xsl:param name="altformats" select="'yes'"/>
  <xsl:param name="numberingDepth" select="'3'"/>

  <xsl:output method="xml" encoding="UTF-8"/>

  <xsl:variable name="stylesURL" select="concat($baseURL, 'styles/')"/>
  <xsl:variable name="graphicsURL" select="concat($baseURL, 'graphics/')"/>

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
  	<xsl:variable name="numbered">
	  <xsl:apply-templates select="body" mode="sectionNumbering"/>    
  	</xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="head"/>  
	  <xsl:apply-templates select="$numbered"/>    
    </html>
  </xsl:template>

  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="{$stylesURL}/md-{$format}.css" />
	  <xsl:call-template name="generateCSSLinks"/>
	  <meta name="viewport" content="width=device-width, initial-scale=1"/>	
      <script type="text/javascript"
	      src="{$stylesURL}/md-{$format}.js">
	      <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
	      src="{$mathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
	      <xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" 
	    href="@highlightjsURL@/styles/googlecode.css"/>
      <script src="@highlightjsURL@/highlight.pack.js">
	<xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <xsl:copy-of select="*"/>
    </xsl:copy>
	  <xsl:call-template name="generateJSLinks"/>
  </xsl:template>
  
  <xsl:template name="generateCSSLinks">
    <xsl:if test="$meta_CSS != ''">
      <xsl:for-each select="tokenize($meta_CSS,',')">
          <link 
          	rel="stylesheet" 
          	type="text/css" 
          	media="screen, projection, print"
	    	href="normalize-space(.)" />
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="generateJSLinks">
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>

      <div class="titleblock">
	    <h1 class="title">
	       <xsl:value-of select="$meta_Title"/>
	    </h1>
	    <xsl:if test="$meta_Author != ''">
	      <h2 class="author">
	        <xsl:value-of select="$meta_Author"/>
	      </h2>
	    </xsl:if>
	    <xsl:if test="$meta_Date != ''">
	      <div class="date">
	        <xsl:text>Last modified: </xsl:text>
	        <xsl:value-of select="$meta_Date"/>
	       </div>
	      </xsl:if>
      </div>

      <xsl:if test="$meta_TOC != ''">
	    <div class="toc">
	      <xsl:text>Contents:</xsl:text>
	        <xsl:apply-templates select="h1 | h2" mode="toc"/>
	    </div>
      </xsl:if>

      <xsl:apply-templates select="node()"/>
      
      <xsl:call-template name="insertFooter"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1|h2|h3|h4|h5">
    <xsl:copy>
      <xsl:copy-of select="@*[local-name() != 'sectionNumber']"/>
      <xsl:choose>
      	<xsl:when test="a[@name != '']">
      	    <xsl:attribute name="id">
      	    	<xsl:value-of select="a/@name"/> 
      	    </xsl:attribute>
	        <xsl:value-of select="@sectionNumber"/>
    	    <xsl:apply-templates select="a/node()"/>
      	</xsl:when>
      	<xsl:otherwise>
          <xsl:value-of select="@sectionNumber"/>
          <xsl:apply-templates select="*|text()"/>
      	</xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h1|h2|h3" mode="toc">
    <div class="toc-{local-name(.)}">
      <a href="#{@id}">
	    <xsl:value-of select="@sectionNumber"/>
	    <xsl:apply-templates select="*|text()"/>
      </a>
    </div>
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
      <xsl:copy-of select="@*"/>
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
