app.service("contentService",function ($http) {
    //根据广告类型ID查询广告列表
    this.selectContentListByCategoryId = function (categoryId) {
     return $http.get("content/selectContentListByCategoryId.do?categoryId="+categoryId);
    };

});
