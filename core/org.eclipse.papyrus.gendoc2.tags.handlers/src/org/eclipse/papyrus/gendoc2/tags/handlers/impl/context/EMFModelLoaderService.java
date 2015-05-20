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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.exception.ElementNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IEMFModelLoaderService;

/**
 * Description of the class EMFModelLoader.
 * 
 */

public class EMFModelLoaderService extends AbstractService implements IEMFModelLoaderService
{
    private static final String SEPARATOR = "/";

    private final Pattern patternForIndex = Pattern.compile("\\{(\\d*)\\}");

    private final Pattern patternForAttribut = Pattern.compile("(.*)=\"(.*)\"");

    private final ResourceSet resourceSet;
    
    private List<URI> paths;

    private boolean isRootVirtual = false;
    
    /**
     * Constructor.
     */
    public EMFModelLoaderService()
    {
        super();
        // Create a resourceSet
        resourceSet = constructResourceSet();
    }

    protected ResourceSet constructResourceSet ()
    {
    	return new ResourceSetImpl();
    }

    protected Resource getResource (URI uri)
    {
    	return resourceSet.getResource(uri, true);
    }

    protected ResourceSet getResourceSet ()
    {
    	return resourceSet;
    }

    /**
     * Load the model from the given path
     * 
     * @param the model path (uri)
     * @return the model
     */
    public EObject getModel(URI path) throws ModelNotFoundException
    {
        checkPath(path);
        if( paths == null){
            paths = new LinkedList<URI>();
        }
        EObject model = null;
        try
        {
            // Load model
            final Resource resource = getResource(path);
            if (!paths.contains(path))
            {
                paths.add(path);
                resolve(resource);
            }
            switch (resource.getContents().size())
            {
            	case 0:
            		throw new ModelNotFoundException("Model can not be loaded from URL: \"" + path.toString() + "\"");
            	case 1:
            		isRootVirtual = false;
            		model = resource.getContents().get(0);
            		break;
            	default:
            		// create root object
            		isRootVirtual = true;
            		model = new EObject()
            		{
						public TreeIterator<EObject> eAllContents() {
							return null;
						}

						public EClass eClass() {
							return null;
						}

						public EObject eContainer() {
							return null;
						}

						public EStructuralFeature eContainingFeature() {
							return null;
						}

						public EReference eContainmentFeature() {
							return null;
						}

						public EList<EObject> eContents() {
							return resource.getContents();
						}

						public EList<EObject> eCrossReferences() {
							return null;
						}

						public Object eGet(EStructuralFeature arg0) {
							return null;
						}

						public Object eGet(EStructuralFeature arg0, boolean arg1) {
							return null;
						}

						public boolean eIsProxy() {
							return false;
						}

						public boolean eIsSet(EStructuralFeature arg0) {
							return false;
						}

						public Resource eResource() {
							return resource;
						}

						public void eSet(EStructuralFeature arg0, Object arg1) {
							
						}

						public void eUnset(EStructuralFeature arg0) {
							
						}

						public EList<Adapter> eAdapters() {
							return null;
						}

						public boolean eDeliver() {
							return false;
						}

						public void eNotify(Notification arg0) {
							
						}

						public void eSetDeliver(boolean arg0) {
							
						}

						public Object eInvoke(EOperation arg0, EList<?> arg1)
								throws InvocationTargetException {
							return null;
						}
            		};
            }
        }
        catch (RuntimeException e)
        {
            throw new ModelNotFoundException("Model can not be loaded from URL: \"" + path.toString() + "\"");
        }

        return model;
    }

    /**
     * Method resolving the resource
     * @param resource
     */
    protected void resolve(final Resource resource)
    {
        resourceSet.eSetDeliver(false);
        EcoreUtil.resolveAll(resource);
        resourceSet.eSetDeliver(true);
    }

    /**
     * Preconfition of get Model, override this method if you are using templates without input
     * @param path
     * @throws ModelNotFoundException
     */
    protected void checkPath(URI path) throws ModelNotFoundException
    {
        if (path == null)
        {
            throw new ModelNotFoundException("No path provided");
        }
    }

    /**
     * Return the element corresponding to the path in the model
     * 
     * @param elementPath
     * @param model
     * @param i : must be > 0
     * @param structuralObject
     * @return the current element
     * @throws ElementNotFoundException
     */
    public EObject getCurrentElement(String elementPath, EObject model, int i, String attribute) throws ElementNotFoundException
    {
        // Check parameters
        if (i < 1)
        {
            throw new IllegalArgumentException("i must be higher than zero");
        }

        // if there is no element, root is return
        if (i == 1 && (elementPath == null || elementPath.length() == 0))
        {
            return model;
        }
        String[] splitAttribute = new String[0];

        // Split the path
        if (elementPath != null)
        {
            splitAttribute = elementPath.split(SEPARATOR);
        }
        if (!isRootVirtual)
        {
	        // If the element to search is the root
	        if (i == 1 && (splitAttribute.length == 1 && (isEquals(model, attribute, elementPath) || getIndex(elementPath) == 0)))
	        {
	            return model;
	        }
	        if (i >= splitAttribute.length)
	        {
	            throw new IllegalArgumentException("i must be lower than length of segments");
	        }
        }

        String toSearch = isRootVirtual ? splitAttribute[i-1] : splitAttribute[i];
        String trim = toSearch.trim();
        EObject object = null;

        // Get an eventual index
        int index = getIndex(trim);

        // Get the current element
        if (index >= 0 && !model.eContents().isEmpty())
        {
            object = model.eContents().get(index);
        }
        else
        {
            for (EObject tmp : model.eContents())
            {
                if (isEquals(tmp, attribute, toSearch))
                {
                    object = tmp;
                    break;
                }
            }
        }
        // Get the next element
        if (object != null)
        {
        	int splitAttributeLength = isRootVirtual ? splitAttribute.length : splitAttribute.length -1;
            if (i != splitAttributeLength)
            {
                return getCurrentElement(elementPath, object, i + 1, attribute);
            }
            else if (i == splitAttributeLength)
            {
                return object;
            }
        }

        throw new ElementNotFoundException(elementPath, model.eResource().getURI().toString());
    }

    /**
     * Test if the value of the structural feature of the Object tmp corresponding to attribute is equals to toSearch
     * 
     * @param tmp
     * @param attribute
     * @param toSearch
     * @return true if there are equals else false
     */
    public boolean isEquals(EObject tmp, String attribute, String toSearch)
    {

        String featureName = attribute;
        String featureValue = toSearch;

        // Check if the element is complex (featureId = 'value')
        Matcher m = patternForAttribut.matcher(toSearch);
        if (m.matches())
        {
            if (m.groupCount() == 2)
            {
                featureName = m.group(1);
                featureValue = m.group(2);
            }
        }

        // Get the feature value of the eObject
        String content = getEStructuralFeature(tmp, featureName);
        return content != null && content.equals(featureValue);
    }

    /**
     * If the element match the pattern {\[\d*\]} return the index [0-9]*
     * 
     * @param element to match
     * @return the index found else -1
     */
    protected int getIndex(String element)
    {
        Matcher m = patternForIndex.matcher(element);
        if (m.matches())
        {
            if (m.groupCount() == 1)
            {
                String group = m.group(1);
                try
                {
                    int result = Integer.valueOf(group);
                    return result;
                }
                catch (NumberFormatException e)
                {
                    // regex checks if it is a decimal
                }
            }
        }
        return -1;
    }

    /**
     * Gets the estructural feature
     * 
     * @param e the specified eObject
     * @param attribute the structural feature name to search
     * 
     * @return the estructural feature that match the attribute
     */
    protected String getEStructuralFeature(EObject e, String attribute)
    {
        if (e != null && attribute != null)
        {
            EList<EStructuralFeature> all = e.eClass().getEAllStructuralFeatures();
            for (EStructuralFeature f : all)
            {
                if (f.getName().equalsIgnoreCase(attribute))
                {
                    Object content = e.eGet(f);
                    if (content instanceof String)
                    {
                        return (String) content;
                    }
                }
            }
        }
        return null;
    }

    public void clear()
    {
        // Unload models
        for (Resource r : resourceSet.getResources())
        {
            try
            {
                r.unload();
            }
            catch (Exception e)
            {
                // Sometimes the unload does not work,
                // and throw an exception
            }
        }
        if( paths != null){
        	paths.clear();
        }
    }

}