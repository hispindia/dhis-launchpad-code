package org.hisp.dhis.period;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.mock.MockI18nFormat;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class RelativePeriodTest
{
    private static final I18nFormat i18nFormat = new MockI18nFormat();
    
    private static Date getDate( int year, int month, int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( year, month - 1, day );

        return calendar.getTime();
    }
    
    @Test
    public void getRelativePeriods()
    {        
        RelativePeriods periods = new RelativePeriods( true, true, true, true, true, true, true, true, true, true, true, true, true, true, true );
        
        List<Period> relatives = periods.getRelativePeriods( getDate( 2001, 1, 1 ), i18nFormat, false );
        
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 1, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 2, 1 ), getDate( 2001, 2, 28 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 3, 1 ), getDate( 2001, 3, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 4, 1 ), getDate( 2001, 4, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 5, 1 ), getDate( 2001, 5, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 6, 1 ), getDate( 2001, 6, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 7, 1 ), getDate( 2001, 7, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 8, 1 ), getDate( 2001, 8, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 9, 1 ), getDate( 2001, 9, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 10, 1 ), getDate( 2001, 10, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 11, 1 ), getDate( 2001, 11, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2001, 12, 1 ), getDate( 2001, 12, 31 ) ) ) );

        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2000, 3, 1 ), getDate( 2000, 4, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2000, 5, 1 ), getDate( 2000, 6, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2000, 7, 1 ), getDate( 2000, 8, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2000, 9, 1 ), getDate( 2000, 10, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2000, 11, 1 ), getDate( 2000, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new BiMonthlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 2, 28 ) ) ) );

        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 3, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2001, 4, 1 ), getDate( 2001, 6, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2001, 7, 1 ), getDate( 2001, 9, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2001, 10, 1 ), getDate( 2001, 12, 31 ) ) ) );

        assertTrue( relatives.contains( new Period( new SixMonthlyPeriodType(), getDate( 2000, 7, 1 ), getDate( 2000, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new SixMonthlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 6, 30 ) ) ) );
        
        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 12, 31 ) ) ) );

        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 1, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 2, 1 ), getDate( 2000, 2, 29 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 3, 1 ), getDate( 2000, 3, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 4, 1 ), getDate( 2000, 4, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 5, 1 ), getDate( 2000, 5, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 6, 1 ), getDate( 2000, 6, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 7, 1 ), getDate( 2000, 7, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 8, 1 ), getDate( 2000, 8, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 9, 1 ), getDate( 2000, 9, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 10, 1 ), getDate( 2000, 10, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 11, 1 ), getDate( 2000, 11, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new MonthlyPeriodType(), getDate( 2000, 12, 1 ), getDate( 2000, 12, 31 ) ) ) );        

        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 3, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2000, 4, 1 ), getDate( 2000, 6, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2000, 7, 1 ), getDate( 2000, 9, 30 ) ) ) );
        assertTrue( relatives.contains( new Period( new QuarterlyPeriodType(), getDate( 2000, 10, 1 ), getDate( 2000, 12, 31 ) ) ) );

        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 12, 31 ) ) ) );

        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 1997, 1, 1 ), getDate( 1997, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 1998, 1, 1 ), getDate( 1998, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 1999, 1, 1 ), getDate( 1999, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 12, 31 ) ) ) );
        assertTrue( relatives.contains( new Period( new YearlyPeriodType(), getDate( 2001, 1, 1 ), getDate( 2001, 12, 31 ) ) ) );
        
        assertEquals( 67, relatives.size() );
    }
    
    @Test
    public void testGetRelativePeriods()
    {
        Set<String> periodTypes = new HashSet<String>();
        periodTypes.add( MonthlyPeriodType.NAME );
        periodTypes.add( BiMonthlyPeriodType.NAME );
        periodTypes.add( SixMonthlyPeriodType.NAME );
        
        RelativePeriods relatives = new RelativePeriods().getRelativePeriods( periodTypes );
        
        assertTrue( relatives.isLast12Months() );
        assertTrue( relatives.isLast6BiMonths() );
        assertTrue( relatives.isLast2SixMonths() );
        
        assertFalse( relatives.isLast4Quarters() );
        assertFalse( relatives.isLastYear() );        
    }
}
