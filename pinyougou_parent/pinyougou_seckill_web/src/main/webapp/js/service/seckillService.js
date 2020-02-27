//服务层
app.service('seckillService',function($http){
	    	
	//查询秒杀商品列表
	this.findSeckillGoodsList=function(){
		return $http.get('seckill/findSeckillGoodsList.do');
	}

	//查询秒杀商品列表
	this.findOne=function (seckillGoodsId) {
		return $http.get("seckill/findOne.do?seckillGoodsId="+seckillGoodsId);
    }

    //秒杀下单
	this.saveOrder=function (seckillGoodsId) {
        return $http.get("seckill/saveOrder.do?seckillGoodsId="+seckillGoodsId);
    }
});
