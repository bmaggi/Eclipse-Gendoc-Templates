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

// TODO javadoc
public interface IService
{

    /**
     * Clears any resources maintained by the service.
     */
    void clear();

    /**
     * Get the ID of this service. This is the id used to reference a service within the services attribute of
     * &lt;config&gt; or &lt;context&gt; tags in a Gendoc2 template.
     * 
     * @return the service id
     */
    String getServiceId();

    /**
     * Sets the service ID. This is the id used to reference a service within the services attribute of &lt;config&gt;
     * or &lt;context&gt; tags in a Gendoc2 template. The ID of a service must not change after it has been registered
     * with GendocServices.
     * 
     * @param serviceId the new service ID to set
     */
    void setServiceId(String serviceId);

}
