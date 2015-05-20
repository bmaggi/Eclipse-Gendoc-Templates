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
package org.eclipse.papyrus.gendoc2.document.parser.documents.xlsx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.document.parser.documents.AbstractZipDocument;
import org.eclipse.papyrus.gendoc2.document.parser.documents.XMLParser;
import org.eclipse.papyrus.gendoc2.document.parser.documents.helper.OfficeHelper;
import org.eclipse.papyrus.gendoc2.document.parser.documents.helper.XLSXHelper;
import org.w3c.dom.Node;

/**
 * The Class XLSXDocument.
 */
public class XLSXDocument extends AbstractZipDocument
{
    /** The name of nodes indicating a new sheet */
    private static final String SHEET_NODE = "sheet";//$NON-NLS-1$

    /** The name of nodes indicating a new cell value */
    private static final String NEW_CELL_NODE = "si"; //$NON-NLS-1$

    /** The name of nodes indicating a new textual value (part of a cell value) */
    private static final String TEXT_VALUE_NODE = "t";//$NON-NLS-1$

    private String[] strings = null;

    public XLSXDocument(File documentFile) throws IOException
    {
        this(documentFile.toURI().toURL(), null);
    }

    public XLSXDocument(File documentFile, Map<CONFIGURATION, Boolean> configuration) throws IOException
    {
        this(documentFile.toURI().toURL(), configuration);

    }

    public XLSXDocument(URL documentFile, Map<CONFIGURATION, Boolean> configuration)
    {
        super(documentFile,configuration);
        initStrings();
    }

    /**
     * Fill the array of shared strings to get them later
     */
    private void initStrings()
    {
        XMLParser parser = new XMLParser(getUnzipper().getFile(XLSXHelper.SHARED_STRING_FILE));
        Node item = parser.getCurrentNode().getAttributes().getNamedItem(XLSXHelper.ATTRIBUTE_COUNT);
        if (item == null)
        {
            return;
        }
        int nb = Integer.valueOf(item.getTextContent());
        strings = new String[nb];
        int i = -1;
        do
        {
            String nodeName = parser.getCurrentNode().getNodeName();
            if (NEW_CELL_NODE.equals(nodeName))
            {
                i++;
            }
            if (TEXT_VALUE_NODE.equals(nodeName))
            {
                String text = parser.getCurrentNode().getTextContent();
                if (strings[i] != null)
                {
                    strings[i] = strings[i].concat(text);
                }
                else
                {
                    strings[i] = text;
                }
            }
        }
        while (parser.next());

    }

    @Override
    protected Collection<XMLParser> getXmlParsers(CONFIGURATION idForDocument)
    {
        Collection<XMLParser> parsers = new LinkedList<XMLParser>();
        switch (idForDocument)
        {
            case content:
                XMLParser workbook = new XMLParser(getUnzipper().getFile(XLSXHelper.CONTENTS_FILE_NAME), idForDocument);
                do
                {
                    if (SHEET_NODE.equals(workbook.getCurrentNode().getNodeName()))
                    {
                        Node item = workbook.getCurrentNode().getAttributes().getNamedItem(XLSXHelper.SHEET_ID);
                        if (item != null)
                        {
                            OfficeHelper.fillCollection(getUnzipper(), parsers, XLSXHelper.RELATIONSHIPS_SHEETS, idForDocument, XLSXHelper.DOCUMENT_RELS_FILE_NAME, item.getTextContent());
                        }
                    }
                }
                while (workbook.next());
                break;
            case footer:
                break;
            case header:
                break;
            case comment:
                // TODO
            default:
        }
        return parsers;
    }

    public String getStyle()
    {
        return "";
    }

    public String getText()
    {
        String result = "";
        Node currentNode = getXMLParser().getCurrentNode();
        if (XLSXHelper.CELL.equals(currentNode.getNodeName()))
        {
            Node item = currentNode.getAttributes().getNamedItem(XLSXHelper.CELL_TYPE);
            for (int i = 0; i < currentNode.getChildNodes().getLength(); i++)
            {
                if (XLSXHelper.CELL_VALUE.equals(currentNode.getChildNodes().item(i).getNodeName()))
                {
                    String value = currentNode.getChildNodes().item(i).getTextContent();
                    if (item != null && XLSXHelper.CELL_VALUE_SHARED_STRING.equals(item.getTextContent()))
                    {
                        try
                        {
                            int index = Integer.valueOf(value);
                            if (index < strings.length)
                            {
                                result = strings[index];
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            // DO NOTHING
                        }
                    }
                    else
                    {
                        result = value;
                    }
                    break;
                }
            }
        }
        return result;
    }

    public String getTextCorrespondingToCurrentStyle()
    {
        return null;
    }

    @Override
    public int getColumnNumber()
    {
        // we init to -1 to not be matched if there is a problem
        int column = -1;
        Node currentNode = getXMLParser().getCurrentNode();
        if (XLSXHelper.CELL.equals(currentNode.getNodeName()))
        {
            Node item = currentNode.getAttributes().getNamedItem("r");
            if (item != null)
            {
                String[] columnArray = item.getTextContent().split("\\d");
                if (columnArray.length > 0)
                {
                    String columnString = columnArray[0];
                    column = getColumnNumber(columnString);
                }
            }
        }
        return column;
    }

    private int getColumnNumber(String columnString)
    {
        if (columnString == null || columnString.length() == 0)
        {
            return -1;
        }
        int finalValue = 0;
        int j = 0;
        for (int i = columnString.length() - 1; i >= 0; i--)
        {
            finalValue += getColumnNumber(columnString.charAt(i)) * Math.pow(26, j);
            j++;
        }
        return finalValue - 1;
    }

    /**
     * Return the column number for a character A ==> 1
     * 
     * @param charAt
     * @return
     */
    private int getColumnNumber(char charAt)
    {
        return charAt - 'A' + 1;
    }

    public Object get(PROPERTY property)
    {
        switch (property)
        {
            case row:
                return getRowNumber();
            case column:
                return getColumnNumber();
            case text:
                return getText();
            default:
                return null;
        }
    }

    private int getRowNumber()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
