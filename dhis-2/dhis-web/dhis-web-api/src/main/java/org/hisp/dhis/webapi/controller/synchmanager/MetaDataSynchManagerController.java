package org.hisp.dhis.webapi.controller.synchmanager;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping( method = RequestMethod.GET )
public class MetaDataSynchManagerController
{
    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    
    public static final String RESOURCE_PATH = "/MetaDataSynchManager";
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private ValidationRuleService validationRuleService;
    
    @Autowired
    private AttributeService  attributeService;    
    
    @Autowired
    private OrganisationUnitService organisationUnitService; 
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService; 
    
    
    /*
    @Autowired
    private DataElementCategoryService dataElementCategoryService ; 
    */
    
    @Autowired
    private ContextUtils contextUtils;
    
    @RequestMapping( value = MetaDataSynchManagerController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();
        
        Set<DataElementCategory> categories = new HashSet<DataElementCategory>();
        Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();
        Set<DataElementCategoryCombo> categoryCombos = new HashSet<DataElementCategoryCombo>();
        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        Set<OptionSet> optionSets = new HashSet<OptionSet>();
        
        Set<DataElementGroup> synchDataElementGroups = new HashSet<DataElementGroup>();
        Set<DataElementGroupSet> synchDataElementGroupSets = new HashSet<DataElementGroupSet>();
        /*
        Set<DataElementGroup> dataElementGroups = new HashSet<DataElementGroup>();
        
        
        Set<DataElementGroupSet> dataElementGroupSets = new HashSet<DataElementGroupSet>();
        
        
        dataElementGroups =new HashSet<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        dataElementGroupSets = new HashSet<DataElementGroupSet>( dataElementService.getAllDataElementGroupSets() );
        */
        
        //dataElementCategoryService.getAllDataElementCategories();
        //dataElementCategoryService.getAllDataElementCategoryOptionCombos();
        //dataElementCategoryService.getAllDataElementCategoryOptions();
        
        //List<Attribute> attributes = new ArrayList<Attribute>();
        //attributes = new ArrayList<Attribute>( attributeService.getAllAttributes() );
        //List<Attribute> synchDataElementAttribute = new ArrayList<Attribute>();
        
        
        Set<Indicator> indicators  = new HashSet<Indicator>();
        
        Set<IndicatorType> indicatorTypes = new HashSet<IndicatorType>();
        Set<IndicatorType> synchIndicatorTypes = new HashSet<IndicatorType>();
        indicatorTypes = new HashSet<IndicatorType>( indicatorService.getAllIndicatorTypes() );
        
        Set<IndicatorGroup> indicatorGroups = new HashSet<IndicatorGroup>();
        Set<IndicatorGroup> synchIndicatorGroups = new HashSet<IndicatorGroup>();
        indicatorGroups = new HashSet<IndicatorGroup>( indicatorService.getAllIndicatorGroups() );
        
        Set<IndicatorGroupSet> indicatorGroupSets = new HashSet<IndicatorGroupSet>();
        Set<IndicatorGroupSet> synchIndicatorGroupSets = new HashSet<IndicatorGroupSet>();
        indicatorGroupSets = new HashSet<IndicatorGroupSet>( indicatorService.getAllIndicatorGroupSets() );
        
        
        Set<ValidationRule> validationRules  = new HashSet<ValidationRule>();
        Set<ValidationRuleGroup> validationRuleGroups = new HashSet<ValidationRuleGroup>();
        Set<ValidationRuleGroup> synchValidationRuleGroups = new HashSet<ValidationRuleGroup>();
        validationRuleGroups = new HashSet<ValidationRuleGroup>( validationRuleService.getAllValidationRuleGroups() );
        
       
        Set<Attribute> synchAttributes = new HashSet<Attribute>();
        
        /*
        Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
        attributeValues = new HashSet<AttributeValue>( attributeService.getAllAttributeValues() );
        */
        
        
        //attributeService.getAllAttributes().iterator().next().isDataSetAttribute();
        
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        
        Set<OrganisationUnitGroup> organisationUnitGroups  =  new HashSet<OrganisationUnitGroup>();
        organisationUnitGroups  =  new HashSet<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );
        Set<OrganisationUnitGroup> synchOrganisationUnitGroups = new HashSet<OrganisationUnitGroup>();
        
        Set<OrganisationUnitGroupSet> organisationUnitGroupSets  =  new HashSet<OrganisationUnitGroupSet>();
        organisationUnitGroupSets  =  new HashSet<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() );
        Set<OrganisationUnitGroupSet> synchOrganisationUnitGroupSets = new HashSet<OrganisationUnitGroupSet>();
        
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
               
        String dataElementsUIDs = null;
        
        String indicatorUIDs = null;
        
        String orgUnitUIDs = null;
        
        String validationRuleUIDs = null;
        
        response.setContentType( CONTENT_TYPE_XML );
        
        if ( options.getOptions().containsKey( "DE" ) && options.getOptions().containsKey( "IND" ) && options.getOptions().containsKey( "ORGUNIT" ) && options.getOptions().containsKey( "VR" ) )
        {
            dataElementsUIDs = options.getOptions().get("DE");
            indicatorUIDs = options.getOptions().get("IND");
            validationRuleUIDs = options.getOptions().get("VR");
            orgUnitUIDs = options.getOptions().get("ORGUNIT");
            
            System.out.println("Inside MetaDataSynchController 1 " + dataElementsUIDs + "--" + indicatorUIDs + "--" + validationRuleUIDs + "--" + orgUnitUIDs );
            
            if ( dataElementsUIDs != null && dataElementsUIDs.length() > 0 && !dataElementsUIDs.equalsIgnoreCase( "NA" ) )
            {
                System.out.println("Inside MetaDataSynchController 2 " + dataElementsUIDs.length() + "--" + dataElementsUIDs );
                
                List<String> deUIDList = new ArrayList<String>();
                
                String[] dataElementUIDList = dataElementsUIDs.split( "," );

                for ( String dataElementUID : dataElementUIDList )
                {
                    deUIDList.add( dataElementUID );
                }
                
                MetaData resultDataElementMetaData  = getMetaDataDataElement( deUIDList );
                
                if( resultDataElementMetaData != null )
                {
                    if ( resultDataElementMetaData.getDataElements() != null && resultDataElementMetaData.getDataElements().size() > 0 )
                    {
                        dataElements.addAll( resultDataElementMetaData.getDataElements() );
                    }
                    
                    if ( resultDataElementMetaData.getOptionSets() != null && resultDataElementMetaData.getOptionSets().size() > 0 )
                    {
                        optionSets.addAll( resultDataElementMetaData.getOptionSets() );
                    }
                    
                    if ( resultDataElementMetaData.getCategories() != null && resultDataElementMetaData.getCategories().size() > 0 )
                    {
                        categories.addAll( resultDataElementMetaData.getCategories() );
                    }
                    
                    if ( resultDataElementMetaData.getCategoryCombos() != null && resultDataElementMetaData.getCategoryCombos().size() > 0 )
                    {
                        categoryCombos.addAll( resultDataElementMetaData.getCategoryCombos() );
                    }
                    
                    if ( resultDataElementMetaData.getCategoryOptions() != null && resultDataElementMetaData.getCategoryOptions().size() > 0 )
                    {
                        categoryOptions.addAll( resultDataElementMetaData.getCategoryOptions() );
                    }
                    
                    if ( resultDataElementMetaData.getCategoryOptionCombos() != null && resultDataElementMetaData.getCategoryOptionCombos().size() > 0 )
                    {
                        categoryOptionCombos.addAll( resultDataElementMetaData.getCategoryOptionCombos() );
                    }
                    
                    if ( resultDataElementMetaData.getDataElementGroups() != null && resultDataElementMetaData.getDataElementGroups().size() > 0 )
                    {
                        synchDataElementGroups.addAll( resultDataElementMetaData.getDataElementGroups() );
                    }
                    
                    if ( resultDataElementMetaData.getDataElementGroupSets() != null && resultDataElementMetaData.getDataElementGroupSets().size() > 0 )
                    {
                        synchDataElementGroupSets.addAll( resultDataElementMetaData.getDataElementGroupSets() );
                    }
                    
                }

                /*
                if( dataElements != null && dataElements.size() > 0 )
                {
                    //categories = new ArrayList<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() );
                    //categoryOptions = new ArrayList<DataElementCategoryOption>( dataElementCategoryService.getAllDataElementCategoryOptions() );
                    //categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryService.getAllDataElementCategoryOptionCombos() );

                    for ( DataElement dataElement : dataElements )
                    {
                        if( dataElement.getOptionSet() != null  )
                        {
                            optionSets.add( dataElement.getOptionSet() );
                            
                        }
                        
                        if( dataElement.getCategoryCombo() != null  )
                        {
                            categoryCombos.add( dataElement.getCategoryCombo() );
                            
                            if( dataElement.getCategoryCombo().getCategories() != null && dataElement.getCategoryCombo().getCategories().size() > 0  )
                            {
                                categories.addAll( dataElement.getCategoryCombo().getCategories() );
                            }
                            
                            if( dataElement.getCategoryCombo().getCategoryOptions() != null && dataElement.getCategoryCombo().getCategoryOptions().size() > 0 )
                            {
                                categoryOptions.addAll( dataElement.getCategoryCombo().getCategoryOptions() );
                            }
                            
                            if( dataElement.getCategoryCombo().getOptionCombos() != null && dataElement.getCategoryCombo().getOptionCombos().size() > 0 )
                            {
                                categoryOptionCombos.addAll( dataElement.getCategoryCombo().getOptionCombos() );
                            }
                            
                        }
                        
                        if( dataElementGroups != null && dataElementGroups.size() > 0 )
                        {
                            for( DataElementGroup deGroup : dataElementGroups )
                            {
                                if( deGroup.getMembers().contains( dataElement ) )
                                {
                                    synchDataElementGroups.add( deGroup );
                                }
                            }
                        }
                        
                        if( dataElementGroupSets != null && dataElementGroupSets.size() > 0 )
                        {
                            for ( DataElementGroupSet deGroupSet : dataElementGroupSets )
                            {
                                for( DataElementGroup synchDEGroup : synchDataElementGroups )
                                {
                                    if( deGroupSet.getMembers().contains( synchDEGroup ) )
                                    {
                                        synchDataElementGroupSets.add( deGroupSet );
                                    }
                                }
                                
                            }
                        }
                        
                        if( attributeValues != null && attributeValues.size() > 0 )
                        {
                            for ( AttributeValue attributeValue : attributeValues )
                            {
                                if( dataElement.getAttributeValues().contains( attributeValue ) )
                                {
                                    synchAttributes.add( attributeValue.getAttribute() );
                                }
                            }
                        }
                        
                        
                        
                    }
                }
               */ 
            }
            
            if ( indicatorUIDs != null && indicatorUIDs.length() > 0 && !indicatorUIDs.equalsIgnoreCase( "NA" ) )
            {
                String[] indicatorUIDList = indicatorUIDs.split( "," );
                String indicatorDEUIDExpression = "";
                
                System.out.println("Inside MetaDataSynchController 3 " + indicatorUIDs.length() + "--" + indicatorUIDs );
                
                for ( String indicatorUID : indicatorUIDList )
                {
                    Indicator indicator = indicatorService.getIndicator( indicatorUID );
                    indicatorDEUIDExpression += "+" + indicator.getNumerator() + "+" + indicator.getDenominator();
                    indicators.add( indicator );
                }
                
                List<String> indicatorDeUIDList = new ArrayList<>( getDataElementsInExpression( indicatorDEUIDExpression ) );
                MetaData indicatorDeMetaData = getMetaDataDataElement( indicatorDeUIDList );
                
                if( indicatorDeMetaData != null )
                {
                    if ( indicatorDeMetaData.getDataElements() != null && indicatorDeMetaData.getDataElements().size() > 0 )
                    {
                        dataElements.addAll( indicatorDeMetaData.getDataElements() );
                    }
                    
                    if ( indicatorDeMetaData.getOptionSets() != null && indicatorDeMetaData.getOptionSets().size() > 0 )
                    {
                        optionSets.addAll( indicatorDeMetaData.getOptionSets() );
                    }
                    
                    if ( indicatorDeMetaData.getCategories() != null && indicatorDeMetaData.getCategories().size() > 0 )
                    {
                        categories.addAll( indicatorDeMetaData.getCategories() );
                    }
                    
                    if ( indicatorDeMetaData.getCategoryCombos() != null && indicatorDeMetaData.getCategoryCombos().size() > 0 )
                    {
                        categoryCombos.addAll( indicatorDeMetaData.getCategoryCombos() );
                    }
                    
                    if ( indicatorDeMetaData.getCategoryOptions() != null && indicatorDeMetaData.getCategoryOptions().size() > 0 )
                    {
                        categoryOptions.addAll( indicatorDeMetaData.getCategoryOptions() );
                    }
                    
                    if ( indicatorDeMetaData.getCategoryOptionCombos() != null && indicatorDeMetaData.getCategoryOptionCombos().size() > 0 )
                    {
                        categoryOptionCombos.addAll( indicatorDeMetaData.getCategoryOptionCombos() );
                    }
                    
                    if ( indicatorDeMetaData.getDataElementGroups() != null && indicatorDeMetaData.getDataElementGroups().size() > 0 )
                    {
                        synchDataElementGroups.addAll( indicatorDeMetaData.getDataElementGroups() );
                    }
                    
                    if ( indicatorDeMetaData.getDataElementGroupSets() != null && indicatorDeMetaData.getDataElementGroupSets().size() > 0 )
                    {
                        synchDataElementGroupSets.addAll( indicatorDeMetaData.getDataElementGroupSets() );
                    }
                    
                }

                if( indicators != null && indicators.size() > 0 )
                {
                    for ( Indicator indicator : indicators )
                    {
                        for( IndicatorType indicatorType  : indicatorTypes ) 
                        {
                            if( indicatorType.getUid().equals( indicator.getIndicatorType().getUid() ) )
                            {
                                synchIndicatorTypes.add( indicatorType );                            
                                break;
                            }
                        }
                        
                        for( IndicatorGroup indicatorGroup : indicatorGroups )
                        {
                            if( indicatorGroup.getMembers().contains( indicator )  )
                            {
                                synchIndicatorGroups.add( indicatorGroup );
                            }
                            
                        }
                        
                        for ( IndicatorGroupSet indicatorGroupSet : indicatorGroupSets )
                        {
                            for( IndicatorGroup synchIndicatorGroup : synchIndicatorGroups )
                            {
                                if( indicatorGroupSet.getMembers().contains( synchIndicatorGroup ) )
                                {
                                    synchIndicatorGroupSets.add( indicatorGroupSet );
                                }
                            }
                            
                        }
                    }
                    
                }
            }
            
            
            if ( validationRuleUIDs != null && validationRuleUIDs.length() > 0 && !validationRuleUIDs.equalsIgnoreCase( "NA" ) )
            {
                System.out.println("Inside MetaDataSynchController 4 " + validationRuleUIDs.length() + "--" + validationRuleUIDs );
                
                String vrDEUIDExpression = "";
                
                String[] validationRuleUIDList = validationRuleUIDs.split( "," );

                for ( String validationRuleUID : validationRuleUIDList )
                {
                    ValidationRule validationRule = validationRuleService.getValidationRule( validationRuleUID );
                    
                    vrDEUIDExpression += "+" + validationRule.getLeftSide() + "+" + validationRule.getRightSide();
                    
                    validationRules.add( validationRule );
                }
                
                List<String> vrDeUIDList = new ArrayList<>( getDataElementsInExpression( vrDEUIDExpression ) );
                MetaData vrDataElementMetaData = getMetaDataDataElement( vrDeUIDList );
                
                if( vrDataElementMetaData != null )
                {
                    if ( vrDataElementMetaData.getDataElements() != null && vrDataElementMetaData.getDataElements().size() > 0 )
                    {
                        dataElements.addAll( vrDataElementMetaData.getDataElements() );
                    }
                    
                    if ( vrDataElementMetaData.getOptionSets() != null && vrDataElementMetaData.getOptionSets().size() > 0 )
                    {
                        optionSets.addAll( vrDataElementMetaData.getOptionSets() );
                    }
                    
                    if ( vrDataElementMetaData.getCategories() != null && vrDataElementMetaData.getCategories().size() > 0 )
                    {
                        categories.addAll( vrDataElementMetaData.getCategories() );
                    }
                    
                    if ( vrDataElementMetaData.getCategoryCombos() != null && vrDataElementMetaData.getCategoryCombos().size() > 0 )
                    {
                        categoryCombos.addAll( vrDataElementMetaData.getCategoryCombos() );
                    }
                    
                    if ( vrDataElementMetaData.getCategoryOptions() != null && vrDataElementMetaData.getCategoryOptions().size() > 0 )
                    {
                        categoryOptions.addAll( vrDataElementMetaData.getCategoryOptions() );
                    }
                    
                    if ( vrDataElementMetaData.getCategoryOptionCombos() != null && vrDataElementMetaData.getCategoryOptionCombos().size() > 0 )
                    {
                        categoryOptionCombos.addAll( vrDataElementMetaData.getCategoryOptionCombos() );
                    }
                    
                    if ( vrDataElementMetaData.getDataElementGroups() != null && vrDataElementMetaData.getDataElementGroups().size() > 0 )
                    {
                        synchDataElementGroups.addAll( vrDataElementMetaData.getDataElementGroups() );
                    }
                    
                    if ( vrDataElementMetaData.getDataElementGroupSets() != null && vrDataElementMetaData.getDataElementGroupSets().size() > 0 )
                    {
                        synchDataElementGroupSets.addAll( vrDataElementMetaData.getDataElementGroupSets() );
                    }
                    
                }
                
                if( validationRules != null && validationRules.size() > 0 )
                {
                    for ( ValidationRule validationRule : validationRules )
                    {
                        for( ValidationRuleGroup vrGroup : validationRuleGroups )
                        {
                            if( vrGroup.getMembers().contains( validationRule ) )
                            {
                                synchValidationRuleGroups.add( vrGroup );
                            }
                            
                        }
                        
                    }
                }
            }
            
            if ( orgUnitUIDs != null && orgUnitUIDs.length() > 0 && !orgUnitUIDs.equalsIgnoreCase( "NA" ) )
            {
                System.out.println("Inside MetaDataSynchController 5 " + orgUnitUIDs.length() + "--" + orgUnitUIDs );
                
                String[] organisationUnitUIDList = dataElementsUIDs.split( "," );

                for ( String organisationUnitUID : organisationUnitUIDList )
                {
                    OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitUID );
                    
                    if( organisationUnit != null )
                    {
                        organisationUnits.add( organisationUnit );
                        
                        while( organisationUnit.getParent() != null )
                        {
                            OrganisationUnit orgUnitParent = organisationUnitService.getOrganisationUnit( organisationUnit.getParent().getUid() );
                            
                            if( orgUnitParent != null )
                            {
                                //System.out.print( orgUnitParent.getShortName() + " -> " );
                                organisationUnits.add( orgUnitParent );
                                organisationUnit = orgUnitParent;
                            }
                            else
                            {
                                break;
                            }
                        }
                        
                        if( organisationUnitGroups != null && organisationUnitGroups.size() > 0 )
                        {
                            for( OrganisationUnitGroup ogrGroup : organisationUnitGroups )
                            {
                                if(  ogrGroup.getMembers().contains( organisationUnit )  )
                                {
                                    synchOrganisationUnitGroups.add( ogrGroup );
                                }
                                
                                while( organisationUnit.getParent() != null )
                                {
                                    OrganisationUnit orgUnitParent = organisationUnitService.getOrganisationUnit( organisationUnit.getParent().getUid() );
                                        
                                    if( orgUnitParent != null && ogrGroup.getMembers().contains( orgUnitParent ) )
                                    {
                                        synchOrganisationUnitGroups.add( ogrGroup );
                                        organisationUnit = orgUnitParent;
                                    }
                                    else
                                    {
                                        break;
                                    }
                                       
                                }
                            }
                        }
                        
                        if( organisationUnitGroupSets != null && organisationUnitGroupSets.size() > 0 )
                        {
                            for (OrganisationUnitGroupSet orgGroupSet : organisationUnitGroupSets  )
                            {
                                for( OrganisationUnitGroup synchOrgUnitGroup : synchOrganisationUnitGroups )
                                {
                                    if( orgGroupSet.getOrganisationUnitGroups().contains( synchOrgUnitGroup ) )
                                    {
                                        synchOrganisationUnitGroupSets.add( orgGroupSet );
                                    }
                                }
                            }
                        }
                    }
                                        
                }
            }
        }
        
        else
        {
            
        }
        
        for( Attribute attribute : attributeService.getAllAttributes() )
        {
            if( dataElements != null && dataElements.size() > 0 && attribute.isDataElementAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( synchDataElementGroups != null && synchDataElementGroups.size() > 0 && attribute.isDataElementGroupAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( indicators != null && indicators.size() > 0 && attribute.isIndicatorAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( synchIndicatorGroups != null && synchIndicatorGroups.size() > 0 && attribute.isIndicatorGroupAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( organisationUnits != null && organisationUnits.size() > 0 && attribute.isOrganisationUnitAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( synchOrganisationUnitGroups != null && synchOrganisationUnitGroups.size() > 0 && attribute.isOrganisationUnitGroupAttribute() )
            {
                synchAttributes.add( attribute );
            }
            
            if( synchOrganisationUnitGroupSets != null && synchOrganisationUnitGroupSets.size() > 0 && attribute.isOrganisationUnitGroupSetAttribute() )
            {
                synchAttributes.add( attribute );
            }
        }
        
        
        metaData.setAttributes( new ArrayList<Attribute>( synchAttributes ) );
        
        // dataElement meta data
        metaData.setDataElements( new ArrayList<DataElement>( dataElements ) );
        metaData.setOptionSets( new ArrayList<OptionSet>( optionSets ) );
        metaData.setCategories( new ArrayList<DataElementCategory>( categories ) );
        metaData.setCategoryCombos( new ArrayList<DataElementCategoryCombo>( categoryCombos ) );
        metaData.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( categoryOptionCombos ) );
        metaData.setCategoryOptions( new ArrayList<DataElementCategoryOption>( categoryOptions ) );
        
        metaData.setDataElementGroups( new ArrayList<DataElementGroup>( synchDataElementGroups ) );
        metaData.setDataElementGroupSets( new ArrayList<DataElementGroupSet>( synchDataElementGroupSets ) );
        
        // Indicator meta data
        metaData.setIndicators(  new ArrayList<Indicator>( indicators ) );
        metaData.setIndicatorTypes(  new ArrayList<IndicatorType>( synchIndicatorTypes ) );
        metaData.setIndicatorGroups(  new ArrayList<IndicatorGroup>( synchIndicatorGroups ) );
        metaData.setIndicatorGroupSets(  new ArrayList<IndicatorGroupSet>( synchIndicatorGroupSets ) );

        // ValidationRule meta data
        metaData.setValidationRules( new ArrayList<ValidationRule>( validationRules ) );
        metaData.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( synchValidationRuleGroups ) );
        
        // orgUnit meta data
        metaData.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnits ) );
        metaData.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( synchOrganisationUnitGroups ) );
        metaData.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( synchOrganisationUnitGroupSets ) );
        
        System.out.println(" Final Meta Data is " + metaData );
        
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );

        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
        
    }

    
    
    // method for getting meta data related to dataElements
    public MetaData getMetaDataDataElement( List<String> deUIDList )
    {
        //MetaData resultMetaData  = metaData;
        
        MetaData resultDEMetaData  = new MetaData();
        
        Set<DataElementGroup> dataElementGroups = new HashSet<DataElementGroup>();
        Set<DataElementGroup> synchDataElementGroups = new HashSet<DataElementGroup>();
        
        Set<DataElementGroupSet> dataElementGroupSets = new HashSet<DataElementGroupSet>();
        Set<DataElementGroupSet> synchDataElementGroupSets = new HashSet<DataElementGroupSet>();
        
        dataElementGroups =new HashSet<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        dataElementGroupSets = new HashSet<DataElementGroupSet>( dataElementService.getAllDataElementGroupSets() );
        
        Set<DataElement> filteredDataElements = new HashSet<DataElement>();
        Set<DataElementCategory> filteredCategories = new HashSet<DataElementCategory>();
        Set<DataElementCategoryOption> filteredCategoryOptions = new HashSet<DataElementCategoryOption>();
        Set<DataElementCategoryOptionCombo> filteredCategoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        Set<DataElementCategoryCombo> filteredCategoryCombos = new HashSet<DataElementCategoryCombo>();
        
        Set<OptionSet> filteredOptionSets = new HashSet<OptionSet>();
        Set<DataElementGroup> filteredDataElementGroups = new HashSet<DataElementGroup>();
        Set<DataElementGroupSet> filteredDataElementGroupSets = new HashSet<DataElementGroupSet>();

        for ( String dataElementUID : deUIDList )
        {
            DataElement dataElement = dataElementService.getDataElement( dataElementUID );
            filteredDataElements.add( dataElement );
        }
            
        if( filteredDataElements != null && filteredDataElements.size() > 0 )
        {
            for ( DataElement dataElement : filteredDataElements )
            {
                if( dataElement.getOptionSet() != null  )
                {
                    filteredOptionSets.add( dataElement.getOptionSet() );
                    
                }
                
                if( dataElement.getCategoryCombo() != null  )
                {
                    filteredCategoryCombos.add( dataElement.getCategoryCombo() );
                    
                    if( dataElement.getCategoryCombo().getCategories() != null && dataElement.getCategoryCombo().getCategories().size() > 0  )
                    {
                        filteredCategories.addAll( dataElement.getCategoryCombo().getCategories() );
                    }
                    
                    if( dataElement.getCategoryCombo().getCategoryOptions() != null && dataElement.getCategoryCombo().getCategoryOptions().size() > 0 )
                    {
                        filteredCategoryOptions.addAll( dataElement.getCategoryCombo().getCategoryOptions() );
                    }
                    
                    if( dataElement.getCategoryCombo().getOptionCombos() != null && dataElement.getCategoryCombo().getOptionCombos().size() > 0 )
                    {
                        filteredCategoryOptionCombos.addAll( dataElement.getCategoryCombo().getOptionCombos() );
                    }
                    
                }
                
                if( dataElementGroups != null && dataElementGroups.size() > 0 )
                {
                    for( DataElementGroup deGroup : dataElementGroups )
                    {
                        if( deGroup.getMembers().contains( dataElement ) )
                        {
                            synchDataElementGroups.add( deGroup );
                        }
                    }
                }
                
                if( dataElementGroupSets != null && dataElementGroupSets.size() > 0 )
                {
                    for ( DataElementGroupSet deGroupSet : dataElementGroupSets )
                    {
                        for( DataElementGroup synchDEGroup : synchDataElementGroups )
                        {
                            if( deGroupSet.getMembers().contains( synchDEGroup ) )
                            {
                                synchDataElementGroupSets.add( deGroupSet );
                            }
                        }
                        
                    }
                }
            }
        }
        
        resultDEMetaData.setDataElements( new ArrayList<> ( filteredDataElements ) );
        
        resultDEMetaData.setCategories( new ArrayList<DataElementCategory>( filteredCategories ) );
        resultDEMetaData.setCategoryCombos( new ArrayList<>( filteredCategoryCombos ) );
        resultDEMetaData.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( filteredCategoryOptionCombos ) );
        resultDEMetaData.setCategoryOptions( new ArrayList<DataElementCategoryOption>( filteredCategoryOptions ) );
        
        resultDEMetaData.setOptionSets( new ArrayList<>( filteredOptionSets ) );
        resultDEMetaData.setDataElementGroups( new ArrayList<>( filteredDataElementGroups ) );
        resultDEMetaData.setDataElementGroupSets( new ArrayList<>( filteredDataElementGroupSets ) );

        return resultDEMetaData;
        
    }
        
    // find dataElement from Expression   
    public Set<String> getDataElementsInExpression( String expression )
    {
        Set<String> dataElementsInExpression = null;

        if ( expression != null )
        {
            dataElementsInExpression = new HashSet<>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                String deUID =  matcher.group( 1 );
                dataElementsInExpression.add( deUID );
                
            }
        }
        return dataElementsInExpression;
    }    

}
