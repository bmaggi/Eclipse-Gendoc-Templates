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
 * Alexia Allanic (Atos Origin) alexia.allanic@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.handlers.impl.scripts;

import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.AbstractTagHandler;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;

/**
 * Handler for &lt;include&gt; tags.
 * 
 * @author Alexia Allanic
 */
public class IncludeTagHandler extends AbstractTagHandler {

	@Override
	public String run(ITag tag) throws GenDocException {
		String result = super.run(tag);
		if ((tag != null) && (tag.getAttributes() != null)) {
			String filePath = tag.getAttributes().get(RegisteredTags.INCLUDE_FILE_PATH);
			IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
			String id = documentService.getAdditionalResourceService().includeFile(filePath);
			String output = "&lt;drop/&gt;</w:p><w:altChunk xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + id + "\" />";
            return output;
		}
		return result;
	}

}
