app.controller("searchController", function ($scope, $http, $controller,$location, searchService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.searchMap = {
        "keywords": null,
        "category": null,
        "brand": null,
        "spec": {},
        "price": null,
        "pageNum": 1,
        "pageSize": 30,
        "sort": null,
        "sortField": null
    };
    $scope.searchResult = {
        "categoryList": [],
        "brandList": [],
        "specList": [],
        "rows": [],
        "totalPages": null,
        "totalCount": null
    };

    $scope.jumpPage = 1;
    $scope.pageArr = [];

    //获取主页传递的keywords参数
    $scope.getKeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search()
    };

    //构建分页条
    var buildPageBar = function () {
        var startPage = $scope.searchMap.pageNum - 2;
        if (startPage <= 0) {
            startPage = 1;
        }
        var endPage = startPage + 4;
        if (endPage > $scope.searchResult.totalPages) {
            endPage = $scope.searchResult.totalPages;
        }
        if (endPage - startPage + 1 !== $scope.searchResult.totalPages && endPage - startPage !== 4) {
            startPage = endPage - 4;
        }
        for (var i = startPage; i <= endPage; i++) {
            $scope.pageArr.push(i);
        }
    };
    //分页工具条进行分页查询
    $scope.queryForPage = function (page) {
        if (page <= 0) {
            page = 1;
        }
        if (page > $scope.searchResult.totalPages) {
            page = $scope.searchResult.totalPages;
        }
        $scope.pageArr = [];
        $scope.searchMap.pageNum = page;
        $scope.search();
        $scope.jumpPage = 1;


    };

    $scope.search = function () {
        $location.search("keywords",$scope.searchMap.keywords);
        $scope.pageArr = [];
        $scope.isBrand = false;
        searchService.search($scope.searchMap).success(function (response) {
            $scope.searchResult = response;
            buildPageBar();
            $scope.keywordsContainsBrand();
        });
    };


    //构造查询条件(添加条件)
    $scope.appendSearchCondition = function (conditionName, conditionValue) {
        if (conditionName === "category" || conditionName === "brand" || conditionName === "price") {
            $scope.searchMap[conditionName] = conditionValue;
        } else {
            $scope.searchMap.spec[conditionName] = conditionValue;
        }
        ;
        $scope.queryForPage(1);
    };

    //构造查询条件(移除条件)
    $scope.removeSearchCondition = function (conditionName) {
        if (conditionName === "category" || conditionName === "brand" || conditionName === "price") {
            $scope.searchMap[conditionName] = null;
        } else {
            delete $scope.searchMap.spec[conditionName];
        }
        ;
        $scope.queryForPage(1);
    };

    //排序
    $scope.sortQuery = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.queryForPage(1);
    };
    //隐藏品牌列表(搜索关键字内是否包含品牌)
    $scope.isBrand = false;
    $scope.keywordsContainsBrand = function () {
        for (var i = 0; i < $scope.searchResult.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.searchResult.brandList[i].text) >= 0) {
                $scope.isBrand = true ;
                return;
            }

        }
    };


});