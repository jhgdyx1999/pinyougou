app.controller('cartController', function ($scope, cartService) {

    $scope.selectCartList = function () {
        cartService.selectCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.total = cartService.sum($scope.cartList);
            }
        );
    };

    //更改购物车商品数
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (response) {
            if (response.success) {
                $scope.selectCartList();
            } else {
                alert(response.message)
            }
        })
    };

    //查询地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for (var i = 0; i <$scope.addressList.length ; i++) {
                if ($scope.addressList[i].isDefault === "1" ) {
                    $scope.currentAddress = $scope.addressList[i];
                    return;
                }
            }
        })
    };
    //选择地址
    $scope.selectAddress = function (address) {
        $scope.currentAddress = address;
    };
    //判断是否为当前地址
    $scope.isCurrentAddress = function (address) {
        return address === $scope.currentAddress;
    };

    //新增收货地址
    $scope.addAddress = function () {
        cartService.addAddress($scope.entity).success(function (response) {
           $scope.findAddressList();
            alert(response.message);
        })
    };

    $scope.order = {"paymentType":"1"};
    //选择支付方式
    $scope.selectPaymentType = function (paymentType) {
            $scope.order.paymentType = paymentType;
    };

    //提交订单
    $scope.submitOrder = function () {

        $scope.order.receiverAreaName = $scope.currentAddress.address;
        $scope.order.receiverMobile = $scope.currentAddress.mobile;
        $scope.order.receiver = $scope.currentAddress.contact;

        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success) {
                if ( $scope.order.paymentType === "1") {
                    location.href="pay.html";
                }else{
                    location.href="paysuccess.html";
                }
            }else{
                alert(response.message);
            }

        })
    }

});