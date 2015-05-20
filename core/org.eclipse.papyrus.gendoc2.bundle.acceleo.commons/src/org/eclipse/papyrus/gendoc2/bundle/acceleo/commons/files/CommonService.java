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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.bundle.acceleo.commons.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;

public class CommonService
{
    private static final Pattern newLinePattern = Pattern.compile("\\r\\n|\\n|\\r");

    /**
     * Replace special characters inside the given String <, >, &, ', " and replace them by corresponding XML codes.
     * 
     * @param string The string to modify
     * 
     * @return the string without special characters
     */
    public static String removeSpecialCharacters(String string)
    {
        if (string == null)
        {
            return null;
        }
        return string.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
    }

    /**
     * Gets a tab where string is separated on the new lines
     * 
     * @param string
     * @return the tab
     */
    public static List<String> splitNewLine(String string)
    {
        if (string == null)
        {
            return null;
        }
        return Arrays.asList(newLinePattern.split(string));
    }

    /**
     * Gendoc2 put Put a variable available along the whole gendoc2 process
     * 
     * @param key
     * @param value
     * @return
     */
    public static String gPut(Object key, Object value)
    {
        IRegistryService registry = GendocServices.getDefault().getService(IRegistryService.class);
        if (registry != null)
        {
            registry.put(key, value);
        }
        return "";
    }

    /**
     * Gendoc2 get Get a variable available along the whole gendoc2 process
     * 
     * @param key
     * @return
     */
    public static Object gGet(Object key)
    {
        IRegistryService registry = GendocServices.getDefault().getService(IRegistryService.class);
        if (registry != null)
        {
            return registry.get(key);
        }
        return null;
    }

    /**
     * Returns a generic String for the given Eobject
     * 
     * @param e, the eobject
     * @return the text
     */
    public static String getText(EObject e)
    {
        if (e == null)
        {
            return "";
        }
        AdapterFactory factory = null;
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(e.eClass().getEPackage());
        list.add(IStructuredItemContentProvider.class);
        Descriptor desc = ComposedAdapterFactory.Descriptor.Registry.INSTANCE.getDescriptor(list);
        if (desc != null)
        {
            factory = desc.createAdapterFactory();
        }
        if (factory == null)
        {
            factory = new ReflectiveItemProviderAdapterFactory();
        }
        return new ReflectiveItemProvider(factory).getText(e);
    }
}
