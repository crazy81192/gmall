//package com.atguigu.gmall.user.impl;
//
//import com.alibaba.dubbo.config.annotation.Service;
//import com.alibaba.fastjson.JSON;
//import com.atguigu.gmall.bean.UmsMember;
//import com.atguigu.gmall.service.UserService;
//import com.atguigu.gmall.user.mapper.UserMapper;
//import com.atguigu.gmall.util.RedisUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import redis.clients.jedis.Jedis;
//
//import java.util.List;
//
///**
// * @author ming
// * @create 2019-11-2019/11/20 15:01
// */
//@Service
//public class UserServiceImpl implements UserService {
//    @Autowired
//    UserMapper userMapper;
//
//    @Autowired
//    RedisUtil redisUtil;
//
//    @Override
//    public List<UmsMember> getAllUser() {
//        List<UmsMember> umsMembers = userMapper.selectAll();
//        return umsMembers;
//    }
//
//    @Override
//    public UmsMember login(UmsMember umsMember) {
//        Jedis jedis = null;
//        try {
//            jedis = redisUtil.getJedis();
//            if(jedis != null){
//                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");
//                if (StringUtils.isNotBlank(umsMemberStr)) {
//                    //密码正确
//                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
//                    return umsMemberFromCache;
//                }
//            }
//            //链接redis失败开启数据库
//            UmsMember umsMemberFromDb = loginFromDb(umsMember);
//            if(umsMemberFromDb != null){
//                jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
//            }
//            return umsMemberFromDb;
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            jedis.close();
//        }
//        return null;
//    }
//
//    @Override
//    public void addUserToken(String token, String memberId) {
//        Jedis jedis = redisUtil.getJedis();
//        jedis.setex("user:" + memberId + ":token",60*60*2,token);
//        jedis.close();
//    }
//
//    private UmsMember loginFromDb(UmsMember umsMember) {
//        List<UmsMember> umsMemberList = userMapper.select(umsMember);
//        if(umsMemberList != null){
//            return umsMemberList.get(0);
//        }
//        return null;
//    }
//
//}
