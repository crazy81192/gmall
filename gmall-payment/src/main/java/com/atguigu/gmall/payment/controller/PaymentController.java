package com.atguigu.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ming
 * @create 2019-12-2019/12/12 10:55
 */
@Controller
public class PaymentController {
    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;

    @Reference
    OrderService orderService;

    @RequestMapping("/alipay/callback/return")
    @LoginRequired(loginSeccess = true)
    public String alipayCallBackReturn(HttpServletRequest request,ModelMap modelMap){
        //回调请求中获取支付宝参数
        String sing = request.getParameter("sing");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();

        //通过支付宝的paramsMap进行签名验证，2.0版本接口将parasMap参数去掉了，导致同步请求没法验签
        if(StringUtils.isNotBlank(sing)){
            //验签成功
            //更新用户的支付状态
            //进行支付更新的幂等性检查
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrdersn(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);//支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);//回调请求字符串
            paymentInfo.setCallbackTime(new Date());

            paymentService.updatePayment(paymentInfo);
        }

        return "finish";
    }

    @RequestMapping("/mx/submit")
    @LoginRequired(loginSeccess = true)
    public String mx(String outTradeNo, BigDecimal totalAmount){
        return null;
    }

    @RequestMapping("/alipay/submit")
    @LoginRequired(loginSeccess = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount){
        //获得支付宝请求的客户端（他并不是一个链接，而是一个封装好的http表单请求）
        String form = null;
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request

        //回调函数
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);
        map.put("subject","尚硅谷作死系列手机");

        String param = JSON.toJSONString(map);
        alipayRequest.setBizContent(param);
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();//调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //生成并保存用户的支付信息
        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrdersn(outTradeNo);
        paymentInfo.setPaymentStatus("订单未付款");
        paymentInfo.setSubject("谷粒商城商品");
        paymentInfo.setTotalAmount(totalAmount);

        paymentService.savePaymentInfo(paymentInfo);

        //向消息中间件发送一个检查支付状态（支付服务消费）的延迟消息队列
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo,5);

        //提交请求到支付宝
        return form;
    }

    @RequestMapping("/index")
    @LoginRequired(loginSeccess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap){
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        modelMap.put("nickname",nickname);
        modelMap.put("memberId",memberId);
        modelMap.put("totalAmount",totalAmount);
        modelMap.put("outTradeNo",outTradeNo);


        return "index";
    }
}
