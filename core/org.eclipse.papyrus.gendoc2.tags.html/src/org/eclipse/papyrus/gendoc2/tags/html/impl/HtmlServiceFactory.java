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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.services.GendocServices;

/**
 * The HTML service factory creates a new HTML service for the correct document type.
 *   
 * @author Kris Robertson
 */
public class HtmlServiceFactory implements IExecutableExtensionFactory
{

    /*
     * (non-Javadoc)
     * 
     * @see IExecutableExtensionFactory#create()
     */
    public Object create() throws CoreException
    {
        // FIXME HtmlService should either be returned by the document service or specified in the documentTypes extension point
        String extension = "";
        Document document = ((IDocumentManager)GendocServices.getDefault().getService(IDocumentManager.class)).getDocTemplate();
        if (document != null)
        {
            extension = new Path(document.getPath()).getFileExtension();
        }
        if (extension.equals("docx"))
        {
            return new DOCXHtmlService();
        }
        else if (extension.equals("odt"))
        {
            return new ODTHtmlService();
        }
        return null;
    }

}
