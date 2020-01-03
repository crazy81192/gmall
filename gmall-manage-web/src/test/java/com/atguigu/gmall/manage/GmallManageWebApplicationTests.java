package com.atguigu.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    @Test
    public void contextLoads() throws IOException, MyException {
        //获得配置文件的路径
        String path = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
        ClientGlobal.init(path);

        TrackerClient trackerClient = new TrackerClient();
        //获得一个trackerServer的实例
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);
        String[] uploadInfos = storageClient.upload_file("C:/Users/10207/Pictures/Saved Pictures/4.jpg", "jpg", null);
        String url = "http://192.168.1.128";
        for (String uploadInfo : uploadInfos) {
            url += "/" + uploadInfo;
            System.out.println(url);
        }
    }
    @Test
    public void testFdfs(){
        String imageName = "adf.asdf.qewr.adf.jpg";
        String[] split = imageName.split("\\.");
        String extName = split[split.length - 1];
        System.out.println(extName);
    }

}
