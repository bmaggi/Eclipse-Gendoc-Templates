<?xml version='1.0'?>
<!-- * Copyright (c) 2010 Atos Origin. * * * All rights reserved. This program 
	and the accompanying materials * are made available under the terms of the 
	Eclipse Public License v1.0 * which accompanies this distribution, and is 
	available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: 
	* Kris Robertson (Atos Origin) kris.robertson@atosorigin.com - Initial API 
	and implementation * -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
	xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" >

	<!-- root (added to ensure single root element) -->

	<xsl:template match="/root">
		<xsl:apply-templates />
	</xsl:template>

	<!-- basic ============================================================= -->

	<xsl:template match="body|html">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="br">
		<xsl:element name="text:line-break" />
	</xsl:template>

	<xsl:template match="h1">
		<text:h text:style-name="Heading_20_1">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="h2">
		<text:h text:style-name="Heading_20_2">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="h3">
		<text:h text:style-name="Heading_20_3">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="h4">
		<text:h text:style-name="Heading_20_4">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="h5">
		<text:h text:style-name="Heading_20_5">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="h6">
		<text:h text:style-name="Heading_20_6">
			<xsl:apply-templates />
		</text:h>
	</xsl:template>

	<xsl:template match="hr">
		<text:p text:style-name="Horizontal_20_Line">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="p">
		<text:p text:style-name="Text_20_Body_20_Single">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<!-- formatting ======================================================== -->

	<xsl:template match="acronym">
		<text:span text:style-name="ACRONYM">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="address">
		<text:p text:style-name="Sender">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="blockquote">
		<text:p text:style-name="Quotations">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="cite">
		<text:span text:style-name="Citation">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="code">
		<text:span text:style-name="Source_20_Text">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="del">
		<text:span text:style-name="DEL">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="dfn">
		<text:span text:style-name="Definition">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="em">
		<text:span text:style-name="Emphasis">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="ins">
		<text:span text:style-name="INS">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="kbd">
		<text:span text:style-name="User_20_Entry">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="q">
		<text:span text:style-name="Q">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="samp">
		<text:span text:style-name="Example">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="strong">
		<text:span text:style-name="Strong_20_Emphasis">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="tt">
		<text:span text:style-name="Teletype">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<xsl:template match="var">
		<text:span text:style-name="Variable">
			<xsl:apply-templates />
		</text:span>
	</xsl:template>

	<!-- problems -->
	<xsl:template match="b|big|i|pre|small|sub|sup">
		<xsl:apply-templates />
	</xsl:template>

	<!-- not supported -->
	<xsl:template match="abbr|bdo">
		<xsl:apply-templates />
	</xsl:template>

	<!-- deprecated -->
	<xsl:template match="center|font|s|strike|u|xmp">
		<xsl:apply-templates />
	</xsl:template>

	<!-- lists ============================================================= -->

	<!-- ordered lists -->
	<xsl:template match="ol">
		<text:list text:style-name="Numbering_20_1">
			<xsl:apply-templates />
		</text:list>
	</xsl:template>

	<xsl:template match="ol/li">
		<text:list-item text:style-name="Number_20_List_20_1">
			<xsl:apply-templates />
		</text:list-item>
	</xsl:template>

	<!-- unordered lists -->
	<xsl:template match="ul">
		<text:list text:style-name="List_20_1">
			<xsl:apply-templates />
		</text:list>
	</xsl:template>

	<xsl:template match="ul/li">
		<text:list-item>
			<xsl:apply-templates />
		</text:list-item>
	</xsl:template>

	<!-- definition lists -->
	<xsl:template match="dl">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="dt">
		<text:p text:style-name="List_20_Heading">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="dd/p">
		<text:p text:style-name="List_20_Contents">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<!-- deprecated -->
	<xsl:template match="dir|menu">
		<xsl:apply-templates />
	</xsl:template>

	<!-- tables ============================================================ -->



	<xsl:template match="table">
		<table:table>
		<!-- Count Columns : try to count from nodes colgroup/col -->
			<xsl:variable name="nbColumns" select="count(colgroup/col)"/>
			<xsl:if test="$nbColumns = 0">
				<!-- When no colgroup, count 1 for each first-line thead/tr or tbody/tr  -->
				<xsl:for-each select="thead/tr|tbody/tr">
					<xsl:sort select="count(*)" data-type="number" order="descending" />
					<xsl:if test="position() = 1">
						<xsl:for-each select="*">
							<table:table-column />
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
			</xsl:if> 			
			<xsl:if test="$nbColumns != 0">
			<!-- Do a for(i=0,i<nbColumns,i++ through the 'forLoop' template -->
			<xsl:call-template name="forLoop">
				<xsl:with-param name="min">
					0
				</xsl:with-param>
				<xsl:with-param name="max">
					<xsl:value-of select="$nbColumns" />
				</xsl:with-param>
			</xsl:call-template>
			</xsl:if> 		
			<xsl:apply-templates />
		</table:table>
	</xsl:template>

	<xsl:template match="thead">
		<table:table-header-rows>
			<xsl:apply-templates />
		</table:table-header-rows>
	</xsl:template>

	<xsl:template match="tr">
		<table:table-row>
			<xsl:if test="@colspan">
				<xsl:attribute name="table:number-columns-spanned">
       <xsl:value-of select="@colspan" />
      </xsl:attribute>
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="table:number-rows-spanned">
       <xsl:value-of select="@rowspan" />
      </xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</table:table-row>
	</xsl:template>

	<xsl:template match="td|th">
		<table:table-cell>
			<xsl:attribute name="table:number-columns-spanned">
       <xsl:value-of select="@colspan" />
      </xsl:attribute>
			<xsl:attribute name="table:number-rows-spanned">
       <xsl:value-of select="@rowspan" />
      </xsl:attribute>
      <xsl:attribute name="table:style-name"><xsl:value-of select="'TableWithBorder'" /></xsl:attribute>
      
			<xsl:apply-templates />
		</table:table-cell>
	</xsl:template>

	<xsl:template match="th/p">
		<text:p text:style-name="Table_20_Heading">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="td/p">
		<text:p text:style-name="Table_20_Contents">
			<xsl:apply-templates />
		</text:p>
	</xsl:template>

	<xsl:template match="col|colgroup|tbody|tfoot">
		<xsl:apply-templates />
	</xsl:template>

	<!-- not supported -->
	<xsl:template match="caption">
	</xsl:template>

	<!-- others ============================================================ -->

	<!-- forms -->
	<xsl:template
		match="button|fieldset|form|input|isindex|label|legend|optgroup|option|select|textarea">
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

	<!-- Utilities ======================================================== -->

	<!-- FOR loop : for(int i=$min;i<$max;i++){display staticText} -->
	<xsl:template name="forLoop">
		<xsl:param name="min"></xsl:param>
		<xsl:param name="max"></xsl:param>
		<!-- display -->
		<table:table-column />
		<!-- end of display -->
		<xsl:if test="number($min) &lt; number($max - 1)">
			<xsl:call-template name="forLoop">
				<xsl:with-param name="min">
					<xsl:value-of select="$min + 1"></xsl:value-of>
				</xsl:with-param>
				<xsl:with-param name="max">
					<xsl:value-of select="$max"></xsl:value-of>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="applyTableBorderStyle">
		<xsl:param name="styleName" />
		<xsl:attribute name="table:style-name"><xsl:value-of select="$styleName" /></xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
