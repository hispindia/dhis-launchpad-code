package org.hisp.dhis.dataaccess.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 15/11/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchByDataelements implements Action {


    private DataElementService dataElementService;

    public DataElementService getDataElementService() {
        return dataElementService;
    }

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    public Collection<DataElement> allDataElements = new ArrayList<DataElement>();

    public Collection<DataElement> getAllDataElements() {
        return allDataElements;
    }

    public void setAllDataElements(Collection<DataElement> allDataElements) {
        this.allDataElements = allDataElements;
    }

    @Override
    public String execute() throws Exception {

        allDataElements = dataElementService.getAllDataElements();
        System.out.println("Size :"+allDataElements.size());
        return SUCCESS;

    }
}
