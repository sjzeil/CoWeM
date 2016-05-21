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
  
  <xsl:param name="format" select="'pages'"/>
  <xsl:param name="numberDepth" select="'3'"/>
  
  <xsl:output method="xml" encoding="UTF-8"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>


<!-- sectionNumbering -->


  <xsl:template match="html" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectionNumbering"/>
    </copy>
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
      <xsl:if test="../body  and  $numberDepth &gt; 0">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="1 + count(preceding-siblings::h1)"/>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '. ')"/>
	</xsl:if>
      </xsl:attribute>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h2" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="../body  and  $numberDepth &gt; 1">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="1 + count(preceding-siblings::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-siblings::h2">
	      <xsl:value-of select="1 + count(preceding-siblings::h2) 
				    - count(preceding-siblings::h1[1]/preceding-siblings::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-siblings::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, '. ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h3" mode="sectionNumbering">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="../body  and  $numberDepth &gt; 2">
	<xsl:variable name="sectionNumber">
	  <xsl:value-of select="1 + count(preceding-siblings::h1)"/>
	</xsl:variable>
	<xsl:variable name="subsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-siblings::h2">
	      <xsl:value-of select="1 + count(preceding-siblings::h2) 
		- count(preceding-siblings::h1[1]/preceding-siblings::h2)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-siblings::h2)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:variable name="subsubsectionNumber">
	  <xsl:choose>
	    <xsl:when test="preceding-siblings::h2">
	      <xsl:value-of select="1 + count(preceding-siblings::h3) 
		- count(preceding-siblings::h2[1]/preceding-siblings::h3)"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="1 + count(preceding-siblings::h3)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>
	<xsl:attribute name="sectionNumber">
	  <xsl:value-of select="concat($sectionNumber, '.', 
				$subsectionNumber, '.',
				$subsubsectionNumber, '. ')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" mode="sectionNumbering"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="node()" mode="sectionNumber">
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
      </copy>
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


  <xsl:template match="page" mode="flatten">
    <xsl:choose>
        <xsl:when test="p|div">
            <!-- This page has at least one paragraph. Keep it. -->
            <xsl:copy>
               <xsl:copy-of select='@*'/>
               <xsl:apply-templates select="*[local-name() != 'page']|text()"  mode="flatten"/>
            </xsl:copy>
            <xsl:apply-templates select="page"  mode="flatten"/>
        </xsl:when>
        <xsl:otherwise>
            <!-- This page has no paragraphs of its own. Merge with next page. -->
            <xsl:copy>
               <xsl:copy-of select='@*'/>
               <xsl:apply-templates select="*[local-name() != 'page']|text()"  mode="flatten"/>
               <xsl:apply-templates select="page/*"  mode="flatten"/>
            </xsl:copy>
        </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="text()" mode="flatten">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*" mode="flatten">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"  mode="flatten"/>
    </xsl:copy>
  </xsl:template>



<!-- sectionize: Rearrange pages into a hierarchical section structure -->

  <xsl:template match="html" mode="sectionize">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectionize"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sectionize">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="node()[1]" mode="sectionize1"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="h1" mode="sectionize1">
    <section>
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize2"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h1[1]"
			 mode="sectionize1"/>
  </xsl:template>

  <xsl:template match="node()" mode="sectionize1">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize1"/>
  </xsl:text>

  <xsl:template match="h2" mode="sectionize2">
    <section>
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize3"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h2[1]"
			 mode="sectionize2"/>
  </xsl:template>

  <xsl:template match="h1" mode="sectionize2">
  </xsl:template>

  <xsl:template match="node()" mode="sectionize2">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize2"/>
  </xsl:text>


  <xsl:template match="h3" mode="sectionize3">
    <section>
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize4"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h3[1]"
			 mode="sectionize3"/>
  </xsl:template>

  <xsl:template match="h1|h2" mode="sectionize3">
  </xsl:template>

  <xsl:template match="node()" mode="sectionize3">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize3"/>
  </xsl:text>


  <xsl:template match="h4" mode="sectionize4">
    <section>
      <xsl:copy-of select="."/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize5"/>
    </section>
    <xsl:apply-templates select="./following-sibling::h4[1]"
			 mode="sectionize4"/>
  </xsl:template>

  <xsl:template match="h1|h2|h3" mode="sectionize4">
  </xsl:template>

  <xsl:template match="node()" mode="sectionize4">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize4"/>
  </xsl:text>


  <xsl:template match="h1|h2|h3|h4" mode="sectionize5">
  </xsl:template>

  <xsl:template match="node()" mode="sectionize5">
      <xsl:copy-of select='.'/>
      <xsl:apply-templates select="./following-sibling::node()[1]"
			   mode="sectionize5"/>
  </xsl:text>

