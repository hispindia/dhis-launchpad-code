package org.hisp.dhis.sm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MetaDataValidationCheck
{

    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private ExpressionService expressionService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Util methods
    // -------------------------------------------------------------------------
    
    public void validateDataElements( MetaData metaData, Collection<String> dataElementUids )
    {
        //Collection<DataElementCategory> deCategories = metaData.getCategories();
        //Collection<DataElementCategoryOption> deCategoryOptions = metaData.getCategoryOptions();
        Collection<DataElementCategoryCombo> deCategoryCombos = metaData.getCategoryCombos();
        Collection<DataElement> dataElements = metaData.getDataElements();
        
        for( DataElement de : dataElements )
        {
            if( dataElementUids.contains( de.getUid() ) )
            {
                DataElementCategoryCombo currentdecc = de.getCategoryCombo();
                for( DataElementCategoryCombo decc : deCategoryCombos )
                {
                    if( decc.getUid().equals( currentdecc.getUid() ) )
                    {
                        //decc.get
                    }
                }
            }
        }
    }
    
    // complete methods
    public MetaData getMetaData(  MetaData metaData, List<String> deUIDList, List<String> indicatorUIDList, List<String> organisationUnitUIDList )
    {
        MetaData completeMetaData = metaData;
        
        //System.out.println( " 0 pppppp --- " + metaData.getIndicators().size() );
        
        //System.out.println( " 10 pppppp --- " + metaData.getIndicators().size() );
        
        Set<DataElement> completeDes = new HashSet<>();
        Set<DataElementCategoryCombo> completeDeCombo = new HashSet<>();
        Set<OptionSet> completeDeOptionSet = new HashSet<>();
        
        completeMetaData.setDataElements( new ArrayList<>( completeDes )  );
        completeMetaData.setCategoryCombos(  new ArrayList<>( completeDeCombo )  );
        completeMetaData.setOptionSets(  new ArrayList<>( completeDeOptionSet )  );
        
        //System.out.println( " 20 pppppp --- " + metaData.getIndicators().size() );
        
        // for dataElement
        if( deUIDList != null && deUIDList.size() > 0 )
        {        
            //ystem.out.println( " 00 pppppp --- " + metaData.getIndicators().size() );
            
            MetaData deMetaData = getMetaDataDataElement( deUIDList, metaData );
            completeDes.addAll( deMetaData.getDataElements() );
            completeDeCombo.addAll( deMetaData.getCategoryCombos() );
            completeDeOptionSet.addAll( deMetaData.getOptionSets() );
            
            completeMetaData.setDataElements( new ArrayList<>( completeDes ) );
            completeMetaData.setCategoryCombos( new ArrayList<>( completeDeCombo ) );
            completeMetaData.setOptionSets( new ArrayList<>( completeDeOptionSet ) );
            
        }
        
        // for indicator 
        Set<Indicator> completeIndicator = new HashSet<>();
        Set<IndicatorType> completeIndicatorType = new HashSet<>();
        
        //System.out.println( " 30 pppppp --- " + metaData.getIndicators().size() );
        
        completeMetaData.setIndicators(  new ArrayList<>( completeIndicator )  );
        completeMetaData.setIndicatorTypes(new ArrayList<>( completeIndicatorType )  );
        
        completeMetaData.setCategoryCombos(  new ArrayList<>( completeDeCombo )  );
        
        //System.out.println( " 40 pppppp --- " + metaData.getIndicators().size() );
        
        if( indicatorUIDList != null && indicatorUIDList.size() > 0 )
        {       
            //System.out.println( "######### --- inside indicator importing" );
            
            //System.out.println( " 50 pppppp --- " + metaData.getIndicators().size() );
            
            MetaData indicatorMetaData = getMetaDataIndicator( indicatorUIDList, metaData );
            completeDes.addAll( indicatorMetaData.getDataElements() );
            completeDeCombo.addAll( indicatorMetaData.getCategoryCombos() );
            completeDeOptionSet.addAll( indicatorMetaData.getOptionSets() );
            
            
            completeIndicator.addAll( indicatorMetaData.getIndicators() );
            completeIndicatorType.addAll( indicatorMetaData.getIndicatorTypes() );
            
            completeMetaData.setDataElements( new ArrayList<>( completeDes ) );
            completeMetaData.setCategoryCombos( new ArrayList<>( completeDeCombo ) );
            completeMetaData.setOptionSets( new ArrayList<>( completeDeOptionSet ) );
            
            completeMetaData.setIndicators( new ArrayList<>( completeIndicator ) );
            completeMetaData.setIndicatorTypes( new ArrayList<>( completeIndicatorType ) );
            
        }
        
        // for OrganisationUnit
        Set<OrganisationUnit> completeOrganisationUnits = new HashSet<>();
       
        completeMetaData.setOrganisationUnits( new ArrayList<>( completeOrganisationUnits ) );
        
        if( organisationUnitUIDList != null && organisationUnitUIDList.size() > 0 )
        {        
            MetaData organisationUnitMetaData = getMetaDataOrganisationUnit( organisationUnitUIDList, metaData );
            
            completeOrganisationUnits.addAll( organisationUnitMetaData.getOrganisationUnits() );

            completeMetaData.setOrganisationUnits( new ArrayList<>( completeOrganisationUnits ) );
            
        }
        
        return completeMetaData;
    }
 
    
    // get MetaData OrganisationUnit
    public MetaData getMetaDataOrganisationUnit( List<String> organisationUnitUIDList, MetaData metaData )
    {
    	
        //MetaData resultMetaData  = metaData;
        
        MetaData resultMetaData  = new MetaData();
        
        //System.out.println( "******************* Inside getMetaDataOrganisationUnit : *********************");
        List<OrganisationUnit> organisationUnits  = metaData.getOrganisationUnits();
        List<OrganisationUnitGroup> organisationUnitGroups  = metaData.getOrganisationUnitGroups();
        List<OrganisationUnitGroupSet> organisationUnitGroupSets  = metaData.getOrganisationUnitGroupSets();
        
        Set<OrganisationUnit> filterOrganisationUnits = new HashSet<OrganisationUnit>();
        Set<OrganisationUnitGroup> filterOrganisationUnitGroups = new HashSet<OrganisationUnitGroup>();
        Set<OrganisationUnitGroupSet> filterOrganisationUnitGroupSets = new HashSet<OrganisationUnitGroupSet>();
        
        //List<OrganisationUnitLevel> organisationUnitLevels  = metaData.getOrganisationUnitLevels();
        
        Map<String, OrganisationUnit> orgUnitUIDMap = new HashMap<String, OrganisationUnit>();        
        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            orgUnitUIDMap.put(organisationUnit.getUid(), organisationUnit );
        }
        
        
        Map<String, List<String>> orgUnitGroupUIDMap = new HashMap<String, List<String>>();        
        for ( OrganisationUnitGroup orgGroup : organisationUnitGroups )
        {
            List<String> orgUIDs = new ArrayList<>();
            for( OrganisationUnit ou : orgGroup.getMembers() )
            {
                orgUIDs.add( ou.getUid() );                
            }
            orgUnitGroupUIDMap.put( orgGroup.getUid(), orgUIDs );
        }
        
        Map<String, List<String>> orgGroupSetsUIDMap = new HashMap<String, List<String>>(); 
        for ( OrganisationUnitGroupSet orgGroupSet : organisationUnitGroupSets )
        {
            List<String> orgGroupUIDs = new ArrayList<>();
            for( OrganisationUnitGroup orgGroup : orgGroupSet.getOrganisationUnitGroups() )
            {
                orgGroupUIDs.add( orgGroup.getUid() );                
            }
            orgGroupSetsUIDMap.put( orgGroupSet.getUid(), orgGroupUIDs );
        }
        

        for ( String organisationUnitUID : organisationUnitUIDList )
        {
        	//System.out.println( "\n**************** " + organisationUnitUID + " ***********************" );
        	
        	OrganisationUnit orgUnit = orgUnitUIDMap.get( organisationUnitUID );
        	
        	if( orgUnit != null )
        	{
        		//System.out.print( orgUnit.getShortName() + " -> " );
        		
        		filterOrganisationUnits.add( orgUnit );
        		
        		while( orgUnit.getParent() != null )
        		{
        			OrganisationUnit orgUnitParent = orgUnitUIDMap.get( orgUnit.getParent().getUid() );
        			if( orgUnitParent != null )
        			{
        			    //System.out.print( orgUnitParent.getShortName() + " -> " );
        			    filterOrganisationUnits.add( orgUnitParent );
        			    orgUnit = orgUnitParent;
        			}
        			else
        			{
        			    //System.out.print( "Breaking from while........." );
        			    break;
        			}
        		}
        		
        		for( OrganisationUnitGroup ogrGroup : organisationUnitGroups )
                        {
                            if( orgUnitGroupUIDMap.get( ogrGroup.getUid() ).contains( organisationUnitUID ) )
                            {
                                filterOrganisationUnitGroups.add( ogrGroup );
                            }
                            
                            while( orgUnit.getParent() != null )
                            {
                                    OrganisationUnit orgUnitParent = orgUnitUIDMap.get( orgUnit.getParent().getUid() );
                                    
                                    if( orgUnitParent != null && orgUnitGroupUIDMap.get( ogrGroup.getUid() ).contains( orgUnitParent.getUid() ) )
                                    {
                                        filterOrganisationUnitGroups.add( ogrGroup );
                                        orgUnit = orgUnitParent;
                                    }
                                   
                            }
                        }
        		
        		for (OrganisationUnitGroupSet orgGroupSet : organisationUnitGroupSets  )
                        {
                            for( OrganisationUnitGroup filteredOrgUnitGroup : filterOrganisationUnitGroups )
                            {
                                if( orgGroupSetsUIDMap.get( orgGroupSet.getUid() ).contains( filteredOrgUnitGroup.getUid() ) )
                                {
                                    filterOrganisationUnitGroupSets.add( orgGroupSet );
                                }
                            }
                            
                        }
                        
        	}
        	
        	/*
            for ( OrganisationUnit organisationUnit : organisationUnits )
            {
                if ( organisationUnit.getUid().equals( organisationUnitUID ) )
                {
                    
                    filteredOrganisationUnits.add( organisationUnit );
                    
                    System.out.print( organisationUnit.getShortName() );
                    
                    OrganisationUnit orgUnit = organisationUnit;
                    
                    while ( orgUnit.getParent() != null )
                    {
                    	filteredOrganisationUnits.add( orgUnit.getParent() );
                        
                        orgUnit = orgUnit.getParent();
                        
                        System.out.print( orgUnit.getShortName() + " -> " );
                    }

                    break;
                }
            }
            */
            
        }
        
        resultMetaData.setOrganisationUnits( new ArrayList<OrganisationUnit> ( filterOrganisationUnits ) );
        resultMetaData.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup> ( filterOrganisationUnitGroups ) );
        resultMetaData.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet> ( filterOrganisationUnitGroupSets )  );
        
        //resultMetaData.setDataElements( null );
        //resultMetaData.setCategoryCombos( null );
        //resultMetaData.setOptionSets( null );
        //resultMetaData.setIndicators( null );
        //resultMetaData.setIndicatorTypes( null );
        
        //System.out.println( "######### --- filtered Organisation Units Size " + filteredOrganisationUnits.size() );
        
        return resultMetaData;
        
    }    
    
    // get MetaData DataElement
    public MetaData getMetaDataDataElement( List<String> deUIDList, MetaData metaData )
    {
        //MetaData resultMetaData  = metaData;
        
        MetaData resultMetaData  = new MetaData();
        
        List<DataElement> dataElements  = metaData.getDataElements();
        List<DataElementCategoryCombo> deCCs = metaData.getCategoryCombos();
        List<OptionSet> optionSets = metaData.getOptionSets();
        List<DataElementGroup> dataElementGroups = metaData.getDataElementGroups();
        List<DataElementGroupSet> dataElementGroupSets = metaData.getDataElementGroupSets();
        
        
        Set<DataElement> filteredDataElements = new HashSet<>();
        Set<DataElementCategoryCombo> filteredDECCs = new HashSet<>();
        Set<OptionSet> filteredOptionSets = new HashSet<>();
        Set<DataElementGroup> filteredDataElementGroups = new HashSet<>();
        Set<DataElementGroupSet> filteredDataElementGroupSets = new HashSet<>();
        
        Map<String, List<String>> deGroupUIDMap = new HashMap<String, List<String>>();        
        for ( DataElementGroup deGroup : dataElementGroups )
        {
            List<String> deUIDs = new ArrayList<>();
            for( DataElement de : deGroup.getMembers() )
            {
                deUIDs.add( de.getUid() );                
            }
            deGroupUIDMap.put(deGroup.getUid(), deUIDs );
        }
        
        
        Map<String, List<String>> deGroupSetsUIDMap = new HashMap<String, List<String>>(); 
        
        for ( DataElementGroupSet deGroupSet : dataElementGroupSets )
        {
            List<String> deGroupUIDs = new ArrayList<>();
            for( DataElementGroup dataElementGroup : deGroupSet.getMembers() )
            {
                deGroupUIDs.add( dataElementGroup.getUid() );                
            }
            deGroupSetsUIDMap.put( deGroupSet.getUid(), deGroupUIDs );
        }
        

        for ( String dataElementUID : deUIDList )
        {
            for ( DataElement dataElement : dataElements )
            {
                if ( dataElement.getUid().equals( dataElementUID ) )
                {
                    filteredDataElements.add( dataElement );
                    
                    //filtereddataElementGroups.addAll( dataElement.getGroups() );
                    
                    for( DataElementCategoryCombo decc : deCCs ) 
                    {
                        if( dataElement.getCategoryCombo() != null && decc.getUid().equals( dataElement.getCategoryCombo().getUid() ) )
                        {
                            filteredDECCs.add( decc );                            
                            break;
                        }
                    }
                    
                    for( OptionSet os : optionSets )
                    {
                        if( dataElement.getOptionSet() != null && os.getUid().equals( dataElement.getOptionSet().getUid() ) )
                        {
                            filteredOptionSets.add( os );
                            break;
                        }
                    }
                    /*
                    for( DataElementGroup deGroup : dataElementGroups )
                    {
                        if( dataElement.getGroups().contains( deGroup ) )
                        {
                            filtereddataElementGroups.add( deGroup );
                            break;
                        }
                        
                        
                    }
                    */
                    
                    for( DataElementGroup deGroup : dataElementGroups )
                    {
                        if( deGroupUIDMap.get( deGroup.getUid() ).contains( dataElementUID ) )
                        {
                            filteredDataElementGroups.add( deGroup );
                        }
                        
                    }
                    
                    for ( DataElementGroupSet deGroupSet : dataElementGroupSets )
                    {
                        for( DataElementGroup filteredDEGroup : filteredDataElementGroups )
                        {
                            if( deGroupSetsUIDMap.get( deGroupSet.getUid() ).contains( filteredDEGroup.getUid() ) )
                            {
                                filteredDataElementGroupSets.add( deGroupSet );
                            }
                        }
                        
                    }
                    
                    break;
                }
            }
        }
        
        resultMetaData.setDataElements( new ArrayList<> ( filteredDataElements ) );
        resultMetaData.setCategoryCombos( new ArrayList<>( filteredDECCs ) );
        resultMetaData.setOptionSets( new ArrayList<>( filteredOptionSets ) );
        resultMetaData.setDataElementGroups( new ArrayList<>( filteredDataElementGroups ) );
        resultMetaData.setDataElementGroupSets( new ArrayList<>( filteredDataElementGroupSets ) );
        
        //resultMetaData.setOrganisationUnits( null );
        //resultMetaData.setIndicators( null );
        //resultMetaData.setIndicatorTypes( null );
        
        //System.out.println( " ######### --- filtered Data Element Groups " + resultMetaData.getDataElementGroups().size() );
        //System.out.println( " ######### --- filtered Data Element Group Sets " + resultMetaData.getDataElementGroupSets().size() );
        
        return resultMetaData;
        
    }
    
    // get MetaData Indicator
    public MetaData getMetaDataIndicator( List<String> indicatorUIDList, MetaData metaData )
    {
        //MetaData resultMetaData  = metaData;
        
        MetaData resultMetaData  = new MetaData();
        
        String deUIDExpression = "";
        
        List<Indicator> indicators  = metaData.getIndicators();
        List<IndicatorType> indicatorTypes = metaData.getIndicatorTypes();
        List<IndicatorGroup> indicatorGroups = metaData.getIndicatorGroups();
        List<IndicatorGroupSet> indicatorGroupSets = metaData.getIndicatorGroupSets();
        
        
        
        Set<Indicator> filteredIndicators = new HashSet<Indicator>();
        Set<IndicatorType> filteredIndicatorTypes = new HashSet<IndicatorType>();
        Set<IndicatorGroup> filteredIndicatorGroups = new HashSet<>();
        Set<IndicatorGroupSet> filteredIndicatorGroupSets = new HashSet<>();
        
        
        Map<String, List<String>> indicatorGroupUIDMap = new HashMap<String, List<String>>();        
        for ( IndicatorGroup indicatorGroup : indicatorGroups )
        {
            List<String> indicatorUIDs = new ArrayList<>();
            for( Indicator indicator : indicatorGroup.getMembers() )
            {
                indicatorUIDs.add( indicator.getUid() );                
            }
            indicatorGroupUIDMap.put( indicatorGroup.getUid(), indicatorUIDs );
        }
        
        
        Map<String, List<String>> indicatorGroupSetsUIDMap = new HashMap<String, List<String>>(); 
        for ( IndicatorGroupSet indicatorGroupSet : indicatorGroupSets )
        {
            List<String> indicatorGroupUIDs = new ArrayList<>();
            for( IndicatorGroup indicatorGroup : indicatorGroupSet.getMembers() )
            {
                indicatorGroupUIDs.add( indicatorGroup.getUid() );                
            }
            indicatorGroupSetsUIDMap.put( indicatorGroupSet.getUid(), indicatorGroupUIDs );
        }
        
        
        
        
        
        //Set<DataElement> filteredDataElements = new HashSet<>();
        
        //System.out.println( " 1 pppppp --- " + indicators.size() );
        
        for ( String indicatorUID : indicatorUIDList )
        {
            //System.out.println( " 2 pppppp --- " + indicatorUIDList.size() );
            
            for ( Indicator indicator : indicators )
            {
                //System.out.println( " 3 pppppp --- " + indicators.size() );
                
                //filteredDataElements.addAll( new HashSet<>( expressionService.getDataElementsInExpression( indicator.getDenominator() ) ) );
                //filteredDataElements.addAll( new HashSet<>( expressionService.getDataElementsInExpression( indicator.getNumerator() ) ) );
                //indicator.getDenominator();
                //indicator.getNumerator();
                //System.out.println( " 4 ######### --- OutSide indicator UID " + indicator.getUid() + "--" + indicatorUID );
                
                if ( indicator.getUid().equals( indicatorUID ) )
                {
                    //System.out.println( " 5 ######### --- Inside indicator UID " + indicator.getUid() + "--" + indicatorUID );
                    
                    deUIDExpression += "+" + indicator.getNumerator() + "+" + indicator.getDenominator();
                    
                    filteredIndicators.add( indicator );
                    
                    for( IndicatorType indicatorType  : indicatorTypes ) 
                    {
                        System.out.println( " 1 Indicator Name : " + indicator.getName() + "--" + indicatorType.getUid() + "--"+ indicator.getIndicatorType().getUid() );
                        
                        if( indicator.getIndicatorType() != null && indicatorType.getUid().equals( indicator.getIndicatorType().getUid() ) )
                        {
                            System.out.println( " 2 Indicator Name : " + indicator.getName() + "--" + indicatorType.getUid() + "--"+ indicator.getIndicatorType().getUid() );
                            
                            filteredIndicatorTypes.add( indicatorType );                            
                            break;
                        }
                    }
                    
                    for( IndicatorGroup indicatorGroup : indicatorGroups )
                    {
                        if( indicatorGroupUIDMap.get( indicatorGroup.getUid() ).contains( indicatorUID ) )
                        {
                            filteredIndicatorGroups.add( indicatorGroup );
                        }
                        
                    }
                    
                    for ( IndicatorGroupSet indicatorGroupSet : indicatorGroupSets )
                    {
                        for( IndicatorGroup filteredIndicatorGroup : filteredIndicatorGroups )
                        {
                            if( indicatorGroupSetsUIDMap.get( indicatorGroupSet.getUid() ).contains( filteredIndicatorGroup.getUid() ) )
                            {
                                filteredIndicatorGroupSets.add( indicatorGroupSet );
                            }
                        }
                        
                    }
                    
                    
                    break;
                }
            }
        }
        
        List<String> deUIDList = new ArrayList<>( getDataElementsInExpression( deUIDExpression ) );
        
        MetaData deMetaData = getMetaDataDataElement( deUIDList, metaData );
        
        resultMetaData.setDataElements( deMetaData.getDataElements() );
        resultMetaData.setCategoryCombos( deMetaData.getCategoryCombos() );
        resultMetaData.setOptionSets( deMetaData.getOptionSets() );
        
        //Set<DataElement> filteredDataElements = new HashSet<>( expressionService.getDataElementsInIndicators( filteredIndicators ) );
        //resultMetaData.setDataElements( new ArrayList<> ( filteredDataElements ) );
        
        resultMetaData.setIndicators( new ArrayList<Indicator> ( filteredIndicators ) );
        resultMetaData.setIndicatorTypes( new ArrayList<IndicatorType> ( filteredIndicatorTypes ) );
        resultMetaData.setIndicatorGroups(  new ArrayList<IndicatorGroup> ( filteredIndicatorGroups ) );
        resultMetaData.setIndicatorGroupSets( new ArrayList<IndicatorGroupSet> ( filteredIndicatorGroupSets )   );
        
        //resultMetaData.setOrganisationUnits( null );
        
        
        //System.out.println( "######### --- filtered Indicators Size " + filteredIndicators.size() );
        
        return resultMetaData;
        
    }
    
    // get MetaDataValidation Rule
    public MetaData getMetaDataValidationRule( List<String> validationRuleUIDList, MetaData metaData )
    {
        //MetaData resultMetaData  = metaData;
        
        MetaData resultMetaData  = new MetaData();
        
        String deUIDExpression = "";
        
        List<ValidationRule> validationRules  = metaData.getValidationRules();
        List<ValidationRuleGroup> validationRuleGroups = metaData.getValidationRuleGroups();
        
        Set<ValidationRule> filteredvalidationRules = new HashSet<ValidationRule>();
        Set<ValidationRuleGroup> filteredValidationRuleGroups = new HashSet<>();
        
        Map<String, List<String>> vrGroupUIDMap = new HashMap<String, List<String>>();        
        for ( ValidationRuleGroup vrGroup : validationRuleGroups )
        {
            List<String> vrUIDs = new ArrayList<>();
            for( ValidationRule vr : vrGroup.getMembers() )
            {
                vrUIDs.add( vr.getUid() );                
            }
            vrGroupUIDMap.put( vrGroup.getUid(), vrUIDs );
        }
        
        for ( String validationRuleUID : validationRuleUIDList )
        {
            
            for ( ValidationRule validationRule : validationRules )
            {

                if ( validationRule.getUid().equals( validationRuleUID ) )
                {
                    
                    deUIDExpression += "+" + validationRule.getLeftSide() + "+" + validationRule.getRightSide();
                    
                    filteredvalidationRules.add( validationRule );
                    
                    break;
                }
                
                
                for( ValidationRuleGroup vrGroup : validationRuleGroups )
                {
                    if( vrGroupUIDMap.get( vrGroup.getUid() ).contains( validationRuleUID ) )
                    {
                        filteredValidationRuleGroups.add( vrGroup );
                    }
                    
                }
            }
        }
        
        List<String> deUIDList = new ArrayList<>( getDataElementsInExpression( deUIDExpression ) );
        
        MetaData deMetaData = getMetaDataDataElement( deUIDList, metaData );
        
        resultMetaData.setDataElements( deMetaData.getDataElements() );
        resultMetaData.setCategoryCombos( deMetaData.getCategoryCombos() );
        resultMetaData.setOptionSets( deMetaData.getOptionSets() );
        
        resultMetaData.setValidationRules(  new ArrayList<ValidationRule>( filteredvalidationRules ) );
        resultMetaData.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( filteredValidationRuleGroups )  );
        //System.out.println( "######### --- filtered ValidationRule Size " + filteredvalidationRules.size() );
        
        return resultMetaData;
        
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

