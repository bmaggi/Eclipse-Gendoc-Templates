/*****************************************************************************
 * Copyright (c) 2009 ATOS ORIGIN INTEGRATION.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Tristan FAURE (ATOS ORIGIN INTEGRATION) tristan.faure@atosorigin.com - Initial API and implementation
 *
  *****************************************************************************/
package org.eclipse.papyrus.gendoc2.document.parser.documents.ods;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.DocumentFactory;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;

/**
 * A factory for creating ODS objects.
 */
public class ODSFactory implements DocumentFactory
{

    /**
     * Instantiates a new oDS factory.
     */
    public ODSFactory()
    {
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.document.parser.documents.DocumentFactory#loadDocument(java.io.File)
     */
    public Document loadDocument(File documentFile)
    {
        return loadDocument(documentFile,null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.document.parser.documents.DocumentFactory#loadDocument(java.io.File, java.util.Map)
     */
    public Document loadDocument(File documentFile, Map<CONFIGURATION, Boolean> configuration)
    {
        try
        {
            return loadDocument(documentFile.toURI().toURL(),configuration);
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
        return new ODSDocument(documentFile, configuration);
    }

}
