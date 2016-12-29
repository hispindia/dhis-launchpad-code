/**
 * Created by harsh on 22/6/15.
 */

reportsApp.controller('ReportGenerationController',
    function ($rootScope,$scope, $location,reportSettingService,reportsService,
              ReportAppSectionSettingService,periodService,$window,organisationUnitGroupService,userService) {

        var clearAllValues = function(){
            $scope.currentReport={
                "id":"",
                "section":"",
                "orgUnit":"",
                "periodType":"",
                "startDate":"",
                "endDate":"",
                "period": {"month" : "",
                            "year" : ""}
            }
        }

        $scope.reportSettingsBackup=[];
        clearAllValues();

        var updateReportSetting = function(){
            clearAllValues()
            reportSettingService.getAll().then(function(data){
                $scope.reportSettings = data.reports;

                $scope.reportSettingMapping = [];
                angular.forEach($scope.reportSettings, function(report){
                    report.name = $scope.reportListMapping[report.id].name;
                    report.userGroupAccesses = $scope.reportListMapping[report.id].userGroupAccesses;

                    $scope.reportSettingMapping[report.id] = report;
                    angular.copy($scope.reportSettings,$scope.reportSettingsBackup);

                })

                organisationUnitGroupService.getOuGroupsByOu($scope.selectedOrgUnit).then(function(data){
                    //make a map for quick comparison
                    $scope.organisationUnitGroupMap = [];
                    angular.forEach(data.organisationUnitGroups,function(ouGroup){
                        $scope.organisationUnitGroupMap[ouGroup.id] = ouGroup;
                    })

                    angular.forEach($scope.reportSettings,function(report,index){
                        if ($scope.organisationUnitGroupMap[report.orgUnitGroup] == undefined || !isReportInUserGroup(report.userGroupAccesses)){
                            $scope.reportSettings.splice(index,1);
                       //     delete($scope.reportSettings[index]);
                        }
                    })
                    $scope.updatePeriods();

                })

            });
        }
        $scope.updatePeriods = function(){
            if (true){
                var currentDate = new Date();
                $scope.monthList = periodService.getMonthList();
                $scope.yearList = periodService.getYearListBetweenTwoYears(1900,currentDate.getFullYear());

            }else{
                $scope.periodList = periodService.getLast12Months();
            }
        }
        //initialize
        updateReportSetting();

            $scope.listenToOuChange = function(){
        $scope.selectedOrgUnit = selection.getSelected();
            $scope.currentReport.orgUnit = $scope.selectedOrgUnit;
            updateReportSetting();

        };

        userService.getCurrentUser().then(function(user){
            $scope.currentUser = user;
            $scope.userGroupMap = [];
            angular.forEach($scope.currentUser.userGroups,function(userGroup,index){
                $scope.userGroupMap[userGroup.id] = userGroup;
            })
        });
        selection.setListenerFunction($scope.listenToOuChange);


        ReportAppSectionSettingService.getAllReportAppSection().then(function(data) {
            $scope.sectionList = data.sections;
        })
          // get DHIS reports
            reportsService.getAllWithDetails().then(function(data){
            $scope.reportList = data.reports;
                $scope.reportListMapping = [];
                angular.forEach($scope.reportList,function(report){
                    $scope.reportListMapping[report.id] = report;
                })

                updateReportSetting();
        });

        var fetchReportSettings = function(){

            reportSettingService.getAll().then(function(data){
                $scope.reportSettings = data.reports;
                $scope.reportSettingMapping = [];
                angular.forEach($scope.reportSettings, function(report){
                        report.name = $scope.reportListMapping[report.id].name;
                        report.userGroupAccesses = $scope.reportListMapping[report.id].userGroupAccesses;

                    $scope.reportSettingMapping[report.id] = report;
                })
            });

        }

        var isReportInUserGroup = function(userGroupAccesses){
            var returnVariable = false;
          angular.forEach(userGroupAccesses,function(userGroupAccess){
                if ($scope.userGroupMap[userGroupAccess.userGroupUid] != undefined)
                    returnVariable = true;
          })
            return returnVariable;
        }



        $scope.generateReport = function(){
		//alert( $scope.currentReport.period.year );
		if( $scope.currentReport.period.year == "" ) 
		{
			$scope.currentReport.period.year = ($scope.currentReport.startDate).split("-")[0];
			$scope.currentReport.period.month = ($scope.currentReport.startDate).split("-")[1]; 
		}	
            $window.location.href = "../dhis-web-reporting/generateHtmlReport.action?uid="+$scope.currentReport.id+"&pe="+$scope.currentReport.period.year+""+$scope.currentReport.period.month+"&ou="+ selection.getSelected()+"&sd="+$scope.currentReport.startDate+"&ed="+$scope.currentReport.endDate;
        }
    });
