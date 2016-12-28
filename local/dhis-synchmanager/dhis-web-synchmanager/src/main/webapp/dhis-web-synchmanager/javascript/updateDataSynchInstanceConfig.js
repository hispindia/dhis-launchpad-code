jQuery(document).ready(	function(){
		validation( 'updateDataSynchInstanceForm', function(form){
			//alert( "update instance");
			form.submit();
		}); 
		
		
		//checkValueIsExist( "name", "validateDataSynchInstanceConfig.action", {synchType:getFieldValue('synchType')}, {id:getFieldValue('dataSynchInstanceId')}  );
		//checkValueIsExist( "url", "validateDataSynchInstanceConfig.action", {synchType:getFieldValue('synchType')}, {id:getFieldValue('dataSynchInstanceId')}   );
		
		
		checkValueIsExist( "name", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType') + "##" + getFieldValue('dataSynchInstanceId') } );
		checkValueIsExist( "url", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType')  + "##" + getFieldValue('dataSynchInstanceId') } );
		
	});