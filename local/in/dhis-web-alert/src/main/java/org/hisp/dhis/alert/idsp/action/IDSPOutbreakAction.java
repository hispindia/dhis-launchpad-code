package org.hisp.dhis.alert.idsp.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.alert.util.AlertUtility;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version IDSPOutbreakAction.java Jun 5, 2012 12:43:10 PM
 */

public class IDSPOutbreakAction
    implements Action
{

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService orgUnitGroupService;

    public void setOrgUnitGroupService( OrganisationUnitGroupService orgUnitGroupService )
    {
        this.orgUnitGroupService = orgUnitGroupService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AlertUtility alertUtility;

    public void setAlertUtility( AlertUtility alertUtility )
    {
        this.alertUtility = alertUtility;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private String resultString;

    public String getResultString()
    {
        return resultString;
    }

    private List<OrganisationUnit> immChildrenList;

    public List<OrganisationUnit> getImmChildrenList()
    {
        return immChildrenList;
    }

    Map<String, Integer> orgUnit_ProgramMap;

    public Map<String, Integer> getOrgUnit_ProgramMap()
    {
        return orgUnit_ProgramMap;
    }

    Map<String, String> outBreakAlertMap;

    public Map<String, String> getOutBreakAlertMap()
    {
        return outBreakAlertMap;
    }

    Map<String, String> outBreakAlertColorMap;

    public Map<String, String> getOutBreakAlertColorMap()
    {
        return outBreakAlertColorMap;
    }

    Map<String, Integer> totalEnrollCountForSelDateMap;

    public Map<String, Integer> getTotalEnrollCountForSelDateMap()
    {
        return totalEnrollCountForSelDateMap;
    }

    Integer totalRegCountForSelDate = 0;

    public Integer getTotalRegCountForSelDate()
    {
        return totalRegCountForSelDate;
    }

    Integer totalRegCount = 0;

    public Integer getTotalRegCount()
    {
        return totalRegCount;
    }

    List<Integer> totalRegCountList;

    public List<Integer> getTotalRegCountList()
    {
        return totalRegCountList;
    }

    List<Integer> totalRegCountListForSelDate;

    public List<Integer> getTotalRegCountListForSelDate()
    {
        return totalRegCountListForSelDate;
    }

    List<Program> programList;

    public List<Program> getProgramList()
    {
        return programList;
    }

    String rootOrgUnitName;

    public String getRootOrgUnitName()
    {
        return rootOrgUnitName;
    }

    List<Integer> rootOrgUnitEnrollCountList;

    public List<Integer> getRootOrgUnitEnrollCountList()
    {
        return rootOrgUnitEnrollCountList;
    }

    String drillDownOrgUnitId;

    public void setDrillDownOrgUnitId( String drillDownOrgUnitId )
    {
        this.drillDownOrgUnitId = drillDownOrgUnitId;
    }

    private String navigationString;

    public String getNavigationString()
    {
        return navigationString;
    }

    private String toDaysDate;

    public String getToDaysDate()
    {
        return toDaysDate;
    }

    List<String> normInfo;

    public List<String> getNormInfo()
    {
        return normInfo;
    }

    List<String> normNames;

    public List<String> getNormNames()
    {
        return normNames;
    }

    private String populationDeId;

    private Integer orgUnitGroupId;

    private String dataSetId;
    
    // for form P
    
    private List<String> normInfoFormP;
    
    public List<String> getNormInfoFormP()
    {
        return normInfoFormP;
    }
    
    private List<String> normNamesFormP;
    
    public List<String> getNormNamesFormP()
    {
        return normNamesFormP;
    }
    
    
    private Integer orgUnitGroupIdFormP;

    private String dataSetIdFormP;
    
    private String drillDownOrgUnitIdFormP;
    

    public void setDrillDownOrgUnitIdFormP( String drillDownOrgUnitIdFormP )
    {
        this.drillDownOrgUnitIdFormP = drillDownOrgUnitIdFormP;
    }
    
    private String navigationStringFormP;
    
    public String getNavigationStringFormP()
    {
        return navigationStringFormP;
    }
    
    private String rootOrgUnitNameFormP;
    

    public String getRootOrgUnitNameFormP()
    {
        return rootOrgUnitNameFormP;
    }
    
    private List<OrganisationUnit> immChildrenListFormP;
    
    public List<OrganisationUnit> getImmChildrenListFormP()
    {
        return immChildrenListFormP;
    }
    
    private Map<String, String> outBreakAlertMapFormP;
    
    public Map<String, String> getOutBreakAlertMapFormP()
    {
        return outBreakAlertMapFormP;
    }

    private Map<String, String> outBreakAlertColorMapFormP;
    
    public Map<String, String> getOutBreakAlertColorMapFormP()
    {
        return outBreakAlertColorMapFormP;
    }

    
    
    
    
    
    
    
    // for form L
    
    private List<String> normInfoFormL;
    
    public List<String> getNormInfoFormL()
    {
        return normInfoFormL;
    }
    
    private List<String> normNamesFormL;
    
    public List<String> getNormNamesFormL()
    {
        return normNamesFormL;
    }
    
    
    private Integer orgUnitGroupIdFormL;

    private String dataSetIdFormL;
    
    private String drillDownOrgUnitIdFormL;
    

    public void setDrillDownOrgUnitIdFormL( String drillDownOrgUnitIdFormL )
    {
        this.drillDownOrgUnitIdFormL = drillDownOrgUnitIdFormL;
    }
    
    private String navigationStringFormL;
    
    public String getNavigationStringFormL()
    {
        return navigationStringFormL;
    }
    
    private String rootOrgUnitNameFormL;
    

    public String getRootOrgUnitNameFormL()
    {
        return rootOrgUnitNameFormL;
    }
    
    private List<OrganisationUnit> immChildrenListFormL;
    
    public List<OrganisationUnit> getImmChildrenListFormL()
    {
        return immChildrenListFormL;
    }
    
    private Map<String, String> outBreakAlertMapFormL;
    
    public Map<String, String> getOutBreakAlertMapFormL()
    {
        return outBreakAlertMapFormL;
    }

    private Map<String, String> outBreakAlertColorMapFormL;
    
    public Map<String, String> getOutBreakAlertColorMapFormL()
    {
        return outBreakAlertColorMapFormL;
    }

    
    private List<OrganisationUnit> formSDataSetSourceList;
    
    public List<OrganisationUnit> getFormSDataSetSourceList()
    {
        return formSDataSetSourceList;
    }
    
    private List<OrganisationUnit> formPDataSetSourceList;
    
    public List<OrganisationUnit> getFormPDataSetSourceList()
    {
        return formPDataSetSourceList;
    }

    private List<OrganisationUnit> formLDataSetSourceList;
    
    public List<OrganisationUnit> getFormLDataSetSourceList()
    {
        return formLDataSetSourceList;
    }
    
    private String periodString;
    
    public String getPeriodString()
    {
        return periodString;
    }
    
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------

    
 

    public String execute() throws Exception
    {
        //statementManager.initialise();
        int idspFlag = 0;
        if ( currentUserService.getCurrentUser().getId() != 0 )
        {
            UserCredentials userCredentials = userService.getUserCredentialsByUsername( currentUserService.getCurrentUsername() );

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                if ( userAuthorityGroup.getAuthorities().contains( ("F_REPORT_IDSP") ) )
                {
                    idspFlag = 1;
                    break;
                }
            }
        }

        if ( idspFlag == 0 )
        {
            return "standard";
        }

        normInfo = new ArrayList<String>();
        normNames = new ArrayList<String>();
        immChildrenList = new ArrayList<OrganisationUnit>();
        programList = new ArrayList<Program>();
        rootOrgUnitEnrollCountList = new ArrayList<Integer>();
        totalRegCountList = new ArrayList<Integer>();
        totalRegCountListForSelDate = new ArrayList<Integer>();
        totalEnrollCountForSelDateMap = new HashMap<String, Integer>();
        orgUnit_ProgramMap = new HashMap<String, Integer>();
        
        formSDataSetSourceList = new ArrayList<OrganisationUnit>();
        formPDataSetSourceList = new ArrayList<OrganisationUnit>();
        formLDataSetSourceList = new ArrayList<OrganisationUnit>();
        
        outBreakAlertMap = new HashMap<String, String>();
        outBreakAlertColorMap = new HashMap<String, String>();

        resultString = "";

        navigationString = "Form S ( Possible Diagnosis )";

        String periodIdString = alertUtility.getPeriodIdForIDSPOutBreak();
        String periodId = periodIdString.split( "::" )[0];
        
        
        //navigationString += " ( " + periodIdString.split( "::" )[1] + " )";

        periodString = " ( " + periodIdString.split( "::" )[1] + " )";
        
        
        String populationPeriodId = alertUtility.getPeriodIdForIDSPPopulation();

        normInfo = getNormInfoFromXML();
        
        DataSet dSetFormS = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        formSDataSetSourceList = new ArrayList<OrganisationUnit>( dSetFormS.getSources() );
        

        if ( normInfo != null && normInfo.size() > 0 )
        {
            List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

            if ( drillDownOrgUnitId != null )
            {
                rootOrgUnitList.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( drillDownOrgUnitId ) ) );
                List<OrganisationUnit> orgUnitBrach = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( Integer.parseInt( drillDownOrgUnitId ) ) );
                int flag = 1;
                for ( OrganisationUnit orgUnit : orgUnitBrach )
                {
                    if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( orgUnit ) )
                    {
                        flag = 2;
                    }
                    if ( flag == 2 )
                    {
                        navigationString += " -> <a href=\"idspoutbreak.action?drillDownOrgUnitId=" + orgUnit.getId()
                            + "\">" + orgUnit.getName() + "</a>";
                    }
                }
            }
            else
            {
                rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );
            }

            for ( OrganisationUnit orgUnit : rootOrgUnitList )
            {
                rootOrgUnitName = orgUnit.getName() + ", ";
                List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                
                //System.out.println( orgUnit.getName() + "---" + tempOuList.size() );
                
                Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );

                //immChildrenList.addAll( tempOuList );

                for ( OrganisationUnit ou : tempOuList )
                {       
                    //System.out.println( ou.getName() ); 
                    
                    List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                    String orgUnitIdsByComma1 = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );
                    
                    if( ( childTree == null || childTree.isEmpty() ) && !formSDataSetSourceList.contains( ou ) )
                    {
                        continue;
                    }
                    
                    if( childTree != null && childTree.size() >= 1 && !formSDataSetSourceList.contains( ou )  )
                    {
                        int flag = 1;
                        for( OrganisationUnit ou1 : childTree )
                        {
                            if( formSDataSetSourceList.contains( ou1 ) ) 
                            {
                                flag = 2;
                                break;
                            }                                
                        }
                        
                        if( flag == 1 ) continue;
                    }
                    
                    //System.out.println( ou.getName() );
                    
                    immChildrenList.add( ou );
                    
                    //System.out.println( immChildrenList.size() );
                    
                    
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    childTree.retainAll( orgUnitGroupMembers );
                    String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );

                    int populationData = 0;
                    int confirmedCount = 0;
                    
                    if( orgUnitIdsByComma1 != null && !orgUnitIdsByComma1.trim().equals("") )
                    {
                    	populationData = alertUtility.getAggregatedData( orgUnitIdsByComma1, populationDeId, populationPeriodId );
                    }

                    if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                    {
                    	confirmedCount = alertUtility.getConfirmedCount( orgUnitIdsByComma, dataSetId, periodId );
                    }
                    
                    int totalSubcentreCount = childTree.size();
                    
                    //System.out.println( confirmedCount + " : " + totalSubcentreCount );

                    for ( String norm : normInfo )
                    {
                        //String normId = norm.split( "@:@" )[0];
                        String caseId = norm.split( "@:@" )[1];
                        String deathId = norm.split( "@:@" )[2];
                        String normName = norm.split( "@:@" )[3];

                        int caseData = 0;
                        int deathData = 0;
                        
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            caseData = alertUtility.getAggregatedData( orgUnitIdsByComma, caseId, periodId );
                        }
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            deathData = alertUtility.getAggregatedData( orgUnitIdsByComma, deathId, periodId );
                        }

                        if ( deathData >= 1 )
                        {
                            outBreakAlertMap.put( normName + ":" + ou.getId(), deathData + " Deaths" );

                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "PINK" );
                            }
                            else
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "RED" );
                            }
                        }
                        else
                        {
                            long minLimit = Math.round( populationData / 1000.0 );
                            long maxLimit = Math.round( (populationData / 1000.0) * 5 );
                            outBreakAlertMap.put( normName + ":" + ou.getId(), caseData + " Cases" );

			    //System.out.println( minLimit + " :  " + maxLimit + " : " + populationData );
                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "PINK" );
                                continue;
                            }

                            if ( minLimit == 0 || maxLimit == 0 )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "WHITE" );
                                continue;
                            }

                            if ( caseData > maxLimit )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "RED" );
                            }
                            
                            else if ( caseData == 0  )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            
                            else if ( caseData > 0 && caseData <= maxLimit )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            
                            /*
                            
                            else if ( caseData <= minLimit )
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            else
                            {
                                outBreakAlertColorMap.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            */
                        }
                    }
                }
            }
        }

        
// information Regarding Form P   
        
        
        navigationStringFormP = "Form P ( Probable Diagnosis )";
        normInfoFormP = new ArrayList<String>();   
        normNamesFormP = new ArrayList<String>();
        
        normInfoFormP = getNormInfoFormPFromXML();
        
        DataSet dSetFormP = dataSetService.getDataSet( Integer.parseInt( dataSetIdFormP ) );
        formPDataSetSourceList = new ArrayList<OrganisationUnit>( dSetFormP.getSources() );
        
        immChildrenListFormP = new ArrayList<OrganisationUnit>();
        
        outBreakAlertMapFormP = new HashMap<String, String>();
        outBreakAlertColorMapFormP = new HashMap<String, String>();
        
        
        if ( normInfoFormP != null && normInfoFormP.size() > 0 )
        {
            List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupIdFormP );

            if ( drillDownOrgUnitIdFormP != null )
            {
                rootOrgUnitList.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( drillDownOrgUnitIdFormP ) ) );
                List<OrganisationUnit> orgUnitBrach = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( Integer.parseInt( drillDownOrgUnitIdFormP ) ) );
                int flag = 1;
                for ( OrganisationUnit orgUnit : orgUnitBrach )
                {
                    if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( orgUnit ) )
                    {
                        flag = 2;
                    }
                    if ( flag == 2 )
                    {
                        navigationStringFormP += " -> <a href=\"idspoutbreak.action?drillDownOrgUnitIdFormP=" + orgUnit.getId()
                            + "\">" + orgUnit.getName() + "</a>";
                    }
                }
            }
            else
            {
                rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );
            }
            
            for ( OrganisationUnit orgUnit : rootOrgUnitList )
            {
                rootOrgUnitNameFormP = orgUnit.getName() + ", ";
                List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );

                if( formPDataSetSourceList.contains( orgUnit ) )
                {
                    tempOuList.add( orgUnit );
                }
                
                //immChildrenListFormP.addAll( tempOuList );
                
                for ( OrganisationUnit ou : tempOuList )
                {
                    List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                    String orgUnitIdsByComma1 = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );
                    
                    //System.out.println( ou.getName() + " : " + orgUnitIdsByComma1 + " : " + childTree.size() );
                    
                    if( ( childTree == null || childTree.isEmpty() ) && !formPDataSetSourceList.contains( ou ) )
                    {
                        System.out.println( "inside if : " + ou.getName() + " : " + orgUnitIdsByComma1 + " : " );
                        continue;
                    }
                    
                    if( childTree != null && childTree.size() >= 1 && !formPDataSetSourceList.contains( ou )  )
                    {
                        int flag = 1;
                        for( OrganisationUnit ou1 : childTree )
                        {
                            if( formPDataSetSourceList.contains( ou1 ) ) 
                            {
                                flag = 2;
                                break;
                            }                                
                        }
                        
                        if( flag == 1 ) continue;
                    }
                    
                    immChildrenListFormP.add( ou );
                    
                    
                    
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    childTree.retainAll( orgUnitGroupMembers );
                    String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );

                    int populationData = 0;
                    int confirmedCount = 0;
                    
                    if( orgUnitIdsByComma1 != null && !orgUnitIdsByComma1.trim().equals("") )
                    {
                        populationData = alertUtility.getAggregatedData( orgUnitIdsByComma1, populationDeId, populationPeriodId );
                    }

                    if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                    {
                        confirmedCount = alertUtility.getConfirmedCount( orgUnitIdsByComma, dataSetIdFormP, periodId );
                    }
                    
                    int totalSubcentreCount = childTree.size();
                    
                    for ( String norm : normInfoFormP )
                    {
                        //String normId = norm.split( "@:@" )[0];
                        String caseId = norm.split( "@:@" )[1];
                        String deathId = norm.split( "@:@" )[2];
                        String normName = norm.split( "@:@" )[3];

                        int caseData = 0;
                        
                        int deathData = 0;
                        
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            caseData = alertUtility.getAggregatedData( orgUnitIdsByComma, caseId, periodId );
                        }
                        
                       
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            deathData = alertUtility.getAggregatedData( orgUnitIdsByComma, deathId, periodId );
                        }
                        
                        
                        if ( deathData >= 1 )
                        {
                            outBreakAlertMapFormP.put( normName + ":" + ou.getId(), deathData + " Deaths" );

                            if( formPDataSetSourceList.contains( ou ) )
                            {
                                totalSubcentreCount = 1;
                            }
                            
                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "PINK" );
                            }
                            else
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "RED" );
                            }
                        }
                        
                        
                        else
                        {
                            long minLimit = Math.round( populationData / 1000.0 );
                            long maxLimit = Math.round( (populationData / 1000.0) * 5 );
                            
                            outBreakAlertMapFormP.put( normName + ":" + ou.getId(), caseData + " Cases" );

                            //System.out.println( minLimit + " :  " + maxLimit + " : " + populationData );
                            
                            if( formPDataSetSourceList.contains( ou ) )
                            {
                                totalSubcentreCount = 1;
                            }
                            
                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "PINK" );
                                continue;
                            }

                            if ( minLimit == 0 || maxLimit == 0 )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "WHITE" );
                                continue;
                            }

                            if ( caseData > maxLimit )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "RED" );
                            }
                            
                            else if ( caseData == 0  )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            
                            else if ( caseData > 0 && caseData <= maxLimit )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            
                            /*
                            else if ( caseData <= minLimit )
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            
                            
                            else
                            {
                                outBreakAlertColorMapFormP.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            */
                        }
                    }
                }
            }
            
        }
        
              
// information Regarding Form L        
        
        
        navigationStringFormL = "Form L (Confirmed Diagnosis)";
        normInfoFormL = new ArrayList<String>();   
        normNamesFormL = new ArrayList<String>();
        
        normInfoFormL = getNormInfoFormLFromXML();
        
        DataSet dSetFormL = dataSetService.getDataSet( Integer.parseInt( dataSetIdFormL ) );
        formLDataSetSourceList = new ArrayList<OrganisationUnit>( dSetFormL.getSources() );
        
        immChildrenListFormL = new ArrayList<OrganisationUnit>();
        
        outBreakAlertMapFormL = new HashMap<String, String>();
        outBreakAlertColorMapFormL = new HashMap<String, String>();
        
        
        if ( normInfoFormL != null && normInfoFormL.size() > 0 )
        {
            List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupIdFormL );

            if ( drillDownOrgUnitIdFormL != null )
            {
                rootOrgUnitList.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( drillDownOrgUnitIdFormL ) ) );
                List<OrganisationUnit> orgUnitBrach = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( Integer.parseInt( drillDownOrgUnitIdFormL ) ) );
                int flag = 1;
                for ( OrganisationUnit orgUnit : orgUnitBrach )
                {
                    if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( orgUnit ) )
                    {
                        flag = 2;
                    }
                    if ( flag == 2 )
                    {
                        navigationStringFormL += " -> <a href=\"idspoutbreak.action?drillDownOrgUnitIdFormL=" + orgUnit.getId()
                            + "\">" + orgUnit.getName() + "</a>";
                    }
                }
            }
            else
            {
                rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );
            }
            
            for ( OrganisationUnit orgUnit : rootOrgUnitList )
            {
                rootOrgUnitNameFormL = orgUnit.getName() + ", ";
                List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );

                //immChildrenListFormL.addAll( tempOuList );
                
                if( formLDataSetSourceList.contains( orgUnit ) )
                {
                    tempOuList.add( orgUnit );
                }
                
                for ( OrganisationUnit ou : tempOuList )
                {
                    List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                    String orgUnitIdsByComma1 = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );
                    
                    if( ( childTree == null || childTree.isEmpty() ) && !formLDataSetSourceList.contains( ou ) )
                    {
                        continue;
                    }
                    
                    if( childTree != null && childTree.size() >= 1 && !formLDataSetSourceList.contains( ou )  )
                    {
                        int flag = 1;
                        for( OrganisationUnit ou1 : childTree )
                        {
                            if( formLDataSetSourceList.contains( ou1 ) ) 
                            {
                                flag = 2;
                                break;
                            }                                
                        }
                        
                        if( flag == 1 ) continue;
                    }
                    
                    immChildrenListFormL.add( ou );
                    
                    
                    
                    
                    List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                    childTree.retainAll( orgUnitGroupMembers );
                    String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );

                    int populationData = 0;
                    int confirmedCount = 0;
                    
                    if( orgUnitIdsByComma1 != null && !orgUnitIdsByComma1.trim().equals("") )
                    {
                        populationData = alertUtility.getAggregatedData( orgUnitIdsByComma1, populationDeId, populationPeriodId );
                    }

                    if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                    {
                        confirmedCount = alertUtility.getConfirmedCount( orgUnitIdsByComma, dataSetIdFormL, periodId );
                    }
                    
                    int totalSubcentreCount = childTree.size();
                    
                    for ( String norm : normInfoFormL )
                    {
                        //String normId = norm.split( "@:@" )[0];
                        String caseId = norm.split( "@:@" )[1];
                        String deathId = norm.split( "@:@" )[2];
                        String normName = norm.split( "@:@" )[3];
                        
                        String caseColorValidation = norm.split( "@:@" )[4];

                        int caseData = 0;
                        
                        int deathData = 0;
                        
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            caseData = alertUtility.getAggregatedData( orgUnitIdsByComma, caseId, periodId );
                        }
                        
                        
                        if( orgUnitIdsByComma != null && !orgUnitIdsByComma.trim().equals("") )
                        {
                            deathData = alertUtility.getAggregatedData( orgUnitIdsByComma, deathId, periodId );
                        }
                        
                        
                        if ( deathData >= 1 )
                        {
                            outBreakAlertMapFormL.put( normName + ":" + ou.getId(), deathData + " Deaths" );

                            if( formLDataSetSourceList.contains( ou ) )
                            {
                                totalSubcentreCount = 1;
                            }
                            
                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "PINK" );
                            }
                            else
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "RED" );
                            }
                        }
                        
                        else if( Integer.parseInt( caseColorValidation ) != -1 && caseData >= Integer.parseInt( caseColorValidation ) )
                        {
                            outBreakAlertMapFormL.put( normName + ":" + ou.getId(), caseData + " Cases" );
                            outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "RED" );
                        }
                        else
                        {
                            long minLimit = Math.round( populationData / 1000.0 );
                            long maxLimit = Math.round( (populationData / 1000.0 ) * 5 );
                            
                            outBreakAlertMapFormL.put( normName + ":" + ou.getId(), caseData + " Cases" );

                            //System.out.println( minLimit + " :  " + maxLimit + " : " + populationData );
                            
                            if( formLDataSetSourceList.contains( ou ) )
                            {
                                totalSubcentreCount = 1;
                            }
                            
                            
                            if ( confirmedCount != totalSubcentreCount )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "PINK" );
                                continue;
                            }

                            if ( minLimit == 0 || maxLimit == 0  )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "WHITE" );
                                continue;
                            }
                            
                            if ( caseData > maxLimit )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "RED" );
                            }
                            
                            else if ( caseData == 0  )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            
                            else if ( caseData > 0 && caseData <= maxLimit )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            
                            /*
                            else if ( caseData <= minLimit )
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "GREEN" );
                            }
                            
                            
                            else
                            {
                                outBreakAlertColorMapFormL.put( normName + ":" + ou.getId(), "YELLOW" );
                            }
                            */
                        }
                    }
                }
            }
            
        }
        
        //statementManager.destroy();

        return SUCCESS;
    }

    public List<String> getNormInfoFromXML()
    {
        List<String> normInfo = new ArrayList<String>();
        String raFolderName = alertUtility.getRAFolderName();

        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "OutBreaks.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DHIS_HOME is not set" );
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {

                return null;
            }

            populationDeId = doc.getElementsByTagName( "population" ).item( 0 ).getFirstChild().getNodeValue();

            dataSetId = doc.getElementsByTagName( "dataset" ).item( 0 ).getFirstChild().getNodeValue();

            orgUnitGroupId = Integer.parseInt( doc.getElementsByTagName( "orgunitgroup" ).item( 0 ).getFirstChild()
                .getNodeValue() );

            NodeList listOfNorms = doc.getElementsByTagName( "norm" );
            int totalNorms = listOfNorms.getLength();
            
            //System.out.println( " Form S Length :  " + totalNorms );
            
            for ( int s = 0; s < totalNorms; s++ )
            {
                Element element = (Element) listOfNorms.item( s );
                String normId = element.getAttribute( "id" );
                String caseId = element.getAttribute( "caseid" );
                String caseColorValidation = element.getAttribute( "casecolorvalidation" );
                String deathId = element.getAttribute( "deathid" );
                String lableName = element.getAttribute( "name" );

                //System.out.println( " Form S name :  " + lableName );
                
                if ( normId != null && caseId != null && deathId != null && lableName != null && caseColorValidation != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName +  "@:@" + caseColorValidation );
                    normNames.add( lableName );
                }                
                
                /*
                if ( normId != null && caseId != null && deathId != null && lableName != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName );
                    normNames.add( lableName );
                }
                */
                
                
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }

        return normInfo;
    }

    
    
    public List<String> getNormInfoFormPFromXML()
    {
        List<String> normInfo = new ArrayList<String>();
        String raFolderName = alertUtility.getRAFolderName();

        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "OutBreaksFormP.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DHIS_HOME is not set" );
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {

                return null;
            }

            populationDeId = doc.getElementsByTagName( "population" ).item( 0 ).getFirstChild().getNodeValue();

            dataSetIdFormP = doc.getElementsByTagName( "dataset" ).item( 0 ).getFirstChild().getNodeValue();

            orgUnitGroupIdFormP = Integer.parseInt( doc.getElementsByTagName( "orgunitgroup" ).item( 0 ).getFirstChild()
                .getNodeValue() );

            NodeList listOfNorms = doc.getElementsByTagName( "norm" );
            int totalNorms = listOfNorms.getLength();
            
            //System.out.println( " Form P Length :  " + totalNorms );
            
            for ( int s = 0; s < totalNorms; s++ )
            {
                Element element = (Element) listOfNorms.item( s );
                String normId = element.getAttribute( "id" );
                String caseId = element.getAttribute( "caseid" );
                String caseColorValidation = element.getAttribute( "casecolorvalidation" );
                String deathId = element.getAttribute( "deathid" );
                String lableName = element.getAttribute( "name" );
                
                //System.out.println( " Form P name :  " + lableName );
                
                if ( normId != null && caseId != null && deathId != null && lableName != null && caseColorValidation != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName +  "@:@" + caseColorValidation );
                    normNamesFormP.add( lableName );
                }
                /*
                if ( normId != null && caseId != null && deathId != null && lableName != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName );
                    normNamesFormP.add( lableName );
                }
                */
                
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }

        return normInfo;
    }    
    
    
    public List<String> getNormInfoFormLFromXML()
    {
        List<String> normInfo = new ArrayList<String>();
        String raFolderName = alertUtility.getRAFolderName();

        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "OutBreaksFormL.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println( "DHIS_HOME is not set" );
            return null;
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {

                return null;
            }

            populationDeId = doc.getElementsByTagName( "population" ).item( 0 ).getFirstChild().getNodeValue();

            dataSetIdFormL = doc.getElementsByTagName( "dataset" ).item( 0 ).getFirstChild().getNodeValue();

            orgUnitGroupIdFormL = Integer.parseInt( doc.getElementsByTagName( "orgunitgroup" ).item( 0 ).getFirstChild()
                .getNodeValue() );

            NodeList listOfNorms = doc.getElementsByTagName( "norm" );
            int totalNorms = listOfNorms.getLength();
            
            //System.out.println( " Form L Length :  " + totalNorms );
            
            for ( int s = 0; s < totalNorms; s++ )
            {
                Element element = (Element) listOfNorms.item( s );
                String normId = element.getAttribute( "id" );
                String caseId = element.getAttribute( "caseid" );
                String caseColorValidation = element.getAttribute( "casecolorvalidation" );
                String deathId = element.getAttribute( "deathid" );
                String lableName = element.getAttribute( "name" );
                
                //System.out.println( " Form L name :  " + lableName );
                
                if ( normId != null && caseId != null && deathId != null && lableName != null && caseColorValidation != null )
                {
                    normInfo.add( normId + "@:@" + caseId + "@:@" + deathId + "@:@" + lableName +  "@:@" + caseColorValidation );
                    normNamesFormL.add( lableName );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }

        return normInfo;
    }    
        
}
