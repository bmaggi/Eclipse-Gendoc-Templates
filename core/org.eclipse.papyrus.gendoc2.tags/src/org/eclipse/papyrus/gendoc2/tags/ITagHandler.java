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

import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * The ITagHandler interface. Tag handlers are called to process each tag in a document. Only one instance of a handler
 * is created for each type of tag.
 * 
 * @author Kris Robertson
 */
public interface ITagHandler
{

    /**
     * Runs the tag handler for the specified tag.
     * 
     * @param tag the tag to handle
     * @return the text that will replace the tag
     * @throws GenDocException the gen doc exception
     */
    public String run(ITag tag) throws GenDocException;

}
