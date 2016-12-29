var isOrganisationUnit = false;

function organisationUnitSelected( units )
{
    isOrganisationUnit = units && units.length > 0;
}

function getPeriods( periodTypeList, availableList, selectedList, timespan )
{
    $( "#periodId" ).removeAttr( "disabled" );

    getAvailablePeriods( periodTypeList, availableList, selectedList, timespan );
}

function displayCompleteness()
{
    var criteria = $( "input[name='criteria']:checked" ).val();
    var dataSetId = $( "#dataSetId" ).val();
    var periodList = byId( "periodId" );
    var periodId = periodList.options.length > 0 ? $( "#periodId" ).val() : null;

    if ( !completenessIsValid( periodId ) )
    {
        return false;
    }

    showLoader();

    var url = "getDataCompleteness.action" + "?periodId=" + periodId + "&criteria=" + criteria + "&dataSetId="
            + dataSetId + "&type=html";

    $( "#contentDiv" ).load( url, function()
    {
        hideLoader();
        pageInit();
    } );
}

function completenessIsValid( periodId )
{
    if ( !isOrganisationUnit )
    {
        setHeaderDelayMessage( i18n_please_select_org_unit );
        return false;
    }

    if ( periodId == null )
    {
        setHeaderDelayMessage( i18n_please_select_period );
        return false;
    }

    return true;
}

function getCompleteness( type )
{
    window.location.href = "getDataCompleteness.action?type=" + type;
}