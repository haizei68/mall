/***
 * 创建一个服务层
 * 抽取发送请求的一部分代码
 * */
app.service("userService",function($http){

    //调用后台发送验证码
    this.createCode=function (phone) {
        return $http.get('/user/createCode.shtml?phone='+phone);
    }

    //注册
    this.reg=function(entity,code){
        return $http.post("/user/add.shtml?code="+code,entity);
    }


});
