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

import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidContentException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handler for &lt;list&gt; tags.
 * 
 * @author Kris Robertson
 */
public class ListTagHandler extends AbstractTagHandler
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
        // ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        StringBuffer returnValue = new StringBuffer(value);
        String listLabel = documentService.getListLabel();
        if (listLabel != null)
        {
            Pattern listPattern = Pattern.compile("<" + listLabel + "[^<>]*>.*" + "</" + listLabel + ">");

            // Get the text from the first list node to last list
            Matcher m = listPattern.matcher(returnValue);
            // If the is not tag the the text is return as it is
            if (!m.find())
            {
                // removed this warning - many were being generated that serve no real purpose
                // logger.log("No list found inside <list> tag.", IStatus.WARNING);
                return returnValue.toString();
            }
            else
            {
                int indexStart = m.start();
                int indexEnd = returnValue.lastIndexOf("</" + listLabel + ">");
                // Add the length of the tag label </TAG_LABEL> (3 => '<' + '/' + '>')
                indexEnd += listLabel.length() + 3;
                // Extract the string that contains only the list nodes
                String listText = returnValue.substring(indexStart, indexEnd);
                // Create a single node with all the lists
                String singleList = this.addContinueListTagAttribute(documentService, listText);
                // Replace the lists by the created list
                returnValue.replace(indexStart, indexEnd, singleList);
            }
        }
        return returnValue.toString();
    }

    private String addContinueListTagAttribute(IDocumentService documentService, String listText) throws InvalidContentException
    {
        StringBuffer finalListText = new StringBuffer();
        // Transform the text as XML nodes
        String nodes = "<document>" + listText + "</document>";

        nodes = documentService.addNamingSpaces(nodes, "document");
        nodes = documentService.cleanTextTag(nodes);

        Node listNode = documentService.asNode(nodes).getFirstChild();

        // Get the first list
        Node firstList = listNode.getFirstChild();
        finalListText.append(documentService.asText(firstList));
        // Get the id of the first list
        String idList = documentService.getListId(firstList);

        NodeList childNodes = listNode.getChildNodes();

        for (int i = 1; i < childNodes.getLength(); i++)
        {
            Node currentNode = childNodes.item(i);
            if (documentService.isList(currentNode.getNodeName()))
            {
                finalListText.append(documentService.getContinueList(currentNode, idList));
            }
            else
            {
                finalListText.append(documentService.asText(currentNode));
            }
        }
        return finalListText.toString();
    }

}
