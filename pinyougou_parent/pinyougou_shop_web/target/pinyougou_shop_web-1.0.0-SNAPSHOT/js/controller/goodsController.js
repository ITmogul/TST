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
        })
    });

    //文件上传
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (response) {
			if(response.success){
				//上传成功
				$scope.image_url=response.message;
			}else{
				//上传失败
				alert(response.message);
			}
        })
    }

    
});	
