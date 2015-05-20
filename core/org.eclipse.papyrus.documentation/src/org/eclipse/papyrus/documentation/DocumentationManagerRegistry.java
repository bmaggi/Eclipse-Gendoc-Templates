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
 *  Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.documentation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrus.documentation.eannotation.EAnnotationDocumentationManager;

public abstract class DocumentationManagerRegistry implements IDocumentationChangedListener {
	
	// extension point ID
	private static final String MODELDOCUMENTATION_EXTENSION_ID = "org.eclipse.papyrus.documentation.documentationManager"; //$NON-NLS-1$

	private static final String MODELNSURI_ID = "modelNsURI"; //$NON-NLS-1$

	private static final String PRIORITY_ID = "priority"; //$NON-NLS-1$

	private static final String MODELDOCUMENTATIONCLASS_ID = "managerClass"; //$NON-NLS-1$

	// Lowest is default
	private static final Integer DEFAULT_PRIORITY = 0;


	private Map<String, PriorityQueue<IDocumentationManager>> nsURIDocumentationManagerMap = new HashMap<String, PriorityQueue<IDocumentationManager>>();

	private Map<IDocumentationManager, Integer> documentationManagerPriorityMap = new HashMap<IDocumentationManager, Integer>();

	private IDocumentationManager eAnnotationDocumentationManager;

	public DocumentationManagerRegistry() {
		eAnnotationDocumentationManager = EAnnotationDocumentationManager.getInstance();
		eAnnotationDocumentationManager.registerDocumentationChangedListener(this);
		initializeMap();
	}

	protected IDocumentationManager getDocumentationManager(String nsURI) {
		IDocumentationManager documentationManager = null;

		PriorityQueue<IDocumentationManager> documentationManagers = nsURIDocumentationManagerMap.get(nsURI);
		if(documentationManagers != null) {
			documentationManager = documentationManagers.peek();
		}

		if(documentationManager == null) {
			return eAnnotationDocumentationManager;
		}

		return documentationManager;
	}

	private void initializeMap() {
		// Reading data from plugins
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(MODELDOCUMENTATION_EXTENSION_ID);
		for(int i = 0; i < configElements.length; i++) {
			initializeOne(configElements[i]);
		}
	}

	private void initializeOne(IConfigurationElement iConfigurationElement) {
		try {
			String modelNsURI = iConfigurationElement.getAttribute(MODELNSURI_ID);

			IDocumentationManager documentationManager = (IDocumentationManager)createExtension(iConfigurationElement, MODELDOCUMENTATIONCLASS_ID);
			documentationManager.registerDocumentationChangedListener(this);

			String priority = iConfigurationElement.getAttribute(PRIORITY_ID);
			documentationManagerPriorityMap.put(documentationManager, convertPriorityToInteger(priority));

			PriorityQueue<IDocumentationManager> documentationManagers = nsURIDocumentationManagerMap.get(modelNsURI);
			if(documentationManagers == null) {
				documentationManagers = new PriorityQueue<IDocumentationManager>(10, new Comparator<IDocumentationManager>() {

					public int compare(IDocumentationManager o1, IDocumentationManager o2) {
						Integer p1 = documentationManagerPriorityMap.get(o1);
						if(p1 == null) {
							p1 = DEFAULT_PRIORITY;
						}
						Integer p2 = documentationManagerPriorityMap.get(o2);
						if(p2 == null) {
							p2 = DEFAULT_PRIORITY;
						}
						return p2 - p1;
					}
				});

				nsURIDocumentationManagerMap.put(modelNsURI, documentationManagers);
			}
			documentationManagers.add(documentationManager);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Integer convertPriorityToInteger(String priorityString) {
		if(priorityString == null) {
			return DEFAULT_PRIORITY;
		}
		if(priorityString.equalsIgnoreCase("Lowest")) { //$NON-NLS-1$
			return 0;
		}
		if(priorityString.equalsIgnoreCase("Low")) { //$NON-NLS-1$
			return 1;
		}
		if(priorityString.equalsIgnoreCase("Medium")) { //$NON-NLS-1$
			return 2;
		}
		if(priorityString.equalsIgnoreCase("High")) { //$NON-NLS-1$
			return 3;
		}
		if(priorityString.equalsIgnoreCase("Highest")) { //$NON-NLS-1$
			return 4;
		}
		return DEFAULT_PRIORITY;
	}

	/**
	 * Load an instance of a class
	 * 
	 * @param element
	 *        the extension point
	 * @param classAttribute
	 *        the name of the class to load
	 * @return the loaded Class
	 * @throws Exception
	 *         if the class is not loaded
	 */
	private static Object createExtension(final IConfigurationElement element, final String classAttribute) throws Exception {
		return element.createExecutableExtension(classAttribute);
	}

}
