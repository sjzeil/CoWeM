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
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="head"/>
      <xsl:apply-templates select="body" mode="paginate"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="*|text()" mode="copy-of">
      <xsl:copy-of select="."/>
  </xsl:template>


	<xsl:template match="body" mode="paginate">
		<xsl:variable name="split">
			<xsl:copy>
				<xsl:copy-of select="@*" />
				<xsl:variable name="splits" select="h1 | h2 | h3 | hr" />
				<xsl:choose>
					<xsl:when test="count($splits) = 0">
						<xsl:copy-of select='*' />
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="firstSplit" select="$splits[1]" />
						<xsl:if test="generate-id(*[1]) != generate-id($firstSplit)">
							<xsl:variable name="exclusions"
								select="$firstSplit | $firstSplit/following-sibling::*" />
							<page>
								<xsl:apply-templates select="* except $exclusions"
									mode="copy-of" />
							</page>
						</xsl:if>
						<xsl:variable name="pageCount" select="count($splits)-1" />
						<xsl:for-each select="$splits">
							<page>
								<xsl:copy-of select="." />
								<xsl:variable name="stoppers"
									select="./following-sibling::h1
							| ./following-sibling::h2 | ./following-sibling::h3 | ./following-sibling::hr" />
								<xsl:choose>
									<xsl:when test="count($stoppers) = 0">
										<xsl:apply-templates mode="copy-of"
											select="./following-sibling::*" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="stop" select="$stoppers[1]" />
										<xsl:variable name="exclusions"
											select="$stop | $stop/following-sibling::*" />
										<xsl:apply-templates mode="copy-of"
											select="./following-sibling::* except $exclusions" />
									</xsl:otherwise>
								</xsl:choose>
							</page>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:copy>
		</xsl:variable>
		<xsl:variable name="merged">
		    <xsl:apply-templates select="$split" mode="merging" />
		</xsl:variable>
		<xsl:apply-templates select="$merged" mode="incremental" />
	</xsl:template>

	<xsl:template match="body" mode="merging">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="*" mode="merging" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="page" mode="merging">
	   <xsl:variable name="prior" select="./preceding-sibling::page[1]"/>
       <xsl:variable name="next" select="./following-sibling::page[1]"/>
       <!-- 
       <xsl:message>
           <xsl:text>merging </xsl:text>
           <xsl:value-of select="count($prior)"/>
           <xsl:text> </xsl:text>
           <xsl:value-of select="local-name(*[1])"/>
           <xsl:text> </xsl:text>
           <xsl:value-of select="count(*)"/>
           <xsl:text> </xsl:text>
           <xsl:value-of select="count($next)"/>
       </xsl:message>
        -->
	   <xsl:choose>
	       <xsl:when test="count(*) = 1 and count($next) = 1 and count($next/*) = 1">
	           <xsl:copy-of select= '.'/>
	       </xsl:when>
           <xsl:when test="count(*) = 1 and count($next) = 1">
               <!--  omit this page -->
           </xsl:when>
           <xsl:when test="count(*) = 1 and count($next) = 0">
               <xsl:copy-of select= '.'/>
           </xsl:when>
           <xsl:when test="count($prior) = 0">
               <xsl:copy-of select= '.'/>
           </xsl:when>
           <xsl:when test="count(*) &gt; 1 and count($prior) = 1 and count($prior/*) = 1">
               <page>
                   <xsl:copy-of select="$prior/*"/>
                   <xsl:apply-templates select="*" mode="copy-of"/>
               </page>
           </xsl:when>
           <xsl:otherwise>
               <xsl:copy-of select= '.'/>
           </xsl:otherwise>
	   </xsl:choose>
	</xsl:template>



    <xsl:template match="body" mode="incremental">
        <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates select="*" mode="incremental" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="page" mode="incremental">
        <xsl:choose>
            <xsl:when test="count(.//span[@class='incremental']) &gt; 0">
                <xsl:variable name="thisPage" select='.'/>
                <xsl:for-each select=".//li">
                    <xsl:call-template name="copyIncremental">
                        <xsl:with-param name="thisPage" select="$thisPage"/>
                        <xsl:with-param name="upTo" select="."/>
                        <xsl:with-param name="node" select="$thisPage"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:copy-of select="$thisPage"/> 
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select='.'/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

	<xsl:template name="copyIncremental">
		<xsl:param name="thisPage" />
		<xsl:param name="upTo" />
		<xsl:param name="node" />

		<xsl:variable name="upToID" select="generate-id($upTo)" />
		<xsl:variable name="ancestors" select="$node/ancestor::*" />
		<xsl:variable name="followers" select="$node/following::* | $node/descendant::*" />
		
		
		<xsl:choose>
		    <xsl:when test="self::*">
            <xsl:if test="count($ancestors[generate-id() = $upToID])
		               + count($followers[generate-id() = $upToID]) &gt; 0">
			<xsl:element name="{local-name($node)}">
			    <xsl:copy-of select="@*[local-name() != 'id']"/>
				<xsl:for-each select="$node/* | $node/text()">
					<xsl:call-template name="copyIncremental">
						<xsl:with-param name="thisPage" select="$thisPage" />
						<xsl:with-param name="upTo" select="$upTo" />
						<xsl:with-param name="node" select="." />
					</xsl:call-template>
				</xsl:for-each>
			</xsl:element>
			</xsl:if>
		    </xsl:when>
            <xsl:when test="self::text()">
                <xsl:copy-of select='.'/>
            </xsl:when>
		</xsl:choose>
	</xsl:template>





</xsl:stylesheet>
