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
import java.util.Map;

/**
 * The ITag interface represents a tag in a document (either text or structured) that can be processed by a tag handler.
 * 
 * @author Kris Robertson
 */
public interface ITag
{

    /**
     * Returns the attributes of the tag.
     * 
     * @return the tag attributes
     */
    public Map<String, String> getAttributes();

    /**
     * Returns the list of tags that are children of this tag.
     * 
     * @return the list of child tags
     */
    List<ITag> getChildren();

    /**
     * Returns the tag name.
     * 
     * @return the tag name
     */
    String getName();

    /**
     * Returns the parent of this tag.
     * 
     * @return the parent tag
     */
    ITag getParent();

    /**
     * Returns the raw text of the tag.
     * 
     * @return the raw text
     */
    String getRawText();

    /**
     * Returns the value (inner text) of the tag.
     * 
     * @return the tag value
     */
    String getValue();

    /**
     * Returns true if the tag is complete (has been closed).
     * 
     * @return true if the tag is complete, otherwise false
     */
    boolean isComplete();

    /**
     * Returns true if the tag is structured or false if this is a text tag.
     * 
     * @return true if the tag is structured, otherwise false
     */
    boolean isStructured();

    /**
     * Sets the value (inner text) of the tag.
     * 
     * @param value the new value
     */
    void setValue(String value);

}
