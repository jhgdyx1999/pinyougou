app.controller("indexController", function ($scope, $http, loginService, $controller) {

    $controller("baseController", {$scope: $scope});

    $scope.getLoginName = function () {
        loginService.getLoginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    };

});