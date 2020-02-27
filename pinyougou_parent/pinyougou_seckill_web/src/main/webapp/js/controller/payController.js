 //控制层 
app.controller('payController' ,function($scope,$controller  ,$location ,payService){
	
	$controller('baseController',{$scope:$scope});//继承

    //生成二维码
    $scope.createNative=function () {
        payService.createNative().success(function (response) {
            //接收支付订单号、支付金额（分）
            $scope.out_trade_no = response.out_trade_no;
            $scope.total_fee = (response.total_fee/100).toFixed(2);
            //基于qrious生成二维码
            var qr = window.qr = new QRious({
                element: document.getElementById('qrious'),
                size: 300,
                value: response.code_url,
                level:"H"
            })
            //调用查询支付状态
            $scope.queryPayStatus($scope.out_trade_no);
        });

    }

    ////查询支付状态
    $scope.queryPayStatus=function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                //支付成功，跳转成功页面
                location.href="paysuccess.html#?totalFee="+$scope.total_fee;
            }else{

                if(response.message=="timeout"){
                    //如果支付超时，重新生成二维码
                    $scope.createNative();
                }
                //支付失败，跳转失败页面
                location.href="payfail.html";
            }
        })
    }


    //定义接收支付金额的方法
    $scope.getMoney=function () {
       $scope.totalFee =  $location.search()["totalFee"];
    }

});	
