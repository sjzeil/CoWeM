<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
  <!-- 
     Management of document hierarchy.

     Provides 3 modes for pre-rprocessing of documents prior to final
     formatting.

     sectionNumbering: Adds @sectionNumber attributes to top-level <h?> 
       elements down to $numberDepth. (Must be applied before either of the
       following.)

     paginate: Chops an HTML page into a recursive page structure, splitting
       at each top-level h1,h2,h3, & hr element, then invokes flatten mode.
       
     section: Arranges the document into a hierarchy of sections based on
        h1..h5 headers.
  -->

<!--   
  <xsl:param name="format" select="'pages'"/>
 -->
  <xsl:param name="numberDepth" select="'3'"/>
  
 <!-- 
  <xsl:output method="xml" encoding="UTF-8"/>
   -->
  
<!--  For testing purposes -->
  <xsl:template match="html[@test = 'pagination']">
      <xsl:apply-templates select="." mode="paginate"/>
  </xsl:template>

  <xsl:template match="html[@test = 'sectioning']">
      <xsl:apply-templates select="." mode="sectioning"/>
  </xsl:template>

  <xsl:template match="html[@test = 'sectionNumbering']">
      <xsl:apply-templates select="." mode="sectionNumbering"/>
  </xsl:template>


<!-- sectionNumbering -->


  <xsl:template match="html" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 0)">
	    <xsl:variable name="sectionNumber">
	      <xsl:value-of select="1 + count(preceding-sibling::h1)"/>
	    </xsl:variable>
	    <xsl:attribute name="sectionNumber">
	      <xsl:value-of select="concat($sectionNumber, ' ')"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h2" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 1)">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="count(preceding-sibling::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="1 + count(preceding-sibling::h2) 
				    - count(preceding-sibling::h1[1]/preceding-sibling::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, ' ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h3" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="(local-name(..) = 'body') and (number($numberDepth) &gt; 2)">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="count(preceding-sibling::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="count(preceding-sibling::h2) 
		- count(preceding-sibling::h1[1]/preceding-sibling::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:variable name="subsubsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-sibling::h2">
	      <xsl:value-of select="1 + count(preceding-sibling::h3) 
		- count(preceding-sibling::h2[1]/preceding-sibling::h3)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-sibling::h3)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, '.',
				$subsubsectionNumber, ' ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="*" mode="sectionNumbering">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="text()" mode="sectionNumbering">
    <xsl:copy-of select='.'/>
  </xsl:template>


<!-- Paginate -->  
  
  <xsl:template match="html" mode="paginate">
    <xsl:variable name="recursivePages">
      <xsl:apply-templates select="body" mode="recurseOnPages"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="$recursivePages" mode="flattenPages"/>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="body" mode="recurseOnPages">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <page>
	    <xsl:if test="*">
	      <xsl:apply-templates select="*[1]" mode="recurseOnPages"/>
	    </xsl:if>
      </page>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3" mode="recurseOnPages">
    <page>
      <xsl:copy>
	    <xsl:copy-of select="@*"/>
	    <xsl:copy-of select="node()"/>
      </xsl:copy>
      <xsl:if test="following-sibling::*">
	    <xsl:apply-templates select="following-sibling::*[1]" mode="recurseOnPages"/>
      </xsl:if>
    </page>
  </xsl:template>

  <xsl:template match="hr" mode="recurseOnPages">
    <xsl:copy-of select='.'/>
    <page>
      <xsl:if test="following-sibling::*">
	<xsl:apply-templates select="following-sibling::*[1]" 
			     mode="recurseOnPages"/>
      </xsl:if>
    </page>
  </xsl:template>
  
  <xsl:template match="text()" mode="recurseOnPages">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="*" mode="recurseOnPages">
    <xsl:copy-of select='.'/>
    <xsl:if test="following-sibling::*">
      <xsl:apply-templates select="following-sibling::*[1]"
			   mode="recurseOnPages"/>
    </xsl:if>
  </xsl:template>



<!-- Flatten pages -->


  <xsl:template match="page" mode="flattenPages">
    <xsl:choose>
        <xsl:when test="p|div">
            <!-- This page has at least one paragraph. Keep it. -->
            <page>
               <xsl:copy-of select='@*'/>
               <xsl:copy-of select="*[local-name() != 'page']|text()"/>
            </page>
            <xsl:apply-templates select="page"  mode="flattenPages"/>
        </xsl:when>
        <xsl:when test="page/p | page/div">
            <!-- This page has no paragraphs of its own, but the next one does. Merge with next page. -->
            <page>
               <xsl:copy-of select='@*'/>
               <xsl:copy-of select="*[local-name() != 'page']|text()"/>
               <xsl:copy-of select="page/*[local-name() != 'page']| page/text()"/>
            </page>
            <xsl:apply-templates select="page/page"  mode="flattenPages"/>
        </xsl:when>
        <xsl:otherwise>
            <!-- This page has no paragraphs of its own. Neither does the next one. Merge with next two pages. -->
            <page>
               <xsl:copy-of select='@*'/>
               <xsl:copy-of select="*[local-name() != 'page']|text()"/>
               <xsl:copy-of select="page/*[local-name() != 'page']| page/text()"/>
               <xsl:copy-of select="page/page/*[local-name() != 'page']| page/page/text()"/>
            </page>
            <xsl:apply-templates select="page/page/page"  mode="flattenPages"/>
        </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="text()" mode="flattenPages">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*" mode="flattenPages">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"  mode="flattenPages"/>
    </xsl:copy>
  </xsl:template>



<!-- sectioning: Rearrange pages into a hierarchical section structure -->

  <xsl:template match="html" mode="sectioning">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectioning"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sectioning">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()[1]" mode="sectioning1"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="h1" mode="sectioning1">
    <section depth="1">
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning2"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h1[1]"
			 mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="node()" mode="sectioning1">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="h2" mode="sectioning2">
    <section depth="2">
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning3"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h2[1]"
			 mode="sectioning2"/>
  </xsl:template>

  <xsl:template match="h1" mode="sectioning2">
  </xsl:template>

  <xsl:template match="node()" mode="sectioning2">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning2"/>
  </xsl:template>


  <xsl:template match="h3" mode="sectioning3">
    <section depth="3">
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning4"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h3[1]"
			 mode="sectioning3"/>
  </xsl:template>

  <xsl:template match="h1|h2" mode="sectioning3">
  </xsl:template>

  <xsl:template match="node()" mode="sectioning3">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning3"/>
  </xsl:template>


  <xsl:template match="h4" mode="sectioning4">
    <section depth="4">
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning5"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h4[1]"
			 mode="sectioning4"/>
  </xsl:template>

  <xsl:template match="h1|h2|h3" mode="sectioning4">
  </xsl:template>

  <xsl:template match="node()" mode="sectioning4">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning4"/>
  </xsl:template>


  <xsl:template match="h1|h2|h3|h4" mode="sectioning5">
  </xsl:template>

  <xsl:template match="node()" mode="sectioning5">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectioning5"/>
  </xsl:template>
  
</xsl:stylesheet>

