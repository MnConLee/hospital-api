package com.lmk.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.model.order.PaymentInfo;
import com.lmk.yygh.model.order.RefundInfo;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/9/2 13:20
 */
public interface PaymentService extends IService<PaymentInfo> {

    void savePaymentInfo(OrderInfo order, Integer status);

    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

}
