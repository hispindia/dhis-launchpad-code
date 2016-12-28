package org.hisp.dhis.schema;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.annotation.Description;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Default PropertyIntrospectorService implementation that uses Reflection and Jackson annotations
 * for reading in properties.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Jackson2PropertyIntrospectorService extends AbstractPropertyIntrospectorService
{
    protected Map<String, Property> scanClass( Class<?> clazz )
    {
        Map<String, Property> propertyMap = Maps.newHashMap();

        // TODO this is quite nasty, should find a better way of exposing properties at class-level
        if ( clazz.isAnnotationPresent( JacksonXmlRootElement.class ) )
        {
            Property property = new Property();

            JacksonXmlRootElement jacksonXmlRootElement = clazz.getAnnotation( JacksonXmlRootElement.class );

            if ( !StringUtils.isEmpty( jacksonXmlRootElement.localName() ) )
            {
                property.setName( jacksonXmlRootElement.localName() );
            }

            if ( !StringUtils.isEmpty( jacksonXmlRootElement.namespace() ) )
            {
                property.setNamespace( jacksonXmlRootElement.namespace() );
            }

            propertyMap.put( "__self__", property );
        }

        List<Property> properties = collectProperties( clazz );

        for ( Property property : properties )
        {
            Method method = property.getGetterMethod();
            JsonProperty jsonProperty = method.getAnnotation( JsonProperty.class );

            String name = jsonProperty.value();

            if ( StringUtils.isEmpty( name ) )
            {
                String[] getters = new String[]{
                    "is", "has", "get"
                };

                name = method.getName();

                for ( String getter : getters )
                {
                    if ( name.startsWith( getter ) )
                    {
                        name = name.substring( getter.length() );
                    }
                }

                name = StringUtils.uncapitalize( name );
            }

            property.setName( name );

            if ( method.isAnnotationPresent( Description.class ) )
            {
                Description description = method.getAnnotation( Description.class );
                property.setDescription( description.value() );
            }

            if ( method.isAnnotationPresent( JsonView.class ) )
            {
                JsonView jsonView = method.getAnnotation( JsonView.class );

                for ( Class<?> klass : jsonView.value() )
                {
                    if ( ExportView.class.isAssignableFrom( klass ) )
                    {
                        property.setOwner( true );
                        break;
                    }
                }
            }
            else
            {
                property.setOwner( true );
            }

            if ( method.isAnnotationPresent( JacksonXmlProperty.class ) )
            {
                JacksonXmlProperty jacksonXmlProperty = method.getAnnotation( JacksonXmlProperty.class );

                if ( StringUtils.isEmpty( jacksonXmlProperty.localName() ) )
                {
                    property.setName( name );
                }
                else
                {
                    property.setName( jacksonXmlProperty.localName() );
                }

                if ( !StringUtils.isEmpty( jacksonXmlProperty.namespace() ) )
                {
                    property.setNamespace( jacksonXmlProperty.namespace() );
                }

                property.setAttribute( jacksonXmlProperty.isAttribute() );
            }

            if ( method.isAnnotationPresent( JacksonXmlElementWrapper.class ) )
            {
                JacksonXmlElementWrapper jacksonXmlElementWrapper = method.getAnnotation( JacksonXmlElementWrapper.class );

                // TODO what if element-wrapper have different namespace?
                if ( !StringUtils.isEmpty( jacksonXmlElementWrapper.localName() ) )
                {
                    property.setCollectionName( jacksonXmlElementWrapper.localName() );
                }
            }

            propertyMap.put( name, property );

            Class<?> returnType = method.getReturnType();
            property.setKlass( returnType );

            if ( Collection.class.isAssignableFrom( returnType ) )
            {
                property.setCollection( true );

                Type type = method.getGenericReturnType();

                if ( ParameterizedType.class.isInstance( type ) )
                {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Class<?> klass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    property.setItemKlass( klass );

                    if ( collectProperties( klass ).isEmpty() )
                    {
                        property.setSimple( true );
                    }

                    if ( IdentifiableObject.class.isAssignableFrom( klass ) )
                    {
                        property.setIdentifiableObject( true );

                        if ( NameableObject.class.isAssignableFrom( klass ) )
                        {
                            property.setNameableObject( true );
                        }
                    }
                }
            }
            else
            {
                if ( collectProperties( returnType ).isEmpty() )
                {
                    property.setSimple( true );
                }
            }
        }

        return propertyMap;
    }

    private static List<Property> collectProperties( Class<?> klass )
    {
        List<Method> allMethods = ReflectionUtils.getAllMethods( klass );
        List<Property> properties = Lists.newArrayList();

        for ( Method method : allMethods )
        {
            if ( method.isAnnotationPresent( JsonProperty.class ) )
            {
                if ( method.getGenericParameterTypes().length == 0 )
                {
                    properties.add( new Property( klass, method, null ) );
                }
            }
        }

        return properties;
    }
}
