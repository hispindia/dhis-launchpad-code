package org.hisp.dhis.dqa.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dqa.api.DQAParameter;
import org.hisp.dhis.dqa.api.DQAParameterService;

import com.opensymphony.xwork2.Action;

public class SaveDQAParametersAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DQAParameterService dqaParameterService;
    
    public void setDqaParameterService( DQAParameterService dqaParameterService )
    {
        this.dqaParameterService = dqaParameterService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {

        List<DQAParameter> dqaParameterList = new ArrayList<DQAParameter>( dqaParameterService.getAllDQAParameters() );
        
        System.out.println( " Size of Parameter List is  : " + dqaParameterList.size()  );
        /*
        for( DQAParameter dqaParameter : dqaParameterList )
        {
            System.out.println( " DQA Parameter Name is : " + dqaParameter.getName() + " DQA Parameter Discription is is : " + dqaParameter.getDescription() );
        }
        */
        HttpServletRequest request = ServletActionContext.getRequest();

        //DQAParameter dqaParameterDetails = null;
        
        if ( dqaParameterList != null && dqaParameterList.size() != 0 )
        {
            for( DQAParameter dqaParameter : dqaParameterList )
            {
                String value = request.getParameter( dqaParameter.getName() );
                   if (value!=null){
                	dqaParameter.setValue( value.trim() );
                    dqaParameterService.updateDQAParameter( dqaParameter );
                   }
               
            }
        }

        return SUCCESS;
    }
}
