package org.hisp.dhis.dataanalyser.ds.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateDataStatusResultAction
    implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    @SuppressWarnings( "unused" )
    private Comparator<OrganisationUnit> orgUnitComparator;

    public void setOrgUnitComparator( Comparator<OrganisationUnit> orgUnitComparator )
    {
        this.orgUnitComparator = orgUnitComparator;
    }

    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, List<Integer>> ouMapDataStatusResult;

    public Map<OrganisationUnit, List<Integer>> getOuMapDataStatusResult()
    {
        return ouMapDataStatusResult;
    }

    private Map<OrganisationUnit, List<Integer>> ouMapDataElementCount;

    public Map<OrganisationUnit, List<Integer>> getOuMapDataElementCount()
    {
        return ouMapDataElementCount;
    }

    private Collection<Period> periodList;

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }

    private Map<DataSet, Map<OrganisationUnit, List<Integer>>> dataStatusResult;

    public Map<DataSet, Map<OrganisationUnit, List<Integer>>> getDataStatusResult()
    {
        return dataStatusResult;
    }

    private Map<DataSet, Collection<Period>> dataSetPeriods;

    public Map<DataSet, Collection<Period>> getDataSetPeriods()
    {
        return dataSetPeriods;
    }

    List<Period> selectedPeriodList;

    public List<Period> getSelectedPeriodList()
    {
        return selectedPeriodList;
    }

    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }

    private int maxOULevel;

    public int getMaxOULevel()
    {
        return maxOULevel;
    }

    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------

    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
    }

    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }

    public String getIncludeZeros()
    {
        return includeZeros;
    }

    private String selectedButton;

    public void setselectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    public String getSelectedButton()
    {
        return selectedButton;
    }

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String immChildOption;
    
    public String getImmChildOption()
    {
        return immChildOption;
    }

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private List<String> selectedDataSets;

    public void setSelectedDataSets( List<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    public List<String> getSelectedDataSets()
    {
        return selectedDataSets;
    }

    private int minOULevel;

    public int getMinOULevel()
    {
        return minOULevel;
    }

    private int number;

    public int getNumber()
    {
        return number;
    }

    private DataSet selDataSet;

    public DataSet getSelDataSet()
    {
        return selDataSet;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    String orgUnitInfo;

    String periodInfo;

    String deInfo;

    int orgUnitCount;

    private int dataSetMemberCount1;

    public int getDataSetMemberCount1()
    {
        return dataSetMemberCount1;
    }

    private Integer dataElementCount;

    public Integer getDataElementCount()
    {
        return dataElementCount;
    }

    private List<OrganisationUnit> dso;
    
    public List<OrganisationUnit> getDso()
    {
        return dso;
    }

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    // @SuppressWarnings( { "deprecation", "unchecked" } )
    public String execute()
        throws Exception
    {
        System.out.println( "Data Entry Status  Start Time  : " + new Date() );
        orgUnitCount = 0;

        ouMapDataElementCount = new HashMap<OrganisationUnit, List<Integer>>();// Map for DataElement count Intialization
        periodNameList = new ArrayList<String>();
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        if ( immChildOption != null && immChildOption.equalsIgnoreCase( "yes" ) )
        {
            //System.out.println( "Inside Drill Down" );
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );

            facilityLB = "immChildren";

            selectedDataSets = new ArrayList<String>();
            selectedDataSets.add( dsId );
        }

        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>();

        deInfo = "-1";
        for ( String ds : selectedDataSets )
        {
            DataSet dSet = dataSetService.getDataSet( Integer.parseInt( ds ) );
            selDataSet = dSet;
            for ( DataElement de : dSet.getDataElements() )
                deInfo += "," + de.getId();
        }

        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            @SuppressWarnings( "unused" )
            int number;

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            number = selectedOrgUnit.getChildren().size();
            orgUnitList = new ArrayList<OrganisationUnit>();

            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            while ( orgUnitIterator.hasNext() )
            {
                OrganisationUnit o = organisationUnitService.getOrganisationUnit( Integer
                    .parseInt( (String) orgUnitIterator.next() ) );
                orgUnitList.add( o );
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( o.getChildren() );
                Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( organisationUnits );
            }
        }
        else
        {
            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            OrganisationUnit o;
            while ( orgUnitIterator.hasNext() )
            {
                o = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitIterator.next() ) );
                orgUnitList.add( o );
                Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
                //displayPropertyHandler.handle( orgUnitList );
            }
        }

        // Set<OrganisationUnit> dSetSource = selDataSet.getSources();
        List<OrganisationUnit> dSetSource = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
        orgUnitInfo = "-1";
        Iterator<OrganisationUnit> ouIt = orgUnitList.iterator();
        while ( ouIt.hasNext() )
        {
            OrganisationUnit ou = ouIt.next();

            orgUnitCount = 0;
            if ( !dSetSource.contains( ou ) )
            {
                getDataSetAssignedOrgUnitCount( ou, dSetSource );

                if ( orgUnitCount > 0 )
                {
                    orgUnitInfo += "," + ou.getId();
                    getOrgUnitInfo( ou );
                }
                else
                {
                    ouIt.remove();
                }
            }
            else
            {
                orgUnitInfo += "," + ou.getId();
            }
        }

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );

        periodInfo = "-1";
        for ( Period p : periodList )
        {
            periodInfo += "," + p.getId();
        }

        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();

        dataSetMemberCount1 = 0;
        for ( DataElement de1 : dataElements )
        {
            dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
        }
        

        deInfo = getDEInfo( dataElements );

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		String pSDate = simpleDateFormat.format( startPeriod.getStartDate() );
		String pEDate = simpleDateFormat.format( endPeriod.getEndDate() );
		Map<String, String> dataStatusResultMap = new HashMap<String, String>( getDataStatusResultForAssingedOrgunits( orgUnitInfo, selDataSet.getId(), pSDate, pEDate, selDataSet.getPeriodType().getId(), dataSetMemberCount1, includeZeros ) );
		
		dso = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
		
		Set<OrganisationUnit> parentOrgUnits = new HashSet<OrganisationUnit>();
		parentOrgUnits.addAll( orgUnitList );
		parentOrgUnits.removeAll( dso );
        Collection<Integer> parentOrgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, parentOrgUnits ) );        
        String parentOrgUnitIdsByComma = getCommaDelimitedString( parentOrgUnitIds );
		Map<String, String> dataStatusAvgResultMap = new HashMap<String, String>( getDataStatusResultAvgForParents( parentOrgUnitIdsByComma, selDataSet.getId(), pSDate, pEDate, selDataSet.getPeriodType().getId(), dataSetMemberCount1, includeZeros ) );
		
		Map<Integer, String> orgUnit_DSO_Info = new HashMap<Integer, String>( getOrgUnit_DSO_Info( orgUnitInfo, selDataSet.getId() ) );
        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        while ( orgUnitListIterator.hasNext() )
        {
        	OrganisationUnit o = orgUnitListIterator.next();
        	
        	String tempVal = orgUnit_DSO_Info.get( o.getId() );
        	int orgUnitLevel = 1;
        	int orgUnit_DSO_Count = 0;
        	if( tempVal != null )
        	{
        		orgUnitLevel = Integer.parseInt( tempVal.split(":")[0] );
        		orgUnit_DSO_Count = Integer.parseInt( tempVal.split(":")[1] );
        	}
        	else
        	{
        		orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );
        		orgUnit_DSO_Count = 0;
        	}
        	
            orgUnitInfo = "" + o.getId();
            
            if ( maxOULevel < orgUnitLevel )
                maxOULevel = orgUnitLevel;

            if ( minOULevel > orgUnitLevel )
                minOULevel = orgUnitLevel;

            List<Integer> dsResults = new ArrayList<Integer>();
            List<Integer> deCounts = new ArrayList<Integer>();
            
            Iterator<Period> periodIterator = periodList.iterator();
            while ( periodIterator.hasNext() )
            {
            	Period p = (Period) periodIterator.next();
                periodInfo = "" + p.getId();
                dataElementCount = 0;

                if ( dso == null )
                {
                    dsResults.add( -1 );
                    deCounts.add( -1 );
                    continue;
                }
                else if ( !dso.contains( o ) )
                {
                    //System.out.println("Dataset : " + selDataSet.getName() + " not assign to " + o.getName() );
                    
                    //List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                    //childOrgUnits = filterChildOrgUnitsByDataSet( o, dso );
                    
                    if( orgUnit_DSO_Count <= 0 )
                    {
                        dsResults.add( -2 );
                        continue;
                    }
                    else
                    {
                        String dsResult = dataStatusAvgResultMap.get( o.getId() + ":" + p.getId() );
                        
                        if( dsResult != null )
                        {
                            dsResults.add( Integer.parseInt( dsResult.split(":")[1] ) );
                            deCounts.add( Integer.parseInt( dsResult.split(":")[0] ) );
                        }
                        else
                        {
                            dsResults.add( 0 );
                            deCounts.add( -1 );                	
                        }
                        continue;
                    }
                }
                else
                {
                	orgUnitInfo = "" + o.getId();

	                String dsResult = dataStatusResultMap.get( o.getId() + ":" + p.getId() );
	                if( dsResult != null )
	                {
	                    dsResults.add( Integer.parseInt( dsResult.split(":")[1] ) );
	                    deCounts.add( Integer.parseInt( dsResult.split(":")[0] ) );
	                }
	                else
	                {
	                    dsResults.add( 0 );
	                    deCounts.add( -1 );                	
	                }
                }
            }
            
            ouMapDataStatusResult.put( o, dsResults );
            ouMapDataElementCount.put( o, deCounts );
        }


		// For Level Names
		String ouLevelNames[] = new String[organisationUnitService.getNumberOfOrganisationalLevels() + 1];
		for ( int i = 0; i < ouLevelNames.length; i++ )
		{
		    ouLevelNames[i] = "Level" + i;
		}
		
		List<OrganisationUnitLevel> ouLevels = new ArrayList<OrganisationUnitLevel>( organisationUnitService.getFilledOrganisationUnitLevels() );
		for ( OrganisationUnitLevel ouL : ouLevels )
		{
		    ouLevelNames[ouL.getLevel()] = ouL.getName();
		}
		
		levelNames = new ArrayList<String>();
		int count1 = minOULevel;
		while ( count1 <= maxOULevel )
		{
		    levelNames.add( ouLevelNames[count1] );
		    count1++;
		}

        try
        {

        }
        finally
        {
            try
            {
                //deleteDataView( dataViewName );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }// finally block end

        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );

        System.out.println( "Data Entry Status  End Time  : " + new Date() );
        
        return SUCCESS;
    }

    public void getDataSetAssignedOrgUnitCount( OrganisationUnit organisationUnit, List<OrganisationUnit> dso )
    {
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            if ( dso.contains( child ) )
            {
                orgUnitCount++;
            }
            getDataSetAssignedOrgUnitCount( child, dso );
        }
    }

    
    
    public Map<String,String> getDataStatusResultForAssingedOrgunits( String orgUnitInfo, Integer dataSetId, String startDate, String endDate, Integer periodTypeId, Integer dsMemCount, String includeZero )
    {
		Map<String, String> dataStatusResultMap = new HashMap<String, String>();

        try
        {
			String query = "SELECT dsr.sourceid, dsr.periodid, dsr.entered, ROUND( ( dsr.entered / " + dsMemCount +" ) * 100, 0 ) as perc  " +
							" FROM " +
								"( SELECT dv.sourceid, COUNT(dv.value) as entered, p.periodid FROM datavalue dv " +
									" INNER JOIN period p ON p.periodid = dv.periodid " +
									" INNER JOIN datasetmembers dsm ON dsm.dataelementid = dv.dataelementid " +
									" WHERE " +
										" dsm.datasetid = " + dataSetId +" AND " +
										" p.startdate BETWEEN '"+ startDate +"' AND '"+ endDate +"' AND " +
										" p.periodtypeid = "+ periodTypeId +" AND " +
										" dv.sourceid in ( "+ orgUnitInfo +") ";
										if( includeZero == null )
										{
											query += " AND dv.value <> 0 ";
										}
									query += " GROUP BY dv.sourceid, p.periodid " +
								" )dsr";

			SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
				Integer periodId = rs.getInt( 2 );
                Integer dataEntryCount = rs.getInt( 3 );
				Integer dataEntryPercentage = rs.getInt( 4 );
				
				dataStatusResultMap.put( orgUnitId+":"+periodId, dataEntryCount+":"+dataEntryPercentage );
            }

        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }

        return dataStatusResultMap;
    }


    public Map<String,String> getDataStatusResultAvgForParents( String orgUnitInfo, Integer dataSetId, String startDate, String endDate, Integer periodTypeId, Integer dsMemCount, String includeZero )
    {
		Map<String, String> dataStatusResultMap = new HashMap<String, String>();

		int ouMaxLevel = organisationUnitService.getMaxOfOrganisationUnitLevels();
		
        try
        {
        	String query = "SELECT parent, periodid, entered, CASE WHEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) >= 100 THEN 100 " + 
							" WHEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) < 100 THEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) END "+
							" FROM "+
							" ( "+
							" SELECT sag.parent,sag.total_ou,SUM(IFNULL(sag1.entered,0)) AS entered,IFNULL(sag1.periodid,0) AS periodid "+
							" FROM " +
							"(" +
							" SELECT a.level,a.parent,a.organisationunitid,a.datasetid,b.total_ou FROM " +
							"( SELECT * FROM " +        	               
        	               " ( "+
        	               	" SELECT asd.level,asd.organisationunitid  AS 'parent',asd1.organisationunitid "+
        	               " FROM "+
							"( SELECT os.organisationunitid,os.idlevel1";
							for( int i = 2; i <= ouMaxLevel; i++ )
							{
								query += ",os.idlevel"+i;
							}
							
							query += ",os.level "+
									" FROM _orgunitstructure os "+
									")asd1"+

        	               " INNER JOIN "+
        	               "(SELECT os.organisationunitid,os.level FROM _orgunitstructure os WHERE organisationunitid IN ("+ orgUnitInfo +") )asd "+        	               
        	               "ON CASE ";
        	               for( int i = 1; i <= ouMaxLevel; i++ )
        	               {
								query += " WHEN asd.level="+i+" THEN asd1.idlevel"+i+"=asd.organisationunitid ";
        	               }							
							query += " END " + 
        	               ")sa"+
        	               " INNER JOIN " +
        	               "( SELECT datasetid,sourceid FROM datasetsource WHERE datasetid = "+ dataSetId +" )sa1 "+
        	               " ON sa.organisationunitid= sa1.sourceid "+        
        	               ")a "+
        	               " INNER JOIN "+         	           
        	               "("+
        	               "SELECT sa.parent,COUNT(sa.level) AS 'total_ou' FROM "+        	               
        	               "("+
        	               "SELECT asd.level,asd.organisationunitid  AS parent, asd1.organisationunitid FROM "+
							"( SELECT os.organisationunitid,os.idlevel1";
							for( int i = 2; i <= ouMaxLevel; i++ )
							{
								query += ",os.idlevel"+i;
							}
							
							query += ",os.level "+
									" FROM _orgunitstructure os "+
									")asd1 "+
        	               " INNER JOIN "+
        	               "(SELECT os.organisationunitid,os.level FROM _orgunitstructure os WHERE organisationunitid IN ("+ orgUnitInfo +") )asd "+        	               
        	               " ON CASE ";
        	               for( int i = 1; i <= ouMaxLevel; i++ )
        	               {
								query += " WHEN asd.level="+i+" THEN asd1.idlevel"+i+"=asd.organisationunitid ";
        	               }							
							query += " END " + 
        	               ")sa "+
        	               " INNER JOIN"+
        	               "( SELECT datasetid,sourceid FROM datasetsource WHERE datasetid= "+ dataSetId +")sa1 "+
        	               " ON sa.organisationunitid= sa1.sourceid GROUP BY parent "+                   
        	               ")b "+
        	               " ON a.parent=b.parent "+        	               
        	               ")sag"+
        	               " LEFT JOIN "+
        	               "("+
        	               "SELECT sag.sourceid,sag.entered,sag.periodid "+
        	               " FROM "+
        	               " (SELECT dv.sourceid,COUNT(dv.value) AS entered,p.periodid "+
        	               " FROM datavalue dv " +
        	               " INNER JOIN period p ON p.periodid=dv.periodid "+
        	               " WHERE dv.dataelementid IN (SELECT dataelementid FROM datasetmembers WHERE datasetid = "+ dataSetId +" )";
							if( includeZero == null )
							{
								query += " AND dv.value <> 0 ";
							}
							query +=" AND dv.sourceid IN ( "+        	       
        			       " SELECT sa.organisationunitid FROM " +        				       
        				       "( "+
        				       " SELECT asd1.organisationunitid "+
        				       " FROM "+
        				       "( SELECT os.organisationunitid,os.idlevel1";
   							for( int i = 2; i <= ouMaxLevel; i++ )
   							{
   								query += ",os.idlevel"+i;
   							}
   							
   							query += ",os.level "+
   									" FROM _orgunitstructure os "+
   									")asd1 "+
   									
        				       " INNER JOIN "+
        				       "(SELECT os.organisationunitid,os.level FROM _orgunitstructure os WHERE organisationunitid IN ( "+ orgUnitInfo +") )asd "+        				       
        				       " ON CASE ";
        				       
								for( int i = 1; i <= ouMaxLevel; i++ )
								{
									query += " WHEN asd.level="+i+" THEN asd1.idlevel"+i+"=asd.organisationunitid ";
								}
								
								query += " END " +
								
        				       ")sa "+
        			       " INNER JOIN"+
        				       "(SELECT datasetid,sourceid FROM datasetsource WHERE datasetid= "+ dataSetId +")sa1 "+
        			       " ON sa.organisationunitid= sa1.sourceid "+        
        	       ")"+                        
        	       " AND p.startdate BETWEEN '"+ startDate +"' AND '"+ endDate +"' AND periodtypeid = "+ periodTypeId +
        	       " GROUP BY dv.sourceid,p.periodid "+
        	       ")sag"+
        	       ")sag1"+
        	" ON sag.organisationunitid = sag1.sourceid "+
        	" GROUP BY parent,periodid "+
        	")final "+
        	" INNER JOIN organisationunit ou ON ou.organisationunitid=final.parent";
        	
								/*
			String query = "SELECT parent, periodid, entered, CASE WHEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) >= 100 THEN 100 " + 
							" WHEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) < 100 THEN ROUND((entered/(total_ou*"+ dsMemCount +"))*100, 0) END AS pers " +
							" FROM " +
							" ( " +
								" SELECT sag.parent,COUNT(sag.organisationunitid) AS total_ou,SUM(IFNULL(sag1.entered,0)) AS entered,IFNULL(sag1.periodid,0) AS periodid " +
									" FROM " +
									"( " +
										" SELECT * FROM " +							
										" ( " +
											" SELECT asd.level,asd.organisationunitid  AS parent, asd1.organisationunitid " +
											" FROM " + 
											"( SELECT os.organisationunitid,os.idlevel1";
											for( int i = 2; i <= ouMaxLevel; i++ )
											{
												query += ",os.idlevel"+i;
											}
											
											query += ",os.level "+
											
											" FROM _orgunitstructure os "+
											")asd1"+
										" INNER JOIN " +
										" (SELECT os.organisationunitid,os.level FROM _orgunitstructure os WHERE organisationunitid IN ("+ orgUnitInfo +") )asd "+							
										" ON CASE ";
										
											for( int i = 1; i <= ouMaxLevel; i++ )
											{
												query += " WHEN asd.level="+i+" THEN asd1.idlevel"+i+"=asd.organisationunitid ";
											}
											
										query += " END " + 
									")sa "+
								"INNER JOIN "+ 
								"(SELECT datasetid,sourceid	FROM datasetsource WHERE datasetid = "+ dataSetId +" )sa1 " +
								" ON sa.organisationunitid= sa1.sourceid "+								
								")sag"+
								" INNER JOIN "+ 
								"( "+
								" SELECT sag.sourceid,sag.entered,sag.periodid FROM (SELECT dv.sourceid,COUNT(dv.value) AS entered,p.periodid FROM datavalue dv "+
								" INNER JOIN period p ON p.periodid=dv.periodid "+ 
								" WHERE dv.dataelementid IN (SELECT dataelementid FROM datasetmembers WHERE datasetid = "+ dataSetId +" )"+
								" AND p.startdate BETWEEN '"+ startDate +"' AND '"+ endDate +"' AND periodtypeid = "+ periodTypeId + 
								" GROUP BY dv.sourceid,p.periodid"+
								")sag"+
								")sag1"+
					" ON sag.organisationunitid = sag1.sourceid "+
					" GROUP BY parent,periodid"+
					" )final"+
					" INNER JOIN organisationunit ou ON ou.organisationunitid=final.parent ";
*/
			//System.out.println( "Query: " + query );
			
			SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
				Integer periodId = rs.getInt( 2 );
                Integer dataEntryCount = rs.getInt( 3 );
				Integer dataEntryPercentage = rs.getInt( 4 );
				
				dataStatusResultMap.put( orgUnitId+":"+periodId, dataEntryCount+":"+dataEntryPercentage );
            }

        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }

        return dataStatusResultMap;
    }

    
    public Map<Integer,String> getOrgUnit_DSO_Info( String orgUnitInfo, Integer dataSetId )
    {
		Map<Integer, String> orgUnit_DSO_Info = new HashMap<Integer, String>();
		
		int ouMaxLevel = organisationUnitService.getMaxOfOrganisationUnitLevels();

        try
        {
			String query = " SELECT parent, LEVEL, COUNT(organisationunitid) FROM " +							
										" ( " +
											" SELECT asd.level,asd.organisationunitid  AS parent, asd1.organisationunitid " +
											" FROM "+ 
											"( SELECT os.organisationunitid,os.idlevel1";
											
											for( int i = 2; i <= ouMaxLevel; i++ )
											{
												query += ",os.idlevel"+i;
											}
											
											query += ",os.level "+
											" FROM _orgunitstructure os "+
											")asd1"+
										" INNER JOIN " +
										" (SELECT os.organisationunitid,os.level FROM _orgunitstructure os WHERE organisationunitid IN ("+ orgUnitInfo +") )asd "+							
										" ON CASE ";
											
											for( int i = 1; i <= ouMaxLevel; i++ )
											{
												query += " WHEN asd.level="+i+" THEN asd1.idlevel"+i+"=asd.organisationunitid ";
											}
										
										query += " END "+ 
									")sa "+
								"INNER JOIN "+ 
								"(SELECT datasetid,sourceid	FROM datasetsource WHERE datasetid = "+ dataSetId +" )sa1 " +
								" ON sa.organisationunitid= sa1.sourceid GROUP BY parent";								
								
			SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
				Integer level = rs.getInt( 2 );
                Integer childOrgUnitCount = rs.getInt( 3 );
				
                orgUnit_DSO_Info.put( orgUnitId, level+":"+childOrgUnitCount );
            }

        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }

        return orgUnit_DSO_Info;
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

    private void getOrgUnitInfo( OrganisationUnit organisationUnit )
    {
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            orgUnitInfo += "," + child.getId();
            getOrgUnitInfo( child );
        }
    }

    
    private String getDEInfo( Collection<DataElement> dataElements )
    {
        StringBuffer deInfo = new StringBuffer( "-1" );

        for ( DataElement de : dataElements )
        {
            deInfo.append( "," ).append( de.getId() );
        }
        return deInfo.toString();
    }
    
    
}// class end
