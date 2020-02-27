//定义基础controller
app.controller("baseController",function ($scope) {

    //定义分页对象，分页配置
    $scope.paginationConf = {
        currentPage:1,  				//当前页
        totalItems:10,					//总记录数
        itemsPerPage:10,				//每页记录数
        perPageOptions:[10,20,30,40,50], //分页选项，下拉选择一页多少条记录
        onChange:function(){			//页面变更后触发的方法
            $scope.reloadList();		//启动就会调用分页组件
        }
    };

    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }



    //更新复选框勾选状态，往品牌id数组中添加值
    //选中的id数组
    $scope.selectIds=[];
    $scope.updateSelection=function ($event,id) {

        //判断复选框选中状态 $event.target 事件源对象此时就是复选框对象
        if($event.target.checked){
            //勾选
            $scope.selectIds.push(id);
        }else{
            //取消勾选
            //获取元素索引
            var index = $scope.selectIds.indexOf(id);
            //参数一：移除位置的元素的索引值  参数二：从该位置移除几个元素
            $scope.selectIds.splice(index,1);
        }
    }

    //解析json格式字符串，获取字符串中的对象，基于对象属性名获取属性值，然后将属性值以逗号格式拼接
    //例如：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]  [{"id":27,"name":"网络"},{"id":32,"name":"机身内存"}]
    $scope.praseJsonString=function (jsonString,key) {
        //解析json格式字符串
        var jsonArr= JSON.parse(jsonString);
        var value="";
        for(var i=0;i<jsonArr.length;i++){
            //获取字符串中的对象,基于对象属性名获取属性值
            //js基于对象的属性名获取属性值有两种方式
            //方式一：在对象的属性名是确定值， 属性值=对象.属性名
            //方式二：在对象的属性名是不确定值是变量或者是确定值， 属性值=对象[属性名]
            if(i==0){
                value+=jsonArr[i][key];
            }else{
                value+=","+jsonArr[i][key];
            }
        }
        return value;

    }





});