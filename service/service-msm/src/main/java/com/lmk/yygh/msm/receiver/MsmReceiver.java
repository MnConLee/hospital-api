package com.lmk.yygh.msm.receiver;

import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.yygh.msm.service.MsmService;
import com.lmk.yygh.vo.msm.MsmVo;
import org.aspectj.bridge.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;

/**
 * @author 李明康
 * @create 2022/8/29 14:42
 */
@Component
public class MsmReceiver {
    @Autowired
    private MsmService msmService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        msmService.send(msmVo);
    }
}
