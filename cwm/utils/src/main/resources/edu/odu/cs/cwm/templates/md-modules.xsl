<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

  <xsl:include href="md-common.xsl"/>
  <xsl:include href="paginate.xsl"/>
  
  <xsl:param name="format" select="'modules'"/>

  

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
	      <xsl:variable name="preamble" select="section[normalize-space(./*[1]) = 'Preamble']"/>
	      <xsl:if test="$preamble">
	         <xsl:apply-templates select="$preamble/*[position() &gt; 1]"/>
	      </xsl:if>
	      <form action="">
            <div class="showHideControls">
	          <input type="button" value="expand all" onclick="expandAll()"/>
	          <input type="button" value="collapse all" onclick="collapseAll()"/>
	          <xsl:if test="contains($formats, 'topics')">
	  	         <input type="button" value="table view" onclick="visitPage('outline__topics.html')"/>
	          </xsl:if>
            </div>
          </form>
	      
	      <xsl:for-each select="section">
	           <xsl:variable name="sectionName" select="normalize-space(*[1])"/>
	           <xsl:choose>
	               <xsl:when test="$sectionName = 'Preamble'"/>
                   <xsl:when test="$sectionName = 'Postscript'"/>
	               <xsl:when test="$sectionName = 'Presentation'"/>
	               <xsl:otherwise>
                      <xsl:apply-templates select="."/>
	               </xsl:otherwise>
	           </xsl:choose>
	      </xsl:for-each>
          <xsl:variable name="postscript" select="section[normalize-space(./*[1]) = 'Postscript']"/>
          <xsl:if test="$postscript">
             <xsl:apply-templates select="$postscript/*[position() &gt; 1]"/>
          </xsl:if>
	      <xsl:call-template name="insertFooter"/>
	    </div>
      </div>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="section">
  	  <xsl:variable name="toggleID" select="generate-id()"/>
  	  <!--  Generate the topic descriptor, possibly with an
  	        expand/collapse control.  -->
      <div class="topic{@depth}">
      	<xsl:value-of select="sectionHeader/@sectionNumber"/>
      	<xsl:choose>
      	    <xsl:when test="./section">
      		    <xsl:apply-templates select="sectionHeader/node()" mode="activities"/>
      	    </xsl:when>
      	    <xsl:otherwise>
      	    	<input type="button" value="+" class="expandButton" 
      	    	   onclick="toggleDisplay('{concat('_topic_', $toggleID)}')">
      	    	    <xsl:attribute name="id">
      	    	       <xsl:value-of select="concat('but_topic_', $toggleID)"/>
      	    	    </xsl:attribute>
      	    	</input>
      	        <xsl:apply-templates select="sectionHeader/*[local-name() != 'a'] | sectionHeader/text()"/>
      	    </xsl:otherwise>
      	</xsl:choose>
      </div>
      <!--  Process the contents of this section -->
      <xsl:message>
        <xsl:text>number of children </xsl:text>
        <xsl:value-of select="count(sectionContent/node())"/>
      </xsl:message>
      <xsl:variable name="activityLists" select="sectionContent/ol"/>
      <xsl:message>
        <xsl:text>activityLists size is </xsl:text>
        <xsl:value-of select="count($activityLists)"/>
      </xsl:message>
      <xsl:variable name="firstListPos">
          <xsl:choose>
              <xsl:when test="count($activityLists) = 0">
                 <xsl:value-of select="1 + count(sectionContent/*)"/>
              </xsl:when>
              <xsl:otherwise>
                 <xsl:variable name="firstList" select="$activityLists[1]"/>
                 <xsl:value-of select="index-of(sectionContent/*, $firstList)"/>
              </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>      
      <xsl:variable name="descriptiveContent" select="sectionContent/*[position() &lt; $firstListPos]"/>
      <xsl:variable name="activityContent" select="sectionContent/*[position() &gt;= $firstListPos]"/>
      <xsl:message>
        <xsl:text>descriptiveContent size is </xsl:text>
        <xsl:value-of select="count($descriptiveContent)"/>
      </xsl:message>
      <xsl:message>
        <xsl:text>activityContent size is </xsl:text>
        <xsl:value-of select="count($activityContent)"/>
      </xsl:message>
     
      <div class="module">
         <xsl:attribute name="id">
            <xsl:value-of select="concat('_topic_', $toggleID)"/>
         </xsl:attribute>
         
         <xsl:choose>
             <xsl:when test="(count($activityLists) != 0) and (count($descriptiveContent) != 0)">
                 <table width="100%">
                    <tr>
                       <td class="moduleDescription">
                          <div class="moduleDescription">
                              <xsl:apply-templates select="$descriptiveContent[local-name() != 'section']"/>
                          </div>
                       </td>
                       <td class="moduleActivitiesRt">
                          <div class="moduleActivitiesRt">
                              <b>Activities</b>
                              <xsl:apply-templates select="$activityContent[local-name() != 'section']"
                                 mode="activities"/>
                          </div>
                       </td>
                    </tr>
                 </table>
      	     </xsl:when>
             <xsl:when test="count($activityLists) != 0">
                  <div class="moduleActivities">
                      <b>Activities</b>
                      <xsl:apply-templates select="$activityContent"
                         mode="activities"/>
                  </div>
             </xsl:when>
             <xsl:when test="count($descriptiveContent) != 0">
                  <div class="moduleDescription">
                      <xsl:apply-templates select="$descriptiveContent"/>
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

  <xsl:variable name="prefixTable"
     select="/html/body/section[ends-with(normalize-space(sectionHeader), 'Presentation')]//table[2]"/>
  <xsl:template match="li" mode="activities">
       <xsl:variable name="kind" select="normalize-space(a[1]/@href)"/>
       <li>
          <xsl:copy-of select="@*"/>
          <xsl:choose>
              <xsl:when test="local-name(*[1]) = 'a'">
                 <img src="{$baseURL}graphics/{$kind}.png" alt="{$kind}"/>
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
       	  <xsl:apply-templates select="node()"/>
       </li>
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
