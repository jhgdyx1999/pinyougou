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

    $scope.jsonToString = function(jsonStr,field){
       var jsonObj =  JSON.parse(jsonStr);
       var strObj = "";
        for (var i = 0; i < jsonObj.length; i++) {
            strObj += jsonObj[i][field]+",";
        }
        if (strObj.length>0){
            strObj = strObj.substring(0,strObj.length-1)
        }
        return strObj;
    };

    //验证数组内是否包含特定属性,若存在则返回集合对象
    $scope.containsSpecificFieldValue = function (list,attrName,attrValue) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][attrName] === attrValue) {
                return list[i];
            }
        }
        return null;
    }
});