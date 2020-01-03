package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author ming
 * @create 2019-11-2019/11/25 11:13
 */
public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        String imagUrl = "http://192.168.1.128";
        //获得配置文件的路径
        String path = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        //获得一个trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);
        try {
            byte[] bytes = multipartFile.getBytes();//获得上传的二进制对象

            //获得文件后缀名
            String originalFilename = multipartFile.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String extName = split[split.length - 1];

            String[] uploadInfos = storageClient.upload_file(bytes, extName, null);
            for (String uploadInfo : uploadInfos) {
                imagUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagUrl;
    }
}
