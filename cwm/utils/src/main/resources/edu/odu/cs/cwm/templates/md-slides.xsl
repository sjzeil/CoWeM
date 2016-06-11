<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  <xsl:param name="format" select="'slides'"/>
  
  
  
  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <xsl:variable name="numbered">
      <xsl:apply-templates select="body" mode="sectionNumbering"/>    
    </xsl:variable>
    <xsl:variable name="paged">
      <xsl:apply-templates select="$numbered" mode="paginate"/>    
    </xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="head"/>  
      <xsl:apply-templates select="$paged"/>    
    </html>
  </xsl:template>



  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:call-template name="insertHeader"/>
      
      <div class="page" id="slide-0-0">
          <div class="titleblock">
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

          <xsl:if test="$TOC != ''">
            <div class="toc">
              <xsl:text>Contents:</xsl:text>
                <xsl:apply-templates select="page/h1 | page/h2" mode="toc"/>
            </div>
          </xsl:if>
      </div>  <!--  class="page" -->

      <xsl:apply-templates select="node()"/>
      
      <xsl:call-template name="insertFooter"/>
      <script>
          <xsl:apply-templates select="*" mode="collectIDs"/>
      </script>
      
    </xsl:copy>
  </xsl:template>


  <xsl:template match="page">
    <xsl:variable name="pageNumber" select="1 + count(preceding-sibling::page)"/>
    <xsl:variable name="slideshowNum" select="'0'"/>
    <xsl:variable name="slideNum" select="$pageNumber"/>
    
    <div class="page">
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
        <xsl:apply-templates select="node()"/>
    </div> 
    <xsl:comment>
        <xsl:text>end page </xsl:text>
        <xsl:value-of select="$slideNum"/>
    </xsl:comment>
  </xsl:template>

  <xsl:template match="page"  mode="collectIDs">
    <xsl:variable name="pageNum" select="1+count(./preceding-sibling::page)"/>
    <xsl:text>collectID('slide-0-</xsl:text>
    <xsl:value-of select="$pageNum"/>
    <xsl:text>', </xsl:text>
    <xsl:value-of select="$pageNum"/>
    <xsl:text>);&#10;</xsl:text>
    <xsl:apply-templates select="*"  mode="collectIDs"/>
  </xsl:template>

  <xsl:template match="h1|h2|h3|h4|h5"  mode="collectIDs">
    <xsl:variable name="pageNum" select="1+count(./ancestor::page/preceding-sibling::page)"/>
    <xsl:variable name="hid">
       <xsl:choose>
           <xsl:when test="@id != ''">
               <xsl:value-of select="@id"/>
           </xsl:when>
           <xsl:when test="local-name(*[1]) = 'a' and a[1]/@name != ''">
               <xsl:value-of select="a[1]/@name"/>
           </xsl:when>
       </xsl:choose>
    </xsl:variable>
    <xsl:if test="$hid != ''">
       <xsl:text>collectID('</xsl:text>
       <xsl:value-of select="$hid"/>
       <xsl:text>', </xsl:text>
       <xsl:value-of select="$pageNum"/>
       <xsl:text>);&#10;</xsl:text>
    </xsl:if>
    <xsl:apply-templates select="*"  mode="collectIDs"/>
  </xsl:template>

  <xsl:template match="*"  mode="collectIDs">
      <xsl:if test="@id != ''">
          <xsl:text>collectID('</xsl:text>
          <xsl:value-of select="@id"/>
          <xsl:text>', '_page</xsl:text>
          <xsl:value-of select="1+count(./ancestor::page/preceding-sibling::page)"/>
          <xsl:text>');$#10;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="*"  mode="collectIDs"/>
  </xsl:template>

  <xsl:template match="text()" mode="collectIDs">
    <xsl:copy-of select='.'/>
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
