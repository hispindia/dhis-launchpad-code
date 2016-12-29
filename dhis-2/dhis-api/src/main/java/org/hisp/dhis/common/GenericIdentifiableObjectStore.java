package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface GenericIdentifiableObjectStore<T>
    extends GenericStore<T>
{
    /**
     * Retrieves the object with the given uid.
     *
     * @param uid the uid.
     * @return the object with the given uid.
     */
    T getByUid( String uid );

    /**
     * Retrieves the object with the given name.
     *
     * @param name the name.
     * @return the object with the given name.
     */
    T getByName( String name );

    /**
     * Retrieves the object with the given code.
     * 
     * @param name the code.
     * @return the object with the given code.
     */
    T getByCode( String code );
    
    /**
     * Retrieves the objects determined by the given first result and max result.
     * 
     * @param first the first result object to return.
     * @param max the max number of result objects to return. 
     * @return collection of objects.
     */
    Collection<T> getBetween( int first, int max );

    /**
     * Retrieves the objects determined by the given first result and max result
     * which name is like the given name.
     * 
     * @param the name which result object names must be like.
     * @param first the first result object to return.
     * @param max the max number of result objects to return. 
     * @return collection of objects.
     */    
    Collection<T> getBetweenByName( String name, int first, int max );
    
    /**
     * Gets the count of objects which name is like the given name.
     * 
     * @param name the name which result object names must be like.
     * @return the count of objects.
     */
    int getCountByName( String name );
}
