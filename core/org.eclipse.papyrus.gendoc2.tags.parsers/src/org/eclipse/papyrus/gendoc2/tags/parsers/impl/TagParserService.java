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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;
import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.parsers.ITagParserService;
import org.eclipse.papyrus.gendoc2.tags.parsers.TagParserConfig;

/**
 * The tag parser service parses text into tags.
 * 
 * @author Caroline Bourdeu d'Aguerre
 */
public class TagParserService extends AbstractService implements ITagParserService
{

    /**
     * Instantiates a new TagParserService.
     */
    public TagParserService()
    {
    }

    /**
     * <p>
     * Parse the given text into tags (TextTag and StructuredTag). Structured tags will be created for the given
     * tagNames.
     * </p>
     * <p>
     * For example:<br/>
     * text: aaaaaaa &lt;gendoc&gt; bbbbbbbbb &lt;/gendoc&gt;&lt;context/&gt;<br/>
     * tagNames: [ "gendoc", "context" ]<br/>
     * returns: [ TextTag (aaaaaaa), StructuredTag (&lt;gendoc&gt; bbbbbbbbb &lt;/gendoc&gt;), StructuredTag
     * (&lt;context/&gt;) ]
     * </p>
     * 
     * @param parent the parent tag
     * @param text the text to parse
     * @param tagNames the names of tags to return
     * 
     * @return a list of tags
     * @throws IncompleteTagException
     */
    public List<ITag> parse(ITag parent, String text, List<String> tagNames) throws IncompleteTagException
    {
        List<ITag> tags = new LinkedList<ITag>();
        Stack<String> stack = new Stack<String>();

        /** current index in the string */
        int iCurrent = 0;

        /** index of the end of the previous text */
        int iTextEnd = 0;
        /** index of the start of the next tag */
        int iTagStart = 0;
        /** index of the start of the next tag name */
        int iNameStart = 0;
        /** index of the end of the next tag name */
        int iNameEnd = 0;
        /** index of the end of the next tag */
        int iTagEnd = 0;
        /** index of the start of the next text */
        int iTextStart = 0;

        boolean isClosingTag = false;
        boolean searchClosingTag = false;

        while (iCurrent < text.length())
        {
            // get the next '<'
            iTagStart = text.indexOf(TagParserConfig.INF, iCurrent);
            if (iTagStart >= 0)
            {
                iNameStart = iTagStart + TagParserConfig.INF.length();

                // get the next '>'
                iTagEnd = text.indexOf(TagParserConfig.SUP, iNameStart);
                if (iTagEnd >= 0)
                {

                    // if there are several '<' before the next '>'
                    while ((text.indexOf(TagParserConfig.INF, iNameStart) >= 0) && (text.indexOf(TagParserConfig.INF, iNameStart) < iTagEnd))
                    {
                        // move to the last '<'
                        iTagStart = text.indexOf(TagParserConfig.INF, iNameStart);
                        iNameStart = iTagStart + TagParserConfig.INF.length();
                    }

                    // is it a single tag? ( < ... /> )
                    if (text.startsWith(TagParserConfig.SLASH, iTagEnd - TagParserConfig.SLASH.length()))
                    {
                        iTagEnd -= TagParserConfig.SLASH.length();

                        // if not searching for a closing tag
                        if (!searchClosingTag)
                        {
                            iNameEnd = text.indexOf(TagParserConfig.SPACE, iNameStart);
                            if ((iNameEnd < 0) || (iNameEnd > iTagEnd))
                            {
                                iNameEnd = iTagEnd;
                            }
                            String tagName = text.substring(iNameStart, iNameEnd);
                            iTagEnd += TagParserConfig.SLASH.length() + TagParserConfig.SUP.length();
                            if ((tagNames != null) && tagNames.contains(tagName))
                            {
                                iTextEnd = iTagStart;
                                // if we have previous text
                                if (iTextStart < iTextEnd)
                                {
                                    // add the previous text to the list
                                    tags.add(new TextTag(parent, text.substring(iTextStart, iTextEnd)));
                                }
                                // add the single tag to the list
                                ITag tag = new StructuredTag(parent, text.substring(iTagStart, iTagEnd));
                                tags.add(tag);
                                // save index after tag end (start of the following text)
                                iTextStart = iTagEnd;
                            }
                        }
                        else
                        {
                            // it's a nested tag - ignore it
                        }
                    }
                    else
                    // it is an opening or closing tag
                    {
                        // is it a closing tag? (</ ... >)
                        isClosingTag = (iTagStart < text.length()) && (text.startsWith(TagParserConfig.SLASH, iNameStart));
                        if (isClosingTag)
                        {
                            iNameStart += TagParserConfig.SLASH.length();

                            // get the name of the tag
                            iNameEnd = text.indexOf(TagParserConfig.SPACE, iNameStart);
                            if ((iNameEnd < 0) || (iNameEnd > iTagEnd))
                            {
                                iNameEnd = iTagEnd;
                            }
                            // if this tag closes the tag on the top of the stack
                            String tagName = text.substring(iNameStart, iNameEnd);
                            iTagEnd += TagParserConfig.SUP.length();
                            if (!stack.isEmpty() && stack.peek().equals(tagName))
                            {
                                // pop the tag off the stack
                                stack.pop();
                                if (stack.isEmpty())
                                {
                                    // if we have previous text
                                    if (iTextStart < iTextEnd)
                                    {
                                        // add the previous text to the list
                                        tags.add(new TextTag(parent, text.substring(iTextStart, iTextEnd)));
                                    }
                                    // add the complete tag to the list
                                    ITag tag = new StructuredTag(parent, text.substring(iTextEnd, iTagEnd));
                                    tags.add(tag);
                                    // save index after tag end (start of the following text)
                                    iTextStart = iTagEnd;
                                    // we are no longer searching for a closing tag
                                    searchClosingTag = false;
                                }
                                else
                                {
                                    // it's a nested tag - ignore it
                                }
                            }
                            else
                            // the name doesn't match the top of the stack
                            {
                                // we're still searching for the closing tag
                            }
                        }
                        else
                        // it's an opening tag
                        {
                            // get the name of the tag
                            iNameEnd = text.indexOf(TagParserConfig.SPACE, iNameStart);
                            if ((iNameEnd < 0) || (iNameEnd > iTagEnd))
                            {
                                iNameEnd = iTagEnd;
                            }
                            String tagName = text.substring(iNameStart, iNameEnd);
                            iTagEnd += TagParserConfig.SUP.length();
                            // push the tag onto the stack
                            if ((tagNames != null) && tagNames.contains(tagName))
                            {
                                if (stack.isEmpty())
                                {
                                    iTextEnd = iTagStart;
                                }
                                stack.push(tagName);
                                searchClosingTag = true;
                            }
                        }
                    }
                }
                else
                // no '>' found
                {
                    break;
                }
                iCurrent = iTagEnd;
            }
            else
            // no '<' found
            {
                break;
            }
        }

        // if a tag is not closed
        if (!stack.isEmpty())
        {
            // save the text before the tag
            if (iTextStart < iTextEnd)
            {
                tags.add(new TextTag(parent, text.substring(iTextStart, iTextEnd)));
            }
            // save the incomplete tag
            String tagName=stack.firstElement();
            ITag tag = new IncompleteTag(parent, text.substring(iTextEnd, text.length()), tagName);
            increaseTagIndex(tagName);
            tags.add(tag);
        }
        else if ((iTextStart >= 0) && (iTextStart < text.length()))
        {
            // Save the text at the end of the string
            tags.add(new TextTag(parent, text.substring(iTextStart, text.length())));
        }

        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        StringBuffer buffer = new StringBuffer("Tags found :");
        for (ITag tag : tags)
        {
            buffer.append("- ");
            buffer.append(tag.getName());
            buffer.append("  value :");
            buffer.append(tag.getValue());
            buffer.append("\n");
        }
        logger.log(buffer.toString(), ILogger.DEBUG);
        return tags;
    }
    

    /**
     * Count tags inside Registry. Increase a counter name after the tagName inside RegistryService
     */
    public int increaseTagIndex(String tagName)
    {
        IRegistryService registryService = GendocServices.getDefault().getService(IRegistryService.class);
        int currentTagIndex = getTagIndex(tagName);
        registryService.put(tagName, currentTagIndex+1);
        return currentTagIndex+1;
    }
    
    
    /**
     * Get the index of current Tag inside list of similar tags. (Tag count is done inside Registry)
     */
    public int getTagIndex(String tagName)
    {
        int currentTagIndex = 0;
        IRegistryService registryService = GendocServices.getDefault().getService(IRegistryService.class);
        Object value = (Integer) registryService.get(tagName);
        if (value != null)
        {
            currentTagIndex = (Integer) value;
        }
        return currentTagIndex;
    }

}
