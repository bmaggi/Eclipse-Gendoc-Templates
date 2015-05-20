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
 *  Caroline Bourdeu d'Aguerre (Atos Origin) caroline.bourdeudaguerre@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.tags.handlers.impl.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.papyrus.gendoc2.document.parser.documents.Document;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.documents.SourceException;
import org.eclipse.papyrus.gendoc2.services.AbstractService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IRegistryService;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.InvalidTemplateParameterException;
import org.eclipse.papyrus.gendoc2.tags.handlers.IConfigurationService;

/**
 * Description of the class Configuration.
 * 
 */

public class ConfigurationService extends AbstractService implements IConfigurationService
{

    /** The gendoc version used during the generation (default value is 2) */
    private int version = 2;

    /** The script language used for the generation (default value is acceleo) */
    private String language = "acceleo";

    /** Defined if a gendoc V1 generation has to be run (default value is false) */
    private boolean runV1 = false;

    /** The output file path to generate the doc */
    private String output;

    /** Variables defined by the user to used in the gendoc scripts */
    private Parameters parameters;

    /** The path to a repository that contains the generated images */
    private String importedDiagrams;

    private Pattern patternParam = Pattern.compile("\\$\\{[a-zA-Z0-9_]+\\}");

    /**
     * Constructor.
     */
    public ConfigurationService()
    {
        super();
        parameters = new Parameters();
    }

    public void clear()
    {
        parameters.clear();
    }

    /**
     * Replace the parameters by their values
     * 
     * @param path
     * @return the given string with all the parameters replaced with their value
     */
    public String replaceParameters(String path) throws InvalidTemplateParameterException
    {
        if (path == null)
        {
            return null;
        }
        Matcher m = patternParam.matcher(path);
        String paramName;
        String value;

        StringBuffer buffer = new StringBuffer();
        int index = 0; // Current index
        while (m.find())
        {
            buffer.append(path.substring(index, m.start()));
            paramName = path.substring(m.start() + 2, m.end() - 1);
            value = getParameter(paramName);
            if (value == null)
            {
                throw new InvalidTemplateParameterException(paramName, "No value found for parameter.");
            }
            buffer.append(value);
            index = m.end();
//            m = patternParam.matcher(buffer);
        }
        buffer.append(path.substring(index, path.length()));
        return buffer.toString();
    }

    // /////// Getters and Setters /////////

    public int getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = Integer.valueOf(version);
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public boolean isRunV1()
    {
        return runV1;
    }

    public void setRunV1(String runV1)
    {
        this.runV1 = Boolean.valueOf(runV1);
    }

    public String getOutput() throws GenDocException
    {
        if (output == null)
        {
            initOutputFile();
        }
        return output;
    }

    public void setOutput(String outputPath) throws GenDocException 
    {
        if (outputPath == null || outputPath.length() == 0)
        {
            initOutputFile();
        }
        else
        {
            StringBuffer newOutput = new StringBuffer(outputPath);

            // Check Directory
            String dirPath = getDirectory(outputPath);
            File dir = new File(dirPath);

            if (!dir.isDirectory())
            {
                dir.mkdir();
            }

            // Check file
            if ("".equals(getFile(outputPath)))
            {
                Document template = ((IDocumentService) GendocServices.getDefault().getService(IDocumentService.class)).getDocument();
                String filePath = template.getPath().substring(template.getPath().lastIndexOf('/')+1, template.getPath().length());
                filePath = addGeneratedPart(filePath);

                if (newOutput.toString().endsWith("/"))
                {
                    newOutput.append(filePath);
                }
                else
                {
                    newOutput.append("/" + filePath);
                }
            }
            this.output = newOutput.toString();
        }
    }

    /**
     * Return the the eventual file name at the end of the path)
     * 
     * @param path
     * @return the file name if exist else ""
     */
    private String getFile(String path)
    {
        String fileName = path.substring(path.lastIndexOf('/'), path.length());

        if (!"".equals(fileName))
        {
            if (!fileName.contains("."))
            {
                // If it does not contains a dot, it is not
                // a valid file (it is certainly a directory)
                fileName = "";
            }
            else
            {
                // Remove the '/' at the start of the string
                fileName = fileName.substring(1);
            }
        }

        return fileName;
    }

    /**
     * Return the directory path (substring the eventual file name at the end of the path)
     * 
     * @param path
     * @return the dir path
     */
    private String getDirectory(String path)
    {
        if ("".equals(getFile(path)))
        {
            return path;
        }
        else
        {
            return path.substring(0, path.lastIndexOf('/'));
        }
    }

    /**
     * Init the field output with the template directory concatenate 
     * with the file name as XXXX_generated.extension
     * @throws GenDocException 
     */
    private void initOutputFile() throws GenDocException
    {
        // replace fileName "XXXX.docx" by "XXXX_generated.docx" if no outputFile specified
        Document template = ((IDocumentService) GendocServices.getDefault().getService(IDocumentService.class)).getDocument();
        if (!"file".equals(template.getDocumentURL().getProtocol()))
        {
            throw new SourceException(String.format("Your input document %s uses the protocol %s please specify an output path in config tag",template.getDocumentURL().getPath(),template.getDocumentURL().getProtocol()));
        }
        String fileName = template.getPath();
        output = addGeneratedPart(fileName);
    }

    public String getParameter(String name)
    {
        if (name != null)
        {
            return parameters.get(name.toLowerCase());
        }
        else
        {
            return "";
        }
    }

    public void addParameter(String name, String value)
    {
        if (name != null)
        {
            parameters.put(name.toLowerCase(), value);
            addParameterToRegistry(name, value);
        }
    }

    public String getImportedDiagrams()
    {
        return importedDiagrams;
    }

    public void setImportedDiagrams(String importedDiagrams)
    {
        this.importedDiagrams = importedDiagrams;
    }

    private String addGeneratedPart(String filePath)
    {
        StringBuffer buffer = new StringBuffer(filePath);
        buffer.insert(buffer.lastIndexOf("."), "_generated");
        return buffer.toString();
    }
    
 
    protected void addParameterToRegistry(String key, String value)
    {
        IRegistryService registry = GendocServices.getDefault().getService(IRegistryService.class);
        if (registry != null)
        {
            if (key != null)
            {
                registry.put(key, value);
            }
        }
    }
   
}