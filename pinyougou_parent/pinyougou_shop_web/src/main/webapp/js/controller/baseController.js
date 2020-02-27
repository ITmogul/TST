 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}

	//从json列表中，基于对象的属性值判断对象是否存在，如果该对象存在，将对象返回
	//[{"attributeName":"网络","attributeValue":["移动3G"]}]
	$scope.getObjectByValue=function (list,key,name) {
		for(var i=0;i<list.length;i++){
            //判断对象中的属性值是否与传递的name参数是否一致
			if(list[i][key]==name){
				return list[i];
			}
		}
		return null;
    }
	
});	