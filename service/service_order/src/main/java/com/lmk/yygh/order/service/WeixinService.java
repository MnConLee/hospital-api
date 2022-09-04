package com.lmk.yygh.order.service;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/9/2 13:09
 */
public interface WeixinService {
    /**
     * 生成二维码
     * @param orderId
     * @return
     */
    Map createNative(long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    /**
     * 微信退款调用
     *
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);
}
