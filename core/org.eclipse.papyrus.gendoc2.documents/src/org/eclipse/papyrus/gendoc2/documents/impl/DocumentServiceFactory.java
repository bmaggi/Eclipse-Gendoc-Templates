/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.documents.impl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentManager;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ServicesExtensionPoint;
import org.eclipse.papyrus.gendoc2.services.exception.DocumentServiceException;

public class DocumentServiceFactory implements IExecutableExtensionFactory, IExecutableExtension
{

	private String serviceId;
	
	public Object create () throws CoreException
	{
		IDocumentService service = null;
		try
		{
			Document document = ((IDocumentManager)GendocServices.getDefault().getService(IDocumentManager.class)).getDocTemplate();
			if (document != null)
			{
				String extension = new Path(document.getPath()).getFileExtension();
				if (extension == null)
				{
					throw new DocumentServiceException("Invalid document extension : null.");
				}
				IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(DocumentTypesExtensionPoint.EXTENSION_POINT_ID);
				for (IConfigurationElement element : elements)
				{
					String extensionAttribute = element.getAttribute(DocumentTypesExtensionPoint.DOCUMENT_TYPE_EXTENSION);
					if (extension.equals(extensionAttribute))
					{
						service = (IDocumentService)element.createExecutableExtension(DocumentTypesExtensionPoint.DOCUMENT_TYPE_DOCUMENT_SERVICE);
	                    service.setDocument(document);
	                    service.setServiceId(this.serviceId);
					}
				}
				if (service == null)
				{
					throw new DocumentServiceException("Invalid document extension : " + extension);
				}
			}
			else
			{
				throw new IllegalStateException("Document service cannot be initialized : no document is defined.");
			}
		}
		catch (DocumentServiceException e)
		{
			throw new IllegalStateException("Document service cannot be initialized.", e);
		}
		return service;
	}

	public void setInitializationData (IConfigurationElement config, String propertyName, Object data) throws CoreException
	{
		String serviceIdAttribute = config.getAttribute(ServicesExtensionPoint.SERVICE_ID);
		this.serviceId = serviceIdAttribute;
	}

}
