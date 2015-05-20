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
 *  Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * The Class ElementNotFoundException.
 */
public class ElementNotFoundException extends GenDocException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3573158148680243861L;

    public ElementNotFoundException(String elementName, String modelURI)
    {
        this(elementName, "", modelURI);
    }

    /**
     * Instantiates a new element not found exception.
     * 
     * @param elementName the element name
     * @param modelName the model name
     * @param modelURI the model uri
     */
    public ElementNotFoundException(String elementName, String modelName, String modelURI)
    {
        super("The element '" + elementName + "' was not found in the model '" + modelName + "' [" + modelURI + "].");
    }
}
