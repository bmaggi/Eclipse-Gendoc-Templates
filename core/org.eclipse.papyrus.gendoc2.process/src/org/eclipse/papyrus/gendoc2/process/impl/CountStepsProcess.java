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
 * Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.process.impl;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.process.AbstractStepProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * The Class CountProcess.
 */
public class CountStepsProcess extends AbstractStepProcess
{

    private int sum = 0;

    @Override
    public void doRun() throws GenDocException
    {
        super.doRun();
        final IRegistryService registryService = GendocServices.getDefault().getService(IRegistryService.class);
        registryService.put(CountStepsProcess.class, this.sum);
    }

    @Override
    protected int getTotalWork()
    {
        return 1;
    }

    @Override
    protected void step(Document document)
    {
        this.sum++;
        this.worked(1);
    }

}
