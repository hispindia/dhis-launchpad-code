package org.hisp.dhis.reporttable;

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

import org.codehaus.jackson.annotate.JsonProperty;
import org.hisp.dhis.common.Dxf2Namespace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * The ReportParams object represents report parameters for a ReportTable. Report
 * parameters are meant to make ReportTables more generic, as it can avoid having
 * dynamic, selectable parameters rather than static.
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
@XmlRootElement( name = "reportParams", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class ReportParams
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 2509958165452862235L;

    private Boolean paramReportingMonth;

    private Boolean paramLeafParentOrganisationUnit;
    
    private Boolean paramGrandParentOrganisationUnit;
    
    private Boolean paramParentOrganisationUnit;
    
    private Boolean paramOrganisationUnit;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ReportParams()
    {
    }
    
    public ReportParams( boolean paramReportingMonth, boolean paramLeafParentOrganisationUnit,
        boolean paramGrandParentOrganisationUnit, boolean paramParentOrganisationUnit, boolean paramOrganisationUnit )
    {
        this.paramReportingMonth = paramReportingMonth;
        this.paramLeafParentOrganisationUnit = paramLeafParentOrganisationUnit;
        this.paramGrandParentOrganisationUnit = paramGrandParentOrganisationUnit;
        this.paramParentOrganisationUnit = paramParentOrganisationUnit;
        this.paramOrganisationUnit = paramOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isParamReportingMonth()
    {
        return paramReportingMonth != null && paramReportingMonth;
    }
    
    public boolean isParamLeafParentOrganisationUnit()
    {
        return paramLeafParentOrganisationUnit != null && paramLeafParentOrganisationUnit;
    }
    
    public boolean isParamGrandParentOrganisationUnit()
    {
        return paramGrandParentOrganisationUnit != null && paramGrandParentOrganisationUnit;
    }
    
    public boolean isParamParentOrganisationUnit()
    {
        return paramParentOrganisationUnit != null && paramParentOrganisationUnit;
    }
    
    public boolean isParamOrganisationUnit()
    {
        return paramOrganisationUnit != null && paramOrganisationUnit;
    }
    
    public boolean isSet()
    {
        return isParamReportingMonth() || isOrganisationUnitSet();
    }

    public boolean isOrganisationUnitSet()
    {
        return isParamLeafParentOrganisationUnit() || isParamGrandParentOrganisationUnit() || 
            isParamParentOrganisationUnit() || isParamOrganisationUnit();
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement
    @JsonProperty
    public Boolean getParamReportingMonth()
    {
        return paramReportingMonth;
    }

    public void setParamReportingMonth( Boolean paramReportingMonth )
    {
        this.paramReportingMonth = paramReportingMonth;
    }

    @XmlElement
    @JsonProperty
    public Boolean getParamLeafParentOrganisationUnit()
    {
        return paramLeafParentOrganisationUnit;
    }

    public void setParamLeafParentOrganisationUnit( Boolean paramLeafParentOrganisationUnit )
    {
        this.paramLeafParentOrganisationUnit = paramLeafParentOrganisationUnit;
    }

    @XmlElement
    @JsonProperty
    public Boolean getParamGrandParentOrganisationUnit()
    {
        return paramGrandParentOrganisationUnit;
    }

    public void setParamGrandParentOrganisationUnit( Boolean paramGrandParentOrganisationUnit )
    {
        this.paramGrandParentOrganisationUnit = paramGrandParentOrganisationUnit;
    }

    @XmlElement
    @JsonProperty
    public Boolean getParamParentOrganisationUnit()
    {
        return paramParentOrganisationUnit;
    }

    public void setParamParentOrganisationUnit( Boolean paramParentOrganisationUnit )
    {
        this.paramParentOrganisationUnit = paramParentOrganisationUnit;
    }

    @XmlElement
    @JsonProperty
    public Boolean getParamOrganisationUnit()
    {
        return paramOrganisationUnit;
    }

    public void setParamOrganisationUnit( Boolean paramOrganisationUnit )
    {
        this.paramOrganisationUnit = paramOrganisationUnit;
    }    
}
