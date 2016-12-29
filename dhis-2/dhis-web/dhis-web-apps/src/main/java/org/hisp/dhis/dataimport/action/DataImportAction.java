package org.hisp.dhis.dataimport.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 *
 */
public class DataImportAction implements Action {

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();


    public List<String> getKeyCodeList() {
        return keyCodeList;
    }

    private List<String> keyCodeList = new ArrayList<String>();

    public Map<String, String> getUidMap() {
        return uidMap;
    }

    private Map<String, String> uidMap = new HashMap<String, String>();

    private String XMLcontent="";
    public String getXMLcontent() { return XMLcontent; }

    String path = System.getenv( "DHIS2_HOME" ) + File.separator + "excelimport" + File.separator;

    @Override
    public String execute() throws Exception {

        System.out.println("inside java class");

        organisationUnits.addAll(organisationUnitService.getAllOrganisationUnits());

        for (OrganisationUnit organisationUnit : organisationUnits) {

        //    System.out.println(organisationUnit.getCode() + " " + organisationUnit.getUid());

            if(organisationUnit.getCode() != null){
                keyCodeList.add(organisationUnit.getCode());
                uidMap.put(organisationUnit.getCode(), organisationUnit.getUid());
            }
        }

        BufferedReader in = null;
        try {
            String filename = path + "ccmp.xml";

            in = new BufferedReader(new FileReader(filename));
            while (in.ready()) {
                XMLcontent += in.readLine();
            }
            //System.out.println(XMLcontent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            in.close();
        }

        return SUCCESS;
    }

}
