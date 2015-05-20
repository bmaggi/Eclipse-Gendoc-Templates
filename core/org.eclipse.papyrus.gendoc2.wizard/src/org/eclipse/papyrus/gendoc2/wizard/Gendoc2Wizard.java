/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Barthelemy HABA (Atos Origin) barthelemy.haba@atosorigin.com - 
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.papyrus.gendoc2.GendocProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IConfigurationService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

// TODO: Auto-generated Javadoc
/**
 * The Class Gendoc2Wizard.
 */
public class Gendoc2Wizard extends Wizard
{

    /** The runners. */
    private final List<IGendoc2Runner> runners;

    /** The selectedObject. */
    private final Object selectedObject;

    /** The page. */
    private Gendoc2WizardPage page;

    /** The Constant mymessage. */
    private static final String mymessage = "Error generating documentation.\n" + "Please contact TOPCASED team or save the template";

    /**
     * Instantiates a new gendoc2 wizard.
     * 
     * @param runners the runners
     * @param file the file
     */
    public Gendoc2Wizard(List<IGendoc2Runner> runners, Object selectedObject)
    {
        this.runners = runners;
        Collections.sort(this.runners, new Comparator<IGendoc2Runner>()
        {
            public int compare(IGendoc2Runner o1, IGendoc2Runner o2)
            {
                String label1 = o1.getLabel() != null ? o1.getLabel() : "";
                String label2 = o2.getLabel() != null ? o2.getLabel() : "";
                return label1.compareTo(label2);
            }
        });
        this.selectedObject = selectedObject;
        setWindowTitle("Generate Documentation");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages()
    {
        page = new Gendoc2WizardPage();
        addPage(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        try
        {
            
            getContainer().run(false, false, new IRunnableWithProgress()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
                {
                    IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);
                    diagnostician.init();
                    IProgressMonitorService monitorService = (IProgressMonitorService) GendocServices.getDefault().getService(IProgressMonitorService.class);
                    monitorService.setMonitor(monitor);

                    try
                    {
                        IConfigurationService parameter = GendocServices.getDefault().getService(IConfigurationService.class);
                        parameter.addParameter(replacePercentBySpace(page.getSelected().getOutputKey(), 3), replacePercentBySpace(page.getFullOutputPath(), 3));
                        parameter.addParameter(replacePercentBySpace(page.getSelected().getModelKey(), 3), replacePercentBySpace(page.getModel(), 3));
                        GendocProcess gendocProcess = new GendocProcess();
                        String resultFile = gendocProcess.runProcess(page.getSelected().getTemplate());

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
                        Display.getDefault().syncExec(new Runnable()
                        {
                            public void run()
                            {
                                MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Document generator", "The document has been generated successfully: \n"
                                        + resultFilePath);
                            }
                        });
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
                                ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Document generator", mymessage, BasicDiagnostic.toIStatus(resultDiagnostic));
                            }
                        });
                    }
                }
            });
            return true;
        }
        catch (InvocationTargetException e)
        {
            return false;
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            ((WizardPage) getContainer().getCurrentPage()).setErrorMessage("Erreur de generation de la documentation");

            return false;
        }
    }

    /**
     * Gets the runners.
     * 
     * @return the runners contains all the possibles templates necessary for document generation
     */
    public List<IGendoc2Runner> getRunners()
    {
        return runners;
    }

    /**
     * Gets the input file.
     * 
     * @return the input file represent the model on witch user have made the click
     */
    public IFile getInputFile(IGendoc2Runner runner)
    {
    	return runner.getSelectionConverter().getFile(selectedObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish()
    {
        boolean state = true;
        int cpteur = 1;

        if (getContainer().getCurrentPage() == null)
        {
            return false;
        }
        ((WizardPage) getContainer().getCurrentPage()).setErrorMessage(null);
        StringBuffer message = new StringBuffer();
        String path = page.getOutputPath();
        File f = new File(path);
        if (!f.isDirectory() || !f.exists())
        {
            message.append(cpteur + " : Please specify a directory path in the path field \n ");
            state = false;
            cpteur += 1;
        }

        if (!page.isCorrectExtension())
        {
            message.append(" " + cpteur + " : OutPut file extension is not correct\n ");
            cpteur += 1;
            state = false;
        }

        if (!page.allIsFilled())
        {
            message.append(" " + cpteur + " : Please Fill all parameters fields \n ");
            state = false;
        }

        if (!state)
        {
            ((WizardPage) getContainer().getCurrentPage()).setErrorMessage(message.toString());
            return false;
        }
        return true;
    }

    /**
     * Refresh. this methode is use to refresh the wizard page
     */
    public void refresh()
    {
        getContainer().updateButtons();
        getContainer().updateMessage();
    }

    /**
     * @param theString string contening percentage caracteres
     * @param pas the nomber of caractere that represent space in the système
     * @return theString with any percentage caractere
     */
    public String replacePercentBySpace(String theString, int pas)
    {
        StringBuffer buffer = new StringBuffer(theString);
        int pos = 1;
        while (theString.contains("%"))
        {
            pos = theString.indexOf("%"); // Position de la 1ére occurence
            theString = buffer.replace(pos, pos + pas, " ").toString();
        }
        return theString;
    }
}
