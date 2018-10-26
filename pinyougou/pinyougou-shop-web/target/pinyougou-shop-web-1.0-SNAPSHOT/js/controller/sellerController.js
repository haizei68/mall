/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("sellerController",function($scope,$http,$controller,sellerService){

    //继承父控制器
    $controller("baseController", {$scope: $scope});

    //用户异步登录
    $scope.login=function(){
        $scope.msg="正在登录...";

        sellerService.login($scope.username,$scope.password).success(function (response) {
            //登录
            if (response.success){
                //跳转到对应页面
                location.href=response.message;
            }else{
                //显示错误信息
                $scope.msg=response.message;

            }
        })
    }


    //获取所有的Seller信息
    $scope.getPage = function (page, size) {
        //发送请求获取数据
        sellerService.findAll(page, size, $scope.searchEntity).success(function (response) {
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems = response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function(){
        //增加操作
        var result = sellerService.add($scope.entity);
        //判断操作流程
        result.success(function(response){
            //判断执行状态
            if(response.success){
                //重新加载新的数据,提醒登录
                location.href="/shoplogin.html";
            }else{
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //根据ID查询信息
    $scope.getById = function (id) {
        sellerService.findOne(id).success(function (response) {
            //将后台的数据绑定到前台
            $scope.entity = response;
        });
    }

    //批量删除
    $scope.delete = function () {
        sellerService.delete($scope.selectids).success(function (response) {
            //判断删除状态
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }
});
