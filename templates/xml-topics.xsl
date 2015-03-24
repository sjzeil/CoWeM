<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
>

  <xsl:import href="../styles/footer.xsl"/>
  <xsl:import href="bblink.xsl"/>

  <!-- Converts the course outline to a tabular view. The columns
       of the table are described in the presentation section of the
       outline file.
  -->

  <xsl:param name="doc" select="'outline'"/>
  <xsl:param name="format" select="'topics'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="bbURL" select="/outline/@bbcourse"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../outline'"/>

  <xsl:output method="xml" indent="yes"  encoding="utf-8"/>


  <xsl:variable name="columnKinds">
    <xsl:text> </xsl:text>
    <xsl:for-each select="/outline/presentation/column">
      <xsl:value-of select="@kinds"/>
    <xsl:text> </xsl:text>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name="titleTable" select="document(concat($pwdURL, '/titleTable.xml'))/table"/>


  <xsl:template match="/">
    <html>
      <head>
	     <title>@courseName@ Outline &amp; Topics</title>
	     <link rel="stylesheet" type="text/css" media="screen, projection, print"
	        href="{$stylesURL}/outline.css" />
	     <meta name="viewport" content="width=device-width, initial-scale=1"/>	
	     <script type="text/javascript"
		    src="{$stylesURL}/outline.js">
	       <xsl:text> </xsl:text>
         </script>
         <link rel="stylesheet" type="text/css" media="screen, projection, print"
	       href="local.css" />
      </head>
      <body>
	<div class="titleBlock">
	  <div class="courseName">@courseName@, @semester@</div>
	  <h1>
	    <xsl:value-of select="/html/head/title/text()"/>
	  </h1>
	</div>
	<div class="center">
	  <div class="leftPart">
	    <xsl:copy-of 
		select="document(concat($pwdURL, '../outline/buttons.xml'))"/>
	  </div>
	  <div class="rightPart">
	    <xsl:apply-templates select="/outline"/>
	  </div>
	  <xsl:call-template name="insertFooter"/>
	</div>
      </body>
    </html>
  </xsl:template>


  <xsl:template match="outline">
    <xsl:if test="preamble">
      <xsl:apply-templates select="preamble/*|preamble/text()" mode="copying"/>
    </xsl:if>

    <xsl:variable name="buildURL">
      <xsl:value-of select="concat($pwdURL, 'build.xml')"/>
    </xsl:variable>


    <form action="">
      <div class="showHideControls">
	<xsl:if test="document($buildURL)/project/target[@name='documents']/docformat[@format='topics']">
	  	<input type="button" value="outline view"
		       onclick="visitPage('outline__modules.html')"/>
	</xsl:if>
      </div>
    </form>

    <table border="1" rules="all" 
	   frame="box" role="outline">
      <tr class="columnheader">
	<xsl:apply-templates select="/outline/presentation/column"/>
      </tr>
      <xsl:apply-templates select="topic | subject | item"/>
    </table>

    <xsl:if test="postscript">
      <xsl:apply-templates select="postscript/*|postscript/text()" mode="copying"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="column">
    <th role="columnheader">
      <xsl:value-of select="@title"/>
    </th>
  </xsl:template>

  <xsl:template match="topic | subject" mode="topicSpacing">
    <span style="width: 3em;"/>
  </xsl:template>

  <xsl:template match="description">
    <!-- Ignored: only shown in outline view -->
  </xsl:template>


  <xsl:template match="topic | subject">
    <xsl:variable name="className"
      select="concat('topic', 1+count(ancestor::topic) +count(ancestor::subject))"/>


    <tr class="{$className}">
      <td class="{$className}" 
	     colspan="{count(/outline/presentation/column)}">
	<xsl:apply-templates select="ancestor::topic | ancestor::subject" mode="topicSpacing"/>
	<span class="{$className}">
	  <xsl:if test="local-name() = 'topic'">
	  <xsl:apply-templates select="." mode="itemNumber"/>
	  <xsl:text> </xsl:text>
	  </xsl:if>
	  <xsl:choose>
	    <xsl:when test="@href != ''">
	      <a href="{@href}" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:text>---</xsl:text>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
	    </xsl:when>
	    <xsl:when test="@targetdoc != ''">
	      <a href="../../Public/{@targetdoc}/index.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@targetdoc"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
	    </xsl:when>
	    <xsl:when test="@target != ''">
	      <a href="../../Public/{@target}/index.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@target"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
	    </xsl:when>
	    <xsl:when test="@assignment != ''">
	      <a href="../../Protected/Assts/{@assignment}.mmd.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@target"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
	    </xsl:when>
	    <xsl:when test="@title != ''">
	      <xsl:value-of select="@title"/>
	    </xsl:when>
	  </xsl:choose>
          <xsl:choose>
            <xsl:when test="@enddate != ''">
              <xsl:text> </xsl:text>
              <span class="date">
                <xsl:text>(</xsl:text>
                <xsl:call-template name="formatDate">
                  <xsl:with-param name="date" select="@date"/>
                </xsl:call-template>
                <xsl:text> - </xsl:text>
                <xsl:call-template name="formatDate">
                  <xsl:with-param name="date" select="@enddate"/>
                </xsl:call-template>
                <xsl:text>)</xsl:text>
              </span>
            </xsl:when>
            
            <xsl:when test="@date != ''">
              <xsl:text> </xsl:text>
              <span class="date">
                <xsl:text>(</xsl:text>
                <xsl:call-template name="formatDate">
                  <xsl:with-param name="date" select="@date"/>
                </xsl:call-template>
                
                <xsl:if test="@time != ''">
                  <xsl:text>, </xsl:text>
                  <xsl:value-of select="@time"/>
                </xsl:if>
                <xsl:text>)</xsl:text>
              </span>
            </xsl:when>
          </xsl:choose>
	</span>
	</td>
    </tr>
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="item">
    <xsl:variable name="className"
      select="concat('item', 1+count(ancestor::topic)+count(ancestor::subject))"/>
    <xsl:variable name="item" select="."/>
    <xsl:choose>
      <xsl:when test="contains($columnKinds, concat(' ', @kind, ' '))">
        <xsl:if test="not(preceding-sibling::* 
                      and local-name(preceding-sibling::*[1]) = 'item'
                      and contains($columnKinds, 
                          concat(' ', preceding-sibling::*[1]/@kind, ' ')))">

          <tr class="{$className}">
            <xsl:copy-of select="@id"/>
	    <td><span class="topicSpacing"> </span></td>
            <xsl:for-each select="/outline/presentation/column">
              <xsl:if test="@kinds != 'topics' and @kinds != 'subject'">
                <td class="{$className}">
                  <xsl:call-template name="gatherItems">
                    <xsl:with-param name="node" select="$item"/>
                    <xsl:with-param name="kinds"
                      select="concat(' ', @kinds, ' ')"/>
                  </xsl:call-template>
                </td>
              </xsl:if>
            </xsl:for-each>
          </tr>
        </xsl:if>
      </xsl:when>

      <xsl:otherwise>
        <tr class="{$className}">
          <td class="{$className}" 
            colspan="{count(/outline/presentation/column)}">
            <xsl:call-template name="itemDetail"/>
          </td>
        </tr>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="gatherItems">
    <xsl:param name="node"/>
    <xsl:param name="kinds"/>
    
    <xsl:variable name="paddedKind" select="concat(' ', $node/@kind, ' ')"/>
    <xsl:if test="(local-name($node) = 'item')
                  and contains($columnKinds, $paddedKind)">
      <xsl:if test="contains($kinds, $paddedKind)">
        <xsl:for-each select="$node">
          <xsl:call-template name="itemDetail"/>
          <!-- br/ -->
        </xsl:for-each>
      </xsl:if>
      <xsl:variable name="afterNode" select="$node/following-sibling::*"/>
      <xsl:if test="count($afterNode) &gt; 0">
        <xsl:call-template name="gatherItems">
          <xsl:with-param name="node" select="$afterNode[1]"/>
          <xsl:with-param name="kinds" select="$kinds"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="itemDetail">
    <p>
      <xsl:copy-of select="@id"/>

      <xsl:if test="@kind != ''">
	<img src="{@kind}.gif">
	  <xsl:attribute name="alt">
	    <xsl:value-of select="concat(@kind,':')"/>
	  </xsl:attribute>
	</img>
        <xsl:text> </xsl:text>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="@href != ''">
          <a href="{@href}" target="_blank">
            <xsl:apply-templates select="*|text()" mode="copying"/>
          </a>
        </xsl:when>
        <xsl:when test="@bblink != ''">
	  <xsl:variable name="bbhref">
	    <xsl:call-template name="bblinkConvert">
	      <xsl:with-param name="bbcourseURL"
			      select="$bbURL"/>
	      <xsl:with-param name="bblinkURL"
			      select="@bblink"/>
	    </xsl:call-template>
	  </xsl:variable>
	  <a href="{$bbhref}" target="_blank">
	    <xsl:apply-templates select="*|text()" mode="copying"/>
	  </a>
        </xsl:when>
	<xsl:when test="@target != ''">
	      <a href="../../Public/{@target}/index.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@target"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
        </xsl:when>
	<xsl:when test="@targetdoc != ''">
	      <a href="../../Public/{@targetdoc}/index.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@targetdoc"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
        </xsl:when>
	<xsl:when test="@assignment != ''">
	      <a href="../../Protected/Assts/{@assignment}.mmd.html" target="_blank">
		<xsl:choose>
		  <xsl:when test="@title != ''">
		    <xsl:value-of select="@title"/>
		  </xsl:when>
		  <xsl:when test="normalize-space(./text()) != ''">
		    <xsl:copy-of select="*|text()"/>
		  </xsl:when>
		  <xsl:otherwise>
		    <xsl:variable name="doc" select="@targetdoc"/>
		    <xsl:variable name="titleNode" 
				  select="$titleTable/title[@doc=$doc]"/>
		    <xsl:choose>
		      <xsl:when test="$titleNode">
			<xsl:copy-of select="$titleNode/* | $titleNode/text()"/>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:text>???</xsl:text>
		      </xsl:otherwise>
		    </xsl:choose>
		  </xsl:otherwise>
		</xsl:choose>
	      </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*|text()"  mode="copying"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="@enddate != ''">
          <xsl:text> </xsl:text>
          <span class="date">
            <xsl:text>(</xsl:text>
            <xsl:call-template name="formatDate">
              <xsl:with-param name="date" select="@date"/>
            </xsl:call-template>
            <xsl:text> - </xsl:text>
            <xsl:call-template name="formatDate">
              <xsl:with-param name="date" select="@enddate"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </span>
        </xsl:when>
        
        <xsl:when test="@date != ''">
          <xsl:text> </xsl:text>
          <span class="date">
            <xsl:text>(</xsl:text>
            <xsl:call-template name="formatDate">
              <xsl:with-param name="date" select="@date"/>
            </xsl:call-template>
            
            <xsl:if test="@time != ''">
              <xsl:text>, </xsl:text>
	      <xsl:value-of select="@time"/>
            </xsl:if>
            <xsl:text>)</xsl:text>
          </span>
        </xsl:when>
      </xsl:choose>
      <xsl:if test="@due != ''">
          <span class="date">
            <xsl:text>(Due: </xsl:text>
            <xsl:call-template name="formatDate">
              <xsl:with-param name="date" select="@due"/>
            </xsl:call-template>
            
            <xsl:if test="@time != ''">
              <xsl:text>, </xsl:text>
	      <xsl:value-of select="@time"/>
            </xsl:if>
            <xsl:text>)</xsl:text>
          </span>
        </xsl:if>
    </p>
  </xsl:template>



  <xsl:template match="outline" mode="itemNumber">
  </xsl:template>

  <xsl:template match="topic|item|subject" mode="itemNumber">
    <xsl:apply-templates select=".." mode="itemNumber"/>
    <xsl:if test="local-name(..) = 'topic'">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:if test="local-name(..) = 'subject'">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="count(preceding-sibling::item | preceding-sibling::topic| preceding-sibling::subject) + 1"/>
  </xsl:template>

  <xsl:template match="img" mode="copying">
    <img src="{@src}">
      <xsl:if test="@alt">
	<xsl:attribute name="alt">
	  <xsl:value-of select="@alt"/>
	</xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template match="text()" mode="copying">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="*" mode="copying">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()" mode="copying"/>
    </xsl:element> 
  </xsl:template>

  <xsl:template name="formatDate">
    <xsl:param name="date" select="'2005-01-01'"/>

    <xsl:variable name="year" select="substring-before($date, '-')"/>
    <xsl:variable name="afterYear" select="substring-after($date, '-')"/>

    <xsl:variable name="month" select="substring-before($afterYear, '-')"/>
    <xsl:variable name="afterMonth" select="substring-after($afterYear, '-')"/>

    <xsl:variable name="day">
      <xsl:choose>
        <xsl:when test="contains($afterMonth, 'T')">
          <xsl:value-of select="substring-before($afterMonth, 'T')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$afterMonth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$month"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="$day"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="$year"/>

  </xsl:template>


</xsl:stylesheet>
