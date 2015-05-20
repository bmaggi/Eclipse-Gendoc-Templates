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
package org.eclipse.papyrus.gendoc2.document.parser.documents.csv;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.DocumentFactory;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;

/**
 * A factory for creating CSV objects.
 */
public class CSVFactory implements DocumentFactory
{

    public CSVFactory()
    {
    }

    public Document loadDocument(File documentFile)
    {
        try
        {
            return loadDocument(documentFile.toURI().toURL());
        }
        catch (MalformedURLException e)
        {
        }
        return null ;
    }

    public Document loadDocument(File documentFile, Map<CONFIGURATION, Boolean> configuration)
    {
        try
        {
            return loadDocument(documentFile.toURI().toURL());
        }
        catch (MalformedURLException e)
        {
        }
        return null;
    }

    public Document loadDocument(URL documentFile)
    {
        return loadDocument(documentFile, null);
    }

    public Document loadDocument(URL documentFile, Map<CONFIGURATION, Boolean> configuration)
    {
        return new CSVDocument(documentFile);
    }

}
