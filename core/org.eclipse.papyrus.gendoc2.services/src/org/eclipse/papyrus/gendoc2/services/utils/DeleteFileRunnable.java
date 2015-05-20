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
 * Tristan FAURE (Atos Origin) tristan.faure@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.utils;

import java.io.File;

/**
 * Runnable which will delete file
 * @author tfaure
 *
 */
public class DeleteFileRunnable implements Runnable {

	private final File file;

	public DeleteFileRunnable (File f)
	{
		this.file = f;
		
	}
	
	public void run() {
		if (file.exists())
		{
			file.delete();
		}
	}

}
