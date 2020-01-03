package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttr;

import java.util.List;
import java.util.Set;

/**
 * @author ming
 * @create 2019-11-2019/11/23 21:07
 */
public interface AttrService {
    List<PmsBaseAttrInfo> getAttrInfos(String catalog3Id);

    void saveAttr(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> getBaseSaleAttrList();

    List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueSet);
}
