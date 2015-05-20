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
 * * Papa Malick WADE (Atos Origin) papa-malick.wade@atosorigin.com - development on the id ( new constructor with id)
 *****************************************************************************/

package org.eclipse.papyrus.gendoc2.services.exception;

public class IncompleteTagException extends GenDocException
{

    private static final long serialVersionUID = -2688330129075336697L;

    /**
     * Instantiates a new incomplete tag exception.
     * 
     * @param tagLabel the tag label
     */
    public IncompleteTagException(String tagLabel)
    {
        super("A tag \"" + tagLabel + "\" is incomplete");
    }
  
    public IncompleteTagException(String tagLabel, String tagId)
    {
        super("A tag \"" + tagLabel + "\" with id = '"+ tagId+"' is incomplete.");
    }

    /**
     * Instantiates a new incomplete tag exception.
     * 
     * @param tagLabel the tag label
     */
    public IncompleteTagException(String tagLabel, int tagIndex)
    {
        super("The " + displayTagIndex(tagIndex) + " tag \"" + tagLabel + "\" is incomplete");
    }

    /**
     * Instantiates a new incomplete tag exception.
     * 
     * @param tagLabel the tag label
     * @param tagIndex the index of the incomplete tag
     */
    public IncompleteTagException(String tagLabel, int tagIndex, String comment)
    {
        super("The " + displayTagIndex(tagIndex) + " tag \"" + tagLabel + "\" is incomplete : " + comment);
    }

    /**
     * Transform index to Text
     * 
     * @param tagIndex tagIndex
     * @return
     */
    private static String displayTagIndex(int tagIndex)
    {
        switch (tagIndex)
        {
            case 1:
                return "1st";
            case 2:
                return "2nd";
            default:
                return tagIndex + "th";
        }
    }
}
