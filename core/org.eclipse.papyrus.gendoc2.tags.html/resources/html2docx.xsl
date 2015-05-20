<?xml version='1.0'?>
<!--
 * Copyright (c) 2010 Atos Origin.
 * 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com - Initial API and implementation
 * 
  -->
<xsl:stylesheet	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">

	<xsl:template match="/root">
		<xsl:apply-templates select="p | ul | ol"/>
	</xsl:template>

	<!-- basic ============================================================= -->

	<xsl:template match="p">
		<xsl:choose>
			<xsl:when test="parent::root">
				<w:p>
					<w:r>
						<w:t xml:space="preserve"><xsl:apply-templates /></w:t>
					</w:r>
				</w:p>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="ul">
		<xsl:if test="parent::root">
			<xsl:for-each select="li">
				<w:p>
			    	<w:r>
			        	<w:t xml:space="preserve">&#09;&#8226; <xsl:apply-templates /></w:t>
			        </w:r>
				</w:p>
			</xsl:for-each>
	    </xsl:if>
	</xsl:template>
	
	<xsl:template match="ol">
		<xsl:if test="parent::root">
			<xsl:for-each select="li">
				<w:p>
			    	<w:r>
			        	<w:t xml:space="preserve">&#09;<xsl:value-of select="count(preceding-sibling::*) + 1"/>. <xsl:apply-templates /></w:t>
			        </w:r>
		    	</w:p>
			</xsl:for-each>
	    </xsl:if>
	</xsl:template>

	
	<xsl:template match="br">
	    <xsl:text disable-output-escaping="yes">&#60;/w:t&#62;&#60;/w:r&#62;</xsl:text>
	    <w:br />
	    <xsl:text disable-output-escaping="yes">&#60;w:r&#62;&#60;w:t xml:space="preserve"&#62;</xsl:text>
	</xsl:template>
	
	<xsl:template match="b | strong">
		    <xsl:text disable-output-escaping="yes">&#60;/w:t&#62;&#60;/w:r&#62;</xsl:text>
		    <w:r>
		        <w:rPr>
		            <w:b />
		            <xsl:call-template name="output-character-formatting"/>
		        </w:rPr>
		        <w:t xml:space="preserve"><xsl:apply-templates /></w:t>
		    </w:r>
		    <xsl:text disable-output-escaping="yes">&#60;w:r&#62;</xsl:text>
		    <w:rPr>
		        <xsl:call-template name="output-character-formatting" />
		    </w:rPr>
		    <xsl:text disable-output-escaping="yes">&#60;w:t xml:space="preserve"&#62;</xsl:text>
	</xsl:template>
	
	<xsl:template match="i | em">
		    <xsl:text disable-output-escaping="yes">&#60;/w:t&#62;&#60;/w:r&#62;</xsl:text>
		    <w:r>
		        <w:rPr>
		            <w:i />
		            <xsl:call-template name="output-character-formatting"/>
		        </w:rPr>
		        <w:t xml:space="preserve"><xsl:apply-templates /></w:t>
		    </w:r>
		    <xsl:text disable-output-escaping="yes">&#60;w:r&#62;</xsl:text>
		    <w:rPr>
		        <xsl:call-template name="output-character-formatting" />
		    </w:rPr>
		    <xsl:text disable-output-escaping="yes">&#60;w:t xml:space="preserve"&#62;</xsl:text>
	</xsl:template>
	
	<xsl:template match="u">
		    <xsl:text disable-output-escaping="yes">&#60;/w:t&#62;&#60;/w:r&#62;</xsl:text>
		    <w:r>
		        <w:rPr>
		            <w:u w:val="single" />
		            <xsl:call-template name="output-character-formatting"/>
		        </w:rPr>
		        <w:t xml:space="preserve"><xsl:apply-templates /></w:t>
		    </w:r>
		    <xsl:text disable-output-escaping="yes">&#60;w:r&#62;</xsl:text>
		    <w:rPr>
		        <xsl:call-template name="output-character-formatting" />
		    </w:rPr>
		    <xsl:text disable-output-escaping="yes">&#60;w:t xml:space="preserve"&#62;</xsl:text>
	</xsl:template>
	
	<xsl:template name="output-character-formatting">
	    <xsl:if test="ancestor::i or ancestor::em">
	        <w:i />
	    </xsl:if>
	    <xsl:if test="ancestor::b or ancestor::strong">
	        <w:b />
	    </xsl:if>
	    <xsl:if test="ancestor::u">
	        <w:u w:val="single" />
	    </xsl:if>
	</xsl:template>
	
	<!-- forms -->
	<xsl:template match="button|fieldset|form|input|isindex|label|legend|optgroup|option|select|textarea">
	</xsl:template>

	<!-- frames -->
	<xsl:template match="frame|frameset|iframe|noframes">
	</xsl:template>

	<!-- images -->
	<xsl:template match="area|img|map">
	</xsl:template>

	<!-- links -->
	<xsl:template match="a|link">
		<xsl:apply-templates />
	</xsl:template>

	<!-- styles -->
	<xsl:template match="div|span|style">
		<xsl:apply-templates />
	</xsl:template>

	<!-- meta info -->
	<xsl:template match="base|basefont|head|meta|title">
	</xsl:template>

	<!-- programming -->
	<xsl:template match="applet|noscript|object|param|script">
	</xsl:template>

</xsl:stylesheet>
