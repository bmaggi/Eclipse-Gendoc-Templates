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
 *  Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.documents;

import org.eclipse.papyrus.gendoc2.documents.IImageService;
import org.eclipse.papyrus.gendoc2.services.IService;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;

/**
 * Service used for additional resources
 */
public interface IAdditionalResourceService extends IService
{
    /**
     * Add all additional resources to the final document
     * 
     * @throws AdditionalResourceException
     */
    void addAdditionalResourcesToDocument() throws AdditionalResourceException;

    /**
     * A an image referenced to the full path to the additional resources
     * 
     * @param fullPath image full path
     * @return image ID
     */
    String addImage(String fullPath);

    /**
     * Get the image service
     * 
     * @return an image service
     */
	IImageService getImageService();

    /**
     * Add a diagram runnable
     * 
     * @param runnable runnable object that manages diagrams
     * @return the diagram id
     */
    String addNewImageRunnable(ResourceRunnable runnable);

    /**
     * Get the full path of the resource folder of the generated diagram
     * 
     * @return the path of the resource folder
     */
    String getResourceFolder();

    /**
     * Get the image relative path inside the document from its key
     * 
     * @param imageKey unique id of the image
     * @return the image relative path
     */
    String getImageRelativePath(String imageKey);
    
    /**
     * Resize an image in a frame according to attributes keepW and keepH
     * @param imagePath path of the image file
     * @param frameWidth width of the frame
     * @param frameHeight height of the frame
     * @param keepH when frame height is kept
     * @param keepW when frame width is kep
     * @param maxH frame height is maximum
     * @param maxW frame width is maximum
     * @return a Dimension (height and width) of the new image
     * @throws AdditionalResourceException
     */
    ImageDimension resizeImage(String imagePath, double frameWidth, double frameHeight, boolean keepH, boolean keepW, boolean maxH, boolean maxW) throws AdditionalResourceException;

    /**
     * Add a runnable resource to the document
     * @param mediaLoc media location
     * @param diagramKey
     * @return path of the new image
     * @throws AdditionalResourceException
     */
    String addRunnableResourceToDocument(String mediaLoc, String diagramKey) throws AdditionalResourceException;
    
    /**
     * Add an external file to the document
     * 
     * @param fullPath the path to the external file
     * @return relationship identifier  
     */
    String includeFile(String filePath);

}
