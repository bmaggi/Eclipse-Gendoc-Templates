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

package org.eclipse.papyrus.gendoc2.services;

import org.eclipse.emf.common.util.Diagnostic;

public interface IGendocDiagnostician extends IService
{

    /**
     * Adds the diagnostic to the chain.
     * 
     * @param diagnostic the diagnostic
     */
    void addDiagnostic(Diagnostic diagnostic);

    /**
     * Adds a new diagnostic to the chain.
     * 
     * @param severity the severity
     * @param message a human-readable message
     * @param target
     */
    void addDiagnostic(int severity, String message, Object target);

    /**
     * Adds the children of the diagnostic to the chain.
     * 
     * @param diagnostic the diagnostic
     */
    void addDiagnostics(Diagnostic diagnostic);

    /**
     * Returns the diagnostic.
     * 
     * @return the diagnostic
     */
    Diagnostic getResultDiagnostic();

    /**
     * Initializes the diagnostic service.
     */
    void init();

}
