package org.hisp.dhis.calendar;

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

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class ChronologyBasedCalendar extends AbstractCalendar
{
    private final Chronology chronology;

    protected ChronologyBasedCalendar( Chronology chronology )
    {
        this.chronology = chronology;
    }

    @Override
    public DateUnit toIso( DateUnit dateUnit )
    {
        if ( dateUnit.isIso8601() )
        {
            return dateUnit;
        }

        DateTime dateTime = dateUnit.toDateTime( chronology );
        dateTime = dateTime.withChronology( ISOChronology.getInstance() );

        return new DateUnit( DateUnit.fromDateTime( dateTime ), true );
    }

    @Override
    public DateUnit fromIso( DateUnit dateUnit )
    {
        if ( !dateUnit.isIso8601() )
        {
            return dateUnit;
        }

        DateTime dateTime = dateUnit.toDateTime( ISOChronology.getInstance() );
        dateTime = dateTime.withChronology( chronology );
        return DateUnit.fromDateTime( dateTime );
    }

    @Override
    public DateInterval toInterval( DateUnit dateUnit, DateIntervalType type, int offset, int length )
    {
        switch ( type )
        {
            case ISO8601_YEAR:
                return toYearIsoInterval( dateUnit, offset, length );
            case ISO8601_MONTH:
                return toMonthIsoInterval( dateUnit, offset, length );
            case ISO8601_WEEK:
                return toWeekIsoInterval( dateUnit, offset, length );
            case ISO8601_DAY:
                return toDayIsoInterval( dateUnit, offset, length );
        }

        return null;
    }

    private DateInterval toYearIsoInterval( DateUnit dateUnit, int offset, int length )
    {
        DateTime from = dateUnit.toDateTime( chronology );

        if ( offset > 0 )
        {
            from = from.plusYears( offset );
        }
        else if ( offset < 0 )
        {
            from = from.minusYears( -offset );
        }

        DateTime to = new DateTime( from ).plusYears( length ).minusDays( 1 );

        DateUnit fromDateUnit = DateUnit.fromDateTime( from );
        DateUnit toDateUnit = DateUnit.fromDateTime( to );

        fromDateUnit.setDayOfWeek( isoWeekday( fromDateUnit ) );
        toDateUnit.setDayOfWeek( isoWeekday( toDateUnit ) );

        return new DateInterval( toIso( fromDateUnit ), toIso( toDateUnit ), DateIntervalType.ISO8601_YEAR );
    }

    private DateInterval toMonthIsoInterval( DateUnit dateUnit, int offset, int length )
    {
        DateTime from = dateUnit.toDateTime( chronology );

        if ( offset > 0 )
        {
            from = from.plusMonths( offset );
        }
        else if ( offset < 0 )
        {
            from = from.minusMonths( -offset );
        }

        DateTime to = new DateTime( from ).plusMonths( length ).minusDays( 1 );

        DateUnit fromDateUnit = DateUnit.fromDateTime( from );
        DateUnit toDateUnit = DateUnit.fromDateTime( to );

        fromDateUnit.setDayOfWeek( isoWeekday( fromDateUnit ) );
        toDateUnit.setDayOfWeek( isoWeekday( toDateUnit ) );

        return new DateInterval( toIso( fromDateUnit ), toIso( toDateUnit ), DateIntervalType.ISO8601_MONTH );
    }

    private DateInterval toWeekIsoInterval( DateUnit dateUnit, int offset, int length )
    {
        DateTime from = dateUnit.toDateTime( chronology );

        if ( offset > 0 )
        {
            from = from.plusWeeks( offset );
        }
        else if ( offset < 0 )
        {
            from = from.minusWeeks( -offset );
        }

        DateTime to = new DateTime( from ).plusWeeks( length ).minusDays( 1 );

        DateUnit fromDateUnit = DateUnit.fromDateTime( from );
        DateUnit toDateUnit = DateUnit.fromDateTime( to );

        fromDateUnit.setDayOfWeek( isoWeekday( fromDateUnit ) );
        toDateUnit.setDayOfWeek( isoWeekday( toDateUnit ) );

        return new DateInterval( toIso( fromDateUnit ), toIso( toDateUnit ), DateIntervalType.ISO8601_WEEK );
    }

    private DateInterval toDayIsoInterval( DateUnit dateUnit, int offset, int length )
    {
        DateTime from = dateUnit.toDateTime( chronology );

        if ( offset > 0 )
        {
            from = from.plusDays( offset );
        }
        else if ( offset < 0 )
        {
            from = from.minusDays( -offset );
        }

        DateTime to = new DateTime( from ).plusDays( length );

        DateUnit fromDateUnit = DateUnit.fromDateTime( from );
        DateUnit toDateUnit = DateUnit.fromDateTime( to );

        fromDateUnit.setDayOfWeek( isoWeekday( fromDateUnit ) );
        toDateUnit.setDayOfWeek( isoWeekday( toDateUnit ) );

        return new DateInterval( toIso( fromDateUnit ), toIso( toDateUnit ), DateIntervalType.ISO8601_DAY );
    }

    @Override
    public int monthsInYear()
    {
        DateTime dateTime = new DateTime( 1, 1, 1, 12, 0, chronology );
        return dateTime.monthOfYear().getMaximumValue();
    }

    @Override
    public int daysInWeek()
    {
        DateTime dateTime = new DateTime( 1, 1, 1, 12, 0, chronology );
        return dateTime.dayOfWeek().getMaximumValue();
    }

    @Override
    public int daysInYear( int year )
    {
        DateTime dateTime = new DateTime( year, 1, 1, 12, 0, chronology );
        return (int) dateTime.year().toInterval().toDuration().getStandardDays();
    }

    @Override
    public int daysInMonth( int year, int month )
    {
        DateTime dateTime = new DateTime( year, month, 1, 12, 0, chronology );
        return dateTime.dayOfMonth().getMaximumValue();
    }

    @Override
    public int weeksInYear( int year )
    {
        DateTime dateTime = new DateTime( year, 1, 1, 12, 0, chronology );
        return dateTime.weekOfWeekyear().getMaximumValue();
    }

    @Override
    public int isoWeek( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return dateTime.getWeekOfWeekyear();
    }

    @Override
    public int week( DateUnit dateUnit )
    {
        return isoWeek( dateUnit );
    }

    @Override
    public int isoWeekday( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        dateTime = dateTime.withChronology( ISOChronology.getInstance() );
        return dateTime.getDayOfWeek();
    }

    @Override
    public int weekday( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return dateTime.getDayOfWeek();
    }

    @Override
    public DateUnit plusDays( DateUnit dateUnit, int days )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.plusDays( days ) );
    }

    @Override
    public DateUnit minusDays( DateUnit dateUnit, int days )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.minusDays( days ) );
    }

    @Override
    public DateUnit plusWeeks( DateUnit dateUnit, int weeks )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.plusWeeks( weeks ) );
    }

    @Override
    public DateUnit minusWeeks( DateUnit dateUnit, int weeks )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.minusWeeks( weeks ) );
    }

    @Override
    public DateUnit plusMonths( DateUnit dateUnit, int months )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.plusMonths( months ) );
    }

    @Override
    public DateUnit minusMonths( DateUnit dateUnit, int months )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.minusMonths( months ) );
    }

    @Override
    public DateUnit plusYears( DateUnit dateUnit, int years )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.plusYears( years ) );
    }

    @Override
    public DateUnit minusYears( DateUnit dateUnit, int years )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return DateUnit.fromDateTime( dateTime.minusYears( years ) );
    }
}
