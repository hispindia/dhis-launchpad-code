package org.hisp.dhis.webapi.controller.synchmanager;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping( method = RequestMethod.GET )
public class DataElementSynchStatusController        
{
    public static final String RESOURCE_PATH = "/AccepetanceDE";

    public static final String DE_STATUS_NEW = "new";
    public static final String DE_STATUS_UPDATED = "update";
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementSynchStatusService deSynchStatusService;
    
    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private SynchInstanceService synchInstanceService;

    @RequestMapping( value = DataElementSynchStatusController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        List<DataElement> dataElements = new ArrayList<>();
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();

        String clientURL = options.getOptions().get("cleintURL");
        
        SynchInstance instance = synchInstanceService.getInstanceByUrl( clientURL );
        
        if ( options.getOptions().containsKey( "status" ))
        {
            String deStatus = options.getOptions().get( "status" );
            
            if( DE_STATUS_UPDATED.equalsIgnoreCase( deStatus ) )
            {
                dataElements.addAll( deSynchStatusService.getDataElementListByInstance( instance ) );
            }
            else
            {
                dataElements.addAll( deSynchStatusService.getNewDataElements() );
            }
        }
        else
        {
            dataElements.addAll( deSynchStatusService.getApprovedDataElementListByInstance( instance ) );
        }
        
        metaData.setDataElements( dataElements );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );

        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
    }

    /*
    @Override
    protected List<DataElement> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<DataElement> dataElements = new ArrayList<>();

        if ( options.getOptions().containsKey( "status" ))
        {
            String deStatus = options.getOptions().get( "status" );
            
            if( DE_STATUS_UPDATED.equalsIgnoreCase( deStatus ) )
            {
                dataElements.addAll( deSynchStatusService.getUpdatedDataElements() );
            }
            else
            {
                dataElements.addAll( deSynchStatusService.getNewDataElements() );
            }
        }
        else
        {
            dataElements.addAll( deSynchStatusService.getNewDataElements() );
        }
        
        return dataElements;
    }
    */
}