 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承

    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //商品数量和金额统计
            sum();
        })
    }
    //商品数量加减操作
    $scope.addItemToCartList=function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
            if(response.success){
                //查询购物车列表
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    }
    
    //统计商品总数量和总金额
    sum=function () {
        $scope.totalNum=0;
        $scope.totalMoney=0.00;
        for(var i=0;i< $scope.cartList.length;i++){
            //获取购物车对象
            var cart =  $scope.cartList[i];
            //获取商品列表
           var orderItemList = cart.orderItemList;
           for(var j=0;j<orderItemList.length;j++){
               $scope.totalNum+=orderItemList[j].num;
               $scope.totalMoney+=orderItemList[j].totalFee;
           }
        }

    }
	

});	
