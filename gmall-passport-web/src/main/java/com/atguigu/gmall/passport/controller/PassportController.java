package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.util.HttpclientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ming
 * @create 2019-12-2019/12/6 13:46
 */
@Controller
public class PassportController {
    @Reference
    UserService userService;

    @RequestMapping("/vlogin")
    public String vlogin(String code,HttpServletRequest request){
        //授权码换取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","1910766774");
        paramMap.put("client_secret","44843b1bdef5ed42842f111d58f12dd3");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code",code);//授权有效期内可以使用，每生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期

        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        Map<String,Object> access_map = JSON.parseObject(access_token_json, Map.class);
        //System.out.println(access_map.get("access_token"));
        String uid = (String) access_map.get("uid");
        String access_token = (String)access_map.get("access_token");

        String show_user_url = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
        String user_json = HttpclientUtil.doGet(show_user_url);
        Map<String,Object> user_map = JSON.parseObject(user_json, Map.class);

        //access_token换取用户信息

        //将用户信息保存到数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType(2);
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((String)user_map.get("idstr"));
        umsMember.setCity((String) user_map.get("location"));
        umsMember.setNickname((String) user_map.get("screen_name"));

        Integer g = 0;
        String gender = (String)user_map.get("gender");
        if(gender.equals("m")){
            g = 1;
        }
        umsMember.setGender(g);

        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);// 检查该用户（社交用户）以前是否登陆过系统
        if(umsMemberCheck == null){
            umsMember = userService.addOauthUser(umsMember);
        }else {
            umsMember = umsMemberCheck;
        }
        //生成jwt的token，并且重定向到首页，携带该token
        String token = "";
        String memberId = umsMember.getId();//rpc的主键返回策略失效
        String nickname = umsMember.getNickname();

        Map<String,Object> usrMap = new HashMap<>();
        usrMap.put("memberId",memberId); //是保存数据库后主键返回策略生成的id
        usrMap.put("nickname",nickname);

        String remoteAddr = request.getRemoteAddr();//从request中获取ip
        String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = remoteAddr;
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }
        //按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("2019gmall0105", usrMap, ip);

        //将token存入redis一份
        userService.addUserToken(token,memberId);

        return "redirect:http://search.gmall.com:8083/index?token=" + token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp) {
        //通过jwt校验token真假
        Map<String,String> map = new HashMap<>();

        Map<String, Object> decode = JwtUtil.decode(token, "2019gmall0105", currentIp);
        if(decode != null){
            map.put("status","success");
            map.put("memberId",(String)decode.get("memberId"));
            map.put("nickname",(String)decode.get("nickname"));
        }else {
            map.put("status","fail");
        }

        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";
        //调用用户服务验证用户名和密码
        UmsMember userLogin = userService.login(umsMember);
        if (userLogin != null) {
            //登陆成功
            //用jwt制作token
            String memberId = userLogin.getId();
            String nickname = userLogin.getNickname();
            Map<String,Object> usrMap = new HashMap<>();
            usrMap.put("memberId",memberId);
            usrMap.put("nickname",nickname);

            String remoteAddr = request.getRemoteAddr();//从request中获取ip
            String ip = request.getHeader("x-forwarded-for");//通过nginx转发的客户端ip
            if(StringUtils.isBlank(ip)){
                ip = remoteAddr;
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }
            //按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("2019gmall0105", usrMap, ip);

            //将token存入redis一份
            userService.addUserToken(token,memberId);
        } else {
            //登陆失败
            token = "fail";
        }

        return token;
    }

    @RequestMapping("/index")
    public String index(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "index";
    }
}
