app.service('cartService',function ($http) {

    //添加订单信息
    this.submitOrder=function (order) {
        return $http.post('/order/add.shtml',order);
    }

    //获取地址信息
    this.addressList=function () {
        return $http.get('/address/user/list.shtml');

    }

    //查询购物车方法
    this.findCartList=function () {
        return $http.get('/cart/list.shtml');

    }

    this.add=function (itemid,num) {
        return  $http.get('/cart/add.shtml?itemid='+itemid+'&num='+num);

    }

})