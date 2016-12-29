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
package org.hisp.dhis.customreport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.customreports.CustomReportStore;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultCustomReportService implements CustomReportService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CustomReportStore customReportStore;
    
    public void setCustomReportStore( CustomReportStore customReportStore )
    {
        this.customReportStore = customReportStore;
    }
    
    // -------------------------------------------------------------------------
    // CustomReport
    // -------------------------------------------------------------------------
    
    @Transactional
    @Override
    public int addCustomReport( CustomReport customReport )
    {
        return customReportStore.addCustomReport( customReport );
    }
    
    @Transactional
    @Override
    public void updateCustomReport( CustomReport customReport )
    {
        customReportStore.updateCustomReport( customReport );
    }
    
    @Transactional
    @Override
    public void deleteCustomReport( CustomReport customReport )
    {
        customReportStore.deleteCustomReport( customReport );
    }
    
    @Transactional
    @Override
    public CustomReport getCustomReportById( int id )
    {
        return customReportStore.getCustomReportById( id );
    }
    
    public Collection<CustomReport> getAllCustomReports()
    {
        return customReportStore.getAllCustomReports();
    }
    
    @Transactional
    @Override
    public Collection<CustomReport> getAllCustomReportsByReportType( String reportType )
    {
        return customReportStore.getAllCustomReportsByReportType( reportType );
    }
    
    @Transactional
    @Override
    public Collection<CustomReport> getAllCustomReportsByUser( User user )
    {
        return customReportStore.getAllCustomReportsByUser( user );
    }
    
    public Collection<CustomReport> getAllAvailableCustomReports()
    {
        List<CustomReport> availableCustomReportList = new ArrayList<CustomReport>();
       
        List<CustomReport> customReportList = new ArrayList<CustomReport>( getAllCustomReports() );
        
        for ( CustomReport customReport : customReportList )
        {
            if ( customReport.isReportAvailable() )
            {
                availableCustomReportList.add( customReport );
            }
        }

        return availableCustomReportList;
    }    

    
    
    public void searchReportsByName( List<CustomReport> customReports, String key )
    {
        Iterator<CustomReport> iterator = customReports.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }    
    
    
    
}
