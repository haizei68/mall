app.controller('cartController', function ($scope, cartService) {

    //提交订单
    $scope.submitOrder=function(){

        //收货地址 receiverAreaName
        $scope.order.receiverAreaName=$scope.address.address;

        //收货人手机号    receiverMobile
        $scope.order.receiverMobile=$scope.address.mobile;

        //收货人名      receiver
        $scope.order.receiver=$scope.address.contact;


        //提交表单
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
               location.href='pay.html'
            } else{
                location.href='payfail.html'
            }
        })

    }

    //定义支付类型
    $scope.order={paymentType:'1'};   //1.表示在线支付, 2.表示货到付款

    //切换支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType=type;
    }


    //记录用户选择的收货地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }

    //查询用户地址列表
    $scope.addressList=function(){
        cartService.addressList().success(function (response) {
            $scope.addressList=response;

            //查找默认地址
            for (var i=0;i<$scope.addressList.length;i++){
                if ($scope.addressList[i].isDefault=='1'){
                    $scope.address=angular.copy($scope.addressList[i]);
                }
            }

        })
    }


    //查询购物车集合
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //计算总金额和购买数量
            $scope.totalValue = {totalNum: 0, totalMoney: 0};

            for (var i = 0; i < $scope.cartList.length; i++) {
                //当前购物车的购物车明细
                var orderitems = $scope.cartList[i].orderItemList;
                //循环购物车明细
                for (var j = 0; j < orderitems.length; j++) {
                    //购买数量增加
                    $scope.totalValue.totalNum += orderitems[j].num;
                    //总金额计算
                    $scope.totalValue.totalMoney += orderitems[j].totalFee;

                }
            }

        })
    }


    //增加购物车
    $scope.add = function (itemid, num) {
        cartService.add(itemid, num).success(function (response) {
            if (response.success) {
                //刷新页面
                $scope.findCartList();
            } else {
                alert(response.message);
            }

        })
    }


})