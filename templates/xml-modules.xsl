<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
>

  <xsl:import href="../styles/footer.xsl"/>

  <!-- Converts the course outline to a tabular view. The columns
       of the table are described in the presentation section of the
       outline file.
  -->

  <xsl:param name="doc" select="'outline'"/>
  <xsl:param name="format" select="'modules'"/>
  <xsl:param name="pwdURL" select="'./'"/>
  <xsl:param name="courseName" select="'CS'"/>
  <xsl:param name="stylesURL" select="'../../styles'"/>
  <xsl:param name="graphicsURL" select="'../../graphics'"/>
  <xsl:param name="homeURL" select="''"/>
  <xsl:param name="forum" select="''"/>
  <xsl:param name="forumsURL" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="stylesDir" select="'../../styles'"/>


  <xsl:output method="xml" indent="yes"  encoding="utf-8"/>


  <xsl:variable name="columnKinds">
    <xsl:text> </xsl:text>
    <xsl:for-each select="/outline/presentation/column">
      <xsl:value-of select="@kinds"/>
    <xsl:text> </xsl:text>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name="titleTable" select="document(concat($pwdURL, '/titleTable.xml'))/table"/>
  <xsl:variable name="kindTable" select="/outline/presentation"/>


  <xsl:template match="/">
    <html>
      <head>
	<title>@courseName@ Modules</title>
	<link rel="stylesheet" type="text/css" media="screen, projection, print"
	      href="{$stylesURL}/modules.css" />
	<script type="text/javascript"
		src="{$stylesURL}/modules.js">
	<xsl:text> </xsl:text>
      </script>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
	    href="local.css" />
      </head>
      <body>
	<div class="titleBlock">
	  <div class="courseName">@courseName@, @semester@</div>
	  <h1>@courseName@ Modules</h1>
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

    <form action="">
      <div class="showHideControls">
	<input type="button" value="expand all"
	       onclick="expandAll()"/>
	<input type="button" value="collapse all"
	       onclick="collapseAll()"/>
      </div>
    </form>

    <xsl:apply-templates select="topic | subject | item"/>

    <xsl:if test="postscript">
      <xsl:apply-templates select="postscript/*|postscript/text()" mode="copying"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="topic" mode="topicSpacing">
    <span style="width: 3em;"/>
  </xsl:template>

  <xsl:template match="topic">
    <xsl:variable name="className"
      select="concat('topic', 1+count(ancestor::topic))"/>

    <xsl:choose>
      <xsl:when test="topic">
	<div class="{$className}">
	  <xsl:apply-templates select="." mode="itemNumber"/>
	  <xsl:text> </xsl:text>
	  
	  <xsl:choose>
	    <xsl:when test="@href | @targetdoc | @bblink">
	      <a>
		<xsl:call-template name="generateLinkAttributes"/>
		<xsl:call-template name="generateTitle"/>
	      </a>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:call-template name="generateTitle"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</div>
	<xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="idValue"
		      select="concat('_topic_', generate-id())"/>
	<div class="{$className}">
	  <xsl:apply-templates select="." mode="itemNumber"/>
	  <xsl:text> </xsl:text>
	  <input id="but{$idValue}" type="button" value="+"
		 class="expandButton"
		 onclick="toggleDisplay('{$idValue}')"
		 />
	  <xsl:text> </xsl:text>
	  <xsl:choose>
	    <xsl:when test="@href | @targetdoc | @bblink">
	      <a>
		<xsl:call-template name="generateLinkAttributes"/>
		<xsl:call-template name="generateTitle"/>
	      </a>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:call-template name="generateTitle"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</div>
	<div class="module">
	  <xsl:attribute name="id">
	    <xsl:value-of select="$idValue"/>
	  </xsl:attribute>
	  <xsl:choose>
	    <xsl:when test="description">
	      <table>
		<tr>
		  <td class="moduleDescription">
		    <div class="moduleDescription">
		      <xsl:apply-templates select="description"/>
		    </div>
		  </td>
		  <td class="moduleActivitiesRt">
		    <div class="moduleActivitiesRt">
		      <b>Activities</b>
		      <ol>
			<xsl:apply-templates select="item | subject"/>
		      </ol>
		    </div>
		  </td>
		</tr>
	      </table>
	    </xsl:when>
	    <xsl:otherwise>
	      <div class="moduleActivities">
		<b>Activities</b>
		<ol>
		  <xsl:apply-templates select="item | subject"/>
		</ol>
	      </div>
	    </xsl:otherwise>
	  </xsl:choose>
	</div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="description">
    <xsl:choose>
      <xsl:when test="ditem|overview|objectives|relevance">
	<xsl:apply-templates select="*"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy-of select="*|text()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="ditem">
    <p><b><xsl:value-of select="@title"/></b></p>
    <xsl:choose>
      <xsl:when test="p|ol|ul">
	<xsl:copy-of select="* | text()"/>
      </xsl:when>
      <xsl:otherwise>
	<p>
	  <xsl:copy-of select="* | text()"/>
	</p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="overview">
    <p><b>Overview</b></p>
    <xsl:choose>
      <xsl:when test="p|ol|ul">
	<xsl:copy-of select="* | text()"/>
      </xsl:when>
      <xsl:otherwise>
	<p>
	  <xsl:copy-of select="* | text()"/>
	</p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="relevance">
    <p><b>Relevance</b></p>
    <xsl:choose>
      <xsl:when test="p|ol|ul">
	<xsl:copy-of select="* | text()"/>
      </xsl:when>
      <xsl:otherwise>
	<p>
	  <xsl:copy-of select="* | text()"/>
	</p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="objectives">
    <p><b>Objectives</b></p>
    <xsl:choose>
      <xsl:when test="@text">
	<p><xsl:value-of select="@text"/></p>
      </xsl:when>
      <xsl:otherwise>
	<p>At the end of this module, students will be able to:</p>
      </xsl:otherwise>
    </xsl:choose>
    <ol>
      <xsl:apply-templates select="obj"/>
    </ol>
  </xsl:template>

  <xsl:template match="obj">
    <li>
      <xsl:copy-of select="*|text()"/>
    </li>
  </xsl:template>


  <xsl:template match="subject">
    <span class="subject">
      <xsl:choose>
        <xsl:when test="@href | @targetdoc | @bblink">
          <a>
            <xsl:call-template name="generateLinkAttributes"/>
            <xsl:call-template name="generateTitle"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="generateTitle"/>
        </xsl:otherwise>
      </xsl:choose>
    </span>
    <ol>
      <xsl:apply-templates select="item | subject"/>
    </ol>
  </xsl:template>


  <xsl:template match="outline" mode="itemNumber">
  </xsl:template>

  <xsl:template match="topic|item" mode="itemNumber">
    <xsl:apply-templates select=".." mode="itemNumber"/>
    <xsl:if test="local-name(..) = 'topic'">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="count(preceding-sibling::item | preceding-sibling::topic) + 1"/>
  </xsl:template>

  <xsl:template name="generateTitle">
    <xsl:choose>
      <xsl:when test="@title">
	<xsl:value-of select="normalize-space(@title)"/>
      </xsl:when>
      <xsl:when test="@targetdoc">
	<xsl:variable name="doc"
		      select="@targetdoc"/>
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
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy-of select="* | text()"/>
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
  </xsl:template>

  <xsl:template name="generateLinkAttributes">
    <xsl:choose>
      <xsl:when test="@href != ''">
	<xsl:attribute name="href">
	  <xsl:value-of select="@href"/>
	</xsl:attribute>
	<xsl:attribute name="target">_blank</xsl:attribute>
      </xsl:when>
      <xsl:when test="@targetdoc != ''">
	<xsl:attribute name="href">
	  <xsl:value-of select="concat('../../Public/', @targetdoc, '/index.html')"/>
	</xsl:attribute>
	<xsl:attribute name="target">_blank</xsl:attribute>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="kindPrefix">
    <xsl:choose>
      <xsl:when test="@prefix">
	<xsl:value-of select="@prefix"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="kindNm" select="@kind"/>
	<xsl:variable name="kindDescription" select="$kindTable/kind[@name=$kindNm]"/>
	<xsl:if test="$kindDescription">
	  <xsl:value-of select="$kindDescription/@prefix"/>
	</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="item">
    <xsl:variable name="className"
      select="concat('item', 1+count(ancestor::topic))"/>
    <xsl:variable name="item" select="."/>
    <li>
      <xsl:call-template name="itemDetail"/>
    </li>
  </xsl:template>

  <xsl:template name="itemDetail">
    <p>
      <xsl:copy-of select="@id"/>

      <xsl:if test="@construct != ''">
	<img src="construct.gif">
	  <xsl:attribute name="alt">
	    <xsl:text>under construction</xsl:text>
	  </xsl:attribute>
	</img>
	<xsl:text> </xsl:text>
      </xsl:if>
      <xsl:if test="@needsReview != ''">
	<img src="review.gif">
	  <xsl:attribute name="alt">
	    <xsl:text>needs review</xsl:text>
	  </xsl:attribute>
	</img>
	<xsl:text> </xsl:text>
      </xsl:if>
      <xsl:if test="@kind != ''">
	<img src="{@kind}.gif">
	  <xsl:attribute name="alt">
	    <xsl:value-of select="concat(@kind,':')"/>
	  </xsl:attribute>
	</img>
	<xsl:text> </xsl:text>
	<xsl:call-template name="kindPrefix"/>
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
			      select="/outline/@bbcourse"/>
	      <xsl:with-param name="bblinkURL"
			      select="@bblink"/>
	    </xsl:call-template>
	  </xsl:variable>
	  <a href="{$bbhref}" target="_blank">
	    <xsl:apply-templates select="*|text()" mode="copying"/>
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

  
  <xsl:template name="bblinkConvert">
    <xsl:param name="bbcourseURL"/>
    <xsl:param name="bblinkURL"/>

    <xsl:variable name="bburlstart"
		  select="concat(substring-before($bbcourseURL,'url='),'url=')"/>
    <xsl:variable name="bburl0"
		  select="concat('/webapps', substring-after($bblinkURL,'/webapps'))"/>
    <xsl:variable name="bburl1">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl0"/>
	<xsl:with-param name="replace" select="'/'"/>
	<xsl:with-param name="by" select="'%2f'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl2">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl1"/>
	<xsl:with-param name="replace" select="'='"/>
	<xsl:with-param name="by" select="'%3d'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl3">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl2"/>
	<xsl:with-param name="replace" select="'&amp;'"/>
	<xsl:with-param name="by" select="'%26'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="bburl4">
      <xsl:call-template name="string-replace-all">
	<xsl:with-param name="text" select="$bburl3"/>
	<xsl:with-param name="replace" select="'?'"/>
	<xsl:with-param name="by" select="'%3f'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:value-of select="concat($bburlstart,$bburl4)"/>
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


  <xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
	<xsl:value-of select="substring-before($text,$replace)" />
	<xsl:value-of select="$by" />
	<xsl:call-template name="string-replace-all">
	  <xsl:with-param name="text"
			  select="substring-after($text,$replace)" />
	  <xsl:with-param name="replace" select="$replace" />
	  <xsl:with-param name="by" select="$by" />
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
