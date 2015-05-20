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
 *  Papa Malick Wade (Atos Origin) papa-malick.wade@atosorigin.com - extension the format of diagrams 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.docx;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.papyrus.gendoc2.document.parser.documents.docx.DocxDocument;
import org.eclipse.papyrus.gendoc2.documents.AdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.FileRunnable;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.documents.IImageService;
import org.eclipse.papyrus.gendoc2.documents.MimeTypes;
import org.eclipse.papyrus.gendoc2.documents.ResourceRunnable;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;
import org.eclipse.papyrus.gendoc2.services.exception.UnknownMimeTypeException;

/**
 * @author ahaugomm
 * 
 */
public class DOCXAdditionalResourceService extends AdditionalResourceService
{
    private static String DOCUMENT_XML_RELS = "document.xml.rels";
    
    private DOCXImageService imageHandler;
    
    private StringBuffer relationShipsToAdd;

    protected Map<String, String> externalChunkMap;

    public DOCXAdditionalResourceService()
    {
        super();
        imageHandler = new DOCXImageService();
        relationShipsToAdd = new StringBuffer();
        externalChunkMap = new LinkedHashMap<String, String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.papyrus.gendoc2.services.IAdditionalResourceService# getImageHandler()
     */
    public IImageService getImageService()
    {
        return imageHandler;
    }

    public void addAdditionalResourcesToDocument() throws AdditionalResourceException
    {

        String mediaLoc = getResourceFolder();
        DOCXDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        File unzipLoc = ((DocxDocument) documentService.getDocument()).getUnzipLocationDocumentFile();

        // Get directory "media" and add it if not present
        File mediaDir = new File(mediaLoc);
        if (!mediaDir.exists())
        {
            mediaDir.mkdir();
        }
        // Add images resources to document
        addImagesResourcesToDocument(mediaLoc);

        // Add external file resources to document
        addExternalResourcesToDocument(mediaLoc);

        // Add mappings between images and files
        String relsLocation = addRelationShips(unzipLoc);

        // Add mapping files for each document XML (document.xml, headers, footers)
        addMappingFiles(unzipLoc, relsLocation);

        // Modify file with content types [Content_Types].xml to add unknown
        // file extensions
        modifyContentTypes(unzipLoc);

    }

    /**
     * Copy relationships to all headers and footers
     * 
     * @param unzipLoc unzip location containing all files from the DOCX archive
     * @param relsLocation location of the word/rels directory containing the future mapping files
     */
    private void addMappingFiles(File unzipLoc, String relsLocation) throws AdditionalResourceException
    {
        ILogger logger = GendocServices.getDefault().getService(ILogger.class);

        // Get Word directory
        File wordDir = new File(unzipLoc.getAbsolutePath() + SEPARATOR + "word");
        if (!wordDir.isDirectory())
        {
            throw new AdditionalResourceException("Invalid mapping files directory :" + wordDir);
        }

        String[] fileNames = wordDir.list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                name = name.toLowerCase();
                return name.endsWith(".xml") && (name.contains("header") || name.contains("footer"));
            }
        });
        try
        {
        	// Add new relationships to all headers and footers
            for (String fileName : fileNames)
            {
            	File rels = new File(relsLocation + fileName + ".rels");
            	StringBuffer relsContent = new StringBuffer(readFileContent(rels));
            	Pattern p = Pattern.compile("</Relationships>");
                Matcher m = p.matcher(relsContent);
                if (m.find())
                {
                    int index = m.start();
                    relsContent.insert(index, relationShipsToAdd);
                    writeFileContent(rels, relsContent);
                }
            }
        }
        catch (IOException e)
        {
            logger.log("Mapping files for headers and footers are not copied properly. Some images in headers and footers can be missing.", IStatus.INFO);
        }

    }

    /**
     * Update file [Content_Types].xml, that contains the list of content types of the additional resources
     * 
     * @param unzipLoc the location of
     * @throws AdditionalResourceException
     */
    private void modifyContentTypes(File unzipLoc) throws AdditionalResourceException
    {
        File contentTypes = new File(unzipLoc.getAbsolutePath() + SEPARATOR + "[Content_Types].xml");
        try
        {
            String contents = readFileContent(contentTypes);
            StringBuffer extensionsToAdd = new StringBuffer();
            for (String extension : fileExtensions)
            {
                if (!contents.contains("<Default Extension=\"" + extension + "\""))
                {
                    try
                    {
                        extensionsToAdd.append("<Default Extension=\"" + extension + "\" ContentType=\"" + MimeTypes.getMimeTypefromExtension(extension) + "\"/>");
                    }
                    catch (UnknownMimeTypeException e)
                    {
                        throw new AdditionalResourceException("Additional resource with extension '" + extension + "' is not supported inside generated DOCX file.", e);
                    }
                }
            }
            extensionsToAdd.append("</Types>");
            writeFileContent(contentTypes, new StringBuffer(contents.replace("</Types>", extensionsToAdd.toString())));

        }
        catch (IOException e)
        {
            throw new AdditionalResourceException("DOCX document generated cannot be completed: Problem with content types");
        }

    }

    private String addRelationShips(File unzipLoc) throws AdditionalResourceException
    {
        try
        {
            // Write relationShips inside word/_rels/document.xml.rels files
            Pattern p = Pattern.compile("</Relationships>");

            String relsLocation = unzipLoc.getAbsolutePath() + SEPARATOR + "word" + SEPARATOR + "_rels" + SEPARATOR;
            File rels = new File(relsLocation + DOCUMENT_XML_RELS);
            StringBuffer relsContent = new StringBuffer(readFileContent(rels));
            Matcher m = p.matcher(relsContent);
            if (m.find())
            {
                int index = m.start();
                relsContent.insert(index, relationShipsToAdd);
                writeFileContent(rels, relsContent);
            }
            return relsLocation;
        }
        catch (IOException e)
        {
            throw new AdditionalResourceException(e);
        }
    }

    public String addRunnableResourceToDocument(String mediaLoc, String diagramKey) throws AdditionalResourceException
    {
    	if(runnableMap.get(diagramKey) == null){
			throw new AdditionalResourceException("Image with id '"
					+ diagramKey + "' cannot be found.");
    	}
    	//Get runnableResource 
    	
    	ResourceRunnable runnable = runnableMap.get(diagramKey);
    	
    	String extension = getFileExtensionFromRunnable(runnable);

    	// Run the resource
    	runnable.run(diagramKey, getResourceFolder());
        // add relationship in document.xml.rels
        String newRelationShips = newRelationship(diagramKey, diagramKey + "." + extension);
        if(relationShipsToAdd.indexOf(newRelationShips) == -1) {
        	relationShipsToAdd.append(newRelationShips);
        }
        fileExtensions.add(extension);
        return getResourceFolder()+ SEPARATOR +diagramKey + "." + extension;

    }

    private String getFileExtensionFromRunnable(ResourceRunnable runnable)
    {
        if( runnable instanceof FileRunnable){
            return ((FileRunnable)runnable).getFileExtension();
        }
        return DIAGRAM_EXTENSION;
    }

    private void addImagesResourcesToDocument(String mediaLoc) throws AdditionalResourceException
    {

        // For each image
        for (String imageKey : imagesMap.keySet())
        {
            // add image as file in the media directory
            File imageFile = new File(imagesMap.get(imageKey));

            // Extract and normalize file name
            String imageLink = imageKey+"."+Path.fromOSString(imageFile.getAbsolutePath()).getFileExtension();
            try
            {
                copyImage(imageFile, mediaLoc + SEPARATOR + imageLink );
            }
            catch (IOException e)
            {
                throw new AdditionalResourceException("File '" + imageFile.getAbsolutePath() + "'cannot be copied.", e);
            }
            // add relationship in document.xml.rels
            relationShipsToAdd.append(newRelationship(imageKey, imageLink));
            
            if (!imageFile.exists())
            {
                throw new AdditionalResourceException("An image cannot be generated and has been replaced by a red cross. Cause: No image found at location:" + imageFile.getAbsolutePath());
            }
        }

    }

    private String newRelationship(String imageKey, String imageLink)
    {
        return "<Relationship Id=\"" + imageKey + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"media/" + imageLink + "\"/>";
    }

    public String getRelativeResourceFolder()
    {
        return "word" + SEPARATOR + "media";
    }

    public String includeFile (String fullPath)
    {
        String id = generateUniqueId();

        externalChunkMap.put(id, fullPath);

        // Add file extension to extensions list
        fileExtensions.add(Path.fromOSString(fullPath).getFileExtension());

        return id;
    }

    private void addExternalResourcesToDocument (String mediaLoc) throws AdditionalResourceException
    {
        for (String key : externalChunkMap.keySet())
        {
            // add external file as file in the media directory
            File file = new File(externalChunkMap.get(key));
            // Extract file
            String externalLink = key+"."+Path.fromOSString(file.getAbsolutePath()).getFileExtension();//normalizeString(getFileName(externalChunkMap.get(key)));
            try
            {
                copyFile(file, mediaLoc + SEPARATOR + externalLink);
            }
            catch (IOException e)
            {
                throw new AdditionalResourceException("File '" + file.getAbsolutePath() + "'cannot be copied.", e);
            }
            // add relationship in document.xml.rels
            relationShipsToAdd.append(newExternalChunkRelationship(key, externalLink));
            if (!file.exists())
            {
                throw new AdditionalResourceException("An external file cannot be generated. Cause: No file found at location:" + file.getAbsolutePath());
            }
        }
    }

    private String newExternalChunkRelationship (String key, String target)
    {
        return "<Relationship Id=\"" + key + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/aFChunk\" Target=\"media/" + target + "\"/>";
    }

}
