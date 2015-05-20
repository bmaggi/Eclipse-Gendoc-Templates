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

public class DocumentationUnsupportedException extends Exception {

	private static final long serialVersionUID = -5469193051315476642L;

	public DocumentationUnsupportedException() {
		super();
	}

	public DocumentationUnsupportedException(String message) {
		super(message);
	}
}
