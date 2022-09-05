package com.lmk.yygh.task.sheduled;

import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 李明康
 * @create 2022/9/5 23:00
 */
@Component
@EnableScheduling
public class ScheduledTask {
    @Autowired
    private RabbitService rabbitService;

    //cron表达式，设置执行间隔

    /**
     * 每天8点执行方法，就医题型
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");

    }
}
