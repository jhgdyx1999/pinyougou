app.controller("searchController",function ($scope,$http,$controller,searchService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.searchMap = {"keywords":null,"category":null,"brand":null,"spec":{}};

    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.searchResult = response;
        });
    };
    //构造查询条件(添加条件)
    $scope.appendSearchCondition = function (conditionName,conditionValue) {
        if (conditionName ==="category" || conditionName=== "brand"){
            $scope.searchMap[conditionName] = conditionValue;
        }else {
            $scope.searchMap.spec[conditionName] = conditionValue;
        };
        $scope.search();
    };
    
    //构造查询条件(移除条件)
    $scope.removeSearchCondition = function (conditionName) {
        if (conditionName ==="category" || conditionName=== "brand"){
            $scope.searchMap[conditionName] = null;
        }else {
           delete $scope.searchMap.spec[conditionName];
        };
        $scope.search();
    }
});