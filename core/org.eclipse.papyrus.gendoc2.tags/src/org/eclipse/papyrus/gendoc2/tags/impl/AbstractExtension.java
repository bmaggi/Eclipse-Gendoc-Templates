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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;

/**
 * The AbstractExtension class provides a common base for extension classes.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractExtension
{

    /** The config element. */
    private final IConfigurationElement configElement;

    /**
     * Logs a message.
     * 
     * @param message a human-readable message
     * @param e an exception
     */
    protected static void log(String message, Exception e)
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        logger.log(message, IStatus.WARNING);
    }

    /**
     * Reads the configuration element and returns the value of the named attribute as a boolean.
     * 
     * @param configElement the configuration element
     * @param attributeName the attribute name
     * @param isRequired true if the attribute is required, otherwise false
     * 
     * @return the attribute value
     * 
     * @throws IllegalArgumentException if a required attribute was not found
     */
    protected static boolean parseBooleanAttribute(IConfigurationElement configElement, String attributeName, boolean isRequired) throws IllegalArgumentException
    {
        boolean value = Boolean.parseBoolean(AbstractExtension.parseStringAttribute(configElement, attributeName, isRequired));
        return value;
    }

    /**
     * Reads the configuration element and returns the value of the named attribute as a string.
     * 
     * @param configElement the configuration element
     * @param attributeName the attribute name
     * @param isRequired true if the attribute is required, otherwise false
     * 
     * @return the attribute value
     * 
     * @throws IllegalArgumentException if a required attribute was not found
     */
    protected static String parseStringAttribute(IConfigurationElement configElement, String attributeName, boolean isRequired) throws IllegalArgumentException
    {
        String value = configElement.getAttribute(attributeName);
        if (value != null)
        {
            return value;
        }
        if (isRequired)
        {
            throw new IllegalArgumentException("Required attribute " + attributeName + " was not found.");
        }
        return value;
    }

    /**
     * Instantiates a new AbstractExtension from a configuration element.
     * 
     * @param configElement the configuration element
     */
    protected AbstractExtension(IConfigurationElement configElement)
    {
        this.configElement = configElement;
    }

    /**
     * Returns the configuration element that the extension was loaded from.
     * 
     * @return the configuration element
     */
    protected IConfigurationElement getConfigElement()
    {
        return this.configElement;
    }

}
