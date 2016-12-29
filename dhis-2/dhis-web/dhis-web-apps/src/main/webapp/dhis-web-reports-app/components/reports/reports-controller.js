/**
 * Created by harsh on 22/6/15.
 */

reportsApp.controller('ReportsController',
    function ($rootScope,$scope, $location,reportSettingService,reportsService,
              ReportAppSectionSettingService,organisationUnitGroupService) {

        $scope.addReport = function () {
            $location.path('/add-report');
        };

        reportSettingService.getAll().then(function(data){
            $scope.mappedReports = data.reports;
        });

        reportsService.getAll().then(function(data){
            $scope.reportListMapping = [];
            angular.forEach(data.reports, function(report){
                $scope.reportListMapping[report.id] = report;
            })
        });

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.sectionListMapping = [];
            angular.forEach(data.sections, function(section){
                $scope.sectionListMapping[section.uid] = section;
            })
        });

        organisationUnitGroupService.getAll().then(function(data){
            $scope.organisationUnitGroupsMapping = [];
            angular.forEach(data.organisationUnitGroups, function(section){
                $scope.organisationUnitGroupsMapping[section.id] = section;
            })
        });

    });
