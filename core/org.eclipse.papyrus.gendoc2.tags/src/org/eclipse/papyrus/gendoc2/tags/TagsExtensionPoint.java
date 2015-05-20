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
package org.eclipse.papyrus.gendoc2.tags;

import org.eclipse.osgi.util.NLS;

/**
 * The TagsExtensionPoint class provides string constants describing the tags extension point elements.
 * 
 * @author Kris Robertson
 */
public class TagsExtensionPoint extends NLS
{

    /** The extension point ID. */
    public static String EXTENSION_POINT_ID;

    /** The top level category. */
    public static String TOP_LEVEL_CATEGORY;

    /** category extension. */
    public static String CATEGORY;
    /** category extension name. */
    public static String CATEGORY_NAME;

    /** tag extension. */
    public static String TAG;
    /** tag extension name. */
    public static String TAG_NAME;
    /** tag extension handler. */
    public static String TAG_HANDLER;

    /** attribute extension. */
    public static String ATTRIBUTE;
    /** attribute extension name. */
    public static String ATTRIBUTE_NAME;
    /** attribute extension type. */
    public static String ATTRIBUTE_TYPE;
    /** attribute extension required. */
    public static String ATTRIBUTE_REQUIRED;
    /** attribute extension default. */
    public static String ATTRIBUTE_DEFAULT;

    /** tagref extension. */
    public static String TAGREF;
    /** tagref extension name. */
    public static String TAGREF_NAME;

    // initialize message bundle
    static
    {
        NLS.initializeMessages(TagsExtensionPoint.class.getName(), TagsExtensionPoint.class);
    }

}
