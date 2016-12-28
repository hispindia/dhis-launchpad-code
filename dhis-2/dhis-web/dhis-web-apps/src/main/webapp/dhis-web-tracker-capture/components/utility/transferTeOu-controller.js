
//Controller for utility
trackerCapture.controller('transferTeOuController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                $filter,
                orderByFilter,
                storage,
                TEIService, 
                TEService,
                OptionSetService,
                EnrollmentService,
                ProgramFactory,
                DashboardLayoutService,
                AttributesFactory,
                CurrentSelection,
				trackedEntity,
				$modalInstance) {
				
	function updateTeI()
	{
    var def = $.Deferred();
	var newTrackedEntityInstance = trackedEntity;
	newTrackedEntityInstance.orgUnit = $scope.transferredToOu[0];
    $.ajax({
        url: '../api/trackedEntityInstances/'+trackedEntity.trackedEntityInstance,
        type: 'PUT',
		contentType: 'application/json',
		dataType: 'json',
		data: angular.toJson(newTrackedEntityInstance)
    }).done(function(response) {
        def.resolve();
    }).fail(function(){
		alert("transfer failed");
        def.resolve();
    });

    return def.promise();
	}
	
	
    $scope.ok = function () {
	$scope.transferredToOu = selection.getSelected();
	updateTeI();
	
    $modalInstance.close($scope.transferredToOu);
	};

	$scope.cancel = function () {
    $modalInstance.dismiss('cancel');
	};
	
	
});
