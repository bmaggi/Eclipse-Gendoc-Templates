/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.wizard;

import org.eclipse.core.resources.IFile;

public interface ISelectionConverter {

	public boolean matches(Object selectedObject);
	
	public IFile getFile(Object selectedObject);
	
}
