<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <!-- 
       Generates the common footer for all documents.
       Generate link elements for 
          - course home URL
          - email contact
          - course forums (on Blackboard)
          - integrated course forum ("discuss this page")
       depending on whether the appropriate parameters
       (presumably drawn from entries in course.properties)
       have been given non-empty values/
  -->

  <xsl:param name="Author" select="''"/>
  <xsl:param name="CSS" select="''"/>
  <xsl:param name="Date" select="''"/>
  <xsl:param name="Title" select="'@Title@'"/>
  <xsl:param name="TOC" select="''"/>

  <xsl:param name="courseName" select="'@courseName@'"/>
  <xsl:param name="courseTitle" select="'@courseTitle@'"/>
  <xsl:param name="semester" select="'@semester@'"/>
  <xsl:param name="sem" select="'@sem@'"/>
  <xsl:param name="instructor" select="'@instructor@'"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="copyright" select="''"/>
  <xsl:param name="primaryDocument" select="'@primaryDocument@'"/>
  <xsl:param name="formats" select="'html'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>

  <xsl:param name="baseURL" select="'../../'"/>
  <xsl:param name="homeURL" select="'../../index.html'"/>

  <xsl:param name="altformats" select="'yes'"/>
  <xsl:param name="numberingDepth" select="'3'"/>
  <xsl:param name="documentSetPath" select="'.'"/>

  <xsl:output method="xml" encoding="UTF-8"/>

  <xsl:variable name="stylesURL" select="concat($baseURL, 'styles')"/>
  <xsl:variable name="graphicsURL" select="concat($baseURL, 'graphics')"/>


  <xsl:template match="head">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}.css" />
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}-ext.css" />
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
    <xsl:if test="$CSS != ''">
      <xsl:for-each select="tokenize($CSS,',')">
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

<xsl:template name="insertNavIcons">
  <xsl:if test="$homeURL != ''">
    <xsl:text>&#10;</xsl:text>
    <a class="imgLink" href="{$homeURL}" title="Course home/outline">
      <img src="{$baseURL}graphics/home.png"/>
    </a>
  </xsl:if>
  <xsl:if test="$email != ''">
    <xsl:variable name="subject" 
       select="encode-for-uri(concat($courseName, ', ', $Title))"/>
    <xsl:text>&#10;</xsl:text>
    <a href="mailto:{$email}?subject={$subject}">
      <img src="{$baseURL}graphics/email.png" title="Email to instructor"/>
    </a>
  </xsl:if>
  <span style="margin: 0 32px;"></span>
  <xsl:for-each select="tokenize($formats,',')">
      <xsl:call-template name="linkToFormat">
          <xsl:with-param name="otherFormat" select="normalize-space(.)"/>
      </xsl:call-template> 
  </xsl:for-each>
</xsl:template>

<xsl:template name="insertHeader">
  <div class="navHeader">
    <div style="text-align: center; border-bottom: solid #000040; margin-bottom: 40px;">
      <xsl:call-template name="insertNavIcons"/>
    </div>
  </div>
</xsl:template>


<xsl:template name="insertFooter">
  <div class="footer">
    <div style="text-align: center; border-top: solid #000040; margin-top: 40px;">
      <xsl:call-template name="insertNavIcons"/>
    </div>
    <xsl:if test="$copyright != ''">
      <div class="copyright">
        <xsl:text>&#169; </xsl:text>
        <xsl:value-of select="$copyright"/>
     </div>
   </xsl:if> 
  </div>
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
        <xsl:value-of select="@summary"/>
      </span>
      <xsl:text> </xsl:text>
      <input id="but{$idValue}" type="button" value="+"
        onclick="toggleDisplay('{$idValue}')"
        />
      <div style="display: none;">
    <xsl:attribute name="id">
      <xsl:value-of select="$idValue"/>
    </xsl:attribute>
    <xsl:apply-templates select="node()"/>
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
  
  <xsl:template match="splitColumns">
    <div class="splitColumns">
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates select="node()"/>
    </div>
  </xsl:template>

  <xsl:template match="leftColumn">
    <div class="leftColumn">
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates select="node()"/>
    </div>
  </xsl:template>

  <xsl:template match="rightColumn">
    <div class="rightColumn">
       <xsl:copy-of select="@*"/>
       <xsl:apply-templates select="node()"/>
    </div>
  </xsl:template>
  
   <xsl:template match="slideshow">
    <xsl:variable name="slideshowNum" select="1 + count(preceding::slideshow)"/>
    <xsl:variable name="slideCount" select="count(./slideshowslide)"/>
    <div class="slideshow">
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
    </div>
  </xsl:template>
  
  
  <xsl:template match="slideshowslide">
    <xsl:variable name="slideshowNum" select="1 + count(preceding::slideshow)"/>
    <xsl:variable name="slideNum" select="count(preceding-sibling::slideshowslide)"/>
    <div class='slideshowslide'>
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
    </div>
  </xsl:template>
  
  <xsl:template match="sidebar">
      <div class="noFloat"> </div>
      <div class="sidebar pct{@width}">
          <xsl:apply-templates select="node()"/>
      </div>
  </xsl:template>

  <xsl:template match="longlisting">
    <div class="details">
      <xsl:variable name="idValue"
            select="concat('_details_', generate-id())"/>
      <span class="summary">
        <a href="{@file}" target="listing">
          <xsl:value-of select="@file"/>
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
      <pre><code id="{$idValue}_code">
      <xsl:copy-of select="text()"/>
      </code></pre>
      </div>
    </div>    
  </xsl:template>
  
  
    <xsl:template match="includeHTML">
    <xsl:variable name="encoded" select="document(@file)"/>
    <xsl:copy-of select="$encoded/html/body/*"/>
  </xsl:template>

  
<xsl:template name="linkToFormat">
  <xsl:param name="otherFormat"/>
  <xsl:if test="$primaryDocument != ''">
    <xsl:if test="$otherFormat != $format">
      <xsl:text>&#10;</xsl:text>
      <xsl:choose>
        <xsl:when test="$otherFormat = 'topics'">
        </xsl:when>
        <xsl:when test="$otherFormat = 'modules'">
        </xsl:when>
        <xsl:when test="$otherFormat = 'epub'">
	      <a class="imgLink" href="{$baseURL}Directory/ebooks/index.html">
	        <img src="{$baseURL}graphics/{$otherFormat}.png" title="e-book for course"/>
	      </a>      
        </xsl:when>
        <xsl:when test="$otherFormat = 'mobi'"></xsl:when>
	    <xsl:when test="$otherFormat = 'html'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="single-page HTML version"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'pages'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="multi-page HTML version"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'slides'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="Slides for lectures"/>
	        </a>
	    </xsl:when>
	    <xsl:when test="$otherFormat = 'slidy'">
	        <a class="imgLink" href="{$primaryDocument}__{$otherFormat}.html">
	          <img src="{$baseURL}graphics/{$otherFormat}.png" title="Slides for lectures"/>
	        </a>
	    </xsl:when>
	  </xsl:choose>
    </xsl:if>
  </xsl:if>
</xsl:template>
  

</xsl:stylesheet>

