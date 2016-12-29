package org.hisp.dhis.coldchain.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultInventoryType_AttributeService.java Jun 14, 2012 3:19:02 PM	
 */

public class DefaultInventoryType_AttributeService implements InventoryType_AttributeService
{
    
    public static final String ICEPACKSINVENTORYTYPE = "IcePacksInventoryType";//283.0
    private final String ICEPACKSINVENTORYTYPEATTRIBUTE = "IcePacksInventoryTypeAttribute";
	
    public static final String COLDBOXINVENTORYTYPE = "ColdBoxInventoryType";//280.0
    private final String COLDBOXINVENTORYTYPEATTRIBUTE = "ColdBoxInventoryTypeAttribute";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private InventoryType_AttributeStore inventoryType_AttributeStore;

    public void setInventoryType_AttributeStore( InventoryType_AttributeStore inventoryType_AttributeStore )
    {
        this.inventoryType_AttributeStore = inventoryType_AttributeStore;
    }
    
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
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public void addInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.addInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public void deleteInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.deleteInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public void updateInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.updateInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributes()
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributes();
    }
    
    @Transactional
    @Override
    public InventoryType_Attribute getInventoryTypeAttribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return inventoryType_AttributeStore.getInventoryTypeAttribute( inventoryType, inventoryTypeAttribute );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributesByInventoryType( InventoryType inventoryType )
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributesByInventoryType( inventoryType );
    }
    
    @Transactional
    @Override
    public Collection<InventoryTypeAttribute> getListInventoryTypeAttribute( InventoryType inventoryType )
    {
        return inventoryType_AttributeStore.getListInventoryTypeAttribute( inventoryType );
    }

    @Transactional
    @Override
    public InventoryType_Attribute getInventoryTypeAttributeForDisplay( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display)
    {
        return inventoryType_AttributeStore.getInventoryTypeAttributeForDisplay( inventoryType, inventoryTypeAttribute, display );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributeForDisplay( InventoryType inventoryType, boolean display )
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributeForDisplay( inventoryType, display );
    }
    
    
    public Map<String, String> getOrgUnitAttributeDataValue( String orgUnitIdsByComma, String orgUnitAttribIdsByComma )
    {
        Map<String, String> orgUnitAttributeDataValueMap = new HashMap<String, String>();
        try
        {
            String query = "SELECT organisationunitattributevalues.organisationunitid, attributevalue.attributeid, value FROM attributevalue "+
                                " INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid "+
                                " WHERE attributeid IN ("+orgUnitAttribIdsByComma+") AND " +
                                    " organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")";
                        
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitID = rs.getInt( 1 );
                Integer attribId = rs.getInt( 2 );
                String value = rs.getString( 3 );

                orgUnitAttributeDataValueMap.put( orgUnitID+":"+attribId, value );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return orgUnitAttributeDataValueMap;
    }
    
    
   
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( String orgUnitIdsByComma, Attribute attribute, String searchText )
    {
        //String sql = searchPatientSql( false, searchKeys, orgunit, min, max );
        
        /*
        System.out.println( "--- orgUnitIdsByComma" + orgUnitIdsByComma  );
        
        System.out.println( "--- attribute" + attribute.getName() );
        
        System.out.println( "--- searchText" + searchText );
        */
        
        String sql = "SELECT distinct organisationunitattributevalues.organisationunitid as organisationunitid, attributevalue.attributeid, value FROM attributevalue " +
        
                     "INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid " +
           
                     "WHERE attributeid = " + attribute.getId() + 
           
                     " AND organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")" +  
                     
                     " AND value like '%" + searchText + "%' " ;

        //System.out.println( "---" + sql );
        
        Collection<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        try
        {
            organisationUnits = jdbcTemplate.query( sql, new RowMapper<OrganisationUnit>()
            {
                public OrganisationUnit mapRow( ResultSet rs, int rowNum ) throws SQLException
                {
                    //System.out.println( "--- " + rs.getString( "organisationunitid" ) );
                    //return organisationUnitService.getOrganisationUnit( rs.getString( "organisationunitid" ) );
                    return organisationUnitService.getOrganisationUnit( rs.getInt( "organisationunitid" ) );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        /*
        System.out.println( "--- Service " + organisationUnits.size() );
        
        for( OrganisationUnit orgUnit : organisationUnits )
        {
            System.out.println( "--- " + orgUnit.getId() + "----" + orgUnit.getName() );
        }
        */
        return organisationUnits;
    }

    
    public Map<Integer, String> getEquipmentCountByOrgUnitList( String orgUnitIdsByComma )
    {
        Map<Integer, String> inventoryTypeCountMap = new HashMap<Integer, String>();
        
        //for IcePacksInventoryType
        Constant icePackInventoryTypeConstant = constantService.getConstantByName( ICEPACKSINVENTORYTYPE );        
        Integer icePackInventoryTypeId =  (int) icePackInventoryTypeConstant.getValue() ;
        OptionSet optionSet = optionService.getOptionSetByName( ICEPACKSINVENTORYTYPEATTRIBUTE );        
        String inventoryTypeAttributeIdsByComma = "-1";        
        for( String option : optionSet.getOptions() )
        {
            inventoryTypeAttributeIdsByComma += "," + option;
        }                
        String icePackValue = getSumInventoryTypeAttributeValue( orgUnitIdsByComma, icePackInventoryTypeId, inventoryTypeAttributeIdsByComma );
        
        //for ColdBoxes
        Constant coldBoxInventoryTypeConstant = constantService.getConstantByName( COLDBOXINVENTORYTYPE );        
        Integer coldBoxInventoryTypeId =  (int) coldBoxInventoryTypeConstant.getValue() ;
        optionSet = optionService.getOptionSetByName( COLDBOXINVENTORYTYPEATTRIBUTE );        
        inventoryTypeAttributeIdsByComma = "-1";        
        for( String option : optionSet.getOptions() )
        {
            inventoryTypeAttributeIdsByComma += "," + option;
        }                
        String coldBoxValue = getSumInventoryTypeAttributeValue( orgUnitIdsByComma, coldBoxInventoryTypeId, inventoryTypeAttributeIdsByComma );
        
        try
        {
            String query = "SELECT equipmentinstance.inventorytypeid,count(*) as total from equipmentinstance "+
                           " WHERE organisationunitid IN (" + orgUnitIdsByComma + " ) " +
                           " group by equipmentinstance.inventorytypeid ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
                Integer inventoryTypeID = rs.getInt( 1 );
                String equipmentCount = rs.getString( 2 );
                                
                if( inventoryTypeID.intValue() == icePackInventoryTypeId.intValue() )
                {
                    if( icePackValue != null )
                    {                        
                        inventoryTypeCountMap.put( icePackInventoryTypeId, icePackValue );
                    }
                }                
				else if( inventoryTypeID.intValue() == coldBoxInventoryTypeId.intValue() )
				{
					if( coldBoxValue != null )
                    {                        
                        inventoryTypeCountMap.put( coldBoxInventoryTypeId, coldBoxValue );
                    }
				}
                else
                {
                    inventoryTypeCountMap.put( inventoryTypeID, equipmentCount );
                }
                //System.out.println( "--- " + inventoryTypeID + "----" + equipmentCount );
                
                //inventoryTypeCountMap.put( inventoryTypeID, equipmentCount );
            }
        }    
            
       catch( Exception e )
       {
           throw new RuntimeException( "Exception: ", e );
       }
            
       
       //System.out.println( "--- Map Size " + inventoryTypeCountMap.size() );
       return inventoryTypeCountMap;
    }
    
    
    
    public String getSumInventoryTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId, String inventoryTypeAttributeIdsByComma )
    {
        String value = "";
        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        
        try
        {
            String query ="";
            if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
            {
                query = "SELECT SUM(VALUE) FROM equipment e INNER JOIN equipmentinstance ei ON e.equipmentinstanceid = ei.equipmentinstanceid "+
                        " WHERE e.inventorytypeattributeid IN (" + inventoryTypeAttributeIdsByComma + " ) " +
                        " and ei.organisationunitid IN (" + orgUnitIdsByComma + " ) " +
                        " and ei.inventorytypeid   = " + inventoryTypeId + " ";
            }

            else if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
            {
                query = "SELECT SUM( cast( VALUE as numeric ) )  FROM equipment e INNER JOIN equipmentinstance ei ON e.equipmentinstanceid = ei.equipmentinstanceid "+
                    " WHERE e.inventorytypeattributeid IN (" + inventoryTypeAttributeIdsByComma + " ) " +
                    " and ei.organisationunitid IN (" + orgUnitIdsByComma + " ) " +
                    " and ei.inventorytypeid   = " + inventoryTypeId + " ";
            }
            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            if ( rs.next() )
            {
                value = rs.getString( 1 );
            }
        }    
            
       catch( Exception e )
       {
           throw new RuntimeException( "Exception: ", e );
       }
       
       //System.out.println( "--- In side Method Ice Packs Value is  " + value ); 
       return value;
    }
    
}
