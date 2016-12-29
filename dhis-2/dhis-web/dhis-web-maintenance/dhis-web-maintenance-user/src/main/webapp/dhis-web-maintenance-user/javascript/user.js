// -----------------------------------------------------------------------------
// Export to PDF file
// -----------------------------------------------------------------------------

function exportPDF( type ) {
  var params = "type=" + type;
  params += "&months=" + jQuery('#months').val();

  exportPdfByType(type, params);
}

// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------

function searchUserName() {
  var key = getFieldValue('key');

  if( key != '' ) {
    jQuery('#userForm').load('searchUser.action', {key: key}, unLockScreen);
    lockScreen();
  }
  else {
    jQuery('#userForm').submit();
  }
}

function filterUsers() {
  var months = $('#months').val();
  var selfRegistered = $('#selfRegistered').val();
  var invitationStatus = $('#invitationStatus').val();

  var url = 'alluser.action?';
  
  url += months ? 'months=' + months + '&' : '';
  url += selfRegistered ? 'selfRegistered=' + selfRegistered + '&' : '';
  url += invitationStatus ? 'invitationStatus=' + invitationStatus + '&' : '';
  
  window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdateUserForm( context ) {
  location.href = 'showUpdateUserForm.action?id=' + context.id;
}

function showUserProfile( context ) {
  location.href = '../dhis-web-dashboard-integration/profile.action?id=' + context.id;
}

function showUserDetails( context ) {
  jQuery.post('getUser.action', { id: context.id }, function( json ) {
    setInnerHTML('usernameField', json.user.username);
	setInnerHTML('idField', json.user.uid);

    var fullName = json.user.firstName + ", " + json.user.surname;
    setInnerHTML('fullNameField', fullName);

    var email = json.user.email;
    setInnerHTML('emailField', email ? email : '[' + i18n_none + ']');

    var phoneNumber = json.user.phoneNumber;
    setInnerHTML('phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']');

    var lastLogin = json.user.lastLogin;
    setInnerHTML('lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']');

    var created = json.user.created;
    setInnerHTML('createdField', created ? created : '[' + i18n_none + ']');

    var disabled = json.user.disabled;
    setInnerHTML('disabledField', disabled == "true" ? i18n_yes : i18n_no);

    var organisationUnits = joinNameableObjects(json.user.organisationUnits);
    setInnerHTML('assignedOrgunitField', organisationUnits ? organisationUnits : '[' + i18n_none + ']');

    var roles = joinNameableObjects(json.user.roles);
    setInnerHTML('roleField', roles ? roles : '[' + i18n_none + ']');

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Add user
// -----------------------------------------------------------------------------

var saved = {};

function changeAccountAction()
{
    if( $('#accountAction').val() == 'create' )
    {
        $('#username').val( $('#inviteUsername').val() );
        $('#inviteUsername').val( 'nonExistingUserName_RpuECtIlVoRKTpYmEkYrAHmPtX4m1U' );
        $('#rawPassword').val( saved["rawPassword"] );
        $('#retypePassword').val( saved["retypePassword"] );
        $('#surname').val( saved["surname"] );
        $('#firstName').val( saved["firstName"] );
        $('#email').val( $('#inviteEmail').val() );
        $('#inviteEmail').val( 'validEmail@domain.com' );

        $('.account').show();
        $('.invite').hide();
    }
    else
    {
        $('.account').hide();
        $('.invite').show();

        saved["rawPassword"] = $('#rawPassword').val();
        saved["retypePassword"] = $('#retypePassword').val();
        saved["surname"] = $('#surname').val();
        saved["firstName"] = $('#firstName').val();

        $('#inviteUsername').val( $('#username').val() );
        $('#username').val( 'nonExistingUserName_RpuECtIlVoRKTpYmEkYrAHmPtX4m1U' );
        $('#rawPassword').val( 'validPassword_123' );
        $('#retypePassword').val( 'validPassword_123' );
        $('#surname').val( 'validSurname' );
        $('#firstName').val( 'validFirstName' );
        $('#inviteEmail').val( $('#email').val() );
        $('#email').val( '' );
    }
}

function validateInvite() {
	var def = $.Deferred();
	var action = $('#accountAction').val();
	
	if ( "invite" == action ) {
		var url = 'validateInvite.action?email=' + $('#inviteEmail').val();
		var options = $('#urSelected').val();
		$.each(options, function(inx,val) {
			url += '&urSelected=' + val;
		});
		
		$.getJSON(url, function(json) {
			if (json.response == "error") {
				setHeaderDelayMessage(json.message);
				def.reject();
			}
			else if (json.response == "success") {
				def.resolve();
			}
		});
	}
	else {
		def.resolve();
	}
	
	return def.promise();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, "removeUser.action");
}

function disableUser( context ) {
  var confirmation = confirm("Are you sure you want to disable this user?\n\n" + context.username);

  if( confirmation ) {
    $.post("disableUser.action", {
        username: context.username
      },
      function( json ) {
        location.reload();
      });
  }
}

function enableUser( context ) {
  var confirmation = confirm("Are you sure you want to enable this user?\n\n" + context.username );

  if( confirmation ) {
    $.post("disableUser.action",
      {
        username: context.username,
        enable: true
      },
      function( json ) {
        location.reload();
      });
  }
}

function showUserOptions()
{
	$( "#showMoreOptions" ).toggle();
	$( "#moreOptions" ).toggle();
}

// -----------------------------------------------------------------------------
// Replicate user
// -----------------------------------------------------------------------------

function showReplicateUserDialog( context ) {
	$( "#replicaId" ).val( context.uid );
	
	$( "#replicateUserForm" ).dialog( {
		modal: true,
		width: 415,
		height: 170,
		resizable: false,
		title: "Replicate user"
	});
}

function replicateUser() {
	var replica = {
		"username": $( "#replicaUsername" ).val(),
		"password": $( "#replicaPassword" ).val()
	};
	
	var id = $( "#replicaId" ).val();
	
	$.ajax({
		url: "../api/users/" + id + "/replica", 
		data: JSON.stringify( replica ),
		type: "post",
		contentType: "application/json; charset=utf-8",
		success: function() {
			$( "#replicateUserForm" ).dialog( "destroy" );
			window.location.href = "alluser.action";
		},
		error: function( xhr, status, error ) {
			setHeaderDelayMessage( xhr.responseText );
		}
	});
}

function cancelReplicateUser() {
	$( "#replicateUserForm" ).dialog( "destroy" );
}

function resendInvitation( context ) {
	var userId = context.uid;
	
	$.ajax( {
		url: "../api/users/" + userId + "/invite",
		type: "post",
		success: function() {
			setHeaderDelayMessage( i18n_invitation_sent );
		},
		error: function( xhr, status, error ) {
			setHeaderDelayMessage( xhr.responseText );
		}
	} );
}
