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

import org.eclipse.osgi.util.NLS;
import org.eclipse.papyrus.gendoc2.services.exception.UnknownMimeTypeException;

/**
 * @author ahaugomm
 * 
 */
public class MimeTypes extends NLS
{

    public static String GIF;

    public static String JPG;

    public static String JPEG;

    public static String BMP;

    public static String PNG;

    public static String SVG;
    
    public static String HTML;

    public static String XHTML;
    
    public static String DOCX;
    
    public static String RTF;
    
    public static String TXT;

    public static final String getMimeTypefromExtension(String extension) throws UnknownMimeTypeException{
        if( extension.equals("gif")){
            return GIF;
        }
        else if (extension.equals("jpg")){
            return JPG;
        }
        else if (extension.equals("jpeg")){
            return JPEG;
        }
        else if (extension.equals("bmp")){
            return BMP;
        }
        else if (extension.equals("png")){
            return PNG;
        }
        else if (extension.equals("svg")){
            return SVG;
        }
        else if (extension.equals("html")){
            return HTML;
        }
        else if (extension.equals("xhtml")){
            return XHTML;
        }
        else if (extension.equals("docx")){
        	return DOCX;
        }
        else if (extension.equals("rtf")){
        	return RTF;
        }
        else if (extension.equals("txt")){
        	return TXT;
        }
        else {
            throw new UnknownMimeTypeException(extension);
        }
        
    }
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(MimeTypes.class.getName(), MimeTypes.class);
    }

}
