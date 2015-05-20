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
 *  Papa Malick Wade (Atos Origin) papa-malic.wade@atosorigin.com - Extension the format of diagrams 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.documents;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.ZipDocument;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServiceActivator;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;

public abstract class AdditionalResourceService extends AbstractService implements IAdditionalResourceService
{

    protected static String DIAGRAM_EXTENSION = "png";
    
    protected String SEPARATOR = "/";

    protected Map<String, ResourceRunnable> runnableMap;

    protected Map<String, String> imagesMap;

    protected Set<String> fileExtensions;
    

    public AdditionalResourceService()
    {
        super();
        imagesMap = new LinkedHashMap<String, String>();
        runnableMap = new LinkedHashMap<String, ResourceRunnable>();
        fileExtensions = new HashSet<String>();
    }

    public void clear()
    {
        // do nothing

    }

   

    /**
     * Get a file name from a file path
     * 
     * @param filePath the full path of a file
     * @return the file name
     */
    protected String getFileName(String filePath)
    {

        int index = filePath.lastIndexOf(File.separatorChar);
        if (index == -1)
        {
            return filePath;
        }
        return filePath.substring(index);
    }

    /**
     * Add an image to the image map
     * 
     * @param fullPath the path to the image
     * @return image identifier in the map
     */
    public String addImage(String fullPath)
    {

        String imageId = generateUniqueId();

        // Keep image in imagesMap
        imagesMap.put(imageId,fullPath);

        // Add file extension to extensions list
        fileExtensions.add(Path.fromOSString(fullPath).getFileExtension());

        return imageId;
    }

    public String getImageRelativePath(String imageKey)
    {

        String imageFullPath = imagesMap.get(imageKey);
        if (imageFullPath != null)
        {
            File imageFile = new File(imagesMap.get(imageKey));
            // Extract file name
            return getRelativeResourceFolder() + SEPARATOR + imageKey + "." +Path.fromOSString(imageFile.getAbsolutePath()).getFileExtension();
        }
        else
        {
            ResourceRunnable runnable = runnableMap.get(imageKey);
            String extension = getFileExtensionFromRunnable(runnable);
            
            // Diagram
            return getRelativeResourceFolder() + SEPARATOR + imageKey + "." + extension;
        }

    }

    private String getFileExtensionFromRunnable(ResourceRunnable runnable)
    {
        if (runnable instanceof FileRunnable)
        {
            return ((FileRunnable) runnable).getFileExtension();
        }
        return DIAGRAM_EXTENSION;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IAdditionalResourceService#addAdditionalResourcesToDocument()
     */
    public abstract void addAdditionalResourcesToDocument() throws AdditionalResourceException;
    
    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IAdditionalResourceService#addRunnableResourceToDocument(java.lang.String, java.lang.String)
     */
    public abstract String addRunnableResourceToDocument(String mediaLoc, String diagramKey) throws AdditionalResourceException;

    
    protected void copyImage(File fromImage, String toLocation) throws IOException{
        if( fromImage.exists()){
            copyFile(fromImage, toLocation);
        }
        else{
            InputStream in = GendocServiceActivator.getDefault().getBundle().getResource("resources/not_generated_picture.gif").openStream();
            copyFile(in, toLocation);
        }
    }
    
    protected void copyFile(File fromFile, String toLocation) throws IOException
    {
        InputStream in = new FileInputStream(fromFile);
        copyFile(in, toLocation);
    }
    
    private void copyFile(InputStream in, String toLocation) throws IOException
    {
        File outputFile = new File(toLocation);
        OutputStream out = new FileOutputStream(outputFile);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    protected String readFileContent(File file) throws IOException
    {
        String lineSep = System.getProperty("line.separator");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String nextLine = "";
        StringBuffer sb = new StringBuffer();
        while ((nextLine = br.readLine()) != null)
        {
            sb.append(nextLine);
            sb.append(lineSep);
        }
        return sb.toString();
    }

    protected void writeFileContent(File file, StringBuffer fileContent) throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(fileContent.toString());
        bw.close();
    }

    public String addNewImageRunnable(ResourceRunnable runnable)
    {
        String id = generateUniqueId();
        runnableMap.put(id, runnable);
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.IAdditionalResourceService#getResourceFolder()
     */
    public String getResourceFolder()
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        Document document = documentService.getDocument();
        if (document instanceof ZipDocument)
        {
            File unzipLoc = ((ZipDocument) document).getUnzipLocationDocumentFile();
            String mediaLoc = unzipLoc.getAbsolutePath() + SEPARATOR + getRelativeResourceFolder();
            return mediaLoc;
        }
        return null;
    }

    /**
     * Get the relative resource folder inside the unzipped generated document
     * @return
     */
    protected abstract String getRelativeResourceFolder();

    /**
     * Generate a unique ID
     * 
     * @return UUID
     */
    protected String generateUniqueId()
    {
        return EcoreUtil.generateUUID();
    }

    /* (non-Javadoc)
     * @see org.eclipse.papyrus.gendoc2.services.IAdditionalResourceService#resizeImage(java.lang.String, double, double, boolean, boolean)
     */
    public ImageDimension resizeImage(String imagePath, double frameWidth, double frameHeight, boolean keepH, boolean keepW, boolean maxH, boolean maxW) throws AdditionalResourceException
    {

        // By default, return frame dimension
        ImageDimension result = new ImageDimension();
        result.setWidth(frameWidth);
        result.setHeight(frameHeight);
        if (imagePath == null)
        {
            return result; 
        }

        try
        {
            BufferedImage image = javax.imageio.ImageIO.read(new File(imagePath));

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();

            if (keepH && keepW)
            {
                // Do nothing => image is adapted to frame
            }
            else
            {
                if (!keepH && keepW)
                {
                    result.setWidth(frameWidth);
                    result.setHeight((imageHeight * frameWidth) / imageWidth);
                }
                else if (!keepW && keepH)
                {
                    result.setWidth((frameHeight * imageWidth) / imageHeight);
                    result.setHeight(frameHeight);
                }
                else if (!keepW && !keepH)
                {
                    result.setWidth(imageWidth);
                    result.setHeight(imageHeight);
                }
            }

            if (maxW && (result.getWidth() >= frameWidth))
            {
                result.setWidth(frameWidth);
                result.setHeight((imageHeight * frameWidth) / imageWidth);
            }
            if (maxH && (result.getHeight() >= frameHeight))
            {
                result.setWidth((frameHeight * imageWidth) / imageHeight);
                result.setHeight(frameHeight);
            }
        }
        catch (IOException e)
        {
        	throw new AdditionalResourceException("Error when reading image file.", e);
        }

        return result;
    }

}
