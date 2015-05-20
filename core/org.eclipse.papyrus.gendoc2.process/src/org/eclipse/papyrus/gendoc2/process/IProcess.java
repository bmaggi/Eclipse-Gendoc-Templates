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
package org.eclipse.papyrus.gendoc2.process;

import java.util.concurrent.Semaphore;

import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * The IProcess interface must be implemented by Gendoc2 processes.
 * 
 * @author Kris Robertson
 */
public interface IProcess
{

    /**
     * Runs this process.
     * 
     * @throws GenDocException
     */
    void run() throws GenDocException;

    /**
     * Runs this process in parallel.
     * 
     * @param s a semaphore that is released when the process is completed.
     * @throws GenDocException
     */
    void run(Semaphore s) throws GenDocException;

}
