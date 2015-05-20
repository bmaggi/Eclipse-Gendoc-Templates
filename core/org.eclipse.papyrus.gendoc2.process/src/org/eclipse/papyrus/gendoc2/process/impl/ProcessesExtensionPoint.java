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
package org.eclipse.papyrus.gendoc2.process.impl;

import org.eclipse.osgi.util.NLS;

/**
 * The ProcessesExtensionPoint class provides constants describing the processes extension point
 * 
 * @author Kris Robertson
 */
public class ProcessesExtensionPoint extends NLS
{

    /** The extension point ID. */
    public static String EXTENSION_POINT_ID;

    /** process element. */
    public static String PROCESS;

    /** process element -> id attribute. */
    public static String PROCESS_ID;

    /** process element -> label attribute. */
    public static String PROCESS_LABEL;

    /** process element -> processor attribute. */
    public static String PROCESS_PROCESSOR;

    /** process element -> parallel flag attribute. */
    public static String PROCESS_PARALLEL;

    /** process element -> priority attribute. */
    public static String PROCESS_PRIORITY;

    /** predecessor element. */
    public static String PREDECESSOR;

    /** predecessor element -> ref attribute. */
    public static String PREDECESSOR_REF;

    /** successor element. */
    public static String SUCCESSOR;

    /** successor element -> ref attribute. */
    public static String SUCCESSOR_REF;

    // initialize message bundle
    static
    {
        NLS.initializeMessages(ProcessesExtensionPoint.class.getName(), ProcessesExtensionPoint.class);
    }

}
