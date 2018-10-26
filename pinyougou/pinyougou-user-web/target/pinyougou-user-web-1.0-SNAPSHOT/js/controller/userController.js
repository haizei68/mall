/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("userController", function ($scope, userService) {

    //获取验证码
    $scope.createCode=function(){
        userService.createCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }

    //注册
    $scope.reg = function () {
        //判断用户密码是否一致
        if ($scope.entity.password != $scope.password) {
            alert("两次输入的密码不一致,请重新输入")
            return;
        }

        userService.reg($scope.entity,$scope.code).success(function (response) {
            alert(response.message);

        })
    }

})

