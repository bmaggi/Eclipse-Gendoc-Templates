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
 * Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *  * Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com - development on the id 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.process.AbstractStepProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidContentException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.ITagExtensionService;
import org.eclipse.papyrus.gendoc2.tags.ITagHandler;
import org.eclipse.papyrus.gendoc2.tags.handlers.ITagHandlerService;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;
import org.eclipse.papyrus.gendoc2.tags.parsers.ITagParserService;
import org.w3c.dom.Node;

/**
 * The Class GendocManager.
 */
public class GendocManager extends AbstractStepProcess
{

    private List<String> tagNames;

    private List<Pattern> tagNamePatterns;

    /**
     * Constructs a new GendocManager
     */
    public GendocManager()
    {
        super();
        this.init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.AbstractProcess#step(org.eclipse.papyrus.document.parser.documents.Document)
     */
    @Override
    protected void step(Document document) throws GenDocException
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        ITagParserService tagParserService = GendocServices.getDefault().getService(ITagParserService.class);

        // List of document nodes that will be replaced
        List<Node> nodesList = new LinkedList<Node>();

        // Got to the next paragraph that contains a tag
        boolean next = this.goToNextTextWithTag(document);
        if (!next)
        {
            return;
        }
        Node currentNode = document.getXMLParser().getCurrentNode();

        // Clean the tags - remove all document tags inside the gendoc tags
        currentNode = documentService.cleanTags(currentNode, this.tagNames);

        document.getXMLParser().setCurrentNode(currentNode);

        nodesList.add(currentNode);

        StringBuffer bufferedText = new StringBuffer(documentService.cleanTagContent(documentService.asText(currentNode), this.tagNames));

        // Split the string into tags
        List<ITag> tags = tagParserService.parse(null, bufferedText.toString(), this.tagNames);

        // while last tag is not closed (IncompleteTag)
        while (next && !(tags.get(tags.size() - 1)).isComplete())
        {
            // Get next paragraph
            next = this.goToNextText(document, bufferedText, nodesList);

            // Get current node
            currentNode = document.getXMLParser().getCurrentNode();

            // Clean the tags - remove all document tags inside the gendoc tags
            currentNode = documentService.cleanTags(currentNode, this.tagNames);
            document.getXMLParser().setCurrentNode(currentNode);
            nodesList.add(currentNode);

            // Concatenate paragraphs
            bufferedText.append(documentService.cleanTagContent(documentService.asText(currentNode), this.tagNames));
            tags = tagParserService.parse(null, bufferedText.toString(), this.tagNames);
        }

        // If the last tag is incomplete
        if (!tags.get(tags.size() - 1).isComplete())
        {
            ITag tag = tags.get(tags.size() - 1);
            
          
            //retrieve the tag
            String tagIdFound = tag.getAttributes().get(RegisteredTags.ID);
            
            if (null == tagIdFound)
            {
                throw new IncompleteTagException(tag.getName());   
            }
            else
            {
                throw new IncompleteTagException(tag.getName(),tagIdFound);  
            }
            
           
        }

        if (tags.size() > 0)
        {
            // Execute the tags
            StringBuffer finalText = this.executeTags(tags);

            // Inject the text in nodes
            Node sibling = documentService.injectNode(currentNode, finalText.toString());

            // Remove old Node
            for (Node node : nodesList)
            {
                node.getParentNode().removeChild(node);
            }
            document.getXMLParser().setCurrentNode(sibling);
        }

        this.worked(1);
    }

    /**
     * Check if one of the tag is present in the given string
     * 
     * @param text
     * @return true is the text contains one the tag
     */
    private boolean containsTag(String text)
    {
        boolean match = false;
        for (Pattern pattern : this.tagNamePatterns)
        {
            if (pattern.matcher(text).matches())
            {
                match = true;
                break;
            }
        }
        return match;
    }

    /**
     * Runs the handler for each tag.
     * 
     * @param tags the tags to execute
     * @return the replacement text
     * @throws GenDocException
     */
    private StringBuffer executeTags(List<ITag> tags) throws GenDocException
    {
        ITagHandlerService tagHandlerService = GendocServices.getDefault().getService(ITagHandlerService.class);

        StringBuffer finalText = new StringBuffer("");
        for (ITag tag : tags)
        {
            ITagHandler tagHandler = tagHandlerService.getHandlerFor(tag);
            if (tagHandler != null)
            {
                finalText.append(tagHandler.run(tag));
            }
            else
            {
                finalText.append(tag.getRawText());
            }

        }

        return finalText;
    }

    /**
     * Run a next on the given document until the current node own text or table
     * 
     * @param document
     * @param nodesToRemove list of nodes that contain tag or tag content and that will be removed in final document
     * @return the return of the next method (false of the document is at the end)
     * @throws InvalidContentException
     */
    private boolean goToNextText(Document document, StringBuffer buffer, List<Node> nodesToRemove) throws InvalidContentException
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);

        this.worked(1);
        boolean next = document.next();
        Object extratedObject = document.get(Document.PROPERTY.text);

        Node currentNode = document.getXMLParser().getCurrentNode();
        while (next && this.isTextKO(nodesToRemove, extratedObject, currentNode))
        {
            this.worked(1);
            next = document.next();
            if (next)
            {
                currentNode = document.getXMLParser().getCurrentNode();
                extratedObject = document.get(Document.PROPERTY.text);
                if ("".equals(extratedObject) && documentService.isPara(currentNode.getNodeName()) && !this.isAlreadyProcessed(nodesToRemove, currentNode))
                {
                    buffer.append(documentService.asText(currentNode));
                    nodesToRemove.add(currentNode);
                }
            }

        }

        return next;
    }

    /**
     * Run a next on the given document until the current node own valid text (para containing "<" and ">")
     * 
     * @param document
     * @return the return of the next method (false of the document is at the end)
     */
    private boolean goToNextTextWithTag(Document document)
    {
        boolean next = true;
        Object extratedObject = document.get(Document.PROPERTY.text);
        while (next && !this.isValidText(document, extratedObject))
        {
            this.worked(1);
            next = document.next();
            if (next)
            {
                extratedObject = document.get(Document.PROPERTY.text);
            }
        }
        return next;
    }

    /**
     * Initialize all the needed fields
     */
    private void init()
    {
        ITagExtensionService tagExtensionService = GendocServices.getDefault().getService(ITagExtensionService.class);
        // Initialize tag names
        this.tagNames = tagExtensionService.getTopLevelTagNames();
        this.tagNamePatterns = new LinkedList<Pattern>();
        for (String label : this.tagNames)
        {
            this.tagNamePatterns.add(Pattern.compile(".*<" + label + ".*>.*"));
            this.tagNamePatterns.add(Pattern.compile(".*</" + label + ".*>.*"));
        }
    }

    /**
     * All nodes that have already been processed are present in the node list. If the current node or one of its parent
     * hierarchy is present in the node list, it is already process
     * 
     * @param nodesList
     * @param node
     * @return true if the node (or one of its parents) has been processed, otherwise false
     */
    private boolean isAlreadyProcessed(List<Node> nodesList, Node node)
    {
        boolean isAlreadyProcessed = false;
        Node parent = node;
        while ((parent != null) && !isAlreadyProcessed)
        {
            if (nodesList.contains(parent))
            {
                isAlreadyProcessed = true;
            }
            parent = parent.getParentNode();
        }
        return isAlreadyProcessed;
    }

    /**
     * Returns true if it is a text valid => the node has not been processed => the text id different from "" or it must
     * be a list or a table
     * 
     * @param nodesToRemove
     * @param extratedObject
     * @param currentNode
     * @return
     */
    private boolean isTextKO(List<Node> nodesToRemove, Object extratedObject, Node currentNode)
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        boolean ko = false;
        boolean isAlreadyProcessed = this.isAlreadyProcessed(nodesToRemove, currentNode);
        if (isAlreadyProcessed)
        {
            ko = true;
        }
        else if ("".equals(extratedObject) && !documentService.isList(currentNode.getNodeName()) && !documentService.isTable(currentNode.getNodeName()))
        {
            ko = true;
        }
        return ko;
    }

    /**
     * Check if the text is valid. A text is valid it is not null if it is a instance of string and if it contains one
     * of the tag to match
     * 
     * @param extratedObject
     * @param extratedObject
     * @return true if it is valid
     */
    private boolean isValidText(Document document, Object extratedObject)
    {
        boolean isValid = false;
        if (!"".equals(extratedObject) && this.containsTag((String) extratedObject))
        {
            isValid = true;
        }
        return isValid;
    }

}
