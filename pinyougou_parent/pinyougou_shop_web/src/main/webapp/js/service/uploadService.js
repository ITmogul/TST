//服务层
app.service('uploadService',function($http){

    /**
	 * 	<form enctype="multipart/form-data" method="post">
			 <input type="file">
		</form>

     */
	    	
	//文件上传
	this.uploadFile=function(){
		//结合angularjs+html5（FormData）实现文件上传
		var formData = new FormData();
		//将页面选择的图片内容，绑定到表单数据对象
		//参数一：与后台java代码接收页面文件对象名称保存一致
		//要提交的文件对象  file.files[0] file与页面中 <input type="file" id="file" />的id一致
        formData.append("file",file.files[0]);

		return $http({
			url:"../upload/uploadFile.do",
			method:"post",
			data:formData,
            headers : {'Content-Type' : undefined}, //上传文件必须是这个类型，相当于设置enctype="multipart/form-data"
            transformRequest : angular.identity  //对整个表单进行二进制序列化
		});
	}
});
