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
package org.eclipse.papyrus.gendoc2.script.acceleo.exception;

import org.eclipse.papyrus.gendoc2.services.exception.ParsingException;

/**
 * The Class AcceleoParsingException.
 */
public class AcceleoParsingException extends ParsingException
{

    private static final long serialVersionUID = 4511151515965253914L;
    private static final String INVOCATION_NOT_TERMINATED = "The invocation isn't terminated";
    private static final String MODULE_NOT_FOUND = "Module .* not found.*";
    private static final String OPERATION_NOT_FOUND = "Cannot find operation";
    private static final String INVALID_BLOCK = "!CSTParser.InvalidBlock!";
    
    public AcceleoParsingException(String message)
    {
        super(message);
        
        if (INVOCATION_NOT_TERMINATED.equals(message))
        {
            setUIMessage("Brackets are not correctly closed inside <gendoc> tags : use [___/] or [___]...[/__])");
        }
        else if (message.matches(MODULE_NOT_FOUND))
        {
            setUIMessage(message+". Maybe the external bundle referencing this module is not installed.");
        }
        else if (message.contains(OPERATION_NOT_FOUND))
        {
            setUIMessage(message+". Try to add the necessary external bundle inside attribute 'importedBundles' of <context> tag.");
        }
        else if (INVALID_BLOCK.contains(message))
        {
            setUIMessage("The Acceleo script contains invalid blocks. Check that all tags are well formed.");
        }
    }
    
    
}
