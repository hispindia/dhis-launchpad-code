package org.hisp.dhis.reports.viewdata.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Collections;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.util.FPMUReportManager;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateViewDataReportResultAction
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
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    private FPMUReportManager fpmuReportManager;

    public void setFpmuReportManager( FPMUReportManager fpmuReportManager )
    {
        this.fpmuReportManager = fpmuReportManager;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private String datasetId;

    public void setDatasetId( String datasetId )
    {
        this.datasetId = datasetId;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    private List<DataElement> dataElementList;

    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<Section> dataSetSection;

    public List<Section> getDataSetSection()
    {
        return dataSetSection;
    }

    private Map<String, List<DataElement>> dataSetMap = new HashMap<String, List<DataElement>>();

    public Map<String, List<DataElement>> getDataSetMap()
    {
        return dataSetMap;
    }

    private  Map<OrganisationUnit, Map<Period, Map<String, String>>> dataViewResultMap = new HashMap<OrganisationUnit, Map<Period, Map<String, String>>>();
    
    public Map<OrganisationUnit, Map<Period, Map<String, String>>> getDataViewResultMap()
    {
        return dataViewResultMap;
    }

    private String orgUnitIdsByComma;
    
    public String getOrgUnitIdsByComma()
    {
        return orgUnitIdsByComma;
    }

    private List<Period> periodList;
    
    public List<Period> getPeriodList()
    {
        return periodList;
    }

    private String headingMessage;
    
    public String getHeadingMessage()
    {
        return headingMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        String dataElementIdsByComma = "";
        
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( datasetId ) );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        if( dataSet.getPeriodType().getName().equalsIgnoreCase( "Monthly" ) )
        {
            dateFormat = new SimpleDateFormat("MMM-yy");
        }
        else if( dataSet.getPeriodType().getName().equalsIgnoreCase( "Yearly" ) )
        {
            dateFormat = new SimpleDateFormat("yyyy");
        }
        else if( dataSet.getPeriodType().getName().equalsIgnoreCase( "FinancialJuly" ) )
        {
            dateFormat = new SimpleDateFormat("yyyy");
        }
        
        Date sDate = format.parseDate( startDate );
        Date eDate = format.parseDate( endDate );

        List<Period> periodList1 = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dataSet.getPeriodType(), sDate, eDate ) );
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList1 ) );
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        periodList = new ArrayList<Period>();
        for( Period p :  periodList1 )
        {
            if( dataSet.getPeriodType().getName().equalsIgnoreCase( "FinancialJuly" ) )
            {
                String temp = dateFormat.format( p.getStartDate() ) + "-" + (Integer.parseInt( dateFormat.format( p.getStartDate() ) )+1);
                p.setName( temp );
            }
            else
            {
                p.setName( dateFormat.format( p.getStartDate() ) );
            }
            
            periodList.add( p );
        }
        
        Collections.sort( periodList, new PeriodStartDateComparator() );
        
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        orgUnitList = new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        orgUnitList.retainAll( dataSetSource );        
        Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );

        if ( dataSet.getSections().size() == 0 )
        {
            dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
            Collection<Integer> dataelementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class,
                dataElementList ) );
            dataElementIdsByComma = getCommaDelimitedString( dataelementIds );
            Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );
        }
        else
        {
            dataSetSection = new ArrayList<Section>( dataSet.getSections() );
            for ( Section section : dataSetSection )
            {
                List<DataElement> deList = new ArrayList<DataElement>( section.getDataElements() );
                dataSetMap.put( section.getName(), deList );
                Collection<Integer> dataelementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, deList ) );
                dataElementIdsByComma = getCommaDelimitedString( dataelementIds );
            }
        }
        
        dataViewResultMap = new HashMap<OrganisationUnit, Map<Period, Map<String, String>>>();
        for( OrganisationUnit orgunit : orgUnitList )
        {
            Map<Period, Map<String, String>> dataViewPeriodMap = new HashMap<Period, Map<String, String>>();
            
            for( Period period : periodList )
            {
                Map<String, String> availableRawDataMap = fpmuReportManager.getAvailableRawData( orgunit.getId(), period.getId(), dataElementIdsByComma );
                if( availableRawDataMap !=  null && !availableRawDataMap.isEmpty() )
                {
                    dataViewPeriodMap.put( period, availableRawDataMap );
                }
            }
            
            if( dataViewPeriodMap != null && !dataViewPeriodMap.isEmpty() )
            {
                dataViewResultMap.put( orgunit, dataViewPeriodMap );
            }
        }
        
        headingMessage = dataSet.getName() + " from " + startDate + " to " + endDate;
        
        return SUCCESS;
    }
}
