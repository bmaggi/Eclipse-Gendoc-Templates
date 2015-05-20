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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.gendoc2.process.IProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;

/**
 * The ProcessExtension class represents a process declared on the processes extension point.
 * 
 * @author Kris Robertson
 */
public class ProcessExtension implements Comparable<ProcessExtension>
{

    /**
     * The priority of a process. Processes with a higher priority are run first.
     */
    enum Priority {
        HIGH, ABOVE_NORMAL, NORMAL, BELOW_NORMAL, LOW
    }

    /**
     * Indicates if a process has been visited by a depth-first topological sort.
     */
    enum VisitState {
        NOT_STARTED, IN_PROGRESS, FINISHED
    }

    /** The configuration element for this extension. */
    private final IConfigurationElement configElement;

    /** Indicates if this process has been visited by a depth-first topological sort. */
    private VisitState visitState = VisitState.NOT_STARTED;

    /** The id of this process */
    private final String id;

    /** The label for this process (displayed within the progress monitor) */
    private final String label;

    /** Indicates if this process be run parallel with other processes */
    private final boolean parallel;

    /** The priority of this process */
    private final Priority priority;

    /** IDs of processes that must run before this process. */
    private final List<String> predecessors = new LinkedList<String>();

    /** IDs of processes that must run after this process. */
    private final List<String> successors = new LinkedList<String>();

    /** The list of processes that must run before this process after refs have been resolved. */
    private final List<ProcessExtension> dependencies = new LinkedList<ProcessExtension>();

    /**
     * Instantiates a new ProcessExtension from a configuration element.
     * 
     * @param configElement the configuration element
     */
    public ProcessExtension(IConfigurationElement configElement)
    {
        // store the config element
        this.configElement = configElement;
        // read attributes
        this.id = configElement.getAttribute(ProcessesExtensionPoint.PROCESS_ID);
        this.label = configElement.getAttribute(ProcessesExtensionPoint.PROCESS_LABEL);
        this.parallel = Boolean.parseBoolean(configElement.getAttribute(ProcessesExtensionPoint.PROCESS_PARALLEL));
        this.priority = Priority.valueOf(configElement.getAttribute(ProcessesExtensionPoint.PROCESS_PRIORITY));
        // read predecessor elements
        for (IConfigurationElement predecessor : configElement.getChildren(ProcessesExtensionPoint.PREDECESSOR))
        {
            String ref = predecessor.getAttribute(ProcessesExtensionPoint.PREDECESSOR_REF);
            if (ref != null)
            {
                this.predecessors.add(ref);
            }
        }
        // read successor elements
        for (IConfigurationElement successor : configElement.getChildren(ProcessesExtensionPoint.SUCCESSOR))
        {
            String ref = successor.getAttribute(ProcessesExtensionPoint.SUCCESSOR_REF);
            if (ref != null)
            {
                this.successors.add(ref);
            }
        }
    }

    /**
     * Compares this process to another. Comparison is based on priority.
     */
    public int compareTo(ProcessExtension o)
    {
        return this.priority.compareTo(o.getPriority());
    }

    /**
     * Checks this extensions dependencies for cycles.
     * 
     * @param message the message to display. If a cycle is found this extension will add it's id to the end.
     * @return true if a cycle is found, otherwise false
     */
    public boolean findCyclicDependencies(StringBuffer message)
    {
        switch (this.visitState)
        {
            case NOT_STARTED:
                this.visitState = VisitState.IN_PROGRESS;
                for (ProcessExtension dependency : this.dependencies)
                {
                    if (dependency.findCyclicDependencies(message))
                    {
                        message.append(" <- " + this.id);
                        return true;
                    }
                }
                this.visitState = VisitState.FINISHED;
                break;
            case IN_PROGRESS:
                message.append(this.id);
                return true;
            case FINISHED:
                break;
        }
        return false;
    }

    /**
     * Returns a list of dependencies for this process. Dependencies are removed from this list after they have been
     * run.
     * 
     * @return the list of dependencies
     */
    public List<ProcessExtension> getDependencies()
    {
        return this.dependencies;
    }

    /**
     * Gets the unique identifier for this process.
     * 
     * @return the process ID
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Gets the label for this process.
     * 
     * @return the process ID
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * Returns the list of predecessors defined for this process extension. This list only contains the predecessors
     * declared as a part of this extension not a fully resolved list.
     * 
     * @return this list of predecessors
     */
    public List<String> getPredecessors()
    {
        return this.predecessors;
    }

    /**
     * Returns the priority of this process; one of HIGH, ABOVE_NORMAL, NORMAL, BELOW_NORMAL, or LOW.
     * 
     * @return the priority
     */
    public Priority getPriority()
    {
        return this.priority;
    }

    /**
     * Returns an instance of the process.
     * 
     * @return the process instance
     */
    public IProcess getProcess()
    {
        try
        {
            return (IProcess) this.configElement.createExecutableExtension(ProcessesExtensionPoint.PROCESS_PROCESSOR);
        }
        catch (CoreException e)
        {
            String message = "Failed to create process " + this.id + " from " + this.configElement.getDeclaringExtension().getNamespaceIdentifier();
            ((ILogger) GendocServices.getDefault().getService(ILogger.class)).log(message, Status.ERROR);
        }
        return null;
    }

    /**
     * Returns the list of successors defined for this process extension. This list only contains the successors
     * declared as a part of this extension not a fully resolved list.
     * 
     * @return
     */
    public List<String> getSuccessors()
    {
        return this.successors;
    }

    /**
     * Indicates if this process can be run in parallel with other processes.
     * 
     * @return true if this process can run in parallel with other processes, otherwise false
     */
    public boolean isParallel()
    {
        return this.parallel;
    }

}
