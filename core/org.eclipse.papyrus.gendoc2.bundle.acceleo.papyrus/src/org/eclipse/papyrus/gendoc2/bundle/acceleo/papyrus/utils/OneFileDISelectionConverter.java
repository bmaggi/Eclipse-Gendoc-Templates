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

package org.eclipse.papyrus.gendoc2.bundle.acceleo.papyrus.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.papyrus.gendoc2.wizard.ISelectionConverter;
import org.eclipse.papyrus.infra.onefile.model.IPapyrusFile;

public class OneFileDISelectionConverter implements ISelectionConverter {

	public boolean matches(Object selectedObject) {
		return (getFile(selectedObject) != null);
	}

	public IFile getFile(Object selectedObject) {
       	IFile selectedFile = null;
        if (selectedObject != null) {
        	
        	if (selectedObject instanceof IPapyrusFile) {
        		selectedFile = ((IPapyrusFile) selectedObject).getMainFile();
        	} else if (selectedObject instanceof IAdaptable) {
        		selectedFile = (IFile)((IAdaptable) selectedObject).getAdapter(IFile.class); // Can be null
        	} else if (selectedObject instanceof IFile) {
        		selectedFile = (IFile) selectedObject;
        	} else {
        		// No valid selection
        	}        	

        } else {
            	// No valid selection
        }
        
        return selectedFile;
	}

}
