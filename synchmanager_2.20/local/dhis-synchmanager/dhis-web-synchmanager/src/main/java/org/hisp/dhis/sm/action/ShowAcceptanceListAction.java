package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * User: gaurav Date: 2/8/14 Time: 12:13 PM
 */
public class ShowAcceptanceListAction
    implements Action
{

    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    private DataElementService dataElementService;

    public DataElementService getDataElementService()
    {
        return dataElementService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public IndicatorService indicatorService;

    public IndicatorService getIndicatorService()
    {
        return indicatorService;
    }

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    @Autowired
    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    /*
     * private Integer instanceId;
     * 
     * public void setInstanceId( Integer instanceId ) { this.instanceId =
     * instanceId; }
     */

    List<DataElement> newDataElements = new ArrayList<DataElement>();

    public List<DataElement> getNewDataElements()
    {
        return newDataElements;
    }

    public void setNewDataElements( List<DataElement> newDataElements )
    {
        this.newDataElements = newDataElements;
    }

    List<DataElement> updatedDataElements = new ArrayList<DataElement>();

    public List<DataElement> getUpdatedDataElements()
    {
        return updatedDataElements;
    }

    public void setUpdatedDataElements( List<DataElement> updatedDataElements )
    {
        this.updatedDataElements = updatedDataElements;
    }

    public List<String> dataElementUpdates = new ArrayList<String>();

    public List<String> getDataElementUpdates()
    {
        return dataElementUpdates;
    }

    public void setDataElementUpdates( List<String> dataElementUpdates )
    {
        this.dataElementUpdates = dataElementUpdates;
    }

    public String metaDataString;

    public String getMetaDataString()
    {
        return metaDataString;
    }

    public void setMetaDataString( String metaDataString )
    {
        this.metaDataString = metaDataString;
    }

    public List<Indicator> newIndicators = new ArrayList<>();

    public List<String> newIndicatorsDetails = new ArrayList<>();

    public List<String> getNewIndicatorsDetails()
    {
        return newIndicatorsDetails;
    }

    public void setNewIndicatorsDetails( List<String> newIndicatorsDetails )
    {
        this.newIndicatorsDetails = newIndicatorsDetails;
    }

    public List<Indicator> updatedIndicators = new ArrayList<>();

    public List<String> indicatorUpdates = new ArrayList<>();

    public List<OrganisationUnit> newOrgUnits = new ArrayList<>();

    public List<OrganisationUnit> updatedOrgUnits = new ArrayList<>();

    public List<String> orgUnitUpdates = new ArrayList<>();

    public List<ValidationRule> newValidationRules = new ArrayList<>();

    public List<ValidationRule> updatedValidationRules = new ArrayList<>();

    public List<String> validationRuleUpdates = new ArrayList<>();

    public List<ValidationRule> getNewValidationRules()
    {
        return newValidationRules;
    }

    public void setNewValidationRules( List<ValidationRule> newValidationRules )
    {
        this.newValidationRules = newValidationRules;
    }

    public List<ValidationRule> getUpdatedValidationRules()
    {
        return updatedValidationRules;
    }

    public void setUpdatedValidationRules( List<ValidationRule> updatedValidationRules )
    {
        this.updatedValidationRules = updatedValidationRules;
    }

    public List<String> getValidationRuleUpdates()
    {
        return validationRuleUpdates;
    }

    public void setValidationRuleUpdates( List<String> validationRuleUpdates )
    {
        this.validationRuleUpdates = validationRuleUpdates;
    }

    public List<OrganisationUnit> getNewOrgUnits()
    {
        return newOrgUnits;
    }

    public void setNewOrgUnits( List<OrganisationUnit> newOrgUnits )
    {
        this.newOrgUnits = newOrgUnits;
    }

    public List<OrganisationUnit> getUpdatedOrgUnits()
    {
        return updatedOrgUnits;
    }

    public void setUpdatedOrgUnits( List<OrganisationUnit> updatedOrgUnits )
    {
        this.updatedOrgUnits = updatedOrgUnits;
    }

    public List<String> getOrgUnitUpdates()
    {
        return orgUnitUpdates;
    }

    public void setOrgUnitUpdates( List<String> orgUnitUpdates )
    {
        this.orgUnitUpdates = orgUnitUpdates;
    }

    public List<Indicator> getNewIndicators()
    {
        return newIndicators;
    }

    public void setNewIndicators( List<Indicator> newIndicators )
    {
        this.newIndicators = newIndicators;
    }

    public List<Indicator> getUpdatedIndicators()
    {
        return updatedIndicators;
    }

    public void setUpdatedIndicators( List<Indicator> updatedIndicators )
    {
        this.updatedIndicators = updatedIndicators;
    }

    public List<String> getIndicatorUpdates()
    {
        return indicatorUpdates;
    }

    public void setIndicatorUpdates( List<String> indicatorUpdates )
    {
        this.indicatorUpdates = indicatorUpdates;
    }

    /*
     * private String thisUrl;
     * 
     * public void setThisUrl( String thisUrl ) { this.thisUrl = thisUrl; }
     */

    @Override
    public String execute()
        throws Exception
    {
        Set<SynchInstance> instances = synchInstanceService.getInstancesByType( "meta-data" );

        HttpServletRequest request = ServletActionContext.getRequest();

        String thisUrlTemp = request.getRequestURL().toString();

        String[] urlFrgmant = thisUrlTemp.split( "/" );

        // System.out.println( "This URL is " +
        // urlFrgmant[0]+"/"+urlFrgmant[1]+"/"+urlFrgmant[2]+"/"+urlFrgmant[3]+"/api"
        // );

        String thisUrl = urlFrgmant[0] + "/" + urlFrgmant[1] + "/" + urlFrgmant[2] + "/" + urlFrgmant[3] + "/api";

        try
        {

            String dataElementURL = instances.iterator().next().getUrl() + "/AccepetanceDE.xml";

            String indicatorURL = instances.iterator().next().getUrl() + "/AccepetanceIndicator.xml";

            String orgUnitURL = instances.iterator().next().getUrl() + "/AccepetanceOrganisationUnit.xml";

            String validationURL = instances.iterator().next().getUrl() + "/AccepetanceValidationRule.xml";

            MetaData dataElementList = synchManager.getMetaData( instances.iterator().next(), dataElementURL, thisUrl );

            MetaData indicatorList = synchManager.getMetaData( instances.iterator().next(), indicatorURL, thisUrl );

            MetaData orgUnitList = synchManager.getMetaData( instances.iterator().next(), orgUnitURL, thisUrl );

            MetaData validationRuleList = synchManager
                .getMetaData( instances.iterator().next(), validationURL, thisUrl );

            dataElementUpdates.add( "Cushion Item" );

            indicatorUpdates.add( "Cushion Item" );

            orgUnitUpdates.add( "Cushion Item" );

            validationRuleUpdates.add( "Cushion Item" );

            if ( dataElementList != null )
            {
                List<DataElement> dataElements = dataElementList.getDataElements();

                for ( DataElement dataElement : dataElements )
                {
                    if ( dataElementService.getDataElement( dataElement.getUid() ) == null )
                    {
                        newDataElements.add( dataElement );
                    }
                    else
                    {
                        updatedDataElements.add( dataElement );
                        DataElement oldDataElement = dataElementService.getDataElement( dataElement.getUid() );

                        String changeLog = new String( "" );

                        if ( !dataElement.getName().equals( oldDataElement.getName() ) )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Name</td>" + "<td>" + oldDataElement.getName()
                                + "</td>" + "<td>" + dataElement.getName() + "</td>" + "</tr>" );
                        }
                        if ( !dataElement.getAggregationOperator().equals( oldDataElement.getAggregationOperator() ) )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Aggregation Operator</td>" + "<td>"
                                + oldDataElement.getAggregationOperator() + "</td>" + "<td>"
                                + dataElement.getAggregationOperator() + "</td>" + "</tr>" );
                        }
                        if ( !dataElement.getType().equals( oldDataElement.getType() ) )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Type</td>" + "<td>" + oldDataElement.getType()
                                + "</td>" + "<td>" + dataElement.getType() + "</td>" + "</tr>" );

                        }
                        if ( !dataElement.getShortName().equals( oldDataElement.getShortName() ) )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Short Name</td>" + "<td>"
                                + oldDataElement.getShortName() + "</td>" + "<td>" + dataElement.getShortName()
                                + "</td>" + "</tr>" );
                        }
                        /*
                         * if(!dataElement.getNumberType().equals(oldDataElement.
                         * getNumberType())) { changeLog =
                         * changeLog.concat("<tr>"+
                         * "<td>Number Type</td>"+"<td>"
                         * +oldDataElement.getNumberType
                         * ()+"</td>"+"<td>"+dataElement
                         * .getNumberType()+"</td>"+ "</tr>"); }
                         */
                        if ( !dataElement.getDomainType().equals( oldDataElement.getDomainType() ) )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Domain Type</td>" + "<td>"
                                + oldDataElement.getDomainType().name() + "</td>" + "<td>"
                                + dataElement.getDomainType().name() + "</td>" + "</tr>" );
                        }
                        /*
                         * if(!dataElement.getCode().equals(oldDataElement.getCode
                         * ())) { changeLog = changeLog.concat("<tr>"+
                         * "<td>Code</td>"
                         * +"<td>"+oldDataElement.getCode()+"</td>"
                         * +"<td>"+dataElement.getCode()+"</td>"+ "</tr>"); }
                         */
                        dataElementUpdates.add( changeLog );

                    }
                }
            }

            if ( indicatorList != null )
            {
                List<Indicator> indicators = indicatorList.getIndicators();

                for ( Indicator indicator : indicators )
                {
                    if ( indicatorService.getIndicator( indicator.getUid() ) == null )
                    {
                        newIndicators.add( indicator );

                        String details = new String( "" );

                        details = details.concat( "<tr>" + "<td>Name:</td>" + "<td>" + indicator.getName()
                            + "</td></tr>" );

                        details = details.concat( "<tr>" + "<td>Type:</td>" + "<td>"
                            + indicator.getIndicatorType().getName() + "</td></tr>" );

                        details = details.concat( "<tr>" + "<td>Description:</td>" + "<td>"
                            + indicator.getDescription() + "</td></tr>" );

                        details = details.concat( "<tr>" + "<td>Numerator:</td>" + "<td>"
                            + indicator.getNumeratorDescription() + "</td></tr>" );

                        details = details.concat( "<tr>" + "<td>Denominator:</td>" + "<td>"
                            + indicator.getDenominatorDescription() + "</td></tr>" );

                        newIndicatorsDetails.add( details );

                        System.out.println( "Indicator Name: " + indicator.getName() );
                    }
                    else
                    {
                        updatedIndicators.add( indicator );

                        Indicator oldIndicator = indicatorService.getIndicator( indicator.getUid() );

                        String changeLog = new String( "" );

                        if ( indicator.getName() != oldIndicator.getName() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Name</td>" + "<td>" + oldIndicator.getName()
                                + "</td>" + "<td>" + indicator.getName() + "</td>" + "</tr>" );
                        }
                        if ( indicator.getShortName() != oldIndicator.getShortName() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Short Name</td>" + "<td>"
                                + oldIndicator.getShortName() + "</td>" + "<td>" + indicator.getShortName() + "</td>"
                                + "</tr>" );
                        }
                        if ( indicator.getDescription() != oldIndicator.getDescription() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Description</td>" + "<td>"
                                + oldIndicator.getDescription() + "</td>" + "<td>" + indicator.getDescription()
                                + "</td>" + "</tr>" );
                        }

                        indicatorUpdates.add( changeLog );
                    }
                }
            }

            if ( orgUnitList != null )
            {
                List<OrganisationUnit> organisationUnits = orgUnitList.getOrganisationUnits();

                for ( OrganisationUnit organisationUnit : organisationUnits )
                {
                    if ( organisationUnitService.getOrganisationUnit( organisationUnit.getUid() ) == null )
                    {
                        newOrgUnits.add( organisationUnit );

                        System.out.println( "OU Name: " + organisationUnit.getName() );
                    }
                    else
                    {
                        updatedOrgUnits.add( organisationUnit );

                        OrganisationUnit oldOrgUnit = organisationUnitService.getOrganisationUnit( organisationUnit
                            .getUid() );

                        String changeLog = new String( "" );

                        if ( organisationUnit.getName() != oldOrgUnit.getName() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Name</td>" + "<td>" + oldOrgUnit.getName()
                                + "</td>" + "<td>" + organisationUnit.getName() + "</td>" + "</tr>" );
                        }
                        if ( organisationUnit.getShortName() != oldOrgUnit.getShortName() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Short Name</td>" + "<td>"
                                + oldOrgUnit.getShortName() + "</td>" + "<td>" + organisationUnit.getShortName()
                                + "</td>" + "</tr>" );
                        }
                        if ( organisationUnit.getCode() != oldOrgUnit.getCode() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Code</td>" + "<td>" + oldOrgUnit.getCode()
                                + "</td>" + "<td>" + organisationUnit.getCode() + "</td>" + "</tr>" );
                        }

                        orgUnitUpdates.add( changeLog );

                    }
                }
            }

            if ( validationRuleList != null )
            {
                List<ValidationRule> validationRules = validationRuleList.getValidationRules();

                for ( ValidationRule validationRule : validationRules )
                {
                    if ( validationRuleService.getValidationRule( validationRule.getUid() ) == null )
                    {
                        newValidationRules.add( validationRule );

                        System.out.println( "VR Name: " + validationRule.getName() );
                    }
                    else
                    {
                        updatedValidationRules.add( validationRule );

                        ValidationRule oldRule = validationRuleService.getValidationRule( validationRule.getUid() );

                        String changeLog = new String( "" );

                        if ( validationRule.getName() != oldRule.getName() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Name</td>" + "<td>" + oldRule.getName()
                                + "</td>" + "<td>" + validationRule.getName() + "</td>" + "</tr>" );
                        }
                        if ( validationRule.getRuleType() != oldRule.getRuleType() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Rule Type</td>" + "<td>"
                                + oldRule.getRuleType() + "</td>" + "<td>" + validationRule.getRuleType() + "</td>"
                                + "</tr>" );
                        }
                        if ( validationRule.getCode() != oldRule.getCode() )
                        {
                            changeLog = changeLog.concat( "<tr>" + "<td>Code</td>" + "<td>" + oldRule.getCode()
                                + "</td>" + "<td>" + validationRule.getCode() + "</td>" + "</tr>" );
                        }

                        validationRuleUpdates.add( changeLog );

                    }
                }
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return SUCCESS;
    }
}
