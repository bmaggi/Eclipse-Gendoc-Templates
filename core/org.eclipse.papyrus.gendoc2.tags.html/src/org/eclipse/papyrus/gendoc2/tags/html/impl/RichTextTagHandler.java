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
package org.eclipse.papyrus.gendoc2.tags.html.impl;

import java.util.Map;

import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.eclipse.papyrus.gendoc2.tags.html.IHtmlService;

/**
 * Handler for &lt;richText&gt; tags.
 * 
 * @author Kris Robertson
 */
public class RichTextTagHandler extends AbstractTagHandler
{

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler#run(org.eclipse.papyrus.gendoc2.tags.ITag)
     */
    @Override
    public String run(ITag tag) throws GenDocException
    {
        String value = super.run(tag);
        IHtmlService htmlService = GendocServices.getDefault().getService(IHtmlService.class);
        value = htmlService.convert(value);
        return value;
    }

    @Override
    protected String runAttributes(ITag tag, String value) throws GenDocException
    {
        IHtmlService htmlService = GendocServices.getDefault().getService(IHtmlService.class);

        Map<String, String> attributes = tag.getAttributes();
        if (attributes != null)
        {
            htmlService.setVersion(attributes.get("version"));
        }
        
        return value;
    }
}
