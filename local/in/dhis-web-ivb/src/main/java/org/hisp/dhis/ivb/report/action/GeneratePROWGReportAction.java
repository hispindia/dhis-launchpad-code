package org.hisp.dhis.ivb.report.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.favorite.Favorite;
import org.hisp.dhis.favorite.FavoriteService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.ivb.util.IVBUtil;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class GeneratePROWGReportAction implements Action
{
    private static final String TABULAR_REPORT_DATAELEMENTGROUP_ID = "TABULAR_REPORT_DATAELEMENTGROUP_ID";

    private static final String VACCINE_ATTRIBUTE = "VACCINE_ATTRIBUTE";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private IVBUtil ivbUtil;

    public void setIvbUtil( IVBUtil ivbUtil )
    {
        this.ivbUtil = ivbUtil;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private FavoriteService favoriteService;

    public void setFavoriteService( FavoriteService favoriteService )
    {
        this.favoriteService = favoriteService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Autowired 
    private LookupService lookupService;
    
    @Autowired
    private SectionService dataSetSectionService;
    
    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

 private String isoCode;
    
    private String whoRegion;
    
    private String unicefRegion;
    
    private String incomeLevel;
    
    private String gaviEligibleStatus;
  
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getWhoRegion() {
		return whoRegion;
	}

	public void setWhoRegion(String whoRegion) {
		this.whoRegion = whoRegion;
	}

	public String getUnicefRegion() {
		return unicefRegion;
	}

	public void setUnicefRegion(String unicefRegion) 
	{
		this.unicefRegion = unicefRegion;
	}

	
	public String getIncomeLevel() {
		return incomeLevel;
	}

	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}

	public String getGaviEligibleStatus() 
	{
		return gaviEligibleStatus;
	}

	public void setGaviEligibleStatus(String gaviEligibleStatus) 
	{
		this.gaviEligibleStatus = gaviEligibleStatus;
	}
	
    private String userSource;

    public String getUserSource()
    {
        return userSource;
    }

    public void setUserSource( String userSource )
    {
        this.userSource = userSource;
    }

    private String language;

    private String userName;

    public String getLanguage()
    {
        return language;
    }

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

    private Map<String, DataValue> dataValueMap = new HashMap<String, DataValue>();

    public Map<String, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private List<Integer> selectedDataElementsValidator = new ArrayList<Integer>();

    public void setSelectedDataElementsValidator( List<Integer> selectedDataElementsValidator )
    {
        this.selectedDataElementsValidator = selectedDataElementsValidator;
    }

    public List<Integer> getSelectedDataElementsValidator()
    {
        return selectedDataElementsValidator;
    }

    private List<Boolean> values = new ArrayList<Boolean>();

    public void setValues( List<Boolean> values )
    {
        this.values = values;
    }

    public List<Boolean> getValues()
    {
        return values;
    }

    private List<Boolean> comments = new ArrayList<Boolean>();

    public void setComments( List<Boolean> comments )
    {
        this.comments = comments;
    }

    public List<Boolean> getComments()
    {
        return comments;
    }

    private List<Integer> headerDataElements = new ArrayList<Integer>();

    public List<Integer> getHeaderDataElements()
    {
        return headerDataElements;
    }

    private List<String> orgUnitIds = new ArrayList<String>();

    public void setOrgUnitIds( List<String> orgUnitIds )
    {
        this.orgUnitIds = orgUnitIds;
    }

    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Map<String, DataValue> headerDataValueMap = new HashMap<String, DataValue>();

    public Map<String, DataValue> getHeaderDataValueMap()
    {
        return headerDataValueMap;
    }

    private List<String> dataElementName = new ArrayList<String>();

    public List<String> getDataElementName()
    {
        return dataElementName;
    }

    private List<String> headerDataElementName = new ArrayList<String>();

    public List<String> getHeaderDataElementName()
    {
        return headerDataElementName;
    }

    private List<Integer> valueDeList = new ArrayList<Integer>();

    public List<Integer> getValueDeList()
    {
        return valueDeList;
    }

    private List<Integer> commentDeList = new ArrayList<Integer>();

    public List<Integer> getCommentDeList()
    {
        return commentDeList;
    }

    private Map<Integer, String> valueDeMap = new HashMap<Integer, String>();

    public Map<Integer, String> getValueDeMap()
    {
        return valueDeMap;
    }

    private Map<Integer, String> commentDeMap = new HashMap<Integer, String>();

    public Map<Integer, String> getCommentDeMap()
    {
        return commentDeMap;
    }

    private Map<String,List<DataElement>> dataElementMap = new HashMap<String, List<DataElement>>();
    
    public Map<String, List<DataElement>> getDataElementMap()
    {
        return dataElementMap;
    }

    private String favoriteType;

    public String getFavoriteType()
    {
        return favoriteType;
    }

    private String favoriteId;

    public void setFavoriteId( String favoriteId )
    {
        this.favoriteId = favoriteId;
    }

    private Favorite favorite;

    public Favorite getFavorite()
    {
        return favorite;
    }

    private List<String> favoriteList = new ArrayList<String>();

    public List<String> getFavoriteList()
    {
        return favoriteList;
    }

    private String introStartDate;

    private String introEndDate;

    public String getIntroStartDate()
    {
        return introStartDate;
    }

    public String getIntroEndDate()
    {
        return introEndDate;
    }

    public void setIntroStartDate( String introStartDate )
    {
        this.introStartDate = introStartDate;
    }

    public void setIntroEndDate( String introEndDate )
    {
        this.introEndDate = introEndDate;
    }

    private Map<String, Set<OrganisationUnit>> vaccineOrgunitMap = new HashMap<String, Set<OrganisationUnit>>();
    
    public Map<String, Set<OrganisationUnit>> getVaccineOrgunitMap()
    {
        return vaccineOrgunitMap;
    }
    
    private Map<String, List<OrganisationUnit>> vaccineOrgunitList = new HashMap<String, List<OrganisationUnit>>();
    
    public Map<String, List<OrganisationUnit>> getVaccineOrgunitList()
    {
        return vaccineOrgunitList;
    }
    
    private OrganisationUnitGroupSet unicefRegionsGroupSet;
    
    public OrganisationUnitGroupSet getUnicefRegionsGroupSet()
    {
        return unicefRegionsGroupSet;
    }
    
    private Map<String, Section> sectionMap = new HashMap<String, Section>();
    
    public Map<String, Section> getSectionMap()
    {
        return sectionMap;
    }
    
    Set<Integer> percentageRequiredDe = new HashSet<Integer>();
	
	public Set<Integer> getPercentageRequiredDe() {
		return percentageRequiredDe;
	}
    // --------------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------------
    public String execute()
    {
    	if( isoCode != null )
        {
        	isoCode = "ON";
        }
        if( whoRegion != null )
        {
        	whoRegion = "ON";
        }
        if( unicefRegion != null )
        {
        	unicefRegion = "ON";
        }
        if( incomeLevel != null )
        {
        	incomeLevel = "ON";
        }
        if( gaviEligibleStatus != null )
        {
        	gaviEligibleStatus = "ON";
        }
        
        Set<Section> dataSetSections = new HashSet<Section>( dataSetSectionService.getAllSections() );
        
        Lookup lookup1 = lookupService.getLookupByName(Lookup.IS_PERCENTAGE);
		int percentage_attribute_id = Integer.parseInt(lookup1.getValue());
        
        Constant vaccineAttributeConstant = constantService.getConstantByName( VACCINE_ATTRIBUTE );
        
        List<Favorite> favorites = favoriteService.getAllFavoriteByFavoriteType( IVBUtil.TABULAR_REPORT );
        for ( Favorite f : favorites )
        {
            favoriteList.add( "\"" + f.getName() + "\"" );
        }
        
        favoriteType = IVBUtil.TABULAR_REPORT;
        if ( favoriteId != null && !favoriteId.equalsIgnoreCase( "-1" ) )
        {
            String[] favoriteAccess = favoriteId.split( ":" );
            if ( favoriteAccess[1].equalsIgnoreCase( "Can View and Edit" ) )
            {
                favorite = favoriteService.getFavorite( Integer.parseInt( favoriteAccess[0] ) );
            }
        }

        ActionContext.getContext().getSession().put("favoriteId", favoriteId );
        Constant tabularDataElementGroupId = constantService.getConstantByName( TABULAR_REPORT_DATAELEMENTGROUP_ID );
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

        Lookup lookup = lookupService.getLookupByName( "UNICEF_REGIONS_GROUPSET" );
        
        unicefRegionsGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( Integer.parseInt( lookup.getValue() ) );
        
        if ( orgUnitIds.size() > 1 )
        {
            for ( String id : orgUnitIds )
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( id ) );
                if ( organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() ) == 3 )
                {
                    orgUnitList.add( orgUnit );
                }
            }
        }
        else if ( selectionTreeManager.getReloadedSelectedOrganisationUnits() != null )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
            List<OrganisationUnit> lastLevelOrgUnit = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> userOrgUnits = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
            for ( OrganisationUnit orgUnit : userOrgUnits )
            {
                if ( organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() ) == 3 )
                {
                    lastLevelOrgUnit.add( orgUnit );
                }
                else
                {
                    lastLevelOrgUnit.addAll( organisationUnitService.getOrganisationUnitsAtLevel( 3, orgUnit ) );
                }
            }
            orgUnitList.retainAll( lastLevelOrgUnit );
        }
        
        
        // Filtering countries by vaccine introduction date
        Date sDate = getStartDateByString( introStartDate );
        Date eDate = getEndDateByString( introEndDate );
        
        Constant introYearGroupConstant = constantService.getConstantByName( IVBUtil.PROWG_INTRO_YEAR_DE_GROUP );
        DataElementGroup introYearDEGroup = dataElementService.getDataElementGroup( (int) introYearGroupConstant.getValue() );
        List<DataElement> introYearDEs = new ArrayList<DataElement>( introYearDEGroup.getMembers() );
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        Map<String, Set<OrganisationUnit>> vaccine_IntroDate_OrgunitMap = new TreeMap<String, Set<OrganisationUnit>>();
        Iterator<OrganisationUnit> orgUnitIterator = orgUnitList.iterator();        
        while( orgUnitIterator.hasNext() )
        {
            OrganisationUnit orgUnit = orgUnitIterator.next();
            
            int flag = 0;
            for( DataElement dataElement : introYearDEs )
            {
                DataValue dv = dataValueService.getLatestDataValue( dataElement, optionCombo, orgUnit );
                if ( dv != null && dv.getValue() != null )
                {
                    String value = dv.getValue();
                    Date valueDate = getStartDateByString( value );
                    if( valueDate!= null && sDate.getTime() <= valueDate.getTime() && valueDate.getTime() <= eDate.getTime())
                    {
                        if ( valueDate.equals( sDate ) || valueDate.equals( eDate ) || (valueDate.after( sDate ) && valueDate.before( eDate )) )
                        {
                            //flag = 1;
                            //break;
                            
                            Set<AttributeValue> dataElementAttributeValues = dataElement.getAttributeValues();
                            if ( dataElementAttributeValues != null && dataElementAttributeValues.size() > 0 )
                            {
                                for ( AttributeValue deAttributeValue : dataElementAttributeValues )
                                {
                                    if ( deAttributeValue.getAttribute().getId() == vaccineAttributeConstant.getValue() && deAttributeValue.getValue() != null )
                                    {
                                        /*
                                        if( vaccineOrgunitMap.get( deAttributeValue.getValue() ) == null || vaccineOrgunitMap.get( deAttributeValue.getValue() ).size() <= 0 || !vaccineOrgunitMap.containsKey( deAttributeValue.getValue() ) )
                                        {
                                            Set<OrganisationUnit> ouList = new HashSet<OrganisationUnit>();
                                            ouList.add( orgUnit );
                                            vaccineOrgunitMap.put( deAttributeValue.getValue(), ouList );
                                        }
                                        else
                                        {
                                            Set<OrganisationUnit> ouList = vaccineOrgunitMap.get( deAttributeValue.getValue() );
                                            ouList.add( orgUnit );
                                            vaccineOrgunitMap.put( deAttributeValue.getValue(), ouList );
                                        }
                                        */
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Set<OrganisationUnit> ouSet = vaccine_IntroDate_OrgunitMap.get( deAttributeValue.getValue() + ":" + simpleDateFormat.format( valueDate ) );
                                        if( ouSet == null )
                                        {
                                            ouSet = new TreeSet<OrganisationUnit>();
                                        }
                                        ouSet.add( orgUnit );
                                        vaccine_IntroDate_OrgunitMap.put( deAttributeValue.getValue() + ":" + simpleDateFormat.format( valueDate ), ouSet );
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            /*
            if( flag == 0 )
            {
                orgUnitIterator.remove();
            }
            */
        }
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );

        String orgUnitIdsByComma = "-1";
        if ( orgUnitList.size() > 0 )
        {
            orgUnitIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        
        List<String> vaccineIntroDates = new ArrayList<String>( vaccine_IntroDate_OrgunitMap.keySet() );
        Collections.sort( vaccineIntroDates );
        for( String vaccineIntroDate : vaccineIntroDates )
        {
            List<OrganisationUnit> ouList = vaccineOrgunitList.get( vaccineIntroDate.split( ":" )[0] );
            if( ouList == null )
            {
                ouList = new ArrayList<OrganisationUnit>();
            }
            ouList.addAll( vaccine_IntroDate_OrgunitMap.get( vaccineIntroDate ) );
            
            vaccineOrgunitList.put( vaccineIntroDate.split( ":" )[0], ouList );
        }
        
        List<DataElement> dataEList = new ArrayList<DataElement>();

        Set<DataElement> dataElementSet = new HashSet<DataElement>();
        for ( int i = 0; i < this.selectedDataElementsValidator.size(); i++ )
        {
            DataElement dataElement = dataElementService.getDataElement( selectedDataElementsValidator.get( i ) );
            
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( dataElement.getAttributeValues() );
        	
            System.out.println("Dataelement name :" + dataElement.getName());
            
            for ( AttributeValue attValue : attrValueSet )
			{
				if ( attValue.getAttribute().getId() == percentage_attribute_id && attValue.getValue().equalsIgnoreCase( "true" ))
						percentageRequiredDe.add(dataElement.getId());
			}
                        
            dataElementSet.add( dataElement );
            if ( values.get( i ) == true )
            {
                dataElementName.add( dataElement.getName() );
                valueDeList.add( dataElement.getId() );
                valueDeMap.put( dataElement.getId(), "true" );
            }
            if ( comments.get( i ) == true )
            {
                dataElementName.add( "Comment of " + dataElement.getName() );
                commentDeList.add( dataElement.getId() );
                commentDeMap.put( dataElement.getId(), "true" );
            } 
            
            Set<AttributeValue> dataElementAttributeValues = dataElement.getAttributeValues();
            if ( dataElementAttributeValues != null && dataElementAttributeValues.size() > 0 )
            {
                for ( AttributeValue deAttributeValue : dataElementAttributeValues )
                {
                    if ( deAttributeValue.getAttribute().getId() == vaccineAttributeConstant.getValue()
                        && deAttributeValue.getValue() != null )
                    {
                        if(!dataElementMap.containsKey( deAttributeValue.getValue() ))
                        {
                            dataEList = new ArrayList<DataElement>();
                            dataEList.add( dataElement );
                        }
                        else
                        {
                            dataEList.add( dataElement ); 
                        }
                        dataElementMap.put( deAttributeValue.getValue(), dataEList );
                        
                        for( Section section : dataSetSections )
                        {
                            if( section.getName().trim().equalsIgnoreCase( deAttributeValue.getValue() ) )
                            {
                                sectionMap.put( section.getName(), section );
                            }
                        }
                    }
                }
            }
        }
        
        for( String key : dataElementMap.keySet() )
        {
            System.out.println( "Key : " + key );
        }
        
        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByGroupId( (int) tabularDataElementGroupId.getValue() ) );
        for ( DataElement de : dataElements )
        {
            headerDataElementName.add( de.getName() );
        }

        headerDataElements = new ArrayList<Integer>( getIdentifiers( DataElement.class, dataElements ) );

        String headerDataElementIdsByComma = "-1";

        if ( headerDataElements.size() > 0 )
        {
            headerDataElementIdsByComma = getCommaDelimitedString( headerDataElements );
        }

        headerDataValueMap = ivbUtil.getLatestDataValuesForTabularReport( headerDataElementIdsByComma, orgUnitIdsByComma );

        String dataElementIdsByComma = "-1";

        if ( selectedDataElementsValidator.size() > 0 )
        {
            dataElementIdsByComma = getCommaDelimitedString( selectedDataElementsValidator );
        }
        
        ActionContext.getContext().getSession().put("tabulardataElementList", selectedDataElementsValidator );
        ActionContext.getContext().getSession().put("values", values );
        ActionContext.getContext().getSession().put("comments", comments );
                
        dataValueMap = ivbUtil.getLatestDataValuesForTabularReport( dataElementIdsByComma, orgUnitIdsByComma );
        
        /*
        for( OrganisationUnit ou : orgUnitList )
        {
            for( DataElement headerDe : dataElements )
            {
                DataValue dataValue = dataValueService.getLatestDataValue( headerDe, optionCombo, ou );
                if( dataValue != null )
                {
                    headerDataValueMap.put( ou.getId()+":"+headerDe.getId(), dataValue );
                }
            }
            
            for( DataElement de : dataElementSet )
            {
                DataValue dataValue = dataValueService.getLatestDataValue( de, optionCombo, ou );
                if( dataValue != null )
                {
                    dataValueMap.put( ou.getId()+":"+de.getId(), dataValue );
                }
            }
        }
        */
        
        return SUCCESS;

    }

    
    /**
     * Get Start Date from String date foramt (format could be YYYY / YYYY-Qn /
     * YYYY-MM )
     * 
     * @param dateStr
     * @return
     */
    private Date getStartDateByString( String dateStr )
    {
        String startDate = "";
        String[] startDateParts = dateStr.split( "-" );
        if ( startDateParts.length <= 1 )
        {
            startDate = startDateParts[0] + "-01-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q1" ) )
        {
            startDate = startDateParts[0] + "-01-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q2" ) )
        {
            startDate = startDateParts[0] + "-04-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q3" ) )
        {
            startDate = startDateParts[0] + "-07-01";
        }
        else if ( startDateParts[1].equalsIgnoreCase( "Q4" ) )
        {
            startDate = startDateParts[0] + "-10-01";
        }
        else
        {
            startDate = startDateParts[0] + "-" + startDateParts[1] + "-01";
        }

        Date sDate = format.parseDate( startDate );

        return sDate;
    }

    /**
     * Get End Date from String date foramt (format could be YYYY / YYYY-Qn /
     * YYYY-MM )
     * 
     * @param dateStr
     * @return
     */
    private Date getEndDateByString( String dateStr )
    {
        String endDate = "";
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String[] endDateParts = dateStr.split( "-" );
        if ( endDateParts.length <= 1 )
        {
            endDate = endDateParts[0] + "-12-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q1" ) )
        {
            endDate = endDateParts[0] + "-03-31";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q2" ) )
        {
            endDate = endDateParts[0] + "-06-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q3" ) )
        {
            endDate = endDateParts[0] + "-09-30";
        }
        else if ( endDateParts[1].equalsIgnoreCase( "Q4" ) )
        {
            endDate = endDateParts[0] + "-12-31";
        }
        else
        {
            if ( Integer.parseInt( endDateParts[0] ) % 400 == 0 )
            {
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-"
                    + (monthDays[Integer.parseInt( endDateParts[1] )] + 1);
            }
            else
            {
                endDate = endDateParts[0] + "-" + endDateParts[1] + "-"
                    + (monthDays[Integer.parseInt( endDateParts[1] )]);
            }
        }

        Date eDate = format.parseDate( endDate );

        return eDate;
    }

}
