<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="normalizeHeaders.xsl" />
	<xsl:include href="sectionNumbering.xsl" />
	<xsl:include href="sectioning.xsl" />
	<xsl:include href="md-common.xsl" />

	<xsl:param name="format" select="'modules'" />
	<xsl:param name="Calendar" select="''" />


	<xsl:template match="/">
		<xsl:apply-templates select="*|text()" />
	</xsl:template>

	<xsl:template match="html">
		<xsl:variable name="numbered">
			<xsl:apply-templates select="body" mode="sectionNumbering" />
		</xsl:variable>
		<xsl:variable name='sectioned'>
			<xsl:apply-templates select="$numbered" mode="sectioning" />
		</xsl:variable>
		
		<html>
			<xsl:apply-templates select='head' mode="LMS"/>
			<xsl:apply-templates select="$sectioned" />
		</html>
	</xsl:template>


	<xsl:template match="head" mode="LMS">
	    <xsl:copy>
          <xsl:copy-of select="@*"/>
          <meta charset="UTF-8"/>
          <link rel="stylesheet" type="text/css" media="screen, projection, print"
            href="{$stylesURL}/md-{$format}.css" />
          <link rel="stylesheet" type="text/css" media="screen, projection, print"
            href="{$stylesURL}/md-{$format}-ext.css" />
          <xsl:call-template name="generateCSSLinks"/>
          <meta name="viewport" content="width=device-width, initial-scale=1"/> 
          <link rel="stylesheet" 
            href="@highlightjsURL@/styles/googlecode.css"/>
          <script src="@highlightjsURL@/highlight.pack.js">
            <xsl:text> </xsl:text>
          </script>
          <script>hljs.initHighlightingOnLoad();</script>
          <script type="text/javascript"
              src="{$stylesURL}/md-{$format}.js">
              <xsl:text> </xsl:text>
          </script>
          <script type="text/javascript"
              src="{$stylesURL}/md-{$format}-ext.js">
              <xsl:text> </xsl:text>
          </script>
          <script type="text/javascript"
              src="{$stylesURL}/rawdeflate.js">
              <xsl:text> </xsl:text>
          </script>
          <script type="text/javascript"
              src="{$stylesURL}/plantuml.js">
              <xsl:text> </xsl:text>
          </script>
          <script type="text/javascript"
              src="{$stylesURL}/mermaid.min.js">
              <xsl:text> </xsl:text>
          </script>
          <xsl:choose>
            <xsl:when test="lower-case($mathSupport) = 'latex'">
               <script type="text/javascript"><xsl:text>
                 window.MathJax = {
                   tex2jax: {
                   inlineMath: [ ['$','$'], ["\\(","\\)"] ],
                   processEscapes: true
                 }
               };
               </xsl:text></script>
               <script type="text/javascript"
                 src="{$mathJaxURL}/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
                 <xsl:text> </xsl:text>
               </script>
            </xsl:when>
            <xsl:when test="lower-case($mathSupport) = 'ascii'">
               <script type="text/javascript"><xsl:text>
                 window.MathJax = {
                   asciimath2jax: {
                     delimiters: [ ['$','$'], ['`', '`']],
                   processEscapes: true
                 }
               };
               </xsl:text></script>
               <script type="text/javascript"
                 src="{$mathJaxURL}/MathJax.js?config=AM_HTMLorMML">
                  <xsl:text> </xsl:text>
               </script>
            </xsl:when>
          </xsl:choose>
	      <base target="_blank" />
          <xsl:copy-of select="*"/>
        </xsl:copy>
	</xsl:template>

	<xsl:template match="body">
		<xsl:copy>
			<xsl:copy-of select="@*" />

			<xsl:apply-templates select="section" />
			<script type='application/javascript'>
			    revealSelectedModule();
			</script>
		</xsl:copy>
	</xsl:template>


	<xsl:template match="preamble" />



	<xsl:template match="section">
		<xsl:variable name='moduleCount'
			select="1+count(preceding::section[not(section)])"/>
		<xsl:variable name='descriptionID' 
			select="concat('overview', $moduleCount)"/> 
		<xsl:variable name='activitiesID' 
			select="concat('activities', $moduleCount)"/>
		<xsl:choose>
			<xsl:when
				test="(count(sectionContent/*) != 0) and (count(sectionDescription/*) != 0)">
				<div class="module">
					<div class="moduleDescription" id="{$descriptionID}">
						<xsl:apply-templates
							select="sectionDescription/node()" />
					</div>
					<div class="moduleActivities" id='{$activitiesID}'>
						<xsl:apply-templates select="sectionContent/*"
							mode="activities" />
					</div>
				</div>
			</xsl:when>
			<xsl:when test="count(sectionContent/ol) != 0">
				<div class="module">
					<div class="moduleActivities" id='{$activitiesID}'>
						<b>Activities</b>
						<xsl:apply-templates select="sectionContent/*"
							mode="activities" />
					</div>
				</div>
			</xsl:when>
			<xsl:when test="count(sectionDescription/*) != 0">
				<div class="module">
					<div class="moduleDescription" id='{$descriptionID}'>
						<xsl:apply-templates
							select="sectionDescription/node()" />
					</div>
				</div>
			</xsl:when>
			<xsl:when test="count(sectionContent/*) != 0">
				<div class="module">
					<div class="moduleActivities" id='{$activitiesID}'>
						<xsl:apply-templates select="sectionContent/*" />
					</div>
				</div>
			</xsl:when>
		</xsl:choose>
		<xsl:apply-templates select="section" />
	</xsl:template>

	<xsl:template match="ol" mode="activities">
		<ol>
			<xsl:apply-templates select="*" mode="activities" />
		</ol>
	</xsl:template>


	<xsl:template match="li" mode="activities">
		<xsl:choose>
			<xsl:when test="local-name(*[1]) = 'p'">
				<li>
					<xsl:copy-of select="@*" />
					<p>
						<xsl:apply-templates select="*[1]"
							mode="activities2" />
					</p>
					<xsl:apply-templates
						select="*[position &gt; 1]" />
				</li>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:copy-of select="@*" />
					<xsl:apply-templates select="."
						mode="activities2" />
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="p|li" mode="activities2">
		<xsl:variable name="prefixTable"
			select="ancestor::body/presentation//table[2]" />
		<xsl:choose>
			<xsl:when
				test="(local-name(*[1]) = 'a') and (normalize-space(*[1]/preceding-sibling::node()) = '')">
				<xsl:variable name="kind"
					select="normalize-space(a[1]/@href)" />
				<img src="{$baseURL}graphics/{$kind}-kind.png" alt="{$kind}" />
				<xsl:text />
				<xsl:choose>
					<xsl:when test="normalize-space(a[1]) = ''">
						<xsl:variable name="kindTD"
							select="$prefixTable//td[normalize-space() = $kind]" />
						<xsl:if test="$kindTD">
							<xsl:apply-templates
								select="$kindTD/../td[2]/node()" />
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="a[1]/node()" />
						<xsl:text />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates
					select="a[1]/following-sibling::node()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="node()" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="a">
		<xsl:copy>
			<xsl:attribute name="target">
				<xsl:text>_blank</xsl:text>
			</xsl:attribute>
			<xsl:copy-of select="@*[name() != 'target']"/>
			<xsl:apply-templates select="*|text()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="*|text()" mode="activities">
		<xsl:apply-templates select="." />
	</xsl:template>
	

	<xsl:template match="text()">
		<xsl:copy-of select='.' />
	</xsl:template>


	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select='@*' />
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>


