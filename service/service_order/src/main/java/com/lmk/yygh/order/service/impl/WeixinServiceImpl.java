package com.lmk.yygh.order.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.lmk.yygh.enums.PaymentStatusEnum;
import com.lmk.yygh.enums.PaymentTypeEnum;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.order.service.OrderService;
import com.lmk.yygh.order.service.PaymentService;
import com.lmk.yygh.order.service.WeixinService;
import com.lmk.yygh.order.util.ConstantPropertiesUtils;
import com.lmk.yygh.order.util.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 李明康
 * @create 2022/9/2 13:09
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成微信支付二维码
     * @param orderId
     * @return
     */
    @Override
    public Map createNative(long orderId) {
        try {
            //从redis获取数据
            Map payMap = (Map) redisTemplate.opsForValue().get(orderId + "");
            if (payMap != null) {
                return payMap;
            }
            //获取订单信息
            OrderInfo order = orderService.getById(orderId);
            //向支付记录添加信息
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
            //设置参数，调用微信接口
            //把参数转换xml格式，使用商户key进行加密
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = order.getReserveDate() + "就诊" + order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //测试我使用0.01元,该支付接口不是我自己的
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");
            paramMap.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            //支付方式（扫码）
            paramMap.put("trade_type", "NATIVE");
            //调用微信生成二维码接口，httpclient调用
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置map参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //返回相关数据
            String xml = client.getContent();
            //转换成map格式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("resultMap:" + resultMap);
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            //二维码地址
            map.put("codeUrl", resultMap.get("code_url"));
            if (resultMap.get("result_code") != null) {
                redisTemplate.opsForValue().set(orderId+"", map, 120, TimeUnit.MINUTES);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            //根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            //封装提交参数
            HashMap paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //设置请求内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //得到微信接口返回数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //把接口数据返回
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }
}
