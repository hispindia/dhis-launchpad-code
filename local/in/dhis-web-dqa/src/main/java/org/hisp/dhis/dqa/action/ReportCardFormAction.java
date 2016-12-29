package org.hisp.dhis.dqa.action;

import static org.hisp.dhis.period.PeriodType.getPeriodFromIsoString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dqa.api.DQAReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class ReportCardFormAction implements Action
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
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private DQAReportService dqaReportService;
    
    public void setDqaReportService(DQAReportService dqaReportService) 
    {
		this.dqaReportService = dqaReportService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

   

    private String pe;

    public String getPe()
    {
        return pe;
    }

    public void setPe( String pe )
    {
        this.pe = pe;
    }

    private String ou;

    public void setOu( String ou )
    {
        this.ou = ou;
    }
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitLevel> levels;

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }

    private boolean render;
    
    public boolean isRender()
    {
        return render;
    }
    
    private int offset;

    public int getOffset()
    {
        return offset;
    }

    private PeriodType periodType;
    
    public PeriodType getPeriodType()
    {
        return periodType;
    }
    
    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }
    
    private SimpleDateFormat simpleDateFormat;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        periodNameList = new ArrayList<String>();
        
        render = ( pe != null && ou != null );
        
        if ( pe != null && getPeriodFromIsoString( pe ) != null )
        {
            Period period = getPeriodFromIsoString( pe );
                        
            offset = new Cal().set( period.getStartDate() ).getYear() - new Cal().now().getYear();
            
            selectionTreeManager.setSelectedOrganisationUnit( organisationUnitService.getOrganisationUnit( ou ) ); //TODO set unit state in client instead
        }
        
        levels = organisationUnitService.getFilledOrganisationUnitLevels();
        
        PeriodType periodType = periodService.getPeriodTypeByName( "Yearly" );

        //periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        periods = new ArrayList<Period>( periodService.getAllPeriods() );
        
        //dqaReportService.getNoOfExtremeOutliers(organisationUnit, periods, dataElement)
        
        Collections.sort( periods, new PeriodComparator() );
        
        simpleDateFormat = new SimpleDateFormat( "yyyy" );
        
        int count = 0;
        for ( Period period : periods )
        {
            periodNameList.add( simpleDateFormat.format( period.getStartDate() ) );
            removeDuplicates( periodNameList );
            /*
            if ( count == 0  )
            {
                System.out.println( " First Period Name is : " + simpleDateFormat.format( period.getStartDate() ) + " ID is : " + count );
            }
            
            if ( count == periods.size()-1  )
            {
                System.out.println( " Last Period Name is : " + simpleDateFormat.format( period.getStartDate() ) + " ID is : " + count );
            }
            */
            count++;
        }
        
        Collections.reverse( periodNameList );
        
        //Collections.sort( periodNameList, new IdentifiableObjectNameComparator() );
        
        /*
        int count1 = 0;
        for( String period : periodNameList )
        {
            System.out.println( " Period Name is : " + period + " Period ID is : " + count1 );
            count1++;
        }
        */
        
        
        //Collections.sort( periods );
        //Collections.reverse( periods );
        
        /*
         for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        for( Period period : periods )
        {
            //period.get
            System.out.println( " Period Name is : " + period.getName() + " Period ID is : " + period.getId() );
        }
        */
        return SUCCESS;
    }
    
    public void removeDuplicates( List<String> list) 
    {
        //HashSet set = new HashSet(list);
       // HashSet set = new HashSet(list);
        Set<String> set = new TreeSet<String>( list );
        list.clear();
        list.addAll(set);
    }
    

}
