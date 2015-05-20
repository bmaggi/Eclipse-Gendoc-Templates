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
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**
 * Provides string constants for the service types extension point.
 * 
 * @author Kris Robertson
 */
public final class ServiceTypesExtensionPoint extends NLS
{

    // extension point id
    public static String EXTENSION_POINT_ID;

    // serviceType
    public static String SERVICE_TYPE;

    public static String SERVICE_TYPE_ID;

    public static String SERVICE_TYPE_INTERFACE;

    static
    {
        NLS.initializeMessages(ServiceTypesExtensionPoint.class.getName(), ServiceTypesExtensionPoint.class);
    }

    // the shared instance
    private static final ServiceTypesExtensionPoint instance = new ServiceTypesExtensionPoint();

    private List<ServiceTypeExtension> serviceTypeExtensions;

    private boolean initialized = false;

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    static ServiceTypesExtensionPoint getDefault()
    {
        return ServiceTypesExtensionPoint.instance;
    }

    /**
     * Instantiates a new ServiceTypesExtensionPoint.
     */
    private ServiceTypesExtensionPoint()
    {
    }

    /**
     * Clears the service type extensions maintained by the class. The extensions will be re-loaded the next time a
     * client requests an extension.
     */
    public void clear()
    {
        if (this.serviceTypeExtensions != null)
        {
            this.serviceTypeExtensions.clear();
        }
        this.initialized = false;
    }

    /**
     * Loads the serviceType extensions from the extension point.
     */
    private void initialize()
    {
        if (this.serviceTypeExtensions == null)
        {
            this.serviceTypeExtensions = new ArrayList<ServiceTypeExtension>();
        }
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(ServiceTypesExtensionPoint.EXTENSION_POINT_ID);
        for (IConfigurationElement element : elements)
        {
            this.serviceTypeExtensions.add(new ServiceTypeExtension(element));
        }
        this.initialized = true;
    }

    /**
     * Gets the service type extension with the given service ID.
     * 
     * @param serviceTypeId the service type ID
     * 
     * @return the service type extension
     */
    ServiceTypeExtension getServiceType(String serviceTypeId)
    {
        if (!this.initialized)
        {
            this.initialize();
        }
        for (ServiceTypeExtension serviceTypeExtension : this.getServiceTypeExtensions())
        {
            if (serviceTypeExtension.getId().equals(serviceTypeId))
            {
                return serviceTypeExtension;
            }
        }
        return null;
    }

    /**
     * Gets a list of all the serviceType extensions registered to the extension point.
     * 
     * @return a list of serviceType extensions
     */
    List<ServiceTypeExtension> getServiceTypeExtensions()
    {
        if (!this.initialized)
        {
            this.initialize();
        }
        return this.serviceTypeExtensions;
    }

}
