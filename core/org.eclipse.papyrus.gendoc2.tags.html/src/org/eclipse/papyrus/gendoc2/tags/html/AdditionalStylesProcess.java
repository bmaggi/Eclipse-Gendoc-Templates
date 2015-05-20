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
 * Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.html;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.process.AbstractProcess;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;

/**
 * Adds additional styles to the generated document.
 * 
 * @author Anne Haugommard
 */
public class AdditionalStylesProcess extends AbstractProcess {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.papyrus.gendoc2.process.AbstractProcess#doRun()
	 */
	@Override
	protected void doRun() throws GenDocException {
		final IDocumentManager documentManager = GendocServices.getDefault()
				.getService(IDocumentManager.class);
		Document document = documentManager.getDocTemplate();
		
		final IHtmlService htmlService = GendocServices.getDefault().getService(IHtmlService.class);
		htmlService.addAdditionalStyles(document);
		this.worked(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.papyrus.gendoc2.process.AbstractProcess#getTotalWork()
	 */
	@Override
	protected int getTotalWork() {
		return 1;
	}

}
