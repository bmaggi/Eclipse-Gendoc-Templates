package org.eclipse.papyrus.gendoc2.wizard.popup.actions;

import java.io.File;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.papyrus.gendoc2.wizard.Gendoc2Wizard;
import org.eclipse.papyrus.gendoc2.wizard.IGendoc2Runner;
import org.eclipse.papyrus.gendoc2.wizard.Utils;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.papyrus.infra.emf.utils.EMFFileUtil;
import org.eclipse.papyrus.infra.onefile.model.IPapyrusFile;

public class GenerateDocumentationModelExplorerHandler extends org.eclipse.core.commands.AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            EObject selectedElement = EMFHelper.getEObject(structuredSelection.getFirstElement());
            if(selectedElement == null) {
                  return null;
            }
            URI resourceURI = selectedElement.eResource().getURI();
            //URI s = resourceURI.trimFileExtension();
            
            IFile selectedObject = org.eclipse.papyrus.infra.emf.utils.EMFFileUtil.getIFile(resourceURI.toString());
            
            //IFile diFile = ((IPapyrusFile)selectedObject).getMainFile();
            //IFile selectedObject = ((IPapyrusFile)org.eclipse.papyrus.infra.emf.utils.EMFFileUtil.getIFile(resourceURI.toString())).getMainFile();
            
            
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
