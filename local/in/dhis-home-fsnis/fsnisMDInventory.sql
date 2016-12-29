DROP TABLE IF EXISTS _mdinventory;
CREATE TABLE _mdinventory AS
SELECT *
FROM
(
SELECT INV_MD.GroupSetName, INV_MD.GroupName, INV_MD.dataelementid AS 'DE ID', INV_MD.Dataelement, INV_Category.Category, INV_MD.Frequency, INV_MD.DataSet, INV_MD.FromDate, INV_MD.ToDate, INV_MD.TotalRecords, INV_Source.Source, INV_Unit.Unit
FROM
(
SELECT DE.dataelementid AS dataelementid,DEGS.name AS 'GroupSetName', DEG.name AS 'GroupName', DE.name AS 'Dataelement', DPT.name AS 'Frequency', DS.name AS 'DataSet', MIN(DP.startdate) AS 'FromDate', MAX(DP.enddate) AS 'ToDate', COUNT(DV.value) AS 'TotalRecords', DV.categoryoptioncomboid
FROM dataelement DE
LEFT JOIN datavalue DV ON DE.dataelementid=DV.dataelementid
LEFT JOIN datasetmembers DSM ON DSM.dataelementid=DV.dataelementid
LEFT JOIN dataset DS ON DS.datasetid = DSM.datasetid
LEFT JOIN period DP ON DP.periodid=DV.periodid
LEFT JOIN periodtype DPT ON DPT.periodtypeid=DP.periodtypeid
LEFT JOIN dataelementgroupmembers DEGM ON DEGM.dataelementid = DV.dataelementid
LEFT JOIN dataelementgroup DEG ON DEG.dataelementgroupid=DEGM.dataelementgroupid
LEFT JOIN dataelementgroupsetmembers DEGSM ON DEGSM.dataelementgroupid=DEGM.dataelementgroupid
LEFT JOIN dataelementgroupset DEGS ON DEGS.dataelementgroupsetid=DEGSM.dataelementgroupsetid
LEFT JOIN organisationunit OU ON OU.organisationunitid=DV.sourceid
GROUP BY DV.dataelementid, DV.categoryoptioncomboid
) INV_MD
LEFT JOIN
(
SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CCN.categoryoptioncomboname AS 'Category'
FROM dataelement DE
LEFT JOIN _dataelementcategoryoptioncombo DECC ON DECC.dataelementuid=DE.uid
LEFT JOIN categoryoptioncombo CC ON CC.uid=DECC.categoryoptioncombouid
LEFT JOIN _categoryoptioncomboname CCN ON CCN.categoryoptioncomboid=CC.categoryoptioncomboid
GROUP BY DE.name, CCN.categoryoptioncomboname
) INV_Category ON INV_MD.dataelement=INV_Category.Dataelement
LEFT JOIN
(
SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CASE WHEN AV.attributeid=2 THEN AV.value END AS 'Source'
FROM dataelement DE
LEFT JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid
LEFT JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid
) INV_Source ON INV_MD.Dataelement=INV_Source.Dataelement
LEFT JOIN
(
SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', AV.value AS 'Unit'
FROM dataelement DE
INNER JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid
INNER JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid
WHERE AV.attributeid=1
) INV_Unit ON INV_MD.Dataelement=INV_Unit.Dataelement
GROUP BY INV_MD.dataelementid, INV_Category.Category
ORDER BY INV_MD.dataelementid
) INVA_MD;
