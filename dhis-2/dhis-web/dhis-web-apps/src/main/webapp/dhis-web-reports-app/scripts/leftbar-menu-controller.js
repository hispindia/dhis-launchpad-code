/**
 * Created by harsh on 22/6/15.
 */


reportsApp.controller('LeftBarMenuController',
    function ($rootScope,$scope, $location) {


        $scope.showProgramManagementScreen = function () {
            $location.path('/program-management');

        };

        $scope.showTemplateManagementScreen = function () {
            $location.path('/template-management');

        };
        $scope.showReportsScreen = function () {
            $location.path('/reports');

        };

        $scope.showReportGenerationScreen = function () {
            $location.path('/generate-report');

        };

    });