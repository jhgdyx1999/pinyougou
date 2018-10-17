package com.pinyougou.manager.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/14,12:33
 */
@SuppressWarnings("Duplicates")
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        String url;
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String serverInnerPath = fastDFSClient.uploadFile(file.getBytes(), extName);
            //获取文件访问url
            url = fileServerUrl+serverInnerPath;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "文件上传失败...");
        }
        return new Result(true, url);
    }
}
