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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.ElementNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;

/**
 * @author cbourdeu
 * 
 */
public interface IContextService extends IService
{

	EObject getModel() throws ModelNotFoundException;

    String getModelPath();

    void setModel(String model) throws GenDocException;

    EObject getElement() throws ModelNotFoundException, ElementNotFoundException;

    void setElementPath(String element);

    List<String> getImportedBundles();

    void setFeatureLabel(String featureLabel);
    
    boolean isSearchMetamodels();
    
    void setSearchMetamodels(boolean searchMetamodels);

    /**
     * The bundles parameters are given as "bundleName1;bundleName2"
     */
    void setImportedBundles(String attributesBundles);
}
