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
package org.eclipse.papyrus.gendoc2.tags.handlers;

import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.ITagHandler;

/**
 * A tag handler service returns a tag handler for a specified tag.
 * 
 * @author Kris Robertson
 */
public interface ITagHandlerService extends IService
{

    /**
     * Returns a tag handler for the specified tag. Only one instance of a handler is created for each type of tag.
     * 
     * @param tag the tag to handle
     * 
     * @return the tag handler
     */
    ITagHandler getHandlerFor(ITag tag);

    /**
     * Returns a tag handler for tags with the given name. Only one instance of a handler is created for each type of
     * tag.
     * 
     * @param tagName the tag name
     * 
     * @return the tag handler
     */
    ITagHandler getHandlerFor(String tagName);

}
