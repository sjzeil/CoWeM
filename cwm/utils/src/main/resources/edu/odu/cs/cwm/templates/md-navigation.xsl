<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:param name="format" select="'navigation'"/>

  <xsl:import href="md-common.xsl"/>
  <xsl:import href="paginate.xsl"/>
  
  

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="head"/>  
	  <xsl:apply-templates select="body"/>    
    </html>
  </xsl:template>


  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:apply-templates select="node()"/>
      
    </xsl:copy>
  </xsl:template>


  <xsl:template match="ul">
    <table>
      <xsl:apply-templates select="node()"/>
    </table>
  </xsl:template>

  <xsl:template match="li">
    <tr>
        <td>
            <xsl:apply-templates select="node()"/>
        </td>
    </tr>
    <tr class="separator"></tr>
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
