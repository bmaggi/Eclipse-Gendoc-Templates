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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.Monitor;

// TODO javadoc
public interface IProgressMonitorService extends IService
{

    /**
     * Notifies that the main task is beginning. This must only be called once on a given progress monitor.
     * 
     * @param name the name (or description) of the main task
     * @param totalWork the total number of work units into which the main task is been subdivided. If the value is
     *        UNKNOWN the implementation is free to indicate progress in a way which doesn't require the total number of
     *        work units in advance.
     */
    void beginTask(String name, int totalWork);

    /**
     * Gets a delegating wrapper that allows the progress monitor to be used in a context requiring an instance
     * implementing the monitor API.
     * 
     * @return a monitor
     */
    Monitor getDelegatingMonitor();

    /**
     * Returns whether cancelation of current operation has been requested. Long-running operations should poll to see
     * if cancelation has been requested.
     * 
     * @return true if cancellation has been requested, otherwise false
     */
    boolean isCanceled();

    /**
     * Gets the progress monitor.
     * 
     * @return the progress monitor
     */
    IProgressMonitor getMonitor();

    /**
     * Sets the progress monitor.
     * 
     * @param monitor the progress monitor
     */
    void setMonitor(IProgressMonitor monitor);

    /**
     * Creates a new sub-progress monitor. The sub progress monitor uses the given number of work ticks from its parent
     * monitor.
     * 
     * @param ticks the number of work ticks allocated from the parent monitor
     * @param name the name (or description) of the subtask
     * @return the sub monitor
     */
    IProgressMonitor subMonitor(int ticks, String name);

    /**
     * Notifies that a given number of work units of the main task has been completed. Note that this amount represents
     * an installment, as opposed to a cumulative amount of work done to date.
     * 
     * @param work a non-negative number of work units just completed
     */
    void worked(int work);

}
