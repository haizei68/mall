/**
 * 搜索实现
 *
 *
 **/
app.service('searchService',function ($http) {

    //搜索服务
    this.search=function (searchMap) {
        return $http.post('/item/search.shtml',searchMap);
    }
})