var app =angular.module("pinyougou", []);

//解析html格式的数据
app.filter("trustAsHtml", ["$sce",function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);