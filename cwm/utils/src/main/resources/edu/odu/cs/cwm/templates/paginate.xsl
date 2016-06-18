<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
  <!-- 
     Management of document hierarchy.

     Provides 3 modes for pre-processing of documents prior to final
     formatting.

     sectionNumbering: Adds @sectionNumber attributes to top-level <h?> 
       elements down to $numberDepth. (Must be applied before either of the
       following.)

     paginate: Chops an HTML page into a recursive page structure, splitting
       at each top-level h1,h2,h3, & hr element, then invokes flatten mode.
       
     section: Arranges the document into a hierarchy of sections based on
        h1..h5 headers. Each section has a header, and one or more content
        areas.  Content areas are separated by top-level <hr/> elements.
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

  <xsl:template match="html[@test = 'normalizeHeaders']">
      <xsl:apply-templates select="." mode="normalizeHeaders"/>
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
    <xsl:variable name="normalized">
        <xsl:apply-templates select="." mode="normalizeHeaders"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="$normalized/body/*" mode="sectionNumbering"/>
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
  
  <xsl:template match="body" mode="paginate">
    <xsl:variable name="recursivePages">
      <xsl:apply-templates select="." mode="recurseOnPages"/>
    </xsl:variable>
    <xsl:apply-templates select="$recursivePages" mode="flattenPages"/>
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
    <xsl:variable name="sectioned">
         <xsl:apply-templates select="node()[1]" mode="sectioningContents"/>
         <xsl:apply-templates select="h1[1]" mode="sectioning1"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="$sectioned" mode="splitContent"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3|h4|h5" mode="sectioningContents">
  </xsl:template>
  
  <xsl:template match="*|text()" mode="sectioningContents">
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
  </xsl:template>
  
  <xsl:template match="h1" mode="sectioning1">
    <section depth="1">
      <sectionHeader tag="h1">
         <xsl:copy-of select="@* | node()"/>
      </sectionHeader>
      <sectionContent>
          <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
      </sectionContent>
      <xsl:variable name="nextSplits" select="./following-sibling::h1 | ./following-sibling::h2"/>
      <xsl:if test="local-name($nextSplits[1]) = 'h2'">
          <xsl:apply-templates select="$nextSplits[1]" mode="sectioning1"/>
      </xsl:if>
    </section>
    <xsl:apply-templates select="./following-sibling::h1[1]" mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="h2" mode="sectioning1">
    <section depth="2">
      <sectionHeader tag="h2">
         <xsl:copy-of select="@* | node()"/>
      </sectionHeader>
      <sectionContent>
          <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
      </sectionContent>
      <xsl:variable name="nextSplits" select="./following-sibling::h2 | ./following-sibling::h3"/>
      <xsl:if test="local-name($nextSplits[1]) = 'h3'">
          <xsl:apply-templates select="$nextSplits[1]" mode="sectioning1"/>
      </xsl:if>
    </section>
    <xsl:apply-templates select="./following-sibling::h2[1]" mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="h3" mode="sectioning1">
    <section depth="3">
      <sectionHeader tag="h3">
         <xsl:copy-of select="@* | node()"/>
      </sectionHeader>
      <sectionContent>
          <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
      </sectionContent>
      <xsl:variable name="nextSplits" select="./following-sibling::h3 | ./following-sibling::h4"/>
      <xsl:if test="local-name($nextSplits[1]) = 'h4'">
          <xsl:apply-templates select="$nextSplits[1]" mode="sectioning1"/>
      </xsl:if>
    </section>
    <xsl:apply-templates select="./following-sibling::h3[1]" mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="h4" mode="sectioning1">
    <section depth="4">
      <sectionHeader tag="h4">
         <xsl:copy-of select="@* | node()"/>
      </sectionHeader>
      <sectionContent>
          <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
      </sectionContent>
      <xsl:variable name="nextSplits" select="./following-sibling::h4 | ./following-sibling::h5"/>
      <xsl:if test="local-name($nextSplits[1]) = 'h5'">
          <xsl:apply-templates select="$nextSplits[1]" mode="sectioning1"/>
      </xsl:if>
    </section>
    <xsl:apply-templates select="./following-sibling::h4[1]" mode="sectioning1"/>
  </xsl:template>

  <xsl:template match="h5" mode="sectioning1">
    <section depth="5">
      <sectionHeader tag="h5">
         <xsl:copy-of select="@* | node()"/>
      </sectionHeader>
      <sectionContent>
          <xsl:apply-templates select="./following-sibling::node()[1]" mode="sectioningContents"/>
      </sectionContent>
    </section>
    <xsl:apply-templates select="./following-sibling::h5[1]" mode="sectioning1"/>
  </xsl:template>

  
  
  <xsl:template match="section" mode="splitContent">
      <xsl:apply-templates select="." mode="splitContent2"/>
  </xsl:template>

  <xsl:template match="section" mode="splitContent2">
      <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:copy-of select="sectionHeader"/>
          <xsl:apply-templates select="sectionContent" mode="splitContent2"/>
          <xsl:apply-templates select="section" mode="splitContent2"/>
      </xsl:copy>
  </xsl:template>
  
  <xsl:template match="sectionContent" mode="splitContent2">
      <xsl:choose>
          <xsl:when test="hr">
              <xsl:variable name="hr1" select="hr[1]"/>
              <sectionDescription>
                  <xsl:copy-of select="$hr1/preceding-sibling::*"/>
              </sectionDescription>
              <sectionContent>
                  <xsl:copy-of select="$hr1/following-sibling::*"/>
              </sectionContent>
          </xsl:when>
          <xsl:otherwise>
              <xsl:copy-of select='.'/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
  <xsl:template match="*|text()" mode="splitContent">
      <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="*|text()" mode="splitContent2">
      <xsl:copy-of select="."/>
  </xsl:template>
  

<!--  Normalize Headers -->

<!--  PegDown generates headers h1, h2, ... with all text inside an <a> element
      containing both an href and name attribute. This complicates a lot of
      of later processing, so we spend a pass to rewrite this as simply an
      id on the header itself.  -->

  <xsl:template match="html" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3|h4|h5" mode="normalizeHeaders">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:choose>
         <xsl:when test="a[@href != '' and @name != ''] 
              and (normalize-space(.) = normalize-space(a[1]))">
             <xsl:attribute name="id">
                <xsl:value-of select="a/@name"/>
             </xsl:attribute>
             <xsl:copy-of select="a/node()"/>
         </xsl:when>
         <xsl:otherwise>
             <xsl:copy-of select="node()"/>
         </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*" mode="normalizeHeaders">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="node()" mode="normalizeHeaders"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()" mode="normalizeHeaders">
    <xsl:copy-of select='.'/>
  </xsl:template>


</xsl:stylesheet>
