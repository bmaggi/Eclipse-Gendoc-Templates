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
 * Kris Robertson (Atos Origin) kris.robertson@atosorigin.com - Initial API and implementation
 * Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com - add all development on the id 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.tags.handlers;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
//import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
//import org.eclipse.osgi.internal.debug.FrameworkDebugOptions;
//import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.papyrus.gendoc2.documents.IDocumentService;
import org.eclipse.papyrus.gendoc2.services.GendocServices;
import org.eclipse.papyrus.gendoc2.services.IGendocDiagnostician;
import org.eclipse.papyrus.gendoc2.services.ILogger;
import org.eclipse.papyrus.gendoc2.services.exception.GenDocException;
import org.eclipse.papyrus.gendoc2.services.exception.IncompleteTagException;

import org.eclipse.papyrus.gendoc2.tags.ITag;
import org.eclipse.papyrus.gendoc2.tags.ITagExtensionService;
import org.eclipse.papyrus.gendoc2.tags.ITagHandler;
import org.eclipse.papyrus.gendoc2.tags.TagsExtensionPoint;
import org.eclipse.papyrus.gendoc2.tags.handlers.impl.RegisteredTags;
import org.eclipse.papyrus.gendoc2.tags.parsers.ITagParserService;


/**
 * Abstract base class for tag handlers that provides default method implementations.
 * 
 * @author Kris Robertson
 */
public abstract class AbstractTagHandler implements ITagHandler, IExecutableExtension
{

    private String tagName;

    /**
     * Returns the name of the tag that this is the handler for.
     * 
     * @return the tag name
     */
    public String getTagName()
    {
        return this.tagName;
    }

    /**
     * Returns true if this is the handler for the specified tag.
     * 
     * @param tag the tag
     * 
     * @return true if this is the handler for the specified tag, otherwise false
     */
    public boolean isHandlerFor(ITag tag)
    {
        return this.tagName.equals(tag.getName());
    }

    /**
     * Runs the handler for the specified tag. The default implementation calls <samp>runAttributes(ITag,
     * String)</samp>, <samp>runScripts(ITag, String)</samp>, and <samp>runChildren(ITag, String)</samp> in order.
     * @throws GenDocException 
     */
    public String run(ITag tag) throws GenDocException
    {
    	IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);
    	 ILogger logger = GendocServices.getDefault().getService(ILogger.class);
    	 String value="";
    	 // Since org.eclipse.osgi.framework.debug.Debug is no more supported in eclipse Luna, the logging service does do anything
      try{ 
    	      	  
        
        
//    	if (true)
//        {
//            StringBuffer buffer = new StringBuffer("AbstractTagHandler.run -> Tag :");
//            buffer.append(tag.getName());
//            buffer.append("  value :");
//            buffer.append(tag.getValue());
//            buffer.append("\n");
//            buffer.append("Attributes  :\n");
//            for (String key : tag.getAttributes().keySet())
//            {
//                buffer.append(" - ");
//                buffer.append(key);
//                buffer.append(":");
//                buffer.append(tag.getAttributes().get(key));
//                buffer.append("\n");
//            }
//            logger.log(buffer.toString(), ILogger.DEBUG);
//        }
     
        // Check tag validity before running tag attributes
        if (!tag.isComplete())
        {
            throw new IncompleteTagException(tag.getName());
         
      }

         value = this.runAttributes(tag, tag.getValue());
        //if (Debug.DEBUG_ENABLED)

//        {
//            StringBuffer buffer = new StringBuffer("AbstractTagHandler.run -> Tag :");
//            buffer.append(tag.getName());
//            buffer.append(" value after RunAttributes : ");
//            buffer.append(value);
//            buffer.append("\n");
//            logger.log(buffer.toString(), ILogger.DEBUG);
//        }
        value = this.runScripts(tag, value);
       // if (Debug.DEBUG_ENABLED)

//        {
//            StringBuffer buffer = new StringBuffer("AbstractTagHandler.run -> Tag :");
//            buffer.append(tag.getName());
//            buffer.append(" value after RunScript : ");
//            buffer.append(value);
//            buffer.append("\n");
//            logger.log(buffer.toString(), ILogger.DEBUG);
//        }
        value = this.runChildren(tag, value);
       // if (Debug.DEBUG_ENABLED)
       
//        {
//            StringBuffer buffer = new StringBuffer("AbstractTagHandler.run -> Tag :");
//            buffer.append(tag.getName());
//            buffer.append(" value after RunChildren : ");
//            buffer.append(value);
//            buffer.append("\n");
//            logger.log(buffer.toString(), ILogger.DEBUG);
//        }
        
        
        
      }
        catch (GenDocException e)
        {
            // if the id of tag is not found, tagIdFound will be null

            String tagIdFound = tag.getAttributes().get(RegisteredTags.ID);
            String message= e.getUIMessage();

            if (null == tagIdFound)
            {
                throw e;
            }
            else
            {
                // creation of diagnostician

                message= "The execution of tag with id '"+tagIdFound+"' failed : " + e.getUIMessage();
                logger.log(message, ILogger.DEBUG);
                diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0, message, null));
            }
        }
        return value;

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.papyrus.gendoc2.tags.ITagHandler#setInitializationData(org.eclipse .core.runtime.IConfigurationElement)
     */
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException
    {
        this.tagName = config.getAttribute(TagsExtensionPoint.TAG_NAME);
    }

    /**
     * Process the attributes of the tag. This method should be overridden by sub-classes that need to process tag
     * attributes. The default implementation simply returns the value as-is.
     * 
     * @param tag the tag to process
     * @param value the value of the tag
     * 
     * @return the new value of the tag
     * 
     * @throws GenDocException
     */
    protected String runAttributes(ITag tag, String value) throws GenDocException
    {
        return value;
    }

    /**
     * Process the child tags. This method parses the tag value and runs a handler for each sub-tag that is found.
     * 
     * @param tag the tag
     * @param value the tag value
     * 
     * @return the new tag value after child handlers have been run
     * 
     * @throws GenDocException
     */
    protected String runChildren(ITag tag, String value) throws GenDocException
    {
        IDocumentService documentService = GendocServices.getDefault().getService(IDocumentService.class);
        ITagExtensionService tagExtensionService = GendocServices.getDefault().getService(ITagExtensionService.class);
        ITagHandlerService tagHandlerService = GendocServices.getDefault().getService(ITagHandlerService.class);
        ITagParserService tagParserService = GendocServices.getDefault().getService(ITagParserService.class);

        List<String> subTagNames = tagExtensionService.getTagExtension(this.getTagName()).getSubTagNames();
        String cleanedTagContent = documentService.cleanTagContent(value, subTagNames);
        List<ITag> tags = tagParserService.parse(tag, cleanedTagContent, subTagNames);

        StringBuffer tagValue = new StringBuffer();

        IGendocDiagnostician diagnostician = GendocServices.getDefault().getService(IGendocDiagnostician.class);

        for (ITag subTag : tags)
        {
            ITagHandler tagHandler = tagHandlerService.getHandlerFor(subTag);
            try{
                if (tagHandler != null)
                {
                    tagValue.append(tagHandler.run(subTag));
                }
                else
                {
                    tagValue.append(subTag.getRawText());
                }
            }
            catch (GenDocException e)
            {
                // if the id of tag is not found, tagIdFound will be null

                String subTagIdFound = subTag.getAttributes().get(RegisteredTags.ID);
                String message= e.getUIMessage();

                if (null == subTagIdFound)
                {
                    throw e;
                }
                else
                {
                    // creation of diagnostician

                    message= "The execution of tag with id '"+subTagIdFound+"' failed : " + e.getUIMessage();
                    //logger.log(message, ILogger.DEBUG);
                    diagnostician.addDiagnostic(new BasicDiagnostic(Diagnostic.ERROR, Activator.PLUGIN_ID, 0, message, null));
                }
            }

        }

       
        return tagValue.toString();
    }

    /**
     * Process scripts within the tag. This method should be overridden by handlers that need to process scripts within
     * the tag. The default implementation simply returns the value as-is.
     * 
     * @param tag the tag
     * @param value the tag value
     * 
     * @return the new value of the tag
     * 
     * @throws GenDocException
     */
    protected String runScripts(ITag tag, String value) throws GenDocException
    {
        return value;
    }

}
