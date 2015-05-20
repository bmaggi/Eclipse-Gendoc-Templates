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
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.process.impl.CountStepsProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * <p>
 * An abstract base class for processes that must step through each part of document.
 * </p>
 * <p>
 * Note: Processes that extend AbstractStepProcess must run after the countSteps process.
 * </p>
 * 
 * @author Kris Robertson
 */
public abstract class AbstractStepProcess extends AbstractProcess
{

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.AbstractProcess#doRun()
     */
    @Override
    protected void doRun() throws GenDocException
    {
        final IDocumentManager documentManager = GendocServices.getDefault().getService(IDocumentManager.class);
        Document document = documentManager.getDocTemplate();
        document.jumpToStart();

        boolean next = document.next();
        while (next && !this.isCanceled())
        {
            this.step(document);
            next = document.next();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.process.AbstractProcess#getTotalWork()
     */
    @Override
    protected int getTotalWork()
    {
        final IRegistryService registryService = GendocServices.getDefault().getService(IRegistryService.class);
        return (Integer) registryService.get(CountStepsProcess.class);
    }

    /**
     * This method is called at each step of the iteration. It is the responsibility of the implemented method to call
     * worked(1) at the end of each step or utilise a sub monitor to display progress.
     * 
     * @param document the document
     * @throws GenDocException
     */
    protected abstract void step(Document document) throws GenDocException;

}
