//服务层，用户发送请求，与后端进行请求交互
//参数一：服务名称  参数二：服务要处理的事情
app.service("brandService",function ($http) {
    //查询所有品牌列表
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    }
    //分页查询
    this.findPage = function (pageNumber,pageSize) {
        return  $http.get('../brand/findPage.do?pageNumber='+pageNumber
            +"&pageSize="+pageSize);
    }
    //新增品牌
    this.add=function (entity) {
        return $http.post("../brand/add.do",entity);
    }

    //修改品牌
    this.update=function (entity) {
        return $http.post("../brand/update.do",entity);
    }
    //修改数据回显
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get("../brand/delete.do?ids="+ids);
    }
    //查询模板关联的品牌列表
    this.selectBrandOptions=function () {
        return $http.get("../brand/selectBrandOptions.do");
    }

});
