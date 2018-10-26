/**
 * 支付service
 * */
app.service('payService',function ($http) {

    //实现订单状态查询
    this.queryPayStatus=function (tradeoutno) {
        return $http.get('/pay/queryPayStatus.shtml?tradeoutno='+tradeoutno);
    }


    //创建二维码
    this.createNative=function () {
        return $http.get('/pay/createNative.shtml');

    }
})
