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
import java.net.URL;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;

/**
 * Interface DocumentFactory
 * 
 * @author tristan.faure@atosorigin.com
 */
public interface DocumentFactory
{
    /**
     * loadDocument gives the document corresponding
     * 
     * @param documentFile the file to load
     * @deprecated use URL
     */
    Document loadDocument(File documentFile);
    
    /**
     * load the document specified at URL
     * 
     * @param documentFile
     * @return the document
     */
    Document loadDocument(URL documentFile);
    
    /**
     * loadDocument gives the document corresponding
     * 
     * @param documentFile the file to load
     * @param configuration 
     * @ deprecated use URL
     */
    Document loadDocument(File documentFile, Map<CONFIGURATION, Boolean> configuration);
    
    /**
     * loadDocument gives the document corresponding
     * 
     * @param documentFile the file to load
     * @param configuration 
     */
    Document loadDocument(URL documentFile, Map<CONFIGURATION, Boolean> configuration);

}