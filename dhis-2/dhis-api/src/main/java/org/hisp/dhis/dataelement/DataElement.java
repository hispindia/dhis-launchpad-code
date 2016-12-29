package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.CategoryComboXmlAdapter;
import org.hisp.dhis.common.adapter.DataElementGroupXmlAdapter;
import org.hisp.dhis.common.adapter.DataSetXmlAdapter;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.dataset.DataSet.NO_EXPIRY;

/**
 * A DataElement is a definition (meta-information about) of the entities that
 * are captured in the system. An example from public health care is a
 * DataElement representing the number BCG doses; A DataElement with "BCG dose"
 * as name, with type DataElement.TYPE_INT. DataElements can be structured
 * hierarchically, one DataElement can have a parent and a collection of
 * children. The sum of the children represent the same entity as the parent.
 * Hiearchies of DataElements are used to give more fine- or course-grained
 * representations of the entities.
 * <p/>
 * DataElement acts as a DimensionSet in the dynamic dimensional model, and as a
 * DimensionOption in the static DataElement dimension.
 *
 * @author Kristian Nordal
 * @version $Id: DataElement.java 5540 2008-08-19 10:47:07Z larshelg $
 */
@XmlRootElement( name = "dataElement", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElement extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -7131541880444446669L;

    public static final String VALUE_TYPE_STRING = "string";

    public static final String VALUE_TYPE_INT = "int";

    public static final String VALUE_TYPE_NUMBER = "number";

    public static final String VALUE_TYPE_POSITIVE_INT = "positiveNumber";

    public static final String VALUE_TYPE_NEGATIVE_INT = "negativeNumber";

    public static final String VALUE_TYPE_BOOL = "bool";

    public static final String VALUE_TYPE_DATE = "date";

    public static final String DOMAIN_TYPE_AGGREGATE = "aggregate";

    public static final String DOMAIN_TYPE_PATIENT = "patient";

    public static final String AGGREGATION_OPERATOR_SUM = "sum";

    public static final String AGGREGATION_OPERATOR_AVERAGE = "average";

    public static final String AGGREGATION_OPERATOR_COUNT = "count";

    /**
     * The name to appear in forms.
     */
    private String formName;

    /**
     * If this DataElement is active or not (enabled or disabled).
     */
    private boolean active;

    /**
     * The domain of this DataElement; e.g. DataElement.DOMAIN_TYPE_AGGREGATE or
     * DataElement.DOMAIN_TYPE_PATIENT.
     */
    private String domainType;

    /**
     * The value type of this DataElement; e.g. DataElement.VALUE_TYPE_INT or
     * DataElement.VALUE_TYPE_BOOL.
     */
    private String type;

    /**
     * The number type. Is relevant when type is INT.
     */
    private String numberType;

    /**
     * The aggregation operator of this DataElement; e.g. DataElement.SUM og
     * DataElement.AVERAGE.
     */
    private String aggregationOperator;

    /**
     * A combination of categories to capture data.
     */
    private DataElementCategoryCombo categoryCombo;

    /**
     * Defines a custom sort order.
     */
    private Integer sortOrder;

    /**
     * URL for lookup of additional information on the web.
     */
    private String url;

    /**
     * The data element groups which this
     */
    private Set<DataElementGroup> groups = new HashSet<DataElementGroup>();

    /**
     * The data sets which this data element is a member of.
     */
    private Set<DataSet> dataSets = new HashSet<DataSet>();

    /**
     * The lower organisation unit levels for aggregation.
     */
    private List<Integer> aggregationLevels = new ArrayList<Integer>();

    /**
     * There is no point of saving 0's for this data element default is false
     * ,we don't want to store 0's if not set to true
     */
    private boolean zeroIsSignificant;

    /**
     * Set of the dynamic attributes values that belong to this data element.
     */
    private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
    
    /**
     * The option set for this data element.
     */
    private OptionSet optionSet;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElement()
    {
    }

    public DataElement( String name )
    {
        this.name = name;
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

        if ( !(o instanceof DataElement) )
        {
            return false;
        }

        final DataElement other = (DataElement) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataElementGroup( DataElementGroup group )
    {
        groups.add( group );
        group.getMembers().add( this );
    }

    public void removeDataElementGroup( DataElementGroup group )
    {
        groups.remove( group );
        group.getMembers().remove( this );
    }

    public void updateDataElementGroups( Set<DataElementGroup> updates )
    {
        for ( DataElementGroup group : new HashSet<DataElementGroup>( groups ) )
        {
            if ( !updates.contains( group ) )
            {
                removeDataElementGroup( group );
            }
        }

        for ( DataElementGroup group : updates )
        {
            addDataElementGroup( group );
        }
    }

    /**
     * Returns the value type. If value type is int and the number type exists,
     * the number type is returned, if the type is int and the number type does
     * not exist int is returned.
     */
    public String getDetailedNumberType()
    {
        return (type != null && type.equals( VALUE_TYPE_INT ) && numberType != null) ? numberType : type;
    }

    /**
     * Returns the PeriodType of the DataElement, based on the PeriodType of the
     * DataSet which the DataElement is registered for.
     */
    public PeriodType getPeriodType()
    {
        return dataSets != null && dataSets.size() > 0 ? dataSets.iterator().next().getPeriodType() : null;
    }

    /**
     * Returns the frequency order for the PeriodType of this DataElement. If no
     * PeriodType exists, 0 is returned.
     */
    public int getFrequencyOrder()
    {
        PeriodType periodType = getPeriodType();

        return periodType != null ? periodType.getFrequencyOrder() : YearlyPeriodType.FREQUENCY_ORDER;
    }

    /**
     * Tests whether a PeriodType can be defined for the DataElement, which
     * requires that the DataElement is registered for DataSets with the same
     * PeriodType.
     */
    public boolean periodTypeIsValid()
    {
        PeriodType periodType = null;

        for ( DataSet dataSet : dataSets )
        {
            if ( periodType != null && !periodType.equals( dataSet.getPeriodType() ) )
            {
                return false;
            }

            periodType = dataSet.getPeriodType();
        }

        return true;
    }

    /**
     * Tests whether more than one aggregation level exists for the DataElement.
     */
    public boolean hasAggregationLevels()
    {
        return aggregationLevels != null && aggregationLevels.size() > 0;
    }

    /**
     * Tests whether the DataElement is associated with a
     * DataELementCategoryCombo with more than one DataElementCategory, or any
     * DataElementCategory with more than one DataElementCategoryOption.
     */
    public boolean isMultiDimensional()
    {
        if ( categoryCombo != null )
        {
            if ( categoryCombo.getCategories().size() > 1 )
            {
                return true;
            }

            for ( DataElementCategory category : categoryCombo.getCategories() )
            {
                if ( category.getCategoryOptions().size() > 1 )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the domain type, or the default domain type if it does not exist.
     */
    public String getDomainTypeNullSafe()
    {
        return domainType != null ? domainType : DOMAIN_TYPE_AGGREGATE;
    }

    /**
     * Returns the form name, or the name if it does not exist.
     */
    public String getFormNameFallback()
    {
        return formName != null && !formName.isEmpty() ? formName : getDisplayName();
    }
    
    /**
     * Returns the minimum number of expiry days from the data sets of this data
     * element.
     */
    public int getExpiryDays()
    {
        int expiryDays = Integer.MAX_VALUE;
        
        for ( DataSet dataSet : dataSets )
        {
            if ( dataSet.getExpiryDays() != NO_EXPIRY && dataSet.getExpiryDays() < expiryDays )
            {
                expiryDays = dataSet.getExpiryDays();
            }
        }
        
        return expiryDays == Integer.MAX_VALUE ? NO_EXPIRY : expiryDays;
    }

    public String toJSON()
    {
        StringBuffer result = new StringBuffer();

        result.append( "{" );
        result.append( "\"id\":\"" + this.id + "\"" );
        result.append( ",\"name\":\"" + StringEscapeUtils.escapeJavaScript( this.name ) + "\"" );
        result.append( ",\"shortName\":\"" + StringEscapeUtils.escapeJavaScript( this.shortName ) + "\"" );
        result.append( ",\"type\":\"" + StringEscapeUtils.escapeJavaScript( this.type ) + "\"" );
        result.append( "}" );
        return result.toString();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement
    @JsonProperty
    public String getFormName()
    {
        return formName;
    }

    public void setFormName( String formName )
    {
        this.formName = formName;
    }

    @XmlElement
    @JsonProperty
    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    @XmlElement
    @JsonProperty
    public String getDomainType()
    {
        return domainType;
    }

    public void setDomainType( String domainType )
    {
        this.domainType = domainType;
    }

    @XmlElement
    @JsonProperty
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @XmlElement
    @JsonProperty
    public String getAggregationOperator()
    {
        return aggregationOperator;
    }

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    @XmlElement
    @XmlJavaTypeAdapter( CategoryComboXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    @XmlElement
    @JsonProperty
    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    @XmlElement
    @JsonProperty
    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    @XmlElementWrapper( name = "dataElementGroups" )
    @XmlElement( name = "dataElementGroup" )
    @XmlJavaTypeAdapter( DataElementGroupXmlAdapter.class )
    @JsonProperty( value = "dataElementGroups" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<DataElementGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<DataElementGroup> groups )
    {
        this.groups = groups;
    }

    @XmlElementWrapper( name = "dataSets" )
    @XmlElement( name = "dataSet" )
    @XmlJavaTypeAdapter( DataSetXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @XmlElementWrapper( name = "aggregationLevels" )
    @XmlElement( name = "aggregationLevel" )
    public List<Integer> getAggregationLevels()
    {
        return aggregationLevels;
    }

    public void setAggregationLevels( List<Integer> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }

    @XmlElement
    @JsonProperty
    public boolean isZeroIsSignificant()
    {
        return zeroIsSignificant;
    }

    public void setZeroIsSignificant( boolean zeroIsSignificant )
    {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    @XmlElement
    @JsonProperty
    public String getNumberType()
    {
        return numberType;
    }

    public void setNumberType( String numberType )
    {
        this.numberType = numberType;
    }

    @XmlElementWrapper( name = "attributes" )
    @XmlElement( name = "attribute" )
    @JsonProperty( value = "attributes" )
    public Set<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet = optionSet;
    }
}
