/**
 *
 *创建支付controller
 */
app.controller('payController',function ($scope,payService) {

    //二维码创建方法
    $scope.createNative=function () {
        payService.createNative().success(function (response) {

            //获取二维码地址
            $scope.url=response.code_url;
            $scope.trade_out_no=response.trade_out_no;

            //创建二维码
            var qr =window.qr= new QRious({
                element:document.getElementById('payimg'),
                size:300,
                value:$scope.url
            })

            //查询状态
            payService.queryPayStatus($scope.trade_out_no).success(function (response) {
                if(response.success){
                    //跳转到支付成功页面
                    location.href='/paysuccess.html';
                }else{
                    if (response.message == 'timeout') {
                         //过期,重新创建二维码
                        //$scope.createNative();
                        location.href='/payfail.html';
                    }else{
                        location.href='/payfail.html';
                    }
                }
            })
            
            
        })
    }
})