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
package org.eclipse.papyrus.gendoc2.document.parser.documents.helper;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Unzipper;
import org.eclipse.papyrus.gendoc2.document.parser.documents.XMLParser;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document.CONFIGURATION;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The Class OfficeHelper.
 */
public final class OfficeHelper
{

    /**
     * Fill collection.
     * 
     * @param unzipper the unzipper
     * @param headers2 the headers2
     * @param relationshipsHeader the relationships header
     * @param idForDocument the id for document
     */
    public static void fillCollection(Unzipper unzipper, Collection<XMLParser> headers2, String relationshipsHeader, CONFIGURATION idForDocument, String relsFile)
    {
        fillCollection(unzipper, headers2, relationshipsHeader, idForDocument,relsFile, null);
    }
    
    /**
     * Fill collection.
     * 
     * @param unzipper the unzipper
     * @param headers2 the headers2
     * @param relationshipsHeader the relationships header
     * @param idForDocument the id for document
     * @param toSearch the to search
     */
    public static void fillCollection(Unzipper unzipper, Collection<XMLParser> headers2, String relationshipsHeader, CONFIGURATION idForDocument, String relsFile, String toSearch)
    {
        if (headers2 == null)
        {
            headers2 = new LinkedList<XMLParser>();
        }
        if (relationshipsHeader != null)
        {
            File docRels = unzipper.getFile(relsFile);
            XMLParser documentRels = new XMLParser(docRels);
            do 
            {
                if ("Relationship".equals(documentRels.getCurrentNode().getNodeName()))
                {
                    NamedNodeMap attributes = documentRels.getCurrentNode().getAttributes();
                    Node item = attributes.getNamedItem("Type");
                    if (item != null && relationshipsHeader.equals(item.getTextContent()))
                    {
                        Node target = attributes.getNamedItem("Target");
                        Node id = attributes.getNamedItem("Id");
                        if (target != null && id != null)
                        {
                            boolean ok = toSearch == null || (toSearch != null && toSearch.equals(id.getTextContent()));
                            if (ok)
                            {
                                File f = unzipper.getFile(target.getTextContent());
                                if (f.exists())
                                {
                                    headers2.add(new XMLParser(f,idForDocument));
                                    if (toSearch != null)
                                    {
                                        break ;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            while (documentRels.next());
        }
    }
    
    private OfficeHelper ()
    {
        // DO NOTHING
    }
    
}
