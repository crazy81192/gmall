package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsCartItem;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author ming
 * @create 2019-12-2019/12/4 22:56
 */
public interface CartService {
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    void addCart(OmsCartItem omsCartItem);

    List<OmsCartItem> cartList(String userId);

    void checkCart(OmsCartItem omsCartItem);
}
