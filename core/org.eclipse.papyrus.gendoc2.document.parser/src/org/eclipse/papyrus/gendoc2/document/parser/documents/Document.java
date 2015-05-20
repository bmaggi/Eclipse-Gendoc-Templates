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

/**
 * The Interface Document.
 * 
 * @author tristan.faure@atosorigin.com
 */
public interface Document
{
    /**
     * Enumeration defining elements to config a parsing 
     * Developer has to precise if he wants to manage header or footer
     * Classes derived from document have to guarante access to xml parsers corresponding
     */
    enum CONFIGURATION {undefined, content, header, footer, comment} ;
    
    /**
     * Enumeration to get property from document
     */
    enum PROPERTY {style, text, column, row} ;
    
    /**
     * Next.
     * 
     * @return false if there is no next
     */
    boolean next();
    
    /**
     * Next file in list of xmls.
     * 
     * @return false if there is no next
     */
    boolean jumpToNextFile();

    /**
     * Gets the value corresponding to the property
     * 
     * @param property the property
     * 
     * @return the string
     */
    Object get (PROPERTY property) ;
    
    /**
     * Gets the text corresponding to current style.
     * 
     * @return the text corresponding to current style
     */
    String getTextCorrespondingToCurrentStyle();

    /**
     * Return the XML Parser object containing all xml information
     * if your document doesn't contain XML Parser please use {@link XMLParser.NullXMLParser}
     * 
     * @return the XML Parser
     */
    XMLParser getXMLParser();

    /**
     * Get the input file document
     * 
     * @return the file
     * @deprecated File are no longer supported
     */
    File getDocumentFile();
    
    /**
     * Get the URL of the document
     * 
     * @return the URL of the document
     */
    URL getDocumentURL () ;
    
    /**
     * Returs to the first xlm file
     */
    void jumpToStart();

    /**
     * Get the path independant of the type of resource
     * @return the path
     */
    String getPath();
    
}