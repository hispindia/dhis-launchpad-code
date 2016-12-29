
//------------------------------------------------------------------------------
// Get Aggregated Dataelements
//------------------------------------------------------------------------------

function getAggDataElements( )
{
	var dataElementGroup = document.getElementById( 'dataElementGroup' );
	var dataElementGroupId = dataElementGroup.options[ dataElementGroup.selectedIndex ].value;
	if( dataElementGroupId == "" ){
		clearList( byId('aggregationDataElementId'));
		return;
	}

	$.post( 'getAggDataElements.action', { dataElementGroupId:dataElementGroupId }, getAggDataElementsCompleted );
}

function getAggDataElementsCompleted( dataelementElement )
{
	var de = document.getElementById( 'aggregationDataElementId' );
  
	clearList( de );
  
	var dataElementList = $(dataelementElement).find( 'dataelement' );
  
	$( dataElementList ).each( function( i, item )
	{
        var id = $(item).find("id").text();
        var name = $(item).find("name").text();

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        de.add(option, null);  			
	} );
}

//------------------------------------------------------------------------------
// Get Program Stages
//------------------------------------------------------------------------------

function getProgramStages()
{
	clearListById( 'programStage' );
  	clearListById( 'programstageDE' );
	
	var program = document.getElementById( 'program' );
	var programId = program.options[ program.selectedIndex ].value;
	if( programId == '0' ){
		return;  
	}

	$.post( 'getProgramStages.action', { programId:programId }, getProgramStagesCompleted );
}

function getProgramStagesCompleted( programstageElement )
{
	var programstage = document.getElementById( 'programStage' );
	var programstageList = $(programstageElement).find( 'programstage' );

	$( programstageList ).each( function( i, item )
	{
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();

		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;

		programstage.add(option, null);       	
	});

	if( programstage.options.length > 0 )
	{
		programstage.options[0].selected = true;
		getPrgramStageDataElements();
	}   
}


function getProgramStagesForFormula()
{
	clearListById( 'programStageFormula' );
	
	var program = document.getElementById( 'programFormula' );
	var programId = program.options[ program.selectedIndex ].value;
	if( programId == '0' ){
		return;  
	}

	$.post( 'getProgramStages.action', { programId:programId }, getProgramStagesFomulaCompleted );
}

function getProgramStagesFomulaCompleted( programstageElement )
{
	var programstage = document.getElementById( 'programStageFormula' );
	var programstageList = $(programstageElement).find( 'programstage' );

	$( programstageList ).each( function( i, item )
	{
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();

		var option = document.createElement("option");
		option.value = "[PS:" + id + "]";
		option.text = name;
		option.title = name;

		programstage.add(option, null);       	
	});

	if( programstage.options.length > 0 )
	{
		programstage.options[0].selected = true;
	}   
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPrgramStageDataElements()
{
	clearListById( 'programstageDE' );

	var programStage = document.getElementById( 'programStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	
	$.post( 'getPSDataElements.action', { psId:psId }, getPrgramStageDataElementsCompleted );
}

function getPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = jQuery('#programstageDE');
	var psDataElements = $(dataelementElement).find( 'dataelement' );

	$( psDataElements ).each( function( i, item )
	{
		var id = $(item).find("id").text();
		var name = $(item).find("name").text();
		var optionset =$(item).find("optionset").text();
		
		programstageDE.append( "<option value='" + id + "' title='" + name + "' suggestedValues='" + optionset + "'>" + name + "</option>" );
	} );	    
}

//-----------------------------------------------------------------
// Insert items into Condition
//-----------------------------------------------------------------

function insertInfo( element )
{
	insertTextCommon('aggregationCondition', element.options[element.selectedIndex].value );
	getConditionDescription();
}

function insertOperator( value )
{
	insertTextCommon('aggregationCondition', ' ' + value + ' ' );
	getConditionDescription();
}

// -----------------------------------------------------------------------------
// Remove Case Aggregation Condition
// -----------------------------------------------------------------------------

function removeCaseAggregation( caseAggregationId, caseAggregationName )
{
	removeItem( caseAggregationId, caseAggregationName, i18n_confirm_delete, 'removeCaseAggregation.action' );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showCaseAggregationDetails( caseAggregationId )
{
    jQuery.post( 'getCaseAggregation.action', { id:caseAggregationId }, function ( json )
	{
		setInnerHTML( 'descriptionField', json.caseAggregation.description );	
		setInnerHTML( 'operatorField', json.caseAggregation.operator );
		setInnerHTML( 'aggregationDataElementField', json.caseAggregation.aggregationDataElement );
		setInnerHTML( 'optionComboField', json.caseAggregation.optionCombo );	
		setInnerHTML( 'aggregationExpressionField', json.caseAggregation.aggregationExpression );
		
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function getConditionDescription()
{
	$.post( 'getCaseAggregationDescription.action', 
		{ 
			condition:getFieldValue('aggregationCondition') 
		},function (data)
		{
			byId('aggregationDescription').innerHTML = data;
		},'html');
}

// -----------------------------------------------------------------------------
// Test condition
// -----------------------------------------------------------------------------

function testCaseAggregationCondition()
{
	$.postJSON( 'testCaseAggregationCondition.action', 
		{ 
			condition:getFieldValue('aggregationCondition') 
		},function (json)
		{
			var type = json.response;
			
			if ( type == "input" )
			{
				showWarningMessage( i18n_run_fail );
			}
			else
			{
				showSuccessMessage( i18n_run_success );
			}
		});
}

function getSuggestedValues( sourceId, targetId )
{
	clearListById( targetId );
	
	var suggestedValues = jQuery('select[id=' + sourceId + '] option:selected').attr('suggestedValues');	
	
	var arrValues = new Array();
	arrValues = suggestedValues.replace(/[//[]+/g,'').replace(/]/g, '').split(', ');

	var suggestedValueSelector = byId( targetId );
	for( var i=0; i< arrValues.length; i++ )
	{
		var option = document.createElement("option");
		option.value = "'" + arrValues[i] + "'";
		option.text = arrValues[i];
		option.title = arrValues[i];

		suggestedValueSelector.add(option, null); 
	}
}


