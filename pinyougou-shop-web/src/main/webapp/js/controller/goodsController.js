//控制层
app.controller('goodsController', function ($scope, $controller, goodsService,uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
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

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };

    $scope.entity = {goods:{},goodsDesc:{itemImages:[],customAttributeItems:null}};
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
                   alert(response.message)
                } else {
                    alert(response.message);
                }
                location.reload();
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

    $scope.upload_entity = {color:null,url:null};
    //上传图片
    $scope.upload = function () {
        uploadService.upload().success(function (response) {
            if (response.success) {
                alert("图片上传成功!");
                $scope.upload_entity.url = response.message;
            }else{
                alert(response.message)
            }
        })
    };

    //添加图片至列表
    $scope.addToList = function () {
        $scope.entity.goodsDesc.itemImages.push( $scope.upload_entity);
        document.getElementById("file").value = "";
    };
    //从列表移除图片
    $scope.removeFromList = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    };

    $scope.entity.goods.category1Id = null;
    //加载一级分类列表
    $scope.selectItemCatList_1 = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCatList_1 = response;
        })
    };

    //加载二级分类列表
    $scope.$watch("entity.goods.category1Id",function (newVal, oldVal) {
        if (newVal === null) {
            $scope.entity.goods.category2Id = null;
            $scope.itemCatList_2 = [];
            $scope.selectItemCatList_1();
            return;
        }
        itemCatService.findByParentId(newVal).success(function (response) {
            $scope.itemCatList_2 = response;
        })
    });
    //加载三级分类列表
    $scope.$watch("entity.goods.category2Id",function (newVal, oldVal) {
        if (newVal === null) {
            $scope.entity.goods.category3Id = null;
            $scope.itemCatList_3 = [];
            return;
        }
        itemCatService.findByParentId(newVal).success(function (response) {
            $scope.itemCatList_3 = response;
        })
    });
    //查询模板ID
    $scope.$watch("entity.goods.category3Id",function (newVal, oldVal) {
        if (newVal === null) {
            $scope.entity.goods.typeTemplateId = null;
            return;
        }
        itemCatService.findOne(newVal).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });
    //查询品牌列表
    $scope.brandIds = {};
    $scope.$watch("entity.goods.typeTemplateId",function (newVal, oldVal) {
        if (newVal === null) {
            $scope.brandIds = null;
            $scope.brandIds = [];
            return;
        }
        typeTemplateService.findOne(newVal).success(function (response) {
            $scope.brandIds = JSON.parse(response.brandIds);
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
        })
    });
});
