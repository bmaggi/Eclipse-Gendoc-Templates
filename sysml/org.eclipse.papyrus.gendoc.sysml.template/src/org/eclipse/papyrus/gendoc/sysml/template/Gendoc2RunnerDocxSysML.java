/*****************************************************************************
 * Copyright (c) 2013 CEA LIST.
 * 
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 * Sebastien Poissonnet (CEA LIST) sebastien.poissonnet@cea.fr
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc.sysml.template;

import java.net.URL;

import org.eclipse.gendoc.wizard.IGendocTemplate;

public class Gendoc2RunnerDocxSysML implements IGendocTemplate{
	
	private String description = "SysML MS Word 2010 generation template";

	public String getOutPutExtension() {
		return "docx";
	}

	public URL getTemplate() {
		return Activator.getDefault().getBundle().getEntry("/resource/templatePapyrus_sysML.docx");
	}

	public String getModelKey() {
		return "generic_generation_model";
	}

	public String getOutputKey() {
		return "generic_generation_output";
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return "SysML";
	}

	public boolean isSavable() {
		// TODO Auto-generated method stub
		return false;
	}
}
