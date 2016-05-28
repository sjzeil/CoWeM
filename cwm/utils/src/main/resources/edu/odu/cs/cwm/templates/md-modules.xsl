<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  
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
  <xsl:param name="format" select="'directory'"/>
  <xsl:param name="formats" select="'directory'"/>
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
  	<xsl:variable name="sectioned">
	  <xsl:apply-templates select="$numbered" mode="sectioning"/>    
  	</xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="head"/>  
	  <xsl:apply-templates select="$sectioned"/>    
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

  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <div class="titleblock">
        <div class="courseName">@courseName@, @semester@</div>
        <h1 class="title">
           <xsl:value-of select="$Title"/>
        </h1>
        <xsl:if test="$Author != ''">
          <h2 class="author">
            <xsl:value-of select="$Author"/>
          </h2>
        </xsl:if>
        <xsl:if test="$Date != ''">
          <div class="date">
            <xsl:text>Last modified: </xsl:text>
            <xsl:value-of select="$Date"/>
           </div>
          </xsl:if>
      </div>
      
      <div class="center">
	    <div class="leftPart">
	      <iframe src="../navigation/index.html" class="navigation">_</iframe>
	    </div>
	    <div class="rightPart">
	      <xsl:variable name="preamble" select="section[normalize-space(./*[1]) = 'Preamble']"/>
	      <xsl:if test="$preamble">
	         <xsl:apply-templates select="$preamble/*[position() &gt; 1]"/>
	      </xsl:if>
	      <form action="">
            <div class="showHideControls">
	          <input type="button" value="expand all" onclick="expandAll()"/>
	          <input type="button" value="collapse all" onclick="collapseAll()"/>
	          <xsl:if test="contains($formats, 'topics')">
	  	         <input type="button" value="table view" onclick="visitPage('outline__topics.html')"/>
	          </xsl:if>
            </div>
          </form>
	      
	      <xsl:for-each select="section">
	           <xsl:variable name="sectionName" select="normalize-space(*[1])"/>
	           <xsl:choose>
	               <xsl:when test="$sectionName = 'Preamble'"/>
                   <xsl:when test="$sectionName = 'Postscript'"/>
	               <xsl:when test="$sectionName = 'Presentation'"/>
	               <xsl:otherwise>
                      <xsl:apply-templates select="."/>
	               </xsl:otherwise>
	           </xsl:choose>
	      </xsl:for-each>
          <xsl:variable name="postscript" select="section[normalize-space(./*[1]) = 'Postscript']"/>
          <xsl:if test="$postscript">
             <xsl:apply-templates select="$postscript/*[position() &gt; 1]"/>
          </xsl:if>
	      <xsl:call-template name="insertFooter"/>
	    </div>
      </div>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="section">
  	  <xsl:variable name="header" select="(h1|h2|h3|h4)[1]"/>
  	  <xsl:variable name="toggleID" select="generate-id()"/>
      <div class="topic{@depth}">
      	<xsl:value-of select="$header/@sectionNumber"/>
      	<xsl:choose>
      	    <xsl:when test="./section">
      		    <xsl:apply-templates select="$header/node()" mode="activities"/>
      	    </xsl:when>
      	    <xsl:otherwise>
      	    	<input type="button" value="+" class="expandButton" 
      	    	   onclick="toggleDisplay('{concat('_topic_', $toggleID)}')">
      	    	    <xsl:attribute name="id">
      	    	       <xsl:value-of select="concat('but_topic_', $toggleID)"/>
      	    	    </xsl:attribute>
      	    	</input>
      	        <xsl:apply-templates select="$header/node()"  mode="activities"/>
      	    </xsl:otherwise>
      	</xsl:choose>
      </div>
      <xsl:choose>
      	 <xsl:when test="./section">
      	    <xsl:if test="p|div">
      	       <div class="module">
                   <xsl:apply-templates select="$header/following-sibling::node()[local-name() != 'section']"/>
                </div>
      	    </xsl:if>
      	    <xsl:apply-templates select="section"/>
      	 </xsl:when>
      	 <xsl:otherwise>
      	    <div class="module">
      	       <xsl:attribute name="id">
      	    	  <xsl:value-of select="concat('_topic_', $toggleID)"/>
      	       </xsl:attribute>
      	       <xsl:choose>
      	           <xsl:when test="p|div">
      	              <table width="100%">
      	                 <tr>
      	                 	<td class="moduleDescription">
      	                 	   <div class="moduleDescription">
      	                 	       <xsl:variable name="activitiesList" select="ul[1]"/>
      	                 	       <xsl:apply-templates select="$header/following-sibling::node()[local-name() != 'ul']"/>
      	                 	   </div>
      	                 	</td>
      	                 	<td class="moduleActivitiesRt">
      	                 	   <div class="moduleActivitiesRt">
      	                 	       <b>Activities</b>
      	                 	       <xsl:apply-templates select="ul" mode="activities"/>
      	                 	   </div>
      	                 	</td>
      	                 </tr>
      	              </table>
      	           </xsl:when>
      	           <xsl:otherwise>
      	              <div class="moduleActivities">
      	                 <b>Activities</b>
      	                 <xsl:apply-templates select="ul" mode="activities"/>
      	              </div>
      	           </xsl:otherwise>
      	       </xsl:choose>
      	    </div>
      	 </xsl:otherwise>
      	</xsl:choose>
  </xsl:template>
    
  <xsl:template match="ul" mode="activities">
     <ol>
        <xsl:apply-templates select="*" mode="activities"/>
     </ol>
  </xsl:template>

  <xsl:template match="li" mode="activities">
     <xsl:choose>
        <xsl:when test=".//ul">
           <li>
              <xsl:copy-of select="@*"/>
              <span class="subject">
           	     <xsl:apply-templates select="text() | *[local-name() != 'ul']"/>
           	   </span>
           	   <xsl:apply-templates select="ul" mode="activities"/>
           	</li>
        </xsl:when>
        <xsl:otherwise>
           <li>
              <xsl:copy-of select="@*"/>
           	  <xsl:apply-templates select="node()"/>
           </li>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <xsl:template match="p" mode="activities">
     <p>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="node()"/>
     </p>
  </xsl:template>

  <xsl:template match="a" mode="activities">
      <xsl:variable name="parentName" select="local-name(..)"/>
      <xsl:choose>
          <xsl:when test="($parentName = 'h1') or ($parentName = 'h2') 
                      or ($parentName = 'h3') or ($parentName = 'h4')">
              <xsl:apply-templates select="node()"/>
          </xsl:when>
          <xsl:otherwise>
              <xsl:copy>
                  <xsl:copy-of select="@*"/>
                  <xsl:apply-templates select="node()"/>
              </xsl:copy>
          </xsl:otherwise>
      </xsl:choose>
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
