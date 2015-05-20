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

import org.eclipse.papyrus.gendoc2.tags.ITag;

/**
 * An text tag is raw, unstructured text.
 * 
 * @author Caroline Bourdeu d'Aguerre
 */
public class TextTag extends AbstractTag
{

    /**
     * Instantiates a new TextTag.
     * 
     * @param parent the parent tag
     * @param text the text
     */
    public TextTag(ITag parent, String text)
    {
        super(parent, text);
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
        return false;
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
        buffer.append("TextTag" + "\n");
        buffer.append("{" + "\n");
        buffer.append("\ttext: " + this.getRawText() + "\n");
        buffer.append("}");
        return buffer.toString();
    }

}
