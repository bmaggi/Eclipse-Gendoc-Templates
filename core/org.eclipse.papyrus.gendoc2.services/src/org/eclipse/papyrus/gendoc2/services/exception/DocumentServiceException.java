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
public class DocumentServiceException extends GenDocException
{

    private static final long serialVersionUID = -703913940715686484L;

    public DocumentServiceException(Throwable cause)
    {
        super(cause);
    }

    public DocumentServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DocumentServiceException(String message)
    {
        super(message);
    }

}
