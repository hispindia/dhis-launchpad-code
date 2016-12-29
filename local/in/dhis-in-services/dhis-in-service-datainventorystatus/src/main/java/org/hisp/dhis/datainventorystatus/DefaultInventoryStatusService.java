package org.hisp.dhis.datainventorystatus;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.JDBCException;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datainventorystatus.InventoryStatusService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueService;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultInventoryStatusService
    implements InventoryStatusService
{
    private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    // -------------------------------------------------------------------------
    // Get Query Output
    // -------------------------------------------------------------------------

    public void getQueryOutput()
    {
        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        try
        {
            String query = "SELECT INV_MD.GroupSetName, INV_MD.GroupName, INV_MD.Dataelement, INV_MD.Frequency, INV_MD.DataSet, INV_MD.FromDate, INV_MD.ToDate, INV_MD.TotalRecords\n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT DEGS.name AS 'GroupSetName', DEG.name AS 'GroupName', DE.name AS 'Dataelement', DPT.name AS 'Frequency', DS.name AS 'DataSet', MIN(DP.startdate) AS 'FromDate', MAX(DP.enddate) AS 'ToDate', COUNT(DV.value) AS 'TotalRecords'\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN datavalue DV ON DE.dataelementid=DV.dataelementid\n" +
                    "LEFT JOIN datasetmembers DSM ON DSM.dataelementid=DE.dataelementid\n" +
                    "INNER JOIN dataset DS ON DS.datasetid = DSM.datasetid AND DS.datasetid IN (1,2,3,4)\n" +
                    "LEFT JOIN period DP ON DP.periodid=DV.periodid\n" +
                    "LEFT JOIN dataelementgroupmembers DEGM ON DEGM.dataelementid = DE.dataelementid\n" +
                    "LEFT JOIN dataelementgroup DEG ON DEG.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupsetmembers DEGSM ON DEGSM.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupset DEGS ON DEGS.dataelementgroupsetid=DEGSM.dataelementgroupsetid\n" +
                    "LEFT JOIN datasetsource DSS ON DSS.datasetid=DS.datasetid\n" +
                    "LEFT JOIN organisationunit OU ON OU.organisationunitid=DSS.sourceid\n" +
                    "LEFT JOIN periodtype DPT ON DPT.periodtypeid=DP.periodtypeid\n" +
                    "LEFT JOIN dataelementattributevalues DEAV ON DEAV.dataelementid=DE.dataelementid\n" +
                    "LEFT JOIN attributevalue DAV ON DAV.attributevalueid=DEAV.attributevalueid\n" +
                    "LEFT JOIN attribute DA ON DAV.attributeid=DA.attributeid\n" +
                    "GROUP BY DE.dataelementid) INV_MD";

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet( query );

            while ( sqlRowSet.next() )
            {
                System.out.println(sqlRowSet);
            }
        }
        catch (JDBCException jdbcException)
        {
            System.out.println(jdbcException);
        }
    }
}
