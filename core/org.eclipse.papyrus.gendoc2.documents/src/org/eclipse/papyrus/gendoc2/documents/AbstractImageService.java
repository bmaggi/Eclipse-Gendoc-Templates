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

package org.eclipse.papyrus.gendoc2.documents;

import java.awt.Toolkit;

import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;

public abstract class AbstractImageService extends AbstractService implements IImageService
{

	public String getFilePath (String imageId) throws AdditionalResourceException
	{
		IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
		IAdditionalResourceService additionalResourceService = documentService.getAdditionalResourceService();
		String filePath = additionalResourceService.addRunnableResourceToDocument(additionalResourceService.getResourceFolder(), imageId);
		return filePath;
	}

	public String getImageId (String filePath)
	{
		IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
		String imageId = documentService.getAdditionalResourceService().addImage(filePath.replaceAll("\\\\", "/"));
		return imageId;
	}

	protected double cmToPixels (double dim)
	{
		return dim * (Toolkit.getDefaultToolkit().getScreenResolution() / 2.54);
	}

	protected double inchesToPixels (double dim)
	{
		return 2.54 * this.cmToPixels(dim);
	}

	protected double pixelToCm (double dim)
	{
		return dim * 2.54 / (Toolkit.getDefaultToolkit().getScreenResolution());
	}

	protected ImageDimension pixelToCm (ImageDimension dim)
	{
		ImageDimension dim2 = new ImageDimension();
		dim2.setWidth(this.pixelToCm(dim.getWidth()));
		dim2.setHeight(this.pixelToCm(dim.getHeight()));
		return dim2;
	}

}
