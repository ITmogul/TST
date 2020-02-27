 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			//商品保存时，获取富文本编辑器中html内容，赋值给商品介绍属性
            $scope.entity.goodsDesc.introduction=editor.html();
            serviceObject=goodsService.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//保存成功后，清空对象中的值
					$scope.entity={};
					//保存成功后，清空富文本编辑器中的内容
					editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//查询一级分类列表
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List=response;
        })
    }

    // 基于angularjs监控机制实现分类级别联动效果
	//一级分类改变，联动查询二级分类
	//参数一：监控的模型数据  参数二：当监控的数据改变后，要执行的事情
    $scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List=response;
            $scope.itemCat3List=[];
        })
    });

    //二级分类改变，联动查询三级分类
    //参数一：监控的模型数据  参数二：当监控的数据改变后，要执行的事情
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        })
    });

    //三级分类改变，联动查询模板id
    //参数一：监控的模型数据  参数二：当监控的数据改变后，要执行的事情
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId=response.typeId;
        })
    });

    //模板id改变，联动查询模板关联的品牌列表
    //参数一：监控的模型数据  参数二：当监控的数据改变后，要执行的事情
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
        	//console.log(response);
			//解析查询的品牌列表数据
			$scope.brandList = JSON.parse(response.brandIds);
			//解析模板关联的扩展属性  示例：[{"text":"内存大小"},{"text":"颜色"}]
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
        });
        typeTemplateService.selectSpecList(newValue).success(function (response) {
			//接收规格列表
			$scope.specList=response;
        })
    });

    $scope.imageEntity={};

    //文件上传
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (response) {
			if(response.success){
				//上传成功
				$scope.imageEntity.url=response.message;
			}else{
				//上传失败
				alert(response.message);
			}
        })
    }

    //初始entity对象
	$scope.entity={
		goods:{isEnableSpec:'1'},
		goodsDesc:{itemImages:[],specificationItems:[]},
		itemList:[]
	};

	//保存上传的图片对象到图片列表中
	$scope.addImage=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }

    //删除图片
    $scope.deleImage=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    /**
     * 判断规格名称对应的规格对象是否存在与勾选的规格列表中
         如果不存在
         新增规格对象到勾选的规格列表中

         如果存在
         判断是勾选还是取消勾选规格选择数据
         如果勾选
         在已存在的规格对象中规格选项列表中添加勾选的规格选项数据

         取消勾选
         在已存在的规格对象中规格选项列表中移除取消勾选的规格选项数据

         如果规格对象中规格选项列表中的规格选项数据全部移除
         这从勾选的规格列表中，移除该规格对象
     */
    $scope.updateSpecAttribute=function ($event,specName,specOptionName) {
        //判断规格名称对应的规格对象是否存在与勾选的规格列表中  [{"attributeName":"网络","attributeValue":["移动3G"]}]
        var specObj = $scope.getObjectByValue($scope.entity.goodsDesc.specificationItems,"attributeName",specName);

        if(specObj!=null){// 如果存在
            // 判断是勾选还是取消勾选规格选择数据
            if($event.target.checked){
                // 如果勾选
                // 在已存在的规格对象中规格选项列表中添加勾选的规格选项数据
                specObj.attributeValue.push(specOptionName);
            }else{
                // 取消勾选
                // 在已存在的规格对象中规格选项列表中移除取消勾选的规格选项数据
                var index = specObj.attributeValue.indexOf(specOptionName);
                specObj.attributeValue.splice(index,1);
                // 如果规格对象中规格选项列表中的规格选项数据全部移除
                // 这从勾选的规格列表中，移除该规格对象
                if(specObj.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(specObj),1);
                }
            }

        }else{// 如果不存在
            // 新增规格对象到勾选的规格列表中
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specOptionName]});
        }
    }

    //构建sku列表 itemList
    $scope.createItemList=function () {
        //初始化item对象  spec:{"机身内存":"16G","网络":"联通3G"}
        $scope.entity.itemList = [{spec:{},price:0,num : 99999,status:'1',isDefault:'0'}];// 初始
        // 勾选的规格结果集 [{"attributeName":"网络","attributeValue":["移动3G","联通3G"]},{"attributeName":"机身内存","attributeValue":["64G"]}]
        var specList = $scope.entity.goodsDesc.specificationItems;

        if(specList.length==0){
            $scope.entity.itemList =[];
        }

        for(var i=0;i<specList.length;i++){
            $scope.entity.itemList = addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
        }
    }

    addColumn=function (list,specName,specOptions) {
        //定义新的sku列表
        var newList=[];

        //遍历list列表
        for(var i=0;i<list.length;i++){
            //{spec:{},price:0,num : 99999,status:'1',isDefault:'0'}
            var oldItem =  list[i];
            //遍历规格选择数组
            for(var j=0;j<specOptions.length;j++){
                //基于深克隆实现对象创建
                var newItem = JSON.parse(JSON.stringify(oldItem));
                newItem.spec[specName]=specOptions[j];
                newList.push(newItem);
            }
        }

        return newList;
    }

    //商品审核状态数组
    $scope.status = ['未审核','已审核','审核未通过','关闭'];

    //定义商品分类展示的数组
    $scope.itemCatArr=[];

    //优化商品列表分类展示
    $scope.selectAllCategory=function () {
        itemCatService.findAll().success(function (response) {
            for(var i=0;i<response.length;i++){
                $scope.itemCatArr[response[i].id]=response[i].name;
            }

        })
    }

    //上下架状态数组
    $scope.isMarketable = ['下架','上架'];

    //商品上下架
    $scope.updatesIsMarketable=function (isMarketable) {
        goodsService.updatesIsMarketable($scope.selectIds,isMarketable).success(function (response) {
            if(response.success){
                //上下架成功
                $scope.reloadList();
                $scope.selectIds=[];
            }else{
                alert(response.message);
            }
        })
    }


});	
