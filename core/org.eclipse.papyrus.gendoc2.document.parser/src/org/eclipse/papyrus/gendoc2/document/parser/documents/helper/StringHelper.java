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
package org.eclipse.papyrus.gendoc2.document.parser.documents.helper;

import java.util.HashMap;

import org.eclipse.papyrus.gendoc2.document.parser.Activator;

/**
 * The Class StringHelper.
 */
public final class StringHelper
{
    public static String QUOTE = "'";
    public static String STRANGE_QUOTE = "�" ;

    /**
     * Instantiates a new string helper.
     */
    private StringHelper()
    {
    }

    /**
     * Gets the attributes in xml string.
     * 
     * @param s the string to examine
     * @param charToStop the char to determine the attribute limit for example
     *            --> '
     * @return the attributes in xml string
     */
    public static HashMap < String, String > getAttributesInXMLString(String s, String... charToStop)
    {
        HashMap < String, String > map = new HashMap < String, String >();
        boolean contains = false ;
        for (String tmp : charToStop)
        {
            contains = contains || s.contains(tmp);
        }
        if (contains)
        {
            for (int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                if (c == '=')
                {
                    try  
                    {
                        CustomCouple couple = getKeyValue(s, i, charToStop);
                        map.put(couple.key, couple.value);
                    }
                    catch (Exception e)
                    {
                        Activator.log(new Exception(
                                String.format("Your input document should have a problem with tag %s please check if it well formed particularly the quotes",s)
                        ));
                    }
                }
            }
        }
        else
        {
            Activator.log(new Exception(
                    String.format("Your input document should have a problem with tag %s please check if it well formed particularly the quotes",s)
            ));
        }
        return map;
    }

    public static boolean equals (char input, String... toCheck)
    {
        for (String c : toCheck)
        {
            if (input == c.charAt(0))
            {
                return true ;
            }
        }
        return false;
    }
    
    /**
     * Gets the key value.
     * 
     * @param stmp the stmp
     * @param rank the rank
     * @param charToStop the char to stop
     * 
     * @return the key value
     */
    private static CustomCouple getKeyValue(String stmp, int rank, String... charToStop)
    {
        int i = rank;
        int nbBlank = 0;
        while (stmp.charAt(i) == ' ')
        {
            i--;
            nbBlank++;
        }
        while (i > 0 && stmp.charAt(i) != ' ')
        {
            i--;
        }
        String key = stmp.substring(i + 1, rank - nbBlank);
        nbBlank = 0;
        i = rank;
        while (i < stmp.length() && (!equals(stmp.charAt(i),charToStop)) || specialCharacter(stmp.substring(i-1,i+1),charToStop)) /*
                                                                              * &&
                                                                              * (
                                                                              * stmp
                                                                              * .
                                                                              * charAt
                                                                              * (
                                                                              * i
                                                                              * )
                                                                              * !=
                                                                              * '['
                                                                              * )
                                                                              */
        {
            i++;
            nbBlank++;
        }
        i++;
        nbBlank++;
        while (i < stmp.length() && (!equals(stmp.charAt(i),charToStop) || specialCharacter(stmp.substring(i-1,i+1),charToStop))) /*
                                                                              * &&
                                                                              * (
                                                                              * stmp
                                                                              * .
                                                                              * charAt
                                                                              * (
                                                                              * i
                                                                              * )
                                                                              * !=
                                                                              * ']'
                                                                              * )
                                                                              */
        {
            i++;
        }
        String value = stmp.substring(rank + nbBlank, i);
        value = replace(value,charToStop);
        CustomCouple c = new CustomCouple();
        c.key = key;
        c.value = value;

        return c;
    }

    /**
     * Replace the \ char to stop to charToStop
     * @param value
     * @param charToStop
     * @return
     */
    private static String replace(String value, String[] charToStop)
    {
        String result = value ;
        for (String s : charToStop)
        {
            result = result.replace("\\" + s, s);
        }
        return result ;
    }

    private static boolean specialCharacter(String input, String[] toCheck)
    {
        for (String c : toCheck)
        {
            if (("\\" + c).equals(input))
            {
                return true ;
            }
        }
        return false;
    }

    /**
     * The Class CustomCouple.
     */
    private static class CustomCouple
    {
        /** The key. */
        private String key;

        /** The value. */
        private String value;

        /**
         * Gets the key.
         * 
         * @return the key
         */
        public String getKey()
        {
            return key;
        }

        /**
         * Sets the key.
         * 
         * @param key the new key
         */
        public void setKey(String key)
        {
            this.key = key;
        }

        /**
         * Gets the value.
         * 
         * @return the value
         */
        public String getValue()
        {
            return value;
        }

        /**
         * Sets the value.
         * 
         * @param value the new value
         */
        public void setValue(String value)
        {
            this.value = value;
        }
    }
}
