var validationRuleGroupId;
var selectedStartPeriodId;
var selectedEndPeriodId;
var organisationUnitId;
var aggregate;
var sendAlerts;

function organisationUnitSelected( ids )
{
    organisationUnitId = ids[0];
}

function validateRunValidation()
{ 	 	 
	selectedStartPeriodId = $( '#selectedStartPeriodId' ).val();
	selectedEndPeriodId = $( '#selectedEndPeriodId' ).val();
	aggregate = $( '#aggregate' ).val();
	validationRuleGroupId = $( '#validationRuleGroupId' ).val();
	sendAlerts =  $( '#sendAlerts' ).is( ':checked' );

	 $.getJSON( 'validateRunValidation.action', { 
		 selectedStartPeriodId:selectedStartPeriodId,
		 selectedEndPeriodId:selectedEndPeriodId
		}, function( json ){
			if ( json.response == 'success' ){
			    $( '#validateButton' ).attr( 'disabled', true )
		        setWaitMessage( i18n_analysing_please_wait );	
		        $.get( 'runValidationAction.action', { 
		        	organisationUnitId:organisationUnitId, 
		        	selectedStartPeriodId:selectedStartPeriodId,
		        	selectedEndPeriodId:selectedEndPeriodId,
		        	validationRuleGroupId:validationRuleGroupId,
		        	aggregate:aggregate,
		        	sendAlerts:sendAlerts
	        }, function( data ){
	            hideMessage();
	            $( 'div#analysisInput' ).hide();
	            $( 'div#analysisResult' ).show();
	            $( 'div#analysisResult' ).html( data );
	            setTableStyles();

                $( '#validateButton' ).removeAttr( 'disabled' );
	        } );
	    }
	    else if ( json.response == 'input' ){
	    	setHeaderDelayMessage( json.message );
	    }
	} );

    return false;
}

function displayValidationDetailsDialog()
{
	$( '#validationResultDetailsDiv' ).dialog( {
	    modal: true,
	   	title: 'Validation details',
	   	width: 550,
	   	height: 500
	} );
}

function viewValidationResultDetails( validationRuleId, sourceId, periodId )
{
	$( '#validationResultDetailsDiv' ).load( 'viewValidationResultDetails.action', {
		validationRuleId: validationRuleId, sourceId: sourceId, periodId: periodId },
		displayValidationDetailsDialog 
	);
}

function exportValidationResult( type )
{
    var url = 'exportValidationResult.action?type=' + type + 
    	"&organisationUnitId=" + $( "#organisationUnitId" ).val();
    	
    window.location.href = url;
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
 $( "#previous" ).removeAttr( "disabled" );
 $( "#next" ).removeAttr( "disabled" );
 
 $( "#selectedEndPeriodId" ).removeAttr( "disabled" );
 $( "#selectedStartPeriodId" ).removeAttr( "disabled" );
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
	 
	 $( "#selectedStartPeriodId" ).removeAttr( "disabled" );
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

	 $( "#selectedStartPeriodId" ).removeAttr( "disabled" );
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
	 
	 $( "#selectedEndPeriodId" ).removeAttr( "disabled" );
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
	 
	 $( "#selectedEndPeriodId" ).removeAttr( "disabled" );
	 clearListById( "selectedEndPeriodId" );

	 for ( i in endPeriods )
	 {
	     addOptionById( "selectedEndPeriodId", endPeriods[i].iso, endPeriods[i].name );
	 }
}

//------------------------------------------------------------------------------------------------


