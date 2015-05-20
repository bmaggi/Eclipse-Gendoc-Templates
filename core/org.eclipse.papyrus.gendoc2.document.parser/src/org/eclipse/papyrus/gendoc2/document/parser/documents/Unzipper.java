/*****************************************************************************
 * Copyright (c) 2008 Atos Origin.
 * 
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Tristan Faure (Atos Origin) tristan.faure@atosorigin.com -
 * Initial API and implementation
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.document.parser.documents;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.Path;
import org.eclipse.papyrus.gendoc2.document.parser.Activator;

/**
 * The Class Unzipper.
 * 
 * @author tristan.faure@atosorigin.com
 */
public class Unzipper
{

    /** The in file name. */
    private URL inFileName;

    /** The path. */
    private String path = null;

    /** The files. */
    private HashMap < String, File > files = new HashMap < String, File >();

    /**
     * Instantiates a new unzipper.
     * 
     * @param file the file
     */
    public Unzipper(File file)
    {
        this (file,null);
        path += file.getName();
    }
    
    public Unzipper(URL file)
    {
        this (file,null);
        path += file.getPath().substring(file.getPath().lastIndexOf('/')+1, file.getPath().length());
    }

    public Unzipper(File file, String thepath)
    {
        path = Activator.getDefault().getStateLocation() + File.separator + "unzipped" + File.separator;
        try
        {
            inFileName = file.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        if (thepath != null)
        {
            path = thepath;
        }
    }
    
    public Unzipper(URL url, String thepath)
    {
        path = Activator.getDefault().getStateLocation() + File.separator + "unzipped" + File.separator;
        inFileName = url;
        if (thepath != null)
        {
            path = thepath;
        }
    }

    /**
     * Unzip.
     */
    public void unzip()
    {
        // The location to unzip the file
        String locationToUnzip = path;

        try
        {
            InputStream openStream = inFileName.openStream();
            ZipInputStream zis = new ZipInputStream(openStream);

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null)
            {
                File file = new File(locationToUnzip, zipEntry.getName());
                if (zipEntry.isDirectory())
                {
                    file.mkdirs();
                }
                else
                {
                    file.getParentFile().mkdirs();
                    final OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));

                    try
                    {
                        final byte[] buf = new byte[2048];
                        int bytesRead;
                        while (-1 != (bytesRead = zis.read(buf)))
                        {
                            fos.write(buf, 0, bytesRead);
                        }

                    }
                    catch (final IOException ioe)
                    {
                        throw new IllegalArgumentException(String.format("can't open %s", inFileName.getPath()));
                    }
                    finally
                    {
                        fos.close();
                    }

                    if (file.length() == 0
                            && Path.fromOSString(file.getAbsolutePath()).getFileExtension().length() == 0)
                    {
                        String pathFile = file.getAbsolutePath();
                        file.delete();
                        new File(pathFile).mkdir();

                    }
                    else
                    {
                        files.put(file.getName(), file);
                        // This second entry is used to access a file using its path in the zip (example: dir/dir/filename)
                        files.put(zipEntry.getName(), file);
                    }

                }
                zipEntry = zis.getNextEntry();
            }
            zis.close();
            openStream.close();

        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(String.format("can't open %s", inFileName.getPath()));
        }
    }

    /**
     * Clean.
     */
    public void clean()
    {
        File f = new Path(path).toFile();
        clean(f);
    }

    /**
     * Clean.
     * 
     * @param f the f
     */
    private void clean(File f)
    {
        File[] thefiles = f.listFiles();
        for (File f2 : thefiles)
        {
            if (f2.isDirectory())
            {
                clean(f2);
            }
            else
            {
                f2.deleteOnExit();
            }
        }
        f.deleteOnExit();
    }

    /**
     * Gets the file.
     * 
     * @param name the name or its relative path in the zip
     * 
     * @return the file
     */
    public File getFile(String name)
    {
        File result = files.get(name);
        if (result == null)
        {
            for (File f : files.values())
            {
                String pattern = "" ;
                if (File.separator.contains("\\"))
                {
                    pattern = "\\";
                }
                if (name.contains(".."))
                {
                    name = name.replace(".", "");
                }
                if (f.getAbsolutePath().replaceAll(pattern + File.separator, "/").contains(name))
                {
                    result =  f ;
                    break ;
                }
            }
        }
        return result;
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    static void main(String[] args)
    {
        new Unzipper(new File("toto")).unzip();
    }

    /**
     * @deprecated 
     * @return
     */
    public File getDocumentFile()
    {
        return new File(inFileName.getFile());
    }

    public File getUnzipDocumentFile()
    {
        return new Path(path).toFile();
    }
}
