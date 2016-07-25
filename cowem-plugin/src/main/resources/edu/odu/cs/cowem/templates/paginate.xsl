<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
  <!-- 

     paginate: Chops an HTML page into a recursive page structure, splitting
       at each top-level h1,h2,h3, & hr element, then invokes flatten mode.
  -->

  
<!--  For testing purposes -->
  <xsl:template match="html[@test = 'pagination']">
      <xsl:apply-templates select="." mode="paginate"/>
  </xsl:template>

  
  <xsl:template match="html" mode="paginate">
    <xsl:variable name="recursivePages">
      <xsl:apply-templates select="body" mode="recurseOnPages"/>
    </xsl:variable>
    <xsl:variable name="flattenedPages">
        <xsl:apply-templates select="$recursivePages" mode="flattenPages"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="$flattenedPages" mode="incrementalPages"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="body" mode="paginate">
    <xsl:variable name="recursivePages">
      <xsl:apply-templates select="." mode="recurseOnPages"/>
    </xsl:variable>
    <xsl:variable name="flattenedPages">
        <xsl:apply-templates select="$recursivePages" mode="flattenPages"/>
    </xsl:variable>
    <xsl:apply-templates select="$flattenedPages" mode="incrementalPages"/>
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


<!-- Incremental pages -->


  <xsl:template match="page" mode="incrementalPages">
      <xsl:choose>
        <xsl:when test=".//*[@class='incremental']">
            <xsl:variable name="incremItem" 
                select=".//*[@class='incremental'][1]/ancestor-or-self::li"/>
            <xsl:variable name="itemCount" 
                select="count(.//li)"/>
            <xsl:variable name="thisPage" 
                select="."/>
            <xsl:variable name="pageCopy0">
                <xsl:copy>
                    <xsl:attribute name="increm">
                        <xsl:text>0</xsl:text>
                    </xsl:attribute>
                    <xsl:copy-of select="@*"/>
                    <xsl:copy-of select="node()"/>
                </xsl:copy>    
            </xsl:variable>
            <xsl:apply-templates select="$pageCopy0" mode="incremental"/>
            <xsl:for-each select="1 to $itemCount">
                <xsl:variable name="pageCopy">
                    <page>
                        <xsl:copy-of select="$thisPage/@*"/>
                        <xsl:attribute name="increm">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                        <xsl:apply-templates select="$thisPage/node()" 
                           mode="stripIDs"/>
                    </page>
                </xsl:variable>
                <xsl:apply-templates select="$pageCopy" 
                    mode="incremental"/>
            </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
            <xsl:copy-of select='.'/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="text()" mode="incrementalPages">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*" mode="incrementalPages">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"  mode="incrementalPages"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()" mode="stripIDs">
    <xsl:copy-of select='.'/>
  </xsl:template>

  <xsl:template match="*" mode="stripIDs">
    <xsl:copy>
      <xsl:copy-of select='@*[local-name() != "id"]'/>
      <xsl:apply-templates select="*|text()"  mode="stripIDs"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="*" mode="incremental">
    <xsl:variable name="increm" select="./ancestor-or-self::page/@increm"/>
    <xsl:variable name="itemCount"
        select="count(./preceding::li) + count(.[local-name() = 'li'])"/>
    <xsl:if test="$itemCount &lt;= $increm">
	    <xsl:copy>
		    <xsl:copy-of select='@*' />
		    <xsl:apply-templates select="*|text()" mode="incremental" />
	    </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="text()" mode="incremental">
    <xsl:copy-of select='.'/>
  </xsl:template>



</xsl:stylesheet>
