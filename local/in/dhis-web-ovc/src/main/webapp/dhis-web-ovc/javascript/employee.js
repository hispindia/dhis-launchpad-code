
function showOVCEmployeeDetails( empId )
{
	$('#detailsInfo').load("getEmployeeDetails.action", 
		{
			empId:empId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovc_employee_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 400
		});
}

//-----------------------------------------------------------------------------
//Remove Employee
//-----------------------------------------------------------------------------

function removeEmployee( employeeId, employeeFullName )
{
	removeItem( employeeId, employeeFullName, i18n_confirm_to_delete_employee, 'removeEmployee.action' );
}



//----------------------------------------------------------------------
//Validation for Employee Add & Update
//----------------------------------------------------------------------

function validateAddEmployee() 
{	
	//alert( " inside validation " );
	
	//alert( byId('empCode').value + " -- " + byId('orgUnitId').value );
	
	var employeeCode = document.getElementById('empCode').value;
	
	//alert( employeeCode );
	
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( orgUnitId );
	
	$.post("validateEmployee.action",
		{
			employeeCode : employeeCode,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			addEmployeeValidationCompleted(data);
		},'xml');

	return false;
}

function addEmployeeValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['addEmployeeForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}

// update validation Employee
function validateUpdateEmployee() 
{	
	var employeeId = document.getElementById('empId').value;
	var employeeCode = document.getElementById('empCode').value;
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	$.post("validateEmployee.action",
		{
			employeeId : employeeId,
			employeeCode : employeeCode,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			updateEmployeeValidationCompleted(data);
		},'xml');

	return false;
}

function updateEmployeeValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['updateEmployeeForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}




