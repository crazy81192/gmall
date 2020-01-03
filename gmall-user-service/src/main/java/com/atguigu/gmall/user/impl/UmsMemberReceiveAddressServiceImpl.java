package com.atguigu.gmall.user.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UmsMemberReceiveAddressService;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/20 22:14
 */
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

    @Autowired
    UmsMemberReceiveAddressMapper umram;

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Integer id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",id);
        List<UmsMemberReceiveAddress> umraList = umram.selectByExample(example);
        return umraList;
    }

    @Override
    public void saveUmra(UmsMemberReceiveAddress umra) {
        umram.insertSelective(umra);
    }

    @Override
    public void deleteUmra(Integer id) {
        umram.deleteByPrimaryKey(id);
    }

    @Override
    public void updateUmra(UmsMemberReceiveAddress umra) {
        umram.updateByPrimaryKeySelective(umra);
    }

    @Override
    public List<UmsMemberReceiveAddress> getAllUmras() {
        List<UmsMemberReceiveAddress> umrasList = umram.selectAll();
        return umrasList;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umra = new UmsMemberReceiveAddress();
        umra.setId(receiveAddressId);
        return umram.selectOne(umra);
    }
}
