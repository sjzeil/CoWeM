<?xml version="1.0" encoding="UTF-8"?>
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
  <xsl:param name="formats" select="'scroll'"/>
  <xsl:param name="mathSupport" select="'latex'"/>
  <xsl:param name="mathJaxURL" select="'@mathJaxURL@'"/>
  <xsl:param name="highlightjsURL" select="'@highlightjsURL@'"/>
  <xsl:param name="highlightjsCSSURL" select="'@highlightjsCSSURL@'"/>

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
      <meta charset="UTF-8"/>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}.css" />
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}-ext.css" />
      <xsl:call-template name="generateCSSLinks"/>
      <meta name="viewport" content="width=device-width, initial-scale=1"/> 
      <link rel="stylesheet" 
        href="@highlightjsCSSURL@"/>
      <script src="@highlightjsURL@">
    <xsl:text> </xsl:text>
      </script>
      <script>hljs.initHighlightingOnLoad();</script>
      <script type="text/javascript"
          src="{$stylesURL}/md-{$format}.js">
          <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
          src="{$stylesURL}/md-{$format}-ext.js">
          <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
          src="{$stylesURL}/rawdeflate.js">
          <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
          src="{$stylesURL}/plantuml.js">
          <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
          src="{$stylesURL}/mermaid.min.js">
          <xsl:text> </xsl:text>
      </script>
      <xsl:choose>
        <xsl:when test="lower-case($mathSupport) = 'latex'">
           <script type="text/javascript"><xsl:text>
             window.MathJax = {
               tex2jax: {
               inlineMath: [ ['$','$'], ["\\(","\\)"] ],
               processEscapes: true
             }
           };
           </xsl:text></script>
           <script type="text/javascript"
             src="{$mathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
             <xsl:text> </xsl:text>
           </script>
        </xsl:when>
        <xsl:when test="lower-case($mathSupport) = 'ascii'">
           <script type="text/javascript"><xsl:text>
             window.MathJax = {
               asciimath2jax: {
                 delimiters: [ ['$','$'], ['`', '`']],
               processEscapes: true
             }
           };
           </xsl:text></script>
           <script type="text/javascript"
             src="{$mathJaxURL}/MathJax.js?config=AM_HTMLorMML">
             <xsl:text> </xsl:text>
           </script>
        </xsl:when>
      </xsl:choose>
      <xsl:copy-of select="*"/>
    </xsl:copy>
      <xsl:call-template name="generateJSLinks"/>
  </xsl:template>



  <xsl:template name="generateCSSLinks">
    <xsl:if test="$CSS != ''">
      <xsl:for-each select="tokenize($CSS,'\t')">
          <link 
            rel="stylesheet" 
            type="text/css" 
            media="screen, projection, print"
            href="{normalize-space(.)}" />
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
  <xsl:variable name="formatsList" select="substring-before(substring-after($formats, '['), ']')"/>
  <xsl:for-each select="tokenize($formatsList,',')">
      <xsl:call-template name="linkToFormat">
          <xsl:with-param name="otherFormat" select="normalize-space(.)"/>
      </xsl:call-template> 
  </xsl:for-each>
</xsl:template>

<xsl:template name="insertHeader">
      <xsl:variable name='slideshowNum' select="'0'"/>
    <xsl:variable name='slideCount' select="1 + count(./ancestor-or-self::body/page)"/>
    <xsl:if test="$slideCount != 0">
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
             <xsl:text>window.onhashchange = hashHasChanged;</xsl:text>        
          </script>
    </xsl:if>
  
    <xsl:variable name='slideshowNum' select="'0'"/>
    <xsl:variable name='slideCount' select="count(./ancestor-or-self::body/page)"/>
    <div id="slideshowControlA0" class="navHeader">
          <table class="navHeader">
             <tr class="slideshowcontrol">
               <td class="slideshowcontrolLeft">
                   <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshow2start(sshowControl{$slideshowNum})" 
                          title="start">
                          <img src="graphics:first.png" alt="first"/>
                       </a>
                       <span style="minWidth: 2ex;">&#160;&#160;</span>
                       <a class="slideshowcontrol"
                          onclick="sshowback(sshowControl{$slideshowNum})"
                          title="previous">
                          <img src="graphics:prev.png" alt="prev"/>
                       </a>
                   </xsl:if>
               </td>
               <td class="slideshowcontrolMiddle">
                  <xsl:if test="$slideCount != 0">
                     <span id="slideshowpositionA{$slideshowNum}"> 
                         <xsl:text>1 of </xsl:text>
                         <xsl:value-of select="$slideCount"/>
                     </span>
                  </xsl:if>
                  <xsl:call-template name="insertNavIcons"/>
               </td>
            
               <td class="slideshowcontrolRight">
                  <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshowforward(sshowControl{$slideshowNum})" 
                          title="next">
                          <img src="graphics:next.png" alt="next"/>
                       </a>
                       <span style="minWidth: 2ex;">&#160;&#160;</span>
                       <a class="slideshowcontrol" 
                          onclick="sshow2end(sshowControl{$slideshowNum})" 
                          title="last">
                          <img src="graphics:last.png" alt="last"/>
                       </a>
                   </xsl:if>
               </td>
             </tr>
          </table>
          </div>
</xsl:template>


<xsl:template name="insertFooter">
    <xsl:variable name='slideshowNum' select="'0'"/>
    <xsl:variable name='slideCount' select="1 + count(./ancestor-or-self::body/page)"/>
    <xsl:if test="$slideCount != 0">
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
             <xsl:text>window.onhashchange = hashHasChanged;</xsl:text>        
          </script>
    </xsl:if>
  
    <xsl:variable name='slideshowNum' select="'0'"/>
    <xsl:variable name='slideCount' select="count(./ancestor-or-self::body/page)"/>
    <div id="slideshowControl0" class="navFooter">
          <table class="navFooter">
             <tr class="slideshowcontrol">
               <td class="slideshowcontrolLeft">
                   <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshow2start(sshowControl{$slideshowNum})" 
                          title="start">
                          <img src="graphics:first.png" alt="first"/>
                       </a>
                       <span>&#160;</span>
                       <a class="slideshowcontrol"
                          onclick="sshowback(sshowControl{$slideshowNum})"
                          title="previous">
                          <img src="graphics:prev.png" alt="prev"/>
                       </a>
                   </xsl:if>
               </td>
               <td class="slideshowcontrolMiddle">
                  <xsl:if test="$slideCount != 0">
                     <span id="slideshowposition{$slideshowNum}"> 
                         <xsl:text>1 of </xsl:text>
                         <xsl:value-of select="$slideCount"/>
                     </span>
                  </xsl:if>
                  <xsl:call-template name="insertNavIcons"/>
               </td>
            
               <td class="slideshowcontrolRight">
                  <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshowforward(sshowControl{$slideshowNum})" 
                          title="next">
                          <img src="graphics:next.png" alt="next"/>
                       </a>
                       <span>&#160;</span>
                       <a class="slideshowcontrol" 
                          onclick="sshow2end(sshowControl{$slideshowNum})" 
                          title="last">
                          <img src="graphics:last.png" alt="last"/>
                       </a>
                   </xsl:if>
               </td>
             </tr>
          </table>
          </div>

    <xsl:if test="$copyright != ''">
      <div class="copyright">
        <xsl:text>&#169; </xsl:text>
        <xsl:value-of select="$copyright"/>
     </div>
   </xsl:if> 
</xsl:template>



  <xsl:template match="h1|h2|h3|h4|h5">
    <xsl:if test="contains(@id, '-')">
        <a id="{translate(@id, '-', '')}" />
    </xsl:if>
    <xsl:copy>
      <xsl:copy-of select="@*[local-name() != 'sectionNumber']"/>
      <xsl:value-of select="@sectionNumber"/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

	<xsl:template match="h1|h2|h3" mode="toc">
		<xsl:if test="count(./ancestor::page[@increm &gt; 0]) = 0">
			<div class="toc-{local-name(.)}">
				<a href="#{@id}">
					<xsl:value-of select="@sectionNumber" />
					<xsl:apply-templates select="*|text()" />
				</a>
			</div>
		</xsl:if>
	</xsl:template>


  <xsl:template match="pre[starts-with(code/@class, 'plantuml')]">
	  <xsl:variable name="idValue"
            select="generate-id()"/>
      <xsl:variable name="classesMarker">
          <xsl:text>classes='</xsl:text>
      </xsl:variable>
      <xsl:variable name="singleQuote">
          <xsl:text>'</xsl:text>
      </xsl:variable>
      <xsl:variable name='classNames'>
         <xsl:choose>
            <xsl:when test="contains(code/@class, $classesMarker)">
                <xsl:value-of select='substring-before(substring-after(code/@class, $classesMarker), $singleQuote)'/>
            </xsl:when>
            <xsl:when test="contains(code/@class, 'class=')">
                <xsl:value-of select='substring-before(substring-after(concat(code/@class, " "), "class="), " ")'/>
            </xsl:when>
         </xsl:choose>
      </xsl:variable>
      <xsl:text>
      </xsl:text>
      <div class="noFloat">&#xA0;</div>
      <pre style="display:none" id="pre{$idValue}">
         <xsl:copy-of select="code"/>
      </pre>
      <img class="{$classNames}" src="" id="img{$idValue}"> </img>
      <script>
      	<xsl:text>plantuml("pre</xsl:text>
      	<xsl:value-of select="$idValue"/>
      	<xsl:text>", "img</xsl:text>
      	<xsl:value-of select="$idValue"/>
      	<xsl:text>");
      	</xsl:text>
      </script>
  </xsl:template>

  <xsl:template match="pre[starts-with(code/@class, 'mermaid')]">
      <xsl:variable name="classesMarker">
          <xsl:text>classes='</xsl:text>
      </xsl:variable>
      <xsl:variable name="singleQuote">
          <xsl:text>'</xsl:text>
      </xsl:variable>
      <xsl:variable name='classNames'>
         <xsl:choose>
            <xsl:when test="contains(code/@class, $classesMarker)">
                <xsl:value-of select='substring-before(substring-after(code/@class, $classesMarker), $singleQuote)'/>
            </xsl:when>
            <xsl:when test="contains(code/@class, 'class=')">
                <xsl:value-of select='substring-before(substring-after(concat(code/@class, " "), "class="), " ")'/>
            </xsl:when>
         </xsl:choose>
      </xsl:variable>
      <xsl:text>
      </xsl:text>
      <div class="noFloat"> </div>
      <span class="{$classNames}">
  		<code class="mermaid"><xsl:copy-of select="code/node()"/></code>
      </span>
  </xsl:template>


  <xsl:template match="code[local-name(..) = 'pre']">
    <xsl:choose>
      <xsl:when test="@class = 'raw'">
    <tt>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="*|text()"/>
    </tt>
      </xsl:when>
      <xsl:otherwise>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
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
      <div class="detailsBody" style="display: none;">
    <xsl:attribute name="id">
      <xsl:value-of select="$idValue"/>
    </xsl:attribute>
    <xsl:apply-templates select="node()"/>
      </div>
    </div>
  </xsl:template>

	<xsl:template match="example">
		<xsl:variable name="exampleCounter" select="1 + count(preceding::example)" />
		<blockquote class="example" id="example{$exampleCounter}">
			<div class="exampleTitle">
				<xsl:text>Example </xsl:text>
				<xsl:value-of select="$exampleCounter" />
				<xsl:text>: </xsl:text>
				<xsl:value-of select="@title" />
			</div>
			<xsl:apply-templates select="node()" />
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
               <td class="slideshowcontrolLeft">
                   <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshow2start(sshowControl{$slideshowNum})" 
                          title="start">
                          <img src="graphics:small-first.png" alt="first"/>
                       </a>
                       <span>&#160;</span>
                       <a class="slideshowcontrol"
                          onclick="sshowback(sshowControl{$slideshowNum})"
                          title="previous">
                          <img src="graphics:small-prev.png" alt="prev"/>
                       </a>
                   </xsl:if>
               </td>
               <td class="slideshowcontrolMiddle">
                  <xsl:if test="$slideCount != 0">
                     <span id="slideshowposition{$slideshowNum}"> 
                         <xsl:text>1 of </xsl:text>
                         <xsl:value-of select="$slideCount"/>
                     </span>
                  </xsl:if>
               </td>
            
               <td class="slideshowcontrolRight">
                  <xsl:if test="$slideCount != 0">
                       <a class="slideshowcontrol" 
                          onclick="sshowforward(sshowControl{$slideshowNum})" 
                          title="next">
                          <img src="graphics:small-next.png" alt="next"/>
                       </a>
                       <span>&#160;</span>
                       <a class="slideshowcontrol" 
                          onclick="sshow2end(sshowControl{$slideshowNum})" 
                          title="last">
                          <img src="graphics:small-last.png" alt="last"/>
                       </a>
                   </xsl:if>
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
      <div class="noFloat">&#160;</div>
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

  <xsl:template match="p|div">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
    <xsl:text>
    </xsl:text>
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
	    <xsl:when test="$otherFormat = 'scroll'">
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

