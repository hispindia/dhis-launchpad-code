package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.dxf2.schema.SchemaValidator;
import org.hisp.dhis.dxf2.schema.ValidationViolation;
import org.hisp.dhis.dxf2.schema.ValidationViolations;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.schema.Schemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/schemas", method = RequestMethod.GET )
public class SchemaController
{
    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SchemaValidator schemaValidator;

    @Autowired
    private RenderService renderService;

    @RequestMapping
    public @ResponseBody Schemas getSchemas()
    {
        return new Schemas( schemaService.getSortedSchemas() );
    }

    @RequestMapping( value = "/{type}", method = RequestMethod.GET )
    public @ResponseBody Schema getSchema( @PathVariable String type )
    {
        Schema schema = getSchemaFromType( type );

        if ( schema != null )
        {
            return schema;
        }

        throw new HttpClientErrorException( HttpStatus.NOT_FOUND, "Type " + type + " does not exist." );
    }

    @RequestMapping( value = "/{type}", method = { RequestMethod.POST, RequestMethod.PUT }, consumes = MediaType.APPLICATION_JSON_VALUE )
    public void validateSchemaJson( @PathVariable String type, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Schema schema = getSchemaFromType( type );

        if ( schema == null )
        {
            throw new HttpClientErrorException( HttpStatus.NOT_FOUND, "Type " + type + " does not exist." );
        }

        Object object = renderService.fromJson( request.getInputStream(), schema.getKlass() );
        List<ValidationViolation> validationViolations = schemaValidator.validate( object );

        renderService.toJson( response.getOutputStream(), validationViolations );
    }

    @RequestMapping( value = "/{type}", method = { RequestMethod.POST, RequestMethod.PUT }, consumes = MediaType.APPLICATION_XML_VALUE )
    public void validateSchemaXml( @PathVariable String type, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        Schema schema = getSchemaFromType( type );

        if ( schema == null )
        {
            throw new HttpClientErrorException( HttpStatus.NOT_FOUND, "Type " + type + " does not exist." );
        }

        Object object = renderService.fromXml( request.getInputStream(), schema.getKlass() );
        List<ValidationViolation> validationViolations = schemaValidator.validate( object );

        renderService.toXml( response.getOutputStream(), new ValidationViolations( validationViolations ) );
    }

    @RequestMapping( value = "/{type}/{property}", method = RequestMethod.GET )
    public @ResponseBody Property getSchemaProperty( @PathVariable String type, @PathVariable String property )
    {
        Schema schema = getSchemaFromType( type );

        if ( schema == null )
        {
            throw new HttpClientErrorException( HttpStatus.NOT_FOUND, "Type " + type + " does not exist." );
        }

        if ( schema.getPropertyMap().containsKey( property ) )
        {
            return schema.getPropertyMap().get( property );
        }

        throw new HttpClientErrorException( HttpStatus.NOT_FOUND, "Property " + property + " does not exist on type " + type + "." );
    }

    private Schema getSchemaFromType( String type )
    {
        Schema schema = schemaService.getSchemaBySingularName( type );

        if ( schema == null )
        {
            try
            {
                schema = schemaService.getSchema( Class.forName( type ) );
            }
            catch ( ClassNotFoundException ignored )
            {
            }
        }

        return schema;
    }
}
