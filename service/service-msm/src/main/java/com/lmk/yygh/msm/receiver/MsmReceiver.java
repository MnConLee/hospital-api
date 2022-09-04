package com.lmk.yygh.msm.receiver;

import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.yygh.msm.service.MsmService;
import com.lmk.yygh.vo.msm.MsmVo;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author 李明康
 * @create 2022/8/29 14:42
 */
@Component
public class MsmReceiver {
    @Autowired
    private MsmService msmService;

    /**
     * mq短信发送监听器（发送端有HospitalReceiver(service_hospital)）
     * @param msmVo
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        String phone = msmVo.getPhone();
        //TODO 为了保证测试不出问题，我将电话号码设置为自己的
        Map<String, Object> param = msmVo.getParam();
        String code = (String) param.get("code");
        phone = "18505060741";
        msmService.sendOrderSuccess(phone, code);
    }
}
