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

import java.util.List;

import org.eclipse.papyrus.gendoc2.process.impl.ProcessExtension;
import org.eclipse.papyrus.gendoc2.services.IService;

/**
 * The process extension service maintains the list of processes to be run by Gendoc2.
 * 
 * @author Kris Robertson
 */
public interface IProcessExtensionService extends IService
{

    /**
     * Gets the total number of processes to be run.
     * 
     * @return the total number of processes
     */
    public int getNumProcesses();

    /**
     * Gets the current list of processes to run. If more than one process is returned they should be run in parallel.
     * 
     * @return the list of processes
     */
    List<ProcessExtension> getProcesses();

    /**
     * Indicates if there are more processes to run. After next has been called the getProcesses method will return the
     * list of processes to run next.
     * 
     * @return true if there are more processes to run, otherwise false
     */
    boolean next();

}
