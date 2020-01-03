package com.atguigu.gmall.manage.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuInService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author ming
 * @create 2019-11-2019/11/25 19:10
 */
@Service
public class SkuInServiceImpl implements SkuInService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //插入skuInfo
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        //插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        //插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        //插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImage.setProductImgId(pmsSkuImage.getSpuImgId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        //发出商品的缓存同步消息
        //发出商品的搜索引擎的同步消息
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId){
        //sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        //sku图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {
        System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "发起请求");

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        if(StringUtils.isNotBlank(skuJson)){ //if(skuJson != null && skuJson.equals(""))
            System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "从缓存中获取商品详情");
            pmsSkuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
        }else{
            //如果缓存中没有，查询mysql
            System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "发现缓存中没有，申请缓存的分布式锁");
            //设置分布式锁
            String token = UUID.randomUUID().toString();

            String OK = jedis.set("sku:" + skuId + ":lock",token,"nx","px",10*1000);//拿到锁的线程有10秒的过期时间
            if(StringUtils.isNotBlank(OK) && OK.equals("OK")){
                //设置成功，有权在10秒的过期时间内访问数据库
                System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "成功拿到锁，有权在10秒内访问数据库");

                pmsSkuInfo = getSkuByIdFromDb(skuId);
                //mysql查询结果存入redis
                if(pmsSkuInfo != null){
                    jedis.set("sku:" + skuId + ":info",JSON.toJSONString(pmsSkuInfo));
                }else {
                    //数据库中不存在该sku
                    //为了防止缓存穿透，null值或者控空字符串设置给redis
                    jedis.setex("sku:" + skuId + ":info",60*3,JSON.toJSONString(""));
                }
                //在访问mysql后，将mysql的分布锁释放
                System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "使用完毕，将锁归还");

                String lockToken = jedis.get("sku:" + skuId + ":lock");
                if(StringUtils.isNotBlank(lockToken) && lockToken.equals(token)){
                    //jedis.eval("lua") 可以用lua脚本，在查询到key的同时删除该key，防止高并发下的意外发生
                    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("sku:" + skuId + ":lock"),Collections.singletonList(token));
                    //jedis.del("sku:" + skuId + ":lock");//用token确认删除的是自己的sku的锁
                }
            }else{
                //设置失败，自旋（该线程在睡眠几秒后重新尝试访问）
                System.out.println("ip为：" + ip + "的客户" + Thread.currentThread().getName() + "没有拿到锁，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //自旋 不能使用getSkuById(skuId);会创建孤儿线程，必须加return，还是原来的线程
                return getSkuById(skuId,ip);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {
        boolean b = false;

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        BigDecimal price = skuInfo.getPrice();
        if(price.compareTo(productPrice) == 0){
            b = true;
        }

        return true;
    }
}
