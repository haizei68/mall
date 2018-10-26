/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("goodsController", function ($scope, $http, $controller, goodsService, itemCatService) {

    //继承父控制器
    $controller("baseController", {$scope: $scope});

    //定义一个状态数组
    $scope.status = ['未审核', '审核通过', '未审核通过', '关闭'];

    //查询分类,然后将分类信息组装成key-value格式
    $scope.itemCatList = {};

    //修改商品状态
    $scope.updateStatus=function(status){
        goodsService.updateStatus($scope.selectids,status).success(function (response) {
            if (response.success){
                //审核成功,刷新页面
                $scope.reloadList();

                //清空选中的id集合
                $scope.selectids=[]
            } else{
                alert(response.message);
            }
        })
    }

    //查询所有分类
    $scope.findAllItemCatList = function () {
        itemCatService.findAllList().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                var key = response[i].id;
                var value = response[i].name;
                $scope.itemCatList[key] = value;
            }
        })
    }


    //获取所有的Goods信息
    $scope.getPage = function (page, size) {
        //发送请求获取数据
        goodsService.findAll(page, size, $scope.searchEntity).success(function (response) {
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems = response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function () {
        var result = null;
        if ($scope.entity.id != null) {
            //执行修改数据
            result = goodsService.update($scope.entity);
        } else {
            //增加操作
            result = goodsService.add($scope.entity);
        }
        //判断操作流程
        result.success(function (response) {
            //判断执行状态
            if (response.success) {
                //重新加载新的数据
                $scope.reloadList();
            } else {
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //根据ID查询信息
    $scope.getById = function (id) {
        goodsService.findOne(id).success(function (response) {
            //将后台的数据绑定到前台
            $scope.entity = response;
        });
    }

    //批量删除
    $scope.delete = function () {
        goodsService.delete($scope.selectids).success(function (response) {
            //判断删除状态
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }
});
