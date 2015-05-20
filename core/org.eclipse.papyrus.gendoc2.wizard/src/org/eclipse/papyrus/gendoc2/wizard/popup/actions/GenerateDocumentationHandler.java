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
package org.eclipse.papyrus.gendoc2.wizard.popup.actions;

import java.awt.print.Paper;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.papyrus.gendoc2.wizard.Gendoc2Wizard;
import org.eclipse.papyrus.gendoc2.wizard.IGendoc2Runner;
import org.eclipse.papyrus.gendoc2.wizard.Utils;
import org.eclipse.ui.handlers.HandlerUtil;

public class GenerateDocumentationHandler extends org.eclipse.core.commands.AbstractHandler
{

	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection)
        {
        	Object selectedObject = (selection != null) ? ((IStructuredSelection) selection).getFirstElement() : null;
        	
            
            List<IGendoc2Runner> runners = (selectedObject != null) ? Utils.getRunners(selectedObject) : null;
            if (runners != null)
            {
                Gendoc2Wizard wizard = new Gendoc2Wizard(runners, selectedObject);
                WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
                wizardDialog.open();
            }
        }
        return null;
    }
    
}
