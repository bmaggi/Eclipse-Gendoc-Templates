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
 *  Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * The Class UnknownScriptLanguageException.
 */
public class UnknownScriptLanguageException extends GenDocException
{
    private static final long serialVersionUID = -9222692769510421563L;

    /**
     * Instantiates a new unknown script language exception.
     * 
     * @param languageName the language name
     */
    public UnknownScriptLanguageException(String languageName)
    {
        super("The script language '" + languageName + "' is not supported.");
    }
}
