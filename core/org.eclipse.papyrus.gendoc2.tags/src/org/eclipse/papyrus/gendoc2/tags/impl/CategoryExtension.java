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
 * The CategoryExtension class describes a group of tag extensions.
 * 
 * @author Kris Robertson
 */
public class CategoryExtension extends AbstractContainerExtension
{

    /** The name. */
    private final String name;

    /**
     * Loads a category extension from a configuration element.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     * 
     * @return the category extension
     */
    public static CategoryExtension load(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        if (!configElement.getName().equals(TagsExtensionPoint.CATEGORY))
        {
            return null;
        }
        try
        {
            String name = AbstractExtension.parseStringAttribute(configElement, "name", false);
            CategoryExtension categoryExtension = tagExtensionService.getCategoryExtension(name);
            if (categoryExtension != null)
            {
                categoryExtension.loadChildren(configElement);
            }
            else
            {
                categoryExtension = new CategoryExtension(tagExtensionService, configElement);
                if (categoryExtension != null)
                {
                    tagExtensionService.getCategoryExtensions().add(categoryExtension);
                }
            }
            return categoryExtension;
        }
        catch (Exception e)
        {
            String message = "Failed to load " + TagsExtensionPoint.TAG + " in " + configElement.getDeclaringExtension().getNamespaceIdentifier();
            AbstractExtension.log(message, e);
        }
        return null;
    }

    /**
     * Instantiates a new category extension.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     */
    protected CategoryExtension(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        super(tagExtensionService, configElement);
        this.name = CategoryExtension.parseStringAttribute(configElement, TagsExtensionPoint.TAG_NAME, true);
    }

    /**
     * Returns the name of the category.
     * 
     * @return the category name
     */
    public String getName()
    {
        return this.name;
    }

}
