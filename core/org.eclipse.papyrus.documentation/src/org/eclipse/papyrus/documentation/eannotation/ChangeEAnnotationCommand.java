/***********************************************************************
 * Copyright (c) 2008 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Jacques Lescot (Anyware Technologies) - initial API and implementation
 *    Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com
 **********************************************************************/
package org.eclipse.papyrus.documentation.eannotation;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.papyrus.documentation.IDocumentationChangedListener;
import org.eclipse.papyrus.documentation.Messages;

/**
 * A GEF Command use to update documentation EAnnotation when an EModelElement is selected.
 * 
 * @author <a href="mailto:jacques.lescot@anyware-tech.com">Jacques LESCOT</a>
 * @author <a href="mailto:sebastien.gabel@c-s.fr">Sebastien GABEL</a>
 * @author Mathieu Velten (Atos Origin) mathieu.velten@atosorigin.com
 */
public class ChangeEAnnotationCommand extends AbstractCommand {

	private EModelElement element;

	private String newValue;

	private String oldValue;
	
	private String source;
	
	private String key;
	
	private Set<IDocumentationChangedListener> documentationChangedListeners;

	/**
	 * Constructor
	 * 
	 * @param element
	 *        the EModelElement
	 * @param newValue
	 *        the new documentation as a String
	 */
	public ChangeEAnnotationCommand(EModelElement element, String source, String key, String newValue, Set<IDocumentationChangedListener> documentationChangedListeners) {
		super(Messages.ChangeDocCommandLabel);
		this.element = element;
		this.source = source;
		this.key = key;
		this.newValue = newValue;
		this.documentationChangedListeners = documentationChangedListeners;
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
		// stores the previous doc
		oldValue = null;
		EAnnotation annotation = element.getEAnnotation(source);
		if(annotation != null) {
			oldValue = annotation.getDetails().get(key);
		}

		redo();
	}

	/**
	 * {@inheritDoc}
	 */
	public void redo() {
		changeDocumentation(element, newValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		changeDocumentation(element, oldValue);
	}
	
	private void notifyListeners(String currentDoc) {
		for (IDocumentationChangedListener listener : documentationChangedListeners) {
			listener.documentationChanged(element);
		}
	}

	/**
	 * Set the documentation for the given Model Element
	 * 
	 * @param elt
	 *        the element to document
	 * @param newDoc
	 *        the documentation text
	 */
	protected IStatus changeDocumentation(EModelElement elt, String newDoc) {
		EAnnotation annotation = elt.getEAnnotation(source);
		if(newDoc != null && !"".equals(newDoc)) { //$NON-NLS-1$
			// creates EAnnotation if needed
			if(annotation == null) {
				annotation = EcoreFactory.eINSTANCE.createEAnnotation();
				annotation.setSource(source);
				elt.getEAnnotations().add(annotation);
			}

			annotation.getDetails().put(key, newDoc);
		} else {
			// remove the documentation
			if(annotation != null) {
				annotation.getDetails().remove(key);

				// remove the EAnnotation if empty
				if(annotation.getDetails().isEmpty()) {
					elt.getEAnnotations().remove(annotation);
				}
			}
		}
		notifyListeners(newDoc);
		
		return Status.OK_STATUS;
	}

}
