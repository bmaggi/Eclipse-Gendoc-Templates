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
 *  Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.documents;

/**
 * The Interface ResourceRunnable.
 */
public interface ResourceRunnable
{

    /**
     * Run.
     * 
     * @param resourceId the resource id
     */
    void run(String resourceId, String outputResourceFolder);
    
}
