package org.hisp.dhis.ivb.aggregation.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.ivb.util.AggregationManager;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;

import com.opensymphony.xwork2.Action;

public class RunAggregationQueryAction
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private AggregationManager aggregationManager;

    public void setAggregationManager( AggregationManager aggregationManager )
    {
        this.aggregationManager = aggregationManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private IVBUtil ivbUtil;
    
    public void setIvbUtil( IVBUtil ivbUtil )
    {
        this.ivbUtil = ivbUtil;
    }

    private IndicatorService indicatorService;
    
    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String importStatus = "";

    public String getImportStatus()
    {
        return importStatus;
    }
    private String language;

    public String getLanguage()
    {
        return language;
    }

    private String userName;

    public String getUserName()
    {
        return userName;
    }
    private int messageCount;
    
    public int getMessageCount()
    {
        return messageCount;
    }

    private String adminStatus;
    
    public String getAdminStatus()
    {
        return adminStatus;
    }
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        userName = currentUserService.getCurrentUser().getUsername();

        if ( i18nService.getCurrentLocale() == null )
        {
            language = "en";
        }
        else
        {
            language = i18nService.getCurrentLocale().getLanguage();
        }
        messageCount = (int) messageService.getUnreadMessageConversationCount();
        List<UserGroup> userGrps = new ArrayList<UserGroup>( currentUserService.getCurrentUser().getGroups() );
        if ( userGrps.contains( configurationService.getConfiguration().getFeedbackRecipients() ) )
        {
            adminStatus = "Yes";
        }
        else
        {
            adminStatus = "No";
        }
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();

        Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );

        /*
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMM" );
        String curMonth = simpleDateFormat.format( new Date() );
        Period period = PeriodType.getPeriodFromIsoString( curMonth );
        period = periodService.reloadPeriod( period );
        */

        Period period = ivbUtil.getCurrentPeriod( new QuarterlyPeriodType(), new Date() );
        period = periodService.reloadPeriod( period );
        
        Map<Indicator, Map<OrganisationUnit, Integer>> individualKeyFlagResultMap = new HashMap<Indicator, Map<OrganisationUnit, Integer>>();

        //--------------------------------------------------------------------------------
        // KEY FLAG SCORE
        //--------------------------------------------------------------------------------
		
        Set<CaseAggregationCondition> conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getCaseAggregationConditionByOperator( Lookup.AGG_TYPE_BY_KEYFLAG_SCORE ) );

        CaseAggregationCondition condition1 = conditions.iterator().next();
        
        DataElement dataElement = condition1.getAggregationDataElement();

        individualKeyFlagResultMap = aggregationManager.calculateKeyFlagScore( period, dataElement, orgUnitList );
            
        for( Indicator keyFlagIndicator : individualKeyFlagResultMap.keySet() )
        {
            Integer keyFlagScore = 0;
            Set<AttributeValue> attributeValues = new HashSet<AttributeValue>( keyFlagIndicator.getAttributeValues() );          

            for ( AttributeValue attributeValue : attributeValues )
            {
                if ( attributeValue.getAttribute().getName().equalsIgnoreCase( "Key Flag Score" ) )
                {
                    try
                    {
                        keyFlagScore = Integer.parseInt( attributeValue.getValue() );
                    }
                    catch( Exception e )
                    {
                        keyFlagScore = 0;
                    }
                }
            }

            Map<OrganisationUnit, Integer> orgUnitResultMap = new HashMap<OrganisationUnit, Integer>( individualKeyFlagResultMap.get( keyFlagIndicator ) );
            
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                Integer keyFlagValue = orgUnitResultMap.get( orgUnit );
                if( keyFlagValue != null && keyFlagValue == 1 )
                {
                    Integer tempSumScore = aggregationResultMap.get( orgUnit.getId()+":"+dataElement.getId() );
                    if( tempSumScore == null )
                    {
                        tempSumScore = 0;
                    }
                    tempSumScore += keyFlagScore;
                    aggregationResultMap.put( orgUnit.getId()+":"+dataElement.getId(), tempSumScore );
                }
            }
        }

        //--------------------------------------------------------------------------------
        // KEY FLAG
        //--------------------------------------------------------------------------------

        conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getCaseAggregationConditionByOperator( Lookup.AGG_TYPE_BY_KEYFLAG ) );
        
        for ( CaseAggregationCondition condition : conditions )
        {
            dataElement = condition.getAggregationDataElement();

            Integer keyFlagIndicatorId = Integer.parseInt( condition.getAggregationExpression() );
            
            Indicator keyFlagIndicator = indicatorService.getIndicator( keyFlagIndicatorId );
            
            //aggregationResultMap.putAll( aggregationManager.calculateKeyFlagCount( period, dataElement, orgUnitList, keyFlagIndicator ) );
            
            Map<OrganisationUnit, Integer> orgUnitResultMap = new HashMap<OrganisationUnit, Integer>( individualKeyFlagResultMap.get( keyFlagIndicator ) );                
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                Integer keyFlagValue = orgUnitResultMap.get( orgUnit );
                if( keyFlagValue != null )
                {
                    aggregationResultMap.put( orgUnit.getId()+":"+dataElement.getId(), keyFlagValue );
                }
            }
        }
		
        
        //--------------------------------------------------------------------------------
        // BY OPTION
        //--------------------------------------------------------------------------------

        conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getCaseAggregationConditionByOperator( Lookup.AGG_TYPE_BY_OPTION ) );
        
        for ( CaseAggregationCondition condition : conditions )
        {
            dataElement = condition.getAggregationDataElement();

            aggregationResultMap.putAll( aggregationManager.calculateByOptionCount( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
        }

        //--------------------------------------------------------------------------------
        // BY OPTION SCORE
        //--------------------------------------------------------------------------------

        conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getCaseAggregationConditionByOperator( Lookup.AGG_TYPE_BY_OPTION_SCORE ) );
        
        for ( CaseAggregationCondition condition : conditions )
        {
            dataElement = condition.getAggregationDataElement();

            aggregationResultMap.putAll( aggregationManager.calculateByOptionScore( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
        }

        /*
            if( condition.getOperator().equals( Lookup.AGG_TYPE_BY_OPTION ) )
            {
                aggregationResultMap.putAll( aggregationManager.calculateByOptionCount( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
            }
            else if( condition.getOperator().equals( Lookup.AGG_TYPE_BY_OPTION_SCORE ) )
            {
                aggregationResultMap.putAll( aggregationManager.calculateByOptionScore( period, dataElement, orgUnitList, condition.getAggregationExpression() ) );
            }
            else if( condition.getOperator().equals( Lookup.AGG_TYPE_BY_KEYFLAG ) )
            {
                Integer keyFlagIndicatorId = Integer.parseInt( condition.getAggregationExpression() );
                
                Indicator keyFlagIndicator = indicatorService.getIndicator( keyFlagIndicatorId );
                
                aggregationResultMap.putAll( aggregationManager.calculateKeyFlagCount( period, dataElement, orgUnitList, keyFlagIndicator ) );
            }
            */
            

        for( String key : aggregationResultMap.keySet() )
        {
            System.out.println( key + " -- " + aggregationResultMap.get(  key ) );
        }
        
        importStatus = aggregationManager.importData( aggregationResultMap, period );

        return SUCCESS;
    }

}
