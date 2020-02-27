package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     * 文件上传
     * //springmvc 实现文件上传，需要配置多媒体解析器  限制文件上传的大小和字符集
     */
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        //基于FastDFS工具类实现图片上传
        try {
            //获取扩展名  1.jpg
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:/config/fdfs_client.conf");
            //示例：group1/M00/00/00/wKgZhV0j-viAY-WdAAA0JGtMezo849.jpg
            String filePath = fastDFSClient.uploadFile(file.getBytes(), extName);

            String fileUrl=FILE_SERVER_URL+filePath;
            return new Result(true,fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"文件上传失败");
        }

    }
}
