app.controller('itemController',function ($scope,$http) {
    //购买数量
    $scope.num=1;

    //加入购物车
    $scope.addCart=function(){
        alert("用户要购买的商品ID:"+$scope.sku.id+" ,购买了"+$scope.num+"个");

        //$http发送请求执行购物车的增加
        $http.get('http://localhost:18093/cart/add.shtml?itemid='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(function (response) {
            if (response.success){
                alert('成功了')
                //加入购物车成功,跳转到购物车展示列表页面
                location.href='http://localhost:18093/cart.html';
            } else{
                alert(response.message);
            }

        })

    }

    //点击数量递增
    $scope.addNum=function(num){
        $scope.num=parseInt($scope.num)+parseInt(num);
        if ( $scope.num<=0){
            $scope.num=1;
        }
    }


    //定义一个变量,记录当前用户所选的规格
    $scope.specifications={};

    //记录用户选择的规格的方法
    $scope.selectSpec=function (key,value) {
        $scope.specifications[key]=value;

        //根据当前用户所选择规格记录从itemList中匹配出一个Item对象
        for (var i=0;i<itemList.length;i++){
            //当前被循环的规格
            var jsonspec=angular.fromJson(itemList[i].spec);

            //匹配对象是否一致
            if(angular.equals($scope.specifications,jsonspec)){
                $scope.sku=angular.copy(itemList[i]);
                return;
            }

        }

        //匹配补上说明不存在
        $scope.sku={"id":0,"title":"---该商品已经下架---","price":0,"spec":{}};
    }
    

    //默认的sku{item}
    $scope.sku={};

    //获取默认的SKU
    $scope.loadDefaultSku=function(){
        //深克隆
        $scope.sku=angular.copy(itemList[0]);
        //将默认的Spec给$scope.specifications
        $scope.specifications =angular.fromJson( angular.copy( $scope.sku.spec )); //字符类型需要转JSON

    }


    //定义一个方法,判断是否已经选中该规格
    //每次将规格属性和规格选项传进来
    //判断该规格属性和选项是否已经选中
    //如果选中,则返回true;
    $scope.selectSpecInfo=function (key,value) {
        if ($scope.specifications[key]==value){
            return true;
        }
        return false;
    }

})