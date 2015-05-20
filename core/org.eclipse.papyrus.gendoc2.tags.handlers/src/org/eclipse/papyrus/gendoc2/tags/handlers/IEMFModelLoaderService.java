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
package org.eclipse.papyrus.gendoc2.tags.handlers;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.ElementNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;

/**
 * @author cbourdeu
 * 
 */
public interface IEMFModelLoaderService extends IService
{

	/**
     * Load the model from the given path
     * 
     * @param the model path (uri)
     * @return the model
     */
    EObject getModel(URI path) throws ModelNotFoundException;

    /**
     * Return the element corresponding to the path in the model
     * 
     * @param elementPath
     * @param model
     * @param i
     * @param structuralObject
     * @return the current element
     * @throws ElementNotFoundException
     */
    EObject getCurrentElement(String elementPath, EObject model, int i, String attribute) throws ElementNotFoundException;
}
