<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <!-- 
       Chops an HTML page into pages
       divided at each <h1>, <h2>, <h3>, and top-level <hr/> marker
  -->

  <xsl:param name="format" select="'pages'"/>

  <xsl:output method="xml" encoding="iso-8859-1"/>


  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>


  <xsl:template match="html">
  	  <xsl:message>copying html</xsl:message>
  	  <xsl:variable name="recursivePages">
          <xsl:apply-templates select="body" mode="paginate"/>
  	  </xsl:variable>
      <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:copy-of select="head"/>
	      <xsl:apply-templates select="$recursivePages" mode="flatten"/>
      </xsl:copy>
  </xsl:template>


  <xsl:template match="body" mode="paginate">
  	  <xsl:message>paginating a body</xsl:message>
      <xsl:copy>
          <xsl:copy-of select="@*"/>
          <page>
              <xsl:if test="*">
	              <xsl:apply-templates select="*[1]" mode="paginate"/>
	          </xsl:if>
          </page>
      </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3" mode="paginate">
  	  <xsl:message>paginating a header</xsl:message>
  	  <page>
          <xsl:copy-of select='.'/>
          <xsl:if test="following-sibling::*">
	          <xsl:apply-templates select="following-sibling::*[1]" mode="paginate"/>
	      </xsl:if>
      </page>
  </xsl:template>

  <xsl:template match="hr" mode="paginate">
      <xsl:copy-of select='.'/>
  	  <page>
          <xsl:if test="following-sibling::*">
	          <xsl:apply-templates select="following-sibling::*[1]" mode="paginate"/>
	      </xsl:if>
      </page>
  </xsl:template>
  
  <xsl:template match="text()" mode="paginate">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="*" mode="paginate">
    <xsl:copy-of select='.'/>
    <xsl:if test="following-sibling::*">
        <xsl:apply-templates select="following-sibling::*[1]" mode="paginate"/>
    </xsl:if>
  </xsl:template>

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

</xsl:stylesheet>
