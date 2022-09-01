package com.lmk.yygh.hospital.receiver;

import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.common.rabbit.service.RabbitService;
import com.lmk.yygh.hospital.service.ScheduleService;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.msm.MsmVo;
import com.lmk.yygh.vo.order.OrderMqVo;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        if(null != orderMqVo.getAvailableNumber()) {
            //下单成功更新预约数
            Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);
        } else {
            //取消预约更新预约数
            Schedule schedule = scheduleService.getScheduleId(orderMqVo.getScheduleId());
            int availableNumber = schedule.getAvailableNumber().intValue() + 1;
            schedule.setAvailableNumber(availableNumber);
            scheduleService.update(schedule);
        }

        //发送短信
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
        //发送短信改
        // MsmVo msmVo = orderMqVo.getMsmVo();
        // String phone = msmVo.getPhone();
        // if(null != msmVo) {
        //     rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, phone);
        // }
    }

}
