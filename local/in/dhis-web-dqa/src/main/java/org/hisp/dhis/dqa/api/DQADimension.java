package org.hisp.dhis.dqa.api;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.validation.ValidationRule;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement( localName = "DQADimension", namespace = DxfNamespaces.DXF_2_0)
public class DQADimension  extends BaseNameableObject
{
    public static final String DQA_DIMENSION_TYPE_DEFAULT = "default";
    public static final String DQA_DIMENSION_TYPE_INDICATOR = "indicator";
    //public static final String DQA_DIMENSION_TYPE_VALIDATIONRULE = "validationrule";
    
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2466830446144115499L;
    
    private String description;
    
    private String type;
    
    /**
     * Indicators associated with this DQA Dimension. Indicators are used for view and
     * output purposes, such as calculated fields in forms and reports.
     */
    @Scanned
    private Set<Indicator> indicators = new HashSet<Indicator>();
    
    /**
     * All ValidationRules associated with this DQA Dimension.
     */
    
    //private Set<ValidationRule> validationRules = new HashSet<ValidationRule>();
    
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    
    public DQADimension()
    {
    }

    public DQADimension( String name )
    {
        this.name = name;
    }

    public DQADimension( String name, String description, String type )
    {
        this.name = name;
        this.description = description;
        this.type = type;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof DQADimension) )
        {
            return false;
        }

        final DQADimension other = (DQADimension) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public Set<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( Set<Indicator> indicators )
    {
        this.indicators = indicators;
    }
    
    /*
    public Set<ValidationRule> getValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( Set<ValidationRule> validationRules )
    {
        this.validationRules = validationRules;
    }
    */
}
