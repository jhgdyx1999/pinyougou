app.controller("contentController",function ($scope,$http,$controller,contentService) {
    $controller('baseController', {$scope: $scope});//继承



    $scope.contentList = [];

    $scope.selectContentListByCategoryId = function (categoryId) {
        contentService.selectContentListByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
        });
    };
    //跳转至搜索列表页面
    $scope.keywords = null;
    $scope.searchItem  = function () {
        location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});