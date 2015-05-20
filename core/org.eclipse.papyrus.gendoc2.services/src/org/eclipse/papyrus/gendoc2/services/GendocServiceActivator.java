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
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GendocServiceActivator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.papyrus.gendoc2.services";

    // The shared instance
    private static GendocServiceActivator plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static GendocServiceActivator getDefault()
    {
        return GendocServiceActivator.plugin;
    }

    /**
     * @return the Plugin Id
     */
    public static String getId()
    {
        return GendocServiceActivator.getDefault().getBundle().getSymbolicName();
    }

    /**
     * Log an IStatus
     * 
     * @param status
     */
    public static void log(IStatus status)
    {
        GendocServiceActivator.plugin.getLog().log(status);
    }

    /**
     * Log a message with given level into the Eclipse log file
     * 
     * @param message the message to log
     * @param level the message priority
     */
    public static void log(String message, int status)
    {
        GendocServiceActivator.log(GendocServiceActivator.getId(), status, message, null);
    }

    public static void log(String id, int status, String message, Object object)
    {
        GendocServiceActivator.log(new Status(status, id, status, message, null));
    }

    /**
     * The constructor
     */
    public GendocServiceActivator()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        GendocServiceActivator.plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        GendocServiceActivator.plugin = null;
        super.stop(context);
    }

}
