app.service("uploadService",function ($http) {
    //上传图片
    this.upload = function () {
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            url: "../upload.do",
            method: "post",
            data: formData,
            headers: {"content-type": undefined},//转换为multipart-formdata
            transformRequest: angular.identity//序列化formData
      });
    }
});

