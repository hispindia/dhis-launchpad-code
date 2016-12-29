package org.hisp.dhis.alert.viewdata.action;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

public class GetDataSetAction
implements Action
{
   // -------------------------------------------------------------------------
   // Dependencies
   // -------------------------------------------------------------------------

   private DataSetService dataSetService;

   public void setDataSetService( DataSetService dataSetService )
   {
       this.dataSetService = dataSetService;
   }
   
   private DataValueService dataValueService;

   public void setDataValueService( DataValueService dataValueService )
   {
       this.dataValueService = dataValueService;
   }

   private SelectionTreeManager selectionTreeManager;

   public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
   {
       this.selectionTreeManager = selectionTreeManager;
   }
   
   private StatementManager statementManager;

   public void setStatementManager( StatementManager statementManager )
   {
       this.statementManager = statementManager;
   }
   
   // -------------------------------------------------------------------------
   // Input & output
   // -------------------------------------------------------------------------

   private Integer dataSetId;

   public Integer getDataSetId()
   {
       return dataSetId;
   }

   public void setDataSetId( Integer dataSetId )
   {
       this.dataSetId = dataSetId;
   }

   private DataSet dataSet;

   public DataSet getDataSet()
   {
       return dataSet;
   }
   
   private List<DataSet> datasets;

   public List<DataSet> getDatasets()
   {
       return datasets;
   }

   private List<DataElement> dataSetDataElements = new ArrayList<DataElement>();

   public List<DataElement> getDataSetDataElements()
   {
       return dataSetDataElements;
   }

   private List<Indicator> dataSetIndicators = new ArrayList<Indicator>();

   public List<Indicator> getDataSetIndicators()
   {
       return dataSetIndicators;
   }

   private DataEntryForm dataEntryForm;

   public DataEntryForm getDataEntryForm()
   {
       return dataEntryForm;
   }

   private DataElementCategoryCombo categoryCombo;

   public DataElementCategoryCombo getCategoryCombo()
   {
       return categoryCombo;
   }
   
    private String startDate;
      
    public String getStartDate()
    {
        return startDate;
    }
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }
    
    private String endDate;
       
    public String getEndDate()
    {
        return endDate;
    }
    
    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

   // -------------------------------------------------------------------------
   // Action
   // -------------------------------------------------------------------------   

    public String execute()
       throws Exception
   {
       dataSet = dataSetService.getDataSet( dataSetId );
       
       datasets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

       dataSetDataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

       Collections.sort( dataSetDataElements, IdentifiableObjectNameComparator.INSTANCE );

       dataSetIndicators = new ArrayList<Indicator>( dataSet.getIndicators() );

       Collections.sort( dataSetIndicators, IdentifiableObjectNameComparator.INSTANCE );

       dataEntryForm = dataSet.getDataEntryForm();
       
       selectionTreeManager.setSelectedOrganisationUnits( dataSet.getSources() );

      /* Set<OrganisationUnit> ougUnits = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits());
       
       Collection<DataValue> dataValues = new ArrayList<DataValue>();
       
       for(OrganisationUnit org : ougUnits)
       {
           if(!dataValues.contains( dataValueService.getDataValues( org ))){
               dataValues.addAll( dataValueService.getDataValues( org ) );
           }
       }
              
       List<Period> pers = new ArrayList<Period>();
       
       for(DataValue dv : dataValues)
       {
           if(!pers.contains( dv.getPeriod())){
               pers.add( dv.getPeriod() );
           }
       }
       
       Collections.sort( pers, new PeriodComparator() );*/
       
       if ( dataSetId != null )
       {
           final StatementHolder holder = statementManager.getHolder();
           
           try
           {
               String sql = "SELECT MIN(p.startdate), MAX(p.startdate) FROM datavalue dv "
                   + "INNER JOIN period p ON dv.periodid = p.periodid "
                   + "INNER JOIN datasetmembers dsm "
                   + "ON dsm.dataelementid = dv.dataelementid WHERE dsm.datasetid = " 
                   +  dataSetId;

               final ResultSet resultSet = holder.getStatement().executeQuery( sql );
               
               while ( resultSet.next() )
               {                   
                   startDate = resultSet.getString( 1 );
                   endDate = resultSet.getString( 2 );                                   
               }
               
           }
           catch ( SQLException ex )
           {
               throw new RuntimeException( "Failed to get DataValues", ex );
           }
           finally
           {
               holder.close();
           }
       }
       
       //startDate = pers.get( 0 ).getStartDate().toString();
       
       //endDate = pers.get( pers.size()-1 ).getEndDate().toString();
              
       return SUCCESS;
   }
}

