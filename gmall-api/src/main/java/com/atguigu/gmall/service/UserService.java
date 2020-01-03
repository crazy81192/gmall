package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMember;

import java.util.List;

/**
 * @author ming
 * @create 2019-11-2019/11/20 15:00
 */
public interface UserService {

    List<UmsMember> getAllUser();

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    UmsMember addOauthUser(UmsMember umsMember);

    UmsMember checkOauthUser(UmsMember umsMember);

}
