 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//将json格式字符串转为json对象或数组
                $scope.entity.brandIds = JSON.parse(response.brandIds);
                $scope.entity.specIds = JSON.parse(response.specIds);
                $scope.entity.customAttributeItems = JSON.parse(response.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			// {name:a,brandIds:b}
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//初始化品牌列表
    $scope.brandList = {
        data: []
    };

	//查询关联的品牌列表
	$scope.selectBrandOptions=function () {
		brandService.selectBrandOptions().success(function (response) {
            $scope.brandList.data=response;
        })
    }

    //初始化规格列表
    $scope.specList = {
        data: []
    };

    //查询关联的规格列表
    $scope.selectSpecOptions=function () {
        specificationService.selectSpecOptions().success(function (response) {
            $scope.specList.data=response;
        })
    }

    //初始化entity对象
    $scope.entity={customAttributeItems:[]};

    //新增扩展属性
    $scope.addRow=function () {
        $scope.entity.customAttributeItems.push({});
    }

    //删除扩展属性
    $scope.deleRow=function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }


});	
