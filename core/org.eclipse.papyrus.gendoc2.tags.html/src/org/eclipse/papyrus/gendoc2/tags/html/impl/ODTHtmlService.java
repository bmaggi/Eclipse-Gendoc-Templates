/*****************************************************************************
 * Copyright (c) 2010 Atos Origin.
 * 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Kris Robertson (Atos Origin) kris.robertson@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.html.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.tags.html.Activator;
import org.eclipse.papyrus.gendoc2.tags.html.IHtmlService;
import org.eclipse.papyrus.gendoc2.tags.html.tidy.Tidy;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The odt html service is used to include html in odt documents by running an
 * xsl transform.
 * 
 * @author Kris Robertson
 */
public class ODTHtmlService extends AbstractService implements IHtmlService {

	/** Name of the file containing the document content */
	private static final String content = "content.xml";

	protected String SEPARATOR = "/";

	public String convert(String value) {
		String output = "";
		try {
			Tidy tidy = new Tidy();
			tidy.setDocType("omit");
			tidy.setEncloseBlockText(true);
			tidy.setLogicalEmphasis(true);
			tidy.setPrintBodyOnly(true);
			tidy.setXmlOut(true);
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);

			StringReader reader = new StringReader(value);
			StringWriter writer = new StringWriter();
			tidy.parse(reader, writer);
			value = writer.toString();

			value = "<root>" + value + "</root>";
			StringWriter outputWriter = new StringWriter();
			StreamSource xslSource = new StreamSource(FileLocator.openStream(
					Platform.getBundle(Activator.PLUGIN_ID), new Path(
							"resources/html2odt.xsl"), false));
			StreamSource inputSource = new StreamSource(new StringReader(value));
			StreamResult outputResult = new StreamResult(outputWriter);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(xslSource);
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.transform(inputSource, outputResult);
			output = outputWriter.toString();
			output = "&lt;drop/&gt;</text:h></text:p>" + output;
		} catch (IOException e) {
			ILogger logger = GendocServices.getDefault().getService(
					ILogger.class);
			logger.log("Unable to open XSL file for HTML transformation.",
					Status.ERROR);
		} catch (TransformerConfigurationException e) {
			ILogger logger = GendocServices.getDefault().getService(
					ILogger.class);
			logger.log("Error in HTML transformer configuration.", Status.ERROR);
		} catch (TransformerFactoryConfigurationError e) {
			ILogger logger = GendocServices.getDefault().getService(
					ILogger.class);
			logger.log("Error in HTML transformer factory configuration.",
					Status.ERROR);
		} catch (TransformerException e) {
			ILogger logger = GendocServices.getDefault().getService(
					ILogger.class);
			logger.log("Error transforming HTML.", Status.ERROR);
		}
		return output;
	}

	public void setVersion(String version) {
	}

	public void addAdditionalStyles(Document document) {

		// FIXME Huge hack to have borders in ODT Html tables
		String TABLEBORDERSTYLE = "TableWithBorder";

		try {
			document.jumpToStart();
			while (!content.equals(document.getXMLParser().getXmlFile()
					.getName())) {
				document.jumpToNextFile();
			}
			org.w3c.dom.Document documentNode = (org.w3c.dom.Document)document.getXMLParser().getDocument();

			Node stylesNode = getNodeFromXPath(documentNode,
					"//office:automatic-styles");
			
			String STYLE_NAMESPACE = "xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\"";
			Element newStyle = documentNode.createElementNS(STYLE_NAMESPACE,"style:style");
			newStyle.setAttributeNS(STYLE_NAMESPACE,"style:name",TABLEBORDERSTYLE);
			newStyle.setAttributeNS(STYLE_NAMESPACE,"style:family","table-cell");
			Element styleProps = documentNode.createElementNS(STYLE_NAMESPACE,"style:table-cell-properties");
			String FO_NAMESPACE = "xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\"";
			styleProps.setAttributeNS(FO_NAMESPACE,"fo:border-bottom","0.002cm solid #000000");
			styleProps.setAttributeNS(FO_NAMESPACE,"fo:border-left","0.002cm solid #000000");
			styleProps.setAttributeNS(FO_NAMESPACE,"fo:border-right","0.002cm solid #000000");
			styleProps.setAttributeNS(FO_NAMESPACE,"fo:border-top","0.002cm solid #000000");
			newStyle.appendChild(styleProps);
			stylesNode.appendChild(newStyle);
			
			
		} catch (DOMException e) {
			e.printStackTrace();
		}

	}

	private Node getNodeFromXPath(Node start, String expression) {
		IDocumentService documentService = GendocServices.getDefault()
				.getService(IDocumentService.class);
		ILogger logger = GendocServices.getDefault().getService(ILogger.class);
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(documentService.getNameSpaceContext());
			XPathExpression expr = xpath.compile(expression);
			Object result = expr.evaluate(start, XPathConstants.NODE);
			if (result != null && result instanceof Node) {
				return (Node) result;
			}
		} catch (XPathExpressionException e) {
			logger.log("Invalid XPath expression : " + expression + "\n"
					+ e.getStackTrace().toString(), IStatus.ERROR);
		}
		return null;
	}
	

}
