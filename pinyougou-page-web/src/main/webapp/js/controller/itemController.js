app.controller("itemController",function ($scope,$http,$controller,itemService) {


    $controller('baseController', {$scope: $scope});//继承

    $scope.num  = 1;
    $scope.specList = {};
    $scope.currentItem = {};

    $scope.modifyCount = function (num) {
        $scope.num += num;
        if ($scope.num <1) {
            $scope.num  = 1;
        }
    };
    //跟新规格列表
    $scope.updateSpecList = function (specName,specVal) {
        $scope.specList[specName] = specVal;
        $scope.modifyCurrentItem();
    };

    $scope.isSelected = function (specName, specVal) {
        return $scope.specList[specName] === specVal;
    };
    //初始化页面
    $scope.init = function () {
        $scope.specList = JSON.parse(JSON.stringify(skuList[0].spec));
        $scope.currentItem = JSON.parse(JSON.stringify(skuList[0]));
    };
    //跟新规格对应的商品
    $scope.modifyCurrentItem = function () {
        for (var i = 0; i <skuList.length; i++) {
            if (JSON.stringify(skuList[i].spec) ===JSON.stringify($scope.specList)) {
                $scope.currentItem = JSON.parse(JSON.stringify(skuList[i]));
            }
        }
    };
    //加入购物车
    $scope.addToCart = function () {
        alert($scope.currentItem.id)
    }


});