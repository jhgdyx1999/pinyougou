app.service('payService', function ($http) {
    //生成支付二维码
    this.generateNative = function () {
        return $http.get("pay/generateNative.do");
    };
    //查询支付状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get("pay/queryPayStatus.do?out_trade_no="+out_trade_no);
    }
});