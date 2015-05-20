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
package org.eclipse.papyrus.gendoc2.process;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.w3c.dom.Node;

/**
 * An abstract base class for processes that step through each part of a document replacing text.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractReplaceProcess extends AbstractStepProcess
{

    /**
     * Indicates if the last call to replace modified the given text or if it was returned as-is.
     * 
     * @return true if the text was modified, otherwise false
     */
    protected abstract boolean isDirty();

    /**
     * Runs a replacement operation on the given text and returns the result.
     * 
     * @param text the text to run the replace operation on
     * @return the modified text
     */
    protected abstract String replace(String text);

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.AbstractStepProcess#step(org.eclipse.papyrus.document.parser.documents.Document)
     */
    @Override
    protected void step(Document document) throws GenDocException
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);

        Node rootNode = document.getXMLParser().getCurrentNode().getOwnerDocument().getDocumentElement();
        Node currentNode = rootNode.getFirstChild();
        while (currentNode != null)
        {
            String text = documentService.asText(currentNode);
            text = this.replace(text);
            if (this.isDirty())
            {
                Node newNode = currentNode.getNextSibling();
                if (text.length() > 0)
                {
                    newNode = documentService.injectNode(currentNode, text);
                }
                currentNode.getParentNode().removeChild(currentNode);
                currentNode = newNode.getNextSibling();
            }
            else
            {
                currentNode = currentNode.getNextSibling();
            }
            document.getXMLParser().setCurrentNode(currentNode);
        }

        this.worked(1);
    }

}
