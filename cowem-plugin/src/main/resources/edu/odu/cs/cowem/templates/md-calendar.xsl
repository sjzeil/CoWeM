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
      <xsl:variable name="pass1"> 
          <html>
            <xsl:copy-of select="@*"/>
	        <xsl:apply-templates select="head"/> 
	        <xsl:apply-templates select="body"/>
	      </html>
	  </xsl:variable>    
	  <xsl:apply-templates select="$pass1" mode="sortEvents"/>
  </xsl:template>


  <xsl:template match="body">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <script>
          window.addEventListener('load',  (event) =&gt;  {
              calendarPageLoad();
          });
      </script>
      <ul>
      <xsl:apply-templates select="*//li | .//h1 | .//h2 | .//h2 | .//h4" mode="activities1"/>
      </ul>
    </xsl:copy>
  </xsl:template>
  
  

   
  <xsl:template match="li"  mode="activities1">
    <xsl:if test="count(.//a[@href='date:']) + count(.//a[@href='due:']) &gt; 0">
        <xsl:choose>
            <xsl:when test="local-name(*[1]) = 'p'">
                <li class='calendarEvent'>
                    <xsl:copy-of select="@*"/>
                    <xsl:call-template name="checkForDates"/>
                    <xsl:apply-templates select="*[1]" mode="activities2"/>
                    <xsl:apply-templates select="*[position &gt; 1]" mode="flatten"/>
                </li>
              </xsl:when>
              <xsl:otherwise>
                  <li class='calendarEvent'>
                  <xsl:copy-of select="@*"/>
                  <xsl:call-template name="checkForDates"/>
                  <xsl:apply-templates select="." mode="activities2"/>
                </li>
              </xsl:otherwise>
        </xsl:choose>
     </xsl:if>
  </xsl:template>

  <xsl:template match="h1|h2|h3|h4"  mode="activities1">
    <xsl:if test="count(.//a[@href='date:']) + count(.//a[@href='due:']) &gt; 0">
        <li class='calendarEvent'>
            <xsl:copy-of select="@*"/>
            <xsl:call-template name="checkForDates"/>
            <xsl:apply-templates select="node()" mode="flatten"/>
        </li>
     </xsl:if>
  </xsl:template>



 <xsl:template match="p|li" mode="activities2">
      <xsl:variable name="prefixTable"
        select="ancestor::body/presentation//table[2]"/>
      <xsl:choose>
          <xsl:when test="(local-name(*[1]) = 'a') and (normalize-space(*[1]/preceding-sibling::node()) = '')">
              <xsl:variable name="kind" select="normalize-space(a[1]/@href)"/>
              <xsl:choose>
                 <xsl:when test="normalize-space(a[1]) = ''">
                     <xsl:variable name="kindTD" select="$prefixTable//td[normalize-space() = $kind]"/>
                     <xsl:if test="$kindTD">
                       <xsl:apply-templates select="$kindTD/../td[2]/node()" mode="flatten"/>
                     </xsl:if>
                 </xsl:when>
                 <xsl:otherwise>
                    <xsl:apply-templates select="a[1]/node()" mode="flatten"/>
                    <xsl:text> </xsl:text>
                 </xsl:otherwise>
              </xsl:choose>
              <xsl:apply-templates select="a[1]/following-sibling::node()" mode="flatten"/>
          </xsl:when>
          <xsl:otherwise>
             <xsl:apply-templates select="node()" mode="flatten"/>
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

  <xsl:template match="p|br" mode="flatten">
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="node()" mode="flatten"/>
  </xsl:template>

  <xsl:template match="a" mode="flatten">
      <xsl:if test="normalize-space(node()) != ''">
          <xsl:copy>
              <xsl:copy-of select="@*"/>
              <xsl:apply-templates select="node()" mode="flatten"/>
          </xsl:copy>
      </xsl:if>
  </xsl:template>

  <xsl:template match="text()"  mode="flatten">
    <xsl:copy-of select='.'/>
  </xsl:template>


  <xsl:template match="*"  mode="flatten">
    <xsl:copy>
      <xsl:copy-of select='@*'/>
      <xsl:apply-templates select="*|text()"  mode="flatten"/>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="html" mode="sortEvents">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="head" mode="sortEvents"/>
      <xsl:apply-templates select="body" mode="sortEvents"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="head" mode="sortEvents">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="*"/>
      <base target="_blank"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sortEvents">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="script"/>
      
      
      <script>
          window.addEventListener('load',  (event) =>  {
              calendarPageLoad();
          });
      </script>
      
      <ul>
        <xsl:for-each select="ul/li">
          <xsl:sort select="@start"/>  
          <xsl:sort select="@stop"/>
        
          <xsl:text>&#10;</xsl:text>
          <xsl:copy-of select="."/>
      </xsl:for-each>
      </ul>
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
