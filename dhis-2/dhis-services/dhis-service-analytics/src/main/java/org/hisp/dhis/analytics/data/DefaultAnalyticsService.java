package org.hisp.dhis.analytics.data;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsSecurityManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DimensionItem;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.calendar.DateTimeUnit;
import org.hisp.dhis.common.AnalyticalObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.DisplayProperty;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.comparator.AscendingPeriodEndDateComparator;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DebugUtils;
import org.hisp.dhis.system.util.ListUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.system.util.UniqueArrayList;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static org.hisp.dhis.analytics.AnalyticsTableManager.*;
import static org.hisp.dhis.analytics.DataQueryParams.*;
import static org.hisp.dhis.common.DimensionalObject.*;
import static org.hisp.dhis.common.DimensionalObjectUtils.toDimension;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getLocalPeriodIdentifier;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getLocalPeriodIdentifiers;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.common.NameableObjectUtils.asList;
import static org.hisp.dhis.common.NameableObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.*;
import static org.hisp.dhis.period.PeriodType.getPeriodTypeFromIsoString;
import static org.hisp.dhis.reporttable.ReportTable.IRT2D;
import static org.hisp.dhis.reporttable.ReportTable.addIfEmpty;

/**
 * @author Lars Helge Overland
 */
public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsService.class );

    private static final String VALUE_HEADER_NAME = "Value";
    private static final int PERCENT = 100;
    private static final int MAX_QUERIES = 8;

    //TODO make sure data x dims are successive
    //TODO completeness on time

    @Autowired
    private AnalyticsManager analyticsManager;

    @Autowired
    private AnalyticsSecurityManager securityManager;

    @Autowired
    private QueryPlanner queryPlanner;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private DataElementOperandService operandService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService; // Testing purposes
    }

    // -------------------------------------------------------------------------
    // Methods for retrieving aggregated data
    // -------------------------------------------------------------------------

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params )
    {
        // ---------------------------------------------------------------------
        // Security and validation
        // ---------------------------------------------------------------------

        securityManager.decideAccess( params );

        securityManager.applyDataApprovalConstraints( params );

        securityManager.applyDimensionConstraints( params );

        queryPlanner.validate( params );

        params.conform();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        addHeaders( params, grid );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        addIndicatorValues( params, grid );

        addDataElementValues( params, grid );

        addDataSetValues( params, grid );

        addDynamicDimensionValues( params, grid );

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        addMetaData( params, grid );

        return grid;
    }

    /**
     * Adds headers to the given grid based on the given data query parameters.
     */
    private void addHeaders( DataQueryParams params, Grid grid )
    {
        for ( DimensionalObject col : params.getHeaderDimensions() )
        {
            grid.addHeader( new GridHeader( col.getDimension(), col.getDisplayName(), String.class.getName(), false, true ) );
        }

        grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );
    }

    /**
     * Adds indicator values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid   the grid.
     */
    private void addIndicatorValues( DataQueryParams params, Grid grid )
    {
        if ( params.getIndicators() != null )
        {
            int indicatorIndex = params.getIndicatorDimensionIndex();
            List<Indicator> indicators = asTypedList( params.getIndicators() );

            expressionService.explodeExpressions( indicators );

            // -----------------------------------------------------------------
            // Get indicator values
            // -----------------------------------------------------------------

            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimension( DATAELEMENT_DIM_ID );
            dataSourceParams.removeDimension( DATASET_DIM_ID );

            dataSourceParams = replaceIndicatorsWithDataElements( dataSourceParams, indicatorIndex );

            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams );

            Map<String, Map<DataElementOperand, Double>> permutationOperandValueMap = dataSourceParams.getPermutationOperandValueMap( aggregatedDataMap );

            List<List<DimensionItem>> dimensionItemPermutations = dataSourceParams.getDimensionItemPermutations();

            Map<String, Double> constantMap = constantService.getConstantMap();

            Period filterPeriod = dataSourceParams.getFilterPeriod();

            Map<String, Map<String, Integer>> permutationOrgUnitTargetMap = getOrgUnitTargetMap( dataSourceParams, indicators );

            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionItem> options : dimensionItemPermutations )
                {
                    String permKey = DimensionItem.asItemKey( options );

                    Map<DataElementOperand, Double> valueMap = permutationOperandValueMap.get( permKey );

                    if ( valueMap == null )
                    {
                        continue;
                    }

                    Period period = filterPeriod != null ? filterPeriod : (Period) DimensionItem.getPeriodItem( options );

                    OrganisationUnit unit = (OrganisationUnit) DimensionItem.getOrganisationUnitItem( options );

                    String ou = unit != null ? unit.getUid() : null;

                    Map<String, Integer> orgUnitCountMap = permutationOrgUnitTargetMap != null ? permutationOrgUnitTargetMap.get( ou ) : null;

                    Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, orgUnitCountMap, period.getDaysInPeriod() );

                    if ( value != null )
                    {
                        List<DimensionItem> row = new ArrayList<>( options );

                        row.add( indicatorIndex, new DimensionItem( INDICATOR_DIM_ID, indicator ) );

                        grid.addRow();
                        grid.addValues( DimensionItem.getItemIdentifiers( row ) );
                        grid.addValue( params.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                    }
                }
            }
        }
    }

    /**
     * Adds data element values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid   the grid.
     */
    private void addDataElementValues( DataQueryParams params, Grid grid )
    {
        if ( params.getDataElements() != null )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATASET_DIM_ID );

            Map<String, Object> aggregatedDataMap = getAggregatedDataValueMapObjectTyped( dataSourceParams );

            for ( Map.Entry<String, Object> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : getRounded( entry.getValue() ) );
            }
        }
    }

    /**
     * Adds data set values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid   the grid.
     */
    private void addDataSetValues( DataQueryParams params, Grid grid )
    {
        if ( params.getDataSets() != null )
        {
            // -----------------------------------------------------------------
            // Get complete data set registrations
            // -----------------------------------------------------------------

            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.ignoreDataApproval(); // No approval for reporting rates
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATAELEMENT_DIM_ID );
            dataSourceParams.setAggregationType( AggregationType.COUNT );

            Map<String, Double> aggregatedDataMap = getAggregatedCompletenessValueMap( dataSourceParams );

            // -----------------------------------------------------------------
            // Get completeness targets
            // -----------------------------------------------------------------

            List<Integer> completenessDimIndexes = dataSourceParams.getCompletenessDimensionIndexes();
            List<Integer> completenessFilterIndexes = dataSourceParams.getCompletenessFilterIndexes();

            DataQueryParams targetParams = dataSourceParams.instance();

            targetParams.setDimensions( ListUtils.getAtIndexes( targetParams.getDimensions(), completenessDimIndexes ) );
            targetParams.setFilters( ListUtils.getAtIndexes( targetParams.getFilters(), completenessFilterIndexes ) );
            targetParams.setSkipPartitioning( true );

            Map<String, Double> targetMap = getAggregatedCompletenessTargetMap( targetParams );

            Integer periodIndex = dataSourceParams.getPeriodDimensionIndex();
            Integer dataSetIndex = dataSourceParams.getDataSetDimensionIndex();

            Map<String, PeriodType> dsPtMap = dataSourceParams.getDataSetPeriodTypeMap();

            PeriodType filterPeriodType = dataSourceParams.getFilterPeriodType();

            // -----------------------------------------------------------------
            // Join data maps, calculate completeness and add to grid
            // -----------------------------------------------------------------

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                List<String> dataRow = ListUtils.getList( entry.getKey().split( DIMENSION_SEP ) );

                List<String> targetRow = ListUtils.getAtIndexes( dataRow, completenessDimIndexes );
                String targetKey = StringUtils.join( targetRow, DIMENSION_SEP );
                Double target = targetMap.get( targetKey );

                if ( target != null && entry.getValue() != null )
                {
                    PeriodType queryPt = filterPeriodType != null ? filterPeriodType : getPeriodTypeFromIsoString( dataRow.get( periodIndex ) );
                    PeriodType dataSetPt = dsPtMap.get( dataRow.get( dataSetIndex ) );

                    target = target * queryPt.getPeriodSpan( dataSetPt );

                    double value = entry.getValue() * PERCENT / target;

                    grid.addRow();
                    grid.addValues( dataRow.toArray() );
                    grid.addValue( params.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                }
            }
        }
    }

    /**
     * Adds values to the given grid based on dynamic dimensions from the given
     * data query parameters.
     *
     * @param params the data query parameters.
     * @param grid   the grid.
     */
    private void addDynamicDimensionValues( DataQueryParams params, Grid grid )
    {
        if ( params.getIndicators() == null && params.getDataElements() == null && params.getDataSets() == null )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( params.instance() );

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : MathUtils.getRounded( entry.getValue() ) );
            }
        }
    }

    /**
     * Adds meta data values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid   the grid.
     */
    private void addMetaData( DataQueryParams params, Grid grid )
    {
        if ( !params.isSkipMeta() )
        {
            Map<Object, Object> metaData = new HashMap<>();

            Map<String, String> uidNameMap = getUidNameMap( params );
            Map<String, String> cocNameMap = getCocNameMap( params );
            uidNameMap.putAll( cocNameMap );

            metaData.put( NAMES_META_KEY, uidNameMap );

            List<String> periodUids = new ArrayList<>();

            Calendar calendar = PeriodType.getCalendar();

            if ( calendar.isIso8601() )
            {
                periodUids = getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) );
            }
            else
            {
                periodUids = getLocalPeriodIdentifiers( params.getDimensionOrFilter( PERIOD_DIM_ID ), calendar );
            }

            metaData.put( PERIOD_DIM_ID, periodUids );
            metaData.put( ORGUNIT_DIM_ID, getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) );
            metaData.put( CATEGORYOPTIONCOMBO_DIM_ID, cocNameMap.keySet() );

            if ( params.isHierarchyMeta() )
            {
                metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ) ) );
            }

            if ( params.isShowHierarchy() )
            {
                metaData.put( OU_NAME_HIERARCHY_KEY, getParentNameGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ), true ) );
            }

            grid.setMetaData( metaData );
        }
    }

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows )
    {
        Grid grid = getAggregatedDataValues( params );

        if ( !tableLayout )
        {
            return grid;
        }

        ListUtils.removeEmptys( columns );
        ListUtils.removeEmptys( rows );

        queryPlanner.validateTableLayout( params, columns, rows );

        ReportTable reportTable = new ReportTable();

        List<NameableObject[]> tableColumns = new ArrayList<>();
        List<NameableObject[]> tableRows = new ArrayList<>();

        if ( columns != null )
        {
            for ( String dimension : columns )
            {
                reportTable.getColumnDimensions().add( dimension );

                tableColumns.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }

        if ( rows != null )
        {
            for ( String dimension : rows )
            {
                reportTable.getRowDimensions().add( dimension );

                tableRows.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }

        reportTable.setGridColumns( new CombinationGenerator<>( tableColumns.toArray( IRT2D ) ).getCombinations() );
        reportTable.setGridRows( new CombinationGenerator<>( tableRows.toArray( IRT2D ) ).getCombinations() );

        addIfEmpty( reportTable.getGridColumns() );
        addIfEmpty( reportTable.getGridRows() );

        reportTable.setTitle( IdentifiableObjectUtils.join( params.getFilterItems() ) );
        reportTable.setHideEmptyRows( params.isHideEmptyRows() );
        reportTable.setShowHierarchy( params.isShowHierarchy() );

        Map<String, Object> valueMap = getAggregatedDataValueMapping( grid );

        return reportTable.getGrid( new ListGrid( grid.getMetaData() ), valueMap, false );
    }

    @Override
    public Map<String, Object> getAggregatedDataValueMapping( DataQueryParams params )
    {
        Grid grid = getAggregatedDataValues( params );

        return getAggregatedDataValueMapping( grid );
    }

    @Override
    public Map<String, Object> getAggregatedDataValueMapping( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = getFromAnalyticalObject( object, format );

        return getAggregatedDataValueMapping( params );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Generates a mapping of permutations keys (organisation unit id or null)
     * and mappings of organisation unit group and counts.
     *
     * @param params     the data query parameters.
     * @param indicators the indicators for which formulas to scan for organisation
     *                   unit groups.
     * @return a map of maps.
     */
    private Map<String, Map<String, Integer>> getOrgUnitTargetMap( DataQueryParams params, Collection<Indicator> indicators )
    {
        Set<OrganisationUnitGroup> orgUnitGroups = expressionService.getOrganisationUnitGroupsInIndicators( indicators );

        if ( orgUnitGroups == null || orgUnitGroups.isEmpty() )
        {
            return null;
        }

        DataQueryParams orgUnitTargetParams = params.instance().pruneToDimensionType( DimensionType.ORGANISATIONUNIT );
        orgUnitTargetParams.getDimensions().add( new BaseDimensionalObject( DimensionalObject.ORGUNIT_GROUP_DIM_ID, null, new ArrayList<NameableObject>( orgUnitGroups ) ) );
        orgUnitTargetParams.setSkipPartitioning( true );

        Map<String, Double> orgUnitCountMap = getAggregatedOrganisationUnitTargetMap( orgUnitTargetParams );

        return orgUnitTargetParams.getPermutationOrgUnitGroupCountMap( orgUnitCountMap );
    }

    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given grid.
     *
     * @param grid the grid.
     * @return a mapping between item identifiers and aggregated values.
     */
    private Map<String, Object> getAggregatedDataValueMapping( Grid grid )
    {
        Map<String, Object> map = new HashMap<>();

        int metaCols = grid.getWidth() - 1;
        int valueIndex = grid.getWidth() - 1;

        for ( List<Object> row : grid.getRows() )
        {
            StringBuilder key = new StringBuilder();

            for ( int index = 0; index < metaCols; index++ )
            {
                key.append( row.get( index ) ).append( DIMENSION_SEP );
            }

            key.deleteCharAt( key.length() - 1 );

            Object value = row.get( valueIndex );

            map.put( key.toString(), value );
        }

        return map;
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedDataValueMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, ANALYTICS_TABLE_NAME ) );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Object> getAggregatedDataValueMapObjectTyped( DataQueryParams params )
    {
        return getAggregatedValueMap( params, ANALYTICS_TABLE_NAME );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedCompletenessValueMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the data set dimension key and the count
     * of expected data sets to report.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     * expected data sets to report.
     */
    private Map<String, Double> getAggregatedCompletenessTargetMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the org unit dimension key and the count
     * of org units inside the subtree of the given organisation units and
     * members of the given organisation unit groups.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     * expected data sets to report.
     */
    private Map<String, Double> getAggregatedOrganisationUnitTargetMap( DataQueryParams params )
    {
        return getDoubleMap( getAggregatedValueMap( params, ORGUNIT_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between a dimension key and the aggregated value. The
     * dimension key is a concatenation of the identifiers of the dimension items
     * separated by "-".
     *
     * @param params the data query parameters.
     */
    private Map<String, Object> getAggregatedValueMap( DataQueryParams params, String tableName )
    {
        queryPlanner.validateMaintenanceMode();

        int optimalQueries = MathUtils.getWithin( getProcessNo(), 1, MAX_QUERIES );

        Timer t = new Timer().start().disablePrint();

        DataQueryGroups queryGroups = queryPlanner.planQuery( params, optimalQueries, tableName );

        t.getSplitTime( "Planned analytics query, got: " + queryGroups.getLargestGroupSize() + " for optimal: " + optimalQueries );

        Map<String, Object> map = new HashMap<>();

        for ( List<DataQueryParams> queries : queryGroups.getSequentialQueries() )
        {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();

            for ( DataQueryParams query : queries )
            {
                futures.add( analyticsManager.getAggregatedDataValues( query ) );
            }

            for ( Future<Map<String, Object>> future : futures )
            {
                try
                {
                    Map<String, Object> taskValues = future.get();

                    if ( taskValues != null )
                    {
                        map.putAll( taskValues );
                    }
                }
                catch ( Exception ex )
                {
                    log.error( DebugUtils.getStackTrace( ex ) );
                    log.error( DebugUtils.getStackTrace( ex.getCause() ) );

                    throw new RuntimeException( "Error during execution of aggregation query task", ex );
                }
            }
        }

        t.getTime( "Got analytics values" );

        return map;
    }

    // -------------------------------------------------------------------------
    // Methods for assembling DataQueryParams
    // -------------------------------------------------------------------------

    @Override
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, AggregationType aggregationType,
        String measureCriteria, boolean skipMeta, boolean skipRounding, boolean hierarchyMeta, boolean ignoreLimit,
        boolean hideEmptyRows, boolean showHierarchy, DisplayProperty displayProperty, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        params.setAggregationType( aggregationType );
        params.setIgnoreLimit( ignoreLimit );

        if ( dimensionParams != null && !dimensionParams.isEmpty() )
        {
            params.getDimensions().addAll( getDimensionalObjects( dimensionParams, format ) );
        }

        if ( filterParams != null && !filterParams.isEmpty() )
        {
            params.getFilters().addAll( getDimensionalObjects( filterParams, format ) );
        }

        if ( measureCriteria != null && !measureCriteria.isEmpty() )
        {
            params.setMeasureCriteria( DataQueryParams.getMeasureCriteriaFromParam( measureCriteria ) );
        }

        params.setSkipMeta( skipMeta );
        params.setSkipRounding( skipRounding );
        params.setHierarchyMeta( hierarchyMeta );
        params.setHideEmptyRows( hideEmptyRows );
        params.setShowHierarchy( showHierarchy );
        params.setDisplayProperty( displayProperty );

        return params;
    }

    @Override
    public DataQueryParams getFromAnalyticalObject( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        if ( object != null )
        {
            Date date = object.getRelativePeriodDate();

            object.populateAnalyticalProperties();

            for ( DimensionalObject column : object.getColumns() )
            {
                params.getDimensions().addAll( getDimension( toDimension( column.getDimension() ), getUids( column.getItems() ), date, format, false ) );
            }

            for ( DimensionalObject row : object.getRows() )
            {
                params.getDimensions().addAll( getDimension( toDimension( row.getDimension() ), getUids( row.getItems() ), date, format, false ) );
            }

            for ( DimensionalObject filter : object.getFilters() )
            {
                params.getFilters().addAll( getDimension( toDimension( filter.getDimension() ), getUids( filter.getItems() ), date, format, false ) );
            }
        }

        return params;
    }

    @Override
    public List<DimensionalObject> getDimensionalObjects( Set<String> dimensionParams, I18nFormat format )
    {
        List<DimensionalObject> list = new ArrayList<>();

        if ( dimensionParams != null )
        {
            for ( String param : dimensionParams )
            {
                String dimension = DimensionalObjectUtils.getDimensionFromParam( param );
                List<String> options = DimensionalObjectUtils.getDimensionItemsFromParam( param );

                if ( dimension != null && options != null )
                {
                    list.addAll( getDimension( dimension, options, null, format, false ) );
                }
            }
        }

        return list;
    }

    // TODO verify that current user can read each dimension and dimension item
    // TODO optimize so that org unit levels + boundary are used in query instead of fetching all org units one by one

    @Override
    public List<DimensionalObject> getDimension( String dimension, List<String> items, Date relativePeriodDate, I18nFormat format, boolean allowNull )
    {
        if ( DATA_X_DIM_ID.equals( dimension ) )
        {
            List<DimensionalObject> dataDimensions = new ArrayList<>();

            List<NameableObject> indicators = new ArrayList<>();
            List<NameableObject> dataElements = new ArrayList<>();
            List<NameableObject> dataSets = new ArrayList<>();
            List<NameableObject> operandDataElements = new ArrayList<>();

            options:
            for ( String uid : items )
            {
                Indicator in = indicatorService.getIndicator( uid );

                if ( in != null )
                {
                    indicators.add( in );
                    continue options;
                }

                DataElement de = dataElementService.getDataElement( uid );

                if ( de != null )
                {
                    dataElements.add( de );
                    continue options;
                }

                DataSet ds = dataSetService.getDataSet( uid );

                if ( ds != null )
                {
                    dataSets.add( ds );
                    continue options;
                }

                DataElementOperand dc = operandService.getDataElementOperandByUid( uid );

                if ( dc != null )
                {
                    operandDataElements.add( dc.getDataElement() );
                    continue options;
                }

                throw new IllegalQueryException( "Data dimension option identifier does not reference any option: " + uid );
            }

            if ( !indicators.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( INDICATOR_DIM_ID, DimensionType.INDICATOR, indicators ) );
            }

            if ( !dataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
            }

            if ( !dataSets.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATASET_DIM_ID, DimensionType.DATASET, dataSets ) );
            }

            if ( !operandDataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, operandDataElements ) );
                dataDimensions.add( new BaseDimensionalObject( CATEGORYOPTIONCOMBO_DIM_ID, DimensionType.CATEGORY_OPTION_COMBO, new ArrayList<NameableObject>() ) );
            }

            if ( indicators.isEmpty() && dataElements.isEmpty() && dataSets.isEmpty() && operandDataElements.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension dx is present in query without any valid dimension options" );
            }

            return dataDimensions;
        }

        if ( CATEGORYOPTIONCOMBO_DIM_ID.equals( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY_OPTION_COMBO, null, DISPLAY_NAME_CATEGORYOPTIONCOMBO, new ArrayList<NameableObject>() );

            return ListUtils.getList( object );
        }

        if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            Calendar calendar = PeriodType.getCalendar();

            Set<Period> periods = new HashSet<>();

            for ( String isoPeriod : items )
            {
                if ( RelativePeriodEnum.contains( isoPeriod ) )
                {
                    RelativePeriodEnum relativePeriod = RelativePeriodEnum.valueOf( isoPeriod );
                    List<Period> relativePeriods = RelativePeriods.getRelativePeriodsFromEnum( relativePeriod, relativePeriodDate, format, true );
                    periods.addAll( relativePeriods );
                }
                else
                {
                    Period period = PeriodType.getPeriodFromIsoString( isoPeriod );

                    if ( period != null )
                    {
                        periods.add( period );
                    }
                }
            }

            if ( periods.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension pe is present in query without any valid dimension options" );
            }

            for ( Period period : periods )
            {
                period.setName( format != null ? format.formatPeriod( period ) : null );

                if ( !calendar.isIso8601() )
                {
                    period.setUid( getLocalPeriodIdentifier( period, calendar ) );
                }
            }

            List<Period> periodList = new ArrayList<>( periods );
            Collections.sort( periodList, AscendingPeriodEndDateComparator.INSTANCE );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.PERIOD, null, DISPLAY_NAME_PERIOD, asList( periodList ) );

            return ListUtils.getList( object );
        }

        if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            User user = currentUserService.getCurrentUser();

            List<NameableObject> ous = new UniqueArrayList<>();
            List<Integer> levels = new UniqueArrayList<>();
            List<OrganisationUnitGroup> groups = new UniqueArrayList<>();

            for ( String ou : items )
            {
                if ( KEY_USER_ORGUNIT.equals( ou ) && user != null && user.hasDataViewOrganisationUnitWithFallback() )
                {
                    ous.add( user.getDataViewOrganisationUnitWithFallback() );
                }
                else if ( KEY_USER_ORGUNIT_CHILDREN.equals( ou ) && user != null && user.hasDataViewOrganisationUnitWithFallback() )
                {
                    ous.addAll( user.getDataViewOrganisationUnitWithFallback().getSortedChildren() );
                }
                else if ( KEY_USER_ORGUNIT_GRANDCHILDREN.equals( ou ) && user != null && user.hasDataViewOrganisationUnitWithFallback() )
                {
                    ous.addAll( user.getDataViewOrganisationUnitWithFallback().getSortedGrandChildren() );
                }
                else if ( ou != null && ou.startsWith( KEY_LEVEL ) )
                {
                    int level = DimensionalObjectUtils.getLevelFromLevelParam( ou );

                    if ( level > 0 )
                    {
                        levels.add( level );
                    }
                }
                else if ( ou != null && ou.startsWith( KEY_ORGUNIT_GROUP ) )
                {
                    String uid = DimensionalObjectUtils.getUidFromOrgUnitGroupParam( ou );

                    OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( uid );

                    if ( uid != null )
                    {
                        groups.add( group );
                    }
                }
                else if ( CodeGenerator.isValidCode( ou ) )
                {
                    OrganisationUnit unit = organisationUnitService.getOrganisationUnit( ou );

                    if ( unit != null )
                    {
                        ous.add( unit );
                    }
                }
            }

            List<NameableObject> orgUnits = new UniqueArrayList<>();
            List<OrganisationUnit> ousList = NameableObjectUtils.asTypedList( ous );

            if ( !levels.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevels( levels, ousList ) );
            }

            if ( !groups.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnits( groups, ousList ) );
            }

            if ( levels.isEmpty() && groups.isEmpty() )
            {
                orgUnits.addAll( ous );
            }

            if ( orgUnits.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension ou is present in query without any valid dimension options" );
            }

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT, null, DISPLAY_NAME_ORGUNIT, orgUnits );

            return ListUtils.getList( object );
        }

        if ( LONGITUDE_DIM_ID.contains( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.STATIC, null, DISPLAY_NAME_LONGITUDE, new ArrayList<NameableObject>() );

            return ListUtils.getList( object );
        }

        if ( LATITUDE_DIM_ID.contains( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.STATIC, null, DISPLAY_NAME_LATITUDE, new ArrayList<NameableObject>() );

            return ListUtils.getList( object );
        }

        OrganisationUnitGroupSet ougs = organisationUnitGroupService.getOrganisationUnitGroupSet( dimension );

        if ( ougs != null )
        {
            List<NameableObject> ous = asList( organisationUnitGroupService.getOrganisationUnitGroupsByUid( items ) );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT_GROUPSET, null, ougs.getDisplayName(), ous );

            return ListUtils.getList( object );
        }

        DataElementGroupSet degs = dataElementService.getDataElementGroupSet( dimension );

        if ( degs != null )
        {
            List<NameableObject> des = asList( dataElementService.getDataElementGroupsByUid( items ) );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.DATAELEMENT_GROUPSET, null, degs.getDisplayName(), des );

            return ListUtils.getList( object );
        }

        CategoryOptionGroupSet cogs = categoryService.getCategoryOptionGroupSet( dimension );

        if ( cogs != null )
        {
            List<NameableObject> cogz = asList( categoryService.getCategoryOptionGroupsByUid( items ) );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORYOPTION_GROUPSET, null, cogs.getDisplayName(), cogz );

            return ListUtils.getList( object );
        }

        DataElementCategory dec = categoryService.getDataElementCategory( dimension );

        if ( dec != null && dec.isDataDimension() )
        {
            List<NameableObject> decos = asList( categoryService.getDataElementCategoryOptionsByUid( items ) );

            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY, null, dec.getDisplayName(), decos );

            return ListUtils.getList( object );
        }

        if ( allowNull )
        {
            return null;
        }

        throw new IllegalQueryException( "Dimension identifier does not reference any dimension: " + dimension );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Replaces the indicator dimension including items with the data elements
     * part of the indicator expressions.
     *
     * @param params         the data query parameters.
     * @param indicatorIndex the index of the indicator dimension in the given query.
     */
    private DataQueryParams replaceIndicatorsWithDataElements( DataQueryParams params, int indicatorIndex )
    {
        List<Indicator> indicators = asTypedList( params.getIndicators() );
        List<NameableObject> dataElements = asList( expressionService.getDataElementsInIndicators( indicators ) );

        params.getDimensions().set( indicatorIndex, new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
        params.enableCategoryOptionCombos();

        return params;
    }

    /**
     * Returns a mapping between the identifier and the name of all dimension and
     * filter items for the given parameters.
     *
     * @param params the data query.
     */
    private Map<String, String> getUidNameMap( DataQueryParams params )
    {
        Map<String, String> map = new HashMap<>();
        map.putAll( getUidNameMap( params.getDimensions(), params.isHierarchyMeta(), params.getDisplayProperty() ) );
        map.putAll( getUidNameMap( params.getFilters(), params.isHierarchyMeta(), params.getDisplayProperty() ) );
        map.put( DATA_X_DIM_ID, DISPLAY_NAME_DATA_X );

        return map;
    }

    /**
     * Returns a mapping between identifiers and names for the given dimensional
     * objects.
     *
     * @param dimensions    the dimensional objects.
     * @param hierarchyMeta indicates whether to include meta data of the
     *                      organisation unit hierarchy.
     */
    private Map<String, String> getUidNameMap( List<DimensionalObject> dimensions, boolean hierarchyMeta, DisplayProperty displayProperty )
    {
        Map<String, String> map = new HashMap<>();

        for ( DimensionalObject dimension : dimensions )
        {
            List<NameableObject> items = new ArrayList<>( dimension.getItems() );

            boolean orgUnitHierarchy = hierarchyMeta && DimensionType.ORGANISATIONUNIT.equals( dimension.getDimensionType() );

            // -----------------------------------------------------------------
            // If dimension is not fixed and has no options, insert all options
            // -----------------------------------------------------------------

            if ( !FIXED_DIMS.contains( dimension.getDimension() ) && items.isEmpty() )
            {
                DimensionalObject dynamicDim = dimensionService.getDimension( dimension.getDimension(), dimension.getDimensionType() );

                items = dynamicDim != null ? dynamicDim.getItems() : items;
            }

            // -----------------------------------------------------------------
            // Insert UID and name into map
            // -----------------------------------------------------------------

            Calendar calendar = PeriodType.getCalendar();

            for ( NameableObject object : items )
            {
                if ( !calendar.isIso8601() && Period.class.isInstance( object ) )
                {
                    Period period = (Period) object;
                    DateTimeUnit dateTimeUnit = calendar.fromIso( period.getStartDate() );
                    map.put( period.getPeriodType().getIsoDate( dateTimeUnit ), period.getDisplayName() );
                }
                else
                {
                    if ( DisplayProperty.SHORTNAME.equals( displayProperty ) )
                    {
                        map.put( object.getUid(), object.getDisplayShortName() );
                    }
                    else // NAME
                    {
                        map.put( object.getUid(), object.getDisplayName() );
                    }
                }

                if ( orgUnitHierarchy )
                {
                    OrganisationUnit unit = (OrganisationUnit) object;

                    if ( DisplayProperty.SHORTNAME.equals( displayProperty ) )
                    {
                        map.putAll( NameableObjectUtils.getUidShortNameMap( unit.getAncestors() ) );
                    }
                    else // NAME
                    {
                        map.putAll( IdentifiableObjectUtils.getUidNameMap( unit.getAncestors() ) );
                    }
                }
            }

            if ( dimension.getDisplayShortName() != null && DisplayProperty.SHORTNAME.equals( displayProperty ) )
            {
                map.put( dimension.getDimension(), dimension.getDisplayShortName() );
            }
            else if ( dimension.getDisplayName() != null ) // NAME
            {
                map.put( dimension.getDimension(), dimension.getDisplayName() );
            }
        }

        return map;
    }

    /**
     * Returns a mapping between the category option combo identifiers and names
     * in the given grid.
     *
     * @param params the data query parameters.
     */
    private Map<String, String> getCocNameMap( DataQueryParams params )
    {
        Map<String, String> metaData = new HashMap<>();

        List<NameableObject> des = params.getDimensionOrFilter( DATAELEMENT_DIM_ID );

        if ( des != null && !des.isEmpty() )
        {
            Set<DataElementCategoryCombo> categoryCombos = new HashSet<>();

            for ( NameableObject de : des )
            {
                DataElement dataElement = (DataElement) de;

                if ( dataElement.getCategoryCombo() != null )
                {
                    categoryCombos.add( dataElement.getCategoryCombo() );
                }
            }

            for ( DataElementCategoryCombo cc : categoryCombos )
            {
                for ( DataElementCategoryOptionCombo coc : cc.getOptionCombos() )
                {
                    metaData.put( coc.getUid(), coc.getName() );
                }
            }
        }

        return metaData;
    }

    /**
     * Gets the number of available cores. Uses explicit number from system
     * setting if available. Detects number of cores from current server runtime
     * if not.
     */
    private int getProcessNo()
    {
        Integer cores = (Integer) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_DATABASE_SERVER_CPUS );

        return (cores == null || cores == 0) ? SystemUtils.getCpuCores() : cores;
    }

    /**
     * Converts a String, Object map into a specific String, Double map.
     *
     * @param map the map to convert.
     */
    private Map<String, Double> getDoubleMap( Map<String, Object> map )
    {
        Map<String, Double> typedMap = new HashMap<>();

        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            final Object value = entry.getValue();

            if ( value != null && Double.class.equals( value.getClass() ) )
            {
                typedMap.put( entry.getKey(), (Double) entry.getValue() );
            }
        }

        return typedMap;
    }

    /**
     * Returns the given value. If of class Double the value is rounded.
     *
     * @param value the value to return and potentially round.
     */
    private Object getRounded( Object value )
    {
        return value != null && Double.class.equals( value.getClass() ) ? MathUtils.getRounded( (Double) value ) : value;
    }
}
