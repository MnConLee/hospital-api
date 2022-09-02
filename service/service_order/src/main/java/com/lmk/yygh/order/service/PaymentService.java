package com.lmk.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.model.order.PaymentInfo;

/**
 * @author 李明康
 * @create 2022/9/2 13:20
 */
public interface PaymentService extends IService<PaymentInfo> {

    void savePaymentInfo(OrderInfo order, Integer status);
}
