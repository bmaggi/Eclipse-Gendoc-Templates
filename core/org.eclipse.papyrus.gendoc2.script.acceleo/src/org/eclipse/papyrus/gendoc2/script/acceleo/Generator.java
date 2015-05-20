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
package org.eclipse.papyrus.gendoc2.script.acceleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.acceleo.engine.AcceleoEngineMessages;
import org.eclipse.acceleo.engine.AcceleoEvaluationException;
import org.eclipse.acceleo.engine.generation.strategy.PreviewStrategy;
import org.eclipse.acceleo.engine.service.AcceleoService;
import org.eclipse.acceleo.model.mtl.Module;
import org.eclipse.acceleo.model.mtl.ModuleElement;
import org.eclipse.acceleo.model.mtl.MtlPackage;
import org.eclipse.acceleo.model.mtl.Template;
import org.eclipse.acceleo.model.mtl.VisibilityKind;
import org.eclipse.acceleo.parser.AcceleoParser;
import org.eclipse.acceleo.parser.AcceleoSourceBuffer;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.papyrus.gendoc2.script.acceleo.exception.AcceleoParsingException;
import org.eclipse.papyrus.gendoc2.services.exception.GenerationException;
import org.eclipse.papyrus.gendoc2.services.exception.ParsingException;

/**
 * @author cbourdeu
 * 
 */
public class Generator
{

    /**
     * System Temporary directory
     */
    private static String tempDir = System.getProperty("java.io.tmpdir");
    
    /**
     * The name of the templates that are to be generated.
     */
    private static String templateNames = "template";

    /**
     * The root element of the module.
     */
    private Module module;

    /**
     * The model.
     */
    private final EObject model;

    /** A private ResourceSet is used in order to not add resource (in the model's ResourceSet) 
        that could be loaded in the services*/
    private ResourceSet resourceSet;

    /**
     * Constructor.
     * 
     * @param modelURI is the URI of the model.
     * @throws IOException Thrown when the output cannot be saved.
     * @throws ParsingException
     */
    public Generator(EObject model, String script) throws IOException, ParsingException
    {
        String moduleName = tempDir + templateNames + ".emtl";

        resourceSet = new ResourceSetImpl();
        registerResourceFactories(resourceSet);
        registerPackages(resourceSet);

        this.model = model;

        AcceleoParser parser = new AcceleoParser();
        StringBuffer textBuffer = new StringBuffer(script);

        Resource r = resourceSet.createResource(URI.createFileURI(moduleName));
        AcceleoSourceBuffer source = new AcceleoSourceBuffer(textBuffer);
        parser.parse(source, r, ServicesExtension.getInstance().getResources());
        if (!source.getProblems().getList().isEmpty())
        {
            throw new AcceleoParsingException(source.getProblems().toString());
        }
        if (r.getContents().size() > 0)
        {
            module = (Module) r.getContents().get(0);
        }

    }

    /**
     * Updates the registry used for looking up resources factory in the given resource set.
     * 
     * @param r The resource set that is to be updated.
     * @generated
     */
    private void registerResourceFactories(ResourceSet r)
    {
        r.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        r.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emtl", new org.eclipse.acceleo.model.mtl.resource.EMtlResourceFactoryImpl());
        r.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    }

    /**
     * Updates the registry used for looking up a package based namespace, in the resource set.
     * 
     * @param r is the resource set
     * @generated
     */
    private void registerPackages(ResourceSet r)
    {
        r.getPackageRegistry().put(org.eclipse.ocl.ecore.EcorePackage.eINSTANCE.getNsURI(), org.eclipse.ocl.ecore.EcorePackage.eINSTANCE);
        r.getPackageRegistry().put(org.eclipse.ocl.expressions.ExpressionsPackage.eINSTANCE.getNsURI(), org.eclipse.ocl.expressions.ExpressionsPackage.eINSTANCE);
        r.getPackageRegistry().put(MtlPackage.eINSTANCE.getNsURI(), MtlPackage.eINSTANCE);
        r.getPackageRegistry().put("http://www.eclipse.org/ocl/1.1.0/oclstdlib.ecore", getOCLStdLibPackage());
    }

    /**
     * Creates the template URI.
     * 
     * @param entry is the local path of the EMTL file
     */
    protected URI createTemplateURI(String entry)
    {
        return URI.createFileURI(URI.decode(entry));
    }

    /**
     * Returns the package containing the OCL standard library.
     * 
     * @return The package containing the OCL standard library.
     */
    private EPackage getOCLStdLibPackage()
    {
        EcoreEnvironmentFactory factory = new EcoreEnvironmentFactory();
        EcoreEnvironment environment = (EcoreEnvironment) factory.createEnvironment();
        return (EPackage) EcoreUtil.getRootContainer(environment.getOCLStandardLibrary().getBag());
    }

    /**
     * Launches the generation.
     * 
     * @param monitor This will be used to display progress information to the user.
     * @return
     * @throws IOException Thrown when the output cannot be saved.
     * @generated
     */
    public String doGenerate(Monitor monitor) throws IOException, GenerationException
    {
        File targetFolder = new File(tempDir);
        if (!targetFolder.exists())
        {
            targetFolder.mkdirs();
        }

        // Returns a map <fileName, fileContent>
        Map<String, String> result = doGenerate(templateNames, new ArrayList<String>(), targetFolder, monitor);
        if (result.size() > 1)
        {
            throw new GenerationException("More than one file has been generated after template execution.");
        }
        
        if (result.size() == 1)
        {
            return result.values().toArray(new String[0])[0];
        }
        else
        {
            throw new GenerationException("No file has been generated after template execution.");
        }
    }

    public Map<String, String> doGenerate(String templateName, List< ? extends Object> arguments, File generationRoot, Monitor monitor)
    {

        AcceleoService service = new AcceleoService(new PreviewStrategy());

        final Template template = findTemplate(templateName, arguments.size() + 1);
        // #findTemplate never returns private templates.

        final Map<String, String> previewResult = new HashMap<String, String>();

        // Calls the template with each potential arguments
        final EClassifier argumentType = template.getParameter().get(0).getType();
        // The input model itself is a potential argument
        if (argumentType.isInstance(model))
        {
            final List<Object> actualArguments = new ArrayList<Object>();
            actualArguments.add(model);
            actualArguments.addAll(arguments);
            previewResult.putAll(service.doGenerateTemplate(template, actualArguments, generationRoot, monitor));
        }

        service.dispose();

        return previewResult;
    }

    /**
     * This will iterate through the module's elements to find public templates named <tt>templateName</tt> with the
     * given count of arguments and return the first found.
     * 
     * @param templateName Name of the sought template.
     * @param argumentCount Number of arguments of the sought template.
     * @return The first public template of this name contained by <tt>module</tt>. Will fail in
     *         {@link AcceleoEvaluationException} if none can be found.
     */
    private Template findTemplate(String templateName, int argumentCount)
    {
        for (ModuleElement element : module.getOwnedModuleElement())
        {
            if (element instanceof Template)
            {
                Template template = (Template) element;
                if (template.getVisibility() == VisibilityKind.PUBLIC && templateName.equals(template.getName()) && template.getParameter().size() == argumentCount)
                {
                    return template;
                }
            }
        }
        throw new AcceleoEvaluationException(AcceleoEngineMessages.getString("AcceleoService.UndefinedTemplate", templateName, module.getName())); //$NON-NLS-1$
    }
    
    public ResourceSet getResourceSet()
    {
        return resourceSet;
    }

}
