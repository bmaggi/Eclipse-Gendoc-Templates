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
package org.eclipse.papyrus.gendoc2.tags;

/**
 * The IAttributeExtension interface describes an attribute of a tag.
 * 
 * @author Kris Robertson
 */
public interface IAttributeExtension
{

    /**
     * Returns the default value of the attribute.
     * 
     * @return the default value
     */
    public String getDefaultValue();

    /**
     * Returns the name of the attribute.
     * 
     * @return the attribute name
     */
    public String getName();

    /**
     * Returns the name of the attribute's type.
     * 
     * @return the attribute type name
     */
    public String getTypeName();

    /**
     * Returns true if the attribute is required.
     * 
     * @return true if the attribute is required, otherwise false
     */
    public boolean isRequired();

}
