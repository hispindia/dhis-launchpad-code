package org.hisp.dhis.settings.action.system;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.NonCriticalUserAuthorityGroupFilter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class GetAccessSettingsAction
    implements Action
{
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<UserAuthorityGroup> userRoles = new ArrayList<>();

    public List<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }
    
    private List<OrganisationUnit> selfRegistrationOrgUnits = new ArrayList<>();

    public List<OrganisationUnit> getSelfRegistrationOrgUnits()
    {
        return selfRegistrationOrgUnits;
    }

    private List<String> corsWhitelist = new ArrayList<>();

    public List<String> getCorsWhitelist()
    {
        return corsWhitelist;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        userRoles = new ArrayList<>( userService.getAllUserAuthorityGroups() );
        
        FilterUtils.filter( userRoles, new NonCriticalUserAuthorityGroupFilter() );
        Collections.sort( userRoles, IdentifiableObjectNameComparator.INSTANCE );
        
        selfRegistrationOrgUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );
        selfRegistrationOrgUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevel( 2 ) );

        corsWhitelist = systemSettingManager.getCorsWhitelist();
        
        return SUCCESS;        
    }
}
