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
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * Generic exception for generation
 */
public class GenerationException extends GenDocException {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -1131070583202625127L;

	public GenerationException(String message, Throwable t) {
		super(message, t);
	}

	public GenerationException(String message) {
		super(message);
	}

	public GenerationException(Throwable t) {
		super(t);
	}
}
