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
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.handlers.impl.context;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.ElementNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;
import org.eclipse.papyrus.gendoc2.tags.handlers.Activator;
import org.eclipse.papyrus.gendoc2.tags.handlers.IContextService;
import org.eclipse.papyrus.gendoc2.tags.handlers.IEMFModelLoaderService;

/**
 * Description of the class Context.
 */
public class ContextService extends AbstractService implements IContextService
{

    private static final String ELEMENT_DEFAULT_PATH = "{0}";

	/** Delimiter use to separate bundles in bundles declaration. */
    private static String delimiter = ";";

    /** The current model (absolute path) used for the next generations. */
    private URI model;

    /** The current element path in the model used for the next generations. */
    private String elementPath = ELEMENT_DEFAULT_PATH;

    /** The current element used for the next generations. */
    private EObject element;

    /** List of the imported bundles used for the next generations. */
    private final List<String> importedBundles;

    /** The label feature of the model elements. */
    private String featureLabel = "name";
    
    /** Defines if the potential multiple metamodels associated to model elements must be searched or not (false by default)*/
    private boolean searchMetamodels = false;

    private static Pattern patternForManySlash = Pattern.compile(".*//+.*");


    /**
     * Constructor.
     */
    public ContextService()
    {
        super();
        importedBundles = new LinkedList<String>();
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    public void clear()
    {
        importedBundles.clear();

    }

    // ////// Getters and setters /////////

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#getModel()
     */
    public EObject getModel() throws ModelNotFoundException
    {
        IEMFModelLoaderService modelLoader = GendocServices.getDefault().getService(IEMFModelLoaderService.class);
        return modelLoader.getModel(model);
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#getModelPath()
     */
    public String getModelPath()
    {
        return model.toString();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#isSearchMetamodels()
     */
    public boolean isSearchMetamodels()
    {
        return searchMetamodels;
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#setModel(java.lang.String)
     */
    public void setModel(String modelPath) throws ModelNotFoundException
    {
        try
        {
            if (patternForManySlash.matcher(modelPath).matches())
           {
               // WARNING
                ILogger logger = GendocServices.getDefault().getService(ILogger.class);
                IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);
                
                String message = "There is more than one '/' in your model path : "+modelPath;
                diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR,Activator.PLUGIN_ID, 0,message, new Object[]{modelPath}));
                logger.log(message, ILogger.DEBUG);  
            }
            model = URI.createFileURI(modelPath);
            // new model = element must be clean
            element = null;
            elementPath = ELEMENT_DEFAULT_PATH;
        }
        catch (IllegalArgumentException e)
        {
            throw new ModelNotFoundException(modelPath);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#getElement()
     */
    public EObject getElement() throws ModelNotFoundException, ElementNotFoundException
    {
        // Load the current element
        if (element == null)
        {
            // Load the current element from the model from the element path
            IEMFModelLoaderService modelLoader = GendocServices.getDefault().getService(IEMFModelLoaderService.class);
            element = modelLoader.getCurrentElement(elementPath, getModel(), 1, featureLabel);
        }
        return element;
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#setElementPath(java.lang.String)
     */
    public void setElementPath(String elementPath)
    {
        this.elementPath = elementPath;
        this.element = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#getImportedBundles()
     */
    public List<String> getImportedBundles()
    {
        return importedBundles;
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#setFeatureLabel(java.lang.String)
     */
    public void setFeatureLabel(String featureLabel)
    {
        this.featureLabel = featureLabel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IContextService#setSearchMetamodels(boolean)
     */
    public void setSearchMetamodels(boolean searchMetamodels){
    	this.searchMetamodels = searchMetamodels;
    }
    
    /**
     * The bundles parameters are given as "bundleName1;bundleName2".
     * 
     * @param attributesBundles the attributes bundles
     */
    public void setImportedBundles(String attributesBundles)
    {
        // Old bundles are erased
        importedBundles.clear();

        // Add the new bundles
        String[] bundles = attributesBundles.split(delimiter);
        for (String bundle : bundles)
        {
            importedBundles.add(bundle);
        }
    }

}