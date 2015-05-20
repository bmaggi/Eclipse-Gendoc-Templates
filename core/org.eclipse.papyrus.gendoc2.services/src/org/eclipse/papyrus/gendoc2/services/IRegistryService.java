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
 * 
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.services;

/**
 * The registry service provides a map between keys and values. The map cannot contain duplicate keys; each key can map
 * to at most one value. The registry service provides a shared storage space that can be accessed throughout Gendoc
 * process.
 * 
 * @author Kris Robertson
 */
public interface IRegistryService extends IService
{

    /**
     * Adds a runnable cleaner to the registry. The runnable will be called before the service is cleared. This allows
     * clients to dispose of any resources before the service itself is cleared.
     * 
     * @param cleaner the runnable cleaner to add
     */
    void addCleaner(Runnable cleaner);

    /**
     * Returns true if this registry contains a mapping for the specified key.
     * 
     * @param key key whose presence is to be tested.
     * 
     * @return if this registry contains a mapping for the specified key.
     */
    boolean containsKey(Object key);

    /**
     * Returns the value to which maps to the specified key. Returns null if there is no mapping for this key. A return
     * value of null does not necessarily indicate that there is no mapping for the key; it's possible that the key
     * explicitly maps to null. The containsKey operation may be used to distinguish these two cases.
     * 
     * @param key key whose associated value is to be returned
     * 
     * @return the value for the specified key, or null if there is no mapping for this key.
     */
    Object get(Object key);

    /**
     * Associates the specified value with the specified key. If a previous mapping for this key existed, the old value
     * is replaced by the specified value.
     * 
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * 
     * @return the previous value associated with specified key, or null if there was no mapping for key.
     */
    Object put(Object key, Object value);

}
