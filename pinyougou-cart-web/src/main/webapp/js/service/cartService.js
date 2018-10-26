app.service('cartService', function ($http) {
    //购物车列表
    this.selectCartList = function () {
        return $http.get('cart/selectCartList.do');
    };
    //更改购物车商品数
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    };
    //获取购物车总商品数及总价
    this.sum = function (cartList) {
        var total = {"totalNum": 0, "totalMoney": 0};
        for (var i = 0; i < cartList.length; i++) {
            var orderItemList = cartList[i].orderItemList;
            for (var j = 0; j < orderItemList.length; j++) {
                total.totalNum += orderItemList[j].num;
                total.totalMoney += orderItemList[j].totalFee;
            }
        }
    return total;
    };
    //查询用户地址列表
    this.findAddressList = function () {
        return $http.get("address/findAddressListByUsername.do");
    };

    this.addAddress = function (address) {
        return $http.post("address/add.do",address);
    };
    //提交订单
    this.submitOrder = function (order) {
        return $http.post("order/add.do",order);
    }

});