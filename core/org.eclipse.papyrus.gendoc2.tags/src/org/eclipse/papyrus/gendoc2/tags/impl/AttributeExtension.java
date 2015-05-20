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
import org.eclipse.papyrus.gendoc2.tags.IAttributeExtension;
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;

/**
 * The org.eclipse.papyrus.gendoc2.tags.IAttributeExtension class describes an attribute of a TagExtension.
 * 
 * @see org.eclipse.papyrus.gendoc2.tags.IAttributeExtension
 * @author Kris Robertson
 */
public class AttributeExtension extends AbstractExtension implements IAttributeExtension
{

    /** The name. */
    private final String name;

    /** The type name. */
    private final String typeName;

    /** The required. */
    private final boolean required;

    /** The default value. */
    private final String defaultValue;

    /**
     * Loads an attribute extension from a configuration element.
     * 
     * @param configElement the configuration element
     * 
     * @return the attribute extension
     */
    public static AttributeExtension load(IConfigurationElement configElement)
    {
        if (!configElement.getName().equals(TagsExtensionPoint.ATTRIBUTE))
        {
            return null;
        }
        try
        {
            AttributeExtension attributeExtension = new AttributeExtension(configElement);
            return attributeExtension;
        }
        catch (Exception e)
        {
            String message = "Failed to load " + TagsExtensionPoint.TAG + " in " + configElement.getDeclaringExtension().getNamespaceIdentifier();
            AbstractExtension.log(message, e);
        }
        return null;
    }

    /**
     * Instantiates a new AttributeExtension from a configuration element.
     * 
     * @param configElement the configuration element
     */
    protected AttributeExtension(IConfigurationElement configElement)
    {
        super(configElement);
        this.name = AttributeExtension.parseStringAttribute(configElement, TagsExtensionPoint.ATTRIBUTE_NAME, true);
        this.typeName = AttributeExtension.parseStringAttribute(configElement, TagsExtensionPoint.ATTRIBUTE_TYPE, true);
        this.required = AttributeExtension.parseBooleanAttribute(configElement, TagsExtensionPoint.ATTRIBUTE_REQUIRED, true);
        this.defaultValue = AttributeExtension.parseStringAttribute(configElement, TagsExtensionPoint.ATTRIBUTE_DEFAULT, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.IAttributeExtension#getDefaultValue()
     */
    public String getDefaultValue()
    {
        return this.defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.IAttributeExtension#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.IAttributeExtension#getType()
     */
    public String getTypeName()
    {
        return this.typeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.IAttributeExtension#isRequired()
     */
    public boolean isRequired()
    {
        return this.required;
    }

}
