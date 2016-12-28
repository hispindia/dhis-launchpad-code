package org.hisp.dhis.webapi.controller.organisationunit;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.schema.descriptors.OrganisationUnitGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitGroupSchemaDescriptor.API_ENDPOINT )
public class OrganisationUnitGroupController
    extends AbstractCrudController<OrganisationUnitGroup>
{
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @RequestMapping( value = "/{uid}/organisationUnits/{ouUid}", method = { RequestMethod.PUT, RequestMethod.POST } )
    public void updatetOrgUnitList( @PathVariable( "uid" ) String uid, @PathVariable( "ouUid" ) String ouUid, 
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    { 
        OrganisationUnitGroup organisationUnitGroup = manager.get( getEntityClass(), uid );
        if ( organisationUnitGroup == null )
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnitGroup not found for uid: " + uid );
            return;
        }
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouUid );
        if ( orgUnit == null ) 
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnit not found for uid: " + ouUid );
            return;
        }
        
        Set<OrganisationUnit> organisationUnits = organisationUnitGroup.getMembers();
        organisationUnits.add( orgUnit );
        organisationUnitGroupService.updateOrganisationUnitGroup( organisationUnitGroup );
        
        // Add the orgUnitGroup to the orgUnit as well
        orgUnit.addOrganisationUnitGroup( organisationUnitGroup );
        organisationUnitService.updateOrganisationUnit( orgUnit );       
        
        ContextUtils.createdResponse( response, "success", null );        
    }
    
    @RequestMapping( value = "/{uid}/organisationUnits/{ouUid}", method = RequestMethod.DELETE )
    public void deleteOrgUnitList( @PathVariable( "uid" ) String uid, @PathVariable( "ouUid" ) String ouUid, 
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    { 
        OrganisationUnitGroup organisationUnitGroup = manager.get( getEntityClass(), uid );
        if ( organisationUnitGroup == null )
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnitGroup not found for uid: " + uid );
            return;
        }
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouUid );
        if ( orgUnit == null ) 
        {
            ContextUtils.notFoundResponse( response, "OrganisationUnit not found for uid: " + ouUid );
            return;
        }

        Set<OrganisationUnit> organisationUnits = organisationUnitGroup.getMembers();
        organisationUnits.remove( orgUnit );
        organisationUnitGroupService.updateOrganisationUnitGroup( organisationUnitGroup );

        // Remove the orgUnitGroup from the orgUnit as well
        orgUnit.removeOrganisationUnitGroup( organisationUnitGroup );
        organisationUnitService.updateOrganisationUnit( orgUnit );       

        ContextUtils.createdResponse( response, "success", null );                
    }
        
    @RequestMapping( value = "/{uid}/organisationUnits", method = RequestMethod.GET )
    public List<OrganisationUnit> getOrgUnitList( @PathVariable( "uid" ) String uid,
        @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    { 
        OrganisationUnitGroup organisationUnitGroup = manager.get( getEntityClass(), uid );
        if ( organisationUnitGroup == null )
        {
            ContextUtils.notFoundResponse( response, "Program not found for uid: " + uid );
            return new ArrayList<OrganisationUnit>();
        }
                
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitGroup.getMembers() );
        
        WebMetaData metaData = new WebMetaData();      
        metaData.setOrganisationUnits( organisationUnits );
        WebOptions options = new WebOptions(parameters);
        
        model.addAttribute( "model", metaData );        
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );
        
        return organisationUnits;        
    }
           
}
