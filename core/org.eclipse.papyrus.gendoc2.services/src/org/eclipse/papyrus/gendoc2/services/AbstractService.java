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
import org.eclipse.core.runtime.IExecutableExtension;

/**
 * The AbstractService class provides a convenient base for classes that implement the IService interface.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractService implements IExecutableExtension, IService
{

    private String serviceId;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    public void clear()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#getServiceId()
     */
    public String getServiceId()
    {
        return this.serviceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)
     */
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException
    {
        String serviceIdAttribute = config.getAttribute(ServicesExtensionPoint.SERVICE_ID);
        this.setServiceId(serviceIdAttribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#setServiceId(String)
     */
    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

}
