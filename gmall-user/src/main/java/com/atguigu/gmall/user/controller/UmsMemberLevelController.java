package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.service.UmsMemberLevelService;
import com.atguigu.gmall.bean.UmsMemberLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/21 8:36
 */
@RestController
public class UmsMemberLevelController {

    @Autowired
    UmsMemberLevelService umls;

    @PostMapping("/uml")
    public void saveUml(UmsMemberLevel uml){
        umls.saveUml(uml);
    }

    @DeleteMapping("/uml/{id}")
    public void deleteUml(@PathVariable("id") Integer id){
        umls.deleteUml(id);
    }

    @PutMapping("/uml")
    public void editUml(UmsMemberLevel uml){
        umls.editUml(uml);
    }

    @GetMapping("/uml/{id}")
    public UmsMemberLevel getUmlById(@PathVariable("id") Integer id){
        UmsMemberLevel uml = umls.getUmlById(id);
        return uml;
    }

    @GetMapping("/umls")
    public List<UmsMemberLevel> getAllUmls(){
        List<UmsMemberLevel> umlsList = umls.getAllUmls();
        return umlsList;
    }
}
