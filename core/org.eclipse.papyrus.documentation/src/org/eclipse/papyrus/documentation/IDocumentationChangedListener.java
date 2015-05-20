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
 *  Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.documentation;

import org.eclipse.emf.ecore.EObject;

/**
 * Listener interface used to be notified of a change in the documentation.
 * 
 * @author mvelten
 * 
 */
public interface IDocumentationChangedListener {

	/**
	 * This method will be called when the documentation of eObject or its author is modified.
	 * 
	 * @param eObject
	 */
	void documentationChanged(EObject eObject);
}
