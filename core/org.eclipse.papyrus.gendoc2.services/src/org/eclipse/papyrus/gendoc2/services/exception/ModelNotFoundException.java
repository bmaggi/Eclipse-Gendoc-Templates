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
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * @author cbourdeu
 * 
 */
public class ModelNotFoundException extends GenDocException
{
    private static final long serialVersionUID = 4491221245836851151L;

    public ModelNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public ModelNotFoundException(Throwable cause, String message)
    {
        super(message, cause);
    }

    public ModelNotFoundException(String fileName)
    {
        super("The model " + fileName + " can not be loaded");
    }

}
