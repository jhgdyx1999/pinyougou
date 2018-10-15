//控制层
app.controller('goodsController', function ($scope, $controller, goodsService,uploadService,itemCatService,typeTemplateService) {

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

    $scope.entity = {goods:{},goodsDesc:{itemImages:[],customAttributeItems:null,specificationItems:[]},itemList:[]};
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
        $scope.entity.goods.category2Id = null;
        if (newVal === null) {
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
        $scope.entity.goods.category3Id = null;
        if (newVal === null) {
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
            $scope.entity.goods.brandId = null;
            $scope.brandIds = [];
            $scope.specList = [];
            $scope.entity.goodsDesc.specificationItems = [];
            return;
        }
        $scope.entity.goodsDesc.specificationItems = [];
        typeTemplateService.findOne(newVal).success(function (response) {
            $scope.brandIds = JSON.parse(response.brandIds);
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
        });
        typeTemplateService.selectSpecificationListWithOptions(newVal).success(function (response) {
            $scope.specList = response;
        });

        //规格属性复选框的增删操作
        $scope.updateSpecificationItems = function ($event,name,value) {
            var obj = $scope.containsSpecificFieldValue($scope.entity.goodsDesc.specificationItems,"attributeName",name);
            if (obj != null) {
                if ($event.target.checked) {
                    obj["attributeValue"].push(value);
                }else{
                    obj["attributeValue"].splice(obj["attributeValue"].indexOf(value),1);
                    if ( obj["attributeValue"].length === 0){
                        $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1)
                    }
                }
            }else{
                $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
            }
        };

        //动态生成规格选项表
        $scope.generateTable = function () {
            $scope.entity.itemList = [{spec:{},price:null,num:null,status:"0",isDefault:"0"}];
            var specificationItems = $scope.entity.goodsDesc.specificationItems;
            for (var i = 0; i <specificationItems.length; i++) {
                $scope.entity.itemList = addColumn( $scope.entity.itemList,specificationItems[i]["attributeName"],specificationItems[i]["attributeValue"]);
            }
        };
        //复制行对象并根据新增列属性动态新增行对象,生成新的行集合
        var addColumn = function (list,columnName,columnValues) {
            var newList = [];
            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];
                for (var j = 0; j < columnValues.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));
                    newRow.spec[columnName] = columnValues[j];
                    newList.push(newRow);
                }
            }
            return newList;
        };
    });
});
