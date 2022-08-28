package com.lmk.yygh.hosp.client;

import com.lmk.yygh.vo.hosp.ScheduleOrderVo;
import com.lmk.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 李明康
 * @create 2022/8/29 1:18
 */
@FeignClient(value = "server-hosp",path = "/api/hosp/hospital/")
@Repository
public interface HospitalFeignClient {
    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId
     * @return
     */
    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

    /**
     * 获取医院签名信息
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);

}
