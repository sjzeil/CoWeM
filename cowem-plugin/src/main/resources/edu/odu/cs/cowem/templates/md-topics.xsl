<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="normalizeHeaders.xsl"/>
  <xsl:include href="sectionNumbering.xsl"/>
  <xsl:include href="sectioning.xsl"/>
  
  <xsl:param name="format" select="'topics'"/>

  

  <xsl:template match="/">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="html">
  	<xsl:variable name="numbered">
	  <xsl:apply-templates select="body" mode="sectionNumbering"/>    
  	</xsl:variable>
  	<xsl:variable name="sectioned">
	  <xsl:apply-templates select="$numbered" mode="sectioning"/>    
  	</xsl:variable>
    <html>
      <xsl:copy-of select="@*"/>
	  <xsl:apply-templates select="head"/>  
	  <xsl:apply-templates select="$sectioned"/>    
    </html>
  </xsl:template>


  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <div class="titleblock">
        <div class="courseName">@courseName@, @semester@</div>
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
      
      <div class="center">
	    <div class="leftPart">
	      <iframe src="../navigation/index.html" class="navigation">_</iframe>
	    </div>
	    <div class="rightPart">
	      <xsl:apply-templates select="preamble/node()"/>
	      <form action="">
            <div class="showHideControls">
	          <xsl:if test="contains($formats, 'modules')">
	  	         <input type="button" value="outline view" onclick="visitPage('outline__modules.html')"/>
	          </xsl:if>
            </div>
          </form>
	      
	      <xsl:variable name="prefixTable"
              select="presentation//table[1]"/>
	      
	      <table border="1" rules="all" frame="box" role="outline">
             <tr class="columnheader">
                <xsl:apply-templates select="$prefixTable/thead/tr[1]/*"
                   mode="columnHeaders"/>
             </tr>

             <xsl:apply-templates select="section"/>
	       </table>
           <xsl:apply-templates select="postscript/node()"/>
	      <xsl:call-template name="insertFooter"/>
	    </div>
      </div>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="td|th" mode="columnHeaders">
      <th>
          <xsl:apply-templates select="node()"/>
      </th>
  </xsl:template>
  
  
  <xsl:template match="section">
      <xsl:variable name="prefixTable"
        select="ancestor::body/presentation//table[1]"/>
      
      <tr class="topic{@depth}">
          <td class="topic{@depth}" colspan="{count($prefixTable/thead/tr[1]/*)}">
             <span class="topic{@depth}">
                 <xsl:value-of select="sectionHeader/@sectionNumber"/>
                 <xsl:text> </xsl:text>
                 <xsl:apply-templates select="sectionHeader/node()"/>
             </span>
          </td>
      </tr>
      <xsl:apply-templates select="sectionContent/*" mode="activities"/>
      <xsl:apply-templates select="section"/>
  </xsl:template>

  <xsl:template match="ol" mode="activities">
      <xsl:variable name="prefixTable"
        select="ancestor::body/presentation//table[1]"/>
      
      <xsl:variable name="theList" select='.'/>
      <tr class="item">
          <xsl:for-each select="$prefixTable/tbody/tr/*">
              <!--   xsl:message>
                 <xsl:text>Tabulating </xsl:text>
                 <xsl:value-of select="normalize-space(.)"/>
              </xsl:message -->
              <xsl:variable name="selectors" select="concat(' ', normalize-space(), ' ')"/>
              <td class="item">
                  <xsl:for-each select="$theList/li">
                      <xsl:call-template name='tabulateListItem'>
                          <xsl:with-param name="item" select='.'/>
                          <xsl:with-param name="selectors" select="$selectors"/>
                      </xsl:call-template>
                  </xsl:for-each>
              </td>
          </xsl:for-each>
      </tr>
  </xsl:template>


  <xsl:template match="*" mode="activities">
      <xsl:variable name="prefixTable"
        select="ancestor::body/presentation//table[1]"/>
      <tr class="subject{@depth}">
          <td class="subject{@depth}" colspan="{count($prefixTable/thead/tr[1]/*)}">
              <xsl:apply-templates select="."/>
          </td>
      </tr>
  </xsl:template>

      
  <xsl:template name='tabulateListItem'>
      <xsl:param name="item" select='.'/>
      <xsl:param name="selectors" select="'unknown'"/>

      <!--  xsl:message>
          <xsl:text>In tabulateListItem, with item </xsl:text>
          <xsl:value-of select="local-name($item)"/>
          <xsl:text>, 1st elem is </xsl:text>
          <xsl:value-of select="local-name($item/*[1])"/>          
      </xsl:message -->        
      
      
      <xsl:choose>
          <xsl:when test="local-name($item/*[1]) = 'p'">
              <xsl:call-template name="tabulateListItem">
                  <xsl:with-param name="item" select="$item/*[1]"/>
                  <xsl:with-param name="selectors" select="$selectors"/>
              </xsl:call-template>
          </xsl:when>    
   
          <xsl:when test="(local-name($item/*[1]) = 'a') and (normalize-space($item/*[1]/preceding-sibling::node()) = '')">
              <xsl:variable name="kind0"  select="normalize-space($item/a[1]/@href)"/>
              <xsl:variable name="kind">
                  <xsl:choose>
                      <xsl:when test="$kind0 = ''">
                          <xsl:text>unknown</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                          <xsl:value-of select="$kind0"/>
                      </xsl:otherwise>
                  </xsl:choose>
              </xsl:variable>
              <!--  xsl:message>
                 <xsl:text>item has kind </xsl:text>
                 <xsl:value-of select="$kind"/>
              </xsl:message>        
              <xsl:message>
                 <xsl:text>selectors are "</xsl:text>
                 <xsl:value-of select="$selectors"/>
                 <xsl:text>"</xsl:text>
              </xsl:message -->        
 
              <xsl:if test="contains($selectors, concat(' ', $kind, ' '))">
                  <p>
                      <img src="{$baseURL}graphics/{$kind}-kind.png" alt="{$kind}"/>
                      <xsl:apply-templates select="$item/a[1]/following-sibling::node()"/>
                  </p>
              </xsl:if>
          </xsl:when>
          <xsl:otherwise>
              <xsl:if test="contains($selectors, 'unknown')">
                  <p>
                      <xsl:apply-templates select="node()"/>
                  </p>
              </xsl:if>
          </xsl:otherwise>
      </xsl:choose>
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
