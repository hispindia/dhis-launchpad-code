package org.hisp.dhis.alert.viewdata.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version GenerateViewDataReportFormAction.javaFeb 15, 2013 11:07:21 AM
 */

public class GenerateViewDataReportFormAction
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

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private List<DataSet> datasets;

    public List<DataSet> getDatasets()
    {
        return datasets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        datasets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        Collections.sort( datasets, new IdentifiableObjectNameComparator() );
        
        selectionTreeManager.setSelectedOrganisationUnit( organisationUnitService.getOrganisationUnit( 1 ) );

        return SUCCESS;
    }

}
