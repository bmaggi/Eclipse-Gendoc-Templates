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
package org.eclipse.papyrus.gendoc.robotml.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.papyrus.gendoc2.bundle.acceleo.papyrus.utils.OneFileUMLSelectionConverter;
import org.eclipse.papyrus.gendoc2.wizard.IGendoc2Runner;
import org.eclipse.papyrus.gendoc2.wizard.IGendoc2Template;
import org.eclipse.papyrus.gendoc2.wizard.ISelectionConverter;

/**
 * The Class represent the loader for papyrus document generation.
 * 
 * @author 
 */
public class RobotMLGendoc2Runner implements IGendoc2Runner 
{
    List<IGendoc2Template> templates = new ArrayList<IGendoc2Template>();

    public RobotMLGendoc2Runner()
    {
    	templates.add(new Gendoc2RunnerDocxRobotML());
    	//templates.add(new Gendoc2RunnerDocx());
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
        return new OneFileUMLSelectionConverter();
    }
    
    /**
     * @return all the template format associated to this Papyrus runner
     */
    public List<IGendoc2Template> getGendoc2Templates()
    {
        return templates;
    }

    public String getLabel()
    {
        return "RobotML Gendoc2 Generation";
    }

}
