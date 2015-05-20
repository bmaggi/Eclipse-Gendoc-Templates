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
 * Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com - add catching error with id
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.odt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.papyrus.gendoc2.documents.AbstractImageService;
import org.eclipse.papyrus.gendoc2.documents.IAdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.documents.ImageDimension;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;
import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;

public class ODTImageService extends AbstractImageService
{

    public String manageImage(ITag tag, String imageId, String imagePath, boolean keepH, boolean keepW, boolean maxH, boolean maxW) 
    {
        
        String widthUnit = "cm";
        String heightUnit = "cm";
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        IAdditionalResourceService resourceService = documentService.getAdditionalResourceService();
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);
      

        StringBuffer height = new StringBuffer();
        StringBuffer newTagContent = new StringBuffer(tag.getRawText());
        double frameWidth = 0;
        double frameHeight = 0;

        Pattern p = Pattern.compile("svg:width=\"[^\"]*\"");
        Matcher m = p.matcher(newTagContent);
        if (m.find())
        {
            if(m.group().endsWith("cm\"")){
                widthUnit="cm";
            }
            else if (m.group().endsWith("in\"")){
                widthUnit="in";
            }
            frameWidth = Double.valueOf(m.group().replaceAll("(in|cm|svg:width=|\")", ""));
        }

        p = Pattern.compile("svg:height=\"[^\"]*\"");
        m = p.matcher(newTagContent);
        if (m.find())
        {
            if(m.group().endsWith("cm\"")){
                heightUnit="cm";
            }
            else if (m.group().endsWith("in\"")){
                heightUnit="in";
            }
            frameHeight = Double.valueOf(m.group().replaceAll("(in|cm|svg:height=|\")", ""));
        }
        else
        {
            p = Pattern.compile("<draw:text-box[^>]*fo:min-height=\"");
            m = p.matcher(newTagContent);
            if (m.find())
            {
                int index = m.end();
                String s = newTagContent.substring(index);
                height.append(" svg:height=\"");
                String heightAndUnit= s.substring(0, s.indexOf("\""));
                height.append(heightAndUnit);
                if( heightAndUnit.endsWith("cm"))
                {
                    heightUnit="cm";
                }
                else if (heightAndUnit.endsWith("in"))
                {
                    heightUnit="in";
                }
                frameHeight = Double.valueOf(heightAndUnit.replaceAll("in|cm", ""));
                height.append("\" ");
            }
        }

      
        logger.log("ODT image to insert : " + newTagContent.toString(), ILogger.DEBUG);
        try
        {
            double widthInPixels = 0;
            double heightInPixels = 0;

            if ("cm".equals(widthUnit))
            {
                widthInPixels = cmToPixels(frameWidth);
            }
            else if ("in".equals(widthUnit))
            {
                widthInPixels = inchesToPixels(frameWidth);
            }

            if ("cm".equals(heightUnit))
            {
                heightInPixels = cmToPixels(frameHeight);
            }
            else if ("in".equals(heightUnit))
            {
                heightInPixels = inchesToPixels(frameHeight);
            }

            ImageDimension d = resourceService.resizeImage(imagePath, widthInPixels, heightInPixels, keepH, keepW, maxH, maxW);
            d = pixelToCm(d); // transform into cm before inserting in the doc
            newTagContent = new StringBuffer(modifyHeight(newTagContent, d.getHeight()));
            newTagContent = new StringBuffer(modifyWidth(newTagContent, d.getWidth()));
            
            p = Pattern.compile("<draw:text-box[^<>]*>");
            m = p.matcher(newTagContent);
            if (m.find())
            {

                String toInsert = "<draw:frame xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\" text:anchor-type=\"frame\" svg:width=\""+(d.getWidth())+"cm\" style:rel-width=\"100%\" svg:height=\""+(d.getHeight())+"cm\" style:rel-height=\"scale\" >" +
                "<draw:image xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"" + resourceService.getImageRelativePath(imageId)
                + "\" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/></draw:frame>";

                
                int endIndex = m.end();
                newTagContent = newTagContent.insert(endIndex, toInsert);
                newTagContent = newTagContent.insert(endIndex - 1, height.toString());
            }
            
        }
        catch (AdditionalResourceException e)
        {
            IGendocDiagnostician diagnostician =(IGendocDiagnostician)GendocServices.getDefault().getService(IGendocDiagnostician.class);
            String tagIdOdt = tag.getAttributes().get(RegisteredTags.ID);
  
            if (null == tagIdOdt)
            {
                diagnostician.addDiagnostic(Diagnostic.WARNING, "Image cannot not be resized", null); 
            }
            else
            { 
               diagnostician.addDiagnostic(Diagnostic.WARNING, "The execution of tag with id '"+tagIdOdt+"' failed : Image cannot not be resized", null);
            }
          
           
          
        }
        return newTagContent.toString();
    }

    protected String modifyHeight(StringBuffer tagContent, double height)
    {
        return modifyDimension(tagContent, "svg:height=\"[^\"]*\"", "svg:height=\"" + height + "cm\"");
    }

    protected String modifyWidth(StringBuffer tagContent, double width)
    {
        return modifyDimension(tagContent, "svg:width=\"[^\"]*\"", "svg:width=\"" + width + "cm\"");
    }

    /**
     * Insert modified dimensions inside 
     * @param tagContent
     * @param patternToMatch
     * @param ToInsert
     * @return
     */
    private String modifyDimension(StringBuffer tagContent, String patternToMatch, String ToInsert)
    {

        Pattern p = Pattern.compile(patternToMatch);
        Matcher m = p.matcher(tagContent);
        boolean found = m.find();
        if(!found){
            p = Pattern.compile("<draw:frame [^<>]*>");
            m = p.matcher(tagContent);
            if (m.find())
            {
                int endIndex = m.end();
                tagContent = tagContent.insert(endIndex - 1, " " + ToInsert);
            }
        }
        else
        {
            tagContent = tagContent.replace(m.start(), m.end(), ToInsert);
            found = m.find();
        }
        return tagContent.toString();
    }

	public void clear ()
	{
		// TODO Auto-generated method stub
		
	}

}
