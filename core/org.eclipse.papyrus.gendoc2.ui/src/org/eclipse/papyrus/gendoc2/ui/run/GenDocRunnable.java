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
 *  Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 *  Pierre Gaufillet (Airbus) pierre.gaufillet@airbus.com - remove modal box after successful generations
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.ui.run;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.papyrus.gendoc2.GendocProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.ui.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * The Class GenDocRunnable.
 */
public class GenDocRunnable implements IRunnableWithProgress
{

    /** The current file. */
    private final IFile currentFile;
    private IStatusLineManager statusLineManager;

    /**
     * Instantiates a new gen doc runnable.
     * 
     * @param selectedFile the selected file
     */
    public GenDocRunnable(IFile selectedFile, IStatusLineManager statusLineManager)
    {
        this.currentFile = selectedFile;
        this.statusLineManager = statusLineManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        // Init
        IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);
        diagnostician.init();
        IProgressMonitorService monitorService = (IProgressMonitorService) GendocServices.getDefault().getService(IProgressMonitorService.class);
        monitorService.setMonitor(monitor);

        try
        {
            GendocProcess gendocProcess = new GendocProcess();
            String resultFile = gendocProcess.runProcess(currentFile.getLocation().toFile());
            // Message in case of error in generation
            handleDiagnostic(diagnostician.getResultDiagnostic(), "The file has been generated but contains errors :\n", resultFile);
        }
        catch (GenDocException e)
        {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getUIMessage(), e));
            diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0, e.getUIMessage(), null));
            handleDiagnostic(diagnostician.getResultDiagnostic(), "An error occured during generation.", null);
        }
        catch (Throwable t)
        {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, t.getMessage(), t));
            diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0, t.getMessage(), t.getStackTrace()));
            handleDiagnostic(diagnostician.getResultDiagnostic(), "An unexpected error occured during the generation.", null);
        }
        finally
        {
            GendocServices.getDefault().clear();
        }

    }

    /**
     * Handle diagnostic.
     * 
     * @param resultDiagnostic the result diagnostic
     */
    private void handleDiagnostic(final Diagnostic resultDiagnostic, final String message, final String resultFilePath)
    {
        if (resultDiagnostic.getSeverity() == Diagnostic.OK)
        {
            if (statusLineManager != null)
            {
                statusLineManager.setMessage(resultFilePath + " successfully generated");
            }
        }
     else
        {
            Display.getDefault().syncExec(new Runnable()
            {
                public void run()
                {
                    String path = "";
                    if (resultFilePath != null)
                    {
                        path = resultFilePath;
                    }
                    ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Document generator", message + path, BasicDiagnostic.toIStatus(resultDiagnostic));
                }
            });
        }

    }
}
