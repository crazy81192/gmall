package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMemberLevel;
import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/21 8:37
 */
public interface UmsMemberLevelService {
    void saveUml(UmsMemberLevel uml);

    void deleteUml(Integer id);

    void editUml(UmsMemberLevel uml);

    UmsMemberLevel getUmlById(Integer id);

    List<UmsMemberLevel> getAllUmls();
}
