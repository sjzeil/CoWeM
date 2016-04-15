<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version='1.0'>



<xsl:template match="command">
  <xsl:text>\command{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="computeroutput">
  <xsl:text>\computeroutput{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="constant">
  <xsl:text>\constant{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="filename">
  <xsl:text>\filename{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="replaceable">
  <xsl:text>\replaceable{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>



<xsl:template match="function">
  <xsl:text>\function{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="keycap">
  <xsl:text>\keycap{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="parameter">
  <xsl:text>\parameter{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="type">
  <xsl:text>\type{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="uri">
  <xsl:call-template name="inline.monoseq"/>
</xsl:template>

<xsl:template match="userinput">
  <xsl:text>\userinput{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>


<xsl:template match="varname">
  <xsl:text>\varname{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="firstterm">
  <xsl:variable name="termtext">
    <xsl:text>\firstterm{</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>}</xsl:text>
  </xsl:variable>

  <xsl:choose>
  <xsl:when test="@linkend">
    <xsl:call-template name="hyperlink.markup">
      <xsl:with-param name="linkend" select="@linkend"/>
      <xsl:with-param name="text" select="$termtext"/>
    </xsl:call-template>
  </xsl:when>
  <xsl:when test="$glossterm.auto.link != 0">
    <xsl:variable name="term">
      <xsl:choose>
        <xsl:when test="@baseform">
          <xsl:value-of select="normalize-space(@baseform)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="normalize-space(.)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="glossentry"
       select="(//glossentry[normalize-space(glossterm)=$term or
                             normalize-space(glossterm/@baseform)=$term][@id])[1]"/>
    <xsl:choose>
    <xsl:when test="$glossentry">
      <xsl:call-template name="hyperlink.markup">
        <xsl:with-param name="linkend" select="$glossentry/@id"/>
        <xsl:with-param name="text" select="$termtext"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
        <xsl:text>Error: no ID glossentry for glossterm: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>.</xsl:text>
      </xsl:message>
      <xsl:value-of select="$termtext"/>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="$termtext"/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="glossterm">
  <xsl:variable name="termtext">
    <xsl:text>\glossterm{</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>}</xsl:text>
  </xsl:variable>

  <xsl:choose>
  <xsl:when test="@linkend">
    <xsl:call-template name="hyperlink.markup">
      <xsl:with-param name="linkend" select="@linkend"/>
      <xsl:with-param name="text" select="$termtext"/>
    </xsl:call-template>
  </xsl:when>
  <xsl:when test="$glossterm.auto.link != 0">
    <xsl:variable name="term">
      <xsl:choose>
        <xsl:when test="@baseform">
          <xsl:value-of select="normalize-space(@baseform)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="normalize-space(.)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="glossentry"
       select="(//glossentry[normalize-space(glossterm)=$term or
                             normalize-space(glossterm/@baseform)=$term][@id])[1]"/>
    <xsl:choose>
    <xsl:when test="$glossentry">
      <xsl:call-template name="hyperlink.markup">
        <xsl:with-param name="linkend" select="$glossentry/@id"/>
        <xsl:with-param name="text" select="$termtext"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
        <xsl:text>Error: no ID glossentry for glossterm: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>.</xsl:text>
      </xsl:message>
      <xsl:value-of select="$termtext"/>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="$termtext"/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<xsl:template match="keycombo">
  <xsl:variable name="action" select="@action"/>
  <xsl:variable name="joinchar">
    <xsl:choose>
      <xsl:when test="$action='seq'"><xsl:text> </xsl:text></xsl:when>
      <xsl:when test="$action='simul'">+</xsl:when>
      <xsl:when test="$action='press'">-</xsl:when>
      <xsl:when test="$action='click'">-</xsl:when>
      <xsl:when test="$action='double-click'">-</xsl:when>
      <xsl:when test="$action='other'"></xsl:when>
      <xsl:otherwise>-</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:for-each select="./*">
    <xsl:if test="position()>1"><xsl:value-of select="$joinchar"/></xsl:if>
    <xsl:apply-templates/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="inlinemediaobject">
  <xsl:text>\wrapPicture[40]{</xsl:text>
  <xsl:value-of select="substring-before(imageobject/imagedata/@fileref,'.')"/>
  <xsl:text>}
</xsl:text>
</xsl:template>


<xsl:template match="mediaobject">
  <xsl:text>\centerPic[80]{</xsl:text>
  <xsl:value-of select="substring-before(imageobject/imagedata/@fileref,'.')"/>
  <xsl:text>}
</xsl:text>
</xsl:template>



<xsl:template match="inlinegraphic|graphic">
  <xsl:choose>
  <xsl:when test="$imagedata.file.check='1'">
    <xsl:variable name="filename">
      <xsl:apply-templates select="." mode="filename.get"/>
    </xsl:variable>
    <xsl:text>\centerPic{</xsl:text>
    <xsl:value-of select="$filename"/>
    <xsl:text>}</xsl:text>
  </xsl:when>
  <xsl:otherwise>
    <xsl:call-template name="imagedata"/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="imageobject">
  <xsl:variable name="figcount"
                select="count(ancestor::figure/mediaobject[imageobject])"/>
  <xsl:if test="$figcount &gt; 1">
    <!-- space before subfigure to prevent from strange behaviour with other
         subfigures -->
    <xsl:text> \subfigure[</xsl:text>
    <xsl:apply-templates select="../caption" mode="subfigure"/>
    <xsl:text>][</xsl:text>
    <xsl:apply-templates select="../caption"/>
    <xsl:text>]{</xsl:text>
  </xsl:if>
  <xsl:if test="$imagedata.boxed = '1'">
    <xsl:text>\fbox{</xsl:text>
  </xsl:if>
  <xsl:apply-templates select="imagedata"/>
  <xsl:if test="$imagedata.boxed = '1'">
    <xsl:text>}</xsl:text>
  </xsl:if>
  <xsl:if test="$figcount &gt; 1">
    <xsl:text>}</xsl:text>
  </xsl:if>
</xsl:template>


<xsl:template match="informaltable">
  <xsl:variable name="numRows" select="count(tr[1]/*)"/>
  <xsl:text>\begin{tabular}{|*{</xsl:text>
  <xsl:value-of select="$numRows"/>
  <xsl:text>}{l|}}
\hline
</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>
\end{tabular}</xsl:text>
</xsl:template>

<xsl:template match="tr">
  <xsl:apply-templates select="*"/>
  <xsl:text>\\ \hline
</xsl:text>
</xsl:template>

<xsl:template match="td">
  <xsl:if test="position() &gt; 1">
    <xsl:text> &amp; </xsl:text>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="th">
  <xsl:if test="position() &gt; 1">
    <xsl:text> &amp; </xsl:text>
  </xsl:if>
  <xsl:text>\multicolumn{1}{|c|}{\textbf{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}}</xsl:text>
</xsl:template>


<xsl:template match="olink">
  <xsl:choose>
    <xsl:when test="@targetptr != ''">
      <xsl:text>\olink{</xsl:text>
      <xsl:value-of select="@targetdoc"/>
      <xsl:text>}{</xsl:text>
      <xsl:value-of select="@targetptr"/>
      <xsl:text>}{</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>}</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>\doclink{</xsl:text>
      <xsl:value-of select="@targetdoc"/>
      <xsl:text>}{</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>}</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="sidebar">
  <xsl:text>\begin{ublock}
</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>
\end{ublock}
</xsl:text>
</xsl:template>


<xsl:template match="example">
  <xsl:choose>
    <xsl:when test="title">
      <xsl:text>\begin{titledExample}[Example: </xsl:text>
      <xsl:apply-templates select="title/* | title/text()"/>
      <xsl:text>]
</xsl:text>
      <xsl:apply-templates select="*[position() &gt; 1]"/>
      <xsl:text>
\end{titledExample}
</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>\begin{titledExample}[Example]
</xsl:text>
      <xsl:apply-templates select="*"/>
      <xsl:text>
\end{titledExample}
</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<xsl:template match="programlisting">
  <xsl:choose>
    <xsl:when test="./textobject/textdata">
      <xsl:text>&#10;\loadlisting{</xsl:text>
      <xsl:value-of select="textobject/textdata/@fileref"/>
      <xsl:text>}&#10;&#10;</xsl:text>
    </xsl:when>
    <xsl:when test="emphasis">
      <xsl:text>&#10;\begin{verbatim}&#10;</xsl:text>
      <xsl:apply-templates select="*|text()" mode="inlisting"/>
      <xsl:text>\end{verbatim}&#10;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&#10;\begin{cpp}&#10;</xsl:text>
      <xsl:call-template name="replace-string">
	<xsl:with-param name="text" select="*|text()"/>
	<xsl:with-param name="replace" select="'&#x22ee;'" />
	<xsl:with-param name="with" select="'&lt;:\smvdots:&gt;'"/>
      </xsl:call-template>
      <xsl:text>\end{cpp}&#10;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="emphasis" mode="inlisting">
  <xsl:text>/*+</xsl:text>
  <xsl:value-of select="substring-after(@role, 'highlight')"/>
  <xsl:text>*/</xsl:text>
  <xsl:apply-templates select="*|text()" mode="inlisting"/>
  <xsl:text>/*-</xsl:text>
  <xsl:value-of select="substring-after(@role, 'highlight')"/>
  <xsl:text>*/</xsl:text>
</xsl:template>

<xsl:template match="text()" mode="inlisting">
  <xsl:call-template name="replace-string">
    <xsl:with-param name="text" select="."/>
    <xsl:with-param name="replace" select="'&#x22ee;'" />
    <xsl:with-param name="with" select="'/*...*/'"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="replace-string">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="with"/>
    <xsl:choose>
      <xsl:when test="contains($text,$replace)">
        <xsl:value-of select="substring-before($text,$replace)"/>
        <xsl:value-of select="$with"/>
        <xsl:call-template name="replace-string">
          <xsl:with-param name="text" select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
