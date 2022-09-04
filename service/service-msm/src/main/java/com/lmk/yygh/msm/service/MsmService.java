package com.lmk.yygh.msm.service;

import com.lmk.yygh.vo.msm.MsmVo;

/**
 * @author 李明康
 * @create 2022/8/26 18:42
 */
public interface MsmService {
    boolean send(String phone, String code);

    /**
     * mq使用发送短信
     */
    // boolean send(MsmVo msmVo);

    /**
     * mq异步发送短信改
     * @param phone
     * @return
     */
    boolean sendOrderSuccess(String phone, String code);
}
