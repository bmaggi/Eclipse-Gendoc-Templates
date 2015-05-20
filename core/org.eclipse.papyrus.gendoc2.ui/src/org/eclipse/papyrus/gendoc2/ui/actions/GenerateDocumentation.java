/*****************************************************************************
 * Copyright (c) 2010 Atos Origin.
 * Copyright (c) 2011 Airbus.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *  Pierre Gaufillet (Airbus) pierre.gaufillet@airbus.com - remove modal box after successful generations
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.papyrus.gendoc2.ui.Activator;
import org.eclipse.papyrus.gendoc2.ui.run.GenDocRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class GenerateDocumentation implements IObjectActionDelegate
{

    private IStructuredSelection selection;
    private IStatusLineManager statusLineManager;

    /**
     * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
     * UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action)
    {
        if (selection.getFirstElement() instanceof IFile)
        {
            final IFile currentFile = (IFile) selection.getFirstElement();
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            try
            {
                dialog.run(false, true, new GenDocRunnable(currentFile, statusLineManager));
            }
            catch (InvocationTargetException e)
            {
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            }
            catch (InterruptedException e)
            {
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            }
        }
    }

    /**
     * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
     * this can only happen after the delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection newSelection)
    {
        this.selection = (IStructuredSelection) newSelection;
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        if (targetPart instanceof IViewPart)
        {
            IViewPart view = (IViewPart) targetPart;
            statusLineManager = view.getViewSite().getActionBars().getStatusLineManager();
        }
        else
        {
            statusLineManager = null;
        }
    }
}