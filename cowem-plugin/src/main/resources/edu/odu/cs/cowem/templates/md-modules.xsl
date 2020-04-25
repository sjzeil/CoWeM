<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="normalizeHeaders.xsl"/>
  <xsl:include href="sectionNumbering.xsl"/>
  <xsl:include href="sectioning.xsl"/>
  <xsl:include href="md-common.xsl"/>
  
  <xsl:param name="format" select="'modules'"/>
  <xsl:param name="Calendar" select="''"/>
  

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
      
      <script>
          window.addEventListener('load',  (event) =&gt;  {
              modulePageLoad();
          });
      </script>
      
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
	    
	      <form action="">
            <div class="showHideControls">
	          <input type="button" value="expand all" onclick="expandAll()"/>
	          <input type="button" value="collapse all" onclick="collapseAll()"/>
	          <xsl:if test="contains($formats, 'topics')">
	  	         <input type="button" value="table view" onclick="visitPage('outline__topics.html')"/>
	          </xsl:if>
            </div>
          </form>

         <xsl:apply-templates select="preamble"/>
	      
        
          <xsl:if test="$Calendar != ''">
              <div class="calendar-title">Upcoming Events</div>
              <iframe class="calendar" src="outline__calendar.html">
                 Unable to display iframes.
              </iframe>
          </xsl:if>
        
          <xsl:apply-templates select="section"/>
          <xsl:apply-templates select="postscript/node()"/>
	      <xsl:call-template name="insertFooter"/>
	    </div>
      </div>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="preamble">
      <xsl:variable name="toggleID" select="generate-id()"/>
      <div class="preamble">
          <input type="button" value="-" class="expandButton" 
              onclick="toggleDisplay('{concat('_topic_', $toggleID)}')">
               <xsl:attribute name="id">
                  <xsl:value-of select="concat('but_topic_', $toggleID)"/>
               </xsl:attribute>
          </input>
      </div>
      <div class="module">
         <xsl:attribute name="id">
            <xsl:value-of select="concat('_topic_', $toggleID)"/>
         </xsl:attribute>
          <xsl:apply-templates select="node()"/>
      </div>
  </xsl:template>
  
  
  <xsl:template match="section">
  	  <xsl:variable name="toggleID" select="generate-id()"/>
  	  <!--  Generate the topic descriptor, possibly with an
  	        expand/collapse control.  -->
      <div class="topic{@depth}">
        <xsl:copy-of select="sectionHeader/@id"/>
      	<xsl:value-of select="sectionHeader/@sectionNumber"/>
      	<xsl:choose>
      	    <xsl:when test="./section">
      		    <xsl:apply-templates select="sectionHeader/node()" mode="activities"/>
      	    </xsl:when>
      	    <xsl:otherwise>
      	    	<input type="button" value="-" class="expandButton" 
      	    	   onclick="toggleDisplay('{concat('_topic_', $toggleID)}')">
      	    	    <xsl:attribute name="id">
      	    	       <xsl:value-of select="concat('but_topic_', $toggleID)"/>
      	    	    </xsl:attribute>
      	    	</input>
      	        <xsl:apply-templates select="sectionHeader/* | sectionHeader/text()"/>
      	    </xsl:otherwise>
      	</xsl:choose>
      </div>
      <!--  Process the contents of this section -->
     
      <div class="module">
         <xsl:attribute name="id">
            <xsl:value-of select="concat('_topic_', $toggleID)"/>
         </xsl:attribute>
         <xsl:text> </xsl:text>
         <xsl:choose>
             <xsl:when test="(count(sectionContent/*) != 0) and (count(sectionDescription/*) != 0)">
                 <table width="100%">
                    <tr>
                       <td class="moduleDescription">
                          <div class="moduleDescription">
                              <xsl:apply-templates select="sectionDescription/node()"/>
                          </div>
                       </td>
                       <td class="moduleActivitiesRt">
                          <div class="moduleActivitiesRt">
                              <b>Activities</b>
                              <xsl:apply-templates select="sectionContent/*"
                                 mode="activities"/>
                          </div>
                       </td>
                    </tr>
                 </table>
      	     </xsl:when>
             <xsl:when test="count(sectionContent/ol) != 0">
                  <div class="moduleActivities">
                      <b>Activities</b>
                      <xsl:apply-templates select="sectionContent/*"
                         mode="activities"/>
                  </div>
             </xsl:when>
             <xsl:when test="count(sectionDescription/*) != 0">
                  <div class="moduleDescription">
                      <xsl:apply-templates select="sectionDescription/node()"/>
                  </div>
             </xsl:when>
             <xsl:when test="count(sectionContent/*) != 0">
                  <div class="moduleDescription">
                      <xsl:apply-templates select="sectionContent/*"/>
                  </div>
             </xsl:when>
      	 </xsl:choose>
       </div>
       <xsl:apply-templates select="section"/>
  </xsl:template>
    
  <xsl:template match="ol" mode="activities">
     <ol>
        <xsl:apply-templates select="*" mode="activities"/>
     </ol>
  </xsl:template>

   
  <xsl:template match="li" mode="activities">
    <xsl:choose>
        <xsl:when test="local-name(*[1]) = 'p'">
            <li>
                <xsl:copy-of select="@*"/>
                <p>
                    <xsl:apply-templates select="*[1]" mode="activities2"/>
                </p>
                <xsl:apply-templates select="*[position &gt; 1]"/>
            </li>
          </xsl:when>
          <xsl:otherwise>
            <li>
              <xsl:copy-of select="@*"/>
              <xsl:apply-templates select="." mode="activities2"/>
            </li>
          </xsl:otherwise>
       </xsl:choose>
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

  <xsl:template match="*|text()" mode="activities">
      <xsl:apply-templates select="."/>
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
