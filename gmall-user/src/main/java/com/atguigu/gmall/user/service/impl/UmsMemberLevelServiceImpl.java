package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.service.UmsMemberLevelService;
import com.atguigu.gmall.bean.UmsMemberLevel;
import com.atguigu.gmall.user.mapper.UmsMemberLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/21 8:38
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {

    @Autowired
    UmsMemberLevelMapper umlm;

    @Override
    public void saveUml(UmsMemberLevel uml) {
        umlm.insertSelective(uml);
    }

    @Override
    public void deleteUml(Integer id) {
        umlm.deleteByPrimaryKey(id);
    }

    @Override
    public void editUml(UmsMemberLevel uml) {
        umlm.updateByPrimaryKeySelective(uml);
    }

    @Override
    public UmsMemberLevel getUmlById(Integer id) {
        UmsMemberLevel uml = umlm.selectByPrimaryKey(id);
        return uml;
    }

    @Override
    public List<UmsMemberLevel> getAllUmls() {
        List<UmsMemberLevel> umlList = umlm.selectAll();
        return umlList;
    }
}
