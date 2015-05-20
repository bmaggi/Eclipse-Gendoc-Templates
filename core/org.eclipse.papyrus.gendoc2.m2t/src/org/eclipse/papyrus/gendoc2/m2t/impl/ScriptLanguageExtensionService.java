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
package org.eclipse.papyrus.gendoc2.m2t.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrus.gendoc2.m2t.IM2TProcessor;
import org.eclipse.papyrus.gendoc2.m2t.IScriptLanguageExtensionService;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.exception.UnknownScriptLanguageException;

/**
 * The default implementation of IScriptLanguageExtensionService.
 */
public class ScriptLanguageExtensionService extends AbstractService implements IScriptLanguageExtensionService
{

    private Map<String, IM2TProcessor> languages  = new HashMap<String, IM2TProcessor>();

    /**
     * Instantiates a new ScriptLanguageExtensionService.
     */
    public ScriptLanguageExtensionService ()
    {
        IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(ScriptLanguagesExtensionPoint.EXTENSION_POINT_ID);
        for (IConfigurationElement element : configElements)
        {
            try
            {
                String name = element.getAttribute(ScriptLanguagesExtensionPoint.SCRIPT_LANGUAGE_NAME);
                IM2TProcessor processor = (IM2TProcessor) element.createExecutableExtension(ScriptLanguagesExtensionPoint.SCRIPT_LANGUAGE_PROCESSOR);
                if (processor != null)
                {
                    this.languages.put(name, processor);
                }
            }
            catch (InvalidRegistryObjectException e)
            {
                // TODO log the exception
                e.printStackTrace();
            }
            catch (CoreException e)
            {
                // TODO log the exception
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IService#clear()
     */
    @Override
    public void clear()
    {
        this.languages.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.m2t.IScriptLanguageExtensionService#getProcessor(String)
     */
    public IM2TProcessor getProcessor(String languageName) throws UnknownScriptLanguageException
    {
        if (!this.languages.containsKey(languageName))
        {
            throw new UnknownScriptLanguageException(languageName);
        }
        return this.languages.get(languageName);
    }

}
