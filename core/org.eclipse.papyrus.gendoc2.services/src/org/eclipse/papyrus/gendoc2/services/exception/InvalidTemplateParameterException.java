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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * @author ahaugomm
 * 
 */
public class InvalidTemplateParameterException extends GenDocException
{
    private static final long serialVersionUID = 6055959616365523427L;

    public InvalidTemplateParameterException(String paramName, String message)
    {
        super("Invalid parameter '" + paramName + "' :" + message);
    }
}
