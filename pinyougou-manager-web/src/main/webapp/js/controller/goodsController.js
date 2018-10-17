//控制层
app.controller('goodsController', function ($scope, $controller, $location,goodsService,itemCatService,typeTemplateService,brandService) {
    $controller('baseController', {$scope: $scope});//继承

    //自定义审核状态数组
    $scope.statusAudit = ['未审核','已审核','审核未通过','关闭'];

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };
    $scope.itemCatList = [];
    //初始化商品分类集合
    $scope.selectAllItemCat = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.itemCatList[response[i]["id"]] = response[i]["name"];
            }
        })
    };
    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    //查询一级分类名称
    $scope.selectCategory1Name = function () {
        itemCatService.findOne($scope.entity.goods.category1Id).success(function (response) {
            $scope.category1Name = response.name;
        })
    };
    //查询二级分类名称
    $scope.selectCategory2Name = function () {
        itemCatService.findOne($scope.entity.goods.category2Id).success(function (response) {
            $scope.category2Name = response.name;
        })
    };
    //查询三级分类名称
    $scope.selectCategory3Name = function () {
        itemCatService.findOne($scope.entity.goods.category3Id).success(function (response) {
            $scope.category3Name = response.name;
        })
    };
    //查询品牌名称
    $scope.selectBrandName = function(){
        brandService.findOne($scope.entity.goods.brandId).success(function (response) {
            $scope.brandName = response.name;
        });
    };

    $scope.entity = {goods:{},goodsDesc:{itemImages:[],customAttributeItems:null,specificationItems:[]},itemList:[]};
    //查询实体
    var id = $location.search()["id"];
    //用以标记在修改时是否是首次加载下拉菜单
    $scope.findOne = function () {
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                $scope.selectCategory1Name();
                $scope.selectCategory2Name();
                $scope.selectCategory3Name();
                $scope.selectBrandName();
                //加载富文本编辑器内容
                customEditor.html($scope.entity.goodsDesc.introduction);
                //加载商品图片
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //加载扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //加载规格选项
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //加载SKU规格列表
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        );
    };


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert(response.message);
                } else {
                    alert(response.message);
                }
                location.href = "goods.html";
                $scope.entity = {};
            }
        );
    };

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    $scope.entity.goods.category1Id = null;
    $scope.entity.goods.category2Id = null;
    $scope.entity.goods.category3Id = null;


    $scope.updateAuditStatus = function (id,status) {
        goodsService.updateAuditStatus(id,status).success(function (response) {
            if (response.success) {
                alert(response.message);
            } else {
                alert(response.message);
            }
            location.href = "goods.html";
            $scope.entity = {};
        })
    };
    //批量删除
    $scope.dele = function () {
        goodsService.dele($scope.selectIds).success(function (data) {
            if (data.success) {
                $scope.reloadList();
            } else {
                alert(data.message);
            }
            $scope.selectIds = [];
        })
    };
});
