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
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * Transform exception messages into more user friendly UI messages.
 */
public class UIMessages
{
    
    private static final String UNKNOWN_TYPE = "Invalid Type :.*";
    private static final String IMAGE_ID_NOT_FOUND = "Image with id '.*' cannot be found.";
    private static final String NO_MODEL_SPECIFIED = "The model No path provided can not be loaded";
    
    public static String transformToUIMessage(String exceptionMessage){
        if (exceptionMessage.matches(UNKNOWN_TYPE))
        {
            return exceptionMessage + ". This may be due to references to multiple metamodels in your model. TIP: You could try to add the following attribute in <context> tag : searchMetamodels='true'.";
        }
        else if (exceptionMessage.matches(IMAGE_ID_NOT_FOUND)){
            return exceptionMessage + " TIP: Check script used inside the attribute 'object' of <image> tags. Brackets may be missing.";
        }
        else if (exceptionMessage.matches(NO_MODEL_SPECIFIED)){
            return "No model file provided. Please fill attribute 'model' of <context> tag.";
        }
        return exceptionMessage;
    }
    
}
