app.controller('payController', function ($scope, $location, payService) {


    //生成支付二维码
    $scope.generateNative = function () {
        payService.generateNative().success(function (response) {
            $scope.code_url = response.code_url;
            $scope.out_trade_no = response.out_trade_no;
            $scope.total_fee = (response.total_fee / 100).toFixed(2);

            var qr = new QRious({
                element: document.getElementById('qrious'),
                size: 250,
                level: 'H',
                value: response.code_url
            });

            queryPayStatus();
        })
    };

    //查询支付状态
    var queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(function (response) {
            if (response.success) {
                if (response.message === "二维码超时") {
                    $scope.generateNative();
                } else {
                    location.href = "paysuccess.html#?money=" + $scope.total_fee;
                }
            } else {
                location.href = "payfail.html";
            }
        })
    };

    // 获取支付金额
    $scope.getMoney = function () {
        return $location.search()["money"];
    }

});