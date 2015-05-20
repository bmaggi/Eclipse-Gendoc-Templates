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
package org.eclipse.papyrus.gendoc2.tags.impl;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;

/**
 * A TagRef is used to reference a previously registered tag on the tags extension point.
 * 
 * @author Kris Robertson
 */
public class TagRef
{

    /**
     * Loads a tagref from a configuration element.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     * 
     * @return the tag name
     */
    public static String load(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        if (!configElement.getName().equals(TagsExtensionPoint.TAGREF))
        {
            return null;
        }
        String name = configElement.getAttribute(TagsExtensionPoint.TAGREF_NAME);
        if (name != null)
        {
            return name;
        }
        return null;
    }

}
