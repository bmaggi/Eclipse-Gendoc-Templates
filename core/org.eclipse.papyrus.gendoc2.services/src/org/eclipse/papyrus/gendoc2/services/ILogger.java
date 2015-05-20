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

/**
 * Provides a logging service to which status events can be written.
 */
public interface ILogger extends IService
{

    /** The DEBUG. */
    int DEBUG = 0x10;

    /**
     * Logs a message with given severity.
     * 
     * @param message a human-readable message
     * @param severity the severity; one of (Status) OK, ERROR, INFO, WARNING, or CANCEL
     */
    void log(String message, int severity);

}
