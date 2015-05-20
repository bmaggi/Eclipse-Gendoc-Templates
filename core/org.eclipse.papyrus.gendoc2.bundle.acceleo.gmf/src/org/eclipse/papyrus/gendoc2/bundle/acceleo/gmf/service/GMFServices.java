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
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.bundle.acceleo.gmf.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.gendoc2.bundle.acceleo.gmf.Activator;
import org.eclipse.papyrus.gendoc2.bundle.acceleo.gmf.impl.GMFDiagramRunnable;
import org.eclipse.papyrus.gendoc2.documents.IAdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;

public class GMFServices {
	private static final String KEY_DIAGRAM_MAP = "key";

	/**
	 * Gets the information whether the diagram is empty or not
	 * 
	 * @param diagram
	 *            diagram to check
	 * @return true if the diagram is empty
	 */
	public Boolean isDiagramEmpty(Diagram diagram) {
		return getElementsInDiagram(diagram).isEmpty();
	}

	/**
	 * Gets the diagram.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagram
	 */
    public String getDiagram(Diagram diagram)
    {
       return getDiagramExt(diagram, "png");
    }

    
    public String getDiagramExt(Diagram diagram, String ext)
    {
        if (diagram != null)
        {
            GMFDiagramRunnable runnable = new GMFDiagramRunnable(diagram,ext);
            IDocumentService docService = GendocServices.getDefault().getService(IDocumentService.class);
            IAdditionalResourceService resourceService = docService.getAdditionalResourceService();
            return resourceService.addNewImageRunnable(runnable);
        }
        else
        {
            return null;
        }
     }
	/**
	 * Get the diagrams containing in a .notation resource
	 * 
	 * @param e
	 *            the eobject
	 * @return the diagram list
	 */
	public List<Diagram> getDiagramsUsingNotation(EObject e) {
		return getDiagramsUsingExtension(e, "notation");
	}

	/**
	 * Replace the e resource extension with the extension. Return diagrams in
	 * the new resource
	 * 
	 * @param e
	 *            the eobject
	 * @param extension
	 *            the extension to use
	 * @return the diagram list
	 */
	public List<Diagram> getDiagramsUsingExtension(EObject e, String extension) {
		URI uri = e.eResource().getURI();
		URI newURI = uri.trimFileExtension().appendFileExtension(extension);
		return getDiagramsInResource(e, newURI);
	}

	/**
	 * Get the diagrams containing in the relativePath
	 * 
	 * @param e
	 *            the eobject
	 * @param relativePath
	 *            the relative path
	 * @return the diagram list
	 */
	public List<Diagram> getDiagramsUsingRelativePath(EObject e, String relativePath) {
		if (e == null || e.eResource() == null || relativePath == null) {
			return null;
		}
		URI uri = URI.createURI(e.eResource().getURI().trimSegments(1)
				.toString()
				+ "/");
		URI r = URI.createURI(relativePath).resolve(uri);
		return getDiagramsInResource(e, r);
	}

	/**
	 * Get the diagram containing in the absolutePath
	 * 
	 * @param e
	 *            the eobject
	 * @param absolutePath
	 *            the absolute path
	 * @return the diagram list
	 */
	public List<Diagram> getDiagramsUsingAbsolutePath(EObject e, String absolutePath) {
		if (e == null || e.eResource() == null || absolutePath == null) {
			return null;
		}
		return getDiagramsInResource(e, URI.createFileURI(absolutePath));
	}

	/**
	 * Get the diagram containing in the uri resource
	 * 
	 * @param e
	 *            the eobject
	 * @param uri
	 *            the uri
	 * @return the diagram list
	 */
	private List<Diagram> getDiagramsInResource(EObject e, URI uri) {
		return getDiagrams(e, uri);
	}

	/**
	 * Get the diagram containing in the model (resource of e)
	 * 
	 * @param e
	 *            the eobject
	 * @return the diagram list
	 */
	public List<Diagram> getDiagramInModel(EObject e) {
		if (e == null || e.eResource() == null) {
			return null;
		}
		return getDiagramsInResource(e, e.eResource().getURI());
	}

	/**
	 * Get all diagrams containing in the uri resource for the object e
	 * 
	 * @param e
	 *            the eobject
	 * @param uri
	 *            the uri
	 * @return list of diagrams
	 */
	public List<Diagram> getDiagrams(EObject e, URI uri) {
		if (e == null || e.eResource() == null || uri == null) {
			return null;
		}
		IRegistryService registry = GendocServices.getDefault().getService(
				IRegistryService.class);
		if (registry != null) {
			Map<URI, List<Diagram>> map = (Map<URI, List<Diagram>>) registry
					.get(KEY_DIAGRAM_MAP);
			if (map == null) {
				// instanciation -> put
				map = new HashMap<URI, List<Diagram>>();
				registry.put(KEY_DIAGRAM_MAP, map);
			}
			List<Diagram> diagrams = map.get(uri);
			if (diagrams == null) {
				// new LinkedList<Diagram> / recherche / add -> put
				diagrams = new LinkedList<Diagram>();
				Resource diResource = null;
				if (e.eResource().getURI().equals(uri)) {
					diResource = e.eResource();
				} else {
					try {
						diResource = e.eResource().getResourceSet()
								.getResource(uri, true);
					} catch (WrappedException ex) {
						IGendocDiagnostician diag = GendocServices.getDefault()
								.getService(IGendocDiagnostician.class);
						diag.addDiagnostic(new BasicDiagnostic(
								Diagnostic.ERROR, Activator.PLUGIN_ID, 0,
								String.format("Resource %s not found",
										uri.toString()), new Object[] { e }));
						ex.printStackTrace();
					}

				}
				EditingDomain domain = TransactionUtil
						.getEditingDomain(diResource.getResourceSet());
				if (domain == null) {
					TransactionalEditingDomainImpl.FactoryImpl.INSTANCE
							.createEditingDomain(diResource.getResourceSet());
				}
				for (Iterator<EObject> i = EcoreUtil.getAllProperContents(
						diResource, false); i.hasNext();) {
					EObject eobject = i.next();
					if (eobject instanceof Diagram) {
						Diagram diagram = (Diagram) eobject;
						diagrams.add(diagram);
					}
				}
				map.put(uri, diagrams);
			}
			if (!diagrams.isEmpty()) {
				return getDiagramsForElement(e, diagrams);
			}
			return null;
		}
		return null;
	}

	protected List<Diagram> getDiagramsForElement(EObject e,
			List<Diagram> diagrams) {
		List<Diagram> diagramsForParameters = new LinkedList<Diagram>();
		for (Diagram d : diagrams) {
			if (d.getElement() != null && d.getElement().equals(e)) {
				diagramsForParameters.add(d);
			}
		}
		return diagramsForParameters;
	}

	/**
	 * Gets the elements in diagram.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the elements in diagram
	 */
	public Collection<EObject> getElementsInDiagram(Diagram diagram) {
		Set<View> allViews = new HashSet<View>();
		getAllNestedViews(diagram, allViews);
		Set<EObject> elements = new HashSet<EObject>();
		for (View view : allViews) {
			EObject elt = view.getElement();
			if (elt != null) {
				elements.add(elt);
			}
		}
		return elements;
	}

	/**
	 * Recursively get all nested elements
	 * 
	 * @param view
	 *            the view
	 * @param allViews
	 *            the list of elements
	 */
	static private void getAllNestedViews(View view, Set<View> allViews) {
		for (View childView : (List<View>) view.getChildren()) {
			getAllNestedViews(childView, allViews);
			allViews.add(childView);
		}
	}
		
}
