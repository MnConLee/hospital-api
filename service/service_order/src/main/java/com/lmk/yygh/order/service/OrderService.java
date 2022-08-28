package com.lmk.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.order.OrderInfo;

/**
 * @author 李明康
 * @create 2022/8/29 0:16
 */
public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);
}
