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
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.papyrus.gendoc2.tags.ITagExtension;
import org.eclipse.papyrus.gendoc2.tags.ITagHandler;
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;

/**
 * The Class TagExtension.
 * 
 * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension
 * @author Kris Robertson
 */
public class TagExtension extends AbstractContainerExtension implements ITagExtension
{

    /** The name. */
    private final String name;

    /** The attributes. */
    private final Set<AttributeExtension> attributes = new HashSet<AttributeExtension>();;

    /** The handler. */
    private ITagHandler handler;

    /**
     * Loads a tag extension from a configuration element.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     * 
     * @return the tag extension
     */
    public static TagExtension load(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        if (!configElement.getName().equals(TagsExtensionPoint.TAG))
        {
            return null;
        }
        try
        {
            TagExtension tagExtension = new TagExtension(tagExtensionService, configElement);
            if (tagExtension != null)
            {
                tagExtensionService.getTagExtensions().add(tagExtension);
            }
            return tagExtension;
        }
        catch (Exception e)
        {
            String message = "Failed to load " + TagsExtensionPoint.TAG + " in " + configElement.getDeclaringExtension().getNamespaceIdentifier();
            AbstractExtension.log(message, e);
        }
        return null;
    }

    /**
     * Instantiates a new TagExtension from a configuration element.
     * 
     * @param tagExtensionService the tag extension service
     * @param configElement the configuration element
     */
    protected TagExtension(TagExtensionService tagExtensionService, IConfigurationElement configElement)
    {
        super(tagExtensionService, configElement);
        this.name = TagExtension.parseStringAttribute(configElement, TagsExtensionPoint.TAG_NAME, true);
        this.loadAttributes(configElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension#getAttributes()
     */
    public Set<AttributeExtension> getAttributes()
    {
        return this.attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension#getHandler()
     */
    public ITagHandler getHandler()
    {
        if (this.handler == null)
        {
            try
            {
                this.handler = (ITagHandler) this.getConfigElement().createExecutableExtension(TagsExtensionPoint.TAG_HANDLER);
            }
            catch (CoreException e)
            {
                String message = "Failed to load handler for " + TagsExtensionPoint.TAG + " named " + this.getName() + " in "
                        + this.getConfigElement().getDeclaringExtension().getNamespaceIdentifier();
                TagExtension.log(message, e);
            }
        }
        return this.handler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagExtension#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Loads the attribute of the tag.
     * 
     * @param configElement the configuration element
     */
    protected void loadAttributes(IConfigurationElement configElement)
    {
        IConfigurationElement[] attributeElements = configElement.getChildren(TagsExtensionPoint.ATTRIBUTE);
        for (IConfigurationElement attributeElement : attributeElements)
        {
            AttributeExtension attributeExtension = AttributeExtension.load(attributeElement);
            if (attributeExtension != null)
            {
                this.attributes.add(attributeExtension);
            }
        }
    }

}
