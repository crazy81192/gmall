package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.manage.util.PmsUploadUtil;
import com.atguigu.gmall.service.SpuService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/24 14:22
 */
@RestController
@CrossOrigin
public class SpuController {
    @Reference
    SpuService spuService;

    //spuList?catalog3Id=61
    @GetMapping("/spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }
    //saveSpuInfo
    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }
    //fileUpload
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file")MultipartFile multipartFile){
        //将图片或者视频上传到分布式的文件存储系统
        //将图片的存储路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }
    //attrInfoList?catalog3Id=61
    //spuSaleAttrList?spuId=24
    //spuImageList?spuId=24

    @GetMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    @GetMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){
        return spuService.spuImageList(spuId);
    }

}
