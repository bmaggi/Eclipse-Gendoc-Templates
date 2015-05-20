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
 *  Alexia Allanic (Atos Origin) alexia.allanic@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.bundle.acceleo.papyrus.service;

import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.documentation.DocumentationManager;
import org.eclipse.papyrus.documentation.DocumentationUnsupportedException;
import org.eclipse.papyrus.gendoc2.bundle.acceleo.gmf.service.GMFServices;
import org.eclipse.papyrus.gendoc2.bundle.acceleo.papyrus.Activator;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IEMFModelLoaderService;

public class PapyrusServices extends GMFServices
{

    @Override
    public List<Diagram> getDiagrams(EObject e, URI uri)
    {
        // ensure first diagrams resource is correctly loaded in Model set
        IEMFModelLoaderService modelLoader = GendocServices.getDefault().getService(IEMFModelLoaderService.class);
        try
        {
            modelLoader.getModel(uri);
        }
        catch (ModelNotFoundException e1)
        {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e1.getMessage(), e1));
        }
        return super.getDiagrams(e, uri);
    }

    /**
     * Get the diagram list associated with the object.
     * 
     * @param object the object
     * 
     * @return the diagram list
     */
    public List<Diagram> getPapyrusDiagrams(EObject object)
    {

        List<Diagram> result = getDiagramsUsingNotation(object);
        // load main ".di" file in addition in resource set
        //        URI diURI = object.eResource().getURI().trimFileExtension().appendFileExtension("di");//$NON-NLS-1$
        // if (!object.eResource().getURI().equals(diURI))
        // {
        // try
        // {
        // object.eResource().getResourceSet().getResource(diURI, true);
        // }
        // catch (WrappedException ex)
        // {
        // IGendocDiagnostician diag = GendocServices.getDefault().getService(IGendocDiagnostician.class);
        // diag.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0,
        // String.format("Resource %s not found", diURI.toString()), new Object[] {object}));
        // }
        //
        // }
        return result;
    }

    /**
     * Get the documentation.
     * 
     * @param object the object
     * 
     * @return the documentation
     */
    public String getDocumentation(EObject object)
    {
        try
        {
            return DocumentationManager.getInstance().getDocumentation(object);
        }
        catch (DocumentationUnsupportedException e)
        {
            return null;
        }
    }
    
    
}
