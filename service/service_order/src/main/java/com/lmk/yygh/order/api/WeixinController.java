package com.lmk.yygh.order.api;

import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.order.service.PaymentService;
import com.lmk.yygh.order.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/9/2 13:08
 */
@Api(tags = "微信接口")
@RequestMapping("api/order/weixin")
@RestController
public class WeixinController {
    @Autowired
    private WeixinService weixinService;
    @Autowired
    private PaymentService paymentService;

    /**
     * 生成微信支付二维码
     * @param orderId
     * @return
     */
    @ApiOperation(value = "生成微信支付二维码")
    @GetMapping("/createNative/{orderId}")
    public Result createNative(@PathVariable long orderId) {
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(@ApiParam(name = "orderId", value = "订单id", required = true)
                                     @PathVariable("orderId") Long orderId) {
        //调用微信接口实现支付状态查询
        Map<String, String> resultMap = weixinService.queryPayStatus(orderId);
        if (resultMap == null) {
            return Result.fail().message("支付出错");
        }
        //支付成功
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            //更新订单状态
            //订单编码
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

}
