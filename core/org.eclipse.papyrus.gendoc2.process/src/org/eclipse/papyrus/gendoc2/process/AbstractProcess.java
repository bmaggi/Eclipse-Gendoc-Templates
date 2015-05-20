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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.papyrus.gendoc2.process.impl.ProcessesExtensionPoint;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * An abstract base class for processes.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractProcess implements IExecutableExtension, IProcess
{

    /** The progress monitor for this service. */
    private IProgressMonitor progressMonitor;

    /** The label to display in the progress monitor. */
    private String label;

    /**
     * Gets the label to display in the progress monitor while this process is run.
     * 
     * @return the label
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * Gets the progress monitor for this service.
     * 
     * @return the progress monitor
     */
    public IProgressMonitor getMonitor()
    {
        return this.progressMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.IProcess#run()
     */
    public final void run() throws GenDocException
    {
        final IProgressMonitorService progressMonitorService = GendocServices.getDefault().getService(IProgressMonitorService.class);
        this.progressMonitor = SubMonitor.convert(progressMonitorService.subMonitor(1, this.getLabel()), this.getTotalWork());
        this.doRun();
        this.progressMonitor.done();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.IProcess#run(java.util.concurrent.Semaphore)
     */
    public final void run(Semaphore s) throws GenDocException
    {
        try
        {
            this.run();
        }
        finally
        {
            s.release();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)
     */
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException
    {
        String labelAttribute = config.getAttribute(ProcessesExtensionPoint.PROCESS_LABEL);
        this.label = labelAttribute;
    }

    /**
     * Runs the process. Developers should implement new processes by defining a doRun method.
     * 
     * @throws GenDocException
     */
    protected abstract void doRun() throws GenDocException;

    /**
     * Gets the total number of work units for this process (to be used by the progress monitor).
     * 
     * @return the total work units
     */
    protected abstract int getTotalWork();

    /**
     * Indicates if this process has been cancelled.
     * 
     * @return true if the process has been cancelled, otherwise false
     */
    protected boolean isCanceled()
    {
        return this.progressMonitor.isCanceled();
    }

    /**
     * Notifies that a given number of work units have been completed.
     * 
     * @param work a non-negative number of work units just completed
     */
    protected void worked(int work)
    {
        this.progressMonitor.worked(work);
    }

}
