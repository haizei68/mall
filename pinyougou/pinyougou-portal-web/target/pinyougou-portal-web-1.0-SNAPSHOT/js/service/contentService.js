/***
 * 创建一个服务层
 * 抽取发送请求的一部分代码
 * */
app.service("contentService",function($http){

   //查询广告数据
    this.findContentByCategoryId=function (categoryId) {
        return  $http.get('/content/findByCategortId.shtml?categoryId='+categoryId);
    }

});
