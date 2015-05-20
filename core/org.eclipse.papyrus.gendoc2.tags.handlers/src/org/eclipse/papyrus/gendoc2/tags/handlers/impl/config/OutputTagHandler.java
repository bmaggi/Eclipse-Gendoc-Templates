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
 * Kris Robertson (Atos Origin) kris.robertson@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.handlers.impl.config;

import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidTemplateParameterException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.eclipse.papyrus.gendoc2.tags.handlers.IConfigurationService;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;

/**
 * Handler for &lt;output&gt; tags.
 * 
 * @author Kris Robertson
 */
public class OutputTagHandler extends AbstractTagHandler
{

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler#runAttributes(org.eclipse.papyrus.gendoc2.tags.ITag, String)
     */
    @Override
    protected String runAttributes(ITag tag, String value) throws GenDocException
    {
        super.runAttributes(tag, value);
        IConfigurationService configService = GendocServices.getDefault().getService(IConfigurationService.class);
        for (String key : tag.getAttributes().keySet())
        {
            if (RegisteredTags.OUTPUT_PATH.equalsIgnoreCase(key))
            {
                String path = this.replaceParameters(tag.getAttributes().get(key));
                configService.setOutput(path.replaceAll("\\\\", "/"));
            }
        }
        return value;
    }

    /**
     * Replaces param references in the given path with their actual values.
     * 
     * @param path the path
     * 
     * @return the path with parameter references replaced by actual values
     * 
     * @throws InvalidTemplateParameterException if a referenced parameter does not exist
     */
    private String replaceParameters(String path) throws InvalidTemplateParameterException
    {
        IConfigurationService configService = GendocServices.getDefault().getService(IConfigurationService.class);
        return configService.replaceParameters(path);
    }

}
