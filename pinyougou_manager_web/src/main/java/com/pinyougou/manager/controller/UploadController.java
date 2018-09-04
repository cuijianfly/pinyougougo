package com.pinyougou.manager.controller;

import com.pinyougou.entity.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制层
 */
@RestController
public class UploadController {

    //获取配置文件的value值
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        try {
            //获取原文件名
            String originalFilename = file.getOriginalFilename();
            //获取原文件名后缀,不带“.”
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //创建FastDFS客户端
            FastDFSClient dfsClient = new FastDFSClient("classpath:fdfs_client.conf");
            //上传文件到FastDFS
            String fileId = dfsClient.uploadFile(file.getBytes(),extName);
            //返回文件的url
            String url = FILE_SERVER_URL + fileId;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"上传 失败");
        }
    }
}
