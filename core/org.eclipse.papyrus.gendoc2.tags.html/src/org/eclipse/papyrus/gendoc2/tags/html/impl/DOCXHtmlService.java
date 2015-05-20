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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;
import org.eclipse.papyrus.gendoc2.services.docx.DOCXAdditionalResourceService;
import org.eclipse.papyrus.gendoc2.services.docx.DOCXDocumentService;
import org.eclipse.papyrus.gendoc2.services.utils.DeleteFileRunnable;
import org.eclipse.papyrus.gendoc2.tags.html.Activator;
import org.eclipse.papyrus.gendoc2.tags.html.IHtmlService;
import org.eclipse.papyrus.gendoc2.tags.html.tidy.Tidy;

/**
 * The docx html service is used to include html in docx documents as external chunks (for word 2007)
 * or convert it in docx tags by running an xsl transform (for word 2003).
 * 
 * @author Kris Robertson
 */
public class DOCXHtmlService extends AbstractService implements IHtmlService
{

    private List<String> tempFiles = new LinkedList<String>();
    
    private boolean use2003Compatibility = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    @Override
    public void clear()
    {
        for (String path : this.tempFiles)
        {
            File file = new File(path);
            file.delete();
        }
        this.tempFiles.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.html.IHtmlService#convert(java.lang.String)
     */
    public String convert(String html)
    {
        if (html.length() > 0)
        {
            if (use2003Compatibility)
            {
                return generateDOCXTagsFromHtml(html);
            }
            else
            {
                final String filePath = this.createHtmlFile(html);
                IRegistryService registry = GendocServices.getDefault().getService(IRegistryService.class);
                registry.addCleaner(new DeleteFileRunnable(new File(filePath)));
                if (filePath != null)
                {
                    DOCXDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
                    DOCXAdditionalResourceService additonalResourceService = (DOCXAdditionalResourceService) documentService.getAdditionalResourceService();
                    String id = additonalResourceService.includeFile(filePath);
                    String output = "&lt;drop/&gt;</w:p><w:altChunk xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + id + "\" />";
                    return output;
                }
            }
        }
        return "";
    }

    private String generateDOCXTagsFromHtml(String html)
    {
        String output = "";
        try
        {
            Tidy tidy = new Tidy();
            tidy.setWraplen(0);
            tidy.setPrintBodyOnly(true);
            tidy.setEncloseBlockText(true);
            tidy.setXmlOut(true);
            tidy.setIndentContent(false);
            tidy.setShowWarnings(false);
            tidy.setQuiet(true);

            StringReader reader = new StringReader(html);
            StringWriter writer = new StringWriter();
            tidy.parse(reader, writer);
            String cleanedXhtml = writer.toString();
            cleanedXhtml = cleanedXhtml.replace("\r", "");
            cleanedXhtml = cleanedXhtml.replace("\n", "");

            cleanedXhtml = "<root>" + cleanedXhtml + "</root>";
            StringWriter outputWriter = new StringWriter();
            StreamSource xslSource = new StreamSource(FileLocator.openStream(Platform.getBundle(Activator.PLUGIN_ID), new Path("resources/html2docx.xsl"), false));
            StreamSource inputSource = new StreamSource(new StringReader(cleanedXhtml));
            StreamResult outputResult = new StreamResult(outputWriter);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(inputSource, outputResult);
            output = outputWriter.toString();
            output = "&lt;drop/&gt;</w:t></w:r></w:p>" + output;
        }
        catch (IOException e)
        {
            ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Unable to open XSL file for HTML transformation.", Status.ERROR);
        }
        catch (TransformerConfigurationException e)
        {
            ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Error in HTML transformer configuration.", Status.ERROR);
        }
        catch (TransformerFactoryConfigurationError e)
        {
            ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Error in HTML transformer factory configuration.", Status.ERROR);
        }
        catch (TransformerException e)
        {
            ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Error transforming HTML.", Status.ERROR);
        }
        return output;
    }

    /**
     * Creates an html file to be included in the document
     * 
     * @param html the html to include in the file
     * @return the file path or null if the file could not be created
     */
    private String createHtmlFile(String html)
    {
        String path = Activator.getDefault().getStateLocation().toOSString() + File.separator + EcoreUtil.generateUUID() + ".xhtml";
        try
        {
            Tidy tidy = new Tidy();
            tidy.setXHTML(true);
            tidy.setShowWarnings(false);
            tidy.setQuiet(true);
            StringReader reader = new StringReader(html);
            FileWriter writer = new FileWriter(path);
            tidy.parse(reader, writer);
            this.tempFiles.add(path);
        }
        catch (IOException e)
        {
            ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Unable to create html file.", Status.ERROR);
            return null;
        }
        return path;
    }

    public void setVersion(String version)
    {
        if (version != null && version.equals("msw2003"))
        {
            use2003Compatibility = true;
        }
    }

	public void addAdditionalStyles(Document document) {
		//Do nothing (In docx, styles from HTML are managed automatically.
	}

}
