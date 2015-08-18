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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.gendoc.wizard.DefaultSelectionConverter;
import org.eclipse.gendoc.wizard.IGendocRunner;
import org.eclipse.gendoc.wizard.IGendocTemplate;
import org.eclipse.gendoc.wizard.ISelectionConverter;

/**
 * The Class represent the loader for papyrus document generation.
 * 
 * @author 
 */
public class SysMLGendoc2Runner implements IGendocRunner
{
    List<IGendocTemplate> templates = new ArrayList<IGendocTemplate>();

    public SysMLGendoc2Runner()
    {
    	templates.add(new Gendoc2RunnerDocxSysML());
    }

    /**
     * @return specify all extension of model that papyrusGendoc2Runner can generate the documentation
     */
    public Pattern getPattern()
    {
        return Pattern.compile(".*\\.uml");
    }

    public ISelectionConverter getSelectionConverter()
    {
        return new DefaultSelectionConverter();
    }
    
    /**
     * @return all the template format associated to this Papyrus runner
     */
    public List<IGendocTemplate> getGendocTemplates()
    {
        return templates;
    }

    public String getLabel()
    {
        return "SysML Gendoc2 Generation";
    }

	public Map<String, String> getAdditionnalParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
