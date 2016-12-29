var selectedOrganisationUnit = null;
var selectedStartPeriodId;
var selectedEndPeriodId;
var validationRuleGroupId;
var aggregate;
var sendAlerts;

function organisationUnitSelected( organisationUnits )
{
    selectedOrganisationUnit = organisationUnits[0];
}

function validateRunAnalyseData()
  {
	selectedStartPeriodId = $( '#selectedStartPeriodId' ).val();
	selectedEndPeriodId = $( '#selectedEndPeriodId' ).val();

        $.getJSON( "validateRunAnalysis.action", { 
        	selectedStartPeriodId : selectedStartPeriodId, 
        	selectedEndPeriodId : selectedEndPeriodId }, function( json ){
            if ( json.response == "success" ){
            	//analyseDataInvalid();
            	analyseData();
            }else{
            	setHeaderDelayMessage( json.message );
                $( '#startButton').removeAttr( 'disabled' );
            }
        } );
        
        return false;
  }

function analyseDataInvalid()
{
	if ( $( "#selectedStartPeriodId" ).val().length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_a_start_date );
        return false;
    }
	
	if ( $( "#selectedEndPeriodId" ).val().length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_an_ending_date );
        return false;
    }

    var dataSets = document.getElementById( "dataSets" );

    if ( dataSets.options.length == 0 )
    {
    	setHeaderDelayMessage( i18n_specify_dataset );
        return false;
    }

    return true;
}

function analyseData()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action" + "?key=" + $( "#key" ).val() 
    + "&selectedStartPeriodId=" + selectedStartPeriodId 
    + "&selectedEndPeriodId=" + selectedEndPeriodId 
    + "&" + getParamString( "dataSets", "dataSets" );

    if ( byId( "standardDeviation" ) != null ){
        url += "&standardDeviation=" + $( "#standardDeviation" ).val();
    }

    $.get( url, function( data ){
        $( "div#analysisInput" ).hide();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );
        $( '#startButton').removeAttr( 'disabled' );
    } );
}

function getFollowUpAnalysis()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action?key=followup";

    $.get( url, function( data )
    {
        hideMessage();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );
    } );
}

//------------------------------------------------------------------------------
//Period
//------------------------------------------------------------------------------

var currentPeriodOffset = 0;
var currentStartPeriodOffset = 0;
var currentEndPeriodOffset = 0;

var periodTypeFactory = new PeriodType();

function displayPeriods() 
{
	var periodType = $( "#periodType" ).val();
	var startPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	var endPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	startPeriods = periodTypeFactory.reverse( startPeriods );
	startPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( startPeriods );
	endPeriods = periodTypeFactory.reverse( endPeriods );
	endPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( endPeriods );
	
	$( "#previous" ).removeAttr( "disabled" );
	$( "#next" ).removeAttr( "disabled" );
	$( "#previous5" ).removeAttr( "disabled" );
	$( "#next5" ).removeAttr( "disabled" );
	$( "#selectedStartPeriodId" ).removeAttr( "disabled" );
	$( "#selectedEndPeriodId" ).removeAttr( "disabled" );
	
	clearListById( "selectedStartPeriodId" );
	clearListById( "selectedEndPeriodId" );
	
	for ( i in startPeriods )
		{
		   addOptionById( "selectedStartPeriodId", startPeriods[i].iso, startPeriods[i].name );
		   addOptionById( "selectedEndPeriodId", endPeriods[i].iso, endPeriods[i].name );
		}
}

function displayNextPeriodsS()
{
if ( currentPeriodOffset < 0 ) // Cannot display future periods
{
	 currentPeriodOffset++;
	 var periodType = $( "#periodType" ).val();
	 var startPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	 startPeriods = periodTypeFactory.reverse( startPeriods );
	 startPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( startPeriods );
	 
	 $( "#previous" ).removeAttr( "disabled" );
		$( "#next" ).removeAttr( "disabled" );
	 clearListById( "selectedStartPeriodId" );
	
	 for ( i in startPeriods )
	 {
	     addOptionById( "selectedStartPeriodId", startPeriods[i].iso, startPeriods[i].name );
	 }
   }
}

function displayPreviousPeriodsS()
{
	 currentPeriodOffset--;
	 var periodType = $( "#periodType" ).val();
	 var startPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	 startPeriods = periodTypeFactory.reverse( startPeriods );
	 startPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( startPeriods );

	 $( "#previous" ).removeAttr( "disabled" );
		$( "#next" ).removeAttr( "disabled" );
	 clearListById( "selectedStartPeriodId" );

	 for ( i in startPeriods )
	 {
	     addOptionById( "selectedStartPeriodId", startPeriods[i].iso, startPeriods[i].name );
	 }
}

function displayNextPeriodsE()
{
	if ( currentPeriodOffset < 0 ) // Cannot display future periods
		{
		 currentPeriodOffset++;
		 var periodType = $( "#periodType" ).val();
		 var endPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
		 endPeriods = periodTypeFactory.reverse( endPeriods );
		 endPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( endPeriods );
		 
		 $( "#previous" ).removeAttr( "disabled" );
			$( "#next" ).removeAttr( "disabled" );
		 clearListById( "selectedEndPeriodId" );
	
		 for ( i in endPeriods )
		 {
		     addOptionById( "selectedEndPeriodId", endPeriods[i].iso, endPeriods[i].name );
		 }
	}
}

function displayPreviousPeriodsE()
{
	 currentPeriodOffset--;
	 var periodType = $( "#periodType" ).val();
	 var endPeriods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
	 endPeriods = periodTypeFactory.reverse( endPeriods );
	 endPeriods = periodTypeFactory.filterFuturePeriodsExceptCurrent( endPeriods );
	 
	 $( "#previous" ).removeAttr( "disabled" );
		$( "#next" ).removeAttr( "disabled" );
	 clearListById( "selectedEndPeriodId" );

	 for ( i in endPeriods )
	 {
	     addOptionById( "selectedEndPeriodId", endPeriods[i].iso, endPeriods[i].name );
	 }
}





