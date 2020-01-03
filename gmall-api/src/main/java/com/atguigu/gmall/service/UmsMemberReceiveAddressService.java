package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/20 22:13
 */

public interface UmsMemberReceiveAddressService {
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Integer id);

    void saveUmra(UmsMemberReceiveAddress umra);

    void deleteUmra(Integer id);

    void updateUmra(UmsMemberReceiveAddress umra);

    List<UmsMemberReceiveAddress> getAllUmras();

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);
}
