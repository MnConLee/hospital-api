package com.lmk.yygh.hospital.receiver;

import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.common.rabbit.service.RabbitService;
import com.lmk.yygh.hospital.service.ScheduleService;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.msm.MsmVo;
import com.lmk.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * @author 李明康
 * @create 2022/8/29 16:11
 */
@Component
public class HospitalReceiver {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key= {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException{
        Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
        schedule.setReservedNumber(orderMqVo.getReservedNumber());
        schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
        scheduleService.update(schedule);
        //发送消息
        MsmVo msmVo = orderMqVo.getMsmVo();
        if (null != msmVo) {
            rabbitService.sendMessage(MqConst.QUEUE_MSM_ITEM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }

    }
}
