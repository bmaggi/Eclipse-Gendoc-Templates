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

package org.eclipse.papyrus.gendoc2.tags.parsers;

/**
 * The Interface TagParserConfig.
 * 
 * @author cbourdeu
 */
public interface TagParserConfig
{

    /** The INF. */
    String INF = "&lt;";

    /** The SUP. */
    String SUP = "&gt;";

    /** The SLASH. */
    String SLASH = "/";
    
    char BACKSLASH = '\u005C\';
    char SLASH_CHAR = '\u002F';

    /** The SPACE. */
    char SPACE = ' ';

//    /** The QUOTE. */
    String VALID_QUOTE = "\u0027";
//    /** The INVALI d_ quote s_ regexp. */
    
    String[] INVALID_QUOTES = new String[]{"\u2018","\u2019","\u0060"};

    
    char[] INVALID_DOUBLE_QUOTES = new char[]{'\u201C','\u201D'};
    char VALID_DOUBLE_QUOTE = '\u0022';
    
    /** The EQUAL. */
    String EQUAL = "=";

    /** The EMPTY. */
    String EMPTY = "";

    /** The XM l_ quote. */
    String XML_QUOTE = "�";

    // String QUOTE = "'";
    // String INF = "&lt;";
    // String SUP = "&gt;";
    // String SLASH = "/";
    // String SPACE = " ";

}
