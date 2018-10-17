//控制层
app.controller('goodsController', function ($scope, $controller, $location,goodsService,uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.selectIdsByButton = [];
    $scope.marketableOperation = null;

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
    //选择执行操作
    $scope.selectOperation = function(ops){
        $scope.marketableOperation = ops;
    };

    //商品上下架操作
    $scope.updateIsMarketableStatus = function (status) {
        var selectIds = [];
        if ($scope.marketableOperation === 1) {
            selectIds =  $scope.selectIdsByButton;
        }else{
            selectIds = $scope.selectIds
        }
        goodsService.updateIsMarketableStatus(selectIds,status).success(function (response) {
            if (response.success) {
                $scope.reloadList();//刷新列表
            }else{
                alert(response.message);
            }
            $scope.selectIdsByButton = [];
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

    $scope.entity = {goods:{},goodsDesc:{itemImages:[],customAttributeItems:null,specificationItems:[]},itemList:[]};
    //查询实体
    var id = $location.search()["id"];
    //用以标记在修改时是否是首次加载下拉菜单
    var count = 0;
    $scope.findOne = function () {
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //加载富文本编辑器内容
                editor.html($scope.entity.goodsDesc.introduction);
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
        if (id == null || count !==0) {
            $scope.entity.goods.category2Id = null;
            if (newVal === null) {
                $scope.itemCatList_2 = [];
                return;
            }
        }
        itemCatService.findByParentId(newVal).success(function (response) {
            $scope.itemCatList_2 = response;
        })
    });
    //加载三级分类列表
    $scope.$watch("entity.goods.category2Id",function (newVal, oldVal) {
        if (id == null || count !==0) {
            $scope.entity.goods.category3Id = null;
            if (newVal === null) {
                $scope.itemCatList_3 = [];
                return;
            }
        }
        itemCatService.findByParentId(newVal).success(function (response) {
            $scope.itemCatList_3 = response;
        })
    });

    //查询模板ID
    $scope.$watch("entity.goods.category3Id",function (newVal, oldVal) {
        if (newVal === null) {
            $scope.entity.goods.typeTemplateId = null;
            $scope.entity.goodsDesc.customAttributeItems = null;
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
            $scope.entity.goods.isEnableSpec = 0;
            return;
        }
        typeTemplateService.findOne(newVal).success(function (response) {
            $scope.brandIds = JSON.parse(response.brandIds);
            if (count !== 0 || id == null) {
                //读取模板扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
            count=1;
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
        //加载规格选项
        $scope.checkSpecItems = function (optionName,optionValue) {
            var specificationItems = $scope.entity.goodsDesc.specificationItems;
            var obj = $scope.containsSpecificFieldValue(specificationItems,"attributeName",optionName);
            return (obj != null && obj.attributeValue.indexOf(optionValue) >=0);
        };


    });
});
