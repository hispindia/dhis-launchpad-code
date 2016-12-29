
var currentPeriodOffset = 0;
var periodTypeFactory = new PeriodType();

//------------------------------------------------------------------------------
//Get and set methods
//------------------------------------------------------------------------------

function getReportCard()
{
 var reportCard = {     
     periodType: "Yearly",
     period: $( "#periodId" ).val(),
     orgUnit: selectionTreeSelection.getSelectedUid()[0],
     offset: currentPeriodOffset
 };
 
 return reportCard;
}

function setReportCard( reportCard )
{
	$( "#periodType" ).val( reportCard.periodType );
	
	currentPeriodOffset = reportCard.offset;
	
	displayPeriods();
	$( "#periodId" ).val( reportCard.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
		
	$( "body" ).on( "oust.selected", function() {
		$( "body" ).off( "oust.selected" );
		validateReportCardForm();
	} );
}

//------------------------------------------------------------------------------
//Period
//------------------------------------------------------------------------------

function displayPeriods()
{
	var periodType = "Yearly";
	var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	periods = periodTypeFactory.reverse( periods );
	periods = periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

	$( "#periodId" ).removeAttr( "disabled" );
	clearListById( "periodId" );

	for ( i in periods )
	{
		addOptionById( "periodId", periods[i].iso, periods[i].name );
	}
}

function displayNextPeriods()
{
	if ( currentPeriodOffset < 0 ) // Cannot display future periods
	{
		currentPeriodOffset++;
		displayPeriods();
	}
}

function displayPreviousPeriods()
{
	currentPeriodOffset--;
	displayPeriods();
}

//------------------------------------------------------------------------------
//Run report
//------------------------------------------------------------------------------

function validateReportCardForm()
{
	var reportCard = getDataSetReport();
	 
	if ( !reportCard.period )
	{
		setHeaderMessage( i18n_select_period );
		return false;
	}
	if ( !selectionTreeSelection.isSelected() )
	{
		setHeaderMessage( i18n_select_organisation_unit );
		return false;
	}
 
	hideHeaderMessage();
	hideCriteria();
	hideContent();
	showLoader();
	
	var currentParams = { pe: reportCard.period, ou: dataSetReport.orgUnit };
 
	$.get( 'generateDataSetReport.action', currentParams, function( data ) {
 	$( '#content' ).html( data );
 	hideLoader();
 	showContent();
 	setTableStyles();
	} );
}

var currentPeriodOffset = 0;
var periodTypeFactory = new PeriodType();

//------------------------------------------------------------------------------
//Get and set methods
//------------------------------------------------------------------------------

function getReportCard()
{
 var reportCard = {     
     periodType: "Yearly",
     period: $( "#periodId" ).val(),
     orgUnit: selectionTreeSelection.getSelectedUid()[0],
     offset: currentPeriodOffset
 };
 
 return reportCard;
}

function setReportCard( reportCard )
{
	$( "#periodType" ).val( reportCard.periodType );
	
	currentPeriodOffset = reportCard.offset;
	
	displayPeriods();
	$( "#periodId" ).val( reportCard.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
		
	$( "body" ).on( "oust.selected", function() {
		$( "body" ).off( "oust.selected" );
		validateReportCardForm();
	} );
}


//------------------------------------------------------------------------------
//Period
//------------------------------------------------------------------------------

function displayPeriods()
{
	var periodType = "Yearly";
	var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	periods = periodTypeFactory.reverse( periods );
	periods = periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

	$( "#periodId" ).removeAttr( "disabled" );
	clearListById( "periodId" );

	for ( i in periods )
	{
		addOptionById( "periodId", periods[i].iso, periods[i].name );
	}
}

function displayNextPeriods()
{
	if ( currentPeriodOffset < 0 ) // Cannot display future periods
	{
		currentPeriodOffset++;
		displayPeriods();
	}
}

function displayPreviousPeriods()
{
	currentPeriodOffset--;
	displayPeriods();
}

//------------------------------------------------------------------------------
//Run report
//------------------------------------------------------------------------------

function validateReportCardForm()
{
	var reportCard = getDataSetReport();
	 
	if ( !reportCard.period )
	{
		setHeaderMessage( i18n_select_period );
		return false;
	}
	if ( !selectionTreeSelection.isSelected() )
	{
		setHeaderMessage( i18n_select_organisation_unit );
		return false;
	}
 
	hideHeaderMessage();
	hideCriteria();
	hideContent();
	showLoader();
	
	var currentParams = { pe: reportCard.period, ou: dataSetReport.orgUnit };
 
	$.get( 'generateDataSetReport.action', currentParams, function( data ) {
 	$( '#content' ).html( data );
 	hideLoader();
 	showContent();
 	setTableStyles();
	} );
}


function getIndicators()
{
    var indicatorGroupList = byId( "indicatorGroupId" );
    var indicatorGroupId = indicatorGroupList.options[indicatorGroupList.selectedIndex].value;

    if ( indicatorGroupId != null )
    {
		$.get( '../dhis-web-commons-ajax/getIndicators.action',
		{
			id: indicatorGroupId
		},getIndicatorsReceived );
    }
}


function getIndicatorsReceived( xmlObject )
{
    var availableIndicators = document.getElementById( "availableIndicators" );
    var selectedIndicators = document.getElementById( "selectedIndicators" );

    clearList( availableIndicators );

    var indicators = xmlObject.getElementsByTagName( "indicator" );

    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[i].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[i].getElementsByTagName( "name" )[0].firstChild.nodeValue;

        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}


function getIndicators2()
{
    var indicatorGroupList = byId( "indicatorGroupId2" );
    var indicatorGroupId = indicatorGroupList.options[indicatorGroupList.selectedIndex].value;

    if ( indicatorGroupId != null )
    {
		$.get( '../dhis-web-commons-ajax/getIndicators.action',
		{
			id: indicatorGroupId
		},getIndicatorsReceived2 );
    }
}


function getIndicatorsReceived2( xmlObject )
{
    var availableIndicators = document.getElementById( "availableIndicators2" );
    var selectedIndicators = document.getElementById( "selectedIndicators2" );

    clearList( availableIndicators );

    var indicators = xmlObject.getElementsByTagName( "indicator" );

    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[i].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[i].getElementsByTagName( "name" )[0].firstChild.nodeValue;

        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}

function getIndicators3()
{
    var indicatorGroupList = byId( "indicatorGroupId3" );
    var indicatorGroupId = indicatorGroupList.options[indicatorGroupList.selectedIndex].value;

    if ( indicatorGroupId != null )
    {
		$.get( '../dhis-web-commons-ajax/getIndicators.action',
		{
			id: indicatorGroupId
		},getIndicatorsReceived3 );
    }
}


function getIndicatorsReceived3( xmlObject )
{
    var availableIndicators = document.getElementById( "availableIndicators3" );
    var selectedIndicators = document.getElementById( "selectedIndicators3" );

    clearList( availableIndicators );

    var indicators = xmlObject.getElementsByTagName( "indicator" );

    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[i].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[i].getElementsByTagName( "name" )[0].firstChild.nodeValue;

        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}

function getIndicators4()
{
    var indicatorGroupList = byId( "indicatorGroupId4" );
    var indicatorGroupId = indicatorGroupList.options[indicatorGroupList.selectedIndex].value;

    if ( indicatorGroupId != null )
    {
		$.get( '../dhis-web-commons-ajax/getIndicators.action',
		{
			id: indicatorGroupId
		},getIndicatorsReceived4 );
    }
}


function getIndicatorsReceived4( xmlObject )
{
    var availableIndicators = document.getElementById( "availableIndicators4" );
    var selectedIndicators = document.getElementById( "selectedIndicators4" );

    clearList( availableIndicators );

    var indicators = xmlObject.getElementsByTagName( "indicator" );

    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[i].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[i].getElementsByTagName( "name" )[0].firstChild.nodeValue;

        if ( listContains( selectedIndicators, id ) == false )
        {
            var option = document.createElement( "option" );
            option.value = id;
            option.text = indicatorName;
            availableIndicators.add( option, null );
        }
    }
}


function showAdvancedParameters()
{
	showById('advancedParameterID');
}

function hideAdvancedParameters()
{
	hideById('advancedParameterID');
	
}
