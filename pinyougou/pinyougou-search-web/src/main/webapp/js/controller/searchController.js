/*
* 搜索controller
* **/
app.controller('searchController',function ($scope,$location,searchService) {

    //提交的数据存入到$scope.searchMap
    //pageNum:当前页
    //rows:每页显示多少条
    $scope.searchMap = {
        "keyword": "",
        "category": "",
        "brand": "",
        spec: {},
        "price": "",
        "pageNum": 1,
        "size": 10,
        "sortField": "",
        "sort": ""
    };

    //定义一个变量,存储所有品牌
    $scope.resultMap = {brandList: []};

    //获取搜索的关键字
    $scope.loadKeyword=function(){
        //获取地址栏的关键字
        var keyword=$location.search()['keyword'];
        if (keyword!=null){
            $scope.searchMap.keyword=keyword;
        }
    }

    //判断当前关键词中是否存在品牌
    $scope.hasBrand = function () {
        if ($scope.resultMap.brandList != null) {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            //当前品牌名字
            var brandName = $scope.resultMap.brandList[i].text;
            //判断关键字中是否包含品牌
            var brandIndex = $scope.searchMap.keyword.indexOf(brandName);
            if (brandIndex >= 0) {
                //存在

                $scope.resultMap.brand=brandName;
                return true;
            }
        }
    }
   return false;
}

    //排序呢点击方法
    $scope.sortSearch=function(sortField,sort){

        $scope.searchMap.sort=sort;//排序类型
        $scope.searchMap.sortField=sortField;//排序域

        //搜索
        $scope.search();
    }

    //移出对应需要去掉的条件
    $scope.removeItemSearch=function(key){
        if (key=='category' || key=='brand'||key=='price'){
            $scope.searchMap[key]="";
        } else{
            //删除指定的key
            delete $scope.searchMap.spec[key];
        }
    }

    //分类,品牌,规格点击,就将品牌信息存入到集合中
    $scope.addItemSearch=function(key,value){
        if (key=='category'||key=='brand'||key=='price') {
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }

        $scope.search();

    }

    //搜索方法实现
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {

            //绑定到$scope中
            $scope.resultMap=response;

            //调用分页计算
            $scope.pageHandler(response.total,$scope.searchMap.pageNum);
        })
    }

    //分页查询
    $scope.pageSearch=function(pageNum){
        //判断pageNum是否是一个数字
        if (!isNaN(pageNum)){
            $scope.searchMap.pageNum=parseInt(pageNum);
        }
        //入参不是一个数字则让它变成一个数字
        if (isNaN(pageNum)){
            $scope.searchMap.pageNum=1;
        }

        if ($scope.searchMap.pageNum>$scope.page.totalPage){
            $scope.searchMap.pageNum=$scope.page.totalPage;
        }

        //执行搜索
        $scope.search();
    }

    //分页定义
    $scope.page={
        size:10,        //每页显示多少条
        total:0,        //总共有多少条记录
        pageNum:1,      //当前页
        offset:1,       //偏移量
        lpage:1,        //起始页
        rpage:1,        //结束页
        totalPage:1,    //总页数
        pages:[],       //页码
        nextPage:1,     //下一页
        prePage:1,       //上一页
        hasPre:0,       //是否有上页
        hasNext:0       //是否有下页
    }

    //分页计算  当前页,总记录数
    $scope.pageHandler=function (total,pageNum) {
        //给当前页赋值
        $scope.page.pageNum=pageNum;

        var size=$scope.page.size;
        //总页数
        $scope.page.totalPage=parseInt(total%size==0?total/size:(total/size)+1);

        //去出总页数
        var totalPage=$scope.page.totalPage;
        var lpage=$scope.page.lpage;        //开始页
        var rpage=$scope.page.rpage;        //结束页
        var offset=$scope.page.offset;      //偏移量

        //1)pageNum-offset>0
        if ((pageNum-offset)>0){
            lpage=pageNum-offset;
            rpage=pageNum+offset;
        }

        //2)pageNum-offset<=0
        if((pageNum-offset)<=0){
            lpage=1;
            rpage=pageNum+offset+Math.abs(pageNum-offset)+1;
        }

        //3)rpage>totalPage
        if (rpage>totalPage){
            lpage=lpage-(rpage-totalPage);
            rpage=totalPage;
        }

        //4)lpage<=0
        if (lpage<=0){
            lpage=1;
        }

        //页码填充
        $scope.page.pages=[];//清空一次分页页码
        for (var i=lpage;i<=rpage;i++){
            $scope.page.pages.push(i);
        }

        //设置上一页下一页
        if ((pageNum-1)>0){
            $scope.page.prePage=(pageNum-1);  //上一页
            $scope.page.hasPre=1;           //有上一页
        } else{
            $scope.page.hasPre=0;
        }

        if ((pageNum<totalPage)){
            $scope.page.nextPage=(pageNum+1);
            $scope.page.hasNext=1;
        } else{
            $scope.page.hasNext=0;
        }



    }

})