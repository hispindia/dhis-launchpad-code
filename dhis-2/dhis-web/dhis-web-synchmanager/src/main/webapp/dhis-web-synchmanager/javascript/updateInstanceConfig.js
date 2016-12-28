jQuery(document).ready(	function(){
		validation( 'updateInstanceForm', function(form){
			//alert( "update instance");
			form.submit();
		}); 
		
		checkValueIsExist( "name", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType') + "##" + getFieldValue('instanceId') } );
		checkValueIsExist( "url", "validateInstanceConfig.action", {synchTypeAndId:getFieldValue('synchType')  + "##" + getFieldValue('instanceId') } );
		
	});