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

package org.eclipse.papyrus.gendoc2.services.impl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;

/**
 * The default implementation of IProgressMonitorService.
 * 
 * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService
 */
public class ProgressMonitorService extends AbstractService implements IProgressMonitorService
{

    /** The progress monitor. */
    private IProgressMonitor progressMonitor = new NullProgressMonitor();

    /** The delegating monitor. */
    private Monitor delegateMonitor;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#beginTask(String, int)
     */
    public void beginTask(String label, int arg1)
    {
        this.progressMonitor.beginTask(label, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    @Override
    public void clear()
    {
        this.progressMonitor.done();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#getDelegatingMonitor()
     */
    public Monitor getDelegatingMonitor()
    {
        if (this.delegateMonitor == null)
        {
            this.delegateMonitor = BasicMonitor.toMonitor(this.progressMonitor);
        }
        return this.delegateMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#getMonitor()
     */
    public IProgressMonitor getMonitor()
    {
        return this.progressMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#isCanceled()
     */
    public boolean isCanceled()
    {
        return this.progressMonitor.isCanceled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#setMonitor(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setMonitor(IProgressMonitor monitor)
    {
        this.progressMonitor = monitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#subMonitor(int, String)
     */
    public IProgressMonitor subMonitor(int ticks, String label)
    {
        this.progressMonitor.subTask(label);
        return new SubProgressMonitor(this.progressMonitor, ticks);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IProgressMonitorService#worked()
     */
    public void worked(int arg0)
    {
        this.progressMonitor.worked(arg0);
    }

}
