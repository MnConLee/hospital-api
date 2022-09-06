package com.lmk.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.vo.order.OrderCountQueryVo;
import com.lmk.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/29 0:16
 */
public interface OrderService extends IService<OrderInfo> {
    OrderInfo getOrder(String orderId);

    Long saveOrder(String scheduleId, Long patientId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    Boolean cancelOrder(Long orderId);

    void patientTips();

    /**
     * 预约统计
     * @param orderCountQueryVo
     * @return
     */
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
