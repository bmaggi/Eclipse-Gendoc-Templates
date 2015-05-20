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
 * Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 * 
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.services.impl;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.papyrus.gendoc2.document.parser.Activator;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.ILogger;

/**
 * The default implementation of IGendocDiagnostician.
 * 
 * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician
 */
public final class GendocDiagnostician extends AbstractService implements IGendocDiagnostician
{

    /** The diagnostic. */
    private BasicDiagnostic basicDiagnostic;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician#addDiagnostic(org.eclipse.emf.common.util.Diagnostic)
     */
    public void addDiagnostic(Diagnostic diagnostic)
    {
        this.basicDiagnostic.add(diagnostic);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician#addDiagnostic(int, String, Object)
     */
    public void addDiagnostic(int severity, String message, Object target)
    {
        this.basicDiagnostic.add(new BasicDiagnostic(severity, Activator.PLUGIN_ID, 0, message, new Object[] {target}));
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        logger.log(message, severity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician#addDiagnostics(org.eclipse.emf.common.util.Diagnostic)
     */
    public void addDiagnostics(Diagnostic otherDiagnostics)
    {
        this.basicDiagnostic.addAll(otherDiagnostics);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician#getResultDiagnostic()
     */
    public Diagnostic getResultDiagnostic()
    {
        return this.basicDiagnostic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician#init()
     */
    public void init()
    {
        this.basicDiagnostic = new BasicDiagnostic(Activator.PLUGIN_ID, 0, "See details.", null);
    }

}
