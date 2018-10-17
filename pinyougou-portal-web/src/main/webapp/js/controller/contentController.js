app.controller("contentController",function ($scope,$http,$controller,contentService) {
    $controller('baseController', {$scope: $scope});//继承



    $scope.contentList = [];

    $scope.selectContentListByCategoryId = function (categoryId) {
        contentService.selectContentListByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
        });
    }
});