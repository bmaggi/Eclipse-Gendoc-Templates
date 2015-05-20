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

import org.eclipse.papyrus.gendoc2.services.IService;

/**
 * A tag extension service maintains a list of tag descriptions. The description of a tag is maintained by an
 * ITagExtension.
 * 
 * @author Kris Robertson
 */
public interface ITagExtensionService extends IService
{

    /**
     * Returns the tag extension with the specified name.
     * 
     * @param name the tag name
     * 
     * @return the tag extension
     */
    ITagExtension getTagExtension(String name);

    /**
     * Returns the list of all tag extensions.
     * 
     * @return the list of tag extensions
     */
    Set< ? extends ITagExtension> getTagExtensions();

    /**
     * Returns the list of tag extensions that can exist at the top level.
     * 
     * @return the list of tag extensions
     */
    Set< ? extends ITagExtension> getTopLevelTagExtensions();

    /**
     * Returns a list of names of tags that can exist at the top level.
     * 
     * @return the list of tag names
     */
    List<String> getTopLevelTagNames();

}
