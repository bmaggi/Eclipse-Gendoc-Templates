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
 * Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.documents.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.Activator;
import org.eclipse.papyrus.gendoc2.document.parser.documents.AbstractZipDocument;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.DocumentFactory;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;

/**
 * Service to manage documents
 */
public class DocumentManager extends AbstractService implements IDocumentManager
{
	private Document docTemplate;
	private Map<Object, Document> documents = new HashMap<Object, Document>();

	public void clear ()
	{
		IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
		for (Document d : this.documents.values())
		{
			if (d instanceof AbstractZipDocument)
			{
				// delete unzipped files
				AbstractZipDocument zip = (AbstractZipDocument)d;
				documentService.clean(zip.getUnzipLocationDocumentFile());
			}
		}
		this.documents.clear();
	}

	/**
	 * Get the initial document template for generation
	 * 
	 * @return the docTemplate
	 */
	public Document getDocTemplate ()
	{
		return this.docTemplate;
	}

	public Document getDocument (File templateDoc)
	{
		Document result = this.documents.get(templateDoc);
		if (result == null)
		{
			// Create a Document from template
			DocumentFactory factory = Activator.getFactoryFromExtension(templateDoc.getAbsolutePath());
			Map<CONFIGURATION, Boolean> conf = this.initConf();
			try
            {
                result = factory.loadDocument(templateDoc.toURI().toURL(), conf);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            if (result != null)
            {
                this.documents.put(templateDoc, result);
            }
		}
		return result;
	}

	/**
	 * set the generation document template
	 * 
	 * @param docTemplate
	 *            the docTemplate to set
	 */
	public void setDocTemplate (Document docTemplate)
	{
		this.docTemplate = docTemplate;
	}

	private Map<CONFIGURATION, Boolean> initConf ()
	{

		Map<CONFIGURATION, Boolean> result = new HashMap<CONFIGURATION, Boolean>();
		result.put(CONFIGURATION.content, true);
		result.put(CONFIGURATION.header, true);
		result.put(CONFIGURATION.footer, true);
		return result;
	}

    public Document getDocument(URL url)
    {
        Document result = this.documents.get(url);
        if (result == null)
        {
            // Create a Document from template
            DocumentFactory factory = Activator.getFactoryFromExtension(url.getPath());
            Map<CONFIGURATION, Boolean> conf = this.initConf();
            result = factory.loadDocument(url, conf);
            this.documents.put(url, result);
        }
        return result;
    }

}
