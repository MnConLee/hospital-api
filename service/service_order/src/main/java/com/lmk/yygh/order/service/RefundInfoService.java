package com.lmk.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.order.PaymentInfo;
import com.lmk.yygh.model.order.RefundInfo;

/**
 * @author 李明康
 * @create 2022/9/4 14:56
 */
public interface RefundInfoService extends IService<RefundInfo>  {
    /**
     * 保存退款记录
     * @param paymentInfo
     * @return
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
