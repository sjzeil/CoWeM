<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:import href="md-common.xsl"/>
  <xsl:import href="paginate.xsl"/>
  
  <xsl:param name="format" select="'navigation'"/>

  

  <xsl:template match="/">
    <xsl:apply-templates select="/html"/>
  </xsl:template>

  <xsl:template match="html">
    <html>
      <xsl:copy-of select="@*"/>
	  <head>
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}.css" />
      <link rel="stylesheet" type="text/css" media="screen, projection, print"
        href="{$stylesURL}/md-{$format}-ext.css" />
      <xsl:call-template name="generateCSSLinks"/>
      <meta name="viewport" content="width=device-width, initial-scale=1"/> 
      <script type="text/javascript"
          src="{$stylesURL}/md-{$format}.js">
          <xsl:text> </xsl:text>
      </script>
      <script type="text/javascript"
          src="{$mathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
          <xsl:text> </xsl:text>
      </script>
      </head>
      <xsl:call-template name="generateJSLinks"/> 
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
