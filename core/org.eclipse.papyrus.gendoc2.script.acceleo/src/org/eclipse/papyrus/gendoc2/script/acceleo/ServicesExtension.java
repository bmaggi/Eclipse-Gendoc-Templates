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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.acceleo.model.mtl.Module;
import org.eclipse.acceleo.model.mtl.MtlPackage;
import org.eclipse.acceleo.model.mtl.TypedModel;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.tags.handlers.IContextService;

/**
 * The Class ServicesExtension.
 */
public final class ServicesExtension
{

    /** The extension point. */
    private static final String EXTENSION_POINT_ID = "org.eclipse.papyrus.gendoc2.script.acceleo.services";

    /** The Constant EMTL. */
    private static final String EMTL = "emtl";

    /** The Constant NAME. */
    private static final String NAME = "name";

    /** The Constant IMPORTEDBYDEFAULT. */
    private static final String IMPORTEDBYDEFAULT = "importedByDefault";

    /** The instance. */
    private static ServicesExtension instance;

    /** The bundles. */
    private final List<Bundle> bundles;

    /**
     * Instantiates a new services extension.
     */
    private ServicesExtension()
    {
        bundles = new ArrayList<Bundle>();
        readExtensionPoint();
    }

    /**
     * Gets the single instance of ServicesExtension.
     * 
     * @return single instance of ServicesExtension
     */
    public static synchronized ServicesExtension getInstance()
    {
        if (instance == null)
        {
            instance = new ServicesExtension();
        }
        return instance;
    }

    /**
     * Read extension point.
     */
    private void readExtensionPoint()
    {
        Bundle bundle;
        final IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
        for (IConfigurationElement config : configElements)
        {
            bundle = new Bundle();
            try
            {
                String name = config.getAttribute(NAME);
                bundle.setName(name);
                bundle.setUri(findUri(config.getContributor().getName(), config.getAttribute(EMTL)));
                bundle.setImportedByDefault(Boolean.parseBoolean(config.getAttribute(IMPORTEDBYDEFAULT)));
                bundles.add(bundle);
            }
            catch (InvalidRegistryObjectException exc)
            {
                ILogger logger = GendocServices.getDefault().getService(ILogger.class);
                logger.log("Error while reading extension point " + EXTENSION_POINT_ID, IStatus.WARNING);
            }
        }
    }

    private URI findUri(String contributorId, String value)
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
        if (value == null || contributorId == null)
        {
            return null;
        }
        org.osgi.framework.Bundle bundle = Platform.getBundle(contributorId);
        URL resource = bundle.getResource(value);
        if (resource == null)
        {
            if (value.startsWith("src") || value.startsWith("bin"))
            {
                value = value.substring(3, value.length());
                resource = bundle.getResource(value);
                if (resource == null)
                {
                    logger.log("the emtl file referenced in the plugin " + contributorId + " with uri : " + value + " is not available", IStatus.ERROR);
                    return null;
                }
            }
        }
        return URI.createPlatformPluginURI(contributorId + "/" + value, true);
    }

    /**
     * Gets the services.
     * 
     * @return the services
     */
    public List<String> getServices()
    {
        List<String> bundleNames = new ArrayList<String>();
        for (Bundle bundle : bundles)
        {
            bundleNames.add(bundle.getName());
        }
        return bundleNames;
    }

    /**
     * Gets the bundles that need to be imported by default.
     * 
     * @return the services
     */
    public List<String> getBundlesImportedByDefault()
    {
        List<String> bundleNames = new ArrayList<String>();
        for (Bundle bundle : bundles)
        {
            if (bundle.isImportedByDefault())
            {
                bundleNames.add(bundle.getName());
            }
        }
        return bundleNames;
    }

    /**
     * Gets the declared bundles.
     * 
     * @return the declared bundles
     */
    private List<Bundle> getDeclaredBundles()
    {
        IContextService context = GendocServices.getDefault().getService(IContextService.class);
        List<String> declaredList = context.getImportedBundles();
        List<Bundle> result = new ArrayList<Bundle>(declaredList.size());
        for (Bundle b : bundles)
        {
            if (b.getName() != null && declaredList.contains(b.getName()))
            {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Gets the resources.
     * 
     * @return the resources
     */
    public List<URI> getResources()
    {
        List<URI> result = new ArrayList<URI>();
        for (Bundle b : getDeclaredBundles())
        {
            result.add(b.getUri());
        }
        return result;
    }

    /**
     * Gets the dependent metamodels.
     * 
     * @return the dependent metamodels
     */
    public List<String> getDependentMetamodels()
    {
        Set<String> result = new HashSet<String>();
        for (Bundle b : getDeclaredBundles())
        {
            for (String modelUri : b.getMetamodels())
            {
                result.add(modelUri);
            }
        }
        return new ArrayList<String>(result);
    }

    /**
     * Clears the resources map.
     */
    public void clear()
    {
        bundles.clear();
    }

    /**
     * Re-read the extension for the services.
     */
    public void resetResourceMap()
    {
        clear();
        readExtensionPoint();
    }

    /**
     * The Class Bundle.
     */
    private class Bundle
    {

        /** The name. */
        private String name;

        /** The uri. */
        private URI uri;

        /** The metamodels. */
        private List<String> metamodels;

        private boolean importedByDefault;

        /**
         * Instantiates a new bundle.
         */
        private Bundle()
        {
            name = "";
            uri = null;
            importedByDefault = false;
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * Sets the name.
         * 
         * @param name the new name
         */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
         * Gets the uri.
         * 
         * @return the uri
         */
        public URI getUri()
        {
            return uri;
        }

        /**
         * Sets the uri.
         * 
         * @param uri the new uri
         */
        public void setUri(URI uri)
        {
            this.uri = uri;
        }

        /**
         * Gets the metamodels.
         * 
         * @return the metamodels
         */
        public List<String> getMetamodels()
        {
            if (metamodels == null)
            {
                metamodels = new LinkedList<String>();
                ResourceSet set = new ResourceSetImpl();
                set.getPackageRegistry().put(MtlPackage.eNS_URI, MtlPackage.eINSTANCE);
                if (getUri() != null)
                {
                    Resource r = set.getResource(getUri(), true);
                    EcoreUtil.resolveAll(set);
                    if (r != null && !r.getContents().isEmpty() && r.getContents().get(0) instanceof Module)
                    {
                        Module m = (Module) r.getContents().get(0);
                        for (TypedModel t : m.getInput())
                        {
                            for (EPackage p : t.getTakesTypesFrom())
                            {
                                String uri = null;
                                if (p.eIsProxy())
                                {
                                    uri = ((InternalEObject) p).eProxyURI().trimFragment().toString();
                                }
                                else
                                {
                                    uri = p.getNsURI();
                                }
                                metamodels.add(uri);
                            }
                        }
                    }
                    for (Resource tmp : set.getResources())
                    {
                        try
                        {
                            tmp.unload();
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                else
                {
                    ((ILogger)GendocServices.getDefault().getService(ILogger.class)).log("uri not found for bundle " + getName(), IStatus.WARNING);
                }
            }
            return metamodels;
        }

        public boolean isImportedByDefault()
        {
            return importedByDefault;
        }

        public void setImportedByDefault(boolean importedByDefault)
        {
            this.importedByDefault = importedByDefault;
        }

    }

}
