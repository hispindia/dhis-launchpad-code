package org.hisp.dhis.alert.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 3/12/13
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetDataValuesFormAction implements Action {

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
