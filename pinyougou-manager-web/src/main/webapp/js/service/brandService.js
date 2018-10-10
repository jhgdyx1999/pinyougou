app.service("brandService",function ($http) {
    //分页条件查询
    this.findPage = function (searchEntity,page, size) {
        return  $http.post("../brand/search.do?page=" + page + "&size=" + size,searchEntity);
    };
    //添加
    this.insert = function (entity) {
        return  $http.post("../brand/insert.do", entity);
    };
    //修改
    this.update = function (entity) {
        return  $http.post("../brand/update.do", entity);
    };
    //显示需要修改的品牌信息
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }
    //删除
    this.dele = function (ids) {
        return $http.get("../brand/del.do?ids="+ids);
    }

});