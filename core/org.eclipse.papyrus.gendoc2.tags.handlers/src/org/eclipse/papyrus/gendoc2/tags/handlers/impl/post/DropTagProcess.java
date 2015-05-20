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
package org.eclipse.papyrus.gendoc2.tags.handlers.impl.post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.process.AbstractReplaceProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;

public class DropTagProcess extends AbstractReplaceProcess
{

    private boolean dirty;

    @Override
    protected String replace(String text)
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        this.dirty = false;
        for (Pattern pattern : documentService.getDropPatterns(RegisteredTags.DROP))
        {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
            {
                this.dirty = true;
                text = matcher.replaceAll("");
            }
        }
        return text;
    }

    @Override
    protected boolean isDirty()
    {
        return this.dirty;
    }

}
