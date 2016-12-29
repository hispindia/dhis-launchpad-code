/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.validationrule.action.dataanalysis;

import java.util.Date;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @author Brajesh Murari
 * @version $Id ValidationRunAnalysisAction.java Mar 1, 2011 9:54:31 AM $
 */
public class ValidationRunAnalysisAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    private String selectedStartPeriodId;

	public String getSelectedStartPeriodId() {
		return selectedStartPeriodId;
	}

	public void setSelectedStartPeriodId(String selectedStartPeriodId) {
		this.selectedStartPeriodId = selectedStartPeriodId;
	}
	
	private String selectedEndPeriodId;

	public String getSelectedEndPeriodId() {
		return selectedEndPeriodId;
	}

	public void setSelectedEndPeriodId(String selectedEndPeriodId) {
		this.selectedEndPeriodId = selectedEndPeriodId;
	}
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit selectedOrganisationUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();

        if ( selectedOrganisationUnit == null )
        {
            message = i18n.getString( "specify_organisationunit" );

            return INPUT;
        }

        System.out.println("selectedStartPeriodId :" + selectedStartPeriodId);
        System.out.println("selectedEndPeriodId :" + selectedEndPeriodId);

        if ( selectedStartPeriodId == null || selectedStartPeriodId.trim().length() == 0 )
        {
            message = i18n.getString( "specify_start_period" );

            return INPUT;
        }
        else
        {
            if ( PeriodType.getPeriodFromIsoString(selectedStartPeriodId) == null )
            {
            	message = i18n.getString( "specify_a_valid_start_period" );

                return INPUT;
            }
        } 
        
        if ( selectedEndPeriodId == null || selectedEndPeriodId.trim().length() == 0 )
        {
            message = i18n.getString( "specify_a_end_period" );

            return INPUT;
        }
        else
        {
            if ( PeriodType.getPeriodFromIsoString(selectedEndPeriodId) == null )
            {
            	message = i18n.getString( "specify_a_valid_end_period" );

                return INPUT;
            }
        } 
             
        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
