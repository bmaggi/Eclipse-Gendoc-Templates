/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.services;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;

//TODO javadoc
class AbstractExtension
{

    private final IConfigurationElement configElement;

    protected static void log(String message, Exception e)
    {
        ILogger loggerService = GendocServices.getDefault().getService(ILogger.class);
        loggerService.log(message, IStatus.WARNING);
    }

    protected AbstractExtension(IConfigurationElement configElement)
    {
        this.configElement = configElement;
    }

    protected IConfigurationElement getConfigElement()
    {
        return this.configElement;
    }

    protected boolean parseBooleanAttribute(IConfigurationElement configElement, String attributeName, boolean isRequired)
    {
        boolean value = Boolean.parseBoolean(this.parseStringAttribute(configElement, attributeName, isRequired));
        return value;
    }

    protected String parseStringAttribute(IConfigurationElement configElement, String attributeName, boolean isRequired)
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

}
