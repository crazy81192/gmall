package com.atguigu.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ming
 * @create 2019-12-2019/12/8 20:41
 */
public class TestOauth2 {
    public static String getCode(){
        //1获取授权码
        String s1 = HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=1910766774&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin");
        System.out.println(s1);

        //2返回授权码到回调地址
        String s2 = "http://passport.gmall.com:8085/vlogin?code=c25239407c5ab4ee337f343328a69abc";

        return null;
    }
    public static String getAccess_token(){
        //3换取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";//?client_id=1910766774&client_secret=44843b1bdef5ed42842f111d58f12dd3&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","1910766774");
        paramMap.put("client_secret","44843b1bdef5ed42842f111d58f12dd3");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","c25239407c5ab4ee337f343328a69abc");//授权有效期内可以使用，每生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        Map<String,String> access_map = JSON.parseObject(access_token_json, Map.class);
        System.out.println(access_map.get("access_token"));
        return access_map.get("access_token");
    }
    public static Map<String,String> getUser_info(){
        //4用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=???&uid=?";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);
        System.out.println(user_map.get(1));
        return user_map;
    }


    public static void main(String[] args) {
        getCode();
    }
}
