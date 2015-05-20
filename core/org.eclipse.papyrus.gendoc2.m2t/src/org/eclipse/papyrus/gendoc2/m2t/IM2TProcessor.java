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
 * Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.m2t;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * Description of the interface IM2TProcess.
 */
public interface IM2TProcessor
{

	/**
	 * Clears any resources maintained by the processor.
	 */
    void clear();

    /**
     * Gets all the available bundles.
     * 
     * @return all the available bundles
     */
    List<String> getAllAvailableBundles();

    /**
     * Get all the script patterns (depends on language)
     * 
     * @return a list of regex pattern for script elements
     */
    List<Pattern> getScriptPatterns();

    /**
     * Execute the given script with the given context and return the generated text.
     * 
     * @param script to execute as a String
     * @param element to run the script with 
     * @return the generated text as a String
     * @throws GenDocException a gendoc exception
     */
    String runScript(String script, EObject element) throws GenDocException;

}
