
//-----------------------------------------------------------------------------
// Employee Details
//-----------------------------------------------------------------------------

function showEmployeeDetails( empId )
{
	$('#detailsInfo').load("getEmployeeDetails.action", 
		{
			empId:empId
		}
		, function( ){
			
		}).dialog({
			title: i18n_employee_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 400
		});
}


//-----------------------------------------------------------------------------
// Remove Employee
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
	
	var name = document.getElementById('name').value;
	var birthDate = document.getElementById('birthDate').value;
	
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( orgUnitId );
	
	$.post("validateEmployee.action",
		{
			name : name,
			birthDate : birthDate,
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


//update validation Employee
function validateUpdateEmployee() 
{	
	var employeeId = document.getElementById('empId').value;
	
	var name = document.getElementById('name').value;
	var birthDate = document.getElementById('birthDate').value;
	
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	$.post("validateEmployee.action",
		{
			employeeId : employeeId,
			name : name,
			birthDate : birthDate,
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
