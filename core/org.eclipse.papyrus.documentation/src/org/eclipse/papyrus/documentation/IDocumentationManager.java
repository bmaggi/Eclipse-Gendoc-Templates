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

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;


/**
 * Interface used to provide a way to get or change the documentation of an element.
 * 
 * @author mvelten
 * 
 */
public interface IDocumentationManager {

	/**
	 * Returns an EMF Command to change the documentation.
	 * 
	 * @param eObject
	 *        The element where to change the documentation.
	 * @param newDocumentation
	 *        The new value of the documentation or null to remove it.
	 * @return the command to execute or null if this element can not host a documentation.
	 */
	public Command getChangeDocumentationCommand(EObject eObject, String newDocumentation);

	/**
	 * Returns an EMF Command to add an associated resource to the element.
	 * 
	 * @param eObject
	 *        The element where to add the associated resource.
	 * @param resourceURI
	 *        the URI of the resource.
	 * @return the command to execute or null if this element can not host a documentation.
	 */
	public Command getAddAssociatedResourceCommand(EObject eObject, URI resourceURI);

	/**
	 * Returns an EMF Command to remove an associated resource from the element.
	 * 
	 * @param eObject
	 *        The element where to remove the associated resource.
	 * @param resourceURI
	 *        the URI of the resource.
	 * @return the command to execute or null if this element can not host a documentation.
	 */
	public Command getRemoveAssociatedResourceCommand(EObject eObject, URI resourceURI);

	/**
	 * Returns the documentation of an element.
	 * 
	 * @param eObject
	 *        The element where to look for the documentation.
	 * @return The documentation or null if there is no documentation.
	 * @throws DocumentationUnsupportedException
	 *         if documentation is not supported on this element.
	 */
	public String getDocumentation(EObject eObject) throws DocumentationUnsupportedException;

	/**
	 * Returns the associated resources of the element.
	 * 
	 * @param eObject
	 *        The element where to look for the associated resources.
	 * @return a list of resource URIs.
	 * @throws DocumentationUnsupportedException
	 *         if documentation is not supported on this element.
	 */
	public List<URI> getAssociatedResources(EObject eObject) throws DocumentationUnsupportedException;

	/**
	 * Register a IDocumentationChangedListener (see {@link IDocumentationChangedListener}).
	 * 
	 * @param listener
	 */
	public void registerDocumentationChangedListener(IDocumentationChangedListener listener);

	/**
	 * Unregister a IDocumentationChangedListener (see {@link IDocumentationChangedListener}).
	 * 
	 * @param listener
	 */
	public void unregisterDocumentationChangedListener(IDocumentationChangedListener listener);

	/**
	 * @return the registered IDocumentationChangedListeners.
	 */
	public Set<IDocumentationChangedListener> getRegisteredDocumentationChangedListeners();
}
