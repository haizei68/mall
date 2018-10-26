/**
 *
 * Service
 */
app.service('loginService',function ($http) {
    //获取用户名
    this.getUserLoginName=function () {
        return  $http.get('/login/name.shtml');

    }

})