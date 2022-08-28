package com.lmk.yygh.user.api;



import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.common.utils.AuthContextHolder;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.user.service.UserInfoService;
import com.lmk.yygh.vo.user.LoginVo;
import com.lmk.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 登录
     *
     * @param loginVo
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }

    /**
     * 用户认证接口
     * @param userAuthVo
     * @param request
     * @return
     */
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}
