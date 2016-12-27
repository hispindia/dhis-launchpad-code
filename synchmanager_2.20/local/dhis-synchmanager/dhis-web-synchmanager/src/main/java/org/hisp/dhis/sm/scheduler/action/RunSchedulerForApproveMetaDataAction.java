package org.hisp.dhis.sm.scheduler.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RunSchedulerForApproveMetaDataAction implements Action
{
    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private DataElementSynchStatusService dataElementSynchStatusService;
    
    @Autowired
    private IndicatorSynchStatusService indicatorSynchStatusService;
    
    @Autowired
    private OrganisationUnitSynchStatusService organisationUnitSynchStatusService;
    
    @Autowired
    private ValidationRuleSynchStatusService validationRuleSynchStatusService;
    
    @Autowired
    private DataElementService dataElementService;
    
    private DependencySynchStatusService dependencySynchStatusService; 
    
    public void setDependencySynchStatusService( DependencySynchStatusService dependencySynchStatusService )
    {
        this.dependencySynchStatusService = dependencySynchStatusService;
    }

    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    //final String abc = "*/7 * * * * *";
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    //@Scheduled(cron = "*/7 * * * * *")
    //@Scheduled(cron = abc )
    
    //@Scheduled(cron = "${myclass.cron.execute.sth}")
    
    
    
    //@Scheduled(cron = "${myclass.cron.execute.sth}")
    public String execute() throws Exception
    {
        
        
        System.out.println(" MetaData Synch Job Scheduler Started at : " + new Date() );
        
        //scheduledReport();
        
        System.out.println(" MetaData Synch Scheduler Ended at : " + new Date() );
        
        Date lastUpdated = new Date();
        
        //List<DataElement> dataElementList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        // for dataElement
        
        List<DataElementSynchStatus> updateDataElementSynchStatus = new ArrayList<DataElementSynchStatus>( dataElementSynchStatusService.getUpdatedDataElementSyncStatus() );
        
        for ( DataElementSynchStatus deSynchStatus : updateDataElementSynchStatus )
        {
            if ( deSynchStatus != null )
            {
                
                Date mainDELastUpdated = deSynchStatus.getDataElement().getLastUpdated();
                
                Date deSynchStatusLastUpdated = deSynchStatus.getLastUpdated();
                
                Date mainOSLastUpdated = deSynchStatus.getDataElement().getOptionSet().getLastUpdated();
                
                Date mainDCCLastUpdated = deSynchStatus.getDataElement().getCategoryCombo().getLastUpdated();
                
                
                if( !mainDELastUpdated.equals( deSynchStatusLastUpdated ) )
                {
                    if( deSynchStatus.getRememberApproveStatus() != null && !deSynchStatus.getRememberApproveStatus() )
                    {
                        if( deSynchStatus.getAcceptStatus() != null && deSynchStatus.getAcceptStatus() )
                        {
                            deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
                        }
                        else
                        {
                            deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
                        }
                        
                        deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
                        
                    }
                    else
                    {
                        deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_SUBMITTED );
                    }
                }
                
                // related to update dependencySynchStatus
                
                if( deSynchStatus.getDataElement().getOptionSet() != null  )
                {
                    DependencySynchStatus dependencySynchStatusOS = dependencySynchStatusService.getDependencySynchStatuByUID( deSynchStatus.getInstance(), deSynchStatus.getUid(), deSynchStatus.getDataElement().getOptionSet().getUid() );
                    
                    if( dependencySynchStatusOS != null )
                    {
                        Date dependencyOSLastUpdated = dependencySynchStatusOS.getDependencyTypeLastupdated();
                        
                        if( !mainOSLastUpdated.equals( dependencyOSLastUpdated ) )
                        {
                            dependencySynchStatusOS.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getOptionSet().getLastUpdated() );
                            dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusOS );
                            
                            if( deSynchStatus.getRememberApproveStatus() != null && !deSynchStatus.getRememberApproveStatus() )
                            {
                                if( deSynchStatus.getAcceptStatus() != null && deSynchStatus.getAcceptStatus() )
                                {
                                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
                                }
                                else
                                {
                                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
                                }
                                
                                deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
                                
                            }
                            else
                            {
                                deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_SUBMITTED );
                            }
                            
                        }
                    }
                                    
                }
                
                if( deSynchStatus.getDataElement().getCategoryCombo() != null  )
                {
                    DependencySynchStatus dependencySynchStatusDCC = dependencySynchStatusService.getDependencySynchStatuByUID( deSynchStatus.getInstance(), deSynchStatus.getUid(), deSynchStatus.getDataElement().getCategoryCombo().getUid() );
                   
                    if( dependencySynchStatusDCC != null )
                    {
                        Date dependencyDCCLastUpdated = dependencySynchStatusDCC.getDependencyTypeLastupdated();
                        
                        if( !mainDCCLastUpdated.equals( dependencyDCCLastUpdated ) )
                        {
                            dependencySynchStatusDCC.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getCategoryCombo().getLastUpdated() );
                            dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusDCC );
                            
                            if( deSynchStatus.getRememberApproveStatus() != null && !deSynchStatus.getRememberApproveStatus() )
                            {
                                if( deSynchStatus.getAcceptStatus() != null && deSynchStatus.getAcceptStatus() )
                                {
                                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
                                }
                                else
                                {
                                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
                                }
                                
                                deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
                                
                            }
                            else
                            {
                                deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_SUBMITTED );
                            }
                        }
                    }
                }
                
                
                deSynchStatus.setApproveStatus( true );
                deSynchStatus.setApprovedDate( lastUpdated );
                deSynchStatus.setLastUpdated( lastUpdated );
                
                //deSynchStatus.getInstance();
                dataElementSynchStatusService.updateDataElementSynchStatus( deSynchStatus );
                
                
                
                
                /*
                
                if( dependencySynchStatusOS != null && dependencySynchStatusDCC != null )
                {
                    if( !mainOSLastUpdated.equals( dependencyOSLastUpdated ) || !mainDCCLastUpdated.equals( dependencyDCCLastUpdated ) )
                    {
                        dependencySynchStatusOS.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getOptionSet().getLastUpdated() );
                        dependencySynchStatusDCC.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getCategoryCombo().getLastUpdated() );
                        
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusOS );
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusDCC );
                    }
                    
                }
                
                */
                
                /*
                else
                {
                    dependencySynchStatusOS.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                    dependencySynchStatusOS.setMetaDataTypeUID( deSynchStatus.getDataElement().getUid() );
                    dependencySynchStatusOS.setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_OPTION_SET );
                    dependencySynchStatusOS.setDependencyTypeUID( deSynchStatus.getDataElement().getOptionSet().getUid() );
                    dependencySynchStatusOS.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getOptionSet().getLastUpdated() );
                    
                    dependencySynchStatusDCC.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                    dependencySynchStatusDCC.setMetaDataTypeUID( deSynchStatus.getDataElement().getUid() );
                    dependencySynchStatusDCC.setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_DATAELEMENT_CATEGORY_COMBO );
                    dependencySynchStatusDCC.setDependencyTypeUID( deSynchStatus.getDataElement().getCategoryCombo().getUid() );
                    dependencySynchStatusDCC.setDependencyTypeLastupdated( deSynchStatus.getDataElement().getCategoryCombo().getLastUpdated() );
                    
                    dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusOS );
                    dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusDCC );
                    
                }
                */
                
                /*
                if( deSynchStatus.getRememberApproveStatus() != null && !deSynchStatus.getRememberApproveStatus() )
                {
                    if( deSynchStatus.getAcceptStatus() != null && deSynchStatus.getAcceptStatus() )
                    {
                        deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
                    }
                    else
                    {
                        deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
                    }
                    
                    deSynchStatus.setApproveStatus( true );
                    deSynchStatus.setApprovedDate( lastUpdated );
                    deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
                    
                    deSynchStatus.setLastUpdated( lastUpdated );
                    
                    dataElementSynchStatusService.updateDataElementSynchStatus( deSynchStatus );
                }
                */
            }
        }
        
        // for indicator
        List<IndicatorSynchStatus> updateIndicatorSynchStatus = new ArrayList<IndicatorSynchStatus>( indicatorSynchStatusService.getUpdatedIndicatorSyncStatus() );
        
        for ( IndicatorSynchStatus indicatorSynchStatus : updateIndicatorSynchStatus )
        {
            if ( indicatorSynchStatus != null )
            {
                Date mainIndLastUpdated = indicatorSynchStatus.getIndicator().getLastUpdated();
                
                Date indSynchStatusLastUpdated = indicatorSynchStatus.getLastUpdated();
                
                if( !mainIndLastUpdated.equals( indSynchStatusLastUpdated ) )
                {
                    if( indicatorSynchStatus.getRememberApproveStatus() != null && !indicatorSynchStatus.getRememberApproveStatus() )
                    {
                        if( indicatorSynchStatus.getAcceptStatus() != null && indicatorSynchStatus.getAcceptStatus() )
                        {
                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
                        }
                        else
                        {
                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
                        }
                        
                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
                        
                    }
                    else
                    {
                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
                    }
                    
                }
                
                // for dependency indicator dataElement
                String deUIDExpressionForIndicators = "+" + indicatorSynchStatus.getIndicator().getNumerator() + "+" + indicatorSynchStatus.getIndicator().getDenominator();
                
                List<DataElement> indicatorDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForIndicators ) );
                
                if( indicatorDataElementList != null && indicatorDataElementList.size() > 0 )
                {
                    for( DataElement indicatorDataElement : indicatorDataElementList )
                    {
                        Date mainIndOSLastUpdated = indicatorDataElement.getOptionSet().getLastUpdated();
                        
                        Date mainIndDCCLastUpdated = indicatorDataElement.getCategoryCombo().getLastUpdated();
                        
                        if( indicatorDataElement.getOptionSet() != null  )
                        {
                            DependencySynchStatus dependencySynchStatusIndOS = dependencySynchStatusService.getDependencySynchStatuByUID( indicatorSynchStatus.getInstance(), indicatorDataElement.getUid(), indicatorDataElement.getOptionSet().getUid() );
                            
                            if( dependencySynchStatusIndOS != null )
                            {
                                Date dependencyIndOSLastUpdated = dependencySynchStatusIndOS.getDependencyTypeLastupdated();
                                
                                if( !mainIndOSLastUpdated.equals( dependencyIndOSLastUpdated ) )
                                {
                                    dependencySynchStatusIndOS.setDependencyTypeLastupdated( indicatorDataElement.getOptionSet().getLastUpdated() );
                                    dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndOS );
                                    
                                    if( indicatorSynchStatus.getRememberApproveStatus() != null && !indicatorSynchStatus.getRememberApproveStatus() )
                                    {
                                        if( indicatorSynchStatus.getAcceptStatus() != null && indicatorSynchStatus.getAcceptStatus() )
                                        {
                                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
                                        }
                                        else
                                        {
                                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
                                        }
                                        
                                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
                                        
                                    }
                                    else
                                    {
                                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
                                    }
                                }
                            }
                                            
                        }
                        // dataElement categoryCombo
                        if( indicatorDataElement.getCategoryCombo() != null  )
                        {
                            DependencySynchStatus dependencySynchStatusIndDCC = dependencySynchStatusService.getDependencySynchStatuByUID( indicatorSynchStatus.getInstance(), indicatorDataElement.getUid(), indicatorDataElement.getCategoryCombo().getUid() );
                           
                            if( dependencySynchStatusIndDCC != null )
                            {
                                Date dependencyIndDCCLastUpdated = dependencySynchStatusIndDCC.getDependencyTypeLastupdated();
                                
                                if( !mainIndDCCLastUpdated.equals( dependencyIndDCCLastUpdated ) )
                                {
                                    dependencySynchStatusIndDCC.setDependencyTypeLastupdated( indicatorDataElement.getCategoryCombo().getLastUpdated() );
                                    dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndDCC );
                                    
                                    if( indicatorSynchStatus.getRememberApproveStatus() != null && !indicatorSynchStatus.getRememberApproveStatus() )
                                    {
                                        if( indicatorSynchStatus.getAcceptStatus() != null && indicatorSynchStatus.getAcceptStatus() )
                                        {
                                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
                                        }
                                        else
                                        {
                                            indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
                                        }
                                        
                                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
                                        
                                    }
                                    else
                                    {
                                        indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
                                    }
                                }
                            }
                        }

                    }
                }
                
                
                /*
                if( indicatorSynchStatus.getRememberApproveStatus() != null && !indicatorSynchStatus.getRememberApproveStatus() )
                {
                    if( indicatorSynchStatus.getAcceptStatus() != null && indicatorSynchStatus.getAcceptStatus() )
                    {
                        indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
                    }
                    else
                    {
                        indicatorSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
                    }
                    
                    indicatorSynchStatus.setApproveStatus( true );
                    indicatorSynchStatus.setApprovedDate( lastUpdated );
                    indicatorSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
                    
                    indicatorSynchStatus.setLastUpdated( lastUpdated );
                    
                    indicatorSynchStatusService.updateIndicatorSynchStatus( indicatorSynchStatus );
                }
                */
                
                indicatorSynchStatus.setApproveStatus( true );
                indicatorSynchStatus.setApprovedDate( lastUpdated );
                
                indicatorSynchStatus.setLastUpdated( lastUpdated );
                
                indicatorSynchStatusService.updateIndicatorSynchStatus( indicatorSynchStatus );
                
            }
        }        
        

        // for Validation Rule
        List<ValidationRuleSynchStatus> updateValidationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>( validationRuleSynchStatusService.getUpdatedValidationRuleSyncStatus()  );
        
        for ( ValidationRuleSynchStatus validationRuleSynchStatus : updateValidationRuleSynchStatus )
        {
            if ( validationRuleSynchStatus != null )
            {   
                
                Date mainVRLastUpdated = validationRuleSynchStatus.getValidationRule().getLastUpdated();
                
                Date vrStatusLastUpdated = validationRuleSynchStatus.getLastUpdated();
                
                if( !mainVRLastUpdated.equals( vrStatusLastUpdated ) )
                {
                    if( validationRuleSynchStatus.getRememberApproveStatus() != null && !validationRuleSynchStatus.getRememberApproveStatus() )
                    {
                        if( validationRuleSynchStatus.getAcceptStatus() != null && validationRuleSynchStatus.getAcceptStatus() )
                        {
                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
                        }
                        else
                        {
                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
                        }
                        
                    }
                    else
                    {
                        validationRuleSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_SUBMITTED );
                    }
                    
                }
                
                
                
                // for dependency validation rule dataElement
                String deUIDExpressionForValidationRules = "+" + validationRuleSynchStatus.getValidationRule().getLeftSide() + "+" + validationRuleSynchStatus.getValidationRule().getRightSide();
                
                List<DataElement> validationRuleDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForValidationRules ) );

                if( validationRuleDataElementList != null && validationRuleDataElementList.size() > 0 )
                {
                    for( DataElement vrDataElement : validationRuleDataElementList )
                    {
                        Date mainVrOSLastUpdated = vrDataElement.getOptionSet().getLastUpdated();
                        
                        Date mainIndDCCLastUpdated = vrDataElement.getCategoryCombo().getLastUpdated();
                        
                        if( vrDataElement.getOptionSet() != null  )
                        {
                            DependencySynchStatus dependencySynchStatusVrOS = dependencySynchStatusService.getDependencySynchStatuByUID( validationRuleSynchStatus.getInstance(), vrDataElement.getUid(), vrDataElement.getOptionSet().getUid() );
                            
                            if( dependencySynchStatusVrOS != null )
                            {
                                Date dependencyVrOSLastUpdated = dependencySynchStatusVrOS.getDependencyTypeLastupdated();
                                
                                if( !mainVrOSLastUpdated.equals( dependencyVrOSLastUpdated ) )
                                {
                                    dependencySynchStatusVrOS.setDependencyTypeLastupdated( vrDataElement.getOptionSet().getLastUpdated() );
                                    dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusVrOS );
                                    
                                    if( validationRuleSynchStatus.getRememberApproveStatus() != null && !validationRuleSynchStatus.getRememberApproveStatus() )
                                    {
                                        if( validationRuleSynchStatus.getAcceptStatus() != null && validationRuleSynchStatus.getAcceptStatus() )
                                        {
                                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
                                        }
                                        else
                                        {
                                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
                                        }
                                        
                                    }
                                    else
                                    {
                                        validationRuleSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_SUBMITTED );
                                    }
                                }
                            }
                                            
                        }
                        // dataElement categoryCombo
                        if( vrDataElement.getCategoryCombo() != null  )
                        {
                            DependencySynchStatus dependencySynchStatusVrDCC = dependencySynchStatusService.getDependencySynchStatuByUID( validationRuleSynchStatus.getInstance(), vrDataElement.getUid(), vrDataElement.getCategoryCombo().getUid() );
                           
                            if( dependencySynchStatusVrDCC != null )
                            {
                                Date dependencyVrDCCLastUpdated = dependencySynchStatusVrDCC.getDependencyTypeLastupdated();
                                
                                if( !mainIndDCCLastUpdated.equals( dependencyVrDCCLastUpdated ) )
                                {
                                    dependencySynchStatusVrDCC.setDependencyTypeLastupdated( vrDataElement.getCategoryCombo().getLastUpdated() );
                                    dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusVrDCC );
                                    
                                    if( validationRuleSynchStatus.getRememberApproveStatus() != null && !validationRuleSynchStatus.getRememberApproveStatus() )
                                    {
                                        if( validationRuleSynchStatus.getAcceptStatus() != null && validationRuleSynchStatus.getAcceptStatus() )
                                        {
                                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
                                        }
                                        else
                                        {
                                            validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
                                        }
                                        
                                    }
                                    else
                                    {
                                        validationRuleSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_SUBMITTED );
                                    }
                                }
                            }
                        }

                    }
                }
                
                /*
                if( validationRuleSynchStatus.getRememberApproveStatus() != null && !validationRuleSynchStatus.getRememberApproveStatus() )
                {
                    if( validationRuleSynchStatus.getAcceptStatus() != null && validationRuleSynchStatus.getAcceptStatus() )
                    {
                        validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
                    }
                    else
                    {
                        validationRuleSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
                    }
                    
                    validationRuleSynchStatus.setApproveStatus( true );
                    validationRuleSynchStatus.setApprovedDate( lastUpdated );
                    validationRuleSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_APPROVED );
                    
                    validationRuleSynchStatus.setLastUpdated( lastUpdated );
                    
                    validationRuleSynchStatusService.updateValidationRuleSynchStatus( validationRuleSynchStatus );
                }
                */
                
                validationRuleSynchStatus.setApproveStatus( true );
                validationRuleSynchStatus.setApprovedDate( lastUpdated );
                
                validationRuleSynchStatus.setLastUpdated( lastUpdated );
                
                validationRuleSynchStatusService.updateValidationRuleSynchStatus( validationRuleSynchStatus );
                
            }
        }        
        
        
        // for Organization Unit
        List<OrganisationUnitSynchStatus> updateOrganisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>( organisationUnitSynchStatusService.getUpdatedOrganisationUnitSyncStatus()  );
        
        for ( OrganisationUnitSynchStatus organisationUnitSynchStatus : updateOrganisationUnitSynchStatus )
        {
            if ( organisationUnitSynchStatus != null )
            {
                if( organisationUnitSynchStatus.getRememberApproveStatus() != null && !organisationUnitSynchStatus.getRememberApproveStatus() )
                {
                    if( organisationUnitSynchStatus.getAcceptStatus() != null && organisationUnitSynchStatus.getAcceptStatus() )
                    {
                        organisationUnitSynchStatus.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_UPDATE );
                    }
                    else
                    {
                        organisationUnitSynchStatus.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_NEW);
                    }
                    
                    organisationUnitSynchStatus.setApproveStatus( true );
                    organisationUnitSynchStatus.setApprovedDate( lastUpdated );
                    organisationUnitSynchStatus.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_APPROVED );
                    
                    organisationUnitSynchStatus.setLastUpdated( lastUpdated );
                    
                    organisationUnitSynchStatusService.updateOrganisationUnitSynchStatus( organisationUnitSynchStatus );
                }
            }
        }        
        

        System.out.println(" MetaData Synch Scheduler Ended at : " + new Date() );
        
        return SUCCESS;
    }
    
    /*
    @Scheduled(cron = abc)
    public void scheduledReport()
    {
        System.out.println(" MetaData Synch Scheduler Inside void : " + new Date() );
    }
    */
    
    // find dataElement from Expression   
    public Set<DataElement> getDataElementsInExpression( String expression )
    {
        Set<DataElement> dataElementsInExpression = null;

        if ( expression != null )
        {
            dataElementsInExpression = new HashSet<>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                String deUID =  matcher.group( 1 );
                
                DataElement dataElement = dataElementService.getDataElement( deUID );
                
                dataElementsInExpression.add( dataElement );
                
            }
        }
        return dataElementsInExpression;
    }
 
}

