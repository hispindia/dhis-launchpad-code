
function showInstanceDetails(context) 
{

	jQuery.getJSON('getInstance.action', {
		id : context.id
	}, function(json) {
		setInnerHTML('nameField', json.instance.name );
		setInnerHTML('urlField', json.instance.url );
		setInnerHTML('userField', json.instance.userId );
		//setInnerHTML('pwdField', json.instance.password );
		//setInnerHTML('lastUpdatedField', json.instance.lastUpdated );
		setInnerHTML('idField', json.instance.uid );

		showDetails();
	});
}

function removeInstance(context) 
{
	removeItem( context.id, context.name, i18n_confirm_delete, 'deleteInstance.action' );
}

function showBusinessRulesForm(context) 
{
	location.href = 'showBusinessRulesForm.action?instanceId=' + context.id;
}

function showUpdateInstanceForm(context)
{
	var synchType = $("#synchType").val();
	location.href = 'showUpdateInstanceForm.action?update=true&instanceId=' + context.id + '&synchType='+synchType;
}


/*
(function ($) {
    $.fn.selectAllRows = function (callerSettings) {
    var settings;
    var headerCheckbox;
    var columnCheckboxes;
    settings = $.extend(
    {
    	column: 'first',
    	selectTip: 'Click to Select All',
    	unselectTip: 'Click to Un-Select All'
    }, 
    callerSettings || {} );
    
    if (isNaN(settings.column)) 
    {
    	headerCheckbox = $("thead tr th:" + settings.column + "-child input:checkbox", this);
    	columnCheckboxes = $("tbody tr td:" + settings.column + "-child input:checkbox", this);
    }
    else 
    {
    	headerCheckbox = $("thead tr th:nth-child(" + settings.column + ") input:checkbox", this);
    	columnCheckboxes = $("tbody tr td:nth-child(" + settings.column + ") input:checkbox", this);
    }
    
    headerCheckbox.attr("title", settings.selectTip);
    
    headerCheckbox.click(function () {
    var checkedStatus = this.checked;
    columnCheckboxes.each(function () {
    this.checked = checkedStatus;
    });
    if (checkedStatus == true) {
    $(this).attr("title", settings.unselectTip);
    }
    else {
    $(this).attr("title", settings.selectTip);
    }
    });
    return $(this);
    };
    })(jQuery);

*/
