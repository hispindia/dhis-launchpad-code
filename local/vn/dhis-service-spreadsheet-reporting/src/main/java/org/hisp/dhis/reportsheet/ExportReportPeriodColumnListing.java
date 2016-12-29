package org.hisp.dhis.reportsheet;

/*
 * Copyright (c) 2004-2011, University of Oslo
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class ExportReportPeriodColumnListing
    extends ExportReport
{
    private Set<PeriodColumn> periodColumns = new HashSet<PeriodColumn>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExportReportPeriodColumnListing()
    {
        super();
    }

    public ExportReportPeriodColumnListing( String name, String group, String excelTemplateFile )
    {
        super( name, group, excelTemplateFile );
    }

    public ExportReportPeriodColumnListing( String name, String group, String excelTemplateFile, String createdBy )
    {
        super( name, group, excelTemplateFile, createdBy );
    }

    public void addPeriodColumn( PeriodColumn periodColumn )
    {
        periodColumns.add( periodColumn );
    }

    public void deletePeriodColumn( PeriodColumn periodColumn )
    {

        periodColumns.remove( periodColumn );
    }

    @Override
    public String getReportType()
    {
        return ExportReport.TYPE.PERIOD_COLUMN_LISTING;
    }

    public Set<PeriodColumn> getPeriodColumns()
    {
        return periodColumns;
    }

    public void setPeriodColumns( Set<PeriodColumn> periodColumns )
    {
        this.periodColumns = periodColumns;
    }

    @Override
    public List<String> getItemTypes()
    {
        List<String> types = new ArrayList<String>();
        types.add( ExportItem.TYPE.DATAELEMENT );
        types.add( ExportItem.TYPE.DATAELEMENT_VALUETYPE_TEXT );
        types.add( ExportItem.TYPE.INDICATOR );

        return types;
    }
}
