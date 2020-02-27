app.service("cartService",function ($http) {
    //查询购物车列表
    this.findCartList=function () {
        return $http.get("cart/findCartList.do");
    }
    //商品数量加减操作
    this.addItemToCartList=function (itemId,num) {
        return $http.get("cart/addItemToCartList.do?itemId="+itemId+"&num="+num);
    }
});