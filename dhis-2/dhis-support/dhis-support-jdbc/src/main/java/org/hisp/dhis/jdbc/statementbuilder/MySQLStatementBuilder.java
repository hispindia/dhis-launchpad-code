package org.hisp.dhis.jdbc.statementbuilder;

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

import static org.hisp.dhis.system.util.DateUtils.getSqlDateString;

import java.util.List;

import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id: MySQLStatementBuilder.java 5715 2008-09-17 14:05:28Z larshelg $
 */
public class MySQLStatementBuilder
    extends AbstractStatementBuilder
{
    public String getDoubleColumnType()
    {
        return "DECIMAL";
    }

    public String getPeriodIdentifierStatement( Period period )
    {
        return
            "SELECT periodid FROM period WHERE periodtypeid=" + period.getPeriodType().getId() + " " + 
            "AND startdate='" + getSqlDateString( period.getStartDate() ) + "' " +
            "AND enddate='" + getSqlDateString( period.getEndDate() ) + "'";
    }    

    public String getDeleteZeroDataValues()
    {
        return
            "DELETE FROM datavalue " +
            "USING datavalue, dataelement " +
            "WHERE datavalue.dataelementid = dataelement.dataelementid " +
            "AND dataelement.aggregationtype = 'sum' " +
            "AND datavalue.value = '0'";
    }
    
    public int getMaximumNumberOfColumns()
    {
        return 720;
    }

    public String getDropDatasetForeignKeyForDataEntryFormTable()
    {
        return  "ALTER TABLE dataentryform DROP FOREIGN KEY fk_dataentryform_datasetid;" ;
    }

    @Override
    public String getMoveDataValueToDestination( int sourceId, int destinationId )
    {
        return "UPDATE datavalue AS d1 SET sourceid=" + destinationId + " " + "WHERE sourceid=" + sourceId + " "
        + "AND NOT EXISTS ( " + "SELECT * from ( SELECT * FROM datavalue ) AS d2 " + "WHERE d2.sourceid=" + destinationId + " "
        + "AND d1.dataelementid=d2.dataelementid " + "AND d1.periodid=d2.periodid "
        + "AND d1.categoryoptioncomboid=d2.categoryoptioncomboid );";
    }

    @Override
    public String getSummarizeDestinationAndSourceWhereMatching( int sourceId, int destId )
    {
        return "UPDATE datavalue AS d1 SET value=( " + "SELECT SUM( value ) " + "FROM (SELECT * FROM datavalue) as d2 "
            + "WHERE d1.dataelementid=d2.dataelementid " + "AND d1.periodid=d2.periodid "
            + "AND d1.categoryoptioncomboid=d2.categoryoptioncomboid " + "AND d2.sourceid IN ( " + destId + ", "
            + sourceId + " ) ) " + "WHERE d1.sourceid=" + destId + " "
            + "AND d1.dataelementid in ( SELECT dataelementid FROM dataelement WHERE valuetype='int' );";
    }

    @Override
    public String getUpdateDestination( int destDataElementId, int destCategoryOptionComboId,
        int sourceDataElementId, int sourceCategoryOptionComboId )
    {
        
        return "UPDATE datavalue d1 LEFT JOIN datavalue d2 ON d2.dataelementid = " + destDataElementId
            + " AND d2.categoryoptioncomboid = " + destCategoryOptionComboId
            + " AND d1.periodid = d2.periodid AND d1.sourceid = d2.sourceid SET d1.dataelementid = "
            + destDataElementId + ", d1.categoryoptioncomboid = " + destCategoryOptionComboId
            + " WHERE d1.dataelementid = " + sourceDataElementId + " AND d1.categoryoptioncomboid = "
            + sourceCategoryOptionComboId + " AND d2.dataelementid IS NULL";

    }

    @Override
    public String getMoveFromSourceToDestination( int destDataElementId, int destCategoryOptionComboId,
        int sourceDataElementId, int sourceCategoryOptionComboId )
    {
        return "UPDATE datavalue d1, datavalue d2 SET d1.value=d2.value,d1.storedby=d2.storedby,d1.lastupdated=d2.lastupdated,d1.comment=d2.comment,d1.followup=d2.followup "
            + "WHERE d1.periodid=d2.periodid "
            + "AND d1.sourceid=d2.sourceid "
            + "AND d1.lastupdated<d2.lastupdated "
            + "AND d1.dataelementid="
            + destDataElementId
            + " AND d1.categoryoptioncomboid="
            + destCategoryOptionComboId
            + " "
            + "AND d2.dataelementid="
            + sourceDataElementId
            + " AND d2.categoryoptioncomboid=" + sourceCategoryOptionComboId + ";";
    }
    
    public String getStandardDeviation( int dataElementId, int categoryOptionComboId, int organisationUnitId ){
    	
    	return "SELECT STDDEV( value ) FROM datavalue " +
            "WHERE dataelementid='" + dataElementId + "' " +
            "AND categoryoptioncomboid='" + categoryOptionComboId + "' " +
            "AND sourceid='" + organisationUnitId + "'";
        
    }
    
    public String getAverage( int dataElementId, int categoryOptionComboId, int organisationUnitId ){
    	 return  "SELECT AVG( value ) FROM datavalue " +
            "WHERE dataelementid='" + dataElementId + "' " +
            "AND categoryoptioncomboid='" + categoryOptionComboId + "' " +
            "AND sourceid='" + organisationUnitId + "'";
    }
    
    public String getDeflatedDataValues( int dataElementId, String dataElementName, int categoryOptionComboId,
    		String periodIds, int organisationUnitId, String organisationUnitName, int lowerBound, int upperBound ){
    	
    	return  "SELECT dv.dataelementid, dv.periodid, dv.sourceid, dv.categoryoptioncomboid, dv.value, dv.storedby, dv.lastupdated, " +
            "dv.comment, dv.followup, '" + lowerBound + "' AS minvalue, '" + upperBound + "' AS maxvalue, " +
            encode( dataElementName ) + " AS dataelementname, pt.name AS periodtypename, pe.startdate, pe.enddate, " + 
            encode( organisationUnitName ) + " AS sourcename, cc.categoryoptioncomboname " +
            "FROM datavalue AS dv " +
            "JOIN period AS pe USING (periodid) " +
            "JOIN periodtype AS pt USING (periodtypeid) " +
            "LEFT JOIN _categoryoptioncomboname AS cc USING (categoryoptioncomboid) " +
            "WHERE dv.dataelementid='" + dataElementId + "' " +
            "AND dv.categoryoptioncomboid='" + categoryOptionComboId + "' " +
            "AND dv.periodid IN (" + periodIds + ") " +
            "AND dv.sourceid='" + organisationUnitId + "' " +
            "AND ( dv.value < '" + lowerBound + "' " +
            "OR  dv.value > '" + upperBound + "' )";
   }
   
    public String archiveData( String startDate, String endDate )
    {
        return "DELETE d FROM datavalue AS d " +
             "INNER JOIN period as p " +
             "WHERE d.periodid=p.periodid " +
             "AND p.startdate>='" + startDate + "' " +
             "AND p.enddate<='" + endDate + "'";
    }
    
    public String unArchiveData( String startDate, String endDate )
    {    
        return "DELETE a FROM datavaluearchive AS a " +
            "INNER JOIN period AS p " +
            "WHERE a.periodid=p.periodid " +
            "AND p.startdate>='" + startDate + "' " +
            "AND p.enddate<='" + endDate + "'";
    }
    
    public String deleteRegularOverlappingData()
    {    
        return "DELETE d FROM datavalue AS d " +
            "INNER JOIN datavaluearchive AS a " +
            "WHERE d.dataelementid=a.dataelementid " +
            "AND d.periodid=a.periodid " +
            "AND d.sourceid=a.sourceid " +
            "AND d.categoryoptioncomboid=a.categoryoptioncomboid";

    }
    
    public String deleteArchivedOverlappingData()
    {
        return "DELETE a FROM datavaluearchive AS a " +
            "INNER JOIN datavalue AS d " +
            "WHERE a.dataelementid=d.dataelementid " +
            "AND a.periodid=d.periodid " +
            "AND a.sourceid=d.sourceid " +
            "AND a.categoryoptioncomboid=d.categoryoptioncomboid";
    }
    
    public String deleteOldestOverlappingDataValue()
    {    
        return "DELETE d FROM datavalue AS d " +
            "INNER JOIN datavaluearchive AS a " +
            "WHERE d.dataelementid=a.dataelementid " +
            "AND d.periodid=a.periodid " +
            "AND d.sourceid=a.sourceid " +
            "AND d.categoryoptioncomboid=a.categoryoptioncomboid " +
            "AND d.lastupdated<a.lastupdated";
    }
    
    public String deleteOldestOverlappingArchiveData()
    {       
        return "DELETE a FROM datavaluearchive AS a " +
            "INNER JOIN datavalue AS d " +
            "WHERE a.dataelementid=d.dataelementid " +
            "AND a.periodid=d.periodid " +
            "AND a.sourceid=d.sourceid " +
            "AND a.categoryoptioncomboid=d.categoryoptioncomboid " +
            "AND a.lastupdated<=d.lastupdated";
    }
    
    public String archivePatientData ( String startDate, String endDate )
    {
        return "DELETE pdv FROM patientdatavalue AS pdv "
            + "INNER JOIN programstageinstance AS psi "
            +    "ON pdv.programstageinstanceid = psi.programstageinstanceid "
            + "INNER JOIN programinstance AS pi "
            +    "ON pi.programinstanceid = psi.programinstanceid "
            + "WHERE pi.enddate >= '" + startDate + "' "
            +    "AND pi.enddate <= '" +  endDate + "';";
    }
   
    public String unArchivePatientData ( String startDate, String endDate )
    {
        return "DELETE pdv FROM patientdatavaluearchive AS pdv "
            + "INNER JOIN programstageinstance AS psi "
            +    "ON pdv.programstageinstanceid = psi.programstageinstanceid "
            + "INNER JOIN programinstance AS pi "
            +    "ON pi.programinstanceid = psi.programinstanceid "
            + "WHERE pi.enddate >= '" + startDate + "' "
            +    "AND pi.enddate <= '" +  endDate + "';";
    }
   
    public String deleteRegularOverlappingPatientData()
    {
        return "DELETE d FROM patientdatavalue AS d " +
            "INNER JOIN patientdatavaluearchive AS a " +
            "WHERE d.programstageinstanceid=a.programstageinstanceid " +
            "AND d.dataelementid=a.dataelementid " +
            "AND d.organisationunitid=a.organisationunitid; " ;
    }
   
    public String deleteArchivedOverlappingPatientData()
    {
        return "DELETE a FROM patientdatavaluearchive AS a " +
            "INNER JOIN patientdatavalue AS d " +
            "WHERE d.programstageinstanceid=a.programstageinstanceid " +
            "AND d.dataelementid=a.dataelementid " +
            "AND d.organisationunitid=a.organisationunitid ";
    }
   
    public String deleteOldestOverlappingPatientDataValue()
    {
        return "DELETE d FROM patientdatavalue AS d " +
            "INNER JOIN patientdatavaluearchive AS a " +
            "WHERE d.programstageinstanceid=a.programstageinstanceid " +
            "AND d.dataelementid=a.dataelementid " +
            "AND d.organisationunitid=a.organisationunitid " +
            "AND d.timestamp<a.timestamp;";
    }
   
    public String deleteOldestOverlappingPatientArchiveData()
    {
        return "DELETE a FROM patientdatavaluearchive AS a " +
            "INNER JOIN patientdatavalue AS d " +
            "WHERE d.programstageinstanceid=a.programstageinstanceid " +
            "AND d.dataelementid=a.dataelementid " +
            "AND d.organisationunitid=a.organisationunitid " +
            "AND a.timestamp<=d.timestamp;";
    }

    public String getPatientsByFullName( String fullName )
    {
        return "SELECT patientid FROM patient " +
            "where lower(concat( firstname, \" \",middleName , \" \" , lastname) ) " +
            "like lower('%" + fullName + "%') ";
    }
   
    public String getPatientsByFullName( String fullName, int min, int max )
    {
        return "SELECT patientid FROM patient " +
            "where lower(concat( firstname, \" \",middleName , \" \" , lastname) ) " +
            "like lower('%" + fullName + "%') " +
            "limit " + min + " ," + max;
    }
   
    public String countPatientsByFullName( String fullName )
    {
        return "SELECT count(patientid) FROM patient " +
            "where lower(concat( firstname, \" \",middleName , \" \" , lastname) ) " +
            "like lower('%" + fullName + "%')";
    }

    public String queryDataElementStructureForOrgUnit()
    {
        StringBuffer sqlsb = new StringBuffer();
        sqlsb.append( "(SELECT DISTINCT de.dataelementid, concat(de.name, \" \", cc.categoryoptioncomboname) AS DataElement " );
        sqlsb.append( "FROM dataelement AS de " );
        sqlsb.append( "INNER JOIN categorycombos_optioncombos cat_opts on de.categorycomboid = cat_opts.categorycomboid ");
        sqlsb.append( "INNER JOIN _categoryoptioncomboname cc on cat_opts.categoryoptioncomboid = cc.categoryoptioncomboid ");
        sqlsb.append( "ORDER BY DataElement) " );
        return sqlsb.toString();
    }

    public String queryRawDataElementsForOrgUnitBetweenPeriods(Integer orgUnitId, List<Integer> betweenPeriodIds)
    {
        StringBuffer sqlsb = new StringBuffer();

        int i = 0;
        for ( Integer periodId : betweenPeriodIds )
        {
            i++;

            sqlsb.append( "SELECT de.dataelementid, concat(de.name, \" \" , cc.categoryoptioncomboname) AS DataElement, dv.value AS counts_of_aggregated_values, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement AS de " );
            sqlsb.append( "INNER JOIN datavalue AS dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "INNER JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "INNER JOIN categorycombos_optioncombos cat_opts on de.categorycomboid = cat_opts.categorycomboid ");
            sqlsb.append( "INNER JOIN _categoryoptioncomboname cc on cat_opts.categoryoptioncomboid = cc.categoryoptioncomboid ");
            sqlsb.append( "WHERE dv.sourceid = " + orgUnitId + " " );
            sqlsb.append( "AND dv.periodid = " + periodId + " " );

            sqlsb.append( i == betweenPeriodIds.size() ? "ORDER BY ColumnHeader,dataelement" : " UNION " );
        }
        return sqlsb.toString();
    }
    
    public String getActivityPlan( int orgunitId, int min, int max )
    {
        return  "SELECT psi.programstageinstanceid " +
                "FROM programstageinstance psi " +
                    "INNER JOIN programinstance pi " +
                        "ON pi.programinstanceid = psi.programinstanceid " +
                    "INNER JOIN programstage ps " +
                        "ON ps.programstageid=psi.programstageid " +
                    "INNER JOIN program_organisationunits po " +
                        "ON po.programid=pi.programid " +
                 "WHERE pi.completed = FALSE  " +
                        "AND po.organisationunitid = " + orgunitId + " AND psi.completed = FALSE " +
                        "AND ps.stageinprogram in ( SELECT min(ps1.stageinprogram) " +
                            "FROM programstageinstance psi1 " +
                            "INNER JOIN programinstance pi1 " +
                                "ON pi1.programinstanceid = psi1.programinstanceid " +
                            "INNER JOIN programstage ps1 " +
                                "ON ps1.programstageid=psi1.programstageid " +
                            "INNER JOIN program_organisationunits po1 " +
                                "ON po1.programid=pi1.programid " +
                            "WHERE pi1.completed = FALSE  " +
                                "AND po1.organisationunitid = " + orgunitId + " AND psi1.completed = FALSE ) " +
                 "ORDER BY ps.stageinprogram " +
                 "LIMIT " + min + " ," + max;
    }
    
    public String limitRecord( int min, int max )
    {
        return " LIMIT " + min + " ," + max;
    }
}
