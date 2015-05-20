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
package org.eclipse.papyrus.gendoc2.tags.html;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.services.IService;

/**
 * The HTML service converts html into document specific text.
 * 
 * @author Kris Robertson
 */
public interface IHtmlService extends IService
{

    /**
     * Converts html to document specific text
     * 
     * @param html the html to convert
     * @return the converted text
     */
    String convert(String html);

    /**
     * Specify a compatibility version for the generated doc (usefull for word 2003 vs 2007)
     * 
     * @param version the compatibility version of the generated doc, can be null or empty (use default in this case).
     */
    void setVersion(String version);
    
    
    /**
     * Add eventual additional styles (due to HTML import) to the final document (during AdditionalStylesProcess)
     */
    void addAdditionalStyles(Document document);

}
