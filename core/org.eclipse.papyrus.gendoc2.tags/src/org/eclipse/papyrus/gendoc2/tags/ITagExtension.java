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
package org.eclipse.papyrus.gendoc2.tags;

import java.util.List;
import java.util.Set;

/**
 * The ITagExtension interface describes a tag that can appear in a document.
 * 
 * @author Kris Robertson
 */
public interface ITagExtension
{

    /**
     * Returns the attributes of the tag.
     * 
     * @return the list of attributes
     */
    public Set< ? extends IAttributeExtension> getAttributes();

    /**
     * Returns the handler for this tag.
     * 
     * @return the tag handler
     */
    public ITagHandler getHandler();

    /**
     * Returns the tag name.
     * 
     * @return the tag name
     */
    public String getName();

    /**
     * Returns the list of sub-tag names after categories have been expanded.
     * 
     * @return the list of sub-tag names
     */
    public List<String> getSubTagNames();

    /**
     * Returns the list of sub-tags after categories have been expanded.
     * 
     * @return the list of sub-tags
     */
    public Set< ? extends ITagExtension> getSubTags();

}
