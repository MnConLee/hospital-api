package com.lmk.yygh.order.api;

import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.order.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
