package com.lmk.yygh.user.controller;



import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.user.service.UserInfoService;
import com.lmk.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/26 11:08
 */

@Api(tags = "前台用户")
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }
}
