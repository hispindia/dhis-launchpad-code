package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2011, University of Oslo
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.mapping.Maps;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.api.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping( value = MapController.RESOURCE_PATH )
public class MapController
{
    public static final String RESOURCE_PATH = "/maps";
    
    @Autowired
    private MappingService mappingService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private MapGenerationService mapGenerationService;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getMaps(IdentifiableObjectParams params, Model model, HttpServletRequest request ) throws IOException
    {
        Maps maps = new Maps();
        maps.setMaps( new ArrayList<MapView>( mappingService.getAllMapViews() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( maps );
        }
        
        model.addAttribute( "model", maps );

        return "maps";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getMap( @PathVariable String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        MapView mapView = mappingService.getMapView( uid );
        
        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( mapView );
        }

        model.addAttribute( "model", mapView );

        return "map";
    }
    
    @RequestMapping( value = {"/{uid}/data","/{uid}/data.png"}, method = RequestMethod.GET )
    public void getMap( @PathVariable String uid, HttpServletResponse response ) throws Exception
    {
        MapView mapView = mappingService.getMapView( uid );
        
        renderMapViewPng( mapView, response );
    }
    
    @RequestMapping( value = {"/data","/data.png"}, method = RequestMethod.GET )
    public void getMap( Model model,
                        @RequestParam( value = "in" ) String indicatorUid, 
                        @RequestParam( value = "ou" ) String organisationUnitUid,
                        @RequestParam( value = "level", required = false ) Integer level,
                        HttpServletResponse response ) throws Exception
    {
        if ( level == null )
        {
            OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitUid );
            
            level = organisationUnitService.getLevelOfOrganisationUnit( unit.getId() );
            level++;
        }
        
        MapView mapView = mappingService.getIndicatorLastYearMapView( indicatorUid, organisationUnitUid, level );
        
        renderMapViewPng( mapView, response );
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postMapXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postMapJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putMapXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putMapJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteMap( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // Supportive methods
    //-------------------------------------------------------------------------------------------------------

    private void renderMapViewPng( MapView mapView, HttpServletResponse response )
        throws Exception
    {
        BufferedImage image = mapGenerationService.generateMapImage( mapView );
        
        if ( image != null )
        {
            response.setContentType( ContextUtils.CONTENT_TYPE_PNG );
            ImageIO.write( image, "PNG", response.getOutputStream() );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
    }
}
