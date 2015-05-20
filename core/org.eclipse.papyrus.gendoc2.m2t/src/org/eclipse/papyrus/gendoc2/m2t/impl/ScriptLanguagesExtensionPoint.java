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

import org.eclipse.osgi.util.NLS;

public class ScriptLanguagesExtensionPoint extends NLS
{

    /** The extension point ID. */
    public static String EXTENSION_POINT_ID;

    /** scriptLanguage extension. */
    public static String SCRIPT_LANGUAGE;
    /** scriptLanguage extension name. */
    public static String SCRIPT_LANGUAGE_NAME;
    /** scriptLanguage extension processor. */
    public static String SCRIPT_LANGUAGE_PROCESSOR;

    // initialize message bundle
    static
    {
        NLS.initializeMessages(ScriptLanguagesExtensionPoint.class.getName(), ScriptLanguagesExtensionPoint.class);
    }

}
