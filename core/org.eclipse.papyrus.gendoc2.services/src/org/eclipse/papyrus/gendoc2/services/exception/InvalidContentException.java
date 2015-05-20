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
 * Exception thrown when an invalid content is generated
 */
public class InvalidContentException extends GenDocException
{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 3312061165623733355L;

	public InvalidContentException(String content)
    {
        super("Some content cannot be inserted in the document: " + content + ".");
    }

    public InvalidContentException(String content, Throwable t)
    {
        super("Some content cannot be inserted in the document: " + content + ".", t);
    }

}
