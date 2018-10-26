/*用户登录信息
* **/
app.controller('indexController',function ($scope,loginService) {

    //获取登录名
    $scope.showName=function () {
        loginService.showName().success(function (response) {
            $scope.username=response;
        })

    }
})