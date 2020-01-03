package com.atguigu.gmall.controller;

import com.atguigu.gmall.util.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

/**
 * @author ming
 * @create 2019-11-2019/11/29 0:53
 */
@RestController
public class RedissonController {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping("/testRedisson")
    public String testRedisson(){
        Jedis jedis = redisUtil.getJedis();
        RLock lock = redissonClient.getLock("lock");//声明锁
        lock.lock();
        try {
            //..............
        }finally {
            jedis.close();
            lock.unlock();
        }
        System.out.println("8082");
        return "success";
    }
}
