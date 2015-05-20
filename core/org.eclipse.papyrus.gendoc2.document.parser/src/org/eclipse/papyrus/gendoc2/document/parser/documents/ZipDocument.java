/*****************************************************************************
 * Copyright (c) 2008 Atos Origin.
 * 
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Tristan Faure (Atos Origin) tristan.faure@atosorigin.com -
 * Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.document.parser.documents;

import java.io.File;

/**
 * The Interface ZipDocument.
 */
public interface ZipDocument extends Document
{

    /**
     * 
     * @param locationToZip
     */
    File getUnzipLocationDocumentFile();

    void zipToLocation(String locationToZip);
}
