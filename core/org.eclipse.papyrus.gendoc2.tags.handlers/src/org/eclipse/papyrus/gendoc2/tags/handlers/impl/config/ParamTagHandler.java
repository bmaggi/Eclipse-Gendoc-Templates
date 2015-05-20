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
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.eclipse.papyrus.gendoc2.tags.handlers.IConfigurationService;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;
import org.eclipse.papyrus.gendoc2.tags.parsers.TagParserConfig;

/**
 * Handler for &lt;param&gt; tags.
 * 
 * @author Kris Robertson
 */
public class ParamTagHandler extends AbstractTagHandler
{

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler#runAttributes(org.eclipse.papyrus.gendoc2.tags.ITag, String)
     */
    @Override
    protected String runAttributes(ITag tag, String value) throws GenDocException
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        StringBuffer buffer = new StringBuffer("ParamTagHandler.runAttributes  -> Tag :");
        buffer.append(tag.getName());
        buffer.append(" value : ");
        buffer.append(value);
        buffer.append("\n");
        logger.log(buffer.toString(), ILogger.DEBUG);

        super.runAttributes(tag, value);

        IConfigurationService configService = GendocServices.getDefault().getService(IConfigurationService.class);
        logger.log("ParamTagHandler.runAttributes  -> Configuration service =" + configService == null ? "null" : configService.toString(), ILogger.DEBUG);
        String paramKey = tag.getAttributes().get(RegisteredTags.PARAM_KEY);
        String paramValue = tag.getAttributes().get(RegisteredTags.PARAM_VALUE);

        logger.log("ParamTagHandler.runAttributes  -> Parameter key='" + paramKey + "' value='" + paramValue + "'.", ILogger.DEBUG);

        paramValue = configService.replaceParameters(paramValue);
        logger.log("ParamTagHandler.runAttributes  -> (after replaceParameters) Parameter key='" + paramKey + "' value='" + paramValue + "'.", ILogger.DEBUG);
        if (paramValue != null)
        {
            paramValue = paramValue.replace(TagParserConfig.BACKSLASH, TagParserConfig.SLASH_CHAR);
        }
        logger.log("ParamTagHandler.runAttributes  -> (after replaceSlashs) Parameter key='" + paramKey + "' value='" + paramValue + "'.", ILogger.DEBUG);
        configService.addParameter(paramKey, paramValue);

        return value;
    }

}
