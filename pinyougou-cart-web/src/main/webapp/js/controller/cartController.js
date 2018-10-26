app.controller('cartController', function ($scope, cartService) {

    $scope.selectCartListFromCookie = function () {
        cartService.selectCartListFromCookie().success(
            function (response) {
                $scope.cartList = response;
                $scope.total = cartService.sum($scope.cartList);
            }
        );
    };
    //更改购物车商品数
    $scope.addGoodsToCartList = function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if (response.success) {
                $scope.selectCartListFromCookie();


            }else{
                alert(response.message)
            }
        })
    }

});