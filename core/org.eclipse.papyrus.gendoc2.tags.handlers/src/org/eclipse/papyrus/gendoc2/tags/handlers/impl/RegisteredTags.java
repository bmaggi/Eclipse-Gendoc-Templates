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
package org.eclipse.papyrus.gendoc2.tags.handlers.impl;

import org.eclipse.osgi.util.NLS;

/**
 * The RegisteredTags class provides string constants that describe the tags that have been registered to the tags
 * extension point.
 * 
 * @author Kris Robertson
 */
public class RegisteredTags extends NLS
{

    // config
    public static String CONFIG;

    public static String CONFIG_VERSION;

    public static String CONFIG_LANGUAGE;

    public static String CONFIG_RUN_V1;

    public static String CONFIG_SERVICES;

    // importedDiagrams
    public static String IMPORTED_DIAGRAMS;

    public static String IMPORTED_DIAGRAMS_PATH;

    // output
    public static String OUTPUT;

    public static String OUTPUT_PATH;

    // param
    public static String PARAM;

    public static String PARAM_KEY;

    public static String PARAM_VALUE;

    // context
    public static String CONTEXT;

    public static String CONTEXT_MODEL;

    public static String CONTEXT_ELEMENT;

    public static String CONTEXT_IMPORTED_BUNDLES;

    public static String CONTEXT_FEATURE_LABEL;

    public static String CONTEXT_SEARCH_METAMODELS;

    // gendoc
    public static String GENDOC;

    // image
    public static String IMAGE;

    public static String IMAGE_FILE_PATH;

    public static String IMAGE_KEEP_H;

    public static String IMAGE_KEEP_W;

    public static String IMAGE_MAX_H;

    public static String IMAGE_MAX_W;

    public static String IMAGE_OBJECT;

    // list
    public static String LIST;

    // table
    public static String TABLE;

    // drop
    public static String DROP;
    
    // nobr
    public static String NOBR;
    
    // input
    public static String INCLUDE_FILE_PATH;
    
    // id
    public static String ID;
    
    // initialize message bundle
    static
    {
        NLS.initializeMessages(RegisteredTags.class.getName(), RegisteredTags.class);
    }

}
