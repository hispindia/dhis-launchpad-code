package org.hisp.dhis.ovc.report.comparator;

import java.util.Comparator;

import org.hisp.dhis.customreports.CustomReport;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ReportNameComparator implements Comparator<CustomReport>
{
    public int compare( CustomReport customReport0, CustomReport customReport1 )
    {
        return customReport0.getName().compareToIgnoreCase( customReport1.getName() );
    }
}
