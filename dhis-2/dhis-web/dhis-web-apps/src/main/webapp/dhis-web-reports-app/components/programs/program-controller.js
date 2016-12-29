/**
 * Created by harsh on 22/6/15.
 */


reportsApp.controller('ProgramsController',
    function ($rootScope,$scope, $location,$route,$window,
              $modal,
              UserGroupService,
              ReportAppSectionSettingService,
              GenerateUidService,
              $timeout)
    {

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.allSections = data.sections;
        });

        $scope.currentSection = {
            uid: "",
            name: "",
            userGroupUid: ""
        };

        $scope.alertMsg = "All fields are required";

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.sectionSettings = data;
        });

        // creating map for user group uid to name
        UserGroupService.getAllUserGroup().then(function(data){
            $scope.userGroupsMap = [];
            angular.forEach(data.userGroups, function(userGroup){
                $scope.userGroupsMap[userGroup.id] = userGroup;
            })
        });

        // method for open add popup
        $scope.addSectionForm = function () {
            //alert("add section");
            $('#addSectionModal').modal('show');

            UserGroupService.getAllUserGroup().then(function(data){
                    $scope.userGroups = data.userGroups;
                }
            )
        };

        //add instance action
        $scope.addSection = function() {
            if(validateAddSectionForm())
            {
                GenerateUidService.getUid().then(function(data){
                    $scope.currentSection.uid = data.codes[0];

                    if ($scope.sectionSettings == ""){
                        // create new json
                        $scope.sectionSettings={ "sections": [{
                            "uid": $scope.currentSection.uid,
                            "name": $scope.currentSection.name,
                            "userGroupUid": $scope.currentSection.userGroupUid
                        }]
                        }
                    }else{
                        //push to existing json

                        $scope.sectionSettings.sections.push($scope.currentSection);
                    }

                    ReportAppSectionSettingService.saveSection($scope.sectionSettings).then(function (response) {
                        if (response != "")
                        {
                            //window.location.path.('/program-management');
                            //$location.path('/program-management');
                            //$route.reload();
                            $window.location.reload();
                            //console.log( " After save Uid : " + $scope.sectionData.uid + " After save Name : " + $scope.sectionData.name + " After Save User Group : " + $scope.sectionData.userGroupUid )
                        }

                    });

                })
            }
        };


    });