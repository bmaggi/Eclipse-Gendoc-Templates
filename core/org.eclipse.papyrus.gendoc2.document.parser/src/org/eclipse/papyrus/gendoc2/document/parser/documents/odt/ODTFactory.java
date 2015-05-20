/*****************************************************************************
 * Copyright (c) 2008 Atos Origin.
 * 
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Tristan Faure (Atos Origin) tristan.faure@atosorigin.com -
 * Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.document.parser.documents.odt;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.DocumentFactory;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;

/**
 * A factory for creating ODT objects.
 * 
 * @author tristan.faure@atosorigin.com
 */
public class ODTFactory implements DocumentFactory
{
    /** The instance. */
    private static ODTFactory instance = new ODTFactory();

    /**
     * Default constructor.
     */
    public ODTFactory()
    {
    }

    /**
     * Method for implemented interface : architecture.DocumentFactory
     * 
     * @return the instance
     * 
     * @see architecture.DocumentFactory#getInstance
     */
    public static DocumentFactory getInstance()
    {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.papyrus.doc2model.documents.DocumentFactory#loadDocument(java.io
     * .File)
     */
    public Document loadDocument(File file)
    {
        return loadDocument(file,null);
    }

    public Document loadDocument(File documentFile, Map<Document.CONFIGURATION, Boolean> configuration)
    {
        try
        {
            return loadDocument(documentFile.toURI().toURL(), configuration);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null ;
    }

    public Document loadDocument(URL documentFile)
    {
        return loadDocument(documentFile, null);
    }

    public Document loadDocument(URL documentFile, Map<CONFIGURATION, Boolean> configuration)
    {
        ODTDocument document = new ODTDocument(documentFile,configuration);
        return document;
    }
}
