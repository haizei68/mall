app.controller('loginController',function ($scope,loginService) {

    //查询用户登录名
    $scope.getUsername=function () {
        loginService.getUserLoginName().success(function (response) {
            //将用户名存入大scope中
            $scope.username=response;
        })

    }
});