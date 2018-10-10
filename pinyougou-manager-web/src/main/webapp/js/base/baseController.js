app.controller("baseController",function ($scope) {
    //分页
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 20,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };
    //重载数据,清除复选框集合
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage,$scope.searchEntity);
        $scope.selectIds = [];
    };
    //批量选择
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        if ( $event.target.checked) {
            $scope.selectIds.push(id);
        }else{
            $scope.selectIds.splice($scope.selectIds.indexOf(id));
        }
    };
});