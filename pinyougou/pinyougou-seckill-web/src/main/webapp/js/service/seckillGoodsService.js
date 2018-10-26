app.service('seckillGoodsService',function ($http) {

    this.add=function (id) {
        return $http.get('/seckill/order/add.shtml?id='+id);
    }

    //根据ID到后台查询商品详情
    this.getOne=function (id) {
        return $http.get('/seckill/goods/one.shtml?id='+id);
    }

    //查询所有商品列表
    this.list=function () {
        return $http.get('/seckill/goods/list.shtml');

    }
})