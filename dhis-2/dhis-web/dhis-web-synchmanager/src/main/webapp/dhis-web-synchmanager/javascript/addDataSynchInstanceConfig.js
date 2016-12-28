jQuery(document).ready(	function(){
		validation( 'addDataSynchInstanceForm', function(form){
			//alert( "add instance");
			form.submit();
		}); 
		
		//checkValueIsExist( "name", "validateDataSynchInstanceConfig.action", {synchType:getFieldValue('synchType')} );
		//checkValueIsExist( "url", "validateDataSynchInstanceConfig.action", {synchType:getFieldValue('synchType')} );
		
		checkValueIsExist( "name", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType') + "##" + "0" } );
		checkValueIsExist( "url", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType') + "##" + "0" } );
		
	});	


//checkValueIsExist( "name", "validateInstanceConfig.action", {id:getFieldValue('id')} );