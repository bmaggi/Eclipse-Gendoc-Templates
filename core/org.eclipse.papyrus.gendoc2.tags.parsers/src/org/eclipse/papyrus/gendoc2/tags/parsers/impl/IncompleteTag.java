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
 *   Caroline Bourdeu d'Aguerre (Atos Origin)
 *   caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.parsers.impl;

import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;
import org.eclipse.papyrus.gendoc2.tags.ITag;

/**
 * An incomplete tag is a structured tag that has not been closed.
 * 
 * @author Caroline Bourdeu d'Aguerre
 */
public class IncompleteTag extends StructuredTag
{

    /**
     * Instantiates a new IncompleteTag.
     * 
     * @param parent the parent tag
     * @param text the text
     * @param name the name
     * @throws IncompleteTagException 
     */
    public IncompleteTag(ITag parent, String text, String name) throws IncompleteTagException
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
        return false;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("IncompleteTag" + "\n");
        buffer.append("{" + "\n");
        buffer.append("\tname: " + this.getName() + "\n");
        buffer.append("\ttext: " + this.getRawText() + "\n");
        buffer.append("}");
        return buffer.toString();
    }

}

