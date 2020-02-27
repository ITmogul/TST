 //控制层 
app.controller('indexController' ,function($scope,$controller   ,contentService){
	
	$controller('baseController',{$scope:$scope});//继承

	//初始化广告列表
    $scope.contentList=[];

    //基于广告分类id查询广告列表
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
				// [ null,[{},{}].[],]
				$scope.contentList[categoryId]=response;
			}			
		);
	}
	//搜索功能
	$scope.search=function () {
		//跳转搜索页面   页面跳转涉及路由传参，需要在参数传递的问号前加 # 号
		location.href="http://search.pinyougou.com/search.html#?keywords="+$scope.keywords;
    }
	

});	
