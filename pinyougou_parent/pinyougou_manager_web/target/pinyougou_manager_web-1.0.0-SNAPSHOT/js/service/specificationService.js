//服务层，用户发送请求，与后端进行请求交互
//参数一：服务名称  参数二：服务要处理的事情
app.service("specificationService",function ($http) {
    //查询所有品牌列表
    this.findAll = function () {
        return $http.get("../specification/findAll.do");
    }
    //分页查询
    this.findPage = function (pageNumber,pageSize) {
        return  $http.get('../specification/findPage.do?pageNumber='+pageNumber
            +"&pageSize="+pageSize);
    }
    //新增
    this.add=function (entity) {
        return $http.post("../specification/add.do",entity);
    }

    //修改
    this.update=function (entity) {
        return $http.post("../specification/update.do",entity);
    }
    //修改数据回显
    this.findOne=function (id) {
        return $http.get("../specification/findOne.do?id="+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get("../specification/delete.do?ids="+ids);
    }

    //条件分页查询
    this.search = function (pageNumber,pageSize,searchEntity) {
        return  $http.post('../specification/search.do?pageNumber='+pageNumber
            +"&pageSize="+pageSize,searchEntity);
    }

    //查询模板管理的规格列表
    this.selectSpecOptions=function () {
        return $http.get("../specification/selectSpecOptions.do");
    }
});
