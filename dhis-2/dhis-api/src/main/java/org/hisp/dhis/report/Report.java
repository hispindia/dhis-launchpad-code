package org.hisp.dhis.report;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.ReportTableXmlAdapter;
import org.hisp.dhis.reporttable.ReportTable;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@XmlRootElement( name = "report", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Report
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 7880117720157807526L;

    public static final String TEMPLATE_DIR = "templates";

    private String designContent;

    private ReportTable reportTable;

    private Boolean usingOrgUnitGroupSets;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Report()
    {
    }

    public Report( String name, String designContent, ReportTable reportTable )
    {
        this.name = name;
        this.designContent = designContent;
        this.reportTable = reportTable;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasReportTable()
    {
        return reportTable != null;
    }

    public boolean isUsingOrganisationUnitGroupSets()
    {
        return usingOrgUnitGroupSets != null && usingOrgUnitGroupSets;
    }

    // -------------------------------------------------------------------------
    // Equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + ((name == null) ? 0 : name.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final Report other = (Report) object;

        return this.name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement
    @JsonProperty
    public String getDesignContent()
    {
        return designContent;
    }

    public void setDesignContent( String designContent )
    {
        this.designContent = designContent;
    }

    @XmlElement
    @XmlJavaTypeAdapter( ReportTableXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public ReportTable getReportTable()
    {
        return reportTable;
    }

    public void setReportTable( ReportTable reportTable )
    {
        this.reportTable = reportTable;
    }

    public Boolean getUsingOrgUnitGroupSets()
    {
        return usingOrgUnitGroupSets;
    }

    public void setUsingOrgUnitGroupSets( Boolean usingOrgUnitGroupSets )
    {
        this.usingOrgUnitGroupSets = usingOrgUnitGroupSets;
    }
}
