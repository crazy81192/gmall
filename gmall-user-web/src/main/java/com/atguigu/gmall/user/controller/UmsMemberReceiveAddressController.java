package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/20 23:53
 */
@RestController
public class UmsMemberReceiveAddressController {

    @Reference
    UmsMemberReceiveAddressService umras;

    @GetMapping("/getReceiveAddressByMemberId/{id}")
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(@PathVariable("id") Integer id){
        List<UmsMemberReceiveAddress> umrasList = umras.getReceiveAddressByMemberId(id);
        return umrasList;
    }

    @PostMapping("/umra")
    public void saveUmra(UmsMemberReceiveAddress umra){
        umras.saveUmra(umra);
    }

    @DeleteMapping("/umra/{id}")
    public void removeUmra(@PathVariable("id") Integer id){
        umras.deleteUmra(id);
    }

    @PutMapping("/umra")
    public void EditUmra(UmsMemberReceiveAddress umra){
        umras.updateUmra(umra);
    }

    @GetMapping("/umras")
    public List<UmsMemberReceiveAddress> getAllUmras(){
        List<UmsMemberReceiveAddress> umrasList = umras.getAllUmras();
        return umrasList;
    }

}
