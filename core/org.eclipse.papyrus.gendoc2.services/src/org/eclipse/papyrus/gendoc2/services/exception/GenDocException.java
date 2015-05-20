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
 *  Maxime Leray (Atos Origin) maxime.leray@atosorigin.com - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services.exception;

/**
 * The Class GenDocException.
 */
public abstract class GenDocException extends Exception
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5488973275629846167L;

    private String uiMessage = "";


    /**
     * Instantiates a new gendoc exception.
     * 
     * @param message the message
     */
    protected GenDocException(String message)
    {
        super(message);
        uiMessage = UIMessages.transformToUIMessage(message);
    }

    /**
     * Instantiates a new gendoc exception.
     * 
     * @param t the throwable
     */
    protected GenDocException(Throwable t)
    {
        super(t);
        uiMessage = UIMessages.transformToUIMessage(t.getMessage());
    }

    /**
     * Instantiates a new gendoc exception.
     * 
     * @param message the message
     * @param t the throwable
     */
    protected GenDocException(String message, Throwable t)
    {
        super(message, t);
        uiMessage = UIMessages.transformToUIMessage(message);
    }
    
    public void setUIMessage(String uiMessage)
    {
        this.uiMessage = uiMessage;
    }

    /**
     * Get the message to display to user
     * @return
     */
    public String getUIMessage()
    {
        return this.uiMessage;
    }

}
