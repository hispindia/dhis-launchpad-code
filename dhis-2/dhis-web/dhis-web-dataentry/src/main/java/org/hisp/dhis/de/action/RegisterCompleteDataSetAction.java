package org.hisp.dhis.de.action;

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

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.LockException;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class RegisterCompleteDataSetAction
    implements Action
{
    private static final Log log = LogFactory.getLog( RegisterCompleteDataSetAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private boolean multiOrganisationUnit;

    public void setMultiOrganisationUnit( boolean multiOrganisationUnit )
    {
        this.multiOrganisationUnit = multiOrganisationUnit;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        Period period = PeriodType.getPeriodFromIsoString( periodId );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        Set<OrganisationUnit> children = organisationUnit.getChildren();

        String storedBy = currentUserService.getCurrentUsername();
        

        // ---------------------------------------------------------------------
        // Check locked status
        // ---------------------------------------------------------------------

        if ( !multiOrganisationUnit )
        {
            if ( dataSetService.isLocked( dataSet, period, organisationUnit, null ) )
            {
                return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + organisationUnit, 2 );
            }
        }
        else
        {
            for ( OrganisationUnit ou : children )
            {
                if ( ou.getDataSets().contains( dataSet ) && dataSetService.isLocked( dataSet, period, ou, null ) )
                {
                    return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + ou, 2 );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Register as completed dataSet
        // ---------------------------------------------------------------------

        if ( !multiOrganisationUnit )
        {
            registerCompleteDataSet( dataSet, period, organisationUnit, storedBy );
        }
        else
        {
            for ( OrganisationUnit ou : children )
            {
                if ( ou.getDataSets().contains( dataSet ) )
                {
                    registerCompleteDataSet( dataSet, period, ou, storedBy );
                }
            }
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void registerCompleteDataSet( DataSet dataSet, Period period, OrganisationUnit organisationUnit, String storedBy )
    {
        CompleteDataSetRegistration registration = new CompleteDataSetRegistration();

        if ( registrationService.getCompleteDataSetRegistration( dataSet, period, organisationUnit ) == null )
        {
            registration.setDataSet( dataSet );
            registration.setPeriod( period );
            registration.setSource( organisationUnit );
            registration.setDate( new Date() );
            registration.setStoredBy( storedBy );

            registration.setPeriodName( format.formatPeriod( registration.getPeriod() ) );

            registrationService.saveCompleteDataSetRegistration( registration, true );

            log.info( "DataSet registered as complete: " + registration );
        }
        
        // Delete Lock Exception if it Exist
        Integer lockExceptionId = null;
        
        if( period ==  null || period.getId() == 0 )
        {
            period = periodService.reloadPeriod( period );
        }
        
        lockExceptionId = getLockExceptionByOrgUnitPeriodDataSet( organisationUnit.getId(), period.getId(), dataSet.getId() );
        
        //System.out.println (organisationUnit.getId() +" -- " + period.getId() + "--" + dataSet.getId() + "--" + lockExceptionExist );
        
        if( lockExceptionId != null )
        {
            //System.out.println ( " In side Delete Lock Exception " );
            //deleteLockException( dataSet.getId(), period.getId(), organisationUnit.getId() );
            
            LockException lockException = dataSetService.getLockException( lockExceptionId );

            if ( lockException != null )
            {
                dataSetService.deleteLockException( lockException );
                System.out.println ( " Delete Lock Exception  " + lockException.getName()  );
            }
        }
        else
        {
            
        }
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
    
    
    // get Lock Exception from organisationUnitId,periodId,dataSetId
    public Integer getLockExceptionByOrgUnitPeriodDataSet( Integer organisationUnitId, Integer periodId, Integer dataSetId )
    {
        Integer lockExceptionId = null;
        
        try
        {
            String query = "SELECT lockexceptionid FROM lockexception WHERE organisationunitid = " + organisationUnitId + " AND "
                + " periodid = " + periodId + " AND datasetid = " + dataSetId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Integer lockExpId = rs.getInt( 1 );
                
                if( lockExpId != null )
                {
                    lockExceptionId = lockExpId;
                }
                
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return lockExceptionId;
    }   
    
    //delete lock exception
    public void deleteLockException( Integer dataSetId, Integer periodId, Integer orgUnitId )
    {
        try
        {
            String query = "DELETE FROM lockexception WHERE organisationunitid = " + orgUnitId + "  AND periodid  = " + periodId +  " AND datasetid = " + dataSetId +"";
            
            //System.out.println( " query "  + query);
            
            jdbcTemplate.execute( query );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }        

}
