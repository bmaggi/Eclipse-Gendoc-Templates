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
 *  Alexia Allanic (Atos Origin) alexia.allanic@atosorigin.com - Initial API and implementation
 *  Papa Malick Wade (Atos Origin) papa-malick.wade@atosorigin.com - extension the format of diagrams 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.bundle.acceleo.gmf.impl;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Factory;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.gendoc2.documents.FileRunnable;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.tags.handlers.Activator;

public class GMFDiagramRunnable implements FileRunnable
{

    private Diagram diagram;

    private enum FileFormat {
        PNG, JPEG, GIF, BMP;
        public String getFullExtension()
        {
            return "." + name().toLowerCase();
        }
    }

    /** The extension of diagram. */
    private FileFormat extension;

    public GMFDiagramRunnable(Diagram diagram, String extension)
    {

        this.diagram = diagram;
        this.extension = transformToFormat(extension);

    }

    private FileFormat transformToFormat(String ext)
    {
        FileFormat format;
        try
        {
            format = FileFormat.valueOf(ext.toUpperCase());
            return format;
        }
        catch (IllegalArgumentException e)
        {
            IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);
            String message = "The format " + ext + " is not supported";
            diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0, message, null));
            return FileFormat.valueOf(ext);
        }

    }

    /**
     * Instantiates a new diagram runnable.
     * 
     * @param diagram the diagram
     */

    public void run(String resourceId, String outputResourceFolder)
    {
        if (extension != null)
        {

            CopyToImageUtil c = new CopyToImageUtil();
            new File(outputResourceFolder).mkdirs();
            IPath path = new Path(outputResourceFolder + "/" + resourceId + extension.getFullExtension());
            try
            {
                // this part of code is necessary for Transactional Editing Domain by GMF
                // if this section is removed a NPE occurs in copyToImage Function (no Editing Domain)
                {
                    Resource eResource = diagram.eResource();
                    if (eResource != null)
                    {
                        ResourceSet resourceSet = eResource.getResourceSet();
                        if (TransactionUtil.getEditingDomain(resourceSet) == null)
                        {
                            Factory factory = TransactionalEditingDomain.Factory.INSTANCE;
                            factory.createEditingDomain(resourceSet);
                        }
                    }
                }
                c.copyToImage(diagram, path, getImageFileFormat(extension), new NullProgressMonitor(), PreferencesHint.USE_DEFAULTS);
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

    private ImageFileFormat getImageFileFormat(FileFormat format)
    {
        return ImageFileFormat.resolveImageFormat(format.name());
    }

    public String getFileExtension()
    {
        return extension.toString().toLowerCase();
    }

}
