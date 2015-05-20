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
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;

/**
 * The AbstractContainerExtension class provides a common base for extensions that can contain tag or category
 * extensions.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractContainerExtension extends AbstractExtension
{

    /** The tag extension service. */
    protected final TagExtensionService tagExtensionService;

    /** The child category names. */
    protected final Set<String> childCategoryNames = new HashSet<String>();

    /** The child tag names. */
    protected final Set<String> childTagNames = new HashSet<String>();

    /** The child categories. */
    private Set<CategoryExtension> childCategories;

    /** The child tags. */
    private Set<TagExtension> childTags;

    /** The sub tags. */
    private Set<TagExtension> subTags;

    /** The sub tag names. */
    private List<String> subTagNames;

    /**
     * Instantiates a new AbstractContainerExtension from a configuration element.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     */
    protected AbstractContainerExtension(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        super(configElement);
        this.tagExtensionService = tagExtensionService;
        this.loadChildren(configElement);
    }

    /**
     * Returns the set of sub-tag names after categories have been expanded.
     * 
     * @return the set of sub-tag names
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension#getSubTagNames()
     */
    public List<String> getSubTagNames()
    {
        if (this.subTagNames == null)
        {
            this.subTagNames = new LinkedList<String>();
            for (TagExtension tagExtension : this.getSubTags())
            {
                this.subTagNames.add(tagExtension.getName());
            }
        }
        return this.subTagNames;
    }

    /**
     * Returns the set of sub-tags after categories have been expanded.
     * 
     * @return the set of sub tags
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension#getSubTags()
     */
    public Set<TagExtension> getSubTags()
    {
        if (this.subTags == null)
        {
            this.subTags = new HashSet<TagExtension>();
            this.subTags.addAll(this.getChildTags());
            this.subTags.addAll(this.expand(this.getChildCategories(), new HashSet<CategoryExtension>()));
        }
        return this.subTags;
    }

    /**
     * Returns an expanded set of sub-tags by recursively expanding categories.
     * 
     * @param categories the categories to expand
     * @param exclude the categories to exclude (those that have already been expanded)
     * 
     * @return the set of sub-tags
     */
    protected Set<TagExtension> expand(Set<CategoryExtension> categories, Set<CategoryExtension> exclude)
    {
        Set<TagExtension> tags = new HashSet<TagExtension>();
        for (CategoryExtension category : categories)
        {
            if (!exclude.contains(category))
            {
                exclude.add(category);
                tags.addAll(category.getChildTags());
                tags.addAll(this.expand(category.getChildCategories(), exclude));
            }
        }
        return tags;
    }

    /**
     * Returns the set of category extensions that are children of this extension.
     * 
     * @return the set of category extensions
     */
    protected Set<CategoryExtension> getChildCategories()
    {
        if (this.childCategories == null)
        {
            this.childCategories = new HashSet<CategoryExtension>();
            for (String name : this.childCategoryNames)
            {
                this.childCategories.add(this.getTagExtensionService().getCategoryExtension(name));
            }
        }
        return this.childCategories;
    }

    /**
     * Returns the set of tag extensions that are children of this extension.
     * 
     * @return the set of tag extensions
     */
    protected Set<TagExtension> getChildTags()
    {
        if (this.childTags == null)
        {
            this.childTags = new HashSet<TagExtension>();
            for (String name : this.childTagNames)
            {
                this.childTags.add(this.getTagExtensionService().getTagExtension(name));
            }
        }
        return this.childTags;
    }

    /**
     * Returns the tag extension service that loaded this extension.
     * 
     * @return the tag extension service
     */
    protected TagExtensionService getTagExtensionService()
    {
        return this.tagExtensionService;
    }

    /**
     * Loads category extensions from a configuration element and returns a list of category names.
     * 
     * @param configElement the configuration element
     * 
     * @return the list of category names
     */
    protected List<String> loadCategories(IConfigurationElement configElement)
    {
        List<String> names = new LinkedList<String>();
        IConfigurationElement[] elements = configElement.getChildren(TagsExtensionPoint.CATEGORY);
        for (IConfigurationElement element : elements)
        {
            CategoryExtension categoryExtension = CategoryExtension.load(this.tagExtensionService, element);
            if (categoryExtension != null)
            {
                names.add(categoryExtension.getName());
            }
        }
        return names;
    }

    /**
     * Loads this extensions from a configuration element as children of this extension.
     * 
     * @param configElement the configuration element
     */
    protected void loadChildren(IConfigurationElement configElement)
    {
        this.childCategoryNames.addAll(this.loadCategories(configElement));
        this.childTagNames.addAll(this.loadTags(configElement));
        this.childTagNames.addAll(this.loadTagRefs(configElement));
    }

    /**
     * Loads tagref extensions from a configuration element and returns a list of tag names.
     * 
     * @param configElement the configuration element
     * 
     * @return the list of tag names
     */
    protected List<String> loadTagRefs(IConfigurationElement configElement)
    {
        List<String> names = new LinkedList<String>();
        IConfigurationElement[] elements = configElement.getChildren(TagsExtensionPoint.TAGREF);
        for (IConfigurationElement element : elements)
        {
            String name = TagRef.load(this.tagExtensionService, element);
            if (name != null)
            {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Loads tag extensions from a configuration element and returns a list of tag names.
     * 
     * @param configElement the configuration element
     * 
     * @return the list of tag names
     */
    protected List<String> loadTags(IConfigurationElement configElement)
    {
        List<String> names = new LinkedList<String>();
        IConfigurationElement[] elements = configElement.getChildren(TagsExtensionPoint.TAG);
        for (IConfigurationElement element : elements)
        {
            TagExtension tagExtension = TagExtension.load(this.tagExtensionService, element);
            if (tagExtension != null)
            {
                names.add(tagExtension.getName());
            }
        }
        return names;
    }

}
