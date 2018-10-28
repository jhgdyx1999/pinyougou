//控制层
app.controller('seckillGoodsController', function ($scope, $controller, $location, $interval, seckillGoodsService,seckillOrderService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.findList = function () {
        seckillGoodsService.findList().success(function (response) {
            $scope.list = response;
        })
    };


    $scope.findOne = function () {
        var id = $location.search()["id"];
        seckillGoodsService.findOne(id).success(function (response) {
            $scope.entity = response;
            $interval(function () {
                $scope.timeCountDown = generateTimeCountStr(response.endTime);
            }, 1000);
        })
    };

    $scope.time = 10;
    $interval(function () {
        $scope.time--;
    }, 1000);

    var generateTimeCountStr = function (endTime) {
        //倒计时
        var timeToGo = Math.floor((new Date(endTime).getTime() - new Date().getTime()) / 1000);
        var days = Math.floor(timeToGo / (3600 * 24));
        var hours = Math.floor((timeToGo - days * 3600 * 24) / 3600);
        var minutes = Math.floor((timeToGo - days * 3600 * 24 - hours * 3600) / 60);
        var seconds = timeToGo - days * 3600 * 24 - hours * 3600 - minutes * 60;

        var timeCountDown = "";
        if (days > 0) {
            timeCountDown += days + "天"
        }
        if (hours > 9) {
            timeCountDown += hours + ":";
        } else {
            timeCountDown += "0" + hours + ":";
        }
        if (minutes > 9) {
            timeCountDown += minutes + ":";
        } else {
            timeCountDown += "0" + minutes + ":";
        }
        if (seconds > 9) {
            timeCountDown += seconds;
        } else {
            timeCountDown += "0" + seconds;
        }
        return timeCountDown;
    };

    //秒杀下单
    $scope.submitOrder = function () {
        seckillOrderService.submitOrder($scope.entity.id).success(function (response) {
            if (response.success) {
                location.href = "pay.html"
            }else{
                alert(response.message);
            }
        })
    }
});	
