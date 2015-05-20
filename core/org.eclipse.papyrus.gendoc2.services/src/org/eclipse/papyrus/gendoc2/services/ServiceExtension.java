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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

//TODO javadoc
class ServiceExtension extends AbstractExtension
{

    private final String id;

    private final String serviceTypeId;

    private final boolean isDefaultService;

    /**
     * Constructs a new ServiceExtension from a configuration element.
     * 
     * @param configElement the configuration element
     */
    public ServiceExtension(IConfigurationElement configElement)
    {
        super(configElement);
        this.id = this.parseStringAttribute(configElement, ServicesExtensionPoint.SERVICE_ID, true);
        this.serviceTypeId = this.parseStringAttribute(configElement, ServicesExtensionPoint.SERVICE_SERVICE_TYPE, true);
        this.isDefaultService = this.parseBooleanAttribute(configElement, ServicesExtensionPoint.SERVICE_DEFAULT, true);

        // we don't need to store the class name but it's a required attribute...
        this.parseStringAttribute(configElement, ServicesExtensionPoint.SERVICE_CLASS, true);
    }

    /**
     * Returns the service ID.
     * 
     * @return the service ID
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Returns an instance of the service.
     * 
     * @return the service instance
     */
    public IService getService()
    {
        try
        {
            return (IService)this.getConfigElement().createExecutableExtension(ServicesExtensionPoint.SERVICE_CLASS);
        }
        catch (CoreException e)
        {
            String message = "Failed to create service " + this.id + " from " + this.getConfigElement().getDeclaringExtension().getNamespaceIdentifier();
            ServiceExtension.log(message, e);
        }
        return null;
    }

    /**
     * Returns the serviceType.
     * 
     * @return the serviceType
     */
    public ServiceTypeExtension getServiceType()
    {
        return ServiceTypesExtensionPoint.getDefault().getServiceType(this.serviceTypeId);
    }

    /**
     * Returns the serviceType ID.
     * 
     * @return the serviceType ID
     */
    public String getServiceTypeId()
    {
        return this.serviceTypeId;
    }

    /**
     * Returns true if this is the default service for the corresponding serviceType.
     * 
     * @return true if this is the default service, otherwise false
     */
    public boolean isDefault()
    {
        return this.isDefaultService;
    }

}
