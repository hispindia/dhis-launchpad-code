package org.hisp.dhis.reportsheet.importing;

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

import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.importitem.ImportReportService;
import org.hisp.dhis.reportsheet.period.generic.PeriodGenericManager;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public abstract class ImportDataGeneric
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    protected DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    protected DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    protected DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    protected ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    protected ImportReportService importReportService;

    public void setImportReportService( ImportReportService importReportService )
    {
        this.importReportService = importReportService;
    }

    protected OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    protected OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    protected PeriodGenericManager periodGenericManager;

    public void setPeriodGenericManager( PeriodGenericManager periodGenericManager )
    {
        this.periodGenericManager = periodGenericManager;
    }

    protected SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        String[] importItemIds = selectionManager.getListObject();

        if ( importItemIds == null )
        {
            message = i18n.getString( "choose_import_item" );

            return ERROR;
        }

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit != null )
        {
            Period period = periodGenericManager.getSelectedPeriod();

            executeToImport( organisationUnit, period, importItemIds );
        }

        message = i18n.getString( "import_successfully" );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Abstract method
    // -------------------------------------------------------------------------

    public abstract void executeToImport( OrganisationUnit organisationUnit, Period period, String[] importItemIds );

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    protected void addDataValue( OrganisationUnit unit, Period period, String expression, String value )
    {
        value = value.replaceAll( "\\.", "" ).replace( ",", "." );
        
        DataElementOperand operand = expressionService.getOperandsInExpression( expression ).iterator().next();

        DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( operand
            .getOptionComboId() );

        String storedBy = currentUserService.getCurrentUsername();

        DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period, optionCombo );

        if ( dataValue == null )
        {
            dataValue = new DataValue( dataElement, period, unit, value, storedBy, new Date(), null, optionCombo );
            dataValueService.addDataValue( dataValue );
        }
        else
        {
            dataValue.setValue( value );
            dataValue.setTimestamp( new Date() );
            dataValue.setStoredBy( storedBy );

            dataValueService.updateDataValue( dataValue );
        }
    }
}
