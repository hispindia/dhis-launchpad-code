package org.hisp.dhis.dataverify;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author BHARATH
 *
 */
public class DataVerification
{

    private int id;
    
    private OrganisationUnit organisationUnit;
    
    private DataSet dataSet;
    
    private String fromDate;
    
    private String toDate;
    
    private String verificationStatus;
    
    private String remarks;
    
    private String storedBy;
    
    private String lastUpdated;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public DataVerification()
    {
        
    }
    
    public DataVerification( OrganisationUnit organisationUnit, DataSet dataSet, String fromDate, 
        String toDate, String verificationStatus, String remarks, String storedBy, String lastUpdated )
    {
        this.organisationUnit = organisationUnit;
        this.dataSet = dataSet;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.verificationStatus = verificationStatus;
        this.remarks = remarks;
        this.storedBy = storedBy;
        this.lastUpdated = lastUpdated;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public void setFromDate( String fromDate )
    {
        this.fromDate = fromDate;
    }

    public String getToDate()
    {
        return toDate;
    }

    public void setToDate( String toDate )
    {
        this.toDate = toDate;
    }

    public String getVerificationStatus()
    {
        return verificationStatus;
    }

    public void setVerificationStatus( String verificationStatus )
    {
        this.verificationStatus = verificationStatus;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks( String remarks )
    {
        this.remarks = remarks;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public String getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( String lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }
}
