app.service("searchService",function ($http) {
    //根据广告类型ID查询广告列表
    this.search = function (searchMap) {
     return $http.post("itemSearch/search.do",searchMap);
    };

});
