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
package org.eclipse.papyrus.gendoc2.document.parser.documents.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.XMLParser;
import org.eclipse.papyrus.gendoc2.document.parser.documents.XMLParser.NullXMLParser;

/**
 * The Class CSVDocument.
 */
public class CSVDocument implements Document
{
    /** line separator */
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    /** String used for qualifying text or escaping itself */
    private final static String TEXT_QUALIFIER = "\"";

    /** String used to delimit cells of a row */
    private final static String DELIMITER = ";";

    private URL currentDocument;

    private BufferedReader reader;

    private int currentColumn = -1;

    private String[] currentRow = null;

    private boolean next = true;

    private NullXMLParser xmlParser;

    private InputStream openStream;

    public URL getDocumentURL()
    {
        return currentDocument;
    }

    public String getPath()
    {
        return currentDocument.getPath();
    }
    
    public CSVDocument(URL documentFile)
    {
        currentDocument = documentFile;
        xmlParser = new XMLParser.NullXMLParser(null);
        try
        {
            openStream = currentDocument.openStream();
            reader = new BufferedReader(new InputStreamReader(openStream));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readLine()
    {
        String line;
        try
        {
            line = reader.readLine();
            next = line != null;
            if (next)
            {
                if (!line.contains(TEXT_QUALIFIER))
                {
                    // simple case : no difficulty
                    currentRow = line.split(DELIMITER);
                }
                else
                {
                    /*
                     * The text contains " text qualifiers. Hence, it may extend on several lines or contain escaping "
                     * characters.
                     */
                    boolean isFirstLine = true;
                    List<StringBuffer> completeRow = new ArrayList<StringBuffer>();
                    boolean inQualifiedText = false;
                    do
                    {
                        if (!isFirstLine)
                        {
                            // append line separator
                            completeRow.get(completeRow.size() - 1).append(LINE_SEPARATOR);
                            // read new line
                            line = reader.readLine();
                            if (line == null)
                            {
                                break;
                            }
                            else if (!line.contains(TEXT_QUALIFIER))
                            {
                                // simple case : simply append new line and continue
                                completeRow.get(completeRow.size() - 1).append(line);
                                continue;
                            }
                        }
                        String[] lineElements = line.split(TEXT_QUALIFIER);
                        for (int indexOfLineElement = 0; indexOfLineElement < lineElements.length; indexOfLineElement++)
                        {
                            String lineElement = lineElements[indexOfLineElement];
                            if ("".equals(lineElement))
                            {
                                // special treatment : may be a " escape or first or last empty string
                                if (indexOfLineElement == 0)
                                {
                                    completeRow.add(new StringBuffer());
                                }
                                else if (indexOfLineElement < lineElements.length - 1)
                                {
                                    // append "
                                    completeRow.get(completeRow.size() - 1).append(TEXT_QUALIFIER);
                                }
                                // else, end of line with end of qualified text
                            }
                            else if (!inQualifiedText)
                            {
                                // handle new elements
                                boolean firstValue = true;
                                for (String cellValue : lineElement.split(DELIMITER))
                                {
                                    if (firstValue && !lineElement.startsWith(DELIMITER) && completeRow.size() > 0)
                                    {
                                        // append qualified text to latest value
                                        completeRow.get(completeRow.size() - 1).append(cellValue);
                                    }
                                    else if (!(firstValue && lineElement.startsWith(DELIMITER) && "".equals(cellValue)))
                                    {
                                        completeRow.add(new StringBuffer(cellValue));
                                    }
                                    firstValue = false;
                                }
                                if (lineElement.endsWith(DELIMITER))
                                {
                                    // add empty value
                                    completeRow.add(new StringBuffer());
                                }
                            }
                            else if (completeRow.size() > 0)
                            {
                                // append qualified text to latest value
                                completeRow.get(completeRow.size() - 1).append(lineElement);
                            }
                            else
                            {
                                // add qualified text as first value
                                completeRow.add(new StringBuffer(lineElement));
                            }
                            if (indexOfLineElement < lineElements.length - 1 || line.endsWith(TEXT_QUALIFIER))
                            {
                                // we enter or exit the qualified text
                                inQualifiedText = !inQualifiedText;
                            }
                        }
                        // continue until we are no longer in qualified text
                        isFirstLine = false;
                    }
                    while (inQualifiedText);
                    currentRow = new String[completeRow.size()];
                    int i = 0;
                    for (StringBuffer value : completeRow)
                    {
                        currentRow[i] = value.toString();
                        i++;
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getColumnNumber()
    {
        return currentColumn;
    }

    public File getDocumentFile()
    {
        return new File(currentDocument.getFile());
    }

    public String getStyle()
    {
        return "";
    }

    public String getText()
    {
        if (currentRow != null && currentRow.length > currentColumn)
        {
            return currentRow[currentColumn];
        }
        return "";
    }

    public String getTextCorrespondingToCurrentStyle()
    {
        return getText();
    }

    public XMLParser getXMLParser()
    {
        return xmlParser;
    }

    public boolean jumpToNextFile()
    {
        return false;
    }

    public void jumpToStart()
    {
    }

    public boolean next()
    {
        if (currentColumn == -1)
        {
            currentColumn = 0;
        }
        else
        {
            currentColumn = (currentColumn + 1) % currentRow.length;
        }
        if (currentColumn == 0)
        {
            readLine();
        }
        if (!next)
        {
            try
            {
                reader.close();
                openStream.close();
            }
            catch (IOException e)
            {
            }
        }
        return next;
    }

    public Object get(PROPERTY property)
    {
        switch (property)
        {
            case column:
                return getColumnNumber();
            case row:
                return getRowNumber();
            case text:
                return getText();
            default:
                return null;
        }
    }

    private int getRowNumber()
    {
        return 0;
    }

}
