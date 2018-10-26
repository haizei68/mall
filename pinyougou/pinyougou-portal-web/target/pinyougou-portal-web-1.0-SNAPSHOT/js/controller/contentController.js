/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("contentController",function($scope,$http,contentService){

    //定义一个对象,存储广告信息
    $scope.categoryList={};

    //查询对应分类的所有广告
    $scope.findContentByCategoryId=function (categoryId) {
        contentService.findContentByCategoryId(categoryId).success(function (response) {
            $scope.categoryList[categoryId]=response;
        })
    }


    //编写js实现跳转
    $scope.search=function () {
        location.href="http://localhost:18088?keyword="+$scope.keyword;
    }

});
