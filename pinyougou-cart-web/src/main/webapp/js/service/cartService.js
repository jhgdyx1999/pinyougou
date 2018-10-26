app.service('cartService', function ($http) {
    //购物车列表
    this.selectCartListFromCookie = function () {
        return $http.get('cart/selectCartListFromCookie.do');
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
    }


});