/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("goodsController", function ($scope, $location, $http, $controller, goodsService, uploadService, itemCatService, typeTemplateService) {

    //继承父控制器
    $controller("baseController", {$scope: $scope});

    //定义一个数组用于存储所有上传的文件   之前 $scope.imageslist=[];

    $scope.entity = {goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []}};

    //定义一个状态参数
    $scope.status = ['未审核', '审核通过', '审核未通过', '关闭']

    $scope.itemCatList = {};

    //查询所有分类信息
    $scope.getItemCatList = function () {
        itemCatService.findAllList().success(function (response) {
            //循环机和,组装key:value的json数据
            for (var i = 0; i < response.length; i++) {
                var key = response[i].id;
                var value = response[i].name;

                $scope.itemCatList[key] = value;
            }
        })
    }

    //创建SKU的方法
    $scope.createItems = function () {
        //构建一个没有任何规格的商品
        var item = {"price": 1, "num": 1, "status": "1", "isDefault": "0", spec: {}};

        //没有启用规格时.组合的商品集合数据
        $scope.entity.items = [item];

        //选择的规格信息
        var speclist = $scope.entity.goodsDesc.specificationItems;

        //实现组合
        for (var i = 0; i < speclist.length; i++) {
            //每次循环不同的规格,并将返回的数据重新赋值$scope.entity.item
            $scope.entity.items = addCloum(speclist[i].attributeName, speclist[i].attributeValue, $scope.entity.items);
        }

    }

    //商品重组
    addCloum = function (attributeName, attributeValue, items) {
        var itemlist = [];
        //拿上一次重组的商品和指定的规格选项每个进行重组
        for (var q = 0; q < items.length; q++) {
            //选中几个规格,就组合几个商品
            for (var i = 0; i < attributeValue.length; i++) {
                //深克隆
                var newItem = angular.copy(items[q]);
                //构建新商品
                newItem.spec[attributeName] = attributeValue[i];

                //将第2次组合的商品加入集合
                itemlist.push(newItem);
            }
        }
        return itemlist;
    }


    //创建SKU的方法
    $scope.createItems1 = function () {
        //构建一个没有任何规格的商品
        var item = {"price": 1, "num": 1, "status": "1", "isDefault": "0", spec: {}};

        //选择的规格信息
        var speclist = $scope.entity.goodsDesc.specificationItems;

        //第一次重组,长度>=1表名至少选中一个规格
        $scope.items1 = [];
        if (speclist.length >= 1) {
            //选中几个规格,就组合几个商品
            for (var i = 0; i < speclist[0].attributeValue.length; i++) {
                //深克隆
                var newItem = angular.copy(item);
                //构建新商品
                newItem.spec[speclist[0].attributeName] = speclist[0].attributeValue[i];

                //将第一次组合的商品加入集合
                $scope.items1.push(newItem);
            }
        }

        //第二次重组,长度>=1表名至少选中一个规格
        $scope.items2 = [];
        if (speclist.length >= 2) {

            for (var q = 0; q < $scope.items1.length; q++) {
                //选中几个规格,就组合几个商品
                for (var i = 0; i < speclist[1].attributeValue.length; i++) {
                    //深克隆
                    var newItem = angular.copy($scope.items1[q]);
                    //构建新商品
                    newItem.spec[speclist[1].attributeName] = speclist[1].attributeValue[i];

                    //将第一次组合的商品加入集合
                    $scope.items2.push(newItem);
                }
            }

        }

        //第三次重组,长度>=1表名至少选中一个规格
        $scope.items3 = [];
        if (speclist.length >= 3) {
            for (var q = 0; q < $scope.items2.length; q++) {
                //选中几个规格,就组合几个商品
                for (var i = 0; i < speclist[2].attributeValue.length; i++) {
                    //深克隆
                    var newItem = angular.copy($scope.items2[q]);
                    //构建新商品
                    newItem.spec[speclist[2].attributeName] = speclist[2].attributeValue[i];

                    //将第一次组合的商品加入集合
                    $scope.items3.push(newItem);
                }
            }
        }


    }


    //定义一个方法,用于规格选项的店家时间,来记录当前选中的规格数据
    $scope.updateSpecAttribute = function ($event, attributeName, attributeValue) {
        //从集合中查询当前规格是否存在,如果存在,则将该对象直接返回并接收
        var result = $scope.selectObjectByKey($scope.entity.goodsDesc.specificationItems, attributeName);
        //如果result不为空,则往对象的attribute中追加规格选项
        if (result != null) {

            if ($event.target.checked) {
                //集合存在就追加选项
                result.attributeValue.push(attributeValue);
            } else {
                //如果取消复选框,从规格选项中移出
                var valueIndex = result.attributeValue.indexOf(attributeValue);

                //移除
                result.attributeValue.splice(valueIndex, 1);
                //如果该规格没有规格选项了,则将该规格移出
                if (result.attributeValue.length <= 0) {
                    //获取当前result对象在集合$scope.entity.goodsDesc.specificationItems中的下标
                    var specIndex = $scope.entity.goodsDesc.specificationItems.indexOf(result);
                    //移除规格
                    $scope.entity.goodsDesc.specificationItems.splice(specIndex, 1);
                }

            }
        } else {
            //如果result为空,则新建一个数据,加入到$scope.entity.goodsDesc.specificationItems集合中
            var specOptionInfo = {"attributeName": attributeName, "attributeValue": [attributeValue]};

            $scope.entity.goodsDesc.specificationItems.push(specOptionInfo);
        }


    }

    //从集合中找到key对应的数据
    $scope.selectObjectByKey = function (list, attributeName) {
        for (var i = 0; i < list.length; i++) {
            if (list[i]['attributeName'] == attributeName) {
                return list[i];
            }
        }

    }

    //查询一级分类
    $scope.findItemCat1List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat1List = response;
        })

        //清空2,3级分类
        $scope.itemCat2List = null;
        $scope.itemCat3List = null;
        //清空entity.typeTemplateId
        $scope.entity.typeTemplateId = null;

        //清空品牌
        $scope.brandList = null;
        //清空扩展属性
        $scope.entity.goodsDesc.customAttributes = null;

    }


    //查询二级分类
    $scope.findItemCat2List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat2List = response;
        })
        //清空3级分类
        $scope.itemCat3List = null;
        //清空entity.typeTemplateId
        $scope.entity.typeTemplateId = null;
        //清空品牌
        $scope.brandList = null;
        //清空扩展属性
        $scope.entity.goodsDesc.customAttributes = null;
    }

    //查询三级分类
    $scope.findItemCat3List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat3List = response;
        })
        //清空entity.typeTemplateId
        $scope.entity.typeTemplateId = null;
        //清空品牌
        $scope.brandList = null;
        //清空扩展属性
        $scope.entity.goodsDesc.customAttributes = null;
    }



    //查询模板ID
    $scope.getTypeTemplate = function () {

        if (!isNaN($scope.entity.category3Id)) {
            itemCatService.findOne($scope.entity.category3Id).success(function (response) {
                $scope.entity.typeTemplateId = response.typeId;
            })
        }
    }


    //变量监控
    $scope.$watch('entity.typeTemplateId', function (newValue, oldValue) {

        if (!isNaN(newValue)) {
            //查询模板信息
            typeTemplateService.findOne(newValue).success(function (response) {
                //定义一个变量存储品牌信息  字符类型JSON格式
                $scope.brandList = angular.fromJson(response.brandIds);

                //扩展属性
                $scope.entity.goodsDesc.customAttributes = JSON.parse(response.customAttributeItems)

                //调用查询规格选项数据
                typeTemplateService.findOptions($scope.entity.typeTemplateId).success(function (response) {
                    $scope.specList = response;
                })


            })
        }
    })


    //移除一张图片
    $scope.remove_image_entity = function (index) {
        //$scope.imageslist.splice(index,1);
        $scope.entity.goodsDesc.itemImages.splice(index, 1);

    }


    //往集合中添加一张图片
    $scope.add_image_entity = function () {
        // $scope.imageslist.push($scope.image_entity);
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //文件上传
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                //获取文件上传后的回显url
                $scope.image_entity.url = response.message;
            }
        });
    }


    //获取所有的Goods信息
    $scope.getPage = function (page, size) {
        //发送请求获取数据
        goodsService.findAll(page, size, $scope.searchEntity).success(function (response) {
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems = response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function () {
        //文本编辑器对象.html()表示获取文本编辑器内容
        $scope.entity.goodsDesc.introduction = editor.html();
        var result = null;
        if ($scope.entity.id != null) {
            //执行修改数据
            result = goodsService.update($scope.entity);
        } else {
            //增加操作
            result = goodsService.add($scope.entity);
        }
        //判断操作流程
        result.success(function (response) {
            //判断执行状态
            if (response.success) {
                //重新加载新的数据
                // $scope.reloadList();
                // alert("恭喜你添加成功")
                //$scope.entity = {};
                //文本编辑器赋值为空
                //editor.html("");
                location.href='/admin/goods.html';
            } else {
                //打印错误消息
                alert(response.message);
            }
        });
    }

    //根据ID查询信息
    $scope.getById = function () {
        //从地址栏获取对应参数
        var id = $location.search()['id'];

        if (id != null) {
            goodsService.findOne(id).success(function (response) {
                //将后台的数据绑定到前台
                $scope.entity = response;

                //记录模板ID
                var typeId=response.typeTemplateId;

                //查询三个级别的分类
                $scope.findItemCat1List(0);
                $scope.findItemCat2List($scope.entity.category1Id);
                $scope.findItemCat3List($scope.entity.category2Id);

                //重置改变模板ID
                $scope.entity.typeTemplateId=typeId;

                $scope.entity=response;

                //循环迭代entity的items并将每个item的spec转成JSON
                for (var i=0;i<$scope.entity.items.length;i++){
                    $scope.entity.items[i].spec=angular.fromJson($scope.entity.items[i].spec);
                }

                //商品介绍给文本编辑器
                editor.html(response.goodsDesc.introduction);

                //图片转JSON
                $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);

                //自定义属性
                $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);

                //规格
                $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);

            });


        }
    }

    //批量删除
    $scope.delete = function () {
        goodsService.delete($scope.selectids).success(function (response) {
            //判断删除状态
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }

    //判断规格是否选中
    $scope.updateAttribteStatus=function (attributeName,attributeValue) {
        //判断对应规格是否选中
        var result = $scope.selectObjectByKey($scope.entity.goodsDesc.specificationItems,attributeName);

        //判断规格选项是否存在
        var index = result.attributeValue.indexOf(attributeValue);

        if (index>=0){
            //有数据
            return true;
        }

        return  false;
    }

});
