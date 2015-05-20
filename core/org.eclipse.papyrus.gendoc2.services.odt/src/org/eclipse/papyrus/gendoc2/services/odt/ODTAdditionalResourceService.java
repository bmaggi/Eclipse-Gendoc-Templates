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
package org.eclipse.papyrus.gendoc2.services.odt;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.document.parser.documents.ZipDocument;
import org.eclipse.papyrus.gendoc2.documents.AdditionalResourceService;
import org.eclipse.papyrus.gendoc2.documents.FileRunnable;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.documents.IImageService;
import org.eclipse.papyrus.gendoc2.documents.ResourceRunnable;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.AdditionalResourceException;

public class ODTAdditionalResourceService extends AdditionalResourceService
{

    private static final String MANIFEST_XML = "manifest.xml";

    private static final String META_INF = "META-INF";

    ODTImageService imageHandler;

    public ODTAdditionalResourceService()
    {
        super();
        imageHandler = new ODTImageService();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.impl.AdditionalResourceService#addAdditionalResourcesToDocument()
     */
    public void addAdditionalResourcesToDocument() throws AdditionalResourceException
    {
        String pictureLoc = getResourceFolder();
        // Get directory "Pictures" and add it if not present
        File pictureDir = new File(pictureLoc);
        if (!pictureDir.exists())
        {
            pictureDir.mkdir();
        }

        // Add images resources to document
        addImagesResourcesToDocument(pictureLoc);

        // Add pictures entries in manifest
        if (pictureDir.listFiles().length > 0)
        {
            addPicturesToManifest(pictureDir.listFiles());
        }
    }

    private void addPicturesToManifest(File[] files) throws AdditionalResourceException
    {
        File f = getMetaInfFile();
        if (f != null)
        {
            StringBuffer relsContent;
            try
            {
                for (File file : files)
                {
                    relsContent = new StringBuffer(readFileContent(f));

                    Pattern p = Pattern.compile("</manifest:manifest>");
                    Matcher m = p.matcher(relsContent);
                    if (m.find())
                    {
                        int index = m.start();
                        relsContent.insert(index, newManifestEntry(file));
                        writeFileContent(f, relsContent);
                    }
                }
            }
            catch (IOException e)
            {
                throw new AdditionalResourceException(e);
            }
        }
        else
        {
            final ILogger logger = GendocServices.getDefault().getService(ILogger.class);
            logger.log("Error during additional resources addition, the manifest.xml file cannot be found in META-INF", ILogger.DEBUG);
        }
    }

    private String newManifestEntry(File file)
    {
        return "<manifest:file-entry manifest:media-type=\"image/" + file.getName().substring(file.getName().lastIndexOf(".")) + "\" manifest:full-path=\"Pictures/" + file.getName() + "\" />";
    }

    public String addRunnableResourceToDocument(String mediaLoc, String diagramKey) throws AdditionalResourceException
    {
        if (runnableMap.get(diagramKey) == null)
        {
            throw new AdditionalResourceException("Image with id '" + diagramKey + "' cannot be found.");
        }

        // Get runnableResource

        ResourceRunnable runnable = runnableMap.get(diagramKey);

        String extension = getFileExtensionFromRunnable(runnable);
        // Run the resource
        runnable.run(diagramKey, getResourceFolder());
        return getResourceFolder() + SEPARATOR + diagramKey + "." + extension;
    }

    private String getFileExtensionFromRunnable(ResourceRunnable runnable)
    {
        if (runnable instanceof FileRunnable)
        {
            return ((FileRunnable) runnable).getFileExtension();
        }
        return DIAGRAM_EXTENSION;
    }

    private void addImagesResourcesToDocument(String pictureLoc) throws AdditionalResourceException
    {

        int notGeneratedImages = 0;
        // For each image
        for (String imageKey : imagesMap.keySet())
        {
            
            File imageFile = new File(imagesMap.get(imageKey));

            // Extract file name
            String imageLink = imageKey+"."+Path.fromOSString(imageFile.getAbsolutePath()).getFileExtension();;
            try
            {
                copyImage(imageFile, pictureLoc + SEPARATOR + imageLink);
            }
            catch (IOException e)
            {
                throw new AdditionalResourceException("File '" + imageFile.getAbsolutePath() + "'cannot be copied.", e);
            }
            if (!imageFile.exists())
            {
                notGeneratedImages++;
            }
        }
        if (notGeneratedImages == 1)
        {
            throw new AdditionalResourceException("An image cannot be generated and has been replaced by a red cross. TIP: Check attribute 'filePath' or 'object' of the <image> tag.");
        }
        else if (notGeneratedImages > 1)
        {
            throw new AdditionalResourceException(notGeneratedImages
                    + " images cannot be generated and have been replaced by a red cross. TIP: Check attribute 'filePath' or 'object' of the <image> tag.");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.services.impl.AdditionalResourceService#getImageHandler()
     */
    public IImageService getImageService()
    {
        return new ODTImageService();
    }

    protected String getRelativeResourceFolder()
    {
        return "Pictures";
    }

    public String includeFile(String filePath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public File getMetaInfFile()
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        Document document = documentService.getDocument();
        if (document instanceof ZipDocument)
        {
            File unzipLoc = ((ZipDocument) document).getUnzipLocationDocumentFile();
            String mediaLoc = unzipLoc.getAbsolutePath() + SEPARATOR + META_INF;
            File metainfDir = new File(mediaLoc);
            if (metainfDir.listFiles().length > 0)
            {
                for (File file : metainfDir.listFiles())
                {
                    if (file.getName().equals(MANIFEST_XML))
                    {
                        return file;
                    }
                }
            }
        }
        return null;
    }
}
