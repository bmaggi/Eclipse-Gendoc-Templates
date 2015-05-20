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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.papyrus.gendoc2.m2t.IM2TProcessor;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IProgressMonitorService;
import org.eclipse.papyrus.gendoc2.services.exception.ElementNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.GenerationException;
import org.eclipse.papyrus.gendoc2.services.exception.ModelNotFoundException;
import org.eclipse.papyrus.gendoc2.services.exception.ParsingException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IContextService;

/**
 * The Class AcceleoM2TProcess.
 * 
 * @author cbourdeu
 */
public class AcceleoM2TProcess implements IM2TProcessor
{

    private Map<URI, Collection<String>> file2MetaModels;

    /** 
	 * 
	 */
    public AcceleoM2TProcess()
    {
        file2MetaModels = new LinkedHashMap<URI, Collection<String>>();
    }

    public void clear()
    {
        file2MetaModels.clear();
    }

    /**
     * 
     * @param eObject
     * @return
     */
    private Resource getModelRootElement(EObject eObject)
    {
        EObject current = eObject;
        while (current.eContainer() != null)
        {
            current = current.eContainer();
        }
        return current.eResource();
    }

    /**
     * Get all metamodels for the model associated to the eObject
     * 
     * @param eObject
     * @return
     */
    @SuppressWarnings("unchecked")
	private Collection<String> getMetamodels(EObject eObject)
    {
        URI fileURI = eObject.eResource().getURI();
        if (file2MetaModels.get(fileURI) == null)
        {
            Resource modelRoot = getModelRootElement(eObject);
            TreeIterator<EObject> allModelObjects = modelRoot.getAllContents();
            String metamodel;
            EObject currentObject;
            Stack<URI> modelFiles = new Stack<URI>();
            while (allModelObjects.hasNext())
            {
                currentObject = allModelObjects.next();
                EPackage epackage = currentObject.eClass().getEPackage();
                if (EPackage.Registry.INSTANCE.containsKey(epackage.getNsURI()))
                {
                	metamodel = epackage.getNsURI();
                	URI file = currentObject.eResource().getURI();
                	if (modelFiles.isEmpty())
                	{
                		modelFiles.push(file);
                	}
                	else if (modelFiles.peek() != null && !modelFiles.peek().equals(file))
                	{
                		if (currentObject.eContainer() != null)
                		{
                			URI parentURI = currentObject.eContainer().eResource().getURI();
                			if (parentURI.equals(modelFiles.peek()))
                			{
                				// If first element on a new file, add file to stack
                				modelFiles.push(file);
                			}
                			else
                			{
                				// Else, pop all elements until parentFile is found.
                				while (!modelFiles.isEmpty() && parentURI != modelFiles.peek())
                				{
                					modelFiles.pop();
                				}
                			}
                			
                		}
                	}
                	addMetaModelForFiles(metamodel, modelFiles);
                }
            }
        }
        Collection<String> collection = file2MetaModels.get(fileURI);
        return collection == null ? Collections.EMPTY_LIST : collection;
    }

    /**
     * Associate a meta model to a file
     * 
     * @param metamodel the metamodel URI
     * @param file file URI
     */
    private void addMetaModelForFiles(String metamodel, List<URI> files)
    {
        for (URI file : files)
        {
            if (file2MetaModels.get(file) == null)
            {
                file2MetaModels.put(file, new LinkedHashSet<String>());
            }
            file2MetaModels.get(file).add(metamodel);

        }
    }

    /**
     * Execute the given script and return the generated text.
     * 
     * @param script to execute as a String
     * @param context the context
     * 
     * @return the generated text as a String
     * 
     * @throws ElementNotFoundException the element not found exception
     * @throws ParsingException the parsing exception
     * @throws ModelNotFoundException the model not found exception
     * @throws GenerationException Other exception thrown during generation
     */
    public String runScript(String script, EObject element) throws ModelNotFoundException, ElementNotFoundException, ParsingException, GenerationException
    {
        String result = "";
        String formattedScript = "";
        try
        {
            formattedScript = formatScript(element, script);
            Generator generator = new Generator(element, formattedScript);
            IProgressMonitorService progressMonitor = GendocServices.getDefault().getService(IProgressMonitorService.class);
            result = generator.doGenerate(progressMonitor.getDelegatingMonitor());
            // Unload
            unload(generator.getResourceSet());
        }
        catch (IOException e)
        {
            throw new GenerationException("Error during generation of the following script : \n" + script, e);
        }
        catch (Exception e)
        {
            if (e instanceof IndexOutOfBoundsException && "index: 0, size: 1".equals(e.getMessage()))
            {
                throw new GenerationException("Error while trying to access index 0 of list.\n TIP : In Acceleo, index start at 1 and not 0.");
            }
            throw new GenerationException("Error during generation of Gendoc script :" + e.getMessage(), e);
        }

        return result;
    }

    private void unload(ResourceSet resourceSet)
    {
        for (Resource r : resourceSet.getResources())
        {
            try
            {
                r.unload();
            }
            catch (Exception e)
            {
                // Sometimes the unload throw an exception 
            }
        }

    }

    private String formatScript(EObject element, String script)
    {

        IContextService contextService = GendocServices.getDefault().getService(IContextService.class);

        StringBuilder resultScript = new StringBuilder();
        List<String> metaModels = ServicesExtension.getInstance().getDependentMetamodels();
        resultScript.append("[module template('");

        if (contextService.isSearchMetamodels())
        {
            // Add metamodels associated to the file containing the element
            Collection<String> metamodelsFromElement = getMetamodels(element);
            for (String metaModel : metamodelsFromElement)
            {
                if (!metaModels.contains(metaModel))
                {
                    metaModels.add(metaModel);
                }
            }
        }
        else
        {
            metaModels.add(element.eClass().getEPackage().getNsURI());
        }

        if (metaModels.isEmpty())
        {
            resultScript.append(element.eClass().getEPackage().getNsURI() + "')/]\n");
        }
        else
        {
            Iterator<String> metaModelsIt = metaModels.iterator();
            resultScript.append(metaModelsIt.next());
            while (metaModelsIt.hasNext())
            {
                resultScript.append("', '" + metaModelsIt.next());
            }
            resultScript.append("')/]\n");
        }

        final Pattern pattern = getQueryPattern();
        List<String> queries = extractQueries(script, pattern);
        String scriptWithoutQueries = cleanFromQueries(script, pattern);

        for (String bundle : getImportedBundlesForScript())
        {
            resultScript.append("[import " + bundle + "/]\n");
        }
        for (String query : queries)
        {
            resultScript.append(query + "\n");
        }
        resultScript.append("[template public template(internalVar : " + element.eClass().getName() + ")]\n");
        resultScript.append("[file ('tempFile',false )]\n");
        resultScript.append(cleanInternalScriptContent(scriptWithoutQueries) + "\n");
        resultScript.append("[/file] \n");
        resultScript.append("[/template]\n");
        return resultScript.toString();
    }

    private String cleanInternalScriptContent(String script)
    {
        StringBuffer scriptContent = new StringBuffer();
        Pattern pattern = getScriptPatterns().get(0);
        Matcher matcher = pattern.matcher(script);
        int start;
        int end;
        int index = 0;
        while (matcher.find())
        {
            start = matcher.start();
            end = matcher.end();
            scriptContent.append(script.substring(index, start));
            String content = script.substring(start, end);
            if (content.contains("&lt;") || content.contains("&gt;"))
            {
                content = content.replaceAll("&lt;", "<");
                content = content.replaceAll("&gt;", ">");
            }
            scriptContent.append(content);
            index = end;
        }
        scriptContent.append(script.substring(index));
        return scriptContent.toString();
    }

    private String cleanFromQueries(String script, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(script);
        return matcher.replaceAll("");
    }

    private Pattern getQueryPattern()
    {
        String regex = Pattern.quote("[") + "query[^]]*" + Pattern.quote("/]");
        return Pattern.compile(regex);
    }

    private List<String> extractQueries(String script, Pattern pattern)
    {
        List<String> queries = new ArrayList<String>();

        Matcher matcher = pattern.matcher(script);
        while (!matcher.hitEnd())
        {
            if (matcher.find())
            {
                String next = matcher.group();
                queries.add(next);
            }
        }
        return queries;
    }

    /**
     * Add all declared importedBundles AND all bundles that are defined as 'importedByDefault'
     * 
     * @return
     */
    private List<String> getImportedBundlesForScript()
    {
        IContextService context = GendocServices.getDefault().getService(IContextService.class);
        List<String> importedBundles = context.getImportedBundles();
        for (String bundleName : ServicesExtension.getInstance().getBundlesImportedByDefault())
        {
            if (!importedBundles.contains(bundleName))
            {
                importedBundles.add(bundleName);
            }
        }
        return importedBundles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.handlers.IM2TProcess#getAllAvailableBundles()
     */
    public List<String> getAllAvailableBundles()
    {
        return ServicesExtension.getInstance().getServices();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.handlers.IM2TProcess#getScriptPatterns()
     */
    public List<Pattern> getScriptPatterns()
    {
        // A pattern for elements matching : [XXXX] or [/XXXX] or [XXXX/]
        Pattern braquets = Pattern.compile("(\\[[^\\[\\]]*\\])|(\\[/[^\\[\\]]*\\])");
        List<Pattern> patterns = new ArrayList<Pattern>(1);
        patterns.add(braquets);
        return patterns;
    }

}
