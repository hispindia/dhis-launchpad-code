package org.hisp.dhis.validationrule.action;

import java.util.List;
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


import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.minmax.MinMaxValidationResult;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;
/**
 * @author Brajesh Murari
 */
public class ExportMinMaxResultAction  implements Action
{
    private static final String DEFAULT_TYPE = "pdf";
    
    private static final String KEY_MINMAXRESULT_GRID = "minMaxResult";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        grid = generateGrid();
        
        type = StringUtils.defaultIfEmpty( type, DEFAULT_TYPE );
        
        return type;
    }

    @SuppressWarnings( "unchecked" )
    private Grid generateGrid()
    {
        List<MinMaxValidationResult> results = (List<MinMaxValidationResult>) SessionUtils.
            getSessionVar( KEY_MINMAXRESULT_GRID );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        Grid grid = new ListGrid();
        
        grid.setTitle( i18n.getString( "min_max_report" ) );
        
        if ( organisationUnit != null )
        {
            grid.setSubtitle( organisationUnit.getName() );
        }
        
        grid.addHeader( new GridHeader( i18n.getString( "data_element" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "source" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "min" ), false, true ) );
        //grid.addHeader( new GridHeader( i18n.getString( "value" ), false, false ) );
        grid.addHeader( new GridHeader( i18n.getString( "max" ), false, false ) );
        grid.addHeader( new GridHeader( i18n.getString( "mark" ), false, false ) );
    
        for ( MinMaxValidationResult minMaxValidationResult : results )
        {
        	DataElement de = minMaxValidationResult.getDataElement();
            OrganisationUnit unit = minMaxValidationResult.getSource();
            Period period = minMaxValidationResult.getPeriod();
            
            grid.addRow();
            grid.addValue( de.getName() );
            grid.addValue( unit.getName() );
            grid.addValue( format.formatPeriod( period ) );
            grid.addValue( minMaxValidationResult.getMin() ); //TODO lazy prone
           // grid.addValue( minMaxValidationResult.getValue() ); //TODO lazy prone
            grid.addValue( minMaxValidationResult.getMax() ); //TODO lazy prone
            grid.addValue( minMaxValidationResult.isGenerated() ); //TODO lazy prone
        }
        
        return grid;
    }
}
