
package org.hisp.dhis.api.controller.equipment;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dataset.DataSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

/**
 * @author Brajesh Murari <brajesh.murari@yahoo.com>
 *
 */

@Controller
@RequestMapping(value = EquipmentTypeController.RESOURCE_PATH)
public class EquipmentTypeController 
	extends AbstractCrudController<EquipmentType>
{
    public static final String RESOURCE_PATH = "/equipmentTypes";

    @RequestMapping( value = "/{uid}/equipmentType_Attributes", method = RequestMethod.GET )
    public String getEquipmentType_Attributes( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = getEntity( uid );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<EquipmentType_Attribute> equipmentType_Attributes = Lists.newArrayList( equipmentType.getEquipmentType_Attributes()) ;

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentType_Attributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentType_Attributes = PagerUtils.pageCollection( equipmentType_Attributes, pager );
        }

        //metaData.setEquipmentType_Attributes(equipmentType_Attributes);
        
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping(value = "/{uid}/equipmentType_Attributes/query/{q}", method = RequestMethod.GET)
    public String getEquipmentType_AttributesByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = getEntity( uid );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        /*
        List<EquipmentType_Attribute> equipmentType_Attributes = Lists.newArrayList( equipmentType.getEquipmentType_Attributes()) ;

        
        for ( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
        {
            if ( equipmentType_Attribute.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
            	equipmentType_Attributes.add( equipmentType_Attribute );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentType_Attributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentType_Attributes = PagerUtils.pageCollection( equipmentType_Attributes, pager );
        }

        metaData.setEquipmentType_Attributes(equipmentType_Attributes );
*/
        
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
    
    @RequestMapping( value = "/{uid}/dataSets", method = RequestMethod.GET )
    public String getDataSets( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = getEntity( uid );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<org.hisp.dhis.dataset.DataSet> dataSets = Lists.newArrayList( equipmentType.getDataSets()) ;

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataSets.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataSets = PagerUtils.pageCollection( dataSets, pager );
        }

        metaData.setDataSets(dataSets);
        
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping(value = "/{uid}/dataSets/query/{q}", method = RequestMethod.GET)
    public String getDataSetsByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = getEntity( uid );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<org.hisp.dhis.dataset.DataSet> dataSets = Lists.newArrayList( equipmentType.getDataSets()) ;

        for ( DataSet dataSet : equipmentType.getDataSets() )
        {
            if ( dataSet.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
            	dataSets.add( dataSet );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataSets.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataSets = PagerUtils.pageCollection( dataSets, pager );
        }

        metaData.setDataSets(dataSets );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
}


