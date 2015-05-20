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
 * Caroline Bourdeu d'Aguerre (Atos Origin)
 * caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.parsers.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.parsers.ITagParserService;
import org.eclipse.papyrus.gendoc2.tags.parsers.TagParserConfig;

/**
 * A structured tag has a name, attributes, and value (inner text).
 * 
 * @author Caroline Bourdeu d'Aguerre
 */
public class StructuredTag extends AbstractTag
{

    /** The attributes. */
    private Map<String, String> attributes;

    /** The name. */
    private String name;

    /** The value. */
    private String value; 

    /**
     * Instantiates a new StructuredTag.
     * 
     * @param parent the parent tag
     * @param text the text
     * @throws IncompleteTagException
     */
    public StructuredTag(ITag parent, String text) throws IncompleteTagException
    {
        super(parent, text);
        this.initFields();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getAttributes()
     */
    @Override
    public Map<String, String> getAttributes()
    {
        return this.attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getValue()
     */
    @Override
    public String getValue()
    {
        return this.value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#isComplete()
     */
    public boolean isComplete()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#isStructured()
     */
    public boolean isStructured()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#setValue(String)
     */
    @Override
    public void setValue(String newValue)
    {
        this.value = newValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("StructuredTag" + "\n");
        buffer.append("{" + "\n");
        buffer.append("\tname: " + this.name + "\n");
        buffer.append("\tattributes: " + "\n");
        buffer.append("\t{" + "\n");
        for (String k : this.getAttributes().keySet())
        {
            buffer.append("\t\t" + k + ": " + this.getAttributes().get(k) + "\n");
        }
        buffer.append("\t}" + "\n");
        buffer.append("\ttext: " + this.getRawText() + "\n");
        buffer.append("\tvalue: " + this.getValue() + "\n");
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Get the index of the end of a word.
     * 
     * @param fromIndex the index from which to start the search
     * @param text the text to search
     * @param str the substring for which to search
     * 
     * @return the index of the end of the word
     */
    private int getWordEnd(int fromIndex, String text, String str)
    {
        int nextSpace = text.indexOf(TagParserConfig.SPACE, fromIndex);
        int nextEqual = text.indexOf(str, fromIndex);
        int next;
        if ((nextSpace != -1) && (nextSpace < nextEqual))
        {
            next = nextSpace;
        }
        else
        {
            next = nextEqual;
        }
        return next;
    }

    /**
     * Initialise the class fields (name, attributes, value).
     * 
     * @throws IncompleteTagException
     */
    private void initFields() throws IncompleteTagException
    {
        ITagParserService tagParserService = GendocServices.getDefault().getService(ITagParserService.class);
        
        int iCurrent = 0;
        this.attributes = new HashMap<String, String>();

        String text = this.getRawText();
        // get the next '<' (should be '0')
        iCurrent = text.indexOf(TagParserConfig.INF, iCurrent);

        // Extract name
        int iNameStart = iCurrent + TagParserConfig.INF.length();
        int iNameEnd = this.getWordEnd(iNameStart, text, TagParserConfig.SUP);
        this.name = text.substring(iNameStart, iNameEnd);
        iCurrent = iNameEnd;
        
        // Add tag to tag list
        tagParserService.increaseTagIndex(name);

        // Extract attributes
        int iTagEnd = text.indexOf(TagParserConfig.SUP, iNameEnd);

        // Is a single tag
        boolean isSingleTag = text.startsWith(TagParserConfig.SLASH, iTagEnd - TagParserConfig.SLASH.length());

        // If it is a single tag the end is not '>' but '/'
        if (isSingleTag)
        {
            iTagEnd -= TagParserConfig.SLASH.length();
        }

        // Get the beginning of the next word (attribute name)
        while (TagParserConfig.SPACE == text.charAt(iCurrent))
        {
            iCurrent++;
        }

        while ((iCurrent < iTagEnd) && (iCurrent < text.length()))
        {
            // Get the end of the word (attribute name) => end at the next '=' or ' '
            iNameEnd = this.getWordEnd(iCurrent + 1, text, TagParserConfig.EQUAL);
            if (iNameEnd < 0)
            {
                break;
            }
            String attributeName = text.substring(iCurrent, iNameEnd);
            iCurrent = iNameEnd + 1;

            // Get the beginning of the next word (attribute value)
            while ((iCurrent < text.length()) && (TagParserConfig.VALID_QUOTE.charAt(0) != text.charAt(iCurrent)))
            {
                iCurrent++;
            }

            // the word begin after the quote
            iCurrent++;

            // Get the end of the word (attribute value) => end at the next quote
            iNameEnd = text.indexOf(TagParserConfig.VALID_QUOTE.charAt(0), iCurrent + 1);
            if (iNameEnd == -1)
            {
                throw new IncompleteTagException(this.name, tagParserService.getTagIndex(name), "attribute '"+attributeName+"' is not correctly filled.");
            }
            String attributeValue = text.substring(iCurrent, iNameEnd);

            // Add the attribute to the list
            this.attributes.put(attributeName, attributeValue);

            iCurrent = iNameEnd + 1;

            // Get the beginning of the next word (attribute name)
            while (TagParserConfig.SPACE == text.charAt(iCurrent))
            {
                iCurrent++;
            }

        }
        iCurrent += TagParserConfig.SUP.length();
        // Extract the value
        if (isSingleTag)
        {
            this.value = TagParserConfig.EMPTY;
        }
        else
        {
            int iCloseTagStart = text.lastIndexOf(TagParserConfig.INF + TagParserConfig.SLASH + this.name + TagParserConfig.SUP, text.length());

            if (iCloseTagStart > iCurrent)
            {
                this.value = text.substring(iCurrent, iCloseTagStart);
            }
            else
            {
                // FIXME Handle this case (should never append but append sometimes!)
                this.value = TagParserConfig.EMPTY;
            }
        }

    }

}
