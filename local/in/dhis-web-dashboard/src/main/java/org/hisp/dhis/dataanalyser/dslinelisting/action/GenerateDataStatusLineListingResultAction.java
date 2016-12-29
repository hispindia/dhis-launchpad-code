package org.hisp.dhis.dataanalyser.dslinelisting.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateDataStatusLineListingResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
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
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // Getter and Setter Input/output
    // -------------------------------------------------------------------------
    
    private Integer orgUntId;
    
    public void setOrgUntId( Integer orgUntId )
    {
        this.orgUntId = orgUntId;
    }
    
    private Integer selectedDataSets;
    
    public void setSelectedDataSets( Integer selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    
    private Integer startPeriodId;
    
    public void setStartPeriodId( Integer startPeriodId )
    {
        this.startPeriodId = startPeriodId;
    }
    
    private Integer endPeriodId;
    
    public void setEndPeriodId( Integer endPeriodId )
    {
        this.endPeriodId = endPeriodId;
    }


    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private OrganisationUnit selectedOrgUnit = new OrganisationUnit();
    
    public OrganisationUnit getSelectedOrgUnit()
    {
        return selectedOrgUnit;
    }
    
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private List<Period> periodList;

    public List<Period> getPeriodList()
    {
        return periodList;
    }
    
    private Period startPeriod;
    
    public Period getStartPeriod()
    {
        return startPeriod;
    }
    
    private Period endPeriod;
    
    public Period getEndPeriod()
    {
        return endPeriod;
    }

    private String dataElementIdsByComma;
    private String  orgUnitIdsByComma;
    private String  periodIdsByComma;
    
    private Map<Integer, String> maporgUnitHierarchy = new HashMap<Integer, String>();
   
    public Map<Integer, String> getMaporgUnitHierarchy()
    {
        return maporgUnitHierarchy;
    }

    private Map<String, String> lineListingDataStatusMap = new HashMap<String, String>();
    
    public Map<String, String> getLineListingDataStatusMap()
    {
        return lineListingDataStatusMap;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        
        System.out.println( "Line Listing Data Entry Status  Start Time  : " + new Date() );
        
        // dataSet and dataElement related information
        dataElementIdsByComma = "-1";
        dataSet = dataSetService.getDataSet( selectedDataSets );
        
        for ( DataElement de : dataSet.getDataElements() )
        {
            dataElementIdsByComma += "," + de.getId();
        }
           
        // orgUnit unit related information
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgUntId);
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsWithChildren( selectedOrgUnit.getUid() ));
        orgUnitList.retainAll( dataSetSource );
        orgUnitIdsByComma = "-1";
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            orgUnitIdsByComma += "," + orgUnit.getId();
            maporgUnitHierarchy.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
        }
        
        // Period Related Info
        startPeriod = periodService.getPeriod( startPeriodId );
        endPeriod = periodService.getPeriod( endPeriodId );

        PeriodType dataSetPeriodType = dataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        
        periodIdsByComma = "-1";
        for ( Period period : periodList )
        {
            periodIdsByComma += "," + period.getId();
        }
        
        lineListingDataStatusMap = new HashMap<String, String>( getLineListingDataStatus( orgUnitIdsByComma, periodIdsByComma, dataElementIdsByComma ) );
        
        //Collections.sort( periodList, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        System.out.println( "Line Listing Data Entry Status  End Time  : " + new Date() );
        
        return SUCCESS;
    }
    
    
    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;
            
            orgunit = orgunit.getParent();
            if( orgunit.getId() == selectedOrgUnit.getId())
            {
                break;
            }
        }

        return hierarchyOrgunit;
    }
    
    //--------------------------------------------------------------------------------
    // Get LineListing Data Status By orgUnitIds, PeriodIds, dataElementsIds
    //--------------------------------------------------------------------------------
    public Map<String, String> getLineListingDataStatus( String orgUnitIdsByComma, String periodIdsByComma, String dataElementIdsByComma )
    {
        Map<String, String> lineListingDataStatusMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT sourceid,periodid,recordno FROM lldatavalue " +
                           " WHERE sourceid IN (" + orgUnitIdsByComma + ") AND " + 
                           " periodid IN (" + periodIdsByComma + ") AND " + 
                           " dataelementid IN (" + dataElementIdsByComma + ") " + 
                           " GROUP BY sourceid,periodid,recordno ORDER BY recordno";
            
              
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer periodId = rs.getInt( 2 );                
                Integer recordNo = rs.getInt( 3 ); 
                
                if ( recordNo != null && recordNo > 0 )
                {
                    lineListingDataStatusMap.put( orgUnitId+":"+periodId, "1" );
                }
            }
            
            return lineListingDataStatusMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal orgUnitIdsByComma id", e );
        }
    }
    
    
    
}
