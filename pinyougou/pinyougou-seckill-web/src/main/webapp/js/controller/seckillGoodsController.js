app.controller('seckillGoodsController',function ($scope,$interval,$location,seckillGoodsService) {

    //下单操作
    $scope.add=function(){
        //获取地址栏对应的数据
        var id=$location.search()['id'];

        //调用下单操作
        seckillGoodsService.add(id).success(function (response){
            if (response.success){
                //下单成功,跳转支付
               location.href="/pay.html";
                //alert('订单下单成功');
            } else{
                //403
                if (response.message=='403'){
                    //CAS---原路返回
                    location.href="/login/loading.shtml";
                } else{
                    alert(response.message);
                }

            }
        })
    }


    //根据ID查询商品详情
    $scope.getOne=function(){
        //获取地址栏对应的数据
        var  id=$location.search()['id'];
        if (!isNaN(id)){
            seckillGoodsService.getOne(id).success(function (response) {
                $scope.item=response;

                //num=活动结束时间-当前时间
                var num=new Date($scope.item.endTime).getTime()-new Date().getTime();

                //倒计时
                var time = $interval(function () {
                    //时间每次都减去一秒
                    num-=1000;
                    if(num<=0){
                        //结束循环
                        $interval.cancel(time);

                        $scope.datestr="已结束";
                    }else{
                        $scope.datestr = datefloor(num);
                    }
                },1000);

            })
        }
    }


    //秒杀商品列表
    $scope.list=function () {
        seckillGoodsService.list().success(function (response) {
            $scope.list=response;
        })
    }

    //将秒转成多少天,多少小时,多少分钟,多小秒
    //将毫秒转成多少天、多少小时、多少分钟、多少秒
    datefloor=function (num) {
        //1秒有N毫秒
        var second=1000;
        //1分钟有多少毫秒
        var minute=second*60;
        //1小时有多少毫秒
        var hour=minute*60;
        //1天有多少毫秒
        var day=hour*24;


        //天  总时间/day 取整数
        var days = Math.floor(num/day);

        //小时   (总时间-天)/hour
        var housrs =Math.floor((num%day)/hour);

        //分钟   总时间-总共的小时数据
        var minutes =Math.floor((num%hour)/minute)

        //秒     总时间-总分钟的时间
        var seconds =Math.floor((num%minute)/second);

        //总时间=天+时+分+秒
        return days+'天'+housrs+'小时'+minutes+'分钟'+seconds+'秒';
    }
})