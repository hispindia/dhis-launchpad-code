package org.hisp.dhis.reports.auto.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class GetPreGeneratedReportsPeriods implements Action
{
	private final String RAFOLDERNAME="ra_haryana";
	
    private final String REPORTSDIRECTORY="reports";
		
	private final String OUTPUTREPORTFOLDER_SC="CAPTURED_DATA_SC_AND_EQUIVALENT_FACILITIES_REPORTS";
	
	private final String OUTPUTREPORTFOLDER_PHC="CAPTURED_DATA_PHC_AND_EQUIVALENT_FACILILITIES_REPORTS";

	private final String OUTPUTREPORTFOLDER_CHC="CAPTURED_DATA_CHC_AND_EQUIVALENT_HOSPITALS_REPORTS";

	private final String OUTPUTREPORTFOLDER_SDH="CAPTURED_DATA_SDH_AND_EQUIVALENT_HOSPITALS_REPORTS";
	
	private final String OUTPUTREPORTFOLDER_DH="CAPTURED_DATA_DH_AND_EQUIVALENT_HOSPITALS_REPORTS";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService=reportService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    
    private String report;

    public void setReport( String report )
    {
        this.report=report;
    }
    
    public String getReport()
    {
    	return report;
    }
    
    private List<String> downloadPeriods = new ArrayList<String>();
	
 	public List<String> getDownloadPeriods() 
 	{
 		return downloadPeriods;
 	}

 	public void setDownloadPeriods( List<String> downloadPeriods ) 
 	{
 		this.downloadPeriods=downloadPeriods;
 	}
 	
 	private String directoryName=null;
 	
	private String SUBREPORTSDIRECTORY=null;

    private Report_in selReportObj=null;
        
    private List<Period> availablePeriods;
    
    private ArrayList<Period> monthlyPeriods;

    public ArrayList<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }
    
    private MonthlyPeriodType monthlyPeriodType;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        selReportObj=reportService.getReport( Integer.parseInt( report ) );
        
        if(selReportObj.getName().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities")){
        	SUBREPORTSDIRECTORY=OUTPUTREPORTFOLDER_SC;
        }
        
		if(selReportObj.getName().equalsIgnoreCase("Bulk Linelisting PHC and equivalent Facilities")){
			SUBREPORTSDIRECTORY=OUTPUTREPORTFOLDER_PHC;   	
		}
		
		if(selReportObj.getName().equalsIgnoreCase("Bulk Linelisting CHC and equivalent Facilities")){
			SUBREPORTSDIRECTORY=OUTPUTREPORTFOLDER_CHC;
		}
		
		if(selReportObj.getName().equalsIgnoreCase("BULK LineListing SDH and equivalent facilities Report")){
			SUBREPORTSDIRECTORY=OUTPUTREPORTFOLDER_SDH;
		}
		
		if(selReportObj.getName().equalsIgnoreCase("Bulk Linelisting DH/GH and equivalent Facilities")){
			SUBREPORTSDIRECTORY=OUTPUTREPORTFOLDER_DH;
		}

        directoryName=System.getenv( "DHIS2_HOME" ) 
        		+ File.separator + RAFOLDERNAME 
        		+ File.separator + REPORTSDIRECTORY 
        		+ File.separator + SUBREPORTSDIRECTORY;
               
        /* Monthly Periods */
        availablePeriods=new ArrayList<Period>();
        monthlyPeriodType=new MonthlyPeriodType();
        monthlyPeriods=new ArrayList<Period>(periodService.getPeriodsByPeriodType(monthlyPeriodType));
        
        for(int i=3;i>0;i--){
        	availablePeriods.add(monthlyPeriods.get( monthlyPeriods.size() - i ));
        }
                
        downloadPeriods=getListFilesAndFolder( directoryName );
        return SUCCESS;
    }
	
	public List<String> getListFilesAndFolder( String directoryName )
	{
		File directory=new File(directoryName);

		//get all the file names from a directory
		File[] fList=directory.listFiles();
		List<String> downloadPeriodList=new ArrayList<String>();
		for (File file:fList){
			downloadPeriodList.add(file.getName());
		}
		return downloadPeriodList;
	}    
}


    

