/***
 * 创建一个服务层
 * 抽取发送请求的一部分代码
 * */
app.service("brandService",function($http){

    //查询列表
    this.findAll=function(page,size,searchEntity){
        return $http.post("/brand/list.shtml?page="+page+"&size="+size,searchEntity);
    }

    //增加Brand
    this.add=function(entity){
        return $http.post("/brand/add.shtml",entity);
    }

    //保存
    this.update=function(entity){
        return $http.post("/brand/update.shtml",entity);
    }

    //根据ID查询
    this.findOne=function(id){
        return $http.get("/brand/"+id+".shtml");
    }

    //批量删除
    this.delete=function(ids){
        return $http.post("/brand/delete.shtml",ids);
    }


    //查询品牌集合
    this.findBrandlist = function () {
        return $http.get('/brand/jsonlist.shtml')
    }

});
