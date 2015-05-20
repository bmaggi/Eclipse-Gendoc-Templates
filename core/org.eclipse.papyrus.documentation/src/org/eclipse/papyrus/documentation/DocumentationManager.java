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
 *   Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.documentation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.documentation.eannotation.EAnnotationDocumentationManager;


/**
 * Helper class to access and modify documentation independently from the meta-model.
 * It also provides an easy way to be notified of documentation change.
 * The default implementation {@link EAnnotationDocumentationManager} uses an EAnnotation if it is available.
 * 
 * @author mvelten
 * 
 */
public class DocumentationManager extends DocumentationManagerRegistry implements IDocumentationManager {

	private static class DocumentationManagerHolder {

		public static final DocumentationManager instance = new DocumentationManager();
	}

	public static DocumentationManager getInstance() {
		return DocumentationManagerHolder.instance;
	}

	private static Set<IDocumentationChangedListener> documentationChangedListeners = new HashSet<IDocumentationChangedListener>();

	/**
	 * {@inheritDoc}
	 */
	public String getDocumentation(EObject eObject) throws DocumentationUnsupportedException {
		if (eObject != null) {
			IDocumentationManager documentationManager = getDocumentationManager(eObject.eClass().getEPackage().getNsURI());
			if(documentationManager != null) {
				return documentationManager.getDocumentation(eObject);
			}
		}
		throw new DocumentationUnsupportedException(Messages.DocumentationManager_UnsupportedModelType);
	}

	/**
	 * {@inheritDoc}
	 */
	public Command getChangeDocumentationCommand(EObject eObject, String newDocumentation) {
		IDocumentationManager documentationManager = getDocumentationManager(eObject.eClass().getEPackage().getNsURI());
		if(documentationManager != null) {
			return documentationManager.getChangeDocumentationCommand(eObject, newDocumentation);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Command getAddAssociatedResourceCommand(EObject eObject, URI resourceURI) {
		IDocumentationManager documentationManager = getDocumentationManager(eObject.eClass().getEPackage().getNsURI());
		if(documentationManager != null) {
			return documentationManager.getAddAssociatedResourceCommand(eObject, resourceURI);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Command getRemoveAssociatedResourceCommand(EObject eObject, URI resourceURI) {
		IDocumentationManager documentationManager = getDocumentationManager(eObject.eClass().getEPackage().getNsURI());
		if(documentationManager != null) {
			return documentationManager.getRemoveAssociatedResourceCommand(eObject, resourceURI);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<URI> getAssociatedResources(EObject eObject) throws DocumentationUnsupportedException {
		IDocumentationManager documentationManager = getDocumentationManager(eObject.eClass().getEPackage().getNsURI());
		if(documentationManager != null) {
			return documentationManager.getAssociatedResources(eObject);
		} else {
			throw new DocumentationUnsupportedException(Messages.DocumentationManager_UnsupportedModelType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerDocumentationChangedListener(IDocumentationChangedListener listener) {
		documentationChangedListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterDocumentationChangedListener(IDocumentationChangedListener listener) {
		documentationChangedListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IDocumentationChangedListener> getRegisteredDocumentationChangedListeners() {
		return documentationChangedListeners;
	}

	public void documentationChanged(EObject eObject) {
		for(IDocumentationChangedListener listener : documentationChangedListeners) {
			listener.documentationChanged(eObject);
		}
	}

}
