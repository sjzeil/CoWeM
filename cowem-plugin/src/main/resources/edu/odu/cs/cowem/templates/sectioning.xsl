<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet> 
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>
  
  <!-- 
       
     section: Arranges the document into a hierarchy of sections based on
        h1..h5 headers. Each section has a header, and one or more content
        areas.  Content areas are separated by top-level <hr/> elements.
  -->


<!--  For testing purposes -->

  <xsl:template match="html[@test = 'sectioning']">
      <xsl:apply-templates select="." mode="sectioning"/>
  </xsl:template>



  <xsl:template match="html" mode="sectioning">
    <xsl:message>
        <xsl:text>Testing sectioning.</xsl:text>
    </xsl:message>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="sectioning"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body" mode="sectioning">
    <xsl:variable name="sectioned">
         <xsl:apply-templates select="h1" mode="sectioning1"/>
    </xsl:variable>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="$sectioned" mode="splitContent"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h1|h2|h3|h4|h5" mode="copyAllButHeaders">
  </xsl:template>
  
  <xsl:template match="*|text()" mode="copyAllButHeaders">
      <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*|text()" mode="copy-of">
      <xsl:copy-of select="."/>
  </xsl:template>


	<xsl:template match="h1" mode="sectioning1">
		<xsl:variable name="title" select="normalize-space(.)" />
		<xsl:variable name="exclusions"
			select="./following-sibling::h1[1] | ./following-sibling::h1[1]/following-sibling::*" />
        <xsl:variable name="thisSection" select="./following-sibling::* except $exclusions"/>
		<xsl:choose>
			<xsl:when test="$title = 'Preamble'">
				<preamble>
					<xsl:apply-templates select="$thisSection" mode="copy-of"/>
				</preamble>
			</xsl:when>
			<xsl:when test="$title = 'Postscript'">
				<postscript>
                    <xsl:apply-templates select="$thisSection" mode="copy-of"/>
				</postscript>
			</xsl:when>
			<xsl:when test="$title = 'Presentation'">
				<presentation>
                    <xsl:apply-templates select="$thisSection" mode="copy-of"/>
				</presentation>
			</xsl:when>
			<xsl:otherwise>
			    <xsl:message>
			        <xsl:text>h1 section </xsl:text>
			        <xsl:value-of select="$title"/>
			    </xsl:message>
                <xsl:message>
                    <xsl:text># exclusions </xsl:text>
                    <xsl:value-of select="count($exclusions)"/>
                </xsl:message>
			    <xsl:variable name='innerSections' select="./following-sibling::h2[1] 
			       | ./following-sibling::h2[1]/following-sibling::*"/>
                <xsl:message>
                    <xsl:text># inner </xsl:text>
                    <xsl:value-of select="count($innerSections)"/>
                </xsl:message>
				<section depth="1">
					<sectionHeader tag="h1">
						<xsl:copy-of select="@* | node()" />
					</sectionHeader>
					<sectionContent>
						<xsl:apply-templates select="$thisSection except $innerSections"
							mode="copyAllButHeaders" />
					</sectionContent>
    				<xsl:apply-templates select="$thisSection[local-name() = 'h2']"
							mode="sectioning1" />
				</section>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="h2" mode="sectioning1">
		<xsl:variable name="title" select="normalize-space(.)" />
		<xsl:variable name="exclusions"
			select="./following-sibling::h1[1] | ./following-sibling::h1[1]/following-sibling::*
               | ./following-sibling::h2[1] | ./following-sibling::h2[1]/following-sibling::*" />
		<xsl:variable name="thisSection"
			select="./following-sibling::* except $exclusions" />
		<xsl:message>
			<xsl:text>h2 section </xsl:text>
			<xsl:value-of select="$title" />
		</xsl:message>
		<xsl:message>
			<xsl:text># exclusions </xsl:text>
			<xsl:value-of select="count($exclusions)" />
		</xsl:message>
		<xsl:variable name='innerSections'
			select="./following-sibling::h3[1] 
                   | ./following-sibling::h3[1]/following-sibling::*" />
		<xsl:message>
			<xsl:text># inner </xsl:text>
			<xsl:value-of select="count($innerSections)" />
		</xsl:message>
		<section depth="2">
			<sectionHeader tag="h2">
				<xsl:copy-of select="@* | node()" />
			</sectionHeader>
			<sectionContent>
				<xsl:apply-templates select="$thisSection except $innerSections"
					mode="copyAllButHeaders" />
			</sectionContent>
            <xsl:apply-templates select="$thisSection[local-name() = 'h3']"
                    mode="sectioning1" />
		</section>
	</xsl:template>


    <xsl:template match="h3" mode="sectioning1">
        <xsl:variable name="title" select="normalize-space(.)" />
        <xsl:variable name="exclusions"
            select="./following-sibling::h1[1] | ./following-sibling::h1[1]/following-sibling::*
               | ./following-sibling::h2[1] | ./following-sibling::h2[1]/following-sibling::*
               | ./following-sibling::h3[1] | ./following-sibling::h3[1]/following-sibling::*" 
               />
        <xsl:variable name="thisSection"
            select="./following-sibling::* except $exclusions" />
        <xsl:message>
            <xsl:text>h3 section </xsl:text>
            <xsl:value-of select="$title" />
        </xsl:message>
        <xsl:message>
            <xsl:text># exclusions </xsl:text>
            <xsl:value-of select="count($exclusions)" />
        </xsl:message>
        <xsl:variable name='innerSections'
            select="./following-sibling::h4[1] 
                   | ./following-sibling::h4[1]/following-sibling::*" />
        <xsl:message>
            <xsl:text># inner </xsl:text>
            <xsl:value-of select="count($innerSections)" />
        </xsl:message>
        <section depth="3">
            <sectionHeader tag="h3">
                <xsl:copy-of select="@* | node()" />
            </sectionHeader>
            <sectionContent>
                <xsl:apply-templates select="$thisSection except $innerSections"
                    mode="copyAllButHeaders" />
            </sectionContent>
            <xsl:apply-templates select="$thisSection[local-name() = 'h4']"
                    mode="sectioning1" />
        </section>
    </xsl:template>


    <xsl:template match="h4" mode="sectioning1">
        <xsl:variable name="title" select="normalize-space(.)" />
        <xsl:variable name="exclusions"
            select="./following-sibling::h1[1] | ./following-sibling::h1[1]/following-sibling::*
               | ./following-sibling::h2[1] | ./following-sibling::h2[1]/following-sibling::*
               | ./following-sibling::h3[1] | ./following-sibling::h3[1]/following-sibling::* 
               | ./following-sibling::h4[1] | ./following-sibling::h4[1]/following-sibling::*" 
               />
        <xsl:variable name="thisSection"
            select="./following-sibling::* except $exclusions" />
        <xsl:message>
            <xsl:text>h4 section </xsl:text>
            <xsl:value-of select="$title" />
        </xsl:message>
        <xsl:message>
            <xsl:text># exclusions </xsl:text>
            <xsl:value-of select="count($exclusions)" />
        </xsl:message>
        <xsl:variable name='innerSections'
            select="./following-sibling::h5[1] 
                   | ./following-sibling::h5[1]/following-sibling::*" />
        <xsl:message>
            <xsl:text># inner </xsl:text>
            <xsl:value-of select="count($innerSections)" />
        </xsl:message>
        <section depth="4">
            <sectionHeader tag="h4">
                <xsl:copy-of select="@* | node()" />
            </sectionHeader>
            <sectionContent>
                <xsl:apply-templates select="$thisSection except $innerSections"
                    mode="copyAllButHeaders" />
            </sectionContent>
            <xsl:apply-templates select="$thisSection[local-name() = 'h5']"
                    mode="sectioning1" />
        </section>
    </xsl:template>


    <xsl:template match="h5" mode="sectioning1">
        <xsl:variable name="title" select="normalize-space(.)" />
        <xsl:variable name="exclusions"
            select="./following-sibling::h1[1] | ./following-sibling::h1[1]/following-sibling::*
               | ./following-sibling::h2[1] | ./following-sibling::h2[1]/following-sibling::*
               | ./following-sibling::h3[1] | ./following-sibling::h3[1]/following-sibling::* 
               | ./following-sibling::h4[1] | ./following-sibling::h4[1]/following-sibling::* 
               | ./following-sibling::h5[1] | ./following-sibling::h5[1]/following-sibling::*" 
               />
        <xsl:variable name="thisSection"
            select="./following-sibling::* except $exclusions" />
        <xsl:message>
            <xsl:text>h5 section </xsl:text>
            <xsl:value-of select="$title" />
        </xsl:message>
        <xsl:message>
            <xsl:text># exclusions </xsl:text>
            <xsl:value-of select="count($exclusions)" />
        </xsl:message>
        <section depth="5">
            <sectionHeader tag="h5">
                <xsl:copy-of select="@* | node()" />
            </sectionHeader>
            <sectionContent>
                <xsl:apply-templates select="$thisSection"
                    mode="copyAllButHeaders" />
            </sectionContent>
        </section>
    </xsl:template>

  
 <!--  split content -->
  
  <xsl:template match="section" mode="splitContent">
      <xsl:apply-templates select="." mode="splitContent2"/>
  </xsl:template>

  <xsl:template match="section" mode="splitContent2">
      <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:copy-of select="sectionHeader"/>
          <xsl:apply-templates select="sectionContent" mode="splitContent2"/>
          <xsl:apply-templates select="section" mode="splitContent2"/>
      </xsl:copy>
  </xsl:template>
  
  <xsl:template match="sectionContent" mode="splitContent2">
      <xsl:choose>
          <xsl:when test="hr">
              <xsl:variable name="hr1" select="hr[1]"/>
              <sectionDescription>
                  <xsl:copy-of select="$hr1/preceding-sibling::*"/>
              </sectionDescription>
              <sectionContent>
                  <xsl:copy-of select="$hr1/following-sibling::*"/>
              </sectionContent>
          </xsl:when>
          <xsl:otherwise>
              <xsl:copy-of select='.'/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
  <xsl:template match="*|text()" mode="splitContent">
      <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="*|text()" mode="splitContent2">
      <xsl:copy-of select="."/>
  </xsl:template>
  

</xsl:stylesheet>
