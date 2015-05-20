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

package org.eclipse.papyrus.gendoc2.tags.parsers.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.papyrus.gendoc2.tags.ITag;

/**
 * An abstract base class for tags that provides default method implementations.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractTag implements ITag
{

    /** The children. */
    private List<ITag> children;

    /** The parent. */
    private final ITag parent;

    /** The raw text. */
    private String rawText;

    /**
     * Instantiates a new AbstractTag.
     * 
     * @param parent the parent tag
     * @param rawText the raw text
     */
    protected AbstractTag(ITag parent, String rawText)
    {
        this.parent = parent;
        this.rawText = rawText;
        if (parent != null)
        {
            this.parent.getChildren().add(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getAttributes()
     */
    public Map<String, String> getAttributes()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getChildren()
     */
    public List<ITag> getChildren()
    {
        if (this.children == null)
        {
            this.children = new LinkedList<ITag>();
        }
        return this.children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getName()
     */
    public String getName()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getChildren()
     */
    public ITag getParent()
    {
        return this.parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getRawText()
     */
    public String getRawText()
    {
        return this.rawText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#getValue()
     */
    public String getValue()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITag#setValue(String)
     */
    public void setValue(String value)
    {
    }

}
