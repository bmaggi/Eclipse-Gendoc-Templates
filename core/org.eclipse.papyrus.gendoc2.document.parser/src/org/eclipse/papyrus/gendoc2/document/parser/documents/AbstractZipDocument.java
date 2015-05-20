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
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Abstract ZipDocument unzip OO and word.
 * 
 * @author tlandre
 * 
 */
public abstract class AbstractZipDocument extends AbstractDocument implements ZipDocument
{

    /** The unzipper. */
    private Unzipper unzipper = null;
    /** the zipper */
    private Zipper zipper = null;
    
    private XmlParsers listOfXmlParsers ;

    public AbstractZipDocument(File document) throws IOException
    {
        this(document,null);
    }
    
    public AbstractZipDocument(URL document)
    {
        this(document,null);
    }

    public AbstractZipDocument(File documentFile, Map<Document.CONFIGURATION, Boolean> configuration) throws IOException
    {
        this (documentFile.toURI().toURL(),configuration);
    }
    
    public AbstractZipDocument(URL documentFile, Map<Document.CONFIGURATION, Boolean> configuration)
    {
        super(documentFile,configuration);
        unzipper = new Unzipper(documentFile);
        getUnzipper().unzip();
        zipper = new Zipper(unzipper.getUnzipDocumentFile());
        listOfXmlParsers = new XmlParsers(this.getXmlParsers(Document.CONFIGURATION.content));
        if (configuration != null)
        {
            for (Document.CONFIGURATION c : configuration.keySet())
            {
                if (c != Document.CONFIGURATION.content)
                {
                    if (configuration.get(c))
                    {
                        listOfXmlParsers.addAll(getXmlParsers(c));
                    }
                }
            }
        }
    }
    
    /**
     * Get XML Parser depending of
     * @param idForDocument
     * @return the collection of XML Parsers
     */
    protected abstract Collection<XMLParser> getXmlParsers(Document.CONFIGURATION idForDocument) ;
    
    public XMLParser getXMLParser()
    {
        return listOfXmlParsers.getCurrent() ;
    }


    public boolean next()
    {
        return listOfXmlParsers.next() ;
    }
    
    public boolean jumpToNextFile()
    {
        return listOfXmlParsers.jumpToNextFile();
    }
    
    public void zipToLocation(String locationToZip)
    {
        getZipper().zip(locationToZip);
    }

    public File getUnzipLocationDocumentFile()
    {
        return getUnzipper().getUnzipDocumentFile();
    }

    protected Unzipper getUnzipper()
    {
        return unzipper;
    }

    protected Zipper getZipper()
    {
        return zipper;
    }

    public XmlParsers getListOfXmlParsers()
    {
        return listOfXmlParsers;
    }

    public void jumpToStart()
    {
        listOfXmlParsers.jumpToStart();
    }

}
