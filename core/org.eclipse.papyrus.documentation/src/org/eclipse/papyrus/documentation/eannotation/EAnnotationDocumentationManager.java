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
package org.eclipse.papyrus.documentation.eannotation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.documentation.DocumentationUnsupportedException;
import org.eclipse.papyrus.documentation.IDocumentationChangedListener;
import org.eclipse.papyrus.documentation.IDocumentationManager;
import org.eclipse.papyrus.documentation.Messages;


public class EAnnotationDocumentationManager implements IDocumentationManager {
	
	private static class EAnnotationDocumentationManagerHolder {

		public static final EAnnotationDocumentationManager instance = new EAnnotationDocumentationManager();
	}

	public static EAnnotationDocumentationManager getInstance() {
		return EAnnotationDocumentationManagerHolder.instance;
	}

	private static Set<IDocumentationChangedListener> documentationChangedListeners = new HashSet<IDocumentationChangedListener>();

	/**
	 * {@inheritDoc}
	 */
	public Command getChangeDocumentationCommand(EObject eObject, String newDocumentation) {
		if(getUnsupportedErrorMessage(eObject) == null) {
			return new ChangeEAnnotationCommand((EModelElement)eObject, IEAnnotationConstants.DOCUMENTATION_SOURCE, IEAnnotationConstants.DOCUMENTATION_KEY, newDocumentation, documentationChangedListeners);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDocumentation(EObject eObject) throws DocumentationUnsupportedException {
		String errorMsg = getUnsupportedErrorMessage(eObject);
		if(errorMsg == null) {
			EAnnotation annotation = ((EModelElement)eObject).getEAnnotation(IEAnnotationConstants.DOCUMENTATION_SOURCE);
			if(annotation != null) {
				return annotation.getDetails().get(IEAnnotationConstants.DOCUMENTATION_KEY);
			}
			return null;
		} else {
			throw new DocumentationUnsupportedException(errorMsg);
		}
	}

	public Command getAddAssociatedResourceCommand(EObject eObject, URI resourceURI) {
		if(getUnsupportedErrorMessage(eObject) == null) {
			return new AddOrRemoveAssociatedResourceCommand(false, (EModelElement)eObject, resourceURI, documentationChangedListeners);
		} else {
			return null;
		}
	}

	public Command getRemoveAssociatedResourceCommand(EObject eObject, URI resourceURI) {
		if(getUnsupportedErrorMessage(eObject) == null) {
			return new AddOrRemoveAssociatedResourceCommand(true, (EModelElement)eObject, resourceURI, documentationChangedListeners);
		} else {
			return null;
		}
	}

	public List<URI> getAssociatedResources(EObject eObject) throws DocumentationUnsupportedException {
		String errorMsg = getUnsupportedErrorMessage(eObject);
		if(errorMsg == null) {
			EAnnotation annotation = ((EModelElement)eObject).getEAnnotation(IEAnnotationConstants.ASSOCIATED_RESOURCES_SOURCE);
			if(annotation != null) {
				return convertDetailsToURIs(annotation.getDetails());
			}
			return new LinkedList<URI>();
		} else {
			throw new DocumentationUnsupportedException(errorMsg);
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

	private static List<URI> convertDetailsToURIs(EMap<String, String> details) {
		List<URI> uris = new LinkedList<URI>();
		for(Entry<String, String> detail : details) {
			String value = detail.getValue();

			String prefix = detail.getKey().substring(0, 2);
			URI uri = null;
			if(IEAnnotationConstants.PREFIX_REMOTE_RESOURCE.equals(prefix)) {
				uri = URI.createURI(value, false);
			} else if(IEAnnotationConstants.PREFIX_EXTERNAL_RESOURCE.equals(prefix)) {
				uri = URI.createFileURI(value);
			} else if(IEAnnotationConstants.PREFIX_WORKSPACE_RESOURCE.equals(prefix)) {
				uri = URI.createPlatformResourceURI(value, true);
			}
			uris.add(uri);
		}
		return uris;
	}
	
	/**
	 * get an error message if eObject is unsupported by this implementation
	 * 
	 * @param eObject
	 * @return null if it is ok or an error message if not
	 */
	private static String getUnsupportedErrorMessage(EObject eObject) {
		// forbid doc on doc eannotation
		if(eObject instanceof EAnnotation && ((EAnnotation)eObject).getSource().equals(IEAnnotationConstants.DOCUMENTATION_SOURCE)) {
			return Messages.EAnnotationDocumentationManager_DocOnDocEAnnotationError;
			// Check whether the element is an EModelElement
		} else if(eObject instanceof EModelElement) {
			// OK, return null
			return null;
		} else {
			return Messages.EAnnotationDocumentationManager_NonEModelElementError;
		}
	}
}
