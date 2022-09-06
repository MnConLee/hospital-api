package com.lmk.yygh.order.client;

import com.lmk.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/9/6 20:55
 */
//Enablefeign就自动管理了，貌似不用repository
@FeignClient(value = "service-order",path = "/api/order/orderInfo")
@Repository
public interface OrderFeignClient {
    /**
     * 获取订单统计数据
     * @param orderCountQueryVo
     * @return
     */
    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
