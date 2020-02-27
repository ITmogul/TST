 //控制层 
app.controller('seckillController' ,function($scope,$controller  ,$location,$interval ,seckillService){
	
	$controller('baseController',{$scope:$scope});//继承

    //查询秒杀商品列表
    $scope.findSeckillGoodsList=function () {
        seckillService.findSeckillGoodsList().success(function (response) {
           $scope.seckillGoodsList=response;
        })
    }

    //查询商品详情
    $scope.findOne=function () {
        //接收秒杀商品列表页面传递过来的商品id
        $scope.seckillGoodsId = $location.search()["seckillGoodsId"];
        seckillService.findOne( $scope.seckillGoodsId).success(function (response) {
            $scope.seckillGoods= response;

            //距离结束时间，倒计时统计效果
            //计算当前时间距离结束时间的多少秒
           var now =  new Date().getTime();//当前时间毫秒值
           var endTime =  new Date($scope.seckillGoods.endTime).getTime();
            $scope.secondes =  Math.floor((endTime-now)/1000);

           //单位换算
            /*var day =Math.floor(seconds/60/60/24); //1.25
            var hours = Math.floor((seconds - day*24*60*60)/60/60); //5.6
            var minutes = Math.floor(seconds - day*24*60*60-hours*60*60)/60; //6.8
            var second=seconds - day*24*60*60-hours*60*60 -minutes*60;*/

            var time =$interval(function () {
                if($scope.secondes>0){
                    //时间递减
                    $scope.secondes--;
                    //时间格式化
                    $scope.timeString=$scope.convertTimeString($scope.secondes);
                }else{
                    //结束时间递减
                    $interval.cancel(time);
                }
            },1000);

        })

    }

    $scope.convertTimeString=function (allseconds) {
        //计算天数
        var days = Math.floor(allseconds/(60*60*24));

        //小时
        var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

        //分钟
        var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

        //秒
        var seconds = allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60);

        //拼接时间
        var timString="";
        if(days>0){
            timString=days+"天:";
        }

        if(hours<10){
            hours="0"+hours;
        }
        if(minutes<10){
            minutes="0"+minutes;
        }
        if(seconds<10){
            seconds="0"+seconds;
        }
        return timString+=hours+":"+minutes+":"+seconds;
    }

    /*$scope.count=10;
    //参数一：定时任务执行的操作  参数二：执行的间隔时间（单位毫秒） 参数三：执行次数
    $interval(function () {
        $scope.count--;
    }, 1000,10);*/
    //秒杀下单
    $scope.saveOrder=function () {
        seckillService.saveOrder($scope.seckillGoodsId).success(function (response) {
            if(response.success){
                //秒杀下单成功，跳转支付页面
                location.href="pay.html";
            }else{
                alert(response.message);
            }
        })
    }

});
