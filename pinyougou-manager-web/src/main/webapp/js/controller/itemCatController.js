//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                $scope.entity.typeId = {"id": response.typeId,"text": null};
                typeTemplateService.findOne($scope.entity.typeId.id).success(function (response) {
                    $scope.entity.typeId.text = response.name;
                });

            }
        );
    };
    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    $scope.selectSubList($scope.entity_copy);
                } else {
                    alert(response.message);
                }
            }
        );
    };
    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.selectSubList($scope.entity_copy);
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //根据父id查询
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.list = response;
        })
    };

    $scope.grade = 0;
    $scope.setGrade = function (value) {
        $scope.grade = value;
    };
    //查询子菜单
    $scope.selectSubList = function (entity) {
        if ($scope.grade === 0) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade === 1) {
            $scope.entity_1 = entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade === 2) {
            $scope.entity_2 = entity;
        }
        $scope.entity_copy = entity;
        $scope.parentId = entity.id;
        $scope.findByParentId(entity.id);
    };
    //获取类型模板
    $scope.templateList = {data: []};
    $scope.selectAllTemplates = function () {
        typeTemplateService.selectAllTemplates().success(function (response) {
            $scope.templateList = {data: response};
        })
    };
    //设置父类id
    $scope.entity = {parentId: null, name: null, typeId: null};
    $scope.setParentId = function () {
        $scope.entity.parentId = $scope.parentId;
    };
    //重新设置实体类的typeId
    $scope.resetTypeId = function () {
        $scope.entity.typeId = $scope.entity.typeId.id;
    }
});
