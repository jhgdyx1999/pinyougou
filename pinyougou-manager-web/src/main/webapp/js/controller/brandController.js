app.controller("brandController", function ($scope, $http,brandService, $controller) {

    $controller("baseController",{$scope:$scope});

    $scope.searchEntity = {};
    $scope.findPage = function (searchEntity,page, size) {
        brandService.findPage(searchEntity,page, size).success(function (data) {
            $scope.brands = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //新增或修改品牌
    $scope.save = function () {
        var method = brandService.insert($scope.entity);
        if ($scope.entity.id != null) {
            method = brandService.update($scope.entity);
        }
        method.success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message);
            }
        })
    };
    //品牌信息回显
    $scope.findOne = function(id){
        brandService.findOne(id).success(function (data) {
            $scope.entity = data;
        })
    };
    //批量删除
    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message);
            }
            $scope.selectIds = [];
        })
    };
});