package org.hisp.dhis.webapi.controller.synchmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.common.ImportOptions;
import org.hisp.dhis.dxf2.common.JacksonUtils;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLogService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( method = RequestMethod.GET )
public class MetaDataSynchLogController
{
    public static final String RESOURCE_PATH = "/MetaDataSynchLog";
    
    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    
    @Autowired
    private SynchInstanceService synchInstanceService;
    
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
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private ValidationRuleService validationRuleService;
    
    @Autowired
    private MetaDataSynchLogService metaDataSynchLogService;
    
    @Autowired
    private DependencySynchStatusService dependencySynchStatusService;    

    @RequestMapping( value = MetaDataSynchLogController.RESOURCE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importJson( ImportOptions importOptions, HttpServletResponse response, HttpServletRequest request )
    {
        try
        {
            MetaData metaData = null;
            
            metaData = JacksonUtils.fromJson( request.getInputStream(), MetaData.class );
        
            System.out.println( metaData );
            
            List<MetaDataSynchLog> metaDataSynchLogs = metaData.getMetaDataSynchLogs();
        
            for( MetaDataSynchLog metaDataSynchLog : metaDataSynchLogs )
            {
                SynchInstance instance = synchInstanceService.getInstanceByUrl( metaDataSynchLog.getUrl() );
                
                String metaDataType = metaDataSynchLog.getMetaDataType();
                String metaDataStatus = "";
                
                if( metaDataType == null ) metaDataType = "";
            
                if( instance != null )
                {
                    String summary = "";
                    for( ImportConflict ic : metaDataSynchLog.getConflicts() )
                    {
                        if( metaDataType.equals( MetaDataSynchLog.METADATA_TYPE_DATAELEMENT ) )
                        {
                            DataElement de = dataElementService.getDataElement( ic.getObject() );
                            if( de != null )
                            {
                                DataElementSynchStatus dess = dataElementSynchStatusService.getStatusByInstanceAndDataElement( instance, de );
                                
                                if( dess.getDeStatus().equalsIgnoreCase( DataElementSynchStatus.DATAELEMENT_STATUS_NEW ))
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_NEW;
                                }
                                else
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_UPDATE;
                                }
                                
                                if( ic.getValue().equals( "SUCCESS" ) )
                                {
                                    dess.setAcceptedDate( new Date() );
                                    dess.setAcceptStatus( true );
                                    dess.setStatus( DataElementSynchStatus.SYNCH_STATUS_ACCEPTED );
                                    
                                    if( dess.getDataElement().getOptionSet() != null )
                                    {
                                        DependencySynchStatus dependencySynchStatusOs = dependencySynchStatusService.getDependencySynchStatuByUID( instance, de.getUid(), de.getOptionSet().getUid() );
                                        dependencySynchStatusOs.setDependencyTypeLastupdated( de.getOptionSet().getLastUpdated() );
                                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusOs );
                                    }
                                    
                                    if( dess.getDataElement().getCategoryCombo() != null )
                                    {
                                        DependencySynchStatus dependencySynchStatusDcc = dependencySynchStatusService.getDependencySynchStatuByUID( instance, de.getUid(), de.getCategoryCombo().getUid() );
                                        dependencySynchStatusDcc.setDependencyTypeLastupdated( de.getCategoryCombo().getLastUpdated() );
                                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusDcc );
                                    }
                                    
                                    //dess.setDeccLastupdated( de.getCategoryCombo().getLastUpdated() );
                                    //dess.setOsLastupdated( de.getOptionSet().getLastUpdated() );
                                }
                                
                                dataElementSynchStatusService.updateDataElementSynchStatus( dess );
                                
                                //summary = "DataElement : ";
                                //summary += de.getName() + " - " + ic.getValue() + "; ";
                                
                                summary = de.getName() + " - " + ic.getValue();
                                
                            }
                        }
                        else if( metaDataType.equals( MetaDataSynchLog.METADATA_TYPE_INDICATOR ) )
                        {
                            Indicator ind = indicatorService.getIndicator( ic.getObject() );
                            if( ind != null )
                            {
                                IndicatorSynchStatus indss = indicatorSynchStatusService.getStatusByInstanceAndIndicator( instance, ind );
                                
                                // for dependency indicator dataElement
                                String deUIDExpressionForIndicators = "+" + indss.getIndicator().getNumerator() + "+" + indss.getIndicator().getDenominator();
                                
                                List<DataElement> indicatorDataElementList = new ArrayList<DataElement>( getDataElementsInExpression( deUIDExpressionForIndicators ) );
                                
                                if( indss.getIndicatorStatus().equalsIgnoreCase( IndicatorSynchStatus.INDICATOR_STATUS_NEW ))
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_NEW;
                                }
                                else
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_UPDATE;
                                }
                                
                                if( ic.getValue().equals( "SUCCESS" ) )
                                {
                                    indss.setAcceptedDate( new Date() );
                                    indss.setAcceptStatus( true );
                                    indss.setStatus( IndicatorSynchStatus.SYNCH_STATUS_ACCEPTED );
                                    
                                    if( indicatorDataElementList != null && indicatorDataElementList.size() > 0 )
                                    {
                                        for( DataElement indicatorDataElement : indicatorDataElementList )
                                        {
                                            if( indicatorDataElement.getOptionSet() != null )
                                            {
                                                DependencySynchStatus dependencySynchStatusIndOs = dependencySynchStatusService.getDependencySynchStatuByUID( instance, indicatorDataElement.getUid(), indicatorDataElement.getOptionSet().getUid() );
                                                dependencySynchStatusIndOs.setDependencyTypeLastupdated( indicatorDataElement.getOptionSet().getLastUpdated() );
                                                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndOs );
                                            }
                                            
                                            if( indicatorDataElement.getCategoryCombo() != null )
                                            {
                                                DependencySynchStatus dependencySynchStatusIndDcc = dependencySynchStatusService.getDependencySynchStatuByUID( instance, indicatorDataElement.getUid(), indicatorDataElement.getCategoryCombo().getUid() );
                                                dependencySynchStatusIndDcc.setDependencyTypeLastupdated( indicatorDataElement.getCategoryCombo().getLastUpdated() );
                                                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndDcc );
                                            }
                                        }
                                    
                                    } 
                                    
                                }
                                
                                indicatorSynchStatusService.updateIndicatorSynchStatus( indss );
                                
                                //summary = "Indicator : ";
                                //summary += ind.getName() + " - " + ic.getValue() + "; ";
                                
                                summary = ind.getName() + " - " + ic.getValue() ;
                                
                            }
                        
                        }
                
                        else if( metaDataType.equals( MetaDataSynchLog.METADATA_TYPE_ORGUNIT ) )
                        {
                            
                            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ic.getObject() );
                            if( orgUnit != null )
                            {
                                OrganisationUnitSynchStatus orgss = organisationUnitSynchStatusService.getStatusByInstanceAndOrganisationUnit( instance, orgUnit );
                                
                                if( orgss.getOrganisationUnitStatus().equalsIgnoreCase( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_NEW ))
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_NEW;
                                }
                                else
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_UPDATE;
                                }
                                
                                if( ic.getValue().equals( "SUCCESS" ) )
                                {
                                    orgss.setAcceptedDate( new Date() );
                                    orgss.setAcceptStatus( true );
                                    orgss.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_ACCEPTED );
                                }
                                
                                organisationUnitSynchStatusService.updateOrganisationUnitSynchStatus( orgss );
                                
                                //summary = "OrganisationUnit : ";
                                
                                //summary += orgUnit.getName() + " - " + ic.getValue() + "; ";
                                summary = orgUnit.getName() + " - " + ic.getValue() ;  
                            }
                            
                        }    
                        
                        // validation Rule related log
                        else if( metaDataType.equals( MetaDataSynchLog.METADATA_TYPE_VALIDATIONRULE ) )
                        {
                            ValidationRule validationRule = validationRuleService.getValidationRule( ic.getObject() );
                            
                            if( validationRule != null )
                            {
                                ValidationRuleSynchStatus validationRuleSynchStatus = validationRuleSynchStatusService.getStatusByInstanceAndValidationRule( instance, validationRule );
                                
                                // for dependency validation rule dataElement
                                String deUIDExpressionForValidationRules = "+" + validationRuleSynchStatus.getValidationRule().getLeftSide() + "+" + validationRuleSynchStatus.getValidationRule().getRightSide();
                                
                                List<DataElement> validationRuleDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForValidationRules ) );

                                if( validationRuleSynchStatus.getValidationRuleStatus().equalsIgnoreCase( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW ))
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_NEW;
                                }
                                else
                                {
                                    metaDataStatus = MetaDataSynchLog.METADATA_STATUS_UPDATE;
                                }
                                
                                
                                if( ic.getValue().equals( "SUCCESS" ) )
                                {
                                    validationRuleSynchStatus.setAcceptedDate( new Date() );
                                    validationRuleSynchStatus.setAcceptStatus( true );
                                    validationRuleSynchStatus.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_ACCEPTED );
                                    
                                    if( validationRuleDataElementList != null && validationRuleDataElementList.size() > 0 )
                                    {
                                        for( DataElement vrDataElement : validationRuleDataElementList )
                                        {
                                            if( vrDataElement.getOptionSet() != null )
                                            {
                                                DependencySynchStatus dependencySynchStatusVrOs = dependencySynchStatusService.getDependencySynchStatuByUID( instance, vrDataElement.getUid(), vrDataElement.getOptionSet().getUid() );
                                                dependencySynchStatusVrOs.setDependencyTypeLastupdated( vrDataElement.getOptionSet().getLastUpdated() );
                                                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusVrOs );
                                            }
                                            
                                            if( vrDataElement.getCategoryCombo() != null )
                                            {
                                                DependencySynchStatus dependencySynchStatusVrDcc = dependencySynchStatusService.getDependencySynchStatuByUID( instance, vrDataElement.getUid(), vrDataElement.getCategoryCombo().getUid() );
                                                dependencySynchStatusVrDcc.setDependencyTypeLastupdated( vrDataElement.getCategoryCombo().getLastUpdated() );
                                                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusVrDcc );
                                            }
                                        }
                                    }
                                    
                                }
                                
                                validationRuleSynchStatusService.updateValidationRuleSynchStatus( validationRuleSynchStatus );
                                
                                //summary = "OrganisationUnit : ";
                                
                                //summary += validationRule.getName() + " - " + ic.getValue() + "; ";
                                
                                summary = validationRule.getName() + " - " + ic.getValue(); 
                                
                            }
                            
                        }                    
                        
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                    
                    String datestring = dateFormat.format( new Date() ); 
                    Date logDate = dateFormat.parse( datestring );
                    
                    MetaDataSynchLog metaDataSynchLogObj = new MetaDataSynchLog();
                    metaDataSynchLogObj.setSynchDate( logDate );
                    metaDataSynchLogObj.setSynchInstance( instance );
                    metaDataSynchLogObj.setRemarks( summary );
                    
                    metaDataSynchLogObj.setMetaDataType( metaDataType );
                    metaDataSynchLogObj.setStatus( metaDataStatus );
                    
                    metaDataSynchLogService.addMetaDataSynchLog( metaDataSynchLogObj );
                    
                    }  
                }
            }
        }         
        catch( Exception e )
        {
            System.out.println( "***********************Exception in Importing: " + e.getMessage() );
            e.printStackTrace();
        }

    }

    
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