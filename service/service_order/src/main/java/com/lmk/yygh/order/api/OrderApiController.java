package com.lmk.yygh.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.common.utils.AuthContextHolder;
import com.lmk.yygh.enums.OrderStatusEnum;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.order.service.OrderService;
import com.lmk.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李明康
 * @create 2022/8/29 0:13
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    /**
     * 生成挂号订单
     * @param scheduleId
     * @param patientId
     * @return
     */
    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrders(@PathVariable String scheduleId,
                             @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }


    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @ApiOperation(value = "获取订单信息")
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }


    /**
     * 获取订单列表
     * @param page
     * @param limit
     * @param orderQueryVo
     * @param request
     * @return
     */
    @ApiOperation(value = "获取订单列表")
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable long page,
                       @PathVariable long limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request) {

        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 获取订单状态
     * @return
     */
    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    /**
     * 取消预约
     * @param orderId
     * @return
     */
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {
        Boolean isOrder = orderService.cancelOrder(orderId);
        return Result.ok(isOrder);
    }

}
