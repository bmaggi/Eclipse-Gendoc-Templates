/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.documents;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.services.IService;

public interface IDocumentManager extends IService
{

	Document getDocument (File templateDoc);
	
	Document getDocument (URL url);

	void setDocTemplate (Document document);

	Document getDocTemplate ();

}
