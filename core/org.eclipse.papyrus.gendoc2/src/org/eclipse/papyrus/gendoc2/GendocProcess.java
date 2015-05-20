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
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.documents.SourceException;
import org.eclipse.papyrus.gendoc2.process.IProcessExtensionService;
import org.eclipse.papyrus.gendoc2.process.impl.ProcessExtension;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;
import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.GenerationException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IConfigurationService;

/**
 * The Class GendocProcess.
 */
public class GendocProcess
{

    /** Delimiter used to separate service IDs. */
    private static final String SERVICES_DELIMITER = ";";

    public URL source = null;

    public String runProcess() throws GenDocException
    {
        if (source == null)
        {
            throw new SourceException("Please provide an input");
        }
        // create the document
        final IDocumentManager documentManager = GendocServices.getDefault().getService(IDocumentManager.class);
        
        Document document = null;
        try
        {
            document = documentManager.getDocument(source);
        }
        catch (RuntimeException e)
        {
            throw new GenerationException("Document " + source.toString() + " can not be loaded",e);
        }
        documentManager.setDocTemplate(document);
        // run each registered process
        final IProcessExtensionService processExtensionService = GendocServices.getDefault().getService(IProcessExtensionService.class);
        final IProgressMonitorService progressMonitorService = GendocServices.getDefault().getService(IProgressMonitorService.class);
        progressMonitorService.beginTask("Generating Document", processExtensionService.getNumProcesses());
        while (!progressMonitorService.isCanceled() && processExtensionService.next())
        {
            try
            {
                List<ProcessExtension> runNext = processExtensionService.getProcesses();
                if (runNext.size() == 1)
                {
                    // we only have one process - run it
                    runNext.get(0).getProcess().run();
                }
                else
                // we have several processes - run them in parallel
                {
                    // assign a semaphore for each process
                    List<Semaphore> semaphores = new ArrayList<Semaphore>(runNext.size());
                    for (final ProcessExtension processExtension : runNext)
                    {
                        final Semaphore s = new Semaphore(0);
                        semaphores.add(s);
                        Thread t = new Thread(new java.lang.Runnable()
                        {
                            public void run()
                            {
                                try
                                {
                                    processExtension.getProcess().run(s);
                                }
                                catch (GenDocException e)
                                {
                                    ((ILogger) GendocServices.getDefault().getService(ILogger.class)).log(e.getUIMessage(), Status.WARNING);
                                }
                            }
                        });
                        t.start();
                    }
                    // wait for the end of each process
                    for (Semaphore s : semaphores)
                    {
                        s.acquire();
                    }
                }
            }
            catch (InterruptedException e)
            {
                ((ILogger) GendocServices.getDefault().getService(ILogger.class)).log("Parallel process has been interrupted.", IStatus.ERROR);
                break;
            }
        }

        // get output path
        final IConfigurationService configurationService = GendocServices.getDefault().getService(IConfigurationService.class);
        String outputPath = configurationService.getOutput();

        // clean up
        GendocServices.getDefault().clear();
        return outputPath;
    }

    /**
     * Run the document generation process.
     * 
     * @param templateDoc the template document
     * @throws GenDocException
     */
    public String runProcess(File templateDoc) throws GenDocException
    {
        setSource(templateDoc);
        return runProcess();
    }
    
    /**
     * Run the document generation process.
     * 
     * @param templateDoc the template document
     * @throws GenDocException
     */
    public String runProcess(URL templateDoc) throws GenDocException
    {
        setSource(templateDoc);
        return runProcess();
    }
    
    public void setSource(File templateDoc) throws SourceException
    {
        try
        {
            source = templateDoc.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            throw new SourceException("Please provide a correct file");
        }
    }

    public void setSource (URL url) throws SourceException
    {
        source = url ;
    }

    /**
     * Run the document generation with the given services.
     * 
     * @param templateDoc the template document
     * @param services the services to use for the generation
     * @return the output file path
     * @throws GenDocException
     */
    public String runProcess(File templateDoc, Map<Class< ? extends IService>, IService> services) throws GenDocException
    {
        for (Class< ? extends IService> key : services.keySet())
        {
            GendocServices.getDefault().setService(key, services.get(key));
        }
        return this.runProcess(templateDoc);
    }
    
    /**
     * Run the document generation with the given services.
     * 
     * @param templateDoc the template document
     * @param services the services to use for the generation
     * @return the output file path
     * @throws GenDocException
     */
    public String runProcess(URL templateDoc, Map<Class< ? extends IService>, IService> services) throws GenDocException
    {
        for (Class< ? extends IService> key : services.keySet())
        {
            GendocServices.getDefault().setService(key, services.get(key));
        }
        return this.runProcess(templateDoc);
    }

    /**
     * Run the document generation with the given services.
     * 
     * @param templateDoc the template document
     * @param services the services to use for the generation separated by semicolons
     * @return the output file path
     * @throws GenDocException
     */
    public String runProcess(File templateDoc, String services) throws GenDocException
    {
        String[] serviceIds = services.split(GendocProcess.SERVICES_DELIMITER);
        for (String serviceId : serviceIds)
        {
            GendocServices.getDefault().setService(serviceId);
        }
        return this.runProcess(templateDoc);
    }
    
    /**
     * Run the document generation with the given services.
     * 
     * @param templateDoc the template document
     * @param services the services to use for the generation separated by semicolons
     * @return the output file path
     * @throws GenDocException
     */
    public String runProcess(URL templateDoc, String services) throws GenDocException
    {
        String[] serviceIds = services.split(GendocProcess.SERVICES_DELIMITER);
        for (String serviceId : serviceIds)
        {
            GendocServices.getDefault().setService(serviceId);
        }
        return this.runProcess(templateDoc);
    }

}
