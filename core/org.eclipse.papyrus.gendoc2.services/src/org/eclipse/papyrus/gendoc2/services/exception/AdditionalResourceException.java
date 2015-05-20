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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * @author ahaugomm
 * 
 */
public class AdditionalResourceException extends GenDocException
{
    private static final long serialVersionUID = 8140051648015956293L;

    public AdditionalResourceException(Throwable cause)
    {
        super(cause);
    }

    public AdditionalResourceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AdditionalResourceException(String message)
    {
        super(message);
    }
}
