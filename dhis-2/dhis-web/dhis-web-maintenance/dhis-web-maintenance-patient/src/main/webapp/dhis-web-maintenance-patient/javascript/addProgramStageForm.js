jQuery( document ).ready( function()
{
	validation( 'addProgramStageForm', function( form ){ 
		form.submit();
	}, function(){
		selectedDataElementsValidator = jQuery( "#selectedDataElementsValidator" );
		selectedDataElementsValidator.empty();
		
		compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		});
	});
	
	checkValueIsExist( "name", "validateProgramStage.action");	
});

