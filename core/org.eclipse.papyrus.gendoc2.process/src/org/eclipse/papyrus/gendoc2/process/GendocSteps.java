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

package org.eclipse.papyrus.gendoc2.process;

/** Steps for gendoc generation */
public enum GendocSteps
{
	POST_COUNTING,
	POST_VERIFIYING,
	POST_PARSING,
	POST_FINALIZING,
	PRE_VERIFIYING,
	PRE_PARSING,
	PRE_FINALIZING,
	PRE_SAVE,
	POST_SAVE
}
