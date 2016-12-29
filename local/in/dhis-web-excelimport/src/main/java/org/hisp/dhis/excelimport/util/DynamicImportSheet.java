package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

/**
 * Gaurav<gaurav08021@gmail.com>, 12/28/12 [5:25 PM]
 */
public class DynamicImportSheet implements Serializable {

    private String RscriptName;
    private String displayName;
    private String mappingXML;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DynamicImportSheet()
    {

    }

    public DynamicImportSheet(String rScriptName, String displayName, String mappingXML)
    {
        this.RscriptName = rScriptName;
        this.displayName = displayName;
        this.mappingXML = mappingXML;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getRscriptName()
    {
        return RscriptName;
    }

    public void setRscriptName(String rScriptName)
    {
        this.RscriptName = rScriptName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getMappingXML() {
        return mappingXML;
    }

    public void setMappingXML(String mappingXML) {
        this.mappingXML = mappingXML;
    }
}
