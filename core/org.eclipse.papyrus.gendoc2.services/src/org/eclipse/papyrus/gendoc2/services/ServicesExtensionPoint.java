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
 * Provides string constants for the services extension point.
 * 
 * @author Kris Robertson
 */
public final class ServicesExtensionPoint extends NLS
{

    // extension point id
    public static String EXTENSION_POINT_ID;

    // service
    public static String SERVICE;

    public static String SERVICE_CLASS;

    public static String SERVICE_ID;

    public static String SERVICE_SERVICE_TYPE;

    public static String SERVICE_DEFAULT;

    static
    {
        NLS.initializeMessages(ServicesExtensionPoint.class.getName(), ServicesExtensionPoint.class);
    }

    // the shared instance
    private static final ServicesExtensionPoint instance = new ServicesExtensionPoint();

    private List<ServiceExtension> serviceExtensions;

    private boolean initialized = false;

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    static ServicesExtensionPoint getDefault()
    {
        return ServicesExtensionPoint.instance;
    }

    /**
     * Instantiates a new ServicesExtensionPoint.
     */
    private ServicesExtensionPoint()
    {
    }

    /**
     * Clears the service extensions maintained by the class. The extensions will be re-loaded the next time a client
     * requests an extension.
     */
    public void clear()
    {
        if (this.serviceExtensions != null)
        {
            this.serviceExtensions.clear();
        }
        this.initialized = false;
    }

    /**
     * Loads the service extensions from the extension point.
     */
    private void initialize()
    {
        if (this.serviceExtensions == null)
        {
            this.serviceExtensions = new ArrayList<ServiceExtension>();
        }
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(ServicesExtensionPoint.EXTENSION_POINT_ID);
        for (IConfigurationElement element : elements)
        {
            this.serviceExtensions.add(new ServiceExtension(element));
        }
        this.initialized = true;
    }

    /**
     * Gets the service extension with the given service ID.
     * 
     * @param serviceId the service ID
     * 
     * @return the service extension
     */
    ServiceExtension getServiceExtension(String serviceId)
    {
        if (!this.initialized)
        {
            this.initialize();
        }
        for (ServiceExtension serviceExtension : this.getServiceExtensions())
        {
            if (serviceExtension.getId().equals(serviceId))
            {
                return serviceExtension;
            }
        }
        return null;
    }

    /**
     * Gets a list of all the service extensions registered to the extension point.
     * 
     * @return a list of service extensions
     */
    List<ServiceExtension> getServiceExtensions()
    {
        if (!this.initialized)
        {
            this.initialize();
        }
        return this.serviceExtensions;
    }

}
