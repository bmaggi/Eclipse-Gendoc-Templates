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

import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;
import org.eclipse.papyrus.gendoc2.tags.ITag;

public interface IImageService extends IService
{

	String getFilePath (String imageId) throws AdditionalResourceException;

	String getImageId (String filePath);

	String manageImage (ITag tag, String imageId, String filePath, boolean keepH, boolean keepW, boolean maxH, boolean maxW) throws AdditionalResourceException;

}
