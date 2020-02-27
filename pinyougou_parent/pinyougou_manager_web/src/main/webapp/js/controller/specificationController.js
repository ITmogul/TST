//定义控制器，需要先初始化模块
//参数一：控制器名称  参数二：控制器要处理的事情
//$scope angularjs内置对象 可以理解为全局的作用对象，可以应用在js和html代码 ，相当于js代码和html代码交换的桥梁
//$http内置服务，发起ajax请求
//$controller实现控制器继承的内置对象
app.controller("specificationController",function ($scope,$controller,specificationService) {

    //控制器继承 $controller
    //参数一：继承的父控制器名称 参数二：固定写法：共享$scope
    $controller("baseController",{$scope:$scope});

    //查询所有品牌列表
    $scope.findAll=function () {
        //发起get请求，success是请求成功后回调函数
        specificationService.findAll().success(
            //function中的参数就是返回值
            function (response) {
                $scope.list=response;
            }
        );
    };

    //初始化条件查询对象
    $scope.searchEntity={};

    //条件分页查询
    $scope.search = function(page,rows){
        specificationService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list = response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        );
    }

    //保存
    $scope.save=function () {
        //判断entity对象是否有id，完成新增和修改操作
        var method=null;
        if($scope.entity.specification.id!=null){
            //修改
            method=specificationService.update($scope.entity);
        }else{
            //新增
            method=specificationService.add($scope.entity);
        }

        //提交数据是json对象时，使用post  参数二：要提交的json对象数据
        method.success(function (response) {
            if(response.success){
                //保存成功，重新加载列表数据
                $scope.reloadList();
            }else{
                //保存失败，提示用户
                alert(response.message);
            }
        })
    };
    //基于id查询品牌
    $scope.findOne=function (id) {
        specificationService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }


    //批量删除
    $scope.dele=function () {
        if(confirm("您确定要删除吗？")){
            specificationService.dele($scope.selectIds).success(function (response) {
                if(response.success){
                    //保存成功，重新加载列表数据
                    $scope.reloadList();
                    $scope.selectIds=[];//清空数组
                }else{
                    //保存失败，提示用户
                    alert(response.message);
                }
            })
        }

    }

    //初始化entity对象
    $scope.entity={
        specification:{},
        specificationOptions:[]
    };

    //新增规格选项
    $scope.addRow=function () {
        $scope.entity.specificationOptions.push({});
    }

    //删除规格选项
    $scope.deleRow=function (index) {
        $scope.entity.specificationOptions.splice(index,1);
    }

});