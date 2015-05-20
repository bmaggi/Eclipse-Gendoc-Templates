/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.process.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.gendoc2.process.IProcessExtensionService;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.exception.ServiceException;

/**
 * The process extension service maintains the list of processes to be run by Gendoc2.
 * 
 * @author Kris Robertson
 * 
 * @see org.eclipse.papyrus.gendoc2.process.IProcessExtensionService
 */
public class ProcessExtensionService extends AbstractService implements IProcessExtensionService
{

    /** Processes that are waiting for others to run (still have dependencies). */
    private List<ProcessExtension> waiting = new LinkedList<ProcessExtension>();

    /** Processes that are ready to run (have no dependencies). */
    private List<ProcessExtension> ready = new ArrayList<ProcessExtension>();

    /** The next processes to run. */
    private List<ProcessExtension> current = new ArrayList<ProcessExtension>();

    /**
     * Instantiates a new ProcessExtensionService.
     * 
     * @throws ServiceException
     */
    public ProcessExtensionService() throws ServiceException
    {
        // load process extensions
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(ProcessesExtensionPoint.EXTENSION_POINT_ID);
        for (IConfigurationElement element : elements)
        {
            ProcessExtension extension = new ProcessExtension(element);
            this.waiting.add(extension);
        }
        // resolve references
        for (ProcessExtension extension : this.waiting)
        {
            for (String ref : extension.getPredecessors())
            {
                ProcessExtension predecessor = this.getProcessExtension(ref);
                if (predecessor != null)
                {
                    extension.getDependencies().add(predecessor);
                }
            }
            for (String ref : extension.getSuccessors())
            {
                ProcessExtension successor = this.getProcessExtension(ref);
                if (successor != null)
                {
                    successor.getDependencies().add(extension);
                }
            }
        }

        // check for cyclic dependencies using a depth-first topological sort
        StringBuffer message = new StringBuffer("Cyclic dependency detected in process extensions: ");
        for (ProcessExtension processExtension : this.waiting)
        {
            if (processExtension.findCyclicDependencies(message))
            {
                ((IGendocDiagnostician) GendocServices.getDefault().getService(IGendocDiagnostician.class)).addDiagnostic(Status.ERROR, message.toString(), null);
                throw new ServiceException(message.toString());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.IProcessExtensionService#getNumProcesses()
     */
    public int getNumProcesses()
    {
        return this.waiting.size() + this.ready.size() + this.current.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.IProcessExtensionService#getProcesses()
     */
    public List<ProcessExtension> getProcesses()
    {
        return this.current;
    }

    /**
     * Gets the process extension with the given ID.
     * 
     * @param id the id of the process extension to get
     * @return the process extension
     */
    public ProcessExtension getProcessExtension(String id)
    {
        for (ProcessExtension processExtension : this.waiting)
        {
            if (id.equals(processExtension.getId()))
            {
                return processExtension;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.IProcessExtensionService#next()
     */
    public boolean next()
    {
        // clear current processes
        this.current.clear();

        // move processes with no dependencies from waiting list to available list
        for (ProcessExtension waiting : this.waiting)
        {
            if (waiting.getDependencies().isEmpty())
            {
                this.ready.add(waiting);
            }
        }
        this.waiting.removeAll(this.ready);

        // sort available processes (by priority)
        Collections.sort(this.ready);

        // get the next processes to run
        if (!this.ready.isEmpty())
        {
            // get the first available process
            ProcessExtension first = this.ready.get(0);
            this.ready.remove(0);
            // add it to the current processes
            this.current.add(first);
            // if it's parallel add other parallel processes with the same priority
            if (first.isParallel())
            {
                for (ProcessExtension next : this.ready)
                {
                    if (next.isParallel() && next.getPriority().equals(first.getPriority()))
                    {
                        this.current.add(next);
                    }
                }
            }
            // remove processes from ready list
            this.ready.removeAll(this.current);
            // remove processes from waiting dependencies
            for (ProcessExtension waiting : this.waiting)
            {
                waiting.getDependencies().removeAll(this.current);
            }
        }

        // this should never happen because we've already checked for cyclic dependencies...
        if (this.current.isEmpty() && !this.waiting.isEmpty())
        {
            throw new RuntimeException("Unable to satisfy dependencies for remaining processes.");
        }

        // return the next processes to run
        return !this.current.isEmpty();
    }

}
