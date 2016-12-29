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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

@Controller
@RequestMapping( value = DimensionController.RESOURCE_PATH )
public class DimensionController extends AbstractCrudController<DimensionalObject>
{
    public static final String RESOURCE_PATH = "/dimensions";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    // -------------------------------------------------------------------------
    // Controller
    // -------------------------------------------------------------------------

    @Override
    protected List<DimensionalObject> getEntityList( WebMetaData metaData, WebOptions options, List<String> filters )
    {
        return dimensionService.getAllDimensions();
    }

    @Override
    protected List<DimensionalObject> getEntity( String uid, WebOptions options )
    {
        return Lists.newArrayList( dimensionService.getDimensionalObjectCopy( uid, true ) );
    }

    @RequestMapping( value = "/{uid}/items", method = RequestMethod.GET )
    public String getItems( @PathVariable String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response )
    {
        WebOptions options = new WebOptions( parameters );
        List<NameableObject> items = dimensionService.getCanReadDimensionItems( uid );

        if ( parameters.containsKey( "filter" ) )
        {
            String filter = parameters.get( "filter" );

            if ( filter.startsWith( "name:like:" ) )
            {
                filter = filter.substring( "name:like:".length() );

                Iterator<NameableObject> iterator = items.iterator();

                while ( iterator.hasNext() )
                {
                    NameableObject nameableObject = iterator.next();

                    if ( !nameableObject.getName().toLowerCase().contains( filter.toLowerCase() ) )
                    {
                        iterator.remove();
                    }
                }
            }
        }

        Collections.sort( items, IdentifiableObjectNameComparator.INSTANCE );

        WebMetaData metaData = new WebMetaData();
        metaData.setItems( items );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "items";
    }

    @RequestMapping( value = "/{uid}/items", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getItemsJson( @PathVariable String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        List<NameableObject> items = dimensionService.getCanReadDimensionItems( uid );

        if ( parameters.containsKey( "filter" ) )
        {
            String filter = parameters.get( "filter" );

            if ( filter.startsWith( "name:like:" ) )
            {
                filter = filter.substring( "name:like:".length() );

                Iterator<NameableObject> iterator = items.iterator();

                while ( iterator.hasNext() )
                {
                    NameableObject nameableObject = iterator.next();

                    if ( !nameableObject.getName().toLowerCase().contains( filter.toLowerCase() ) )
                    {
                        iterator.remove();
                    }
                }
            }
        }

        Collections.sort( items, IdentifiableObjectNameComparator.INSTANCE );

        Map<String, List<?>> output = new HashMap<>();
        List<Map<?, ?>> itemCollection = new ArrayList<>();
        output.put( "items", itemCollection );

        for ( NameableObject item : items )
        {
            Map<String, Object> o = new HashMap<>();
            o.put( "id", item.getUid() );
            o.put( "name", item.getName() );

            itemCollection.add( o );
        }

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        renderService.toJson( response.getOutputStream(), output );
    }

    @RequestMapping( value = "/constraints", method = RequestMethod.GET )
    public String getDimensionConstraints( @RequestParam( value = "links", defaultValue = "true", required = false ) Boolean links,
        Model model )
    {
        WebMetaData metaData = new WebMetaData();

        metaData.setDimensions( dimensionService.getDimensionConstraints() );

        model.addAttribute( "model", metaData );

        if ( links )
        {
            linkService.generateLinks( metaData, false );
        }

        return "dimensions";
    }

    @RequestMapping( value = "/dataSet/{uid}", method = RequestMethod.GET )
    public String getDimensionsForDataSet( @PathVariable String uid,
        @RequestParam( value = "links", defaultValue = "true", required = false ) Boolean links,
        Model model, HttpServletResponse response )
    {
        WebMetaData metaData = new WebMetaData();

        DataSet dataSet = identifiableObjectManager.get( DataSet.class, uid );

        if ( dataSet == null )
        {
            ContextUtils.notFoundResponse( response, "Data set does not exist: " + uid );
            return null;
        }

        if ( !dataSet.hasCategoryCombo() )
        {
            ContextUtils.conflictResponse( response, "Data set does not have a category combination: " + uid );
            return null;
        }

        List<DimensionalObject> dimensions = new ArrayList<>();
        dimensions.addAll( dataSet.getCategoryCombo().getCategories() );
        dimensions.addAll( dataSet.getCategoryOptionGroupSets() );

        dimensions = dimensionService.getCanReadObjects( dimensions );

        for ( DimensionalObject dim : dimensions )
        {
            metaData.getDimensions().add( dimensionService.getDimensionalObjectCopy( dim.getUid(), true ) );
        }

        model.addAttribute( "model", metaData );

        if ( links )
        {
            linkService.generateLinks( metaData, false );
        }

        return "dimensions";
    }
}
