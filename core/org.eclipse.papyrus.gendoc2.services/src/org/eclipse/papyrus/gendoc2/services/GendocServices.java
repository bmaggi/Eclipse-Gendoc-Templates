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
 * Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.gendoc2.services.exception.ServiceException;

/**
 * GendocServices creates and maintains the services used during the Gendoc2 generation process. Clients can request a
 * service by calling the <samp>getService(Class)</samp> method with the required service type.
 * 
 * @author Kris Robertson
 */
public final class GendocServices
{

    /** The shared instance. */
    private static GendocServices instance = new GendocServices();

    /** The thread on which this instance was created. */
    private final Thread initThread;

    /** True if this instance has been initialized, otherwise false. */
    private boolean initialized = false;

    /** Map of serviceTypeId to serviceId. */
    private final Map<Class< ? extends IService>, String> serviceIds = new HashMap<Class< ? extends IService>, String>();

    /** Map of serviceId to IService. */
    private final Map<String, IService> services = new HashMap<String, IService>();

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static GendocServices getDefault()
    {
        return GendocServices.instance;
    }

    /**
     * Constructs a new GendocServices.
     */
    private GendocServices()
    {
        super();
        this.initThread = Thread.currentThread();
    }

    /**
     * Clears all service types and service instances. These must be re-initialized the next time a service is
     * requested.
     */
    public void clear()
    {
        // clear extension point services
        ServiceTypesExtensionPoint.getDefault().clear();
        ServicesExtensionPoint.getDefault().clear();
        // clear services
        for (IService service : this.services.values())
        {
            service.clear();
        }
        // clear service maps
        this.services.clear();
        this.serviceIds.clear();
        // clear initialized flag
        this.initialized = false;
    }

    /**
     * Returns the service corresponding to the given serviceType. If the thread calling is not the init thread, this
     * method is called in a synchronized way.
     * 
     * @param serviceType the service type
     * @return the service
     */
    @SuppressWarnings("unchecked")
    public <T extends IService> T getService(Class< ? extends IService> serviceType)
    {
        T service = null;
        if (!this.initialized)
        {
            this.initialize();
        }
        try
        {
            if (Thread.currentThread() == this.initThread)
            {
                service = (T) this.doGetService(serviceType);
            }
            else
            {
                service = (T) this.doGetSynchronizedService(serviceType);
            }
            return service;
        }
        catch (ClassCastException e)
        {
            throw new RuntimeException("Error getting service for serviceType (" + serviceType.getName() + ")", e);
        }
    }

    /**
     * Sets the given service as the current service for the given service type.
     * 
     * @param serviceType the service type
     * @param service the service
     */
    public void setService(Class< ? extends IService> serviceType, IService service)
    {
        if (serviceType.isInstance(service))
        {
            String serviceId = service.getServiceId();
            if (serviceId != null)
            {
                this.serviceIds.put(serviceType, serviceId);
                this.services.put(serviceId, service);
            }
            else
            {
                throw new RuntimeException("Service of type '" + serviceType.getName() + " has a null id.");
            }
        }
        else
        {
            throw new RuntimeException("Service id='" + service.getServiceId() + "' is not an instance of " + serviceType.getName() + ".");
        }
    }

    /**
     * Sets the service with the given ID as the current service. The identified service will be returned to any client
     * that requests a service of it's type.
     * 
     * @param serviceId the service ID
     */
    public void setService(String serviceId)
    {
        if (!this.initialized)
        {
            this.initialize();
        }
        this.setServiceId(this.getServiceType(serviceId), serviceId);
    }

    /**
     * Creates a new service instance.
     * 
     * @param serviceTypeId the serviceType ID.
     * @param serviceId the service ID
     * @return the service
     */
    private IService createService(Class< ? extends IService> serviceType, String serviceId)
    {
        IService service = null;
        ServiceExtension serviceExtension = ServicesExtensionPoint.getDefault().getServiceExtension(serviceId);
        if (serviceExtension != null)
        {
            service = serviceExtension.getService();
            // is the service an instance of the service type's interface
            ServiceTypeExtension serviceTypeExtension = serviceExtension.getServiceType();
            Class< ? extends IService> serviceTypeInterface = serviceTypeExtension.getInterface();
            if (!serviceTypeInterface.isInstance(service))
            {
                throw new RuntimeException("Service '" + serviceId + "' is not an instance of the interface '" + serviceTypeInterface.getName() + "' required for service type '"
                        + serviceTypeExtension.getId() + "'.");
            }
        }
        if (service == null)
        {
            throw new RuntimeException("Service '" + serviceId + "' is null.");
        }
        return service;
    }

    /**
     * Gets the service in a standard way.
     * 
     * @param serviceTypeId the serviceType ID of the service to get
     * @return the service
     */
    private IService doGetService(Class< ? extends IService> serviceType)
    {
    	ILogger logger = null ;
    	if (!ILogger.class.equals(serviceType))
    	{
    		logger = this.getService(ILogger.class);
    	}
        String serviceId = this.serviceIds.get(serviceType);
        if (serviceId == null)
        {
            throw new RuntimeException("No service has been selected for service type " + serviceType.getName() + ".");
        }
        IService service = this.services.get(serviceId);
        if (service == null)
        {
            service = this.createService(serviceType, serviceId);
            this.services.put(serviceId, service);
            if (logger != null)
            {
            	logger.log("Service '" + serviceType + "' : " + service.getClass().getName(), ILogger.DEBUG);
            }
        }
        return service;
    }

    /**
     * Gets the service in a synchronized way.
     * 
     * @param serviceTypeId the serviceType ID of the service to get
     * @return the service
     */
    private synchronized Object doGetSynchronizedService(Class< ? extends IService> serviceType)
    {
        ((ILogger) this.doGetService(ILogger.class)).log("Synchronized way", ILogger.DEBUG);
        return this.doGetService(serviceType);
    }

    /**
     * Returns the serviceType ID for the given service ID.
     * 
     * @param serviceId the service ID.
     * @return the serviceType ID.
     */
    private Class< ? extends IService> getServiceType(String serviceId)
    {
        ServiceExtension serviceExtension = ServicesExtensionPoint.getDefault().getServiceExtension(serviceId);
        return serviceExtension.getServiceType().getInterface();
    }

    /**
     * Initializes the default service ID for each serviceType.
     */
    private void initialize()
    {
        this.clear();
        for (ServiceTypeExtension serviceTypeExtension : ServiceTypesExtensionPoint.getDefault().getServiceTypeExtensions())
        {
            try
            {
                String serviceId = serviceTypeExtension.getDefaultServiceExtension().getId();
                this.serviceIds.put(serviceTypeExtension.getInterface(), serviceId);
            }
            catch (ServiceException e)
            {
                ((ILogger) this.doGetService(ILogger.class)).log("No default service found for service type '" + serviceTypeExtension.getId() + "'.", Status.WARNING);
            }
        }
        this.initialized = true;
    }

    /**
     * Sets the service ID for the given serviceType.
     * 
     * @param serviceTypeId the serviceType ID
     * @param serviceId the service ID
     */
    private void setServiceId(Class< ? extends IService> serviceType, String serviceId)
    {
        this.serviceIds.put(serviceType, serviceId);
    }

}
