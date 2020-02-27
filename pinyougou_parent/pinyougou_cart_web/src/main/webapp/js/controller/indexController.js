 //控制层 
app.controller('indexController' ,function($scope,$controller   ,loginService){
	
	$controller('baseController',{$scope:$scope});//继承

    //获取用户名
    $scope.getLoginName=function () {
        loginService.getLoginName().success(function (response) {
            $scope.loginName=response.loginName;
        })
    }
	

});	
