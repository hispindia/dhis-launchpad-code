/**
 * Created by harsh on 22/6/15.
 */


var reportsAppServices = angular.module('reportsAppServices',[])


    .service('reportsService', function(){

})

// service for generate uid in application
//http://localhost:8090/dhis/api/system/id.json?n=10
//http://localhost:8090/dhis/api/system/id.json
.service('GenerateUidService',  function ($http){
    return {
        getUid: function () {
            var promise = $http.get('../api/system/id.json&paging=false').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})



// service for get all userGroup name and uid
 //http://127.0.0.1:8090/dhis/api/userGroups.json?fields=[id,name]&paging=false
.service('UserGroupService',  function ($http){
        return {
            getAllUserGroup: function () {
                var promise = $http.get('../api/userGroups.json?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
})

// save report app section and get all section
.service('ReportAppSectionSettingService',  function ($http){
    return {
        saveSection: function (sectionData) {
            var sectionDataJson = JSON.stringify(sectionData);
            var promise = $http.post('../api/systemSettings/reportApp-section-json?value=' + sectionDataJson, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function (response) {
                return response.data ;
            });
            return promise;
        },
        getAllReportAppSection: function () {
            var promise = $http.get('../api/systemSettings/reportApp-section-json').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})

// save report app section and get all section
.service('reportSettingService',  function ($http){
    return {
        save: function (reportData) {
            var reportDataJson = JSON.stringify(reportData);
            var promise = $http.post('../api/systemSettings/reportApp-reports-json?value=' + reportDataJson, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function (response) {
                return response.data ;
            });
            return promise;
        },
        getAll: function () {
            var promise = $http.get('../api/systemSettings/reportApp-reports-json').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})

    .service('reportsService',  function ($http){
        return {
            getAll: function () {
                var promise = $http.get('../api/reports.json?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },
            getAllWithDetails : function(){
                var promise = $http.get('../api/reports.json?fields=[id,name,userGroupAccesses]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
    })


    .service('organisationUnitGroupService',  function ($http){
        return {
            getAll: function () {
                var promise = $http.get('../api/organisationUnitGroups?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },
            getOuGroupsByOu: function(ouUid){
                var promise = $http.get('../api/organisationUnits/'+ouUid+'.json?fields=id,name,organisationUnitGroups[id,name]').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
    })

    .service('userService',  function ($http){
        return {
            getCurrentUser: function () {
                var promise = $http.get('../api/currentUser.json?fields=id,name,userGroups[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
    })

    .service('periodService',  function ($http){
        return {
            getLast12Months: function () {

                var list = [
                            {
                                "id": "201501",
                                "name": "Jan 2015"
                            }
                            ];
                return list;
            },
            getMonthList : function() {

                var list = [
                    {
                        "name": "Jan",
                        "id": "01"
                    },
                    {
                        "name": "Feb",
                        "id": "02"
                    },
                    {
                        "name": "Mar",
                        "id": "03"
                    },
                    {
                        "name": "Apr",
                        "id": "04"
                    },
                    {
                        "name": "May",
                        "id": "05"
                    },
                    {
                        "name": "Jun",
                        "id": "06"
                    },
                    {
                        "name": "Jul",
                        "id": "07"
                    },
                    {
                        "name": "Aug",
                        "id": "08"
                    },
                    {
                        "name": "Sep",
                        "id": "09"
                    },
                    {
                        "name": "Oct",
                        "id": "10"
                    },
                    {
                        "name": "Nov",
                        "id": "11"
                    },
                    {
                        "name": "Dec",
                        "id": "12"
                    }
                ]
                return list;
            },
            getYearListBetweenTwoYears: function(startYear,EndYear){
                var list=[];

                for (EndYear;EndYear > startYear;EndYear--){
                    list.push(EndYear);
                }

                return list;
            }
        };
    })
