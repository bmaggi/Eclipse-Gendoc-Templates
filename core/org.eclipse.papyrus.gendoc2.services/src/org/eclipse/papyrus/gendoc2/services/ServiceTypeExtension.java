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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrus.gendoc2.services.exception.ServiceException;

//TODO javadoc
class ServiceTypeExtension extends AbstractExtension
{

    private final String id;

    private final String interfaceName;

    private List<ServiceExtension> serviceExtensions;

    private ServiceExtension defaultServiceExtension;

    private Class< ? extends IService> interfaceClass;

    /**
     * Constructs a new ServiceTypeExtension from a configuration element.
     * 
     * @param configElement the configuration element
     */
    public ServiceTypeExtension(IConfigurationElement configElement)
    {
        super(configElement);
        this.id = this.parseStringAttribute(configElement, ServiceTypesExtensionPoint.SERVICE_TYPE_ID, true);
        this.interfaceName = this.parseStringAttribute(configElement, ServiceTypesExtensionPoint.SERVICE_TYPE_INTERFACE, true);
    }

    /**
     * Returns the default service for this serviceType.
     * 
     * @return the default service
     * @throws ServiceException
     */
    public ServiceExtension getDefaultServiceExtension() throws ServiceException
    {
        if (this.defaultServiceExtension == null)
        {
            boolean found = false;
            for (ServiceExtension serviceExtension : this.getServiceExtensions())
            {
                if (serviceExtension.isDefault())
                {
                    if (!found)
                    {
                        this.defaultServiceExtension = serviceExtension;
                        found = true;
                    }
                    else
                    {
                        throw new ServiceException("Multiple service extensions declared as default for serviceType " + this.getId());
                    }
                }
            }
            if (!found)
            {
                throw new ServiceException("No default service found for serviceType " + this.getId());
            }
        }
        return this.defaultServiceExtension;
    }

    /**
     * Returns the serviceType ID.
     * 
     * @return the serviceType ID
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Returns the name of the interface that services of this type must implement.
     * 
     * @return the interface name
     */
    @SuppressWarnings("unchecked")
    public Class< ? extends IService> getInterface()
    {
        try
        {
            this.interfaceClass = (Class< ? extends IService>) this.getClass().getClassLoader().loadClass(this.interfaceName);
        }
        catch (ClassNotFoundException e)
        {
            try
            {
                this.interfaceClass = (Class< ? extends IService>)Platform.getBundle(this.getConfigElement().getContributor().getName()).loadClass(this.interfaceName);
            }
            catch (InvalidRegistryObjectException e1)
            {
                throw new RuntimeException("Unable to create service type interface '" + this.interfaceName + ".", e1);
            }
            catch (ClassNotFoundException e1)
            {
                throw new RuntimeException("Unable to create service type interface '" + this.interfaceName + ".", e1);
            }
            catch (ClassCastException e3)
            {
            	throw new RuntimeException("Unable to create service type interface '" + this.interfaceName + ".", e3);
            }
        }
        return this.interfaceClass;
    }

    /**
     * Returns the name of the interface that services of this type must implement.
     * 
     * @return the interface name
     */
    public String getInterfaceName()
    {
        return this.interfaceName;
    }

    /**
     * Returns the registered services that are for this serviceType.
     * 
     * @return a list of services
     */
    public List<ServiceExtension> getServiceExtensions()
    {
        if (this.serviceExtensions == null)
        {
            this.serviceExtensions = new ArrayList<ServiceExtension>();
            for (ServiceExtension serviceExtension : ServicesExtensionPoint.getDefault().getServiceExtensions())
            {
                if (serviceExtension.getServiceTypeId().equals(this.id))
                {
                    this.serviceExtensions.add(serviceExtension);
                }
            }
        }
        return this.serviceExtensions;
    }

}
