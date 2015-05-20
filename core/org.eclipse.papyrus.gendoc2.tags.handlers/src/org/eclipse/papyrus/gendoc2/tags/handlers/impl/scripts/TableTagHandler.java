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
package org.eclipse.papyrus.gendoc2.tags.handlers.impl.scripts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidContentException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handler for &lt;table&gt; tags.
 * 
 * @author Kris Robertson
 */
public class TableTagHandler extends AbstractTagHandler
{

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler#run(org.eclipse.papyrus.gendoc2.tags.ITag)
     */
    @Override
    public String run(ITag tag) throws GenDocException
    {
        String value = super.run(tag);

        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        String tableLabel = documentService.getTableLabel();
        Pattern tablePattern = Pattern.compile("<" + tableLabel + "[^<>]*>.*" + "</" + tableLabel + ">");
        StringBuffer returnValue = new StringBuffer(value);

        // Get the text from the first table node to last table
        Matcher m = tablePattern.matcher(returnValue);
        // If the is not tag the the text is return as it is
        if (!m.find())
        {
            logger.log("No table found inside <table> tag.", IStatus.WARNING);
            return returnValue.toString();
        }
        else
        {
            int indexStart = m.start();
            int indexEnd = returnValue.lastIndexOf("</" + tableLabel + ">");
            // Add the length of the tag label </TAG_LABEL> (3 => '<' + '/' + '>')
            indexEnd += tableLabel.length() + 3;
            // Extract the string that contains only the table nodes
            String tableText = returnValue.substring(indexStart, indexEnd);
            // Create a single node with all the tables
            String singleTable = this.concatTables(documentService, tableText);
            // Replace the tables by the created table
            returnValue.replace(indexStart, indexEnd, singleTable);
        }
        return returnValue.toString();
    }

    /**
     * Create a single table from all the tables in the given string => Find the first table and then insert all the row
     * from other tables in the first one.
     * 
     * @param tableText the text containing the tables
     * 
     * @return the created table
     */
    private String concatTables(IDocumentService documentService, String tableText) throws InvalidContentException
    {
        // Transform the text as XML nodes
        String nodes = "<document>" + tableText + "</document>";

        nodes = documentService.addNamingSpaces(nodes, "document");

        Node tableNode = documentService.asNode(nodes).getFirstChild();

        // Get the first that must be a table
        // All the row from other table will be inserted in this one
        Node mainTable = tableNode.getFirstChild();

        NodeList childNodes = tableNode.getChildNodes();

        // Look over all the child node (except the first one that is the main table)
        for (int i = 1; i < childNodes.getLength(); i++)
        {
            Node currentNode = childNodes.item(i);
            // If the child is a table
            if (documentService.isTable(currentNode.getNodeName()))
            {
                // Add all its row child to the main table
                NodeList rowChildNodes = currentNode.getChildNodes();
                while (rowChildNodes.getLength() > 0)
                {
                    Node rowNode = rowChildNodes.item(0);
                    // if it a row
                    if (documentService.isRow(rowNode.getNodeName()))
                    {
                        // Add it to the main table
                        mainTable.appendChild(rowNode);
                    }
                    else
                    {
                        // discard it
                        rowNode.getParentNode().removeChild(rowNode);
                    }
                }
            }
        }
        return documentService.asText(mainTable);
    }

}
