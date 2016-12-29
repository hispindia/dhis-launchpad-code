package org.hisp.dhis.api.controller.model;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
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
@RequestMapping(value = ModelTypeAttributeGroupController.RESOURCE_PATH)
public class ModelTypeAttributeGroupController 
	extends AbstractCrudController<ModelTypeAttributeGroup>
{
    public static final String RESOURCE_PATH = "/modelTypeAttributeGroups";

    @RequestMapping( value = "/{uid}/modelTypeAttributes", method = RequestMethod.GET )
    public String getModelTypeAttributes( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        ModelTypeAttributeGroup modelTypeAttributeGroup = getEntity( uid );

        if ( modelTypeAttributeGroup == null )
        {
            ContextUtils.notFoundResponse( response, "Model Type Attribute Group not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<ModelTypeAttribute> modelTypeAttributes = Lists.newArrayList( modelTypeAttributeGroup.getModelTypeAttributes() );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), modelTypeAttributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            modelTypeAttributes = PagerUtils.pageCollection( modelTypeAttributes, pager );
        }

        metaData.setModelTypeAttributes(modelTypeAttributes);
        
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping(value = "/{uid}/modelTypeAttributes/query/{q}", method = RequestMethod.GET)
    public String getModelTypeAttributesByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        ModelTypeAttributeGroup modelTypeAttributeGroup = getEntity( uid );

        if ( modelTypeAttributeGroup == null )
        {
            ContextUtils.notFoundResponse( response, "Model Type Attribute Group not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<ModelTypeAttribute> modelTypeAttributes = Lists.newArrayList( modelTypeAttributeGroup.getModelTypeAttributes() );

        for ( ModelTypeAttribute modelTypeAttribute : modelTypeAttributeGroup.getModelTypeAttributes()) 
        {
            if ( modelTypeAttribute.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
            	modelTypeAttributes.add( modelTypeAttribute );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), modelTypeAttributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            modelTypeAttributes = PagerUtils.pageCollection( modelTypeAttributes, pager );
        }

        metaData.setModelTypeAttributes( modelTypeAttributes );

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
}



