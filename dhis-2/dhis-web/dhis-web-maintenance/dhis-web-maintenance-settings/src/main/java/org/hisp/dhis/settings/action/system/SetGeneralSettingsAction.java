package org.hisp.dhis.settings.action.system;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_AGGREGATION_STRATEGY;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_COMPLETENESS_OFFSET;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_DISABLE_DATAENTRYFORM_WHEN_COMPLETED;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_FACTOR_OF_DEVIATION;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART;

import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class SetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String aggregationStrategy;

    public void setAggregationStrategy( String aggregationStrategy )
    {
        this.aggregationStrategy = aggregationStrategy;
    }

    private Integer infrastructuralDataElements;

    public void setInfrastructuralDataElements( Integer infrastructuralDataElements )
    {
        this.infrastructuralDataElements = infrastructuralDataElements;
    }

    private String infrastructuralPeriodType;

    public void setInfrastructuralPeriodType( String infrastructuralPeriodType )
    {
        this.infrastructuralPeriodType = infrastructuralPeriodType;
    }

    private Boolean omitIndicatorsZeroNumeratorDataMart;

    public void setOmitIndicatorsZeroNumeratorDataMart( Boolean omitIndicatorsZeroNumeratorDataMart )
    {
        this.omitIndicatorsZeroNumeratorDataMart = omitIndicatorsZeroNumeratorDataMart;
    }

    private boolean disableDataEntryWhenCompleted;

    public void setDisableDataEntryWhenCompleted( boolean disableDataEntryWhenCompleted )
    {
        this.disableDataEntryWhenCompleted = disableDataEntryWhenCompleted;
    }

    private Double factorDeviation;

    public void setFactorDeviation( Double factorDeviation )
    {
        this.factorDeviation = factorDeviation;
    }

    private Integer feedbackRecipients;

    public void setFeedbackRecipients( Integer feedbackRecipients )
    {
        this.feedbackRecipients = feedbackRecipients;
    }
    
    private Integer completenessRecipients;

    public void setCompletenessRecipients( Integer completenessRecipients )
    {
        this.completenessRecipients = completenessRecipients;
    }

    private Integer completenessOffset;

    public void setCompletenessOffset( Integer completenessOffset )
    {
        this.completenessOffset = completenessOffset;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        systemSettingManager.saveSystemSetting( KEY_AGGREGATION_STRATEGY, aggregationStrategy );
        systemSettingManager.saveSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, omitIndicatorsZeroNumeratorDataMart );
        systemSettingManager.saveSystemSetting( KEY_DISABLE_DATAENTRYFORM_WHEN_COMPLETED, disableDataEntryWhenCompleted );
        systemSettingManager.saveSystemSetting( KEY_FACTOR_OF_DEVIATION, factorDeviation );
        systemSettingManager.saveSystemSetting( KEY_COMPLETENESS_OFFSET, completenessOffset );

        Configuration configuration = configurationService.getConfiguration();

        if ( feedbackRecipients != null )
        {
            configuration.setFeedbackRecipients( userGroupService.getUserGroup( feedbackRecipients ) );
        }

        if ( completenessRecipients != null )
        {
            configuration.setCompletenessRecipients( userGroupService.getUserGroup( completenessRecipients ) );
        }
        
        if ( infrastructuralDataElements != null )
        {
            configuration.setInfrastructuralDataElements( dataElementService
                .getDataElementGroup( infrastructuralDataElements ) );
        }

        if ( infrastructuralPeriodType != null )
        {
            configuration.setInfrastructuralPeriodType( periodService.getPeriodTypeByClass( PeriodType
                .getPeriodTypeByName( infrastructuralPeriodType ).getClass() ) );
        }

        configurationService.setConfiguration( configuration );

        message = i18n.getString( "settings_updated" );
        
        return SUCCESS;
    }
}
