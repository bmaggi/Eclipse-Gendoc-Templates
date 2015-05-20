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
 * Kris Robertson (Atos Origin)
 * kris.robertson@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.parsers;

import java.util.List;

import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;
import org.eclipse.papyrus.gendoc2.tags.ITag;

/**
 * A tag parser service parses text into tags.
 * 
 * @author Kris Robertson
 */
public interface ITagParserService extends IService
{

    /**
     * Parse the given text into tags (ITag).
     * 
     * @param parent the parent tag
     * @param text the text to parse
     * @param tagNames the names of tags to return
     * 
     * @return a list of tags
     * @throws IncompleteTagException
     */
    List<ITag> parse(ITag parent, String text, List<String> tagNames) throws IncompleteTagException;
    
    /**
     * Get index of current parsed tag
     * @param tagName The name of the tag parsed
     * @return the index (ex : first &lt;gendoc&gt; tag, ...)
     */
    int getTagIndex(String tagName);

    /**
     * Count tags inside Registry. Increase a counter name after the tagName inside RegistryService
     * @param tagName name of the tag for which to increase index
     * @return the index increased
     */
    int increaseTagIndex(String tagName);
}
