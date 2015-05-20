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
 * Anne Haugommard (Atos Origin) anne.haugommard@atosorigin.com - Initial API and implementation
 * Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com - add catching error with id
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.services.docx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.papyrus.gendoc2.documents.AbstractImageService;
import org.eclipse.papyrus.gendoc2.documents.IAdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.documents.ImageDimension;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;

/**
 *
 */
public class DOCXImageService extends AbstractImageService
{

	public void clear ()
	{
	}

	public String manageImage (ITag tag, String imageId, String imagePath, boolean keepH, boolean keepW, boolean maxH, boolean maxW) throws AdditionalResourceException
	{
	    
		String toInsert = "<v:imagedata xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + imageId + "\" o:title=\"\" />";

		StringBuffer newTagContent = new StringBuffer(tag.getRawText());
		try{
		Pattern p = Pattern.compile("<v:shape [^<>]*>");
		Matcher m = p.matcher(newTagContent);
		if (m.find())
		{
			int index = m.end();
			newTagContent = newTagContent.insert(index, toInsert);
		}

		newTagContent = this.changeImageSize(newTagContent, imagePath, keepW, keepH, maxH, maxW);
		}
		 catch (AdditionalResourceException e)
	        {
	            IGendocDiagnostician diagnostician =(IGendocDiagnostician)GendocServices.getDefault().getService(IGendocDiagnostician.class);
	            String tagIdDocx = tag.getAttributes().get(RegisteredTags.ID);
	  
	            if (null == tagIdDocx)
	            {
	                throw e; 
	            }
	            else
	            { 
	               diagnostician.addDiagnostic(Diagnostic.WARNING, "The execution of tag with id '"+tagIdDocx+"' failed : Image cannot not be resized", null);
	            }
	          
	        }
		return newTagContent.toString().replaceFirst("editas=\"canvas\"", "");
	}

	private StringBuffer changeImageSize (StringBuffer tagContent, String imagePath, boolean keepW, boolean keepH, boolean maxH, boolean maxW) throws AdditionalResourceException
	{
		IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
		IAdditionalResourceService resourceService = documentService.getAdditionalResourceService();

		double width = 0;
		double height = 0;
		Pattern p = Pattern.compile("<v:group [^>]*>");
		Matcher m = p.matcher(tagContent);
		int widthStart = -1;
		int widthEnd = -1;
		int heightStart = -1;
		int heightEnd = -1;
		if (m.find())
		{
			int start = m.start();
			String subString = m.group();
			p = Pattern.compile("width:[^;]*;");
			m = p.matcher(subString);
			if (m.find())
			{
				widthStart = start + m.start();
				widthEnd = start + m.end();
			}
			p = Pattern.compile("height:[^;]*;");
			m = p.matcher(subString);
			if (m.find())
			{
				heightStart = start + m.start();
				heightEnd = start + m.end();
			}
		}
		if (widthStart != -1)
		{
			String widthString = tagContent.substring(widthStart, widthEnd).replaceAll("(width:|;)", "");
			if (widthString.contains("pt"))
			{
				width = this.pointsToPixels(Double.parseDouble(widthString.replace("pt", "")));
			}
			else if (widthString.contains("in"))
			{
				width = this.inchesToPixels(Double.parseDouble(widthString.replace("in", "")));
			}
			else
			{
				width = Double.parseDouble(widthString);
			}
		}
		if (heightStart != -1)
		{
			String heightString = tagContent.substring(heightStart, heightEnd).replaceAll("(height:|;)", "");
			if (heightString.contains("pt"))
			{
				height = this.pointsToPixels(Double.parseDouble(heightString.replace("pt", "")));
			}
			else if (heightString.contains("in"))
			{
				height = this.inchesToPixels(Double.parseDouble(heightString.replace("in", "")));
			}
			else
			{
				height = Double.parseDouble(heightString);
			}
		}

		ImageDimension d = resourceService.resizeImage(imagePath, width, height, keepH, keepW, maxH, maxW);
		if (heightStart != -1)
		{
			tagContent = tagContent.replace(heightStart, heightEnd, "height:" + d.getHeight() + ";");
		}
		if (widthStart != -1)
		{
			tagContent = tagContent.replace(widthStart, widthEnd, "width:" + d.getWidth() + ";");
		}
		return tagContent;

	}

	private double pointsToPixels (double dim)
	{
		return 2.54 * this.cmToPixels(dim) / 72;
	}

}
