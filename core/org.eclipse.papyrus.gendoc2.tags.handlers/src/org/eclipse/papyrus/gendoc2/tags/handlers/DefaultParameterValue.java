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
 *  Tristan FAURE (Atos Origin) tristan.faure@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.handlers;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.config.IParameterValue;

/**
 * A Parameter Value which returns the location of the workspace
 * 
 * @author tfaure
 */
public class DefaultParameterValue implements IParameterValue
{

    private static final String KEY_WORKSPACE = "workspace_loc";

    private static final String KEY_INPUT = "input";

    private static final String KEY_DATE = "date";

    public Map<String, String> getValue()
    {
        Map<String, String> result = new HashMap<String, String>();

        // fill user workspace location
        IWorkspace wk = ResourcesPlugin.getWorkspace();
        if (wk != null)
        {
            IWorkspaceRoot root = wk.getRoot();
            if (root != null)
            {
                URI uri = root.getLocationURI();
                if (uri != null)
                {
                    result.put(KEY_WORKSPACE, uri.getPath());
                }
            }
        }

        // fill date of the generation
        Date actual = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        result.put(KEY_DATE, format.format(actual));

        // fill document name
        try
        {
            IDocumentService docService = GendocServices.getDefault().getService(IDocumentService.class);
            if (docService != null)
            {
                String name = docService.getDocument().getPath().substring(docService.getDocument().getPath().lastIndexOf('/') + 1, docService.getDocument().getPath().length());
                name = name.substring(0, name.lastIndexOf("."));
                result.put(KEY_INPUT, name);
            }
        }
        catch (RuntimeException e)
        {
            // In some case, the document service is not available
        }
        return result;
    }
}
