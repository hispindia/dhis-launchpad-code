package org.hisp.dhis.system.scheduling;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 */
public class TaskTest
{
    @Test
    public void testGetPeriods()
    {
        Set<String> periodTypes = new HashSet<String>();
        periodTypes.add( MonthlyPeriodType.NAME );
        periodTypes.add( QuarterlyPeriodType.NAME );
        periodTypes.add( SixMonthlyPeriodType.NAME );
        periodTypes.add( YearlyPeriodType.NAME );
        
        DataMartTask dataMartTask = new DataMartTask();
        
        List<Period> periods = dataMartTask.getPeriods( periodTypes );
        
        assertNotNull( periods );
        assertEquals( 20, periods.size() ); // 2 Y, 2 S, 4 Q, 12 M
        
        dataMartTask.setLast6Months( true );
        dataMartTask.setFrom6To12Months( false );
        
        periods = dataMartTask.getPeriods( periodTypes );

        assertNotNull( periods );
        assertEquals( 10, periods.size() ); // 1 Y, 1 S, 2 Q, 6 M
        
        dataMartTask.setLast6Months( false );
        dataMartTask.setFrom6To12Months( true );

        periods = dataMartTask.getPeriods( periodTypes );

        assertNotNull( periods );
        assertEquals( 10, periods.size() ); // 1 Y, 1 S, 2 Q, 6 M        
    }
}
