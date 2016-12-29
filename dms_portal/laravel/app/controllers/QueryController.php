<?php

/**
 * Created by PhpStorm.
 * User: gaurav
 * Date: 17/5/14
 * Time: 5:41 PM
 */
class QueryController extends BaseController
{


    function getFilterLocations()
    {
        $groupSetID = Input::get('groupSetID');

        $frequency = Input::get('frequency');

        $groupID = Input::get('groupID');

        $queryString = ' SELECT oug.name AS \'location_group\',ou.name, ou.organisationunitid'.
                       ' FROM organisationunit ou'.
                       ' INNER JOIN orgunitgroupmembers ougm ON ougm.organisationunitid=ou.organisationunitid'.
                       ' INNER JOIN orgunitgroup oug ON oug.orgunitgroupid=ougm.orgunitgroupid'.
                       ' INNER JOIN datasetsource dss ON ou.organisationunitid=dss.sourceid'.
                       ' INNER JOIN dataset ds ON ds.datasetid=dss.datasetid'.
                       ' INNER JOIN periodtype dpt ON dpt.periodtypeid=ds.periodtypeid'.
                       ' INNER JOIN datasetmembers dsm ON ds.datasetid=dsm.datasetid'.
                       ' INNER JOIN dataelement de ON de.dataelementid=dsm.dataelementid'.
                       ' INNER JOIN dataelementattributevalues deav on deav.dataelementid = de.dataelementid'.
                       ' INNER JOIN attributevalue av on av.attributevalueid=deav.attributevalueid'.
                       ' INNER JOIN attribute fa on fa.attributeid=av.attributeid'.
                       ' INNER JOIN dataelementgroupmembers degm ON degm.dataelementid = de.dataelementid'.
                       ' INNER JOIN dataelementgroup deg ON deg.dataelementgroupid = degm.dataelementgroupid'.
                       ' INNER JOIN dataelementgroupsetmembers degsm ON degsm.dataelementgroupid=deg.dataelementgroupid'.
                       ' INNER JOIN dataelementgroupset degs ON degs.dataelementgroupsetid=degsm.dataelementgroupsetid'.
                       ' WHERE 1=1';

        if($groupID != null)
        {
            $queryString = $queryString." AND deg.dataelementgroupid = ".$groupID;
        }
        if($groupSetID != null)
        {
            $queryString = $queryString." AND degs.dataelementgroupsetid = ".$groupSetID;
        }
        if($frequency != null)
        {
            $queryString = $queryString." AND av.value = ".'\''.$frequency.'\'';
        }

        $queryString = $queryString.' GROUP BY ou.organisationunitid; ';

        //print($queryString);

        $memberElements = DB::select($queryString);

        return Response::json($memberElements)->setCallback(Input::get('callback'));

    }



    function getSubGroups()
    {
        $groupSetID = Input::get('groupSetID');

        $queryString = ' SELECT dg.name, dg.dataelementgroupid'.
                       ' FROM dataelementgroup dg'.
                       ' INNER JOIN dataelementgroupsetmembers degsm ON degsm.dataelementgroupid=dg.dataelementgroupid'.
                       ' INNER JOIN dataelementgroupset degs ON degs.dataelementgroupsetid=degsm.dataelementgroupsetid';

        if($groupSetID != null)
        {
            $queryString = $queryString." WHERE degs.dataelementgroupsetid = ".$groupSetID;
        }

        $queryString = $queryString." ORDER BY dg.name;";

        //print($queryString);

        $subGroups = DB::select($queryString);

        return Response::json($subGroups)->setCallback(Input::get('callback'));

    }

    function getFrequencyList()
    {
       $queryString = ' SELECT distinct(av.value) from dataelement de'.
                      ' INNER JOIN dataelementattributevalues deav on deav.dataelementid = de.dataelementid'.
                      ' INNER JOIN attributevalue av on av.attributevalueid=deav.attributevalueid'.
                      ' INNER JOIN attribute fa on fa.attributeid=av.attributeid'.
                      ' WHERE fa.attributeid=5'.
                      ' ORDER BY de.dataelementid;';
       
       $memberElements = DB::select($queryString);

       return Response::json($memberElements)->setCallback(Input::get('callback'));

    }

    function getItemList()
    {
        /*---Query to fetch filtered items---*/

        $groupSetID = Input::get('groupSetID');

        $frequency = Input::get('frequency');

        $groupID = Input::get('groupID');

        $locations = Input::get('locations');

        if($locations=='null' or $locations=='undefined')
        {
            $locations = null;
        }

        $queryString = ' SELECT CONCAT(de.name, "(", deco.name, ")") as \'name\', pt.name AS \'frequency\', degs.dataelementgroupsetid AS \'groupset\','.
                       '  de.dataelementid, deco.categoryoptionid'.
                       ' FROM dataelement de'.
                       ' INNER JOIN dataelementattributevalues deav on deav.dataelementid = de.dataelementid'.
                       ' INNER JOIN attributevalue av on av.attributevalueid=deav.attributevalueid'.
                       ' INNER JOIN attribute fa on fa.attributeid=av.attributeid'.
                       ' INNER JOIN dataelementcategory dc ON dc.categoryid=de.categorycomboid'.
                       ' INNER JOIN categories_categoryoptions cco ON dc.categoryid=cco.categoryid'.
                       ' INNER JOIN dataelementcategoryoption deco ON cco.categoryoptionid=deco.categoryoptionid'.
                       ' INNER JOIN datasetmembers dsm ON dsm.dataelementid=de.dataelementid'.
                       ' INNER JOIN dataset ds ON dsm.datasetid=ds.datasetid'.
                       ' INNER JOIN datasetsource dss ON ds.datasetid=dss.datasetid'.
                       ' INNER JOIN organisationunit ou ON ou.organisationunitid=dss.sourceid'.
                       ' INNER JOIN periodtype pt ON pt.periodtypeid = ds.periodtypeid'.
                       ' INNER JOIN dataelementgroupmembers degm ON degm.dataelementid=de.dataelementid'.
                       ' INNER JOIN dataelementgroup deg ON deg.dataelementgroupid=degm.dataelementgroupid'.
                       ' INNER JOIN dataelementgroupsetmembers degsm ON degsm.dataelementgroupid=deg.dataelementgroupid'.
                       ' INNER JOIN dataelementgroupset degs ON degs.dataelementgroupsetid=degsm.dataelementgroupsetid'.
                       ' WHERE de.valuetype=\'int\''.
                       ' AND fa.attributeid = 5';


        if($groupID != null)
        {
            $queryString = $queryString." AND deg.dataelementgroupid = ".$groupID;
        }
        if($groupSetID != null)
        {
            $queryString = $queryString." AND degs.dataelementgroupsetid = ".$groupSetID;
        }
        if($frequency != null)
        {
            $queryString = $queryString." AND av.value = ".'\''.$frequency.'\'';
        }
        if($locations != null)
        {
            $queryString = $queryString." AND ou.organisationunitid IN (".$locations.")";
        }

        $queryString = $queryString." GROUP BY de.dataelementid, deco.categoryoptionid ORDER BY de.name;";

        //print($queryString);

        $memberElements = DB::select($queryString);

        return Response::json($memberElements)->setCallback(Input::get('callback'));

    }


    function getItemListTest()
    {
        /*---Query to fetch filtered items---*/

        $groupSetID = Input::get('groupSetID');

        $frequency = Input::get('frequency');

        $groupID = Input::get('groupID');

        $locations = Input::get('locations');

        if($locations=='null' or $locations=='undefined')
        {
            $locations = null;
        }

        $queryString = ' SELECT CONCAT(de.name, "(", deco.name, ")") as \'name\', pt.name AS \'frequency\', degs.dataelementgroupsetid AS \'groupset\', ou.organisationunitid AS \'location\', '.
            '  de.dataelementid, deco.categoryoptionid'.
            ' FROM dataelement de'.
            ' INNER JOIN dataelementcategory dc ON dc.categoryid=de.categorycomboid'.
            ' INNER JOIN categories_categoryoptions cco ON dc.categoryid=cco.categoryid'.
            ' INNER JOIN dataelementcategoryoption deco ON cco.categoryoptionid=deco.categoryoptionid'.
            ' INNER JOIN datasetmembers dsm ON dsm.dataelementid=de.dataelementid'.
            ' INNER JOIN dataset ds ON dsm.datasetid=ds.datasetid'.
            ' INNER JOIN datasetsource dss ON ds.datasetid=dss.datasetid'.
            ' INNER JOIN organisationunit ou ON ou.organisationunitid=dss.sourceid'.
            ' INNER JOIN periodtype pt ON pt.periodtypeid = ds.periodtypeid'.
            ' INNER JOIN dataelementgroupmembers degm ON degm.dataelementid=de.dataelementid'.
            ' INNER JOIN dataelementgroup deg ON deg.dataelementgroupid=degm.dataelementgroupid'.
            ' INNER JOIN dataelementgroupsetmembers degsm ON degsm.dataelementgroupid=deg.dataelementgroupid'.
            ' INNER JOIN dataelementgroupset degs ON degs.dataelementgroupsetid=degsm.dataelementgroupsetid'.
            ' WHERE de.valuetype=\'int\'';


        if($groupID != null)
        {
            $queryString = $queryString." AND deg.dataelementgroupid = ".$groupID;
        }
        if($groupSetID != null)
        {
            $queryString = $queryString." AND degs.dataelementgroupsetid = ".$groupSetID;
        }
        if($frequency != null)
        {
            $queryString = $queryString." AND pt.name = ".'\''.$frequency.'\'';
        }
        if($locations != null)
        {
            $queryString = $queryString." AND ou.organisationunitid IN (".$locations.")";
        }

        $queryString = $queryString." GROUP BY de.dataelementid, deco.categoryoptionid  ORDER BY de.name;";

        print($queryString);

        $memberElements = DB::select($queryString);

        //return Response::json($memberElements)->setCallback(Input::get('callback'));

    }



    function getDataLocations()
    {
        $dataElementID = Input::get('dataElementID');

        /*---Query to fetch data bearing locations for the supplied 'dataelements'---*/

        $queryString = 'SELECT de.name,de.dataelementid, ou.name,ou.organisationunitid' .
            ' FROM dataelement de' .
            ' INNER JOIN datavalue dv ON dv.dataelementid = de.dataelementid' .
            ' INNER JOIN organisationunit ou ON dv.sourceid=ou.organisationunitid' .
            ' WHERE de.dataelementid IN (?)' .
            ' AND de.valuetype=\'int\'' .
            ' GROUP BY de.dataelementid, ou.organisationunitid;';


        $groupSetMemberElements = DB::select($queryString, array($dataElementID));

        return Response::json($groupSetMemberElements)->setCallback(Input::get('callback'));
    }

    function getDateRange()
    {
        $dataElementID = Input::get('dataElementID');

        $locations = Input::get('locations');

        if($locations=='null' or $locations=='undefined')
        {
            $locations = null;
        }

        /*---Query to fetch date range for the supplied 'dataelements'---*/

        $queryString = 'SELECT de.name,de.dataelementid, MIN(p.startdate) AS \'startdate\', MAX(p.startdate) AS \'enddate\' , pt.name AS \'Period-Type\''.
                       ' FROM dataelement de'.
                       ' INNER JOIN datavalue dv ON dv.dataelementid = de.dataelementid'.
                       ' INNER JOIN organisationunit ou ON dv.sourceid=ou.organisationunitid'.
                       ' INNER JOIN period p ON dv.periodid = p.periodid'.
                       ' INNER JOIN periodtype pt ON p.periodtypeid=pt.periodtypeid';

        if($dataElementID != null)
        {
            $queryString = $queryString.' WHERE de.dataelementid IN ('.$dataElementID.')';
        }
        if($locations != null)
        {
            $queryString = $queryString.' AND ou.organisationunitid in ('.$locations.')';
        }

        $queryString = $queryString.' GROUP BY de.dataelementid, ou.organisationunitid;';

        //print($queryString);

        $getRangeObject = DB::select($queryString);

        return Response::json($getRangeObject)->setCallback(Input::get('callback'));
    }

    function getData()
    {


        $dataElementID = Input::get('dataElementID');

        $locations = Input::get('locations');

        $categoryID = Input::get('categoryID');

        $startDate = Input::get('startDate');

        $endDate = Input::get('endDate');

        if($locations=='null' or $locations=='undefined')
        {
            $locations = null;
        }

        //print('LOG: GET::DATA[ '.$dataElementID.":".$categoryID.":".$locations.":".$startDate.":".$endDate." ]");

        /*---Query to fetch date range for the supplied 'dataelements'---*/

        $queryString = ' SELECT CONCAT(de.name, "(", deco.name, ")") as \'name\',dp.startdate, ou.name \'location\', dv.value, av.value AS \'unit\''.
                       ' FROM dataelement de'.
                       ' INNER JOIN dataelementcategory dc ON dc.categoryid=de.categorycomboid'.
                       ' INNER JOIN categories_categoryoptions cco ON dc.categoryid=cco.categoryid'.
                       ' INNER JOIN dataelementcategoryoption deco ON cco.categoryoptionid=deco.categoryoptionid'.
                       ' INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid'.
                       ' INNER JOIN period dp ON dp.periodid=dv.periodid'.
                       ' INNER JOIN organisationunit ou ON dv.sourceid=ou.organisationunitid'.
                       ' INNER JOIN dataelementattributevalues deav ON deav.dataelementid=de.dataelementid'.
                       ' INNER JOIN attributevalue av ON av.attributevalueid=deav.attributevalueid'.
                       ' INNER JOIN attribute a ON a.attributeid=av.attributeid'.
                       ' WHERE de.dataelementid = '. $dataElementID .' AND dv.categoryoptioncomboid = '.$categoryID.
                       ' AND a.attributeid=1';


        if($locations != null)
        {
            $queryString = $queryString.' AND ou.organisationunitid IN ('.$locations.')';
        }

        $queryString = $queryString.' AND (dp.startdate BETWEEN \''.$startDate.'\' AND \''.$endDate.'\')'.
            ' GROUP BY dv.dataelementid,dv.categoryoptioncomboid, dv.periodid, dv.sourceid'.
            ' ORDER BY dp.startdate, ou.name;';

        //print($queryString);

        $getValueList = DB::select($queryString);

        return Response::json($getValueList)->setCallback(Input::get('callback'));
    }

} 
