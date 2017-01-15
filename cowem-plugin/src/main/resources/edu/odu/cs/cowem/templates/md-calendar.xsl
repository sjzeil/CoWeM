<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="normalizeHeaders.xsl"/>
  <xsl:include href="sectionNumbering.xsl"/>
  <xsl:include href="sectioning.xsl"/>
  <xsl:include href="md-common.xsl"/>
  
  <xsl:param name="format" select="'calendar'"/>

  

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
      
      <script>
          window.onload = function() {
              calendarPageLoad();
          }
      </script>
      
      <xsl:apply-templates select="*//li"/>
    </xsl:copy>
  </xsl:template>
  
  

   
  <xsl:template match="li">
    <xsl:if test="count(.//a[@href='date:']) + count(.//a[@href='due:']) &gt; 0">
        <xsl:choose>
            <xsl:when test="local-name(*[1]) = 'p'">
                <div class='calendarEvent'>
                    <xsl:copy-of select="@*"/>
                    <xsl:call-template name="checkForDates"/>
                    <p>
                        <xsl:apply-templates select="*[1]" mode="activities2"/>
                    </p>
                    <xsl:apply-templates select="*[position &gt; 1]"/>
                </div>
              </xsl:when>
              <xsl:otherwise>
                  <div class='calendarEvent'>
                  <xsl:copy-of select="@*"/>
                  <xsl:call-template name="checkForDates"/>
                  <xsl:apply-templates select="." mode="activities2"/>
                </div>
              </xsl:otherwise>
        </xsl:choose>
     </xsl:if>
  </xsl:template>

  <xsl:template match="p|li" mode="activities2">
      <xsl:variable name="prefixTable"
        select="ancestor::body/presentation//table[2]"/>
      <xsl:choose>
          <xsl:when test="(local-name(*[1]) = 'a') and (normalize-space(*[1]/preceding-sibling::node()) = '')">
              <xsl:variable name="kind" select="normalize-space(a[1]/@href)"/>
              <img src="{$baseURL}graphics/{$kind}-kind.png" alt="{$kind}"/>
              <xsl:text> </xsl:text>
              <xsl:choose>
                 <xsl:when test="normalize-space(a[1]) = ''">
                     <xsl:variable name="kindTD" select="$prefixTable//td[normalize-space() = $kind]"/>
                     <xsl:if test="$kindTD">
                       <xsl:apply-templates select="$kindTD/../td[2]/node()"/>
                     </xsl:if>
                 </xsl:when>
                 <xsl:otherwise>
                    <xsl:apply-templates select="a[1]/node()"/>
                    <xsl:text> </xsl:text>
                 </xsl:otherwise>
              </xsl:choose>
              <xsl:apply-templates select="a[1]/following-sibling::node()"/>
          </xsl:when>
          <xsl:otherwise>
             <xsl:apply-templates select="node()"/>
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
  
  <xsl:variable name="midnight" select="concat('00:00:00','')"/>
  <xsl:variable name="lastMinute" select="concat('23:59:00','')"/>
  <xsl:variable name="lastSecond" select="concat('23:59:59','')"/>

  <xsl:template name="checkForDates">
    <xsl:choose>
        <xsl:when test=".//a[@href = 'date:']">
            <xsl:variable name="startDT"
                select=".//a[@href = 'date:'][1]/text()"/>
            <xsl:choose>
                <xsl:when test=".//a[@href = 'enddate:']">
                    <xsl:variable name="stopDT"
                        select=".//a[@href = 'enddate:'][1]/text()"/>
                    <xsl:attribute name="start">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$startDT"/>
                            <xsl:with-param name="padding" 
                                select="concat($midnight,'')"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                    <xsl:attribute name="stop">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$stopDT"/>
                            <xsl:with-param name="padding" select="$lastSecond"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                </xsl:when>
                <xsl:when test=".//a[@href = 'due:']">
                    <xsl:variable name="stopDT"
                        select=".//a[@href = 'due:'][1]/text()"/>
                    <xsl:attribute name="start">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$startDT"/>
                            <xsl:with-param name="padding" select="$midnight"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                    <xsl:attribute name="stop">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$stopDT"/>
                            <xsl:with-param name="padding" select="$lastSecond"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="start">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$startDT"/>
                            <xsl:with-param name="padding" select="$midnight"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                    <xsl:attribute name="stop">
                        <xsl:call-template name="reformatTime">
                            <xsl:with-param name="dateTime" select="$startDT"/>
                            <xsl:with-param name="padding" select="$lastSecond"/> 
                        </xsl:call-template>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:when test=".//a[@href = 'due:']">
            <xsl:variable name="startDT"
                    select=".//a[@href = 'due:'][1]/text()"/>
            <xsl:attribute name="start">
                <xsl:call-template name="reformatTime">
                    <xsl:with-param name="dateTime" select="$startDT"/>
                    <xsl:with-param name="padding" select="$lastMinute"/> 
                </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="stop">
                <xsl:call-template name="reformatTime">
                    <xsl:with-param name="dateTime" select="$startDT"/>
                    <xsl:with-param name="padding" select="$lastSecond"/> 
                </xsl:call-template>
            </xsl:attribute>
        </xsl:when>
      </xsl:choose>
  </xsl:template> 

  <xsl:template name="reformatTime">
      <xsl:param name="dateTime" />
      <xsl:param name="padding" />
      
      <xsl:variable name="fullPadding" select="concat('2017-0101T', $padding)"/>
      <xsl:variable name="dtLen" select="string-length($dateTime)"/>
      <xsl:variable name="suffix" select="substring($fullPadding, $dtLen)"/>
      <xsl:value-of select="concat($dateTime, $suffix)"/>
  </xsl:template>

</xsl:stylesheet>
