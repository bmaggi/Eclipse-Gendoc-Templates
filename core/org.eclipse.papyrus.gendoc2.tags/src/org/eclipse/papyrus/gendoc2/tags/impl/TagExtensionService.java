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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.tags.ITagExtensionService;
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;

/**
 * The TagExtensionService class maintains a list of extensions registered to the tags extension point.
 * 
 * @see org.eclipse.papyrus.gendoc2.tags.ITagExtensionService
 * @author Kris Robertson
 */
public class TagExtensionService extends AbstractService implements ITagExtensionService
{

    /** The category extensions. */
    private Set<CategoryExtension> categoryExtensions = new HashSet<CategoryExtension>();

    /** The tag extensions. */
    private Set<TagExtension> tagExtensions = new HashSet<TagExtension>();

    /**
     * Instantiates a new tag extension service.
     */
    public TagExtensionService()
    {
        IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(TagsExtensionPoint.EXTENSION_POINT_ID);
        for (IConfigurationElement configElement : configElements)
        {
            if (configElement.getName().equals(TagsExtensionPoint.CATEGORY))
            {
                CategoryExtension.load(this, configElement);
            }
            else if (configElement.getName().equals(TagsExtensionPoint.TAG))
            {
                TagExtension.load(this, configElement);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    @Override
    public void clear()
    {
        this.categoryExtensions.clear();
        this.tagExtensions.clear();
    }

    /**
     * Returns the category extension with the specified name.
     * 
     * @param name the category name
     * @return the category extension
     */
    public CategoryExtension getCategoryExtension(String name)
    {
        for (CategoryExtension tagCategoryExtension : this.getCategoryExtensions())
        {
            if (tagCategoryExtension.getName().equals(name))
            {
                return tagCategoryExtension;
            }
        }
        return null;
    }

    /**
     * Returns the list of all category extensions.
     * 
     * @return the list of category extensions
     */
    public Set<CategoryExtension> getCategoryExtensions()
    {
        return this.categoryExtensions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtensionService#getTagExtension(String)
     */
    public TagExtension getTagExtension(String name)
    {
        for (TagExtension tagExtension : this.getTagExtensions())
        {
            if (tagExtension.getName().equals(name))
            {
                return tagExtension;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtensionService#getTagExtensions()
     */
    public Set<TagExtension> getTagExtensions()
    {
        return this.tagExtensions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtensionService#getTopLevelTagExtensions()
     */
    public Set<TagExtension> getTopLevelTagExtensions()
    {
        CategoryExtension categoryExtension = this.getCategoryExtension(TagsExtensionPoint.TOP_LEVEL_CATEGORY);
        if (categoryExtension != null)
        {
            return categoryExtension.getSubTags();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtensionService#getTopLevelTagNames()
     */
    public List<String> getTopLevelTagNames()
    {
        List<String> tagNames = new LinkedList<String>();
        for (TagExtension tagExtension : this.getTopLevelTagExtensions())
        {
            tagNames.add(tagExtension.getName());
        }
        return tagNames;
    }

}
