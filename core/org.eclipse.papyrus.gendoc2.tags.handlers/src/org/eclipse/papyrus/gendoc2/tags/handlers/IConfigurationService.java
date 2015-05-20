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
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.handlers;

import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidTemplateParameterException;

/**
 * The configuration service is used to manage the configuration values for the document generation. These values are
 * set within a &lt;config&gt; tag in a document template.
 * 
 * @author cbourdeu
 */
public interface IConfigurationService extends IService
{

    void addParameter(String name, String value);

    String getImportedDiagrams();

    /**
     * Gets the script language to use for generation. The default value is 'acceleo'.
     * 
     * @return the script language
     */
    String getLanguage();

    String getOutput() throws GenDocException;

    String getParameter(String name);

    /**
     * Returns the gendoc version. The default value is '2'.
     * 
     * @return the gendoc version
     */
    int getVersion();

    boolean isRunV1();

    /**
     * Replaces any parameters in the given string with their actual values.
     * 
     * @param path
     * @return the given string with all parameters replaced by their actual values
     */
    String replaceParameters(String path) throws InvalidTemplateParameterException;

    void setImportedDiagrams(String importedDiagrams);

    /**
     * Sets the script language to use for generation. The language must be registered on the
     * <samp>org.eclipse.papyrus.gendoc2.scriptLanguages extension point.
     * 
     * @param language the script language to set
     */
    void setLanguage(String language);

    void setOutput(String output) throws GenDocException;

    void setRunV1(String runV1);

    /**
     * Sets the gendoc version.
     * 
     * @param version the version to set
     */
    void setVersion(String version);
    
}
