package org.hisp.dhis.reports.viewdata.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import java.util.Collection;

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

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
	  private  Collection<DataSet> datasets;
	  

	    public Collection<DataSet> getDatasets() {
			return datasets;
		}

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


	public String execute()
        throws Exception
    {
       datasets= dataSetService.getAssignedDataSets();

       return SUCCESS;
    }

}

