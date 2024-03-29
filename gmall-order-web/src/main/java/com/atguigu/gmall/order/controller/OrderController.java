package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ming
 * @create 2019-12-2019/12/9 21:45
 */
@Controller
public class OrderController {
    @Reference
    CartService cartService;

    @Reference
    UmsMemberReceiveAddressService addressService;

    @Reference
    OrderService orderService;

    @Reference
    SkuInService skuService;

    @RequestMapping("submitOrder")
    @LoginRequired(loginSeccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, ModelMap modelMap, HttpServletRequest request, String tradeCode) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //检查交易码
        String success = orderService.checkTradeCode(memberId,tradeCode);
        if (success.equals("success")) {
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            //订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            //omsOrder.setFreightAmount(); 运费，支付后，在生成物流信息后生成
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("赶紧发货");

            String outTradeNo = "gmall";
            outTradeNo = outTradeNo + System.currentTimeMillis();//将毫秒时间戳拼接到外部订单
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date());//将时间字符串拼接到外部订单

            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setPayAmount(totalAmount);
            omsOrder.setOrderType(1);

            UmsMemberReceiveAddress umsMemberReceiveAddress = addressService.getReceiveAddressById(memberId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());

            //当前日期加一天，一天后配送
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            Date time = c.getTime();
            omsOrder.setReceiveTime(time);

            omsOrder.setSourceType(0);
            omsOrder.setStatus(2);

            //根据用户id获得要购买的商品列表（购物车）和总价格
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            for (OmsCartItem omsCartItem : omsCartItems) {
                if(omsCartItem.getIsChecked().equals("1")){
                    //获取订单详情列表
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    //检验价格
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if(b == false){
                        ModelAndView modelAndView = new ModelAndView("tradeFail");
                        return modelAndView;
                    }
                    //检验库存，远程调用库存系统
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo);//订单号，用来和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("111111111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");//在仓库中的skuId

                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);

            //将订单和订单详情写入数据库
            //删除购物车的对应商品
            orderService.saveOrder(omsOrder);

            //重定向到支付系统
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.gmall.com:8087/index");
            modelAndView.addObject("outTradeNo",outTradeNo);
            modelAndView.addObject("totalAmount",totalAmount);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("tradeFail");
            return modelAndView;
        }
    }

    //toTrade
    @RequestMapping("toTrade")
    @LoginRequired(loginSeccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //收件人地址列表
        List<UmsMemberReceiveAddress> userAddressList = addressService.getReceiveAddressByMemberId(Integer.parseInt(memberId));

        //将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            //每循环一个购物车对象，就封装一个商品的详情到OmsOrderItem
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());

                omsOrderItems.add(omsOrderItem);
            }
        }
        modelMap.put("omsOrderItems", omsOrderItems);
        modelMap.put("userAddressList", userAddressList);
        modelMap.put("totalAmount", getTotalAmount(omsCartItems));

        //生成交易码，为了在提交订单时做交易码的校验
        String tradeCode = orderService.genTradeCode(memberId);
        modelMap.put("tradeCode", tradeCode);
        return "trade";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                BigDecimal totalPrice = omsCartItem.getTotalPrice();
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }
}
