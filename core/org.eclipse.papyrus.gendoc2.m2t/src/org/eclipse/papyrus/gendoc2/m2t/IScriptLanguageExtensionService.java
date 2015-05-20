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
 * Kris Robertson (Atos Origin) kris.robertson@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.m2t;

import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.UnknownScriptLanguageException;

/**
 * The IScriptLanguageExtensionService class maintains a list of extensions registered to the scriptLanguages extension
 * point.
 */
public interface IScriptLanguageExtensionService extends IService
{

    /**
     * Returns the processor for the specified script language.
     * 
     * @param languageName the script language name
     * @return the M2T processor
     * @throws UnknownScriptLanguageException
     */
    IM2TProcessor getProcessor(String languageName) throws UnknownScriptLanguageException;

}
