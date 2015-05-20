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
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.documents;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.helper.XMLHelper;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidContentException;
import org.eclipse.papyrus.gendoc2.tags.parsers.TagParserConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Document service for XML document types (ex : ODT, DOCX are based on XML documents)
 */
public abstract class XMLDocumentService implements IDocumentService
{
    /**
     * Character that is the start of a tag in XML language (<code>&lt;</code>)
     */
    public static String XML_TAG_START = "<";

    /**
     * Character that is the end of a tag in XML language (<code>&gt;</code>)
     */
    public static String XML_TAG_END = ">";

    public static String XML_AUTOCLOSING_TAG_END = "/>";

    private Transformer trans;

    private final TransformerFactory transFactory = TransformerFactory.newInstance();

    private final Pattern pattern = Pattern.compile("<?.*?>");

    private Document document;

    /**
     * Regex that matched a tag &lt;myTag&gt;
     */
    private final Pattern patternTag = Pattern.compile("<[^<>]+>");

    /**
     * Regex that matched a closing tag &lt;/myTag&gt;
     */
    private final Pattern patternTagClose = Pattern.compile("</[^<>]+>");

    /**
     * Regex that matched a single tag <myTag/>
     */
    private final Pattern patternTagSingle = Pattern.compile("<[^<>]+/>");

    /**
     * Create a new XML Document service
     */
    public XMLDocumentService()
    {
        super();
        init();
    }

    /**
     * Create a new XML Document service
     */
    public XMLDocumentService(Document document)
    {
        super();
        init();
        this.document = document;
    }

    private void init()
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        // Initialize transformer
        try
        {
            trans = transFactory.newTransformer();
        }
        catch (TransformerConfigurationException e)
        {
            logger.log(e.getMessageAndLocation(), IStatus.ERROR);
        }
    }

    /**
     * Get the nameSpaceContext of this document type
     * 
     * @return the nameSpaceContext
     */
    public abstract NamespaceContext getNameSpaceContext();

    /**
     * Get the name space URL
     * 
     * @return the URL of the specific name space
     */
    public abstract String getNamingSpaceURL();

    public void clear()
    {
        // do nothing
    }

    /**
     * Transform the given text in a NEW Document Node
     * 
     * @param text to transform
     * @return the created node
     */
    public Node asNode(String text) throws InvalidContentException
    {
        DOMResult outputTarget = null;
        try
        {
            outputTarget = new DOMResult();
            trans.transform(new StreamSource(new StringReader(text)), outputTarget);

        }
        catch (TransformerException e)
        {
            throw new InvalidContentException(text, e);
        }

        return outputTarget.getNode();
    }

    /**
     * Transform the given node as text.
     * 
     * @param nodeBegin the node begin
     * @return the string
     * @throws InvalidContentException
     * @throws TransformerFactoryConfigurationError the transformer factory configuration error
     */
    public String asText(Node nodeBegin) throws InvalidContentException
    {
        StringWriter stringOut = new StringWriter();

        String xml = "";
        try
        {
            trans.transform(new DOMSource(nodeBegin), new StreamResult(stringOut));
            xml = pattern.matcher(stringOut.toString()).replaceFirst("");
            xml = Pattern.compile(" " + getNamingSpaceURL()).matcher(xml).replaceAll("");
        }
        catch (TransformerException e)
        {
            throw new InvalidContentException("Node " + nodeBegin.getLocalName() + " can not be transform", e);
        }
        return xml;
    }

    public abstract String getTextStyle();

    public abstract boolean isPara(String label);

    public String addNamingSpaces(String nodes)
    {

        String firstNodeName = null;
        String newNodes = nodes;
        Matcher m = Pattern.compile("<[^ >]*( |>)").matcher(nodes);
        if (m.find())
        {
            firstNodeName = m.group().replaceAll("<| |>", "");
            newNodes = newNodes.replaceAll("<" + firstNodeName + " ", "<" + firstNodeName + " " + getNamingSpaceURL() + " ");
            newNodes = newNodes.replaceAll("<" + firstNodeName + ">", "<" + firstNodeName + " " + getNamingSpaceURL() + ">");
        }

        for (String textNode : getTextTagLabels())
        {
            if (firstNodeName != null && !firstNodeName.equals(textNode))
            {
                newNodes = newNodes.replaceAll("<" + textNode + " ", "<" + textNode + " " + getNamingSpaceURL() + " ");
                newNodes = newNodes.replaceAll("<" + textNode + ">", "<" + textNode + " " + getNamingSpaceURL() + ">");
            }
        }
        return newNodes;
    }

    public String addNamingSpaces(String nodes, String label)
    {
        String newNodes = nodes;
        newNodes = newNodes.replaceAll("<" + label + " ", "<" + label + " " + getNamingSpaceURL() + " ");
        newNodes = newNodes.replaceAll("<" + label + ">", "<" + label + " " + getNamingSpaceURL() + ">");

        return newNodes;
    }

    public abstract String[] getTextTagLabels();

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IDocumentService#injectNode(org.w3c.dom.Node, java.lang.String)
     */
    public Node injectNode(Node nodeToBeReplaced, String nodeContent) throws InvalidContentException
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        String newNodeContent = nodeContent;
        newNodeContent = addNamingSpaces("<document>" + newNodeContent + "</document>", "document");
        newNodeContent = cleanTextTag(newNodeContent);

        logger.log("INJECT Node :" + newNodeContent, ILogger.DEBUG);

        // Transform "newNodeContent" text into a DOMResult
        DOMResult outputTarget = null;
        try
        {
            outputTarget = new DOMResult(nodeToBeReplaced);
            trans.transform(new StreamSource(new StringReader(newNodeContent)), outputTarget);

        }
        catch (TransformerException e)
        {
            logger.log("Node can not be created.\n" + e.toString(), IStatus.ERROR);
            logger.log("Node in error :\n" + newNodeContent + "|||||||", IStatus.ERROR);
            throw new InvalidContentException("Your text seems to contain special characters. Try using method 'clean' from external bundle 'commons' for the different model elements found", e);
        }

        // nodeToReplace has a new child (lastChild) containing newNodeContent content
        NodeList nodes = nodeToBeReplaced.getLastChild().getChildNodes();
        List<Node> inserted = new ArrayList<Node>();
        for (int i = 0; i < nodes.getLength();/*
                                               * No modification of the i value : nodes are dynamically removed from
                                               * nodeList when inserted
                                               */)
        {
            inserted.add(nodeToBeReplaced.getParentNode().insertBefore(nodes.item(i), nodeToBeReplaced));
        }
        return inserted.get(inserted.size() - 1);
    }

    public abstract IAdditionalResourceService getAdditionalResourceService();

    /**
     * 
     * The result must be "&lt;gendoc&gt;CONTENT&lt;/gendoc&gt;" or
     * "&lt;context model='$incose' element='INCOSE/System' importedBundles='uml;sysml' /&gt;"
     * 
     * @param currentNode
     * @param tagLabels
     * @throws InvalidContentException
     */
    public Node cleanTags(Node currentNode, List<String> tagLabels) throws InvalidContentException
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        // 1. Find next node with starting tag character

        Node firstNode = findNodeWithOneTag(currentNode);
        Node next = cleanTags(currentNode, tagLabels, firstNode);
        if (currentNode == null || currentNode.getParentNode() == null)
        {
            logger.log("XMLDocumentService.cleanTags : currentNode has been replaced.", ILogger.DEBUG);
            currentNode = next;
        }
        Node nextStartNode;
        // Do the same thing until end of tag is found.
        while (next != null)
        {
            nextStartNode = findNodeWithStartTag(next, currentNode);
            next = cleanTags(currentNode, tagLabels, nextStartNode);
            if (currentNode == null || currentNode.getParentNode() == null)
            {
                logger.log("XMLDocumentService.cleanTags : currentNode has been replaced.", ILogger.DEBUG);
                currentNode = next;
            }
        }
        return currentNode;

    }

    public String cleanTagContent(String text, List<String> labels)
    {
        if (labels == null || labels.isEmpty())
        {
            return text;
        }
        
        // Get list of tag patterns
        List<Pattern> patternsToClean = new ArrayList<Pattern>(labels.size() * 2);
        List<Pattern> patternsToMatch = new ArrayList<Pattern>(labels.size() * 2);
        for (String label : labels)
        {
            patternsToClean.add(Pattern.compile("(&lt;" + label + "[^&;]*&gt;[^&]&lt;/" + label + "[^&;]*&gt;)"));
            patternsToClean.add(Pattern.compile("&lt;" + label + "[^&;]*/&gt;"));
            
            patternsToMatch.add(Pattern.compile("(&lt;" + label + "[^&;]*&gt;|&lt;/" + label + "[^&;]*&gt;)"));
            patternsToMatch.add(Pattern.compile("&lt;" + label + "[^&;]*/&gt;"));
        }
        
        // Get a full regex with all patterns
        String patternToMatch = "";
        for (Pattern p : patternsToMatch)
        {
            patternToMatch += "(" + p + ")|";
        }
        patternToMatch = patternToMatch.substring(0, patternToMatch.length() - 1);
        
        // Clean all values contained between &lt; &gt ;  
        Pattern globalTagPattern = Pattern.compile("&lt;[^&;]*&gt;");
        StringBuffer resultBuffer = new StringBuffer();
        Matcher m = globalTagPattern.matcher(text);
        int index = 0;
        while (m.find())
        {
            resultBuffer.append(text.substring(index, m.start()));
            // Keep Original text
            String originalText = text.substring(m.start(), m.end());
            
            // Try to remove XML tags inside original text.
            // Check if cleaned Tag corresponds to at least one of the patterns
            // If not, keep Original value
            String cleanedTag = removeXMLTag(originalText);
            if(!cleanedTag.toString().matches(patternToMatch)){
                cleanedTag = originalText; 
            }
            resultBuffer.append(cleanedTag);
            index = m.end();
        }
        resultBuffer.append(text.substring(index));

        // Clean content inside effective gendoc tags. 
        String result = cleanContent(resultBuffer.toString(), patternsToClean);
        // Replace invalid characters
        result = cleanXMLContent(result);

        return result;
    }

    protected String cleanXMLContent(String content)
    {
        String newContent =content;
        for (String invalidQuote : TagParserConfig.INVALID_QUOTES){
            newContent =newContent.replace(invalidQuote, TagParserConfig.VALID_QUOTE);
        }
        
        for (char invalidDoubleQuote : TagParserConfig.INVALID_DOUBLE_QUOTES){
            newContent =newContent.replace(invalidDoubleQuote, TagParserConfig.VALID_DOUBLE_QUOTE);
        }
        return newContent;
    }

    public String cleanContent(String text, List<Pattern> patternsToClean)
    {

        StringBuffer result = new StringBuffer();
        String patterns = "";
        for (Pattern p : patternsToClean)
        {
            patterns += "(" + p + ")|";
        }
        patterns = patterns.substring(0, patterns.length() - 1);
        Pattern p = Pattern.compile(patterns);
        Matcher m = p.matcher(text);
        int index = 0;
        while (m.find())
        {
            result.append(text.substring(index, m.start()));
            result.append(removeXMLTag(text.substring(m.start(), m.end())));
            index = m.end();
        }
        result.append(text.substring(index));
        return result.toString();
    }

    private String removeXMLTag(String text)
    {
        StringBuffer result = new StringBuffer();
        Matcher m = patternTag.matcher(text);
        int index = 0;
        while (m.find())
        {
            result.append(text.substring(index, m.start()));
            index = m.end();
        }
        if (index < text.length())
        {
            result.append(text.substring(index));
        }
        return result.toString();
    }

    protected abstract Node cleanTags(Node currentNode, List<String> tagLabels, Node baseNode) throws InvalidContentException;

    /**
     * Checks if the given string contains one of the valid tags completely (either opening tag, or closing tag or
     * autoclosed tag)
     * <p/>
     * Ex :(either<code> &lt;tagLabel ...&gt; </code>or <code>&lt;/tagLabel&gt; </code>or
     * <code>&lt;tagLabel ... /&gt;</code>
     * 
     * @param toCheck string to check
     * @param tagLabels list of valid tag labels
     * @return true if string toCheck contains at least a full and valid tagLabel
     */
    public boolean containsFullTags(String toCheck, List<String> tagLabels)
    {
        if (toCheck == null)
        {
            return false;
        }

        int index = toCheck.lastIndexOf(TagParserConfig.SUP);
        if (index > -1 && containsOneOf(tagLabels, toCheck.substring(index)))
        {
            return false;
        }
        List<String> labelsToFind = new ArrayList<String>();
        boolean result = false;
        for (String tagLabel : tagLabels)
        {
            if (toCheck.matches(".*" + TagParserConfig.INF + "(/|)" + tagLabel + ".*"))
            {
                labelsToFind.add(tagLabel);
            }
            else if (tagLabel.startsWith(toCheck.replace(TagParserConfig.INF, "")))
            {
                labelsToFind.add(tagLabel);
            }
        }

        if (!labelsToFind.isEmpty())
        {
            result = true;
        }
        for (String tagLabel : labelsToFind)
        {
            result = result && (toCheck.matches(".*" + TagParserConfig.INF + "(/|)" + tagLabel + ".*(/|)" + TagParserConfig.SUP + ".*"));
        }
        return result;
    }

    /**
     * Checks if String to check contains the start of one of the labels
     * 
     * @param toCheck String to check
     * @param labels list of labels to compare with
     * @return true if at least one label start matches the string to check
     */
    protected boolean containsOneOf(List<String> labels, String toCheck)
    {
        if (toCheck == null)
        {
            return false;
        }
        if (toCheck.endsWith(TagParserConfig.INF) || toCheck.endsWith(TagParserConfig.INF + TagParserConfig.SLASH))
        {
            return true;
        }
        String stringToCheck = toCheck.substring(toCheck.indexOf(TagParserConfig.INF) + TagParserConfig.INF.length());
        if (stringToCheck.startsWith(String.valueOf(TagParserConfig.SLASH)))
        {
            stringToCheck = stringToCheck.substring(String.valueOf(TagParserConfig.SLASH).length(), stringToCheck.length() - 1);
        }

        for (String label : labels)
        {
            if (label.startsWith(stringToCheck) || stringToCheck.startsWith(label) || (("/" + label).startsWith(stringToCheck)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract the text value of a node
     * 
     * @param node an XML node
     * @return the value contained between node tags
     * @throws InvalidContentException
     */
    public String extractNodeTextValue(Node node) throws InvalidContentException
    {
        if (node == null)
        {
            return null;
        }
        // StringBuffer result = new StringBuffer();
        // if(node.getNodeValue() != null){
        // result.append(node.getNodeValue());
        // }
        // if( node.hasChildNodes()){
        // for (int i=0; i<node.getChildNodes().getLength();i++) {
        // result.append(asText(node.getChildNodes().item(i)));
        // }
        // }
        String result = null;
        String nodeValue = asText(node);
        if (nodeValue.endsWith(XML_AUTOCLOSING_TAG_END))
        {
            result = "";
        }
        else
        {
            int endOfStartingTag = nodeValue.indexOf(XML_TAG_END);
            int startOfEndingTag = nodeValue.lastIndexOf(XML_TAG_START);
            if (endOfStartingTag == -1 || startOfEndingTag == -1 || endOfStartingTag > startOfEndingTag)
            {
                result = nodeValue;
            }
            else
            {
                result = nodeValue.substring(endOfStartingTag + 1, startOfEndingTag);
            }
        }
        return result;
        // String[] elements = asText(node).split(XML_TAG_START + "/|/" + XML_TAG_END + "|" + XML_TAG_START + "|" +
        // XML_TAG_END);
        // for (String element : elements)
        // {
        // if (element != null && !element.startsWith(node.getNodeName()))
        // {
        // result.append(element);
        // }
        // }
        //        
        // return result.toString();
    }

    /**
     * Find next node with a starting tag (<code>TagConstants.INF</code>)
     * 
     * @param currentNode node from which to start the searching
     * @return
     * @throws InvalidContentException
     */
    public Node findNodeWithOneTag(Node currentNode) throws InvalidContentException
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        // FIXME Node node = currentNode.cloneNode(true);
        Node node = currentNode;

        String currentNodeInternalContent = "";
        String nodeContent = null;
        StringBuffer tagContent = new StringBuffer();
        Node startNode = null;

        while (startNode == null && node != null)
        {
            node = XMLHelper.next(node);

            currentNodeInternalContent = extractNodeTextValue(node);
            boolean nullOrContainsStartTag = currentNodeInternalContent == null
                    || ((currentNodeInternalContent.indexOf(TagParserConfig.INF) != -1) && !(currentNodeInternalContent.substring(0, currentNodeInternalContent.indexOf(TagParserConfig.INF)).contains(XML_TAG_START)));
            while (node != null && (!nullOrContainsStartTag))
            {
                node = XMLHelper.next(node);
                currentNodeInternalContent = extractNodeTextValue(node);
                nullOrContainsStartTag = currentNodeInternalContent == null
                        || (currentNodeInternalContent.indexOf(TagParserConfig.INF) != -1 && !(currentNodeInternalContent.substring(0, currentNodeInternalContent.indexOf(TagParserConfig.INF)).contains(XML_TAG_START)));
            }
            if (node == null)
            {
                return null;
            }
            nodeContent = asText(node); // Full content of the node
            tagContent.append(nodeContent.substring(0, nodeContent.indexOf(TagParserConfig.INF)));
            tagContent.append(currentNodeInternalContent);
            startNode = node;
            if (Node.TEXT_NODE == startNode.getNodeType())
            {
                logger.log("XMLDocumentService.findNodeWithOneTag returned a text node :" + startNode.getNodeValue(), ILogger.DEBUG);
                startNode = node.getParentNode();
            }
            return startNode;

        }
        return null;
    }

    protected Node findNodeWithStartTag(Node currentNode, Node ancestorNode)
    {

        String label = currentNode.getNodeName();
        String path = getRelativePath(ancestorNode, currentNode);
        if (path == null)
        {
            return null;
        }
        if (!"".equals(path))
        {
            path += "/";
        }
        path += "following::" + label + "[contains(.,'" + XML_TAG_START + "')]";
        Node result = getNodeFromXPath(ancestorNode, path);

        if (getRelativePath(ancestorNode, result) == null)
        {
            return null;
        }
        if( currentNode.equals(result)){
        	return null;
        }
        return result;
    }

    /**
     * Get list of next nodes with the node label
     * 
     * @param startNode node from which to start the searching
     * @param nodeLabel label of the node to search
     * @return the list of nodes following startNode and with nodeName = nodeLabel
     */
    public NodeList getNextNodes(Node startNode, String nodeLabel)
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(getNameSpaceContext());
            XPathExpression expr = xpath.compile("following::" + nodeLabel);
            Object result = expr.evaluate(startNode, XPathConstants.NODESET);
            if (result != null && result instanceof NodeList)
            {
                return (NodeList) result;
            }
        }
        catch (XPathExpressionException e)
        {
            logger.log(e.getStackTrace().toString(), IStatus.ERROR);
            // Return null in case of problem => do nothing here
        }
        return null;

    }

    /**
     * Find the best ascending node from baseNode until a direct child of high Ascendant
     * 
     * @param higherAscendant Node that must be a direct parent of the result
     * @param baseNode node from which to start the ascension
     * @return a direct child of higherAscendant that is a parent of baseNode
     */
    protected Node getBestAscendantUntil(Node higherAscendant, Node baseNode)
    {
        Node parent = baseNode.getParentNode();
        if (parent == null)
        {
            return null;
        }
        if (parent.equals(higherAscendant))
        {
            return baseNode;
        }
        else
        {
            return getBestAscendantUntil(higherAscendant, parent);
        }
    }

    /**
     * Find the Xpath to get currentNode from ancestorNode
     * 
     * @param ancestorNode ancestor that defines the subtree for the search
     * @param currentNode node to search in that subtree
     * @return the path to get currentNode from ancestorNode, or null if not found
     */
    private String getRelativePath(Node ancestorNode, Node currentNode)
    {
        if (currentNode == null)
        {
            return null;
        }
        if (currentNode.equals(ancestorNode))
        {
            return "";
        }

        // Store path until parent in a buffer
        StringBuffer path = new StringBuffer(currentNode.getNodeName());
        // Recursively get parent and fill path
        Node parent = currentNode.getParentNode();

        Node node = currentNode;

        if (node.getPreviousSibling() != null)
        {
            // Count position
            int index = 1;
            while (node != null)
            {
                String label = node.getNodeName();
                node = node.getPreviousSibling();
                if (node != null && label.equals(node.getNodeName()))
                {
                    index++;
                }

            }
            if (index > 0)
            {
                path.append("[" + index + "]");
            }
        }

        while (parent != null && !parent.equals(ancestorNode))
        {
            path.insert(0, "/");
            currentNode = parent;
            parent = currentNode.getParentNode();
            String label = currentNode.getNodeName();
            if (currentNode.getPreviousSibling() != null)
            {
                // Count position
                int index = 1;
                while (currentNode != null)
                {

                    currentNode = currentNode.getPreviousSibling();
                    if (currentNode != null && label.equals(currentNode.getNodeName()))
                    {
                        index++;
                    }
                }
                if (index > 0)
                {
                    path.insert(0, "[" + index + "]");
                }
            }
            path.insert(0, label);
        }

        if (parent == null)
        {
            return null;
        }
        return path.toString();
    }

    public NodeList getNodeListFromXPath(Node start, String expression)
    {
        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(getNameSpaceContext());
            XPathExpression expr = xpath.compile(expression);
            Object result = expr.evaluate(start, XPathConstants.NODESET);
            if (result != null && result instanceof NodeList)
            {
                return (NodeList) result;
            }
        }
        catch (XPathExpressionException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Node getNodeFromXPath(Node start, String expression)
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(getNameSpaceContext());
            XPathExpression expr = xpath.compile(expression);
            Object result = expr.evaluate(start, XPathConstants.NODE);
            if (result != null && result instanceof Node)
            {
                return (Node) result;
            }
        }
        catch (XPathExpressionException e)
        {
            logger.log("Invalid XPath expression : "+expression+"\n"+e.getStackTrace().toString(), IStatus.ERROR);
        }
        return null;
    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        this.document = document;
    }

    /**
     * Clean a text in order to obtain valid XML tags
     * 
     * @param textTag the text tag to clean
     * @return a node content
     */
    public String cleanTextTag(String textTag)
    {

        StringBuffer cleaned = new StringBuffer("");
        int index = 0;
        Stack<String> tagStack = new Stack<String>();

        /** Match all tags */
        Matcher m = patternTag.matcher(textTag);
        while (m.find())
        {
            String matchedTag = textTag.substring(m.start(), m.end());
            String tagName = getTagName(matchedTag);
            // If it is an close tag
            if (isCloseTag(matchedTag))
            {
                // If the tag close the last opened tag
                if (!tagStack.empty() && tagName.equals(tagStack.peek()))
                {
                    // Pop opened tag
                    tagStack.pop();

                    // Save text before tag + closing tag
                    cleaned.append(textTag.substring(index, m.end()));
                }
                else if (!tagStack.empty() && areSimilarTags(tagName, tagStack.peek()))
                {
                    String toAppend = matchedTag.replace(tagName, tagStack.peek());
                    // Pop opened tag
                    tagStack.pop();

                    // Save text before tag + closing tag
                    cleaned.append(toAppend);
                }
                else
                {
                    // If the tag close a tag push earlier
                    if (tagStack.contains(tagName))
                    {
                        // Save text before tag
                        cleaned.append(textTag.substring(index, m.start()));

                        // Create and add all the closing tag for tag opened after the current tag
                        while (!tagStack.peek().equals(tagName))
                        {
                            cleaned.append("</" + tagStack.pop() + ">");
                        }

                        // Pop opened tag
                        tagStack.pop();

                        // Save the closing tag
                        cleaned.append(matchedTag);
                    }
                    else
                    {
                        String similarTag = containsSimilarTag(tagStack, tagName);
                        if (similarTag != null)
                        {
                            // Save text before tag
                            cleaned.append(textTag.substring(index, m.start()));
                            // Create and add all the closing tag for tag opened after the current tag
                            while (!tagStack.peek().equals(similarTag))
                            {
                                cleaned.append("</" + tagStack.pop() + ">");
                            }
                            // Pop opened tag
                            tagStack.pop();
                            // Save the closing tag
                            cleaned.append(matchedTag.replace(tagName, similarTag));
                        }
                        else
                        {
                            // Save text before tag
                            cleaned.append(textTag.substring(index, m.start()));
                        }
                    }
                }

            }
            else if (!isSingleTag(matchedTag))// If it is an open tag
            {
                // Push the open tag
                tagStack.push(tagName);

                // Save the text before the tag + the opened tag
                cleaned.append(textTag.substring(index, m.end()));
            }
            else
            {
                // Save the text before the tag + the tag
                cleaned.append(textTag.substring(index, m.end()));
            }
            // If the tag has never been opened
            // remove the useless closing tag

            // Update index
            index = m.end();
        }

        // Close all unclosed tags
        while (!tagStack.empty())
        {
            cleaned.append("</" + tagStack.pop() + ">");
        }

        return cleaned.toString();

    }

    /**
     * return the tag name
     * 
     * @param tag
     * @return the tag name
     */
    protected String getTagName(String tag)
    {
        int begin = tag.indexOf('<') + 1;

        if ('/' == tag.charAt(begin))
        {
            begin++;
        }
        int end;
        if (tag.contains(" "))
        {
            end = tag.indexOf(' ');
        }
        else
        {
            end = tag.indexOf('>');
        }
        return tag.substring(begin, end);
    }

    /**
     * Check if the given tag is a closing tag &lt;/mayTag&gt;
     * 
     * @param matchedTag
     * @return true if it is a closing tag else false
     */
    protected boolean isCloseTag(String matchedTag)
    {
        return patternTagClose.matcher(matchedTag).matches();
    }

    /**
     * Check if the given tag is a single tag <mayTag/>
     * 
     * @param matchedTag
     * @return true if it is a single tag else false
     */
    protected boolean isSingleTag(String matchedTag)
    {
        return patternTagSingle.matcher(matchedTag).matches();
    }

    protected Pattern getPatternTag()
    {
        return patternTag;
    }

    protected abstract boolean areSimilarTags(String tagName1, String tagName2);

    protected abstract String containsSimilarTag(Stack<String> tagStack, String tagName);

    protected Pattern getPatternTagClose()
    {
        return patternTagClose;
    }

    protected Pattern getPatternTagSingle()
    {
        return patternTagSingle;
    }

    /**
     * Remove the file f from disk. If f is a directory, remove all sub files
     * 
     * @param f
     */
    public void clean(File f)
    {
        if (f != null)
        {
            File[] thefiles = f.listFiles();
            if (thefiles != null)
            {
                for (File f2 : thefiles)
                {
                    if (f2.isDirectory())
                    {
                        clean(f2);
                    }
                    else
                    {
                        f2.deleteOnExit();
                    }
                }
            }
            f.deleteOnExit();
        }
    }
}
