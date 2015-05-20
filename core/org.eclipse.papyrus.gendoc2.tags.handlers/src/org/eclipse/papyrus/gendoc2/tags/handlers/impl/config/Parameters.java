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
 *  Tristan FAURE (Atos Origin) tristan.faure@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.handlers.impl.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * This class manages parameters given by user and parameters subscribed by extension point
 * @author tfaure
 *
 */
public class Parameters {

	private static String extensionPointId = "org.eclipse.papyrus.gendoc2.tags.handlers.parameters"; 
	private Map<String, String> map = new HashMap<String, String>();

	
	public Parameters ()
	{
		// fill the map with dynamic parameters
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPointId);
		for (IConfigurationElement e : elements)
		{
			IParameterValue value = null;
			try {
				value = (IParameterValue) e.createExecutableExtension("instance");
			} catch (CoreException e1) {
			}
			if (value != null)
			{
				Map<String, String> tmp = value.getValue();
				map.putAll(tmp);
			}
		}
	}
	
	public void clear() {
		map.clear();
	}

	public String get(String lowerCase) {
		String tmp = map.get(lowerCase);
		return tmp != null ? tmp : "" ;
	}

	public void put(String key, String value) {
		map.put(key,value);
	}
	

}
