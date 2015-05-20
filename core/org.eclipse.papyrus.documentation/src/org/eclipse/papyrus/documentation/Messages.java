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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.papyrus.documentation.messages"; //$NON-NLS-1$

	public static String AddOrRemoveAssociatedResourceCommandLabel;

	public static String ChangeDocCommandLabel;

	public static String DocumentationManager_UnsupportedModelType;

	public static String EAnnotationDocumentationManager_DocOnDocEAnnotationError;

	public static String EAnnotationDocumentationManager_NonEModelElementError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
