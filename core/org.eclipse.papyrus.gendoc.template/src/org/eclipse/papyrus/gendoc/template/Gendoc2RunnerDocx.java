/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Barthelemy HABA (Atos Origin) barthelemy.haba@atosorigin.com - 
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc.template;

import java.net.URL;

import org.eclipse.papyrus.gendoc2.wizard.IGendoc2Template;

/**
 * this class represent the format docx of template
 */
public class Gendoc2RunnerDocx implements IGendoc2Template
{

    /** The description of this kind of template format */
    private String description = "Generic MS Word 2010 generation template example";

    public String getOutPutExtension()
    {
        return "docx";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.topcased.gendoc2.wizard.IGendoc2Template#getTemplate()
     */
    public URL getTemplate()
    {
        return Activator.getDefault().getBundle().getEntry("/resource/templatePapyrus.docx");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.topcased.gendoc2.wizard.IGendoc2Template#getModelKey()
     */
    public String getModelKey()
    {
        return "generic_generation_model";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.topcased.gendoc2.wizard.IGendoc2Template#getOutputKey()
     */
    public String getOutputKey()
    {
        return "generic_generation_output";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.topcased.gendoc2.wizard.IGendoc2Template#getDescription()
     */
    public String getDescription()
    {
        return description;
    }
    
    public String getName() {
		return "UML(Generic)";
	}

}
