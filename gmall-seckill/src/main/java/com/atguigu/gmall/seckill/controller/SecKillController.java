package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @author ming
 * @create 2019-12-2019/12/15 18:42
 */
@Controller
public class SecKillController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 先到先得式秒杀
     * @return
     */
    @RequestMapping("/secKill")
    @ResponseBody
    public String secKill(){
        Jedis jedis = redisUtil.getJedis();
        Integer stock = Integer.parseInt(jedis.get("106"));

        RSemaphore semaphore = redissonClient.getSemaphore("106");
        boolean b = semaphore.tryAcquire();

        if (b) {
            System.out.println("当前库存剩余数量:" + stock);
            //用消息队列发出订单消息
        } else {
            System.out.println("用户抢购失败");
        }
        jedis.close();
        return "" + stock;
    }

    /**
     * 随机拼运气秒杀
     * @return
     */
    @RequestMapping("/kill")
    @ResponseBody
    public String kill() {
        Jedis jedis = redisUtil.getJedis();
        //开启商品的监控
        jedis.watch("106");
        Integer stock = Integer.parseInt(jedis.get("106"));
        if (stock > 0) {
            Transaction multi = jedis.multi();
            multi.incrBy("106", -1);
            List<Object> exec = multi.exec();
            if (exec != null && exec.size() > 0) {
                System.out.println("当前库存剩余数量:" + stock);
                //用消息队列发出订单消息
            } else {
                System.out.println("用户抢购失败");
            }
        }
        jedis.close();

        return "" + stock;
    }
}
