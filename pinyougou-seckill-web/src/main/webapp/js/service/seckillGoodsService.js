//服务层
app.service('seckillGoodsService', function ($http) {

    //查询所有有效秒杀商品
    this.findList = function () {
        return $http.get('seckillGoods/findList.do');
    };
    //从缓存中查询单个秒杀商品
    this.findOne = function (id) {
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }
});
