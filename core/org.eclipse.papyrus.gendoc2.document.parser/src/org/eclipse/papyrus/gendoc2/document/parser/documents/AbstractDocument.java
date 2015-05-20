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
package org.eclipse.papyrus.gendoc2.document.parser.documents;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Abstract Document unzip OO and word.
 * 
 * @author tristan.faure@atosorigin.com
 */
public abstract class AbstractDocument implements Document
{
    
    private URL file;

    /**
     * Instantiates a new abstract document.
     * 
     * @param document the document
     */
    public AbstractDocument(File pFile)
    {
        this(pFile,null);
    }
    
    public AbstractDocument(URL pFile)
    {
        this(pFile,null);
    }


    public AbstractDocument(File documentFile, Map<Document.CONFIGURATION, Boolean> configuration)
    {
        try
        {
            this.file = documentFile.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
        }
    }
    
    public AbstractDocument(URL documentFile, Map<Document.CONFIGURATION, Boolean> configuration)
    {
        this.file = documentFile;
    }

    /** 
     * @see org.eclipse.papyrus.doc2model.documents.Document#getDocumentFile()
     */
    public File getDocumentFile()
    {
        return new File(file.getFile());
    }

    /**
     * @see org.eclipse.papyrus.doc2model.documents.Document#getColumnNumber()
     */
    public int getColumnNumber()
    {
        return 0;
    }

    public URL getDocumentURL()
    {
        return file;
    }

    public String getPath()
    {
        return file.getPath();
    }

}
