/* global excelUpload, angular */

//Controller for managing templates
excelUpload.controller('TemplateController',
        function($rootScope,
                $scope,
                $timeout,
                $route,
                $filter,
                ExcelMappingService,
                ValidationRuleService,
                CurrentSelection,
                ExcelReaderService,
                MetaDataFactory,
                orderByFilter,
                OrgUnitService,
                DialogService) {
					
	
	//needed variables
	$scope.orgUnitGroups = {};
	$scope.dataSets = {};
	$scope.templates = {};
	$scope.jsonData = {};
	$scope.advConfigHidden = true;
	
	//retrieving all the needed things
	//**************************************************************************************************************
	
	//templates
	$("#templateProgress").html("Retrieving all the saved templates...");
	ExcelMappingService.get('Excel-import-app-templates').then(function(tem){
		if(!jQuery.isEmptyObject(tem))
			$scope.templates = tem;
		else
			$scope.templates = { templates : [] };
		
		console.log( $scope.templates );
		
		//org unit group
		$("#templateProgress").html("Fetching organisation unit groups...");
		$.get('../api/organisationUnitGroups.json?paging=false', function(ou){
			console.log( ou );
			$scope.orgUnitGroups = ou.organisationUnitGroups;
			
			//datasets
			$("#templateProgress").html("Fetching all the data sets...");
			$.get('../api/dataSets.json?paging=false', function(ds){
				console.log( ds );
				$scope.dataSets = ds.dataSets;
				
				$scope.startBuilding();
				$("#loader").hide();
			}).
			fail(function(jqXHR, textStatus, errorThrown){
				$("#templateProgress").html("Failed to fetch data sets ( " + errorThrown + " )");
			});
		
		}).
		fail(function(jqXHR, textStatus, errorThrown){
			$("#templateProgress").html("Failed to fetch organisation unit groups ( " + errorThrown + " )");
		});
	});
	//**************************************************************************************************************
	
	//building UIs
	$scope.startBuilding = function(){
		$("#templateProgress").html("Making things ready...");
		$.each( $scope.dataSets , function( i, d ){
			$("#dataSetSelect").append("<option value='"+ d.id +"' > " + d.name +" </option>");
		});
		
		$.each( $scope.orgUnitGroups , function( i, o ){
			$("#orgUnitGroupSelect").append("<option value='"+ o.id +"' > " + o.name +" </option>");
		});
	};
	
	//**************************************************************************************************************
	
	//changing templates on changing OU group and Data Set
	$scope.validateSelection = function(){
		if( $("#dataSetSelect").val() != "" && $("#orgUnitGroupSelect").val() != "")
		{
			var htmlString = "";
			var tempFound = false;
			
			$.each( $scope.templates.templates , function( i , t ){
				if( t.dataSet == $("#dataSetSelect").val() && t.orgUnitGroup == $("#orgUnitGroupSelect").val() )
				{
					htmlString += "<option value = '" + t.id + "' > " + t.name + "</option>";
					tempFound = true;
				}
			});
			
			$("#templatesDiv").removeClass("disabled");
			
			if( tempFound )
			{
				$("#templateSelect").html(htmlString);
				$("#editTem").removeAttr("disabled");
				$("#delTem").removeAttr("disabled");
			}
			else
			{
				$("#templateSelect").html("<option> No templates found</option>");
				$("#editTem").attr("disabled" , "true");
				$("#delTem").attr("disabled" , "true");
			}
			
		}
		else
		{
			$("#templatesDiv").removeClass("disabled");	
			$("#templatesDiv").addClass("disabled");
			$("#templateSelect").html("<option> Select a data set and orgUnit group </option>");			
		}
	};
	
	$scope.storeTemp = function(){
		ExcelMappingService.save('Excel-import-app-templates',$scope.templates ).then(function(tem){
			//alert( tem );
			location.reload();
		});	
	};
	
	$scope.deleteTemp = function(){
		var r = confirm("Are you sure that you want to delete this template?");

		if (r == true)
		{
			var deletedIndex = -1;		
			
			$scope.templates.templates.forEach(function(te,index){
				
				if(te.id == $("#templateSelect").val())
				{
					deletedIndex = index;
				}
			});
			
			if( deletedIndex >= 0)
				$scope.templates.templates.splice( deletedIndex , 1 );
			
			$scope.storeTemp();
		}
	};
	
	$scope.viewTemp = function(){
		var selectedTemp = "";		
		
		$scope.templates.templates.forEach(function(te,index){
			if(te.id == $("#templateSelect").val())
			{
				selectedTemp = te;
			}
		});
		
		if( selectedTemp != "")
		{
			$("#loader").fadeIn();
			$("#templateProgress").html("");
			
			var t = "";
			
			if( selectedTemp.typeId == 1 && selectedTemp.rowMetaData == "d" )
				t = "Row Number";
			else if(  selectedTemp.typeId == 1 && selectedTemp.rowMetaData == "o" )
				t = "Column Number";
			else
				t = "Cell Address";
			
			var htmlString = "<tr><th>" + t + "</th><th> Data Element - COC </th></tr>";
			$.each( selectedTemp.DEMappings, function(i, m){
				
				if( selectedTemp.typeId == 1 && selectedTemp.rowMetaData == "d" )
					htmlString += "<tr><td>" + m.rowNumber + "</td><td> " + m.label + " </td></tr>";
				else if(  selectedTemp.typeId == 1 && selectedTemp.rowMetaData == "o" )
					htmlString += "<tr><td>" + m.colNumber + "</td><td> " + m.label + " </td></tr>";
				else
					htmlString += "<tr><td>" + m.cellNumber + "</td><td> " + m.label + " </td></tr>";
			});
			
			
			$("#sth").html( selectedTemp.name + " Template");
			$("#tblView").html(htmlString);
			$("#viewModal").modal("show");
			$("#loader").fadeOut();
		}
	};
	
	//***************************************************************************************
	$scope.addTemplateForm = function(){
		$rootScope.selectedTemplateType = $("#addTempType").val();
		$rootScope.selectedOrgGroup = $("#orgUnitGroupSelect").val();
		$rootScope.selectedDataSet = $("#dataSetSelect").val();
		
		window.location.assign('#add-template');
	};

	/*******************Upload JSON********************/
	$scope.advConfig = function(){
		if($scope.advConfigHidden){
			$("#uploadJSONDiv").slideDown();
			$("#advConfigIcon").removeClass().addClass('glyphicon glyphicon-chevron-up');
			$scope.advConfigHidden = false;
		}
		else {
			$("#uploadJSONDiv").slideUp();
			$("#advConfigIcon").removeClass().addClass('glyphicon glyphicon-chevron-down');
			$scope.advConfigHidden = true;
		}
	}

	$scope.uploadFile = function(){
		$("#fileUpload").click();
		$("#templateProgress").html("Uploading JSON...");
	}

	$scope.getJSON = function(){
		$scope.jsonData = '';
		$("#templateProgress").html("Checking uploaded file...");
		var uploadedFile = document.getElementById('fileUpload').files[0];
		if (typeof uploadedFile == "undefined"){
			$('#modalHeader').removeClass().addClass('alert alert-warning').addClass('modal-header');
			$("#msgIcon").removeClass().addClass('glyphicon glyphicon glyphicon-warning-sign');
			$("#msgTitle").html("&nbsp;&nbsp;Info Message");
			$("#msgTitle").css('color', 'darkgoldenrod');
			$("#errorContent").html("");
			$("#errorContent").append("Please upload a file");
			var fileUpload = $("#fileUpload");
			fileUpload.replaceWith(fileUpload.val('').clone(true));
			$("#errorModal").modal("show");
			return false;
		}
		else if (uploadedFile.type != "text/javascript" && uploadedFile.type != "application/x-javascript" && uploadedFile.type != "application/javascript") {
			$('#modalHeader').removeClass().addClass('alert alert-danger').addClass('modal-header');
			$("#msgIcon").removeClass().addClass('glyphicon glyphicon glyphicon-exclamation-sign');
			$("#msgTitle").html("&nbsp;&nbsp;Error Message");
			$("#msgTitle").css('color', 'darkred');
			$("#errorContent").html("");
			$("#errorContent").append("Wrong file type " + uploadedFile.type + ".</br>" + " Please select .js format file.");
			var fileUpload = $("#fileUpload");
			fileUpload.replaceWith(fileUpload.val('').clone(true));
			$("#errorModal").modal("show");
			return false;
		}
		else {
			$("#templateProgress").html("Retrieving data...");
			var jsonReader = new FileReader();
			jsonReader.onloadend = function (e) {
				$scope.jsonData = e.target.result;
				$scope.uploadJSON(uploadedFile.name);
			}
			jsonReader.readAsText(uploadedFile);
		}
	}

	$scope.uploadJSON = function(uploadedFileName){
		var templateIDs = [];
		$.each($scope.templates.templates, function (idx, temp) {
			templateIDs.push(temp.id);
		});
		try {
			var json = $.parseJSON($scope.jsonData);
			var noOfTempStored = 0, noOfTempUpdated = 0, noOfTempIgnored = 0;
			if(typeof json.templates != "undefined") {
				$.each(json.templates, function (i, template) {
					if (typeof template != "undefined") {
						if ($.inArray(template.id, templateIDs) == -1) {
							$scope.templates.templates.push(template);
							noOfTempStored++;
						}
						else {
							var deletedIndex = -1;
							$scope.templates.templates.forEach(function (te, index) {
								if (te.id == template.id) {
									deletedIndex = index;
								}
							});
							if (deletedIndex >= 0)
								$scope.templates.templates.splice(deletedIndex, 1, template);
							noOfTempUpdated++;
						}
					}
					else {
						noOfTempIgnored++;
					}
				});
				ExcelMappingService.save('Excel-import-app-templates', $scope.templates).then(function (tem) {
					$scope.showUploadCompletedModal(noOfTempStored, noOfTempUpdated, noOfTempIgnored)
				});
			}
			else {
				$('#modalHeader').removeClass().addClass('alert alert-warning').addClass('modal-header');
				$("#msgIcon").removeClass().addClass('glyphicon glyphicon glyphicon-exclamation-sign');
				$("#msgTitle").html("&nbsp;&nbsp;Warning Message");
				$("#msgTitle").css('color', 'darkgoldenrod');
				$("#errorContent").html("");
				$("#errorContent").append("No templates found in " + uploadedFileName + "</br>" + " Please upload a file that contains relevent templates.");
				var fileUpload = $("#fileUpload");
				fileUpload.replaceWith(fileUpload.val('').clone(true));
				$("#errorModal").modal("show");
				return false;
			}
		}
		catch(err){
			$('#modalHeader').removeClass().addClass('alert alert-danger').addClass('modal-header');
			$("#msgIcon").removeClass().addClass('glyphicon glyphicon glyphicon-exclamation-sign');
			$("#msgTitle").html("&nbsp;&nbsp;Error Message");
			$("#msgTitle").css('color', 'darkred');
			$("#errorContent").html("");
			$("#errorContent").append("JSON Not Found in " + uploadedFileName + "</br>" + " Please select .js that contains a JSON");
			var fileUpload = $("#fileUpload");
			fileUpload.replaceWith(fileUpload.val('').clone(true));
			$("#errorModal").modal("show");
			return false;
		}
	}

	$scope.showUploadCompletedModal = function(noOfTempStored, noOfTempUpdated, noOfTempIgnored){
		$("#ucStored").html("");
		$("#ucUpdated").html("");
		$("#ucIgnored").html("");
		$("#ucStored").append("Stored: " + noOfTempStored);
		$("#ucUpdated").append("Updated: " + noOfTempUpdated);
		$("#ucIgnored").append("Ignored: " + noOfTempIgnored);
		$("#updateCompletedModal").modal("show");

		var fileUpload = $("#fileUpload");
		fileUpload.replaceWith( fileUpload.val('').clone( true ) );
		$scope.jsonData = '';
	}

});