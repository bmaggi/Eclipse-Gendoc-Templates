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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.odt;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.odt.ODTDocument;
import org.eclipse.papyrus.gendoc2.document.parser.documents.odt.ODTNamespaceContext;
import org.eclipse.papyrus.gendoc2.documents.IAdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.XMLDocumentService;
import org.eclipse.papyrus.gendoc2.services.exception.DocumentServiceException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidContentException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Specific service for ODT document management
 */
public class ODTDocumentService extends XMLDocumentService implements IExecutableExtension
{

	private static final String XMLNS_OFFICE = "xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\"";
	private static final String XMLNS_TABLE  = "xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\"";
	private static final String XMLNS_TEXT   = "xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"";
	private static final String XMLNS_FO   = "xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\"";
	private static final String XMLNS_STYLE   = "xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\"";

    /** Service for additional resources */
    IAdditionalResourceService additionalResourceService;

    private final String TAG_TABLE = "table:table";

	private String serviceId;
	
    public ODTDocumentService()
    {
        super();
        additionalResourceService = new ODTAdditionalResourceService();
    }

    public ODTDocumentService(Document document)
    {
        super(document);
        additionalResourceService = new ODTAdditionalResourceService();
    }

    public boolean isList(String label)
    {
        return "text:list".equals(label);
    }

    public boolean isListItem(String label)
    {
        return "text:list-item".equals(label);
    }

    public boolean isPara(String label)
    {
        return "text:p".equals(label) || "text:h".equals(label);
    }

    public boolean isTable(String label)
    {
        return "table:table".equals(label);
    }

    public boolean isRow(String label)
    {
        return "table:table-row".equals(label);
    }

    public String getTextStyle()
    {
        return "(text:p|text:h|text:span)";
    }

    public String[] getTextTagLabels()
    {
        return new String[] {"text:p", "text:h", "text:span", "table:table"};
    }

    public String getNamingSpaceURL()
    {
        return XMLNS_TEXT + " " + XMLNS_TABLE + " " + XMLNS_OFFICE + " "+ XMLNS_FO+ " "+XMLNS_STYLE;
    }

    public NamespaceContext getNameSpaceContext()
    {
        return new ODTNamespaceContext();
    }

    public void saveDocument(Document document, String path) throws DocumentServiceException
    {
        if (!(document instanceof ODTDocument))
        {
            throw new DocumentServiceException("Document is not a valid ODT document.");
        }
        insertDocumentInFile((ODTDocument) document);
        ((ODTDocument) document).zipToLocation(path);
 
    }

    /**
     * @throws TransformerFactoryConfigurationError
     */
    private void insertDocumentInFile(ODTDocument document)
    {
        try
        {
            document.jumpToStart();
            do
            {
                DOMSource domSource = new DOMSource(document.getXMLParser().getDocument());
                StreamResult fluxDestination = new StreamResult(new File(document.getUnzipLocationDocumentFile().getAbsolutePath() + "/" + document.getXMLParser().getXmlFile().getName()));
                TransformerFactory fabrique = TransformerFactory.newInstance();

                Transformer transformationIdentite = fabrique.newTransformer();
                transformationIdentite.setOutputProperty(OutputKeys.INDENT, "yes");
                transformationIdentite.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                transformationIdentite.transform(domSource, fluxDestination);
            }
            while (document.jumpToNextFile());
        }
        catch (TransformerException e2)
        {
            e2.printStackTrace();
        }
    }

    /**
     * @param currentNode subtree in which the clean is done.
     * @param tagLabels list of known tag labels
     * @param baseNode Node on which to start
     * @return
     * @throws InvalidContentException 
     */
    protected Node cleanTags(Node currentNode, List<String> tagLabels, Node baseNode) throws InvalidContentException
    {
        if (baseNode == null)
        {
            return null;
        }

        // 2. Check that this node contains the start of a valid tag label
        StringBuffer newNodeContent = new StringBuffer(extractNodeTextValue(baseNode));
        while (baseNode != null && !containsOneOf(tagLabels, newNodeContent.toString()))

        {
            baseNode = findNodeWithStartTag(baseNode, currentNode);
            if (baseNode != null)
            {
                newNodeContent = new StringBuffer(extractNodeTextValue(baseNode));
            }
        }
        if (baseNode == null)
        {
            return null;
        }
        // 3. Base node is found AND matches a valid tag => Check tag closure

        boolean isCompleteTag = containsFullTags(newNodeContent.toString(), tagLabels);

        String[] currentNodeText = new String[0];
        List<String> partsToRemove = new LinkedList<String>();
        String currentNodeContent = extractNodeTextValue(currentNode);

        // 4. If tag not closed :
        if (!isCompleteTag)
        {
            currentNodeText = currentNodeContent.split("((<[^>]*>)*[^<>]*</[^<>]*>|(<[^>]*>))");
            // System.out.println(currentNodeContent+"\n -- > "+Arrays.toString(currentNodeText));
            // Find all nodes matching the base node label
            NodeList followingNodes = getNextNodes(baseNode, baseNode.getNodeName());

            List<Node> nodesToRemove = new ArrayList<Node>();
            if (currentNodeText.length > 0)
            {
                newNodeContent.append(currentNodeText[0]);
                partsToRemove.add(currentNodeText[0]);
            }
            if (followingNodes != null)
            {
                // Append text values of all these nodes until tag closure is found
                for (int i = 0; i < followingNodes.getLength(); i++)
                {
                    if (currentNodeText.length > i + 1)
                    {
                        newNodeContent.append(currentNodeText[i + 1]);
                        partsToRemove.add(currentNodeText[i + 1]);
                    }
                    String textValue = extractNodeTextValue(followingNodes.item(i));

                    Node nodeToRemove = getBestAscendantUntil(currentNode, followingNodes.item(i));
                    if (nodeToRemove != null)
                    {
                        newNodeContent.append(textValue);

                        if (!nodesToRemove.contains(nodeToRemove))
                        {
                            nodesToRemove.add(nodeToRemove);
                        }
                        if (containsFullTags(newNodeContent.toString(), tagLabels))
                        {
                            isCompleteTag = true;
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }

                }
                if (currentNodeText.length > followingNodes.getLength() + 1)
                {
                    newNodeContent.append(currentNodeText[followingNodes.getLength() + 1]);
                    partsToRemove.add(currentNodeText[followingNodes.getLength() + 1]);
                }

                // Remove all nodes that are not useful anymore from initial current Node
                for (Node nodeToRemove : nodesToRemove)
                {
                    if (nodeToRemove != null && currentNode.equals(nodeToRemove.getParentNode()))
                    {
                        currentNode.removeChild(nodeToRemove);
                    }
                }
            }
        }
        // Replace content of base node with the text stored in "textContent" variable
        String[] separated = asText(baseNode).split(XML_TAG_START + "|" + XML_TAG_END);
        if (separated != null && separated.length > 1)
        {
            newNodeContent.insert(0, XML_TAG_START + separated[1] + XML_TAG_END);
            newNodeContent.append(XML_TAG_START + separated[separated.length - 1] + XML_TAG_END);
        }
        else
        {
            newNodeContent.append(asText(baseNode));
        }
        // Replace invalid characters
        String nodeContent = cleanXMLContent(newNodeContent.toString());
//         String nodeContent = newNodeContent.toString().replaceAll("" + TagConstants.INVALID_QUOTE, "" + TagConstants.QUOTE);
       // Replace base node by the value of the buffer

        Node result = injectNode(baseNode, nodeContent);
        baseNode.getParentNode().removeChild(baseNode);
        // If parentNode had text content
        if (!partsToRemove.isEmpty())
        {
            if (currentNode.getParentNode() == null)
            {
                // Inject into result
                StringBuffer finalString = new StringBuffer();
                finalString.append(nodeContent.substring(0, nodeContent.indexOf(XML_TAG_END) + 1));
                finalString.append(clearContents(result, partsToRemove, nodeContent));
                finalString.append(nodeContent.substring(nodeContent.lastIndexOf(XML_TAG_START)));
                Node newNode = injectNode(result, finalString.toString());
                result.getParentNode().removeChild(result);
                result = newNode;

            }
            else
            {
                StringBuffer finalString = new StringBuffer();
                String currentNodeContentString = asText(currentNode);
                finalString.append(currentNodeContentString.substring(0, currentNodeContentString.indexOf(XML_TAG_END) + 1));
                finalString.append(clearContents(currentNode, partsToRemove, nodeContent));
                finalString.append(currentNodeContentString.substring(currentNodeContentString.lastIndexOf(XML_TAG_START)));
                result = injectNode(currentNode, finalString.toString());
                currentNode.getParentNode().removeChild(currentNode);
            }
        }
        return result;

    }

    public String addNamingSpaces(String nodes)
    {

        for (String textNode : getTextTagLabels())
        {
            nodes = nodes.replaceAll("<" + textNode + " ", "<" + textNode + " " + getNamingSpaceURL() + " ");
            nodes = nodes.replaceAll("<" + textNode + ">", "<" + textNode + " " + getNamingSpaceURL() + ">");
        }
        return nodes;
    }

    /**
     * Clear contents of the "currentNodeContent" by removing all toplevel partsToRemove
     * 
     * @param currentNodeContent content of a node
     * @param partsToRemove parts to remove
     * @return The currentNodeContent cleaned
     * @throws InvalidContentException 
     */
    private String clearContents(Node currentNode, List<String> partsToRemove, String nodeContent) throws InvalidContentException
    {
        String currentNodeContent = extractNodeTextValue(currentNode);
        Matcher m = Pattern.compile("<[^>]*>[^<]*</[^>]*>").matcher(currentNodeContent);
        String[] currentNodeParts = currentNodeContent.split("(<[^>]*>[^<]*</[^>]*>)|<[^>]*/>");
        StringBuffer content = new StringBuffer();

        StringBuffer toRemove = new StringBuffer();
        for (String partToRemove : partsToRemove)
        {
            toRemove.append(partToRemove);
        }
        for (int i = 0; i < currentNodeParts.length; i++)
        {
            if (!partsToRemove.contains(currentNodeParts[i]) && !toRemove.toString().equals(currentNodeParts[i]))
            {
                content.append(currentNodeParts[i]);
            }
            if (m.find())
            {
                content.append(currentNodeContent.substring(m.start(), m.end()));
            }
        }
        return content.toString();
    }

    public IAdditionalResourceService getAdditionalResourceService()
    {
        return additionalResourceService;
    }

    public String getTableLabel()
    {
        return TAG_TABLE;
    }

    /**
     * Only &lt;text:p&gt; and &lt;text:h&gt; are considered as similar tags.
     * 
     * @see org.eclipse.papyrus.gendoc2.services.XMLDocumentService#areSimilarTags(java.lang.String, java.lang.String)
     */
    protected boolean areSimilarTags(String tagName1, String tagName2)
    {
        return "text:p".equals(tagName1) && "text:h".equals(tagName2) || "text:p".equals(tagName2) && "text:h".equals(tagName1);
    }

    protected String containsSimilarTag (Stack<String> tagStack, String tagName)
    {
    	if ("text:p".equals(tagName) && tagStack.contains("text:h"))
    	{
    		return "text:h";
    	}
    	else if ("text:h".equals(tagName) && tagStack.contains("text:p"))
    	{
    		return "text:p";
    	}
        return null;
    }

	public void setInitializationData (IConfigurationElement config, String propertyName, Object data) throws CoreException
	{
		// TODO Auto-generated method stub
		
	}

	public String getServiceId ()
	{
		return this.serviceId;
	}

	public void setServiceId (String serviceId)
	{
		this.serviceId = serviceId;
	}

	public String getListLabel()
	{
        return "text:list";
	}
	

    public String getListId(Node n)
    {
        NamedNodeMap map = n.getAttributes();
        if(map != null && map.getNamedItem("xml:id") != null) {
        	return map.getNamedItem("xml:id").getNodeValue();
        }
        return null;
    }

    public List<Pattern> getDropPatterns(String tagName)
    {
        List<Pattern> patterns = new LinkedList<Pattern>();
        patterns.add(Pattern.compile(
                "(?:<text:[p|h][^>]*>)(?:(?!<text:[p|h]).)*(?:&lt;" + tagName + ".*?/&gt;)(?:.*?)(?:</text:[p|h][^>]*>)", Pattern.DOTALL | Pattern.MULTILINE));
        return patterns;
    }

    public List<Pattern> getNobrPatterns(String tagName)
    {
        List<Pattern> patterns = new LinkedList<Pattern>();
        patterns.add(Pattern.compile(
                "(?:&lt;" + tagName + ".*?/&gt;)(?:.*?)(?:</text:[p|h][^>]*>)(?:.*?)(?:<text:[p|h][^>]*>)", Pattern.DOTALL | Pattern.MULTILINE));
        return patterns;
    }

    public String getContinueList(Node currentNode, String idList) throws InvalidContentException
    {
        NamedNodeMap m = currentNode.getAttributes();
        if(m.getNamedItem("text:continue-list") == null) {
            String curNodeString = this.asText(currentNode);
            curNodeString = curNodeString.replaceAll("<text:list ", "<text:list text:continue-list=\"" + idList + "\" ");
            return curNodeString;
        }
        return this.asText(currentNode);
    }

}
