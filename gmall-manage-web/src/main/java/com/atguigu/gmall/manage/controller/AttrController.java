package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.service.AttrService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/23 19:25
 */
@RestController
@CrossOrigin
public class AttrController {
    @Reference
    AttrService attrService;

    @GetMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> getAttrInfos(String catalog3Id) {
        List<PmsBaseAttrInfo> attrInfos = attrService.getAttrInfos(catalog3Id);
        return attrInfos;
    }
    @RequestMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attrService.saveAttr(pmsBaseAttrInfo);
        return "success";
    }
    //getAttrValueList?attrId=45
    @PostMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> attrValueList = attrService.getAttrValueList(attrId);
        return attrValueList;
    }

    //baseSaleAttrList
    @PostMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> baseSaleAttrs = attrService.getBaseSaleAttrList();
        return baseSaleAttrs;
    }
}
