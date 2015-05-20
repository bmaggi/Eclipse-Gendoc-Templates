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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * Exception in case of MimeType not found for a file extension
 * 
 */
public class UnknownMimeTypeException extends GenDocException 
{

    /**
     * generated UID
     */
    private static final long serialVersionUID = -6203825391373853777L;

    public UnknownMimeTypeException(String extension)
    {
        super("MIME type for file extension '" + extension + "' is not found.");
    }
}
