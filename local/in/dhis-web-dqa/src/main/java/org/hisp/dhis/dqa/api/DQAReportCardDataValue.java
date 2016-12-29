package org.hisp.dhis.dqa.api;

import java.io.Serializable;
import java.util.Date;

public class DQAReportCardDataValue  implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6269303850789110610L;
    
    
    /**
     * Part of the DQAReportCardDataValue's composite ID
     */
    
    private String level1;
    
    /**
     * Part of the DQAReportCardDataValue's composite ID
     */
    
    private String level2;
    
    /**
     * Part of the DQAReportCardDataValue's composite ID
     */
    private String year;
    
    /**
     * Part of the DQAReportCardDataValue's composite ID
     */
    
    private String dqaParameter;
    
    
    
    private String value;

    private String storedBy;

    private Date timestamp;

   
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DQAReportCardDataValue()
    {
    }

    public DQAReportCardDataValue( String level1, String level2, String year, String dqaParameter )
    {
        this.level1 = level1;
        this.level2 = level2;
        this.year = year;
        this.dqaParameter = dqaParameter;
    }
    
    public DQAReportCardDataValue( String level1, String level2, String year, String dqaParameter, String value )
    {
        this.level1 = level1;
        this.level2 = level2;
        this.year = year;
        this.dqaParameter = dqaParameter;
        this.value = value;
    }
    
    public DQAReportCardDataValue( String level1, String level2, String year, String dqaParameter, String storedBy, Date timestamp )
    {
        this.level1 = level1;
        this.level2 = level2;
        this.year = year;
        this.dqaParameter = dqaParameter;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
    }    
    
    
    public DQAReportCardDataValue( String level1, String level2, String year, String dqaParameter, String value, String storedBy, Date timestamp )
    {
        this.level1 = level1;
        this.level2 = level2;
        this.year = year;
        this.dqaParameter = dqaParameter;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
    }    
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isZero()
    {
        return dqaParameter != null  && value != null && new Double( value ).intValue() == 0;
    }
    
    public boolean isNullValue()
    {
        return value == null;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DQAReportCardDataValue) )
        {
            return false;
        }

        final DQAReportCardDataValue other = (DQAReportCardDataValue) o;

        return level1.equals( other.getLevel1() ) && level2.equals( other.getLevel2() )
            && year.equals( other.getYear() ) && dqaParameter.equals( other.getDqaParameter() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + level1.hashCode();
        result = result * prime + level2.hashCode();
        result = result * prime + year.hashCode();
        result = result * prime + dqaParameter.hashCode();

        return result;
    }
    
    
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------    
    
    public String getLevel1()
    {
        return level1;
    }

    public void setLevel1( String lavel1 )
    {
        this.level1 = lavel1;
    }

    public String getLevel2()
    {
        return level2;
    }

    public void setLevel2( String lavel2 )
    {
        this.level2 = lavel2;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear( String year )
    {
        this.year = year;
    }

    public String getDqaParameter()
    {
        return dqaParameter;
    }

    public void setDqaParameter( String dqaParameter )
    {
        this.dqaParameter = dqaParameter;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
  
    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }
}
