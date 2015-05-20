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
package org.eclipse.papyrus.documentation.eannotation;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.papyrus.documentation.IDocumentationChangedListener;
import org.eclipse.papyrus.documentation.Messages;


public class AddOrRemoveAssociatedResourceCommand extends AbstractCommand {

	private boolean remove;

	private EModelElement element;

	private Set<IDocumentationChangedListener> documentationChangedListeners;

	private String prefix;

	private String value;

	public AddOrRemoveAssociatedResourceCommand(boolean remove, EModelElement element, URI resourceURI, Set<IDocumentationChangedListener> documentationChangedListeners) {
		super(Messages.AddOrRemoveAssociatedResourceCommandLabel);
		this.remove = remove;
		this.element = element;
		this.documentationChangedListeners = documentationChangedListeners;

		if(resourceURI.isPlatform()) {
			prefix = IEAnnotationConstants.PREFIX_WORKSPACE_RESOURCE;
			value = resourceURI.toPlatformString(true);
		} else if(resourceURI.isFile()) {
			prefix = IEAnnotationConstants.PREFIX_EXTERNAL_RESOURCE;
			value = resourceURI.toFileString();
		} else {
			prefix = IEAnnotationConstants.PREFIX_REMOTE_RESOURCE;
			value = resourceURI.toString();
		}
	}

	private void notifyListeners() {
		for(IDocumentationChangedListener listener : documentationChangedListeners) {
			listener.documentationChanged(element);
		}
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		redo();
	}

	/**
	 * {@inheritDoc}
	 */
	public void redo() {
		if(remove) {
			removeAssociatedResource();
		} else {
			addAssociatedResource();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		if(remove) {
			addAssociatedResource();
		} else {
			removeAssociatedResource();
		}
	}

	private void addAssociatedResource() {
		EAnnotation annotation = element.getEAnnotation(IEAnnotationConstants.ASSOCIATED_RESOURCES_SOURCE);
		if(annotation == null) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(IEAnnotationConstants.ASSOCIATED_RESOURCES_SOURCE);
			element.getEAnnotations().add(annotation);
		}
		if(prefix != null && value != null) {
			annotation.getDetails().put(getNewResourceKey(annotation, prefix, 0), value);
			notifyListeners();
		}
	}

	private void removeAssociatedResource() {
		EAnnotation annotation = element.getEAnnotation(IEAnnotationConstants.ASSOCIATED_RESOURCES_SOURCE);
		if(annotation != null && prefix != null && value != null) {
			EMap<String, String> details = annotation.getDetails();
			for(Entry<String, String> detail : details) {
				if(detail.getKey() != null && detail.getKey().startsWith(prefix) && value.equals(detail.getValue())) {
					details.remove(detail);
					break;
				}
			}
			if (details.isEmpty()) {
				element.getEAnnotations().remove(annotation);
			}
			notifyListeners();
		}
	}

	/**
	 * Recursively compute a new resource key using its prefix, a starting index and a map of resources to create. It
	 * also look in the existing resources to find a free index.
	 * 
	 * @param resourcePrefix
	 *        the resource prefix to use
	 * @param startIndex
	 *        the start index (ie : 0)
	 * @return a new resource key
	 */
	private String getNewResourceKey(EAnnotation annotation, String resourcePrefix, int startIndex) {
		String key = resourcePrefix + startIndex;
		if(annotation != null && annotation.getDetails().get(key) == null) {
			return key;
		}

		return getNewResourceKey(annotation, resourcePrefix, startIndex + 1);
	}
}
