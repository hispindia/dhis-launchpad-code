package org.hisp.dhis.webapi.controller;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.dxf2.datavalueset.DataValueSet;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.view.ClassPathUriResolver;
import org.hisp.dhis.webapi.webdomain.DataValueSets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.webapi.utils.ContextUtils.*;

@Controller
@RequestMapping( value = DataValueSetController.RESOURCE_PATH )
public class DataValueSetController
{
    public static final String RESOURCE_PATH = "/dataValueSets";
    public static final String SDMXCROSS2DXF2_TRANSFORM = "/templates/cross2dxf2.xsl";

    private static final Log log = LogFactory.getLog( DataValueSetController.class );

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Get
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_XML )
    public void getDataValueSetXml(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_XML );

        boolean isSingleDataValueSet = dataSet.size() == 1 && period != null && orgUnit.size() == 1;

        if ( isSingleDataValueSet )
        {
            String ds = dataSet.iterator().next();
            String ou = orgUnit.iterator().next();

            log.info( "Get XML data value set for data set: " + ds + ", period: " + period + ", org unit: " + ou );

            dataValueSetService.writeDataValueSetXml( ds, period, ou, response.getOutputStream() );
        }
        else
        {
            log.info( "Get XML bulk data value set for start date: " + startDate + ", end date: " + endDate );

            dataValueSetService.writeDataValueSetXml( dataSet, startDate, endDate, orgUnit, children, response.getOutputStream() );
        }
    }

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_JSON )
    public void getDataValueSetJson(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_JSON );

        boolean isSingleDataValueSet = dataSet.size() == 1 && period != null && orgUnit.size() == 1;

        if ( isSingleDataValueSet )
        {
            String ds = dataSet.iterator().next();
            String ou = orgUnit.iterator().next();

            log.info( "Get JSON data value set for data set: " + ds + ", period: " + period + ", org unit: " + ou );

            dataValueSetService.writeDataValueSetJson( ds, period, ou, response.getOutputStream() );
        }
        else
        {
            log.info( "Get JSON bulk data value set for start date: " + startDate + ", end date: " + endDate );

            dataValueSetService.writeDataValueSetJson( dataSet, startDate, endDate, orgUnit, children, response.getOutputStream() );
        }
    }

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_CSV )
    public void getDataValueSetCsv(
        @RequestParam Set<String> dataSet,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response ) throws IOException
    {
        log.info( "Get CSV bulk data value set for start date: " + startDate + ", end date: " + endDate );

        response.setContentType( CONTENT_TYPE_CSV );
        dataValueSetService.writeDataValueSetCsv( dataSet, startDate, endDate, orgUnit, children, response.getWriter() );
    }

    // -------------------------------------------------------------------------
    // Post
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = "application/xml" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postDxf2DataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws IOException
    {
        ImportSummary summary = dataValueSetService.saveDataValueSet( in, importOptions );

        log.info( "Data values set saved " + importOptions );

        response.setContentType( CONTENT_TYPE_XML );
        JacksonUtils.toXml( response.getOutputStream(), summary );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postJsonDataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws IOException
    {
        ImportSummary summary = dataValueSetService.saveDataValueSetJson( in, importOptions );

        log.info( "Data values set saved " + importOptions );

        response.setContentType( CONTENT_TYPE_JSON );
        JacksonUtils.toJson( response.getOutputStream(), summary );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/sdmx+xml" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postSDMXDataValueSet( ImportOptions importOptions,
        HttpServletResponse response, InputStream in, Model model ) throws
        IOException, TransformerConfigurationException, TransformerException
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setURIResolver( new ClassPathUriResolver() );

        Transformer transformer = tf.newTransformer( new StreamSource( new ClassPathResource( SDMXCROSS2DXF2_TRANSFORM ).getInputStream() ) );

        StringWriter dxf2 = new StringWriter();
        transformer.transform( new StreamSource( in ), new StreamResult( dxf2 ) );

        importOptions.setOrgUnitIdScheme( "CODE" ); // Override id scheme
        importOptions.setDataElementIdScheme( "CODE" );

        dataValueSetService.saveDataValueSetJson(
            new ByteArrayInputStream( dxf2.toString().getBytes( "UTF-8" ) ), importOptions );
    }

    // -------------------------------------------------------------------------
    // Supportive
    // -------------------------------------------------------------------------

    @ExceptionHandler( IllegalArgumentException.class )
    public void handleError( IllegalArgumentException ex, HttpServletResponse response )
    {
        ContextUtils.conflictResponse( response, ex.getMessage() );
    }
}
